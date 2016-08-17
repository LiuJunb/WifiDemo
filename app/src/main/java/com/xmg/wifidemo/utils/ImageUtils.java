package com.xmg.wifidemo.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;

/**
 * @des:
 * @author: 刘军
 * @version: 1.0.0
 * @date: 2016/8/13 18:03
 * @see {@link }
 */
public class ImageUtils {

    public  static void  disPlayImage(Context activity, String url, ImageView imageView){
        Glide.with(activity).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageView);
    }

    public  static void  disPlayImageNoCache(Context activity, String url, ImageView imageView, RequestListener requestListener){
            Glide
                .with(activity)
                .load(url)
                .listener(requestListener)
                .into(imageView);
    }
}
