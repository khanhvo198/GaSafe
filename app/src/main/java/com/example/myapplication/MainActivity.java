package com.example.myapplication;



import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LineChart chart = (LineChart) findViewById(R.id.chart);


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




    }

}