package com.gingos.dudewheresmycar;

import android.content.pm.PackageManager;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "DUDE_main";


    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");
        if (savedInstanceState != null)
            Log.d(TAG, "KEY: " + savedInstanceState.getString("test-key"));

        // set views
        // draw action bar (when using drawer navigation, usually use a theme with no default action bar)
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        } else {
            Log.d(TAG, "onCreate: " + "getSupportActionBar return null");
        }
        // set drawer navigation view
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        // set drawer listener
        NavigationView navigationView = findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set default fragment is the home fragment
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }
/*

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState: ");
        super.onRestoreInstanceState(savedInstanceState);

    }
*/



    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

/*    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // saves image currently on bitmap
        Log.d(TAG, "onSaveInstanceState: ");
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    /*
                    open the drawer animation
                     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    selection of items on the drawer
    each press causes the matching fragment to be brought to top
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // set item as selected to persist highlight
        item.setChecked(true);
        // close drawer when item is tapped


        switch (item.getItemId()){
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame,
                        new HomeFragment()).commit();
                break;
            case R.id.nav_photo:
                PackageManager packageManager = getPackageManager();
                // do not let user enter the camera fragment if he has not camera
                if(!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
                    Toast.makeText(MainActivity.this,"This device does not have a camera.", Toast.LENGTH_SHORT).show();
                    break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame,
                        new CameraFragment()).commit();
                break;
            case R.id.nav_navigation:
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame,
                        new NavigationFragment()).commit();
                break;
        }
        mDrawerLayout.closeDrawers();

        return true;
    }


}
