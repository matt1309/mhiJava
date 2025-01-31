# Mitsubishi Aircon Java Interface

A MQTT bridge written in Java to itnerface with Mitsubishi Aircon Units.

Heavily inspired/ported from the Home Assistant addon:  
[https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration](https://github.com/jeatheak/Mitsubishi-WF-RAC-Integration)

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
aircon/{airconID}/ReadWrite/{dataName} for command writing

Anything posted in ReadWrite is passed to aircon, the response from aircon is then loaded into
ReadOnly topics. 

### Usage:
The bridge is based on an aircon java class that can be found here for standalone development/inlcusion within other topics for people. 
