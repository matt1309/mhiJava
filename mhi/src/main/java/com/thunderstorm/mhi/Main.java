package com.thunderstorm.mhi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.thunderstorm.mhi.AirCon;

public class Main {
    public static void main(String[] args) {
        // System.out.println("Hello world!");

        // Create list of aircons
        List<AirCon> aircons = new ArrayList<AirCon>();
        // AirCon aircon = new AirCon();
        StringBuilder jsonContent = new StringBuilder();

        //checks for settings file
        String settingsFilePath = "config.json";
        File settingsFile = new File(settingsFilePath);

        if (settingsFile.exists()) {
            //if settings file read from it
            try {
                Scanner scanner = new Scanner(settingsFile);
                // StringBuilder jsonContent = new StringBuilder();
                while (scanner.hasNextLine()) {
                    jsonContent.append(scanner.nextLine());
                }
                scanner.close();
            } catch (Exception e) {

                System.out.println(e.toString());
            }


            try {
            JSONObject settings = new JSONObject(jsonContent.toString());
            JSONArray airconSettings = settings.getJSONArray("aircon");
            JSONObject mqttSettings = settings.getJSONObject("mqttSettings");
            JSONObject globalSettings = settings.getJSONObject("globalSettings");
            catch (Exception e){

                System.out.println("Error loading settings from Config.json : " e.toString());
            }

            //Loop and Load aircon settings.
            for (int i = 0; i < airconSettings.length(); i++) {

                AirCon aircon = new AirCon();
                JSONObject airconSetting = airconSettings.getJSONObject(i);

                aircon.sethostname(airconSetting.get("hostname").toString());
                aircon.setport(airconSetting.get("port").toString());
                aircon.setDeviceID(airconSetting.get("deviceID").toString());
                aircon.setOperatorID(airconSetting.get("operatorID").toString());

                aircons.add(aircon);

            }

        } else {
            //if no settings file then only one Aircon unit can be added at one time via input args to do this section.
            AirCon aircon = new AirCon();



            try{

                //mhi.jar -h 192.168.0.101 -p 51443 -d e8165615c7d6 -o openhab -mH 192.168.0.101 -mP 1883 -mU username -mP password -I 5000
                

                String hostname = null;
                String port = null;
                String deviceID = null;
                String operatorID = null;
        
                for (int i = 0; i < args.length; i++) {
                    switch (args[i]) {
                        case "-h":
                            hostname = args[++i];
                            break;
                        case "-p":
                            port = args[++i];
                            break;
                        case "-d":
                            deviceID = args[++i];
                            break;
                        case "-o":
                            operatorID = args[++i];
                            break;
                        // Add cases for other arguments as needed
                    }
                }




                aircon.sethostname(hostname);
                aircon.setport(port);
                aircon.setDeviceID(deviceID);
                aircon.setOperatorID(operatorID);
    


            }catch (Exception e){

                System.out.println("Error loading settings, missing config.json and one or more missing input args: " e.toString());

            }
            //this will change to being static input args. 
            
            // Set hostname and port.
         
            aircons.add(aircon);

        }

        
        //loop each aircon to get the data. 
        for (AirCon aircon : aircons) {

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
        }

        // if (args[0] == "mqtt") {

        // String mqttHostname = args[1];

        //setup mqtt to read settings files is on the todo list. else use input args again. 
        String mqttHostname = "tcp://192.168.0.101";
        MqttAirConBridge mqttService;

        try {
            mqttService = new MqttAirConBridge(aircons, mqttHostname, 5000, "openhab", "");
            // aircon.mqttStart(mqttHostname, mqttService);

            new Thread(() -> {
                while (true) {
                    try {

                        for (AirCon aircon : aircons) {
                            System.out.println("checking in with aircon unit " + aircon.getAirConID());
                            aircon.getAirconStats(false);
                            mqttService.publishNow(aircon);
                            System.out.println("Sleeping");
                        }

                        Thread.sleep(100000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // mqttService.startPublishing(aircon);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        // }

        // System.out.println("Aircon ID: " + aircon.getAirConID());

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
