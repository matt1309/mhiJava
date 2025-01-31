package com.thunderstorm.mhi;

import org.eclipse.paho.client.mqttv3.MqttMessage;

public class topicMessage {


        private String topic;
        private MqttMessage message;
    
        // Constructor
        public topicMessage(String topic, MqttMessage message) {
            this.topic = topic;
            this.message = message;
        }
    
        // Getters and Setters
        public String getTopic() {
            return topic;
        }
    
        public void setTopic(String topic) {
            this.topic = topic;
        }
    
        public MqttMessage getMessage() {
            return message;
        }
    
        public void setMessage(MqttMessage message) {
            this.message = message;
        }
    
        @Override
        public String toString() {
            return "MessageTopicPair{" +
                    "topic='" + topic + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
    


