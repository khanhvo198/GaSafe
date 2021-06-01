package com.example.myapplication;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.fragment.app.Fragment;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    final String gasDetectionTopic = "_MyStic_/feeds/gas-tracker";
    final String turnOnFanTopic = "_MyStic_/feeds/turn-on-fan";

//    final String gasDetectionTopic = "CSE_BBC1/feeds/bk-iot-gas";
//    final String turnOnFanTopic = "CSE_BBC/feeds/bk-iot-drv";





    MQTTService mqttService;
    View root;
    Button btnTurnOnClick, btnLogout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_fragment, container, false);
        btnTurnOnClick = root.findViewById(R.id.btnFanController);

        LineChart chart = (LineChart) root.findViewById(R.id.chart);


        ArrayList<Entry> myValues = new ArrayList<Entry>();
        myValues.add(new Entry(0,1.2f));
        myValues.add(new Entry(1, 23.1f));
        myValues.add(new Entry(2, 12.4f));
        myValues.add(new Entry(3,1.2f));
        myValues.add(new Entry(4,9f));
        myValues.add(new Entry(5,12f));
        myValues.add(new Entry(6,11.2f));
        myValues.add(new Entry(7,15.6f));
        myValues.add(new Entry(8,22.4f));


        LineDataSet dataSet = new LineDataSet(myValues, "This is sample chart"); // add entries to dataset

        
        LineData lineData = new LineData(dataSet);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
//        dataSet.setFillColor(ContextCompat.getColor(context,R.color.green));

        chart.setData(lineData);
        chart.invalidate();





        //----------------------------------------------------------------------//

        final TextView txt = (TextView)root.findViewById(R.id.txt);


        mqttService = new MQTTService(getContext());
        mqttService.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqtt", "Connect Complete");
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

        btnTurnOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnTurnOnClick.getText().toString().equals("TURN ON")) {
                    btnTurnOnClick.setText("TURN OFF");
//                    txt.setText("Fan is turn off");
                    sendDataMQTT(createTurnOnFanJSON("1").toString(), turnOnFanTopic);
                }
                else {
                    btnTurnOnClick.setText("TURN ON");
//                    txt.setText("Fan is turn on");
                    sendDataMQTT(createTurnOnFanJSON("0").toString(), turnOnFanTopic);
                }
            }
        });

        return root;

    }


    private void sendDataMQTT(@org.jetbrains.annotations.NotNull String data, String topic) {
        MqttMessage message = new MqttMessage();
        message.setId(1234);
        message.setQos(0);
        message.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));



        message.setPayload(b);
        Log.d("ABC", "Publish" + message);

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
        return createJSON("11", "RELAY", data, "");
    }



    private void processGasTracker(String data) {
        final TextView txt = (TextView)root.findViewById(R.id.txt);

        if(Integer.parseInt(data)  == 0) {
            btnTurnOnClick.setText("TURN OFF");
            txt.setTextColor(root.getResources().getColor(R.color.colorGreen));
            txt.setText("Nồng độ bình thường");
            sendDataMQTT(createTurnOnFanJSON("0").toString(), turnOnFanTopic);
        }
        else {
            btnTurnOnClick.setText("TURN ON");
            txt.setTextColor(root.getResources().getColor(R.color.colorRed));
            txt.setText("Nồng độ vượt ngưỡng");
            sendDataMQTT(createTurnOnFanJSON("1").toString(), turnOnFanTopic);
        }
    }




}