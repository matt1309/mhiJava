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
package org.openhab.binding.mhi.internal.protocol;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.core.io.net.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Handles the HTTP {@code beaver/command} protocol used to talk to MHI WF-RAC
 * aircon units. This replaces the OkHttp/Apache HttpClient based transport from
 * the original standalone bridge with openHAB core's {@link HttpUtil}, and uses
 * gson for JSON handling to avoid bundling extra libraries.
 *
 * The binary encoding/decoding of the {@code airconStat} payload is delegated to
 * {@link AirconState.RacParser}.
 *
 * @author matt1309 - Initial contribution
 */
@NonNullByDefault
public class MhiConnection {

    private static final String CONTENT_TYPE = "application/json";

    private final Logger logger = LoggerFactory.getLogger(MhiConnection.class);

    private final AirconState state;
    private final int timeoutMillis;

    public MhiConnection(AirconState state, int timeoutMillis) {
        this.state = state;
        this.timeoutMillis = timeoutMillis;
    }

    private String buildUrl(String command) {
        return "http://" + state.gethostname() + ":" + state.getport() + "/beaver/command/" + command;
    }

    private JsonObject buildRequest(String command, @Nullable JsonObject contents) {
        JsonObject data = new JsonObject();
        data.addProperty("apiVer", "1.0");
        data.addProperty("command", command);
        data.addProperty("deviceId", state.getDeviceID());
        data.addProperty("operatorId", state.getOperatorID());
        data.addProperty("timestamp", System.currentTimeMillis() / 1000);
        if (contents != null) {
            data.add("contents", contents);
        }
        return data;
    }

    /**
     * Performs a single POST request against the unit and returns the parsed
     * JSON response, or {@code null} if the request failed.
     */
    private synchronized @Nullable JsonObject post(String command, @Nullable JsonObject contents) {
        String url = buildUrl(command);
        String payload = buildRequest(command, contents).toString();
        try {
            String response = HttpUtil.executeUrl("POST", url,
                    new ByteArrayInputStream(payload.getBytes(StandardCharsets.UTF_8)), CONTENT_TYPE, timeoutMillis);
            if (response == null) {
                state.setstatus(false);
                return null;
            }
            logger.trace("Received response from aircon {}: {}", url, response);
            state.setstatus(true);
            return JsonParser.parseString(response).getAsJsonObject();
        } catch (IOException | RuntimeException e) {
            state.setstatus(false);
            logger.debug("Request '{}' to aircon at {} failed: {}", command, url, e.getMessage());
            return null;
        }
    }

    /**
     * Reads the device info and returns the {@code contents} JSON string, or
     * {@code null} on failure.
     */
    public @Nullable String getInfo() {
        JsonObject result = post("getDeviceInfo", null);
        if (result != null && result.has("contents")) {
            return result.get("contents").toString();
        }
        return null;
    }

    /**
     * Polls the unit for its current state and updates {@link #state}.
     *
     * @return {@code true} if the state was successfully retrieved and decoded.
     */
    public boolean getAirconStats() {
        JsonObject result = post("getAirconStat", null);
        return applyResponse(result);
    }

    /**
     * Encodes the current {@link #state} and sends it to the unit, then applies
     * the returned state.
     *
     * @return {@code true} if the command was accepted and the response decoded.
     */
    public boolean sendAirconCommand() {
        JsonObject contents = new JsonObject();
        contents.addProperty("airconId", state.getAirConID());
        contents.addProperty("airconStat", state.parser.toBase64());
        JsonObject result = post("setAirconStat", contents);
        return applyResponse(result);
    }

    private boolean applyResponse(@Nullable JsonObject result) {
        if (result == null || !result.has("contents")) {
            return false;
        }
        try {
            JsonObject contents = result.getAsJsonObject("contents");
            if (contents.has("airconId")) {
                state.setAirConID(contents.get("airconId").getAsString());
            }
            if (contents.has("airconStat")) {
                state.parser.translateBytes(contents.get("airconStat").getAsString());
            }
            return true;
        } catch (RuntimeException e) {
            logger.debug("Failed to translate aircon response: {}", e.getMessage());
            return false;
        }
    }
}
