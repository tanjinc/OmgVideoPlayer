package com.tanjinc.playermanager.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by tanjinc on 16-10-19.
 * 自定义一个简单的网络图片加载类
 */
public class ImageLoader {

    private int mImageViewWidth, mImageViewHeight;
    private String mUrl;
    private ImageView mImageView;
    private static HashMap<String,SoftReference<Bitmap>> mImageCache =null;

    private static ImageLoader sIntance;

    private ImageLoader() {
        mImageCache =new HashMap<String,SoftReference<Bitmap>>();
    }

    public static ImageLoader getInstance() {
        if (sIntance == null) {
            sIntance = new ImageLoader();
        }
        return sIntance;
    }

    public void loadImage(String url, ImageView imageView, int width, int height) {
        mUrl = url;
        mImageView = imageView;
        mImageViewWidth = width;
        mImageViewHeight = height;
        new ImageAsyncLoader().execute();
    }

    private class ImageAsyncLoader extends AsyncTask<String, Void, Bitmap> {

        @Override
        public Bitmap doInBackground(String... params) {
            Bitmap bmp = null;

            //在内存缓存中，则返回Bitmap对象
            if (mImageCache.containsKey(mUrl)) {
                SoftReference<Bitmap> reference = mImageCache.get(mUrl);
                bmp = reference.get();
                if (bmp != null) {
                    return bmp;
                }
            }

            try {
                URL myurl = new URL(mUrl);
                // 获得连接
                HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
                conn.setConnectTimeout(6000);//设置超时
                conn.setDoInput(true);
                conn.setUseCaches(true);//不缓存
                conn.connect();
                InputStream is = conn.getInputStream();//获得图片的数据流
                bmp = BitmapFactory.decodeStream(is);

                mImageCache.put(mUrl, new SoftReference<Bitmap>(bmp));
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap == null) {
                return;
            }
            mImageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, mImageViewWidth, mImageViewHeight));
            mImageView = null;
        }
    }
}
