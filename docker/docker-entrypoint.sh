#!/bin/bash
set -e

# Build config.json from environment variables
# This script is the Docker entrypoint: it generates config.json and then runs the app.

CONFIG_FILE="/app/config.json"

# -------------------------------------------------------------------
# Helper: add a key-value pair to a JSON object if the env var exists
# Usage: add_if_set <jq_filter> <env_var_name> <jq_key>
# -------------------------------------------------------------------
add_if_set() {
    local filter="$1"
    local env_var="$2"
    local json_key="$3"
    local value="${!env_var}"
    if [ -n "$value" ]; then
        echo "$filter" | jq --arg key "$json_key" --arg val "$value" '. + {($key): $val}'
    else
        echo "$filter"
    fi
}

add_if_set_num() {
    local filter="$1"
    local env_var="$2"
    local json_key="$3"
    local value="${!env_var}"
    if [ -n "$value" ]; then
        echo "$filter" | jq --arg key "$json_key" --argjson val "$value" '. + {($key): $val}'
    else
        echo "$filter"
    fi
}

add_if_set_bool() {
    local filter="$1"
    local env_var="$2"
    local json_key="$3"
    local value="${!env_var}"
    if [ -n "$value" ]; then
        if [ "$value" = "true" ] || [ "$value" = "True" ] || [ "$value" = "TRUE" ] || [ "$value" = "1" ]; then
            echo "$filter" | jq --arg key "$json_key" '. + {($key): true}'
        else
            echo "$filter" | jq --arg key "$json_key" '. + {($key): false}'
        fi
    else
        echo "$filter"
    fi
}

# -------------------------------------------------------------------
# Validate mandatory environment variables
# -------------------------------------------------------------------
if [ -z "$MQTT_HOSTNAME" ]; then
    echo "ERROR: MQTT_HOSTNAME environment variable is required but not set."
    echo "Usage: docker run -e MQTT_HOSTNAME=<broker-address> ..."
    exit 1
fi

# -------------------------------------------------------------------
# Build globalSettings
# -------------------------------------------------------------------
global=$(echo '{}' | jq '.')
global=$(add_if_set_num "$global" "AIRCON_QUERY_INTERVAL" "AirconQueryinterval")
global=$(add_if_set_bool "$global" "SPAM_MODE" "spamMode")
global=$(add_if_set_num "$global" "SPAM_MODE_INTERVAL" "spamModeInterval")
global=$(add_if_set_bool "$global" "GENERATE_OPENHAB_TEMPLATES" "generateOpenhabTemplates")

# -------------------------------------------------------------------
# Build mqttSettings
# MQTT_HOSTNAME is validated as mandatory above.
# Username and password are always included, defaulting to empty string.
# -------------------------------------------------------------------
mqtt=$(echo '{}' | jq --arg v "$MQTT_HOSTNAME" '. + {"hostname": $v}')
mqtt=$(echo "$mqtt" | jq --arg v "${MQTT_USERNAME:-}" '. + {"username": $v}')
mqtt=$(echo "$mqtt" | jq --arg v "${MQTT_PASSWORD:-}" '. + {"password": $v}')

# -------------------------------------------------------------------
# Build aircon array
# -------------------------------------------------------------------
aircon_json="[]"

# Loop through indices until no more AIRCON_<N>_HOSTNAME is found
for idx in {1..100}; do
    hostname_var="AIRCON_${idx}_HOSTNAME"
    hostname="${!hostname_var}"
    if [ -z "$hostname" ]; then
        break
    fi

    device_id_var="AIRCON_${idx}_DEVICE_ID"
    device_id="${!device_id_var}"

    if [ -z "$device_id" ]; then
        echo "WARNING: AIRCON_${idx}_HOSTNAME is set but AIRCON_${idx}_DEVICE_ID is missing. Skipping aircon ${idx}."
        continue
    fi

    entry=$(echo '{}' | jq --arg v "$hostname" '. + {"hostname": $v}')

    port_var="AIRCON_${idx}_PORT"
    port="${!port_var:-51443}"
    entry=$(echo "$entry" | jq --arg v "$port" '. + {"port": $v}')

    entry=$(echo "$entry" | jq --arg v "$device_id" '. + {"deviceID": $v}')

    operator_id_var="AIRCON_${idx}_OPERATOR_ID"
    operator_id="${!operator_id_var:-openhab}"
    entry=$(echo "$entry" | jq --arg v "$operator_id" '. + {"operatorID": $v}')

    name_var="AIRCON_${idx}_NAME"
    name="${!name_var}"
    if [ -n "$name" ]; then
        entry=$(echo "$entry" | jq --arg v "$name" '. + {"name": $v}')
    fi

    aircon_json=$(echo "$aircon_json" | jq --argjson e "$entry" '. + [$e]')
done

# -------------------------------------------------------------------
# Assemble final config.json
# -------------------------------------------------------------------
config=$(echo '{}' | jq '.')

# Add globalSettings (only if it has content)
if [ "$(echo "$global" | jq '. | length')" -gt 0 ]; then
    config=$(echo "$config" | jq --argjson gs "$global" '. + {"globalSettings": $gs}')
fi

# Add mqttSettings (MQTT_HOSTNAME is validated as mandatory)
config=$(echo "$config" | jq --argjson ms "$mqtt" '. + {"mqttSettings": $ms}')

# Add aircon array (only if we have at least one aircon)
if [ "$(echo "$aircon_json" | jq '. | length')" -gt 0 ]; then
    config=$(echo "$config" | jq --argjson aa "$aircon_json" '. + {"aircon": $aa}')
fi

# Write config.json
echo "$config" > "$CONFIG_FILE"
echo "Generated config.json:"
cat "$CONFIG_FILE"

# -------------------------------------------------------------------
# Execute the main container command
# -------------------------------------------------------------------
exec "$@"