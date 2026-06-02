# Mitsubishi Aircon Java Interface

A MQTT bridge written in Java to itnerface with Mitsubishi Aircon Units.

Heavily inspired/ported from the Home Assistant addon:  
[https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration](https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration)

This bridge uses the java AirCon.java class file that's been created in a seperate github repo. Just in case others just want the aircon object for their own projects. The aircon class has a built in parser to translate data sent from/to the aircon units and get/set methods for each attribute (for thread safety). The send methods also use AirCon attribute spamMode.
If spamMode is false the sendCommand will wait a specified delay ("spamModeInterval" in milliseonds in config.json) to allow you to make more changes and only send one request to the aircon unit rather than sending data to aircon after each change.
This is useful for openhab integration where you might change temp using setpoint. ie if openhab setpoint is 18 and you slide a slider to 20, openhab will send MQTT message for change to 19 and then again for change to 20. spamMode and spamModeInterval delay will allow the sending of 19 to be skipped as temp is updated to 20 within the spamModeInterval time reducing messages sent to aircon unit.

Here: https://github.com/matt1309/mhijava2/

---

## Installation

Build using java 17 or download prebuilt libraries.

### 1. Create a config.json for your aircon/MQTT requirements.

To integrate this library into your project, add the following dependencies to your `pom.xml` file:

```json
{
  "globalSettings": {
    "AirconQueryinterval": 30000,
    "spamMode": false,
    "spamModeInterval": 3000
  },

  "mqttSettings": {
    "hostname": "192.168.0.101",
    "username": "openhab",
    "password": "test123"
  },

  "aircon": [
    {
      "hostname": "192.168.0.12",
      "port": "51443",
      "deviceID": "e8165615c7d6",
      "operatorID": "openhab"
    }
  ]
}
```

### 2. Run the .jar file

```
java -jar mhi.jar
```

### 3. Topic paths:

The topic structure is as follows:
aircon/{airconID}/ReadOnly/{dataName} for state reading

Open examples can be fonud in openhabTemplates folder (I will share my final config once finalised)
aircon/{airconID}/ReadWrite/{dataName} for command writing

Anything posted in ReadWrite is passed to aircon, the response from aircon is then loaded into
ReadOnly topics.

### Usage:

The bridge is based on an aircon java class that can be found here for standalone development/inlcusion within other topics for people.

---

## Docker

The application can be run as a Docker container. All configuration is provided via environment variables — no `config.json` file needed.

### Build

```bash
docker build -f docker/Dockerfile -t mhijava .
```

### Environment Variables

| Variable                     | Required | Default   | Description                                     |
| ---------------------------- | -------- | --------- | ----------------------------------------------- |
| `MQTT_HOSTNAME`              | **Yes**  | —         | MQTT broker hostname or IP address              |
| `MQTT_USERNAME`              | No       | `""`      | MQTT username (empty = anonymous connection)    |
| `MQTT_PASSWORD`              | No       | `""`      | MQTT password (empty = anonymous connection)    |
| `AIRCON_QUERY_INTERVAL`      | No       | `60000`   | Query interval in milliseconds                  |
| `SPAM_MODE`                  | No       | `true`    | Enable spam mode (batch changes before sending) |
| `SPAM_MODE_INTERVAL`         | No       | `2000`    | Spam mode interval in milliseconds              |
| `GENERATE_OPENHAB_TEMPLATES` | No       | `false`   | Generate openHAB templates on startup           |
| `AIRCON_<N>_HOSTNAME`        | **Yes**¹ | —         | Aircon unit hostname or IP                      |
| `AIRCON_<N>_DEVICE_ID`       | **Yes**¹ | —         | Aircon unit device ID                           |
| `AIRCON_<N>_PORT`            | No       | `51443`   | Aircon unit port                                |
| `AIRCON_<N>_OPERATOR_ID`     | No       | `openhab` | Aircon operator ID                              |
| `AIRCON_<N>_NAME`            | No       | —         | Optional friendly name for the aircon           |

¹ At least one aircon unit is required. Replace `<N>` with an index starting at 1 (e.g. `AIRCON_1_HOSTNAME`, `AIRCON_2_HOSTNAME`).

### Docker CLI Example

```bash
# Single aircon unit
docker run -d \
  --name mhijava \
  -e MQTT_HOSTNAME=192.168.0.101 \
  -e MQTT_USERNAME=openhab \
  -e MQTT_PASSWORD=test123 \
  -e AIRCON_1_HOSTNAME=192.168.0.12 \
  -e AIRCON_1_DEVICE_ID=e8165615c7d6 \
  mhijava

# Multiple aircon units
docker run -d \
  --name mhijava \
  -e MQTT_HOSTNAME=192.168.0.101 \
  -e MQTT_PASSWORD=test123 \
  -e AIRCON_1_HOSTNAME=192.168.0.12 \
  -e AIRCON_1_DEVICE_ID=e8165615c7d6 \
  -e AIRCON_2_HOSTNAME=192.168.0.13 \
  -e AIRCON_2_DEVICE_ID=abc123def456 \
  -e AIRCON_2_NAME="Living Room" \
  mhijava
```

### Inspect Generated Config

To verify the generated `config.json` inside a running container:

```bash
docker exec mhijava cat /app/config.json
```

### Docker Compose Example

Create a `docker-compose.yml` file:

```yaml
version: "3.8"

services:
  mhijava:
    build:
      context: .
      dockerfile: docker/Dockerfile
    container_name: mhijava
    restart: unless-stopped
    environment:
      # MQTT broker settings
      MQTT_HOSTNAME: 192.168.0.101
      MQTT_USERNAME: openhab
      MQTT_PASSWORD: test123

      # Global settings
      AIRCON_QUERY_INTERVAL: 60000
      SPAM_MODE: "false"
      SPAM_MODE_INTERVAL: 3000

      # First aircon unit
      AIRCON_1_HOSTNAME: 192.168.0.12
      AIRCON_1_DEVICE_ID: e8165615c7d6
      AIRCON_1_PORT: "51443"
      AIRCON_1_OPERATOR_ID: openhab

      # Second aircon unit (optional)
      AIRCON_2_HOSTNAME: 192.168.0.13
      AIRCON_2_DEVICE_ID: abc123def456
      AIRCON_2_NAME: "Living Room"
```

Then run:

```bash
docker compose up -d
```
