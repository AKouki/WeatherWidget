package com.akouki.weatherwidget.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.akouki.weatherwidget.R;
import com.akouki.weatherwidget.helpers.JSONParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PlaceApiTask extends AsyncTask<Void, Void, String> {

    private static final String PLACES_API = "http://api.openweathermap.org/data/2.5/find";
    private static final String API_KEY = "YOUR_API_KEY";

    Activity activity;
    String cityName;
    ProgressDialog progressDialog;

    public PlaceApiTask(Activity activity, String cityName) {
        this.activity = activity;
        this.cityName = cityName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(activity, "", "Please wait...", true, false);
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            StringBuilder sb = new StringBuilder(PLACES_API);
            sb.append("?q=" + URLEncoder.encode(cityName, "utf8"));
            sb.append("&type=accurate&mode=json");
            sb.append("&appid=" + API_KEY);

            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);

            InputStream is = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        progressDialog.cancel();

        final ArrayList<String> arrayList = JSONParser.getLocations(json);

        ListView lv = (ListView) activity.findViewById(R.id.lvResult);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, arrayList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("location", arrayList.get(position).split("/"));
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }
}
