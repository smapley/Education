package com.smapley.education.http.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.smapley.education.utils.MyData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author Administrator
 */
public class GetBitmap {
    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;

    public GetBitmap(Context context) {
        memoryCache = new ImageMemoryCache(context);
        fileCache = new ImageFileCache();
    }

    /**
     * 获得一张图片,从三个地方获取,首先是内存缓存,然后是文件缓存,最后从网络获取 **
     */
    public void getBitmap(String url, ImageView imageView) {
        // TODO Auto-generated method stub
        // 从内存缓存中获取图片
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if (result == null) {
            System.out.println(url + "从内存获取失败");
            // 文件缓存中获取
            result = fileCache.getImage(url);
            System.out.println(url + "从文件获取......");
            if (result == null) {
                //imageView.setImageResource(R.drawable.logo);
                System.out.println(url + "从文件获取失败");
                // 从网络获取
                GetPic_AsyncTask task = new GetPic_AsyncTask(imageView, url);
                task.execute(url);
                System.out.println(url + "从网络获取......");

            } else {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
                imageView.setImageBitmap(result);
                System.out.println(url + "从文件获取成功");
                MyData.imagechangeed = 1;

            }
        } else {
            imageView.setImageBitmap(result);
            System.out.println(url + "从内存获取成功");
            MyData.imagechangeed = 1;
        }
    }

    /**
     * 网络获取图片
     */
    public class GetPic_AsyncTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageview;
        private String url;

        GetPic_AsyncTask(ImageView imageview, String url) {
            this.imageview = imageview;
            this.url = url;
        }

        // 在后台加载图片。
        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(MyData.URL_FILE + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                //connection.setDoOutput(true);
                Log.i("getpic", "------------->>" + url);
                int code = connection.getResponseCode();
                Log.i("getpic", "------------->>" + code);
                if (code == 200) {


                    System.out.println("从web获取" + params[0]);

                    InputStream inputStream = connection.getInputStream();
                    ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
                    int ch;
                    while ((ch = inputStream.read()) != -1) {
                        bytestream.write(ch);
                    }
                    byte imgdata[] = bytestream.toByteArray();
                    bytestream.close();
                    final Bitmap bitmap = new GetPic_inSampleSize()
                            .decodeSampledBitmapFromByteArray(imgdata,
                                    imageview.getWidth(), imageview.getHeight());
                    Log.i("getpic", "------------->>1");

                    return bitmap;

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Log.i("getpic", "------------->>1");

            if (result != null) {
                Log.i("getpic", "------------->>1");

                imageview.setImageBitmap(result);
                fileCache.saveBitmap(result, url);
                memoryCache.addBitmapToCache(url, result);
                System.out.println(url + "从网络获取成功");
                MyData.imagechangeed = 1;
            } else {
                System.out.println(url + "从网络获取失败");
                MyData.imagechangeed = -1;
            }

        }
    }
}
