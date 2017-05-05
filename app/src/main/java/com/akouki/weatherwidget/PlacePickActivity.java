package com.akouki.weatherwidget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.akouki.weatherwidget.tasks.PlaceApiTask;

public class PlacePickActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_pick);
        setTitle("Select City...");

        final EditText editLocation = (EditText) findViewById(R.id.editLocation);
        ImageButton btnSearch = (ImageButton) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaceApiTask placeApiTask = new PlaceApiTask(PlacePickActivity.this, editLocation.getText().toString());
                placeApiTask.execute();
            }
        });
    }
}
