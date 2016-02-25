package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.http.bitmap.GetBitmap;
import com.smapley.education.http.bitmap.GetPic_inSampleSize;
import com.smapley.education.http.bitmap.ImageFileCache;
import com.smapley.education.utils.MyData;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smapley on 2015/5/6.
 */
public class UpPicFragment extends Fragment {

    private final int GETDATA = 1;
    private final int UPDATA = 2;
    private final int CHANGE = 3;
    private View contentView;
    private ProgressDialog dialog;
    private SharedPreferences sp;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private TextView more;
    private TextView demo;
    private ImageView imageView;
    private GetBitmap getBitmap;
    private Uri imageUri = Uri.fromFile(new File(ImageFileCache.getDirectory(),
            "uppic.jpg"));
    private Thread mthread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.uppic, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));

        mthread = new Thread(new Runnable() {
            @Override
            public void run() {
                MyData.imagechangeed = 0;
                while (MyData.imagechangeed == 0) {
                    try {
                        mthread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mhanlder.obtainMessage(CHANGE).sendToTarget();
            }
        });

        getBitmap = new GetBitmap(getActivity());
        initView(contentView);
        getData();
        return contentView;
    }

    private void initView(View view) {

        tag = (TextView) view.findViewById(R.id.title_title);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(MyData.EXAMNAME);
        imageView = (ImageView) view.findViewById(R.id.pupic_pic);
        more = (TextView) view.findViewById(R.id.title_more);
        more.setVisibility(View.VISIBLE);
        more.setText(R.string.xiangce);
        tag.setText(R.string.uppic_tag);
        demo = (TextView) view.findViewById(R.id.pupic_demo);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");// 相片类型
                intent.putExtra("scale", true);
                intent.putExtra("crop", "true");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                getActivity().startActivityForResult(intent, 0);
            }
        });
        MyData.getPhoto = new GetPhoto() {
            @Override
            public void onResult() {
                Log.i("asdf", "---------------asfasd-");

                Bitmap newBitmap = GetPic_inSampleSize.decodeSampledBitmapFromUrl(
                        ImageFileCache.getDirectory() + "uppic.jpg", imageView.getWidth(), imageView.getHeight());
                // 将处理过的图片显示在界面上，并保存到本地
                imageView.setImageBitmap(newBitmap);
                demo.setVisibility(View.GONE);
                File file = new File(ImageFileCache.getDirectory() + "uppic.jpg");
                upData(file);
            }
        };

    }

    private void getData() {
        dialog.setMessage(getString(R.string.connect));
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("eid", MyData.EID);
                mhanlder.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETEXAMPGOTO)).sendToTarget();
            }
        }).start();
    }

    private void upData(final File file) {
        dialog.setMessage(getString(R.string.updata));
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("tphone", sp.getString("tphone", ""));
                map.put("subject", MyData.SUBJECT);
                map.put("eid", MyData.EID);

                Map filemap = new HashMap();
                filemap.put(file.getName(), file);
                mhanlder.obtainMessage(UPDATA, HttpUtils.post(MyData.URL_SENDPHOTO, map, filemap)).sendToTarget();
            }
        }).start();
    }

    private Handler mhanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETDATA:
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            if (Integer.parseInt(map.get("zid").toString()) > 0) {
                                String url = map.get("photo").toString();
                                mthread.start();
                                getBitmap.getBitmap(url, imageView);
                            } else {
                                dialog.dismiss();

                            }

                        } else {
                            dialog.dismiss();
                        }
                        break;
                    case UPDATA:
                        dialog.dismiss();
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2 != null && !map2.isEmpty()) {
                            if (Integer.parseInt(map2.get("newid").toString()) > 0) {
                                Toast.makeText(getActivity(), R.string.uped, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case CHANGE:
                        dialog.dismiss();
                        if (MyData.imagechangeed == 1) {
                            Toast.makeText(getActivity(), R.string.geted, Toast.LENGTH_SHORT).show();
                            demo.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getActivity(), R.string.getfild, Toast.LENGTH_SHORT).show();
                            demo.setVisibility(View.VISIBLE);
                        }
                        MyData.imagechangeed = 0;
                        break;

                }
            } catch (Exception e) {
                try {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e1) {

                }
            }
        }
    };

    public interface GetPhoto {
        void onResult();
    }
}
