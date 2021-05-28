package com.example.assignment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String requestURL = "http://api.openweathermap.org/data/2.5/weather?q=";
    TextView cityTV;
    TextView descTV;
    TextView tempTV;
    TextView latTV;
    TextView longTV;
    EditText cityEt;
    String apiKey = "&appid=8cb488f4f69a9c3c9efe88853c7660c4&units=metric";
    String city = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityTV= findViewById(R.id.cityTV);
        descTV= findViewById(R.id.descTV);
        tempTV= findViewById(R.id.tempTV);
        latTV= findViewById(R.id.latTV);
        longTV= findViewById(R.id.longTV);
        cityEt= findViewById(R.id.editTextTextCityName);
        

    }

    public void getData(View view) {
        city = cityEt.getText().toString();

        if(isConnected()){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String data = downloadData(requestURL + city + apiKey);
                    try {
                        JSONObject jObject = new JSONObject(data);
                        JSONObject jCoord = jObject.getJSONObject("coord");
                        JSONArray jDesc = jObject.getJSONArray("weather");
                        JSONObject jTemp = jObject.getJSONObject("main");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    cityTV.setText("City: " + jObject.getString("name"));
                                    descTV.setText("Current Weather: " + jDesc.getJSONObject(0).getString("description"));
                                    tempTV.setText("Temperature: " + String.valueOf(jTemp.getDouble("temp")) + "Centigrade");
                                    latTV.setText("Latitude: " +  String.valueOf(jCoord.getDouble("lat")));
                                    longTV.setText("Longitude: " +  String.valueOf(jCoord.getDouble("lon")));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("MainActivity", "load data: " + e.getMessage());
                    }

                }
            }).start();

        }else {
            Toast.makeText(this, "Connection Failed!", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isConnected(){
        boolean res = false;
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            res = true;

        return res;

    }

    private String downloadData(String url){

        InputStream is = null;
        String data = "";

        try {

            URL myUrl = new URL(url);
            HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
            con.setRequestMethod("GET");

            con.connect();
            int response = con.getResponseCode();
            Log.d("MainActivity", "downloadData: response code = "+response);

            is = con.getInputStream();
            data= processResponse(is);

        } catch (MalformedURLException e) {
            Log.d("MainActivity", "downloadData" + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MainActivity", "downloadData" + e.getMessage());
        }

        return data;
    }

    private String processResponse(InputStream is) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        String line = null;
        StringBuilder sb = new StringBuilder();

        while ((line = bufferedReader.readLine()) != null){
            sb.append(line);
        }

        return sb.toString();
    }

}