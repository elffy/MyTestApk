/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zjl.test.sensor;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

/**
 * A class that handles everything about location.
 */
public class LocationManager {
    private static final String TAG = "LocationManager";

    private Context mContext;
    private Listener mListener;
    private android.location.LocationManager mLocationManager;

    LocationListener [] mLocationListeners = new LocationListener[] {
            new LocationListener(android.location.LocationManager.GPS_PROVIDER),
            new LocationListener(android.location.LocationManager.NETWORK_PROVIDER)
    };

    public interface Listener {
        public void onLocationChanged(Location newLocation);
        public void onLocationReady(Address result);
   }

    public LocationManager(Context context, Listener listener) {
        mContext = context;
        mListener = listener;
    }

    public void requestCurrentAddress() {
        startReceivingLocationUpdates();
    }

    public boolean isProviderEnabled() {
        if (mLocationManager == null) {
            mLocationManager = (android.location.LocationManager)
                    mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager == null) {
            return false;
        }
        return mLocationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
                || mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    public void startReceivingLocationUpdates() {
        if (mLocationManager == null) {
            mLocationManager = (android.location.LocationManager)
                    mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        if (mLocationManager != null) {
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.NETWORK_PROVIDER,
                        1000,
                        0F,
                        mLocationListeners[1]);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            try {
                mLocationManager.requestLocationUpdates(
                        android.location.LocationManager.GPS_PROVIDER,
                        1000,
                        0F,
                        mLocationListeners[0]);
            } catch (SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "provider does not exist " + ex.getMessage());
            }
            Log.d(TAG, "startReceivingLocationUpdates");
        }
    }

    public void stopReceivingLocationUpdates() {
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
            Log.d(TAG, "stopReceivingLocationUpdates");
        }
    }

    private class LocationListener implements android.location.LocationListener {
        String mProvider;

        public LocationListener(String provider) {
            mProvider = provider;
        }

        @Override
        public void onLocationChanged(Location newLocation) {
            Log.d(TAG, "onLocationChanged:" + newLocation);
            if (newLocation.getLatitude() == 0.0
                    && newLocation.getLongitude() == 0.0) {
                // Hack to filter out 0.0,0.0 locations
                return;
            }
//            if (requestOnlyLocation) {
                mListener.onLocationChanged(newLocation);
//                return;
//            }
            new AsyncTask<Location, Integer, Address>(){

                @Override
                protected void onPostExecute(Address result) {
                    mListener.onLocationReady(result);
                    // no need to update any more once we get a location.
                    stopReceivingLocationUpdates();
                }

                @Override
                protected Address doInBackground(Location... params) {
                    Location loc = params[0];
                    Geocoder geoCoder = new Geocoder(mContext);
                    try {
                        List<Address> list = geoCoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                        if (list != null && list.size() > 0) {
                            return list.get(0);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

            }.execute(newLocation);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }
}
