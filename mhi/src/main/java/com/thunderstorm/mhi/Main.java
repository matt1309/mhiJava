package com.thunderstorm.mhi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) {

        Boolean spamMode = true; // TO be written, when false the mqtt messages will be sent at a static periodic
                                 // interval
        // with AC object being fully threadsafe this can be done. And then mqtt
        // messages will be sent every 5seconds if change
        // every 60 seconds if no change. This will allow for batch sends.
        int delaybuffer = 2000;

        // Variable setup
        List<AirCon> aircons = new ArrayList<>();
        String settingsFilePath = "config.json";
        // Default query frequency (Assuming no requests)
        int interval = 60000; // 60 seconds
        boolean mqtt = false; // Default is not to use mqtt.
        String mqttHostname = "";
        String mqttUsername = "";
        String mqttPassword = "";

        // Read settings from file if it exists
        File settingsFile = new File(settingsFilePath);
        if (settingsFile.exists()) {
            try {
                StringBuilder jsonContent = new StringBuilder();
                Scanner scanner = new Scanner(settingsFile);
                while (scanner.hasNextLine()) {
                    jsonContent.append(scanner.nextLine());
                }
                scanner.close();

                // Parse JSON content
                JSONObject settings = new JSONObject(jsonContent.toString());
                JSONArray airconSettings = settings.getJSONArray("aircon");

                if (settings.has("mqttSettings")) {

                    mqtt = true;

                    JSONObject mqttSetting = (JSONObject) settings.get("mqttSettings");
                    // Ensure required settings are defined
                    if (!mqttSetting.has("hostname") || !mqttSetting.has("username") ||
                            !mqttSetting.has("username") || !mqttSetting.has("password")) {
                        throw new JSONException("Missing required MQTT settings in config.json");

                    }
                    // edit so username and password are optional and add features like tls etc
                    mqttHostname = mqttSetting.get("hostname").toString();
                    mqttUsername = mqttSetting.get("username").toString();
                    mqttPassword = mqttSetting.get("password").toString();

                }

                if (settings.has("globalSettings")
                        && (((JSONObject) settings.get("globalSettings")).has("AirconQueryinterval"))) {

                    try {
                        interval = (int) (((Number) ((JSONObject) settings.get("globalSettings"))
                                .get("AirconQueryinterval")).floatValue());

                        if (((JSONObject) settings.get("globalSettings")).has("spamMode")) {

                            try {
                                spamMode = (boolean) ((JSONObject) settings.get("globalSettings")).get("spamMode");
                            } catch (Exception e) {

                                spamMode = true;
                            }
                        }

                        if (((JSONObject) settings.get("globalSettings")).has("spamModeInterval")) {

                            try {
                                delaybuffer = (int) ((JSONObject) settings.get("globalSettings"))
                                        .get("spamModeInterval");
                                spamMode = false;
                            } catch (Exception e) {

                                spamMode = true;
                            }
                        }

                    } catch (Exception e) {

                        System.out.println("Error in type of number: " + e.toString());
                    }

                }

                // Load aircon settings
                for (int i = 0; i < airconSettings.length(); i++) {
                    JSONObject airconSetting = airconSettings.getJSONObject(i);
                    AirCon aircon = new AirCon();
                    aircon.spamMode = spamMode;
                    aircon.delayBuffer = delaybuffer; // to add this to settings only used if spamMode = false;

                    // Ensure required settings are defined
                    if (!airconSetting.has("hostname") || !airconSetting.has("port") ||
                            !airconSetting.has("deviceID") || !airconSetting.has("operatorID")) {
                        throw new JSONException("Missing required aircon settings in config.json");
                    }

                    aircon.sethostname(airconSetting.getString("hostname"));
                    aircon.setport(airconSetting.getString("port"));
                    aircon.setDeviceID(airconSetting.getString("deviceID"));
                    aircon.setOperatorID(airconSetting.getString("operatorID"));
                    aircons.add(aircon);
                }

            } catch (FileNotFoundException e) {
                System.err.println("Settings file not found: " + e.getMessage());
            } catch (JSONException e) {
                System.err.println("Error parsing JSON from config.json: " + e.getMessage());
            }
        } else {
            // Handle case where no settings file exists
            try {
                AirCon aircon = new AirCon();
                String hostname = null;
                String port = null;
                String deviceID = null;
                String operatorID = null;

                // Parse command-line arguments
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
                        case "-mH":
                            mqttHostname = args[++i];
                            break;
                        case "-mU":
                            mqttUsername = args[++i];
                            break;
                        case "-mP":
                            mqttPassword = args[++i];
                            break;
                        default:
                            System.err.println("Unknown argument: " + args[i]);
                    }
                }

                if (hostname == null || port == null || deviceID == null || operatorID == null) {
                    throw new IllegalArgumentException("Missing required arguments: -h, -p, -d, -o");
                }

                aircon.sethostname(hostname);
                aircon.setport(port);
                aircon.setDeviceID(deviceID);
                aircon.setOperatorID(operatorID);
                aircons.add(aircon);

            } catch (Exception e) {
                System.err.println("Error configuring aircon from arguments: " + e.getMessage());
            }
        }

        // Process each AirCon instance
        for (AirCon aircon : aircons) {
            try {
                aircon.getAirconStats(false);
                aircon.updateAccountInfo("Europe/London");
                aircon.printDeviceData();
                System.out.println(aircon.getconnectedAccounts());
            } catch (Exception e) {
                System.err.println("Error processing aircon: " + e.getMessage());
            }
        }

        // Setup MQTT bridge
        // String mqttHostname = "tcp://192.168.0.101";

        while(true){

        try {
            MqttAirConBridge mqttService = null;

            if (mqtt) {
                mqttService = new MqttAirConBridge(aircons, mqttHostname, interval, mqttUsername,
                        mqttPassword, spamMode);
            }

            while (true) {
                try {
                    for (AirCon aircon : aircons) {
                        System.out.println("Checking in with aircon unit " + aircon.getAirConID());
                        if (aircon.getAirconStats(false) & mqtt) {

                            if (!aircon.getstatus() && mqtt) {
                                mqttService.logToMQTT(aircon, "Error receiving data from aircon");
                                System.out.println("Error receiving data from aircon");
                                aircon.setstatus(false);

                                mqttService.publishNow(aircon);

                            } else if (mqtt) {

                                if (aircon.getstatus()) {

                                    aircon.setstatus(true);
                                    mqttService.publishNow(aircon);

                                }

                                mqttService.logToMQTT(aircon, "Ok");
                            }

                        }

                        System.out.println("Sleeping for " + interval / 1000 + " seconds...");
                    }
                    Thread.sleep(interval);
                } catch (Exception e) {
                    System.err.println("Error during MQTT loop: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error initializing MQTT bridge: " + e.getMessage());
        }

        System.out.println("Error caught trying again after 30seconds");
        try{
        Thread.sleep(30000);
        } catch (Exception e){

            System.out.println("Error sleeping: " + e.toString());
        }
    }
    }
}
