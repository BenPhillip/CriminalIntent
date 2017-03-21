package com.criminalintent.gzp.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.MaskFilter;
import android.graphics.Point;
import android.util.Log;

/**
 * Created by Ben on 2017/2/16.
 */

public class PictureUtils {
    public static final String TAG="PictureUtils";
    public static Bitmap getScaledBitmap(String path,int destWidth,int destHeight){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        /* 如果将其设为true的话，在decode时将会返回null,
        通过此设置可以去查询一个bitmap的属性，比如bitmap的长与宽，而不占用内存大小。*/
        BitmapFactory.decodeFile(path, options);


        float srcWidth=options.outWidth;//图片的宽和高
        float srcHeight=options.outHeight;

        int inSampleSize=1;
        /*图片长宽方向缩小的倍数*/
        if(srcHeight>destHeight||srcWidth>destWidth){
            if(srcHeight>destHeight){
                inSampleSize = Math.round(srcHeight / destHeight);
            }else{
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options=new BitmapFactory.Options();
        options.inSampleSize=inSampleSize;

        return BitmapFactory.decodeFile(path, options);
        //将图片解析成Bitmap对象
    }
    //获取屏幕的尺寸，将图片缩小成屏幕大小
    public static Bitmap getScaledBitmap(String path, Activity activity){
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }

}
