package com.gingos.dudewheresmycar.StateManagers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapStateManager {

    private static final String TAG = "DUDE_mapStateManager";

    private final String PREFERENCES_MAP = getClass().getPackage() + "_mapPreferences";
    private final String CHANGED = "changed";
    private final String LAST_LAT = "LastLatitude";
    private final String LAST_LNG = "LastLongitude";
    private final String LAST_TITLE = "LastTitle";

    private SharedPreferences _mapStatePrefs;
    private static MapStateManager _instance;

    // singleton private c-tor
    private MapStateManager(Context context){
        // shared-preferences uses different file per fragment
        _mapStatePrefs = context.getSharedPreferences(PREFERENCES_MAP, Context.MODE_PRIVATE);
    }

    // PhotoStateManager is a singleton
    public static MapStateManager getInstance(Context context){
        if (_instance == null){
            _instance = new MapStateManager(context);
        }
        return _instance;
    }


    public void saveMapState(String title, LatLng position){
        SharedPreferences.Editor mapEditor = _mapStatePrefs.edit();
        if (title != null){
            Log.d(TAG, "saveMapState: " + " saving.");
            mapEditor.putString(LAST_TITLE, title);
            mapEditor.putFloat(LAST_LAT, (float)position.latitude);
            mapEditor.putFloat(LAST_LNG, (float)position.longitude);
            mapEditor.apply();
        }
    }


    public MarkerOptions loadMapState(){
        String title = _mapStatePrefs.getString(LAST_TITLE, null);
        double lat, lng;
        LatLng latLng;
        if (title != null){
            Log.d(TAG, "loadMapStateTitle: " + " title found at sharedPreferences");
            lat = _mapStatePrefs.getFloat(LAST_LAT, -1);
            lng = _mapStatePrefs.getFloat(LAST_LNG, -1);
            latLng = new LatLng(lat,lng);
            return new MarkerOptions().title(title).position(latLng);
        }
        else {
            Log.d(TAG, "loadMapStateTitle: " + " title not found at sharedPreferences");
            return null;
        }


    }


}
