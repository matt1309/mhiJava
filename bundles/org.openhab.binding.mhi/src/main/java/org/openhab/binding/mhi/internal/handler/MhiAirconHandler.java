/*
 * Copyright (c) 2010-2024 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.mhi.internal.handler;

import static org.openhab.binding.mhi.internal.MhiBindingConstants.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.measure.quantity.Temperature;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mhi.internal.MhiAirconConfiguration;
import org.openhab.binding.mhi.internal.MhiBindingConstants;
import org.openhab.binding.mhi.internal.protocol.AirconState;
import org.openhab.binding.mhi.internal.protocol.MhiConnection;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.library.types.QuantityType;
import org.openhab.core.library.types.StringType;
import org.openhab.core.library.unit.SIUnits;
import org.openhab.core.library.unit.Units;
import org.openhab.core.thing.ChannelUID;
import org.openhab.core.thing.Thing;
import org.openhab.core.thing.ThingStatus;
import org.openhab.core.thing.ThingStatusDetail;
import org.openhab.core.thing.binding.BaseThingHandler;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.openhab.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MhiAirconHandler} is responsible for handling commands, which are
 * sent to one of the channels, and for polling the state of a single MHI aircon
 * unit.
 *
 * It preserves the "spam mode" behaviour of the original bridge: when spam mode
 * is disabled, rapid consecutive commands (e.g. dragging a setpoint slider from
 * 18 to 20) are coalesced so that only a single request carrying the final state
 * is sent to the unit, once the configured debounce interval has elapsed.
 *
 * @author matt1309 - Initial contribution
 */
@NonNullByDefault
public class MhiAirconHandler extends BaseThingHandler {

    // OperationMode integer <-> label mapping (see RacParser round-trip tests):
    // 0=dry, 1=cool, 2=fan, 3=heat, 4=auto
    private static final Map<String, Integer> MODE_TO_INT = Map.of("dry", 0, "cool", 1, "fan", 2, "heat", 3, "auto", 4);
    private static final Map<Integer, String> INT_TO_MODE = Map.of(0, "dry", 1, "cool", 2, "fan", 3, "heat", 4, "auto");

    private final Logger logger = LoggerFactory.getLogger(MhiAirconHandler.class);

    private final AirconState state = new AirconState();
    private MhiAirconConfiguration config = new MhiAirconConfiguration();
    private @Nullable MhiConnection connection;

    private @Nullable ScheduledFuture<?> pollJob;
    private @Nullable ScheduledFuture<?> pendingSend;

    public MhiAirconHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        config = getConfigAs(MhiAirconConfiguration.class);

        if (config.hostname.isBlank() || config.deviceId.isBlank()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "hostname and deviceId are required");
            return;
        }

        state.sethostname(config.hostname);
        state.setport(String.valueOf(config.port));
        state.setDeviceID(config.deviceId);
        state.setOperatorID(config.operatorId);
        state.setAirConID(config.deviceId);
        connection = new MhiConnection(state, 5000);

        updateStatus(ThingStatus.UNKNOWN);

        scheduler.execute(this::poll);
        int interval = Math.max(1, config.refreshInterval);
        pollJob = scheduler.scheduleWithFixedDelay(this::poll, interval, interval, TimeUnit.SECONDS);
    }

    @Override
    public void dispose() {
        ScheduledFuture<?> localPoll = pollJob;
        if (localPoll != null) {
            localPoll.cancel(true);
            pollJob = null;
        }
        ScheduledFuture<?> localSend = pendingSend;
        if (localSend != null) {
            localSend.cancel(false);
            pendingSend = null;
        }
    }

    private void poll() {
        MhiConnection localConnection = connection;
        if (localConnection == null) {
            return;
        }
        boolean ok = localConnection.getAirconStats();
        if (ok) {
            updateStatus(ThingStatus.ONLINE);
            updateProperties();
            updateAllChannels();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Unable to reach aircon unit");
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            updateChannel(channelUID.getId());
            return;
        }

        boolean changed = applyCommand(channelUID.getId(), command);
        if (changed) {
            scheduleSend();
        }
    }

    /**
     * Applies a command to the local {@link AirconState}. Returns {@code true}
     * when the state was actually modified (and therefore needs sending).
     */
    private boolean applyCommand(String channelId, Command command) {
        switch (channelId) {
            case MhiBindingConstants.CHANNEL_POWER:
                if (command instanceof OnOffType onOff) {
                    return state.setOperation(onOff == OnOffType.ON);
                }
                break;
            case MhiBindingConstants.CHANNEL_MODE:
                Integer mode = MODE_TO_INT.get(command.toString().toLowerCase());
                if (mode != null) {
                    return state.setOperationMode(mode);
                }
                break;
            case MhiBindingConstants.CHANNEL_FAN_SPEED:
                Integer flow = parseFanSpeed(command.toString());
                if (flow != null) {
                    return state.setAirFlow(flow);
                }
                break;
            case MhiBindingConstants.CHANNEL_VANE_UD:
                Integer ud = parseInt(command.toString());
                if (ud != null) {
                    return state.setWindDirectionUD(ud);
                }
                break;
            case MhiBindingConstants.CHANNEL_VANE_LR:
                Integer lr = parseInt(command.toString());
                if (lr != null) {
                    return state.setWindDirectionLR(lr);
                }
                break;
            case MhiBindingConstants.CHANNEL_TARGET_TEMPERATURE:
                Float temp = parseTemperature(command);
                if (temp != null) {
                    return state.setPresetTemp(temp);
                }
                break;
            case MhiBindingConstants.CHANNEL_ENTRUST:
                if (command instanceof OnOffType onOff) {
                    return state.setEntrust(onOff == OnOffType.ON);
                }
                break;
            case MhiBindingConstants.CHANNEL_VACANT:
                if (command instanceof OnOffType onOff) {
                    return state.setVacant(onOff == OnOffType.ON);
                }
                break;
            case MhiBindingConstants.CHANNEL_COOL_HOT_JUDGE:
                if (command instanceof OnOffType onOff) {
                    return state.setCoolHotJudge(onOff == OnOffType.ON);
                }
                break;
            case MhiBindingConstants.CHANNEL_SELF_CLEAN_OPERATION:
                if (command instanceof OnOffType onOff) {
                    return state.setSelfCleanOperation(onOff == OnOffType.ON);
                }
                break;
            case MhiBindingConstants.CHANNEL_SELF_CLEAN_RESET:
                if (command instanceof OnOffType onOff) {
                    return state.setSelfCleanReset(onOff == OnOffType.ON);
                }
                break;
            default:
                logger.debug("Channel '{}' is read-only or unknown, ignoring command", channelId);
        }
        return false;
    }

    /**
     * Schedules the state to be pushed to the unit. When spam mode is enabled the
     * request is sent immediately; otherwise any previously scheduled send is
     * cancelled and a new one is scheduled after the debounce interval so that
     * only the final state is transmitted.
     */
    private synchronized void scheduleSend() {
        ScheduledFuture<?> localSend = pendingSend;
        if (localSend != null) {
            localSend.cancel(false);
        }
        long delay = config.spamMode ? 0 : Math.max(0, config.spamModeInterval);
        pendingSend = scheduler.schedule(this::send, delay, TimeUnit.MILLISECONDS);
    }

    private void send() {
        MhiConnection localConnection = connection;
        if (localConnection == null) {
            return;
        }
        boolean ok = localConnection.sendAirconCommand();
        if (ok) {
            updateStatus(ThingStatus.ONLINE);
            updateAllChannels();
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Failed to send command to aircon unit");
        }
    }

    private void updateProperties() {
        Map<String, String> properties = editProperties();
        properties.put(MhiBindingConstants.PROPERTY_AIRCON_ID, nullToEmpty(state.getAirConID()));
        String firmware = state.getfirmware();
        if (firmware != null) {
            properties.put(MhiBindingConstants.PROPERTY_FIRMWARE, firmware);
        }
        updateProperties(properties);
    }

    private void updateAllChannels() {
        for (String id : new String[] { CHANNEL_POWER, CHANNEL_MODE, CHANNEL_FAN_SPEED, CHANNEL_VANE_UD, CHANNEL_VANE_LR,
                CHANNEL_TARGET_TEMPERATURE, CHANNEL_INDOOR_TEMPERATURE, CHANNEL_OUTDOOR_TEMPERATURE, CHANNEL_ELECTRIC,
                CHANNEL_ERROR_CODE, CHANNEL_ENTRUST, CHANNEL_VACANT, CHANNEL_COOL_HOT_JUDGE, CHANNEL_SELF_CLEAN_OPERATION,
                CHANNEL_SELF_CLEAN_RESET }) {
            updateChannel(id);
        }
    }

    private void updateChannel(String channelId) {
        State value = channelState(channelId);
        if (value != null) {
            updateState(channelId, value);
        }
    }

    private @Nullable State channelState(String channelId) {
        switch (channelId) {
            case CHANNEL_POWER:
                Boolean operation = state.getOperation();
                return OnOffType.from(Boolean.TRUE.equals(operation));
            case CHANNEL_MODE:
                String mode = INT_TO_MODE.get(state.getOperationMode());
                return mode != null ? new StringType(mode) : UnDefType.UNDEF;
            case CHANNEL_FAN_SPEED:
                int flow = state.getAirFlow();
                return flow < 0 ? UnDefType.UNDEF : new StringType(flow == 0 ? "auto" : String.valueOf(flow));
            case CHANNEL_VANE_UD:
                int ud = state.getWindDirectionUD();
                return ud < 0 ? UnDefType.UNDEF : new StringType(String.valueOf(ud));
            case CHANNEL_VANE_LR:
                int lr = state.getWindDirectionLR();
                return lr < 0 ? UnDefType.UNDEF : new StringType(String.valueOf(lr));
            case CHANNEL_TARGET_TEMPERATURE:
                return new QuantityType<>(state.getPresetTemp(), SIUnits.CELSIUS);
            case CHANNEL_INDOOR_TEMPERATURE:
                return temperatureOrUndef(state.getIndoorTemp());
            case CHANNEL_OUTDOOR_TEMPERATURE:
                return temperatureOrUndef(state.getOutdoorTemp());
            case CHANNEL_ELECTRIC:
                float electric = state.getElectric();
                return electric < 0 ? UnDefType.UNDEF : new QuantityType<>(electric, Units.WATT);
            case CHANNEL_ERROR_CODE:
                String errorCode = state.getErrorCode();
                return errorCode == null ? UnDefType.UNDEF : new StringType(errorCode);
            case CHANNEL_ENTRUST:
                return OnOffType.from(state.getEntrust());
            case CHANNEL_VACANT:
                return OnOffType.from(state.getVacant());
            case CHANNEL_COOL_HOT_JUDGE:
                return OnOffType.from(state.getCoolHotJudge());
            case CHANNEL_SELF_CLEAN_OPERATION:
                return OnOffType.from(state.isSelfCleanOperation());
            case CHANNEL_SELF_CLEAN_RESET:
                return OnOffType.from(state.isSelfCleanReset());
            default:
                return null;
        }
    }

    private State temperatureOrUndef(float value) {
        // -100 is the sentinel used by the parser for "unknown".
        return value <= -100.0f ? UnDefType.UNDEF : new QuantityType<>(value, SIUnits.CELSIUS);
    }

    private static String nullToEmpty(@Nullable String value) {
        return value == null ? "" : value;
    }

    private static @Nullable Integer parseInt(String value) {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static @Nullable Integer parseFanSpeed(String value) {
        if ("auto".equalsIgnoreCase(value.trim())) {
            return 0;
        }
        return parseInt(value);
    }

    @SuppressWarnings("unchecked")
    private static @Nullable Float parseTemperature(Command command) {
        if (command instanceof QuantityType) {
            QuantityType<Temperature> quantity = ((QuantityType<Temperature>) command).toUnit(SIUnits.CELSIUS);
            return quantity == null ? null : quantity.floatValue();
        }
        if (command instanceof DecimalType decimal) {
            return decimal.floatValue();
        }
        try {
            return new BigDecimal(command.toString()).floatValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
