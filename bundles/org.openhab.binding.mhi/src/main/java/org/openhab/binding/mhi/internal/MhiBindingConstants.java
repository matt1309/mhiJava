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
package org.openhab.binding.mhi.internal;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.core.thing.ThingTypeUID;

/**
 * The {@link MhiBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author matt1309 - Initial contribution
 */
@NonNullByDefault
public class MhiBindingConstants {

    public static final String BINDING_ID = "mhi";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_AIRCON = new ThingTypeUID(BINDING_ID, "aircon");

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Set.of(THING_TYPE_AIRCON);

    // List of all Channel ids
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_MODE = "mode";
    public static final String CHANNEL_FAN_SPEED = "fanSpeed";
    public static final String CHANNEL_VANE_UD = "vaneUpDown";
    public static final String CHANNEL_VANE_LR = "vaneLeftRight";
    public static final String CHANNEL_TARGET_TEMPERATURE = "targetTemperature";
    public static final String CHANNEL_INDOOR_TEMPERATURE = "indoorTemperature";
    public static final String CHANNEL_OUTDOOR_TEMPERATURE = "outdoorTemperature";
    public static final String CHANNEL_ELECTRIC = "electric";
    public static final String CHANNEL_ERROR_CODE = "errorCode";
    public static final String CHANNEL_ENTRUST = "entrust";
    public static final String CHANNEL_VACANT = "vacant";
    public static final String CHANNEL_COOL_HOT_JUDGE = "coolHotJudge";
    public static final String CHANNEL_SELF_CLEAN_OPERATION = "selfCleanOperation";
    public static final String CHANNEL_SELF_CLEAN_RESET = "selfCleanReset";

    // Thing properties
    public static final String PROPERTY_AIRCON_ID = "airconId";
    public static final String PROPERTY_FIRMWARE = "firmware";
    public static final String PROPERTY_CONNECTED_ACCOUNTS = "connectedAccounts";
}
