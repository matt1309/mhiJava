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
package org.openhab.binding.mhi.internal.discovery;

import java.util.Map;
import java.util.Set;

import javax.jmdns.ServiceInfo;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.openhab.binding.mhi.internal.MhiBindingConstants;
import org.openhab.core.config.discovery.DiscoveryResult;
import org.openhab.core.config.discovery.DiscoveryResultBuilder;
import org.openhab.core.config.discovery.mdns.MDNSDiscoveryParticipant;
import org.openhab.core.thing.ThingTypeUID;
import org.openhab.core.thing.ThingUID;
import org.osgi.service.component.annotations.Component;

/**
 * Discovers MHI WF-RAC aircon units advertised over mDNS/Bonjour using the
 * {@code _beaver._tcp.local.} service type.
 *
 * @author matt1309 - Initial contribution
 */
@NonNullByDefault
@Component(service = MDNSDiscoveryParticipant.class)
public class MhiDiscoveryParticipant implements MDNSDiscoveryParticipant {

    private static final String SERVICE_TYPE = "_beaver._tcp.local.";

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return MhiBindingConstants.SUPPORTED_THING_TYPES_UIDS;
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public @Nullable DiscoveryResult createResult(ServiceInfo service) {
        ThingUID uid = getThingUID(service);
        if (uid == null) {
            return null;
        }
        String host = service.getHostAddresses().length > 0 ? service.getHostAddresses()[0] : service.getName();
        int port = service.getPort() > 0 ? service.getPort() : 51443;
        String deviceId = uid.getId();

        Map<String, Object> properties = Map.of("hostname", host, "port", port, "deviceId", deviceId);

        return DiscoveryResultBuilder.create(uid).withProperties(properties).withRepresentationProperty("deviceId")
                .withLabel("MHI Aircon " + deviceId).build();
    }

    @Override
    public @Nullable ThingUID getThingUID(ServiceInfo service) {
        String deviceId = service.getPropertyString("deviceId");
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = service.getName();
        }
        if (deviceId == null || deviceId.isBlank()) {
            return null;
        }
        String sanitized = deviceId.replaceAll("[^A-Za-z0-9_]", "");
        return new ThingUID(MhiBindingConstants.THING_TYPE_AIRCON, sanitized);
    }
}
