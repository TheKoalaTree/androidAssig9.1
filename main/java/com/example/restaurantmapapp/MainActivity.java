package com.example.restaurantmapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnAddNewPlace;
    private Button btnShowOnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnAddNewPlace = (Button) findViewById(R.id.btnAddNewPlace);
        btnShowOnMap = (Button) findViewById(R.id.btnShowOnMap);

        btnAddNewPlace.setOnClickListener(this);
        btnShowOnMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddNewPlace:
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                break;
            case R.id.btnShowOnMap:
                startActivity(new Intent(MainActivity.this, MapActivity.class));
                break;
        }
    }
}
