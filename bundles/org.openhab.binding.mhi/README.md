# MHI Aircon Binding

This binding integrates Mitsubishi Heavy Industries (MHI) **WF-RAC** Wi-Fi air conditioning units directly into openHAB.

It talks to the units over their local HTTP `beaver/command` API (the same protocol used by the MHI *Smart M-Air* app), so **no cloud account and no MQTT broker are required**. The binding is a native port of the standalone [`mhiJava`](https://github.com/matt1309/mhiJava) MQTT bridge: the proven `AirCon`/`RacParser` protocol encoding/decoding is reused unchanged, and the bridge's **spam mode** command debouncing is preserved as a Thing option.

## Supported Things

| Thing type | Description                            |
|------------|----------------------------------------|
| `aircon`   | A single MHI WF-RAC air conditioning unit |

## Discovery

Units that advertise themselves over mDNS/Bonjour (`_beaver._tcp`) are discovered automatically and appear in the Inbox. Discovered Things are pre-populated with `hostname`, `port` and `deviceId`. Units can also be added manually.

## Thing Configuration

### `aircon`

| Parameter          | Type    | Required | Default   | Description                                                                                          |
|--------------------|---------|----------|-----------|------------------------------------------------------------------------------------------------------|
| `hostname`         | text    | yes      | –         | Hostname or IP address of the aircon unit.                                                            |
| `deviceId`         | text    | yes      | –         | Device ID (MAC-style identifier) of the aircon unit.                                                  |
| `port`             | integer | no       | `51443`   | Port the aircon unit listens on.                                                                     |
| `operatorId`       | text    | no       | `openhab` | Operator ID used when registering with the unit.                                                     |
| `refreshInterval`  | integer | no       | `60`      | Polling interval in seconds.                                                                          |
| `spamMode`         | boolean | no       | `false`   | When `true`, commands are sent immediately. When `false`, rapid changes are debounced (see below).   |
| `spamModeInterval` | integer | no       | `3000`    | Debounce window in milliseconds used when `spamMode` is `false`.                                      |

These map one-to-one onto the settings that used to live in the standalone bridge's `config.json`.

## Spam Mode

openHAB UIs often emit a stream of commands for a single user action — for example dragging a setpoint slider from 18 °C to 20 °C produces separate `19` and `20` commands. Sending each of these to the unit is slow and unnecessary.

* **`spamMode = false` (default):** each incoming command updates the local state and (re)schedules a single send `spamModeInterval` milliseconds in the future, cancelling any previously scheduled send. Only the **final** state (`20 °C`) is transmitted to the unit.
* **`spamMode = true`:** every command is sent to the unit immediately.

## Channels

| Channel              | Type                 | Read/Write | Description                                             |
|----------------------|----------------------|------------|---------------------------------------------------------|
| `power`              | Switch               | R/W        | Turns the unit on or off.                               |
| `mode`               | String               | R/W        | Operating mode: `auto`, `cool`, `heat`, `dry`, `fan`.   |
| `fanSpeed`           | String               | R/W        | Fan speed: `auto`, `2`, `3`, `4`.                       |
| `vaneUpDown`         | String               | R/W        | Vertical vane: `0` (swing) – `4`.                       |
| `vaneLeftRight`      | String               | R/W        | Horizontal vane: `0` (swing) – `7`.                     |
| `targetTemperature`  | Number:Temperature   | R/W        | Set point temperature (0.5 °C steps).                   |
| `indoorTemperature`  | Number:Temperature   | R          | Measured indoor temperature.                            |
| `outdoorTemperature` | Number:Temperature   | R          | Measured outdoor temperature.                           |
| `electric`           | Number:Power         | R          | Instantaneous power draw (W).                           |
| `errorCode`          | String               | R          | Current error code (`00` = no error).                   |
| `entrust`            | Switch               | R/W        | Entrust / auto-adjust mode.                             |
| `vacant`             | Switch               | R/W        | Vacancy / away mode.                                    |
| `coolHotJudge`       | Switch               | R/W        | Cool/hot judgement flag.                                |
| `selfCleanOperation` | Switch               | R/W        | Self-clean operation state.                             |
| `selfCleanReset`     | Switch               | R/W        | Resets the self-clean function.                         |

## Full Example

`things/mhi.things`:

```java
Thing mhi:aircon:livingroom "Living Room AC" [ hostname="192.168.0.12", deviceId="e8165615c7d6", refreshInterval=60, spamMode=false, spamModeInterval=3000 ]
```

`items/mhi.items`:

```java
Switch              LivingRoom_Power   "Power"        { channel="mhi:aircon:livingroom:power" }
String              LivingRoom_Mode    "Mode"         { channel="mhi:aircon:livingroom:mode" }
Number:Temperature  LivingRoom_SetTemp "Set Temp"     { channel="mhi:aircon:livingroom:targetTemperature" }
Number:Temperature  LivingRoom_Indoor  "Indoor Temp"  { channel="mhi:aircon:livingroom:indoorTemperature" }
Number:Temperature  LivingRoom_Outdoor "Outdoor Temp" { channel="mhi:aircon:livingroom:outdoorTemperature" }
```
