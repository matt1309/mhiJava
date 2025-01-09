# Mitsubishi Aircon Java Interface

A generic Java interface for Mitsubishi Aircon Units.

Heavily inspired/ported from the Home Assistant addon:  
[https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration](https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration)

---

## How to Use

Include the `AirCon.java` class in your project.

### Initialization and First Connection

```java
// Create object
AirCon aircon = new AirCon();

// Set hostname and port.
aircon.sethostname("192.168.0.12");
aircon.setport("51443");
aircon.setDeviceID("e8165615c7d6"); 
aircon.setOperatorID("openhab");

// Get initial data from the aircon unit
aircon.getAirconStats(false); 

// Add user account using the information stored in aircon object already
aircon.updateAccountInfo("Europe/London");
```

Once the aircon is connected using the above, you can edit attributes using `get` and `set` methods (a complete list of available methods is noted below). 

### Sending Commands to the Aircon Unit

The `get`/`set` methods don't actually get data from the aircon unit; instead, they update the attributes stored in the aircon object. To send changes to the unit, generate the command and send it as follows:

```java
//Edit the attributes you want to change using set methods
aircon.setPresetTemp(21);

// Generate the command
String command = aircon.parser.toBase64();

// Send the command to the aircon unit
aircon.sendAircoCommand(command);
```

The `sendAircoCommand` method sends the request and also updates all relevant attributes based on the response from the AC unit.

---

### Useful Commands for Debugging

```java
// Prints out all the attributes in the aircon device
aircon.printDeviceData();
```

---

## List of Operation Modes

| Mode | Description |
|------|-------------|
| 1    | Dry         |
| 2    | Cool        |
| 3    | Fan         |
| 4    | Heat        |

---

## List of AirFlow Modes

| Mode | Description |
|------|-------------|
| 0    | 1           |
| 1    | 2           |
| 2    | 3           |
| 6    | 4           |
| 7    | Auto        |

---

## List of `get`/`set` Methods and Available Attributes

```java
// Gets the hostname of the aircon unit
aircon.gethostname();

// Sets the hostname of the aircon unit
aircon.sethostname(String hostname);

// Gets the port of the aircon unit
aircon.getport();

// Sets the port of the aircon unit
aircon.setport(String port);

// Gets the device ID of the aircon unit
aircon.getDeviceID();

// Sets the device ID of the aircon unit
aircon.setDeviceID(String DeviceID);

// Gets the operator ID of the aircon unit
aircon.getOperatorID();

// Sets the operator ID of the aircon unit
aircon.setOperatorID(String OperatorID);

// Gets the aircon ID of the unit
aircon.getAirConID();

// Sets the aircon ID of the unit
aircon.setAirConID(String AirConID);

// Gets the operational status of the aircon unit
aircon.getstatus();

// Gets the firmware version of the aircon unit
aircon.getfirmware();

// Sets the firmware version of the aircon unit
aircon.setfirmware(String firmware);

// Gets the number of connected accounts to the aircon unit
aircon.getconnectedAccounts();

// Sets the number of connected accounts to the aircon unit
aircon.setconnectedAccounts(int connectedAccounts);

// Gets the outdoor temperature status
aircon.getOutdoorTemperature();

// Gets the current operation state of the aircon unit
aircon.getOperation();

// Sets the current operation state of the aircon unit
aircon.setOperation(Boolean operation);

// Gets the current operation mode of the aircon unit
aircon.getOperationMode();

// Sets the current operation mode of the aircon unit
aircon.setOperationMode(int operationMode);

// Gets the airflow setting of the aircon unit
aircon.getAirFlow();

// Sets the airflow setting of the aircon unit
aircon.setAirFlow(int airFlow);

// Gets the wind direction (up-down) setting of the aircon unit
aircon.getWindDirectionUD();

// Sets the wind direction (up-down) setting of the aircon unit
aircon.setWindDirectionUD(int windDirectionUD);

// Gets the wind direction (left-right) setting of the aircon unit
aircon.getWindDirectionLR();

// Sets the wind direction (left-right) setting of the aircon unit
aircon.setWindDirectionLR(int windDirectionLR);

// Gets the preset temperature of the aircon unit
aircon.getPresetTemp();

// Sets the preset temperature of the aircon unit
aircon.setPresetTemp(float presetTemp);

// Gets the model number of the aircon unit
aircon.getModelNr();

// Gets the vacant status of the aircon unit
aircon.getVacant();

// Sets the vacant status of the aircon unit
aircon.setVacant(boolean vacant);

// Gets the cool/hot judge status of the aircon unit
aircon.getCoolHotJudge();

// Sets the cool/hot judge status of the aircon unit
aircon.setCoolHotJudge(boolean coolHotJudge);

// Gets the indoor temperature
aircon.getIndoorTemp();

// Gets the outdoor temperature
aircon.getOutdoorTemp();

// Gets the electric usage of the aircon unit
aircon.getElectric();

// Gets the error code of the aircon unit
aircon.getErrorCode();

// Checks if self-cleaning operation is enabled
aircon.isSelfCleanOperation();

// Enables or disables self-cleaning operation
aircon.setSelfCleanOperation(boolean selfCleanOperation);

// Checks if self-cleaning reset is enabled
aircon.isSelfCleanReset();

// Enables or disables self-cleaning reset
aircon.setSelfCleanReset(boolean selfCleanReset);

// Gets the next request time for the aircon unit
aircon.isnextRequestAfter();

// Sets the next request time for the aircon unit
aircon.setSelfCleanReset(LocalDateTime nextRequestAfter);

// Gets the minimum refresh rate of the aircon unit
aircon.isminrefreshRate();

// Sets the minimum refresh rate of the aircon unit
aircon.setSelfCleanReset(long minrefreshRate);
```
