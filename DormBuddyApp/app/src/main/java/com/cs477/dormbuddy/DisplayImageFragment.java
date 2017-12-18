package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DisplayImageFragment extends DialogFragment {
    static final String DISPLAY_IMAGE_TAG = "DisplayImageTag";
    boolean isLocal = false;
    int imageResource;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_display_image, null);
        ImageView displayImage = (ImageView) v.findViewById(R.id.displayImage);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        if (! isLocal) { //if the element is a server/user uploaded photo
            Bitmap image = getArguments().getParcelable("image");
            if (image != null) {
                displayImage.setImageBitmap(image);
            }
        } else { //else if its a locally stored large photo
            imageResource = getArguments().getInt("drawable");
            //shows a cropped version of the image resourse, depending on device dimensions
            // avoids overfilling heap in the process
            displayImage.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), imageResource), 500,670,true));
            //save campus map
            builder.setPositiveButton("Save Photo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    );
                    //saves original image with original dimensions
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), imageResource);
                    StoreImageInPhone storeImage = new StoreImageInPhone(bmp, getContext().getContentResolver());
                    //save the image
                    storeImage.execute();
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        return builder.create();
    }


    static DisplayImageFragment newInstance(Bitmap image){
        DisplayImageFragment display = new DisplayImageFragment();
        Bundle args = new Bundle();
       // Drawable temp;
        args.putParcelable("image",image);
        //args.putInt("imageID",imageID);
        display.setArguments(args);
        display.isLocal = false;
        return display;
    }

    static DisplayImageFragment newInstanceFromLocal(int drawable){
        DisplayImageFragment display = new DisplayImageFragment();
        Bundle args = new Bundle();
        // Drawable temp;
        args.putInt("drawable",drawable);
        //args.putInt("imageID",imageID);
        display.setArguments(args);
        display.isLocal = true;
        return display;
    }

    static class StoreImageInPhone extends AsyncTask<Void, Void, Boolean> {
        Bitmap bmp;
        ContentResolver resolver;

        StoreImageInPhone(Bitmap bmp, ContentResolver resolver) {
            this.bmp = bmp;
            this.resolver = resolver;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                File storedImagePath = generateImagePath("DormBuddy-Saved-Map", "png");
                if (!compressAndSaveImage(storedImagePath, bmp)) {
                    return null;
                }
                Uri url = addImageToGallery(resolver, "png", storedImagePath);
                return true;
            }
            catch (Exception e){
                return false;
            }
        }

        private static File getImagesDirectory() {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "DormBuddy");//Environment.getExternalStorageDirectory()
            if (!file.mkdirs() && !file.isDirectory()) {
                Log.e("mkdir", "Directory not created");
            }
            return file;
        }

        public static File generateImagePath(String title, String imgType) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
            return new File(getImagesDirectory(), title + "_" + sdf.format(new Date()) + "." + imgType);
        }

        public boolean compressAndSaveImage(File file, Bitmap bitmap) {
            boolean result = false;
            try {
                FileOutputStream fos = new FileOutputStream(file);
                if (result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                    Log.w("image manager", "Compression success");
                }
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        public Uri addImageToGallery(ContentResolver cr, String imgType, File filepath) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "DormBuddy Map");
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "Saved DormBuddy Map");
            values.put(MediaStore.Images.Media.DESCRIPTION, "This map was saved through dormbuddy");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATA, filepath.toString());

            return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }
    }
}
