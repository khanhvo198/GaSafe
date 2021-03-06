package com.example.myapplication;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private static final int REQUEST_CALL = 1;
    final String serverURI ="tcp://io.adafruit.com:1883";
    final String clientID ="asdasdsass";
//    final String username_BBC = "CSE_BBC";
//    final String username_BBC1 = "CSE_BBC1";
//    final String password_BBC  = "";
//    final String password_BBC1 = "";


    final String username_BBC = "_MyStic_";
    final String username_BBC1 = "_MyStic_";
    final String password_BBC  = "aio_xMVA72t0iK51AyN6WhowlVlAbKKL";
    final String password_BBC1 = "aio_xMVA72t0iK51AyN6WhowlVlAbKKL";



    final String gasDetectionTopic = "_MyStic_/feeds/gas-tracker";
    final String turnOnFanTopic = "_MyStic_/feeds/turn-on-fan";
    final String tempHumidTopic = "_MyStic_/feeds/temp-humid-tracker";
    final String turnOnBuzzerTopic = "_MyStic_/feeds/turn-on-buzzer";

//    final String gasDetectionTopic = "CSE_BBC1/feeds/bk-iot-gas";
//    final String turnOnFanTopic = "CSE_BBC/feeds/bk-iot-drv";
//    final String tempHumidTopic = "CSE_BBC/feeds/bk-iot-temp-humid";
//    final String turnOnBuzzerTopic = "CSE_BBC/feeds/bk-iot-speaker";










    MQTTService mqttServiceBBC, mqttServiceBBC1;
    View root;
    Button btnFanController, btnCallFireFighter, btnContactSupport, btnLogout;
    LineChart chart;



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_fragment, container, false);
        btnFanController = root.findViewById(R.id.btnFanController);
        btnCallFireFighter = root.findViewById(R.id.btnCallFireFighter);
        btnContactSupport = root.findViewById(R.id.btnContactSupport);

        chart = (LineChart) root.findViewById(R.id.chart);

        LineData data = new LineData();
//        chartData.addEntry(new Entry(set.getEntryCount(), data), 0);
        ILineDataSet set = createSet();
        data.addDataSet(set);
        data.addEntry(new Entry(set.getEntryCount(),25), 0);
        data.addEntry(new Entry(set.getEntryCount(),26),0);
        data.addEntry(new Entry(set.getEntryCount(),24),0);

        chart.setData(data);

//        myValues.add(new Entry(0,1.2f));
//        myValues.add(new Entry(1, 23.1f));
//        myValues.add(new Entry(2, 12.4f));
//        myValues.add(new Entry(3,1.2f));
//        myValues.add(new Entry(4,9f));
//        myValues.add(new Entry(5,12f));
//        myValues.add(new Entry(6,11.2f));
//        myValues.add(new Entry(7,15.6f));
//        myValues.add(new Entry(8,22.4f));
//
//        ArrayList<Entry> myValues = new ArrayList<Entry>();
//        LineDataSet dataSet = new LineDataSet(myValues, "Simple temperature chart"); // add entries to dataset
//        LineData lineData = new LineData(dataSet);
//
//
//        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        dataSet.setDrawFilled(true);
////        dataSet.setFillColor(ContextCompat.getColor(context,R.color.green));
//
//        chart.setData(lineData);
//        chart.invalidate();


        //----------------------------------------------------------------------//

        final TextView txtHomeMain = (TextView)root.findViewById(R.id.txtHomeMain);
        txtHomeMain.setText("Welcome to GaSafe");

        mqttServiceBBC = new MQTTService(getContext(), username_BBC, password_BBC);
        mqttServiceBBC1 = new MQTTService(getContext(), username_BBC1, password_BBC1);


        mqttServiceBBC.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqttBBC", "Connect Complete");
                try {
                    mqttServiceBBC.mqttAndroidClient.subscribe(turnOnFanTopic,0);
                    mqttServiceBBC.mqttAndroidClient.subscribe(tempHumidTopic, 0);
                    mqttServiceBBC.mqttAndroidClient.subscribe(turnOnBuzzerTopic,0);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                JSONObject data = new JSONObject(new String(message.getPayload()));

                switch (topic) {
                    case tempHumidTopic: {
                        Log.d("Temp-humid data:" , (String) data.get("data"));
                        int temperature = Integer.parseInt(data.get("data").toString().split("-")[0]);
                        processTempHumidTracker(temperature);

                        break;

                    }

                    default: {
                        break;
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });



        mqttServiceBBC1.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqttBBC1", "Connect Complete");
                try {
                    mqttServiceBBC1.mqttAndroidClient.subscribe(gasDetectionTopic,0);
                } catch (MqttException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                JSONObject data = new JSONObject(new String(message.getPayload()));

                switch (topic) {
                    case gasDetectionTopic: {
                        processGasTracker((String) data.get("data"));
                        break;
                    }

                    default: {
                        break;
                    }
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });





        btnFanController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnFanController.getText().toString().equals("TURN ON")) {
//                    txtHomeMain.setText("Fan is ON");
                    btnFanController.setText("TURN OFF");
                    sendDataMQTT(mqttServiceBBC, createTurnOnFanJSON("200").toString(), turnOnFanTopic);
                }
                else {
//                    txtHomeMain.setText("Fan is OFF");
                    btnFanController.setText("TURN ON");
                    sendDataMQTT(mqttServiceBBC, createTurnOnFanJSON("0").toString(), turnOnFanTopic);
                }
            }
        });

        btnCallFireFighter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFireFighter();
            }
        });

        btnContactSupport.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSupport();
            }
        }));

        return root;

    }



    private void processTempHumidTracker(int data) {
        LineData chartData = chart.getData();

        if(chartData != null) {
            ILineDataSet set = chartData.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                chartData.addDataSet(set);
            }

            chartData.addEntry(new Entry(set.getEntryCount(), data), 0);
            chartData.notifyDataChanged();
            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(120);
            chart.moveViewToX(set.getEntryCount());
            chart.setData(chartData);
            chart.invalidate();

        }

    }


    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Bi???u ????? nhi???t ????? theo th???i gian");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);


        return set;

    }



    private void sendDataMQTT( MQTTService mqttService, @org.jetbrains.annotations.NotNull String data, String topic) {
        MqttMessage message = new MqttMessage();
        message.setId(1234);
        message.setQos(0);
        message.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));



        message.setPayload(b);
        Log.d("ABC", "Publish" + message + topic);

        try {
            mqttService.mqttAndroidClient.publish(topic, message);

        } catch (MqttException e) {
            Log.w("mqtt" , "cannot send message");
        }

    }


    private JSONObject createJSON(String id, String name, String data, String unit) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("name",name);
            json.put("data",data);
            json.put("unit",unit);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    private JSONObject createTurnOnFanJSON(String data) {
        return createJSON("10", "DRV_PWM", data, "");
    }

    private JSONObject createTurnOnBuzzerJSON(String data) {
        return createJSON("2","SPEAKER", data, "");
    }



    private void processGasTracker(String data) {
        final TextView txtHomeMain = (TextView)root.findViewById(R.id.txtHomeMain);

        if(Integer.parseInt(data)  == 0) {
            btnFanController.setText("TURN ON");
            txtHomeMain.setTextColor(root.getResources().getColor(R.color.colorGreen));
            txtHomeMain.setText("N???ng ????? b??nh th?????ng");
//            sendDataMQTT(createTurnOnFanJSON("0").toString(), turnOnFanTopic);
            sendDataMQTT(mqttServiceBBC,createTurnOnBuzzerJSON("0").toString(),turnOnBuzzerTopic);
        }
        else {
            btnFanController.setText("TURN OFF");
            txtHomeMain.setTextColor(root.getResources().getColor(R.color.colorRed));
            txtHomeMain.setText("N???ng ????? v?????t ng?????ng");
            sendDataMQTT(mqttServiceBBC, createTurnOnFanJSON("200").toString(), turnOnFanTopic);
            sendDataMQTT(mqttServiceBBC, createTurnOnBuzzerJSON("1000").toString(),turnOnBuzzerTopic);
        }
    }


    private void makeCall(String number) {
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{Manifest.permission.CALL_PHONE},
                        REQUEST_CALL
                );
            } else {
                String dial = "tel:" + number;
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(dial));
                startActivity(intent);
            }
        }
    }

    private void callFireFighter() {
        String phoneNumFireFighter = "114";
        makeCall(phoneNumFireFighter);
    }

    private void callSupport() {
        String phoneNumSupport = "*101#";
        makeCall(phoneNumSupport);
    }
}