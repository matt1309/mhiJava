package com.thunderstorm.mhi;

import com.thunderstorm.mhi.AirCon;

public class Main {
    public static void main(String[] args) {
        // System.out.println("Hello world!");

        // Create object
        AirCon aircon = new AirCon();

        // Set hostname and port.
        aircon.sethostname("192.168.0.12");
        aircon.setport("51443");
        aircon.setDeviceID("e8165615c7d6");
        aircon.setOperatorID("openhab");
        

        /*
         * this.command = command; // "getAirconStat"
         * this.apiVer = apiVer; // "1.0"
         * this.operatorId = operatorId; // "openhab"
         * this.deviceId = deviceId; // "e8165615c7d6"
         * this.timestamp = timestamp; // 842151729
         * this.result = result; // 1
         * this.contents = contents;
         * 
         */

        try {
            // Get initial stats from aircon unit and parse into the aircon object.
            // System.out.println("Made it this far");
            aircon.getAirconStats(false);

            aircon.updateAccountInfo("Europe/London");
            // Berlow is the old way of, closer to how python managed it
            // aircon.parser.translateBytes(aircon.getAirconStats(false));

            aircon.printDeviceData();

            System.out.println(aircon.getconnectedAccounts());

        } catch (Exception e) {

            System.out.println(e.toString());
        }

       // if (args[0] == "mqtt") {

           // String mqttHostname = args[1];
           String mqttHostname = "tcp://192.168.0.101";
            MqttAirConBridge mqttService;

            try{
             mqttService = new MqttAirConBridge(aircon, mqttHostname, aircon.getDeviceID(),5000,"openhab", "");
            // aircon.mqttStart(mqttHostname, mqttService);

            new Thread(() -> {
                while (true) {
                    try {
                        System.out.println("checking in with aircon unit " + aircon.getAirConID());
                        aircon.getAirconStats(false);
                       // mqttService.publishNow(aircon);
                        System.out.println("Sleeping");
    
                        Thread.sleep(100000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
    




            //mqttService.startPublishing(aircon);
        }catch (Exception e){
            System.out.println(e.toString());
        }

       //}     

           
        


        System.out.println("Aircon ID: " + aircon.getAirConID());




       

        /*
         * 
         * 
         * //aircon.setOperationMode(1); // not Sure what operation modes are but they
         * can be 0 to 4. Need to add
         * // checking for this ie if not inside that range throw log error.
         * aircon.setPresetTemp(18); // set Temp to 21.
         * 
         * // Tranlsating command into base64 to send to unit. Might try consolidate
         * this
         * // into the sendAircoComand function. So parser is truly hidden from users of
         * // object.
         * String command = aircon.parser.toBase64();
         * 
         * try {
         * // sending command to the aircon unit itself
         * aircon.sendAircoCommand(command);
         * 
         * aircon.printDeviceData();
         * 
         * } catch (Exception e) {
         * 
         * System.out.println(e.toString());
         * }
         */

    }

}
