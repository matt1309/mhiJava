package com.thunderstorm.mhi;

import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.*;

public class MqttAirConBridge {

    private String url;
    private String clientID = "AirConClient";
    private static HashMap<String, String> topics;
    private static HashMap<String, String> boolTopics;
    private static HashMap<String, String> stringTopics;
    private static HashMap<String, String> floatTopics;
    private static HashMap<String, String> intTopics;

    private static final int interval = 5000; // 5 seconds

    private MqttClient client;
    private AirCon airCon;

    public MqttAirConBridge(AirCon airCon, String url, String clientID) throws MqttException {
        this.airCon = airCon;
        this.url = url;
        this.clientID = clientID;
        client = new MqttClient(url, clientID);

        // make sure things aren't null
        String baseTopicRead = "aircon/" + airCon.getAirConID() + "/ReadOnly/";
        String baseTopicWrite = "aircon/" + airCon.getAirConID() + "/ReadWrite/";

        // String topics
        stringTopics.put(baseTopicRead + "AirConID", "AirConID");
        stringTopics.put(baseTopicRead + "hostname", "hostname");
        stringTopics.put(baseTopicRead + "port", "port");
        stringTopics.put(baseTopicRead + "deviceID", "DeviceID");
        stringTopics.put(baseTopicRead + "errorCode", "errorCode");

        // Boolean topics
        boolTopics.put(baseTopicRead + "status", "status");
        boolTopics.put(baseTopicRead + "entrust", "entrust");
        boolTopics.put(baseTopicRead + "vacant", "vacant");
        boolTopics.put(baseTopicRead + "coolHotJudge", "coolHotJudge");
        boolTopics.put(baseTopicWrite + "selfCleanOperation", "selfCleanOperation");
        boolTopics.put(baseTopicWrite + "selfCleanReset", "selfCleanReset");

        // Float topics
        floatTopics.put(baseTopicWrite + "PresetTemp", "PresetTemp");
        floatTopics.put(baseTopicRead + "indoorTemp", "indoorTemp");
        floatTopics.put(baseTopicRead + "outdoorTemp", "outdoorTemp");
        floatTopics.put(baseTopicRead + "electric", "electric");

        //int Topics
    intTopics.put(baseTopicWrite + "airFlow", "airFlow");
    intTopics.put(baseTopicWrite + "windDirectionUD", "windDirectionUD");
    intTopics.put(baseTopicWrite + "windDirectionLR", "windDirectionLR");

    // Operation topics
    boolTopics.put(baseTopicWrite + "operation", "operation");
    floatTopics.put(baseTopicWrite + "operationMode", "operationMode");

        // to add the rest of the topics. and add a function to retrospectively add
        // units after initialisation for library use more than anything.
        // change the mqtt implementation to be callback functions so that users can add
        // their own functions to make it more generic.

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
                    System.out.println("Float values received from: " + topic + " with value: " + val);
                }

                if (stringTopics.containsKey(topic) && (stringTopics.get(topic) != null)) {

                    String val = new String(message.getPayload());
                    handleTopicString(airCon, topic, val);
                    System.out.println("String values received from: " + topic + " with value: " + val);

                }

                if (boolTopics.containsKey(topic) && (boolTopics.get(topic) != null)) {

                    Boolean val = Boolean.parseBoolean(new String(message.getPayload()));
                    handleTopicBool(airCon, topic, val);
                    System.out.println("Boolean values received from: " + topic + " with value: " + val);
                }

                if (intTopics.containsKey(topic) && (boolTopics.get(topic) != null)) {

                    int val = Integer.parseInt(new String(message.getPayload()));
                    handleTopicInt(airCon, topic, val);
                    System.out.println("Boolean values received from: " + topic + " with value: " + val);
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

    public void publishNow(AirCon aircon) {

        try {

            String baseTopicRead = "aircon/" + airCon.getAirConID() + "/ReadOnly/";
            String baseTopicWrite = "aircon/" + airCon.getAirConID() + "/ReadWrite/";
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
                client.publish(baseTopicRead + "/deviceID", message);
            }
            if (aircon.getOperatorID() != null && !aircon.getOperatorID().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getOperatorID().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/operatorID", message);
            }
            if (aircon.getAirConID() != null && !aircon.getAirConID().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getAirConID().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/airConID", message);
            }
            if (aircon.getstatus()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getstatus()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/status", message);
            }
            if (aircon.getfirmware() != null && !aircon.getfirmware().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getfirmware().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/firmware", message);
            }
            if (aircon.getconnectedAccounts() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getconnectedAccounts()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/connectedAccounts", message);
            }
            if (aircon.getOutdoorTemperature()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOutdoorTemperature()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/outdoorTemperature", message);
            }
            if (aircon.getOperation() != null) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOperation()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/operation", message);
            }
            if (aircon.getOperationMode() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOperationMode()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/operationMode", message);
            }
            if (aircon.getAirFlow() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getAirFlow()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/airFlow", message);
            }
            if (aircon.getWindDirectionUD() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getWindDirectionUD()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/windDirectionUD", message);
            }
            if (aircon.getWindDirectionLR() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getWindDirectionLR()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/windDirectionLR", message);
            }
            if (aircon.getPresetTemp() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getPresetTemp()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/presetTemp", message);
            }
            if (aircon.getEntrust()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getEntrust()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/entrust", message);
            }
            if (aircon.getModelNr() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getModelNr()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/modelNr", message);
            }
            if (aircon.getVacant()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getVacant()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/vacant", message);
            }
            if (aircon.getCoolHotJudge()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getCoolHotJudge()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/coolHotJudge", message);
            }
            if (aircon.getIndoorTemp() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getIndoorTemp()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/indoorTemp", message);
            }
            if (aircon.getOutdoorTemp() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getOutdoorTemp()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/outdoorTemp", message);
            }
            if (aircon.getElectric() != 0.0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.getElectric()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/electric", message);
            }
            if (aircon.getErrorCode() != null && !aircon.getErrorCode().isEmpty()) {
                MqttMessage message = new MqttMessage(aircon.getErrorCode().getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/errorCode", message);
            }
            if (aircon.isSelfCleanOperation()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isSelfCleanOperation()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/selfCleanOperation", message);
            }
            if (aircon.isSelfCleanReset()) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isSelfCleanReset()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/selfCleanReset", message);
            }
            if (aircon.isnextRequestAfter() != null) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isnextRequestAfter()).getBytes());
                message.setQos(1);
                client.publish(baseTopicRead + "/nextRequestAfter", message);
            }
            if (aircon.isminrefreshRate() != 0) {
                MqttMessage message = new MqttMessage(String.valueOf(aircon.isminrefreshRate()).getBytes());
                message.setQos(1);
                client.publish(baseTopicWrite + "/minRefreshRate", message);
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
                System.out.println("Unknown topic");
                break;
        }
    }


    public static void handleTopicInt(AirCon aircon, String topic, int input) {

        String topicClean = topic.substring(10); // change depending on how long the intial ids are in topics.

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
                System.out.println("Unknown topic");
                break;
        }
    }





    public static void handleTopicString(AirCon aircon, String topic, String input) {

        String topicClean = topic.substring(10); // change depending on how long the intial ids are in topics.

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
