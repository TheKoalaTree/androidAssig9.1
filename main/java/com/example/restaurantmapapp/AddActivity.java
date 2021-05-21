package com.example.restaurantmapapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.model.LatLng;
import com.example.restaurantmapapp.db.DatabaseAdapter;
import com.example.restaurantmapapp.db.Place;

public class AddActivity extends Activity implements View.OnClickListener {

    private EditText etPlaceName;
    private EditText etLocation;
    private Button btnGetLocation;
    private Button btnShowOnMap;
    private Button btnSAVE;
    LatLng latLng;
    String placeName;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        initView();
    }

    private void initView() {
        etPlaceName = (EditText) findViewById(R.id.etPlaceName);
        etLocation = (EditText) findViewById(R.id.etLocation);
        btnGetLocation = (Button) findViewById(R.id.btnGetLocation);
        btnShowOnMap = (Button) findViewById(R.id.btnShowOnMap);
        btnSAVE = (Button) findViewById(R.id.btnSAVE);

        btnGetLocation.setOnClickListener(this);
        btnShowOnMap.setOnClickListener(this);
        btnSAVE.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetLocation:
                startActivityForResult(new Intent(AddActivity.this, SearchActivity.class), 1001);
                break;
            case R.id.btnShowOnMap:
                Intent intent = new Intent(AddActivity.this, MapActivity.class);
                intent.putExtra("latLng",latLng);
                startActivity(intent);
                break;
            case R.id.btnSAVE:
                submit();
                break;
        }
    }

    private void submit() {
        // validate
        String etPlaceNameString = etPlaceName.getText().toString().trim();
        if (TextUtils.isEmpty(etPlaceNameString)) {
            Toast.makeText(this, "Place Name", Toast.LENGTH_SHORT).show();
            return;
        }

        String etLocationString = etLocation.getText().toString().trim();
        if (TextUtils.isEmpty(etLocationString)) {
            Toast.makeText(this, "Location", Toast.LENGTH_SHORT).show();
            return;
        }
        if (latLng == null) {
            Toast.makeText(this, "Location is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(this);
        databaseAdapter.addPlace(new Place(placeName, (float) latLng.latitude, (float) latLng.longitude));
        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
        finish();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                latLng = data.getParcelableExtra("latLng");
                placeName = data.getStringExtra("placeName");
                address = data.getStringExtra("address");
                if (latLng != null && !TextUtils.isEmpty(placeName) && !TextUtils.isEmpty(address)) {
                    etPlaceName.setText(placeName);
                    etLocation.setText(address);
                }
            }
        }
    }
}
