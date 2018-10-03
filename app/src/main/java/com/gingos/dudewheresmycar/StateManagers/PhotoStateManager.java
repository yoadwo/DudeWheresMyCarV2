package com.gingos.dudewheresmycar.StateManagers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


public class PhotoStateManager {

    private static final String TAG = "DUDE_photoStateManager";

    private final String PREFERENCES_PHOTO = getClass().getPackage() + "_photoPreferences";
    private final String CHANGED = "changed";
    private final String LAST_PATH = "LastPath";
    private final String LAST_WIDTH = "LastWidth";
    private final String Last_HEIGHT = "LastHeight";

    private SharedPreferences _photoStatePrefs;
    private static PhotoStateManager _instance;

    // singleton private c-tor
    private PhotoStateManager(Context context){
        // shared-preferences uses different file per fragment
        _photoStatePrefs = context.getSharedPreferences(PREFERENCES_PHOTO, Context.MODE_PRIVATE);
    }

    // PhotoStateManager is a singleton
    public static PhotoStateManager getInstance(Context context){
        if (_instance == null){
            _instance = new PhotoStateManager(context);
        }
        return _instance;
    }



    public void savePhotoState(String path){
        SharedPreferences.Editor photoEditor = _photoStatePrefs.edit();
        if (path != null){
            Log.d(TAG, "savePhotoState: " + " saving.");
            photoEditor.putString(LAST_PATH, path);
            photoEditor.apply();
        }
    }

    public void savePhotoState(String path, int width, int height){
        SharedPreferences.Editor photoEditor = _photoStatePrefs.edit();
        if (path != null){
            Log.d(TAG, "savePhotoState: " + " saving path.");
            photoEditor.putString(LAST_PATH, path);
            photoEditor.apply();
        }

        if (width != 0 && height != 0){
            Log.d(TAG, "savePhotoState: " + "saving width and height");
            photoEditor.putInt(LAST_WIDTH, width);
            photoEditor.putInt(Last_HEIGHT, height);
            photoEditor.apply();
        }
    }


    public String loadPhotoStatePath(){
        String filePath = _photoStatePrefs.getString(LAST_PATH, null);
        if (filePath != null){
            Log.d(TAG, "loadPhotoStatePath: " + " path found at sharedPreferences");
        }
        else {
            Log.d(TAG, "loadPhotoStatePath: " + " no path found at sharedPreferences ");
        }
        return filePath;
    }

    public int[] loadPhotoStateMeasurements(){
        int[] measurements = new int[2];
        int width = _photoStatePrefs.getInt(LAST_WIDTH, -1);
        int height = _photoStatePrefs.getInt(Last_HEIGHT, -1);
        measurements[0] = width;
        measurements[1] = height;
        return measurements;
    }


}
