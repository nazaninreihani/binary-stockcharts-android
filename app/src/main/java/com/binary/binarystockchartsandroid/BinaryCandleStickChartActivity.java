package com.binary.binarystockchartsandroid;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import com.binary.binarystockchart.charts.BinaryCandleStickChart;
import com.binary.binarystockchart.data.BinaryCandleEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.AssetManager;

public class BinaryCandleStickChartActivity extends AppCompatActivity {

    BinaryCandleStickChart chart;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binary_candle_stick_chart);

        this.chart = (BinaryCandleStickChart) findViewById(R.id.binaryCandleStickChart);
        this.chart.setDrawGridBackground(false);
        // scaling can now only be done on x- and y-axis separately
        this.chart.setPinchZoom(false);
        this.chart.setAutoScaleMinMaxEnabled(true);
        this.chart.setGranularity(120);

        try {
            this.chart.addEntries(createMockData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (BinaryCandleEntry entry : createMockData("candle-data-subscribe-120.json")) {

                        chart.addEntry(entry);

                        try{
                            thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private List<BinaryCandleEntry> createMockData() throws IOException {
        return createMockData("candle-data-120.json");
    }

    private List<BinaryCandleEntry> createMockData(String fileName) throws IOException {
        List<BinaryCandleEntry> entries = new ArrayList<>();

        boolean isStream = false;

        if (fileName.equals("candle-data-subscribe-120.json")) {
            isStream = true;
        }

        String jsonData = AssetManager.readFromAssets(this, fileName);

        try {
            JSONObject candleJObject = new JSONObject(jsonData);

            JSONArray entriesJArray = candleJObject.getJSONArray("candles");

            for (int i = 0; i < entriesJArray.length(); i++) {
                JSONObject entry = entriesJArray.getJSONObject(i);

                BinaryCandleEntry candleEntry = new BinaryCandleEntry(
                        Long.valueOf(entry.getString(isStream ? "open_time" : "epoch")),
                        Float.valueOf(entry.getString("high")),
                        Float.valueOf(entry.getString("low")),
                        Float.valueOf(entry.getString("open")),
                        Float.valueOf(entry.getString("close"))
                );

                entries.add(candleEntry);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return entries;
    }
}