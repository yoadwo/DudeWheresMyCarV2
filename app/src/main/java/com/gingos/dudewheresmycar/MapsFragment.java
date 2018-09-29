package com.gingos.dudewheresmycar;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "DUDE_nav_mapFragment";

    private static final int GPS_FINE_PERMISSION_REQUEST_CODE = 100;

    //private boolean is_GPSCoarsePermission_granted;
    private boolean is_GPSFinePermission_granted;
    SupportMapFragment supportMapFragment;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreateView: has GPS fine location permission");
            if (supportMapFragment == null){
                Log.d(TAG, "onCreateView: " + "supportMapFragment was null");
                supportMapFragment = new SupportMapFragment();
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                ft.replace(R.id.map, supportMapFragment).commit();
            }
            supportMapFragment.getMapAsync(this);

        }else{
            Log.d(TAG, "onCreateView: no permission granted yet for GPS fine location");
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, GPS_FINE_PERMISSION_REQUEST_CODE);
        }
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GPS_FINE_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "gps fine permission granted", Toast.LENGTH_SHORT).show();
                is_GPSFinePermission_granted = true;
                supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(this);
            } else {
                Toast.makeText(getContext(), "gps fine permission denied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onRequestPermissionsResult: gps fine permission denied");
                is_GPSFinePermission_granted = false;
            }
        }
    }
}
