package com.gingos.dudewheresmycar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG_main = "DUDE_main";


    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG_main, "onCreate: ");

        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
        } else {
            Log.d(TAG_main, "onCreate: " + "getSupportActionBar return null");
        }

        mDrawerLayout = findViewById(R.id.main_drawer_layout);

        NavigationView navigationView = findViewById(R.id.main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // default fragment is the home fragment
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content_frame,
                    new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

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
