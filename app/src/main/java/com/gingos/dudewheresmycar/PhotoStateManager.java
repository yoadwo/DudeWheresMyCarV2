package com.gingos.dudewheresmycar;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PhotoStateManager {

    private static final String TAG = "DUDE_photoStateManager";

    private final String PREFERENCES_PHOTO = getClass().getPackage() + "_photoPreferences";
    private final String CHANGED = "changed";
    private final String LAST_PATH = "LastPath";
    private SharedPreferences photoStatePrefs;

    private static PhotoStateManager instance;

    // singleton private c-tor
    private PhotoStateManager(Context context){
        // shared-preferences uses different file per fragment
        photoStatePrefs = context.getSharedPreferences(PREFERENCES_PHOTO, Context.MODE_PRIVATE);
    }

    // PhotoStateManager is a singleton
    public static PhotoStateManager getInstance(Context context){
        if (instance == null){
            instance = new PhotoStateManager(context);
        }
        return instance;
    }



    public void savePhotoState(String path){
        SharedPreferences.Editor photoEditor = photoStatePrefs.edit();
        if (path != null){
            Log.d(TAG, "savePhotoState: " + " saving.");
            photoEditor.putString(LAST_PATH, path);
            photoEditor.apply();
        }
    }

    public String loadPhotoState(){
        String filePath = photoStatePrefs.getString(LAST_PATH, null);
        if (filePath != null){
            Log.d(TAG, "loadPhotoState: " + " path found at sharedPreferences");
        }
        else {
            Log.d(TAG, "loadPhotoState: " + " no path found at sharedPreferences ");
        }
        return filePath;
    }


}
