package com.example.myapplication;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.UUID;

public class MQTTService {

    final String serverURI ="tcp://io.adafruit.com:1883";
    final String clientID ="asdasdsass";
//    final String subscriptionTopic ="duyctin2000/feeds/gas-detection";
    final String username_BBC = "CSE_BBC";
    final String username_BBC1 = "CSE_BBC1";
    final String password_BBC  = "aio_YWqQ75LLnzE66cGrbMWNhCka1Xhb";
    final String password_BBC1 = "aio_byWm36bA6XUDSqPfCfVboXjt3Uf1";


//    final String username = "_MyStic_";
//    final String password = "aio_mIEU65n3PYMldvC0mMJ8EpSDvc86";


    final String gasDetectionTopic = "CSE_BBC1/feeds/bk-iot-gas";
    final String turnOnFanTopic = "CSE_BBC/feeds/bk-iot-drv";
    final String buzzerTopic = "CSE_BBC/feeds/bk-iot-speaker";




//    final String gasDetectionTopic = "_MyStic_/feeds/gas-tracker";
//    final String DRVTopic = "_MyStic_/feeds/turn-on-fan";
//    final String buzzerTopic = "CSE_BBC/feeds/bk-iot-speaker";


    public MqttAndroidClient mqttAndroidClient;

    public MQTTService(Context context, String username, String password) {
        mqttAndroidClient = new MqttAndroidClient(context, serverURI, clientID);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.w("mqtt",serverURI);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.w("Lost connection", serverURI);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.w("mqtt", message.toString());

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        connect(username, password);
//        connect(username, password,buzzerTopic);
//        connect(username_BBC1, password_BBC1);

        //-----------------------------------------------------------------
//        subscriptionTopic(gasDetectionTopic);
//        subscriptionTopic(turnOnFanTopic);


    }

    public void setCallback(MqttCallbackExtended callback) {
        mqttAndroidClient.setCallback(callback);
    }


    private void connect(String username, String password) {
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());

        try{
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
//                    subscriptionTopic(subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("mqtt", "Fail to connect" + serverURI + exception.toString());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


    }


    public void subscriptionTopic(String subscriptionTopic) {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.w("mqtt", "Subscribed!" + subscriptionTopic);

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.w("mqtt", "Subscribed Fail!");
                }
            });
        } catch (MqttException ex) {
            System.out.println("Exception subscribing");
            ex.printStackTrace();
        }
    }

}
