package com.example.myapplication;

import com.google.gson.Gson;
import org.json.simple.JSONObject;

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

import androidx.fragment.app.Fragment;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class HomeFragment extends Fragment {


    MQTTService mqttService;
    View root;
    Button btnTurnOnClick, btnLogout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.home_fragment, container, false);
        btnTurnOnClick = root.findViewById(R.id.button2);

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


        final TextView txt = (TextView)root.findViewById(R.id.txt);


        mqttService = new MQTTService(getContext());
        mqttService.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d("mqtt", "some thing done");
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String json = message.toString();
//                port.write(data.getBytes(),1000);

//                Log.w("Message Arrived: ",  data);
//                Log.d(topic, data);
                //txt.setText(data);
                JsonObject obj = new JsonParser().parse(jsFile).getAsJsonObject();
                String data = json.getString("");

                if (Integer.parseInt(data) > 15) {
                    btnTurnOnClick.setText("TURN OFF");
                    txt.setText("Nòng Độ Gas: " + data + "%");
                    txt.setTextColor(root.getResources().getColor(R.color.colorRed));
                    sendDataMQTT("{\"id\":\"3\",\"name\":\"SPEAKER\",\"data\":\"1000\",\"unit\":\"\"}");
                }
                else {
                    btnTurnOnClick.setText("TURN ON");
                    txt.setText("Nòng Độ Gas: " + data + "%");
                    txt.setTextColor(root.getResources().getColor(R.color.colorGreen));
                }
//                System.out.print(topic + ": " + data);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        btnTurnOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String message;
//                JSONObject json = new JSONObject();
//                try {
//                    json.put("name1", "value1");
//                    message = json.toString();
//                    Log.w("json object", message);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                sendDataMQTT(json.toString());
                if (btnTurnOnClick.getText().toString().equals("TURN ON")) {
                    btnTurnOnClick.setText("TURN OFF");
                    txt.setText("ON");
                    sendDataMQTT("ON");
                }
                else {
                    btnTurnOnClick.setText("TURN ON");
                    txt.setText("OFF");
                    sendDataMQTT("OFF");
                }
            }
        });

        return root;

    }


    private void sendDataMQTT(@org.jetbrains.annotations.NotNull String data) {
        MqttMessage message = new MqttMessage();
        message.setId(1234);
        message.setQos(0);
        message.setRetained(true);

        byte[] b = data.getBytes(Charset.forName("UTF-8"));



        message.setPayload(b);
        Log.d("ABC", "Publish" + message);

        try {
            mqttService.mqttAndroidClient.publish("duyctin2000/feeds/gas-detection", message);

        } catch (MqttException e) {
            Log.w("mqtt" , "cannot send message");
        }

    }


}