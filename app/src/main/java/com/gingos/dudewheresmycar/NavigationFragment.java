package com.gingos.dudewheresmycar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NavigationFragment extends Fragment {

    private static final String TAG = "DUDE_navigation";

    MapsFragment _mapsFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    // This event is triggered soon after onCreateView(), only if returned view is non-null.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /*
        if (mapsFragment1 == null)
            mapsFragment1 = new MapsFragment();
        insertNestedFragment(R.id.child_fragment_image_container, mapsFragment1);
        */
        if (_mapsFragment == null){
            Log.d(TAG, "onViewCreated: " +"nav_mapsFragment was null");
            _mapsFragment = new MapsFragment();
        }

        insertNestedFragment(R.id.child_fragment_map_container, _mapsFragment);


    }

    // Embeds the child fragment dynamically
    private void insertNestedFragment(int containerID, Fragment fragment) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(containerID, fragment).commit();
    }
}
