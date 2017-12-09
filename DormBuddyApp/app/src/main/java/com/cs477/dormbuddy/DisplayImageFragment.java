package com.cs477.dormbuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.StreamEncoder;
import com.bumptech.glide.load.resource.file.FileToStreamDecoder;
import com.caverock.androidsvg.SVG;

import java.io.InputStream;

public class DisplayImageFragment extends DialogFragment {
    static final String DISPLAY_IMAGE_TAG = "DisplayImageTag";
    boolean isLocal = false;
    private GenericRequestBuilder<Uri, InputStream, SVG, PictureDrawable> requestBuilder;
    int imageResource;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_display_image, null);
        ImageView displayImage = (ImageView) v.findViewById(R.id.displayImage);
        //retrieves map from link
        Uri uri = Uri.parse("http://housingplans.gmu.edu/suites/CommonwealthDominion/Commonwealth/Commonwealth.svg");
        requestBuilder = Glide.with(this)
                .using(Glide.buildStreamModelLoader(Uri.class, this.getContext()), InputStream.class)
                .from(Uri.class)
                .as(SVG.class)
                .transcode(new SvgDrawableTranscoder(), PictureDrawable.class)
                .sourceEncoder(new StreamEncoder())
                .cacheDecoder(new FileToStreamDecoder<SVG>(new SvgDecoder()))
                .decoder(new SvgDecoder())
                .placeholder(R.drawable.empty)
                .error(R.drawable.empty) //presumably a loading icon -- Loading svg
                .animate(android.R.anim.fade_in)
                .listener(new SvgSoftwareLayerSetter<Uri>());
        requestBuilder
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                // SVG cannot be serialized so it's not worth to cache it
                .load(uri)
                .into(displayImage);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        /*
        if (! isLocal) { //if the element is a server/user uploaded photo
            Bitmap image = getArguments().getParcelable("image");
            if (image != null) {
                displayImage.setImageBitmap(image);
            }
        } else { //else if its a locally stored large photo
            imageResource = getArguments().getInt("drawable");
            displayImage.setImageResource(imageResource);
            //save campus map
            builder.setPositiveButton("Save Photo", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    );
                    Bitmap bmp = BitmapFactory.decodeResource(getResources(), imageResource);
                    MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, "DormBuddy" , "stored dormBuddy image");
                    Toast.makeText(getContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }*/
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

}
