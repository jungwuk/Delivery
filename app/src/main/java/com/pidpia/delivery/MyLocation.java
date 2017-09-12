package com.pidpia.delivery;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by USER on 2017-04-11.
 */

    public class MyLocation {

        Timer timer1;
        LocationManager lm;
        LocationResult locationResult;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        String provide=null;



        public boolean getLocation(Context context, LocationResult result) {
            //I use LocationResult callback class to pass location value from MyLocation to user code.
            locationResult = result;
            if (lm == null)
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            //exceptions will be thrown if provider is not permitted.
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }
            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            //don't start listeners if no provider is enabled
            if (!gps_enabled && !network_enabled)
                return false;

            if (gps_enabled) {
                try {

                    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);

                } catch (SecurityException e) {
                    Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                }


                provide="gps";
            }
            if (network_enabled) {

                try {

                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0, locationListenerNetwork);

                } catch (SecurityException e) {
                    Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                }




                provide="network";
            }
            return true;
        }

        LocationListener locationListenerGps = new LocationListener() {
            public void onLocationChanged(Location location) {

                locationResult.gotLocation(location);

                try {

                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerNetwork);

                } catch (SecurityException e) {
                    Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                }



            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        LocationListener locationListenerNetwork = new LocationListener() {
            public void onLocationChanged(Location location) {
//                timer1.cancel();
                locationResult.gotLocation(location);


                try {


                    lm.removeUpdates(this);
                    lm.removeUpdates(locationListenerGps);

                } catch (SecurityException e) {
                    Log.e("PERMISSION_EXCEPTION", "PERMISSION_NOT_GRANTED");
                }


            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };



        public static abstract class LocationResult {
            public abstract void gotLocation(Location location);
        }


 }

