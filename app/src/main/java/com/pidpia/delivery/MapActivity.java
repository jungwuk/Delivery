package com.pidpia.delivery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
    Geocoder coder;
    LocationManager locManager;
    LocationListener locationListener;
    double latPoint, lngPoint;
    GoogleMap googleMap;

    LinearLayout map_location_arrive, map_location_current;
    LatLng dest_point;

    User user;
    float lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        user = new User(this);

        //GPS가 켜져있지 않을때
        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            builder.setMessage("GPS가 꺼져있습니다. GPS를 켜주세요")
                    .setCancelable(false)
                    .setPositiveButton("GPS 켜기", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent gps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(gps);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            onBackPressed();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();

        }

        Log.d("LOC:", user.getData("end_lat") + "/" + user.getData("end_lng"));


        try {

            lat = Float.parseFloat(user.getData("end_lat"));
            lng = Float.parseFloat(user.getData("end_lng"));

            Log.d("LOC2:", lat + "/" + lng);
        } catch (Exception ee) {
            Log.d("error point", "");
            lat = 0.00f;
            lng = 0.00f;
        }

        dest_point = new LatLng(lat,lng);

        map_location_arrive = (LinearLayout) findViewById(R.id.map_location_arrive);
        map_location_current = (LinearLayout) findViewById(R.id.map_location_current);


        Log.d("[LOC]", "Start");

        //map.moveCamera(CameraUpdateFactory.newLatLng(sydney));



        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("[LOC]", "Faield...1");

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {


            coder = new Geocoder(this, Locale.KOREA);
            locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location locations) {
                    String location_address = "";
                    latPoint = locations.getLatitude();
                    lngPoint = locations.getLongitude();

                    Log.d("[LOC]:", latPoint + " " + lngPoint + " - " + location_address);
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latPoint, lngPoint)).title("현재위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location));
                    googleMap.addMarker(marker);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latPoint, lngPoint), 16));


                    try {
                        if (coder != null) {
                            List<Address> address = coder.getFromLocation(latPoint, lngPoint, 1);
                            if (address != null && address.size() > 0) {
                                location_address = address.get(0).getAddressLine(0).toString();
                                //  order_input_address.setText(location_address);
                                Toast.makeText(MapActivity.this, location_address, Toast.LENGTH_SHORT).show();
                                if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.


                                    Log.d("[LOC]", "Faield...2");
                                    return;
                                }
                                locManager.removeUpdates(locationListener);
                            } else {
                                Toast.makeText(getApplicationContext(), "Address null.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        // order_input_address.setText("");
                        Toast.makeText(getApplicationContext(), "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        locManager.removeUpdates(locationListener);
                        Log.d("location: error", e.toString());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };


        }

        Log.d("[LOC]", "Start ok");


        map_location_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 1, locationListener);
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, locationListener);
            }
        });
        map_location_arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(dest_point, 16));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality

            Log.d("[LOC]:", "get permission ok");


        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        MarkerOptions marker = new MarkerOptions().position(dest_point).title("배송지").icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery_marker));
//        MarkerOptions marker = new MarkerOptions().position(new LatLng(latPoint, lngPoint)).title("현재위치").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus_location));

        googleMap.addMarker(marker);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dest_point, 16));

    }
}