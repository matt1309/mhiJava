# Mitsubishi Aircon Java Interface

A MQTT bridge written in Java to itnerface with Mitsubishi Aircon Units.

Heavily inspired/ported from the Home Assistant addon:  
[https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration](https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration)

Uses a java AirCon.java class file that's been created here in case other just want the aircon object that has built
in parser and get/set methods for each attribute (for thread safety). THe send methods also use AirCon attribute 
spamMode. If false calling the sendCommand will wait a specific delay to allow you to make more changes and only send one command to the aircon unit rather than sending data to aircon after each change. 
This is useful for openhab integration where you might change temp using setpoint so go through several temps before reaching your desired one.

Here: https://github.com/matt1309/mhijava2/

---
 
## Installation

Build using java 17 or download prebuilt libraries. 

### 1. Create a config.json for your aircon/MQTT requirements. 

To integrate this library into your project, add the following dependencies to your `pom.xml` file:

```json
  {

"globalSettings" :{"AirconQueryinterval": 30000
                    },


"mqttSettings": {"hostname": "192.168.0.101",
                "username": "openhab",
                "password":"test123"},


"aircon": [{"hostname":"192.168.0.12",
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
