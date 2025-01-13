package com.thunderstorm.mhi;

import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.*;

public class MqttAirConBridge {

    private static String url;
    private static String clientID = "AirConClient";
    private static HashMap<String, String> topics;
    private static HashMap<String, String> boolTopics;
    private static HashMap<String, String> stringTopics;
    private static HashMap<String, String> floatTopics;

    private static final int interval = 5000; // 5 seconds

    private MqttClient client;
    private AirCon airCon;

    public MqttAirConBridge(AirCon airCon, String url, String clientID) throws MqttException {
        this.airCon = airCon;
        this.url = url;
        this.clientID = clientID;
        client = new MqttClient(url, clientID);
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection lost: " + cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (floatTopics.containsKey(topic) && (floatTopics.get(topic) != null)) {

                    float val = Float.parseFloat(new String(message.getPayload()));
                    handleTopicFloat(airCon, topic, val);
                    System.out.println("Temperature updated from MQTT: " + val);
                }

                if (stringTopics.containsKey(topic) && (stringTopics.get(topic) != null)) {

                    String val = new String(message.getPayload());
                    handleTopicString(airCon, topic, val);
                    System.out.println("Temperature updated from MQTT: " + val);
                }

                if (boolTopics.containsKey(topic) && (boolTopics.get(topic) != null)) {

                    Boolean val = Boolean.parseBoolean(new String(message.getPayload()));
                    handleTopicBool(airCon, topic, val);
                    System.out.println("Updated topic from MQTT: " + topic + " with value:" + val);
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // No action needed
            }
        });
        client.connect();

        for (String key : floatTopics.keySet()) {
        
        client.subscribe(key);
        }

        for (String key : boolTopics.keySet()) {
        
            client.subscribe(key);
            }
            for (String key : stringTopics.keySet()) {
        
                client.subscribe(key);
                }
    }

    public void startPublishing(AirCon aircon) {
        new Thread(() -> {
            while (true) {
                try {

                    
                   // Publish each data point directly in the if conditions
        if (aircon.gethostname() != null && !aircon.gethostname().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.gethostname().getBytes());
            message.setQos(1); // QoS 1 ensures the message is delivered at least once
            client.publish("device/hostname", message);
        }
        if (aircon.getport() != null && !aircon.getport().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.getport().getBytes());
            message.setQos(1);
            client.publish("device/port", message);
        }
        if (aircon.getDeviceID() != null && !aircon.getDeviceID().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.getDeviceID().getBytes());
            message.setQos(1);
            client.publish("device/deviceID", message);
        }
        if (aircon.getOperatorID() != null && !aircon.getOperatorID().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.getOperatorID().getBytes());
            message.setQos(1);
            client.publish("device/operatorID", message);
        }
        if (aircon.getAirConID() != null && !aircon.getAirConID().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.getAirConID().getBytes());
            message.setQos(1);
            client.publish("device/airConID", message);
        }
        if (aircon.getstatus()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getstatus()).getBytes());
            message.setQos(1);
            client.publish("device/status", message);
        }
        if (aircon.getfirmware() != null && !aircon.getfirmware().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.getfirmware().getBytes());
            message.setQos(1);
            client.publish("device/firmware", message);
        }
        if (aircon.getconnectedAccounts() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getconnectedAccounts()).getBytes());
            message.setQos(1);
            client.publish("device/connectedAccounts", message);
        }
        if (aircon.getOutdoorTemperature()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getOutdoorTemperature()).getBytes());
            message.setQos(1);
            client.publish("device/outdoorTemperature", message);
        }
        if (aircon.getOperation() != null) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getOperation()).getBytes());
            message.setQos(1);
            client.publish("device/operation", message);
        }
        if (aircon.getOperationMode() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getOperationMode()).getBytes());
            message.setQos(1);
            client.publish("device/operationMode", message);
        }
        if (aircon.getAirFlow() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getAirFlow()).getBytes());
            message.setQos(1);
            client.publish("device/airFlow", message);
        }
        if (aircon.getWindDirectionUD() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getWindDirectionUD()).getBytes());
            message.setQos(1);
            client.publish("device/windDirectionUD", message);
        }
        if (aircon.getWindDirectionLR() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getWindDirectionLR()).getBytes());
            message.setQos(1);
            client.publish("device/windDirectionLR", message);
        }
        if (aircon.getPresetTemp() != 0.0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getPresetTemp()).getBytes());
            message.setQos(1);
            client.publish("device/presetTemp", message);
        }
        if (aircon.getEntrust()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getEntrust()).getBytes());
            message.setQos(1);
            client.publish("device/entrust", message);
        }
        if (aircon.getModelNr() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getModelNr()).getBytes());
            message.setQos(1);
            client.publish("device/modelNr", message);
        }
        if (aircon.getVacant()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getVacant()).getBytes());
            message.setQos(1);
            client.publish("device/vacant", message);
        }
        if (aircon.getCoolHotJudge()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getCoolHotJudge()).getBytes());
            message.setQos(1);
            client.publish("device/coolHotJudge", message);
        }
        if (aircon.getIndoorTemp() != 0.0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getIndoorTemp()).getBytes());
            message.setQos(1);
            client.publish("device/indoorTemp", message);
        }
        if (aircon.getOutdoorTemp() != 0.0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getOutdoorTemp()).getBytes());
            message.setQos(1);
            client.publish("device/outdoorTemp", message);
        }
        if (aircon.getElectric() != 0.0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.getElectric()).getBytes());
            message.setQos(1);
            client.publish("device/electric", message);
        }
        if (aircon.getErrorCode() != null && !aircon.getErrorCode().isEmpty()) {
            MqttMessage message = new MqttMessage(aircon.getErrorCode().getBytes());
            message.setQos(1);
            client.publish("device/errorCode", message);
        }
        if (aircon.isSelfCleanOperation()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.isSelfCleanOperation()).getBytes());
            message.setQos(1);
            client.publish("device/selfCleanOperation", message);
        }
        if (aircon.isSelfCleanReset()) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.isSelfCleanReset()).getBytes());
            message.setQos(1);
            client.publish("device/selfCleanReset", message);
        }
        if (aircon.isnextRequestAfter() != null) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.isnextRequestAfter()).getBytes());
            message.setQos(1);
            client.publish("device/nextRequestAfter", message);
        }
        if (aircon.isminrefreshRate() != 0) {
            MqttMessage message = new MqttMessage(String.valueOf(aircon.isminrefreshRate()).getBytes());
            message.setQos(1);
            client.publish("device/minRefreshRate", message);
        }






                    Thread.sleep(interval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void handleTopicFloat(AirCon aircon, String topic, float input) {

        String topicClean = topic.substring(10); // change depending on how long the intial ids are in topics.

        switch (topicClean) {
            case "PresetTemp":
                aircon.setPresetTemp(input);
                break;
            // Add more cases as needed
            default:
                System.out.println("Unknown topic");
                break;
        }
    }

    public static void handleTopicString(AirCon aircon, String topic, String input) {

            String topicClean = topic.substring(10); //change depending on how long the intial ids are in topics.

/*
 * private String hostname;
    private String port = "5443";
    private String DeviceID;
    private String OperatorID;
    private String AirConID;

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
                default:
                    System.out.println("Unknown topic");
                    break;
            }
        }

    public static void handleTopicBool(AirCon aircon, String topic, Boolean input) {

        String topicClean = topic.substring(10); // change depending on how long the intial ids are in topics.

        switch (topicClean) {
            case "status":
                aircon.setstatus(input);
                break;

            // Add more cases as needed
            default:
                System.out.println("Unknown topic");
                break;
        }
    }

}
