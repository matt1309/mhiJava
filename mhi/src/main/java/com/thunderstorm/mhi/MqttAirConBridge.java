package com.thunderstorm.mhi;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.*;

public class MqttAirConBridge {

    private String clientID = "AirConClient";
    // private static HashMap<String, String> topics;
    private static ConcurrentHashMap<String, List<String>> boolTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, List<String>> stringTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, List<String>> floatTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, List<String>> intTopics = new ConcurrentHashMap<String, List<String>>();
    private static ConcurrentHashMap<String, AirCon> airCons = new ConcurrentHashMap<String, AirCon>();

    private static int interval = 5000; // 5 seconds

    private MqttClient client; //to make this thread safe with locks
    //private AirCon airCon;

    public MqttAirConBridge(List<AirCon> airConList, String url, int interval, String username, String password)
            throws MqttException {
      //  this.airCon = airCon;

     // airCons.put(airCon.getAirConID(), airCon);
      
        //this.clientID = clientID;
        this.interval = interval;

        client = new MqttClient("tcp://" + url, MqttClient.generateClientId());

for(AirCon aircon : airConList){

airCons.put(aircon.getAirConID(), aircon);

}



        MqttConnectOptions options = new MqttConnectOptions();
        if (!username.isEmpty() && !password.isEmpty()) {
            options.setUserName(username);
            options.setPassword(password.toCharArray());
        }

        //airCons.put(airCon.getAirConID(), airCon);
        // make sure things aren't null
      //  
/*
        String baseTopicRead = "aircon/" + airCon.getAirConID() + "/ReadOnly/";
        String baseTopicWrite = "aircon/" + airCon.getAirConID() + "/ReadWrite/";

        // String topics
        stringTopics.put(baseTopicWrite + "AirConID", Arrays.asList(airCon.getAirConID(),"AirConID"));
        stringTopics.put(baseTopicWrite + "hostname", Arrays.asList(airCon.getAirConID(),"hostname"));
        stringTopics.put(baseTopicWrite + "port", Arrays.asList(airCon.getAirConID(),"port"));
        stringTopics.put(baseTopicWrite + "deviceID", Arrays.asList(airCon.getAirConID(),"DeviceID"));
        stringTopics.put(baseTopicWrite + "errorCode", Arrays.asList(airCon.getAirConID(),"errorCode"));

        // Boolean topics
        boolTopics.put(baseTopicWrite + "status", Arrays.asList(airCon.getAirConID(),"status"));
        boolTopics.put(baseTopicWrite + "entrust", Arrays.asList(airCon.getAirConID(),"entrust"));
        boolTopics.put(baseTopicWrite + "vacant", Arrays.asList(airCon.getAirConID(),"vacant"));
        boolTopics.put(baseTopicWrite + "coolHotJudge", Arrays.asList(airCon.getAirConID(),"coolHotJudge"));
        boolTopics.put(baseTopicWrite + "selfCleanOperation", Arrays.asList(airCon.getAirConID(),"selfCleanOperation"));
        boolTopics.put(baseTopicWrite + "selfCleanReset", Arrays.asList(airCon.getAirConID(),"selfCleanReset"));

        // Float topics
        floatTopics.put(baseTopicWrite + "presetTemp", Arrays.asList(airCon.getAirConID(),"PresetTemp"));
        floatTopics.put(baseTopicWrite + "indoorTemp", Arrays.asList(airCon.getAirConID(),"indoorTemp"));
        floatTopics.put(baseTopicWrite + "outdoorTemp", Arrays.asList(airCon.getAirConID(),"outdoorTemp"));
        floatTopics.put(baseTopicWrite + "electric", Arrays.asList(airCon.getAirConID(),"electric"));

        // int Topics
        intTopics.put(baseTopicWrite + "airFlow", Arrays.asList(airCon.getAirConID(),"airFlow"));
        intTopics.put(baseTopicWrite + "windDirectionUD", Arrays.asList(airCon.getAirConID(),"windDirectionUD"));
        intTopics.put(baseTopicWrite + "windDirectionLR", Arrays.asList(airCon.getAirConID(),"windDirectionLR"));

        // Operation topics
        boolTopics.put(baseTopicWrite + "operation", Arrays.asList(airCon.getAirConID(),"operation"));
        floatTopics.put(baseTopicWrite + "operationMode", Arrays.asList(airCon.getAirConID(),"operationMode"));

        // to add the rest of the topics. and add a function to retrospectively add
        // units after initialisation for library use more than anything.
        // change the mqtt implementation to be callback functions so that users can add
        // their own functions to make it more generic.
        */

        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                Boolean changesMade = false;
                Set<String> airConsChanged = new HashSet<String>();

                if (floatTopics.containsKey(topic) && (floatTopics.get(topic) != null)) {

                    float val = Float.parseFloat(new String(message.getPayload()));
                    handleTopicFloat(topic, val);
                    System.out.println("Float values received from: " + topic + " with value: " + val);
                    changesMade = true;
                    airConsChanged.add((floatTopics.get(topic)).get(0));
                    
                }

                if (stringTopics.containsKey(topic) && (stringTopics.get(topic) != null)) {

                    String val = new String(message.getPayload());
                    handleTopicString(topic, val);
                    System.out.println("String value received from: " + topic + " with value: " + val);
                    changesMade = true;
                    airConsChanged.add((floatTopics.get(topic)).get(0));

                }

                if (boolTopics.containsKey(topic) && (boolTopics.get(topic) != null)) {

                    Boolean val = Boolean.parseBoolean(new String(message.getPayload()));
                    handleTopicBool(topic, val);
                    System.out.println("Boolean values received from: " + topic + " with value: " + val);
                    changesMade = true;
                    airConsChanged.add((floatTopics.get(topic)).get(0));
                }

                if (intTopics.containsKey(topic) && (boolTopics.get(topic) != null)) {

                    int val = Integer.parseInt(new String(message.getPayload()));
                    handleTopicInt(topic, val);
                    System.out.println("Int values received from: " + topic + " with value: " + val);
                    changesMade = true;
                    airConsChanged.add((floatTopics.get(topic)).get(0));
                }

                if (changesMade) {


            updateAirCon(airConsChanged);
                    

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // No action needed
            }
        });
        client.connect(options);

        
        airCons.forEach((key, value) -> addTopics(value));
        

        /*

        for (String key : floatTopics.keySet()) {

            client.subscribe(key);
        }

        for (String key : boolTopics.keySet()) {

            client.subscribe(key);
        }
        for (String key : stringTopics.keySet()) {

            client.subscribe(key);
        }
            */
    }



    public void updateAirCon(Set<String> airConIDs) {


        for(String airconID : airConIDs){

        AirCon aircon = airCons.get(airconID);
        String command = aircon.parser.toBase64();

        try {
            // sending command to the aircon unit itself
            aircon.sendAircoCommand(command);
            //airCon.printDeviceData();
            publishNow(aircon);
        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }





    }

    public void addTopics(AirCon airCon) {



        //airCons.put(airCon.getAirConID(), airCon);
        // make sure things aren't null
      //  String baseTopicRead = "aircon/" + airCon.getAirConID() + "/ReadOnly/";
        String baseTopicWrite = "aircon/" + airCon.getAirConID() + "/ReadWrite/";
        
        HashMap<String,List<String>> newstringTopics = new HashMap<String, List<String>>();
        HashMap<String,List<String>> newboolTopics = new HashMap<String, List<String>>();
        HashMap<String,List<String>> newfloatTopics = new HashMap<String, List<String>>();
        HashMap<String,List<String>> newintTopics = new HashMap<String, List<String>>();


        // String topics
        newstringTopics.put(baseTopicWrite + "AirConID", Arrays.asList(airCon.getAirConID(),"AirConID"));
        newstringTopics.put(baseTopicWrite + "hostname", Arrays.asList(airCon.getAirConID(),"hostname"));
        newstringTopics.put(baseTopicWrite + "port", Arrays.asList(airCon.getAirConID(),"port"));
        newstringTopics.put(baseTopicWrite + "deviceID", Arrays.asList(airCon.getAirConID(),"DeviceID"));
        newstringTopics.put(baseTopicWrite + "errorCode", Arrays.asList(airCon.getAirConID(),"errorCode"));


        // Boolean topics
        newboolTopics.put(baseTopicWrite + "status", Arrays.asList(airCon.getAirConID(),"status"));
        newboolTopics.put(baseTopicWrite + "entrust", Arrays.asList(airCon.getAirConID(),"entrust"));
        newboolTopics.put(baseTopicWrite + "vacant", Arrays.asList(airCon.getAirConID(),"vacant"));
        newboolTopics.put(baseTopicWrite + "coolHotJudge", Arrays.asList(airCon.getAirConID(),"coolHotJudge"));
        newboolTopics.put(baseTopicWrite + "selfCleanOperation", Arrays.asList(airCon.getAirConID(),"selfCleanOperation"));
        newboolTopics.put(baseTopicWrite + "selfCleanReset", Arrays.asList(airCon.getAirConID(),"selfCleanReset"));

        // Float topics
        newfloatTopics.put(baseTopicWrite + "presetTemp", Arrays.asList(airCon.getAirConID(),"PresetTemp"));
        newfloatTopics.put(baseTopicWrite + "indoorTemp", Arrays.asList(airCon.getAirConID(),"indoorTemp"));
        newfloatTopics.put(baseTopicWrite + "outdoorTemp", Arrays.asList(airCon.getAirConID(),"outdoorTemp"));
        newfloatTopics.put(baseTopicWrite + "electric", Arrays.asList(airCon.getAirConID(),"electric"));

        // int Topics
        newintTopics.put(baseTopicWrite + "airFlow", Arrays.asList(airCon.getAirConID(),"airFlow"));
        newintTopics.put(baseTopicWrite + "windDirectionUD", Arrays.asList(airCon.getAirConID(),"windDirectionUD"));
        newintTopics.put(baseTopicWrite + "windDirectionLR", Arrays.asList(airCon.getAirConID(),"windDirectionLR"));

        // Operation topics
        newboolTopics.put(baseTopicWrite + "operation", Arrays.asList(airCon.getAirConID(),"operation"));
        newfloatTopics.put(baseTopicWrite + "operationMode", Arrays.asList(airCon.getAirConID(),"operationMode"));

        stringTopics.putAll(newstringTopics);
        floatTopics.putAll(newfloatTopics);
        boolTopics.putAll(newboolTopics);
        intTopics.putAll(newintTopics);


        try{
            for (String key : newfloatTopics.keySet()) {

                client.subscribe(key);
            }
    
            for (String key : newboolTopics.keySet()) {
    
                client.subscribe(key);
            }
            for (String key : newstringTopics.keySet()) {
    
                client.subscribe(key);
            }
            for (String key : newintTopics.keySet()) {
    
                client.subscribe(key);
            }

            
        } catch (Exception e){

            System.out.println("Error: " + e.toString());
        }



    }




    public void publishNow(AirCon aircon) {

        try {

            System.out.println("Publishing data");

            String baseTopicRead = "aircon/" + aircon.getAirConID() + "/ReadOnly/";
            //String baseTopicWrite = "aircon/" + aircon.getAirConID() + "/ReadWrite/";
            // move this to a function so it can be called easily.

            // Publish each data point directly in the if conditions
            if (aircon.gethostname() != null && !aircon.gethostname().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.gethostname().getBytes());
                message.setQos(1); // QoS 1 ensures the message is delivered at least once
                client.publish(baseTopicRead + "hostname", message);
            }
            if (aircon.getport() != null && !aircon.getport().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getport().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "port", message);
            }
            if (aircon.getDeviceID() != null && !aircon.getDeviceID().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getDeviceID().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "deviceID", message);
            }
            if (aircon.getOperatorID() != null && !aircon.getOperatorID().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getOperatorID().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "operatorID", message);
            }
            if (aircon.getAirConID() != null && !aircon.getAirConID().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getAirConID().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "airConID", message);
            }
            if (aircon.getstatus()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getstatus()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "status", message);
            }
            if (aircon.getfirmware() != null && !aircon.getfirmware().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getfirmware().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "firmware", message);
            }
            if (aircon.getconnectedAccounts() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getconnectedAccounts()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "connectedAccounts", message);
            }
            if (aircon.getOutdoorTemperature()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOutdoorTemperature()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "outdoorTemperature", message);
            }
            if (aircon.getOperation() != null) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOperation()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "operation", message);
            }
            if (aircon.getOperationMode() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOperationMode()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "operationMode", message);
            }
            if (aircon.getAirFlow() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getAirFlow()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "airFlow", message);
            }
            if (aircon.getWindDirectionUD() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getWindDirectionUD()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "windDirectionUD", message);
            }
            if (aircon.getWindDirectionLR() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getWindDirectionLR()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "windDirectionLR", message);
            }
            if (aircon.getPresetTemp() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getPresetTemp()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "presetTemp", message);
            }
            if (aircon.getEntrust()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getEntrust()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "entrust", message);
            }
            if (aircon.getModelNr() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getModelNr()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "modelNr", message);
            }
            if (aircon.getVacant()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getVacant()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "vacant", message);
            }
            if (aircon.getCoolHotJudge()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getCoolHotJudge()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "coolHotJudge", message);
            }
            if (aircon.getIndoorTemp() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getIndoorTemp()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "indoorTemp", message);
            }
            if (aircon.getOutdoorTemp() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOutdoorTemp()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "outdoorTemp", message);
            }
            if (aircon.getElectric() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getElectric()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "electric", message);
            }
            if (aircon.getErrorCode() != null && !aircon.getErrorCode().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getErrorCode().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "errorCode", message);
            }
            if (aircon.isSelfCleanOperation()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isSelfCleanOperation()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "selfCleanOperation", message);
            }
            if (aircon.isSelfCleanReset()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isSelfCleanReset()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "selfCleanReset", message);
            }
            if (aircon.isnextRequestAfter() != null) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isnextRequestAfter()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "nextRequestAfter", message);
            }
            if (aircon.isminrefreshRate() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isminrefreshRate()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "minRefreshRate", message);
            }

            // Thread.sleep(interval);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startPublishing(AirCon aircon) {
        new Thread(() -> {
            while (true) {
                try {

                    publishNow(aircon);
                    System.out.println("Sleeping for MQTT: " + interval);

                    Thread.sleep(interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void handleTopicFloat(String topic, float input) {

        String topicClean = (floatTopics.get(topic)).get(1);
        String airconID = (floatTopics.get(topic)).get(0);
        
        AirCon aircon = airCons.get(airconID);
        
        // change depending on how long the intial ids are in topics.

        switch (topicClean) {
            case "PresetTemp":
                aircon.setPresetTemp(input);
                break;
            // Add more cases as needed
            case "indoorTemp":
                aircon.setIndoorTemp(input);
                break;
            // Add more cases as needed
            case "outdoorTemp":
                aircon.setOutdoorTemp(input);
                break;
            // Add more cases as needed
            case "electric":
                aircon.setElectric(input);
                break;
            // Add more cases as needed

            default:
                System.out.println("Unknown topic: " + topicClean);
                break;
        }
    }

    public static void handleTopicInt(String topic, int input) {

        String topicClean = intTopics.get(topic).get(1);
        String airconID = intTopics.get(topic).get(0);
        
       AirCon aircon = airCons.get(airconID); // change depending on how long the intial ids are in topics.

        switch (topicClean) {
            case "airFlow":
                aircon.setAirFlow(input);
                break;
            case "windDirectionUD":
                aircon.setWindDirectionUD(input);
                break;
            // Add more cases as needed
            case "windDirectionLR":
                aircon.setWindDirectionLR(input);
                break;
            // Add more cases as needed

            case "operationMode":
                aircon.setOperationMode(input);
                break;
            // Add more cases as needed
            default:
                System.out.println("Unknown topic: " + topicClean);
                break;
        }
    }

    public static void handleTopicString(String topic, String input) {

        String topicClean = stringTopics.get(topic).get(1);
        String airconID = stringTopics.get(topic).get(0);
        
       AirCon aircon = airCons.get(airconID);
        
        // change depending on how long the intial ids are in topics.

        /*
         * private String hostname;
         * private String port = "5443";
         * private String DeviceID;
         * private String OperatorID;
         * private String AirConID;
         * 
         * 
         */

        switch (topicClean) {
            case "hostname":
                aircon.sethostname(input);
                break;
            case "port":
                aircon.setport(input);
                break;
            case "DeviceID":
                aircon.setDeviceID(input);
                break;
            case "AirConID":
                aircon.setAirConID(input);
                break;
            // Add more cases as needed
            case "errorCode":
                aircon.setErrorCode(input);
                break;

            // Add more cases as needed
            default:
                System.out.println("Unknown topic: " + topicClean);
                break;
        }
    }

    public static void handleTopicBool(String topic, Boolean input) {

               
        String topicClean = boolTopics.get(topic).get(1);
        String airconID = boolTopics.get(topic).get(0);
        
        AirCon aircon = airCons.get(airconID);
        
        // change depending on how long the intial ids are in topics.


        switch (topicClean) {
            case "status":
                aircon.setstatus(input);
                break;
            case "entrust":
                aircon.setEntrust(input);
                break;
            case "vacant":
                aircon.setVacant(input);
                break;
            case "coolHotJudge":
                aircon.setCoolHotJudge(input);
                break;
            case "selfCleanOperation":
                aircon.setSelfCleanOperation(input);
                break;
            case "selfCleanReset":
                aircon.setSelfCleanReset(input);
                break;

            // Add more cases as needed
            default:
                System.out.println("Unknown topic");
                break;
        }
    }

}
