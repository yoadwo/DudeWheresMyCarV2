package com.gingos.dudewheresmycar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {

    private static final String TAG_camera = "DUDE_camera";

    static final int REQUEST_IMAGE_CAPTURE = 10;
    static final int REQUEST_TAKE_PHOTO = 20;

    private String mCurrentPhotoPath;
    private ImageView imgv_camera_container;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG_camera, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set views
        // set camera use button
        ImageButton bt_camera_use = getView().findViewById(R.id.imgb_camera_use);
        if (bt_camera_use !=null){
            bt_camera_use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG_camera, "onClick: " + "camera take button clicked");
                    dispatchTakePictureIntent();
                }
            });
        } else
            Log.d(TAG_camera, "onViewCreated: " + "bt_camera_use is null");

        // set camera container imageview
        imgv_camera_container = getView().findViewById(R.id.imgv_camera_container);
    }

    private void dispatchTakePictureIntent() {
        Log.v(TAG_camera, "dispatchTakePictureIntent: ");
        Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //Return the Activity this fragment is currently associated with.
        android.content.Context context = getActivity();
        if (context != null){
            android.content.pm.PackageManager pm = context.getPackageManager();
            if (pm != null){
                if (takePictureIntent.resolveActivity(pm) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG_camera, "onActivityResult: ");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            //TODO:
            /*
            1. imageview should have a default starting size, but change dynamically after photo taken
            2. image quality is very low for some reason
             */
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgv_camera_container.setImageBitmap(imageBitmap);
        }
    }

    /*
    create image on phone disk, format: JPEG_<date>_.jpeg
    TODO: create in folder DudeApp
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // might not work: used "context.getActivity.getExternal.." inside fragment and not activity
        File storageDir = getActivity().getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
