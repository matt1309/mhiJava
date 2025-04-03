package com.thunderstorm.mhi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

public class jsonParser {

    private static ConcurrentHashMap<String, List<String>> boolTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, List<String>> stringTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, List<String>> floatTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, List<String>> intTopics = new ConcurrentHashMap<String, List<String>>();

    public static void jsonParser(ConcurrentHashMap<String, AirCon> aircons, JSONObject jsonAircons,
            MqttAirConBridge mqttbridge) {
        // Assuming the JSON array is named "airconSettings"
        JSONArray airconArray = jsonAircons.getJSONArray("airconSettings");
        // List<String> airconIDs = getAirConIDs(aircons);

        for (int i = 0; i < airconArray.length(); i++) {
            JSONObject airconJson = airconArray.getJSONObject(i);

            try {
                // aircon already exists, let the settings be updated
                if (aircons.contains(airconJson.get("AirConID").toString())) {

                    // code for updating aircon units settings via jsonobject so you can update
                    // commands in one go rather than individual mqtt topics.
                    // this will be useful if we expand the bridge to take attributeVals from sources other
                    // than mqtt.

                    AirCon aircon = aircons.get(airconJson.get("AirConID").toString());

                } else {

                    // make a new aircon element add it to main aircon list and add to MQTT bridge
                    // and settings file.
                    // need a thread safe way of adding new aircon units

                    AirCon newAircon = new AirCon();
                    newAircon.sethostname(airconJson.getString("hostname"));
                    newAircon.setport(airconJson.getString("port"));
                    newAircon.setDeviceID(airconJson.getString("deviceID"));
                    newAircon.setOperatorID(airconJson.getString("operatorID"));
                    aircons.put(airconJson.getString("deviceID"), newAircon);
                    // to ensure mqttbrdige listens for newsends.
                    mqttbridge.addTopics(newAircon);
                    // need to make sure mqtt knows to listen to new topics

                    // write the new aircon unit to the settings file so it persists on next boot.

                }

            } catch (Exception e) {

                System.out.println(e.toString());
            }

            AirCon airconUnit = new AirCon();

            /*
             * // Assuming the AirconUnit class has appropriate setter methods
             * airconUnit.setTemperature(airconJson.getDouble("temperature"));
             * airconUnit.setMode(airconJson.getString("mode"));
             * airconUnit.setFanSpeed(airconJson.getInt("fanSpeed"));
             * // Add more setters as needed
             */
            // airconUnits.add(airconUnit);
        }

    }

    public static List<String> getAirConIDs(List<AirCon> aircons) {

        List<String> output = new ArrayList<String>();

        try {
            for (int i = 0; i < aircons.size(); i++) {

                output.add(aircons.get(i).getAirConID());

            }
        } catch (Exception e) {

            System.out.println("Malformed aircon unit: " + e.toString());
        }

        return output;
    }

    public static boolean jsonUpdate(AirCon aircon, String attribute, String attributeVal) {

        Boolean output = false;

        switch (attribute) {
            case "hostname":
                output = aircon.sethostname(attributeVal);
                break;
            case "port":
                output = aircon.setport(attributeVal);
                break;
            case "DeviceID":
                output = aircon.setDeviceID(attributeVal);
                break;
            case "AirConID":
                output = aircon.setAirConID(attributeVal);
                break;
            // Add more cases as needed
            case "errorCode":
                output = aircon.setErrorCode(attributeVal);
                break;

            default:
                System.out.println("Unknown topic: " + attribute);
                break;

        }

        return output;

    }


    public static boolean jsonUpdate(AirCon aircon, String attribute, float attributeVal) {

        Boolean output = false;

        switch (attribute) {
            case "PresetTemp":
                output = aircon.setPresetTemp(attributeVal);
                break;
            // Add more cases as needed
            case "indoorTemp":
                output = aircon.setIndoorTemp(attributeVal);
                break;
            // Add more cases as needed
            case "outdoorTemp":
                output = aircon.setOutdoorTemp(attributeVal);
                break;
            // Add more cases as needed
            case "electric":
                output = aircon.setElectric(attributeVal);
                break;
            // Add more cases as needed

            default:
                System.out.println("Unknown topic: " + attribute);
                break;
                

        }

        return output;

    }



    public static boolean jsonUpdate(AirCon aircon, String attribute, int attributeVal) {

        Boolean output = false;

        switch (attribute) {
            case "airFlow":
                output = aircon.setAirFlow(attributeVal);
                break;
            case "windDirectionUD":
                output = aircon.setWindDirectionUD(attributeVal);
                break;
            // Add more cases as needed
            case "windDirectionLR":
                output = aircon.setWindDirectionLR(attributeVal);
                break;
            // Add more cases as needed
            case "operationMode":
                output = aircon.setOperationMode(attributeVal);
                break;
            // Add more cases as needed

            default:
                System.out.println("Unknown topic: " + attribute);
                break;
        }

        return output;

    }





    public static boolean jsonUpdate(AirCon aircon, String attribute, Boolean attributeVal) {

        Boolean output = false;

        switch (attribute) {
            case "status":
                output = aircon.setstatus(attributeVal);
                break;
            case "entrust":
                output = aircon.setEntrust(attributeVal);
                break;
            case "vacant":
                output = aircon.setVacant(attributeVal);
                break;
            case "coolHotJudge":
                output = aircon.setCoolHotJudge(attributeVal);
                break;
            case "selfCleanOperation":
                output = aircon.setSelfCleanOperation(attributeVal);
                break;
            case "selfCleanReset":
                output = aircon.setSelfCleanReset(attributeVal);
                break;
            case "operation":
                output = aircon.setOperation(attributeVal);
                break;

            // Add more cases as needed
            default:
                System.out.println("Unknown topic");
                break;
        }
        return output;

    }




}
