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

import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * The {@link MhiAirconConfiguration} class contains fields mapping the Thing
 * configuration parameters. These map to the values previously provided in the
 * standalone bridge's {@code config.json}.
 *
 * @author matt1309 - Initial contribution
 */
@NonNullByDefault
public class MhiAirconConfiguration {

    /** Hostname or IP address of the aircon unit. */
    public String hostname = "";

    /** Port the aircon unit listens on. */
    public int port = 51443;

    /** Device ID of the aircon unit. */
    public String deviceId = "";

    /** Operator ID used when registering with the unit. */
    public String operatorId = "openhab";

    /** Polling interval in seconds. */
    public int refreshInterval = 60;

    /**
     * When {@code true} commands are sent to the unit immediately. When
     * {@code false} rapid changes are coalesced (debounced) for
     * {@link #spamModeInterval} milliseconds before a single request is sent.
     */
    public boolean spamMode = false;

    /** Debounce window in milliseconds used when {@link #spamMode} is false. */
    public int spamModeInterval = 3000;
}
