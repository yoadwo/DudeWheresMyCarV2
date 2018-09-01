package com.gingos.dudewheresmycar;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment {

    private static final String TAG_camera = "DUDE_camera";

    public static final int REQUEST_IMAGE_CAPTURE = 10;

    private String mCurrentPhotoPath;
    private ImageView imgv_camera_thumbnail;

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
        imgv_camera_thumbnail = getView().findViewById(R.id.imgv_camera_thumbnail);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG_camera, "onActivityResult: ");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d(TAG_camera, "onActivityResult: " + "REQUEST_IMAGE_CAPTURE");
            switch (resultCode) {
                case RESULT_OK:
                    Log.d(TAG_camera, "onActivityResult: " + "IMAGE_CAPTURE-->RESULT_OK");
                    // not using @param data. It is null, because intent was sent with extras
                    /*
                    TODO:
                    1. thumbnail will a default size, which will change after a photo was taken
                    */
                    setPic();
                    galleryAddPic();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG_camera, "onActivityResult: " + "IMAGE_CAPTURE-->RESULT_CANCELED");
                    break;

            }
        }
    }

    private void dispatchTakePictureIntent() {
        Log.v(TAG_camera, "dispatchTakePictureIntent: ");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Return the Activity this fragment is currently associated with.
        android.content.Context activityContext = getActivity();
        if (activityContext != null){
            android.content.pm.PackageManager pm = activityContext.getPackageManager();
            if (pm != null){
                if (takePictureIntent.resolveActivity(pm) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(activityContext, "Could not create file on disk", Toast.LENGTH_SHORT).show();
                        Log.e(TAG_camera, "dispatchTakePictureIntent: ", ex);
                        return;
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        //Uri photoURI = Uri.fromFile(photoFile);
                        Uri photoURI = FileProvider.getUriForFile(activityContext,
                                getString(R.string.uri_provider),
                                photoFile);
                        /*
                         NOTE! sending intents with EXTRA_OUTPUT means that its data
                            will be null in OnActivityResult
                          */
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }


                }
            }

        }

    }

    /*
    create image on phone disk, format: JPEG_<date>_.jpeg
    */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String imageFileName = "DUDE_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir       /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imgv_camera_thumbnail.getWidth();
        int targetH = imgv_camera_thumbnail.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

        // Determine how much to scale down the image
        //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        int scaleFactor = calculateInSampleSize(bmOptions, targetW, targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //Deprecated:
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imgv_camera_thumbnail.setImageBitmap(bitmap);

    }

    /*
    A more efficient way of presenting the bitmap
    loads a scaled down version into memory
    taken from https://developer.android.com/topic/performance/graphics/load-bitmap
    use in case of OutOfMemory exception
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private void galleryAddPic() {
        //TODO: create in folder DudeApp
        // NOT WORKING
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);

        String path = contentUri.getPath(), fileName = contentUri.getLastPathSegment();



    }





}
