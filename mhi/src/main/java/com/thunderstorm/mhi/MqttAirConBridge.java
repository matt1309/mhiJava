package com.thunderstorm.mhi;

import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.*;

public class MqttAirConBridge {

    private static final String url;
    private static final String clientID = "AirConClient";
    private static final HashMap<String, String> topics;
    private static final HashMap<String, String> boolTopics;
    private static final HashMap<String, String> stringTopics;
    private static final HashMap<String, String> floatTopics;

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

    public void startPublishing() {
        new Thread(() -> {
            while (true) {
                try {
                    double temp = airCon.getTemp();
                    MqttMessage message = new MqttMessage(Double.toString(temp).getBytes());
                    client.publish(TEMP_TOPIC, message);
                    System.out.println("Temperature sent to MQTT: " + temp);
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
            case "hi":
                hi();
                break;
            // Add more cases as needed
            default:
                System.out.println("Unknown topic");
                break;
        }
    }

}
