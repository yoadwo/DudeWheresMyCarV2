package com.gingos.dudewheresmycar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class CameraFragment extends Fragment implements ConfirmationDialogFragment.ConfirmationDialogListener{

    private static final String TAG = "DUDE_camera";

    public static final int REQUEST_IMAGE_CAPTURE = 10;
    private static final int STORAGE_PERMISSION_CODE = 21;
    private static final int CONFIRMATION_DIALOG_TO_CAMERA_FRAGMENT_REQUEST_CODE = 31;

    private String _currentPhotoPath;
    private String _oldPhotoPath;
    private ImageView imgv_camera_thumbnail;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    /*
    Inflate the view scheme
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    /*
    always use findViewById in onViewCreated(when view is fully created)
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set views
        // set camera use button
        ImageButton bt_camera_use = getView().findViewById(R.id.imgb_camera_use);
        if (bt_camera_use !=null){
            bt_camera_use.setOnClickListener(takePhotoListener);
        } else
            Log.d(TAG, "onViewCreated: " + "bt_camera_use not found (return null)");

        ImageButton bt_camera_share = getView().findViewById(R.id.imgb_camera_share);
        if (bt_camera_share !=null){
            bt_camera_share.setOnClickListener(sharePhotoListener);
        } else
            Log.d(TAG, "onViewCreated: " + "bt_camera_share not found (return null)");

        ImageButton bt_camera_clear = getView().findViewById(R.id.imgb_camera_clear);
        if (bt_camera_clear !=null){
            bt_camera_clear.setOnClickListener(clearPhotoListener);
        } else
            Log.d(TAG, "onViewCreated: " + "bt_camera_clear not found (return null)");

        ImageButton bt_camera_restore = getView().findViewById(R.id.imgb_camera_restore);
        if (bt_camera_restore !=null){
            bt_camera_restore.setOnClickListener(restorePhotoListener);
        } else
            Log.d(TAG, "onViewCreated: " + "bt_camera_restore not found (return null)");

        // set camera container imageview
        imgv_camera_thumbnail = getView().findViewById(R.id.imgv_camera_thumbnail);
        Log.d(TAG, "onViewCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + "mCurrentPhotoPath:" + _currentPhotoPath);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    private View.OnClickListener takePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "onClick: " + "camera take button clicked");
            if (_currentPhotoPath == null)
                // no photo was taken in this app run, so no problem to take a new one
                dispatchTakePictureIntent();
            else{
                // photo was taken or restored, we should ask the user if he wants to replace current
                ConfirmationDialogFragment dialog = new ConfirmationDialogFragment();
                dialog.show(getFragmentManager(), "ConfirmationDialog");
                dialog.setTargetFragment(CameraFragment.this, CONFIRMATION_DIALOG_TO_CAMERA_FRAGMENT_REQUEST_CODE);
            }

            /*
            app versioning needed (minSDK is 21
            if(isWriteStorageAllowed())
                dispatchTakePictureIntent();
            else
                requestWriteStoragePermission();
            */
        }
    };

    private View.OnClickListener clearPhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "onClick: " + "camera clear button clicked");
            _currentPhotoPath = null;
            imgv_camera_thumbnail.setImageResource(R.drawable.ic_all_out_black_24dp);
        }
    };

    private View.OnClickListener sharePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.v(TAG, "onClick: " + "camera take button clicked");
            dispatchSharePictureIntent();
        }
    };

    private View.OnClickListener restorePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: " + "camera restore button clicked");
            setCameraThumbnail();
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onStop() {
        super.onStop();
        savePhotoState();
        Log.d(TAG, "onStop: " + "mCurrentPhotoPath:" + _currentPhotoPath);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }

    //
    //

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Log.d(TAG, "onActivityResult: " + "REQUEST_IMAGE_CAPTURE");
            switch (resultCode) {
                case RESULT_OK:
                    Log.d(TAG, "onActivityResult: " + "IMAGE_CAPTURE-->RESULT_OK");
                    // not using @param data. It is null, because intent was sent with extras
                    /*
                    TODO:
                    1. thumbnail will a default size, which will change after a photo was taken
                    */
                    // update thumbnail with taken photo
                    setPic();
                    // add photo to public gallery folder
                    galleryAddPic();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG, "onActivityResult: " + "IMAGE_CAPTURE-->RESULT_CANCELED");
                    _currentPhotoPath = _oldPhotoPath;
                    // restore path if user cancelled camera
                    // only if had photo on screen. if pressed clear then regrets camera - photo is gone.
                    if (_currentPhotoPath != null)
                        setPic();
                    deleteTempFiles(getContext().getCacheDir());
                    break;

            }
        }
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //Displaying a toast
                Log.d(TAG, "onRequestPermissionsResult: " + "granted");
                Toast.makeText(getContext(),"Permission granted now you can write to the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Log.d(TAG, "onRequestPermissionsResult: " + "denied");
                Toast.makeText(getContext(),"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean isWriteStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        return result == PackageManager.PERMISSION_GRANTED;

        //If permission is not granted returning false
    }

    private void requestWriteStoragePermission() {
        //If the user has denied the permission previously your code will come to this block
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Camera is needed to save car's photo", Toast.LENGTH_SHORT).show();
        }

        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);



    }

    // called when Take button is pressed

    private void dispatchTakePictureIntent() {
        Log.v(TAG, "dispatchTakePictureIntent: ");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Return the Activity this fragment is currently associated with.
        android.content.Context activityContext = getActivity();
        if (activityContext != null){
            android.content.pm.PackageManager pm = activityContext.getPackageManager();
            if (pm != null){
                if (takePictureIntent.resolveActivity(pm) != null) {
                    File photoFile;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Toast.makeText(activityContext, "Could not create file on disk", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "dispatchTakePictureIntent: ", ex);
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

    // called when Share button is pressed

    private void dispatchSharePictureIntent(){
        if (_currentPhotoPath == null) {
            Toast.makeText(getContext(), "Take Photo First", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "dispatchSharePictureIntent: " + "mCurrentPhotoPath is null");
        }
        else{
            Intent sharePhotoIntent = new Intent(Intent.ACTION_SEND);
            sharePhotoIntent.setType("image/jpg");
            sharePhotoIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(_currentPhotoPath));
            Log.d(TAG, "dispatchSharePictureIntent: " + "dispatching...");
            startActivity(Intent.createChooser(sharePhotoIntent, "Share Image Using"));
        }

    }

    // attempt to restore last photo taken into thumbnail
    // assumes mCurrentPhotoPath is null
    // looks in the PhotoStateManager class for recent photo entry

    private void setCameraThumbnail() {
        // init photoStateManager instance
        PhotoStateManager photoStateManager = PhotoStateManager.getInstance(getActivity());
        String loadedPath = photoStateManager.loadPhotoState();
        // no photo was saved when app was started
        if (_currentPhotoPath == null){
            // on new instance of app
            Log.d(TAG, "setCameraThumbnail: " + "mCurrentPhotoPath is null");
            if (loadedPath != null){
                // new instance, but on previous app run a photo was saved
                // restore last photo used
                Log.d(TAG, "setCameraThumbnail: " + "loadedPath was found on PhotoStateManager, restoring");
                _currentPhotoPath = loadedPath;
                setPic();
            } else {
                // new instance, and no photo was saved on previous app run
                Log.d(TAG, "setCameraThumbnail: " + "no path saved on photoStateManager");
                Toast.makeText(getContext(),"No photo saved for app",Toast.LENGTH_SHORT).show();
            }
        } else {
            // app returned from background
            Log.d(TAG, "setCameraThumbnail: " + "mCurrentPhotoPath is not null");

            if (loadedPath != null){
                // some photo is on the thumbnail, user wants saved one
                //promptThumbnailReplaceDialog();
                Toast.makeText(getContext(), "Saved photo is already on screen...", Toast.LENGTH_SHORT).show();
            } else {
                // no photo was previously saved, and new instance of app
                // not supposed to happen, because a photo is saved on any exit. just LOG it for now.
                Log.d(TAG, "setCameraThumbnail: " + "currentPhoto is null, loadedPath is null, somehow got here");
            }

        }

    }

    private void promptThumbnailReplaceDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new ConfirmationDialogFragment();
        dialog.show(getFragmentManager(), "ReplaceDialog");

    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogPositiveClick: ");
        dispatchTakePictureIntent();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d(TAG, "onDialogNegativeClick: ");
    }


    //create image on phone disk, format: JPEG_<date>_.jpeg

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String imageFileName = "DUDE_" + timeStamp + "_";
        //File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES); //original and works as private
        File storageDir = getStorageDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir       /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // backup currentPhotoPath in case of camera cancel
        _oldPhotoPath = _currentPhotoPath;
        _currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // get directory path for storing the photos
    // at the moment, app couldn't get WRITE permissions, hence storage.mkdir() @ getExternalStoragePublicDirectory fails
    // so photos are saved privately at getExternalFilesDir (pictures)

    private File getStorageDir() {
        File storageDir = null;
        boolean success = true;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    getString(R.string.album_dir));
            if (storageDir != null) {

                if (!storageDir.exists()) {
                    Log.d(TAG, "getStorageDir: " + "dir does not exist, trying to create..");
                    if (!storageDir.mkdir()) {
                        Log.d(TAG, "getStorageDir: " + "failed to create dir");
                        success = false;
                    } else {
                        Log.d(TAG, "getStorageDir: " + "DIR CREATED!");
                    }
                } else {
                    Log.d(TAG, "getStorageDir: " + "dir exists");
                }

            } else {
                Log.d(TAG, "getStorageDir: " + "failed to get DIRECTORY_PICTURES");
                success = false;

            }

        } else {
            Log.w(TAG,"getStorageDir: " + "getExternalStorage mounted state is false, using getExternalFilesDir instead");
            success = false;
        }

        if (!success)
            storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return storageDir;

    }

    // used to decode mCurrentPhotoPath into imageview thumbail
    // assumes mCurrentPhotoPath has some path value

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imgv_camera_thumbnail.getWidth();
        int targetH = imgv_camera_thumbnail.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(_currentPhotoPath, bmOptions);

        // Determine how much to scale down the image
        //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        int scaleFactor = calculateInSampleSize(bmOptions, targetW, targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        //Deprecated:
        //bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(_currentPhotoPath, bmOptions);
        imgv_camera_thumbnail.setImageBitmap(bitmap);

    }


    // A more efficient way of presenting the bitmap
    // loads a scaled down version into memory
    // taken from https://developer.android.com/topic/performance/graphics/load-bitmap
     // use in case of OutOfMemory exception

    public int calculateInSampleSize(
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

        File f = new File(_currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);

        String path = contentUri.getPath(), fileName = contentUri.getLastPathSegment();

    }

    // recursively delete temp files in folder
    // needed when user cancels camera (file is created prior to camera successful return)
    private boolean deleteTempFiles(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteTempFiles(f);
                    } else {
                        f.delete();
                    }
                }
            }
        }
        return file.delete();
    }

    private void savePhotoState(){
        PhotoStateManager photoStateManager = PhotoStateManager.getInstance(getActivity());
        // mCurrentPhotoPath could be null, but that's a waste of resources to save it anyway
        if (_currentPhotoPath != null)
            photoStateManager.savePhotoState(_currentPhotoPath);
    }
    
}
