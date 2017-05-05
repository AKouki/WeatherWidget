package com.akouki.weatherwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.akouki.weatherwidget.helpers.PrefsManager;
import com.akouki.weatherwidget.models.WidgetConfiguration;
import com.akouki.weatherwidget.tasks.TimeZoneFetchTask;

import java.util.concurrent.ExecutionException;

public class WeatherWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    TextView tvLocation, tvUnit;
    WidgetConfiguration wc = new WidgetConfiguration();

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            PrefsManager.saveConfigs(WeatherWidgetConfigureActivity.this,
                    mAppWidgetId,
                    wc.getLocation(),
                    wc.getLocationName(),
                    wc.getDisplayUnit(),
                    wc.getTimeRawOffset());

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WeatherWidgetConfigureActivity.this);
            WeatherWidget.updateAppWidget(WeatherWidgetConfigureActivity.this, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public WeatherWidgetConfigureActivity() {
        super();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Array Structure
                // [0] = Location Id
                // [1] = Location Name
                // [2] = Country Code
                // [3] = Latitude
                // [4] = Longitude
                String[] result = data.getStringArrayExtra("location");
                wc.setLocation(Integer.parseInt(result[0]));
                wc.setLocationName(result[1]);
                tvLocation.setText(result[1] + "," + result[2]);
                double lat = Double.parseDouble(result[3]);
                double lon = Double.parseDouble(result[4]);

                // Get TimeZone
                int rawOffset = 0;
                try {
                    rawOffset = new TimeZoneFetchTask(lat, lon).execute().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                wc.setTimeRawOffset(rawOffset);

                findViewById(R.id.add_button).setEnabled(true);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Please select Location...", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.weather_widget_configure);

        tvLocation = (TextView) findViewById(R.id.textViewLocation);
        tvUnit = (TextView) findViewById(R.id.textViewUnit);
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), PlacePickActivity.class), 1);
            }
        });
        tvUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectUnitDialog();
            }
        });

        wc.setLocation(0);
        wc.setDisplayUnit(tvUnit.getText().toString());

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        int locationId = PrefsManager.loadConfig(getApplicationContext(), mAppWidgetId).getLocation();
        if (locationId == 0)
            startActivityForResult(new Intent(this, PlacePickActivity.class), 1);
    }

    private void selectUnitDialog() {
        final CharSequence[] items = {"Celsius", "Fahrenheit"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Temperature Unit");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                tvUnit.setText(items[position]);
                wc.setDisplayUnit((String) items[position]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}

