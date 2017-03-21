package com.criminalintent.gzp.criminalintent;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Ben on 2017/2/16.
 */

public class ImageFragment extends DialogFragment {
    private ImageView mImageView;
    private static String EXTRA_IMAGE_PATH="com.criminalintent.gzp.criminalintent.image_path";

    public static ImageFragment newInstance(String imagePath) {

        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH,imagePath);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE,0);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        mImageView=new ImageView(getActivity());
        String path=(String)getArguments().getSerializable(EXTRA_IMAGE_PATH);
        Bitmap image = PictureUtils.getScaledBitmap(path,getActivity());
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x4c000000));
        //背景透明
        mImageView.setImageBitmap(image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FragmentManager fm=getActivity().getSupportFragmentManager();
//                Fragment fragment=fm.findFragmentByTag(CrimeFragment.DIALOG_IMAGE);
//                fm.beginTransaction().remove(fragment).commit();
                dismiss();
            }
        });
        return mImageView;
    }

}
