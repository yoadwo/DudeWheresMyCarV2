package com.gingos.dudewheresmycar;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.gingos.dudewheresmycar.Dialogs.ConfirmationDialogFragment;
import com.gingos.dudewheresmycar.StateManagers.MapStateManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

// with https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
public class MapsFragment extends Fragment implements OnMapReadyCallback, ConfirmationDialogFragment.ConfirmationDialogListener {

    private static final String TAG = "DUDE_nav_mapFragment";

    private static final int CONFIRMATION_DIALOG_TO_MAP_FRAGMENT_REQUEST_CODE = 200;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;
    private static final int DEFAULT_ZOOM = 15;

    private FusedLocationProviderClient _fusedLocationProviderClient; // The entry point to the Fused Location Provider.
    private Location _lastKnownLocation; // Last-known location retrieved by the Fused Location Provider.
    private final LatLng _defaultLocation = new LatLng(32.299, -64.79); //Bermuda Triangle
    private boolean _locationPermissionGranted;

    private GoogleMap _googleMap;
    private Marker _parkingMarker;
    private MarkerOptions _markerOptions;



    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //build the map
        SupportMapFragment _supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (_supportMapFragment == null){
            Log.d(TAG, "onCreate: " + "supportMapFragment was null");
            _supportMapFragment = new SupportMapFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.map, _supportMapFragment).commit();
        }
        _supportMapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        _fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set views
        // set buttons
        ImageButton bt_maps_marker_set = getView().findViewById(R.id.imgb_maps_set);
        if (bt_maps_marker_set != null)
            bt_maps_marker_set.setOnClickListener(addMarkerListener);
        else
            Log.d(TAG, "onViewCreated: " + "imgb_maps_set view not found (return null)");

        ImageButton bt_maps_marker_clear = getView().findViewById(R.id.imgb_maps_clear);
        if (bt_maps_marker_clear != null)
            bt_maps_marker_clear.setOnClickListener(clearMarkerListener);
        else
            Log.d(TAG, "onViewCreated: " + "imgb_maps_clear view not found (return null)");

    }


    private final View.OnClickListener addMarkerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            markCurrentLocation();
        }
    };

    private void markCurrentLocation(){
        Log.d(TAG, "onClick: " + "add marker clicked");

        // not checking permission as it is checked with getDeviceLocation
        getDeviceLocation();

        if (_parkingMarker == null){
            _markerOptions = new MarkerOptions()
                    .title("Your Car Here!")
                    .position(new LatLng(_lastKnownLocation.getLatitude(), _lastKnownLocation.getLongitude()));
            _parkingMarker = _googleMap.addMarker(_markerOptions);
        }
        else {
            showConfirmationDialog();
        }
    }

    private void showConfirmationDialog() {
        ConfirmationDialogFragment markerDialog =
                ConfirmationDialogFragment.newInstance
                        (getString(R.string.navigation_map_replace_marker_dialog_title), getString(R.string.navigation_map_replace_marker_dialog_message));

        markerDialog.show(getFragmentManager(), "Nav_maps_ConfirmationDialog");
        markerDialog.setTargetFragment(MapsFragment.this, CONFIRMATION_DIALOG_TO_MAP_FRAGMENT_REQUEST_CODE);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogPositiveClick: ");
        // if confirmed new marker
        if (_parkingMarker != null)
            _parkingMarker.remove();
        _markerOptions = new MarkerOptions()
                .title("Your Car Here!")
                .position(new LatLng(_lastKnownLocation.getLatitude(), _lastKnownLocation.getLongitude()));
        _parkingMarker = _googleMap.addMarker(_markerOptions);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogNegativeClick: ");
    }

    private final View.OnClickListener clearMarkerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + "clear marker clicked");
            if (_parkingMarker != null){
                _parkingMarker.remove();
                _parkingMarker = null;
            }

            if (_markerOptions != null)
                _markerOptions = null;

            if (_googleMap != null)
                _googleMap.clear();
        }
    };

    // check-explain-request permission cycle for location
    // if granted, will get map for the supportMapFragment
    // if not granted, show dialog
    private void permissionRequestCycle_location(){
        if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onMapReady: " + " permissionRequestCycle_location: " + "has GPS fine location permission");
            _locationPermissionGranted = true;

        }else{
            Log.d(TAG, "onMapReady: " + " permissionRequestCycle_location: " + "no permission granted yet for GPS fine location");
            _locationPermissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    // upon permission result, toggle _locationPermissionGranted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "gps fine permission granted", Toast.LENGTH_SHORT).show();
                _locationPermissionGranted = true;
                //_supportMapFragment= (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                //_supportMapFragment.getMapAsync(this);
            } else {
                Toast.makeText(getContext(), "gps fine permission denied", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onRequestPermissionsResult: gps fine permission denied");
                _locationPermissionGranted = false;
            }
        }
        // according to tutorial. maybe should be removed because it nags the user.
        updateLocationUI();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady: ");
        _googleMap = googleMap;

        //TODO
        setMapMarker();
        
        // Prompt the user for permission.
        permissionRequestCycle_location();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    // if location permission is granted, add "current location" button to map UI
    // if not granted, ask for it
    private void updateLocationUI(){
        if (_googleMap == null) {
            return;
        }
        try {
            if (_locationPermissionGranted) {
                _googleMap.setMyLocationEnabled(true);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                _googleMap.setMyLocationEnabled(false);
                _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                _lastKnownLocation = null;
                permissionRequestCycle_location();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    // ASYNC - use fusedLocations to retrieve device location and move camera there(!)
    private void getDeviceLocation(){
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (_locationPermissionGranted) {
                Log.d(TAG, "getDeviceLocation: " + "registering task with OnComplete listener");
                Task locationResult = _fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(onCompleteListener);

            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }

    }

    private final OnCompleteListener onCompleteListener = new OnCompleteListener<Location>() {
        @Override
        public void onComplete(@NonNull Task<Location> task) {
            if (task.isSuccessful()) {
                Log.d(TAG, "onCompleteListener, " + "onComplete: " + "location task successful");
                // Set the map's camera position to the current location of the device.
                _lastKnownLocation = task.getResult();
                _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(_lastKnownLocation.getLatitude(),
                                _lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            } else {
                Log.d(TAG, "onCompleteListener, " + "onComplete: " + "location task unsuccessful");
                Log.e(TAG, "Exception: %s", task.getException());
                // if task fails, go to default location (bermuda triangle)
                _googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(_defaultLocation, DEFAULT_ZOOM));
                _googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        }
    };

    private void setMapMarker(){
        Log.d(TAG, "setMapMarker: ");
        if (_parkingMarker != null){
            Log.d(TAG, "setMapMarker: " + "_parkingMarker is not null");
            // app came either from background or from another fragment
            redrawMarker();
        }
        else {
            Log.d(TAG, "setMapMarker: " + "_parkingMarker is null");
            MapStateManager mapStateManager = MapStateManager.getInstance(getContext());
            MarkerOptions loadedMarkerOptions = mapStateManager.loadMapState();
            if (loadedMarkerOptions != null){
                // new app lifecycle, but not the first one
                // so a photo was likely to be taken
                Log.d(TAG, "setMapMarker: " + "loadedMarkerOptions was found on ,mapStateManager, restoring");
                _markerOptions = loadedMarkerOptions;
                redrawMarker();
            }
            else {
                // new app lifecycle, with no photo previously taken
                Log.d(TAG, "setMapMarker: " + "no markerOptions saved on mapStateManager");
                Toast.makeText(getContext(),"No marker exists for app",Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    public void redrawMarker(){
        if (_parkingMarker != null)
            _parkingMarker.remove();
        if (_markerOptions != null){
            _parkingMarker = _googleMap.addMarker(_markerOptions);
            _googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(_parkingMarker.getPosition(), DEFAULT_ZOOM));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + "saving map state");
        saveMapState();
    }

    private void saveMapState() {
        MapStateManager mapStateManager = MapStateManager.getInstance(getContext());

        if (_parkingMarker != null) {
            mapStateManager.saveMapState(_parkingMarker.getTitle(), _parkingMarker.getPosition());
        }
    }


}
