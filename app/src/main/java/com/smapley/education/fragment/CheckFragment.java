package com.smapley.education.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Smapley on 2015/4/26.
 */
public class CheckFragment extends Fragment {
    private TextView phone;
    private TextView check;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private View contentView;
    private SharedPreferences sp;
    private final int GETDATA = 1;
    private final int CHECK = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.check, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        initView(contentView);
        getData();
        return contentView;

    }

    private void initView(View view) {
        phone = (TextView) view.findViewById(R.id.check_phone);
        check = (TextView) view.findViewById(R.id.check_check);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.student_tag);
        tag.setText(R.string.check_tag);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map = new HashMap();
                        map.put("salt", MyData.getKey());
                        map.put("sid", MyData.SID);
                        map.put("status", 2);
                        mhandler.obtainMessage(CHECK, HttpUtils.updata(map, MyData.URL_SETSTAT)).sendToTarget();
                    }
                }).start();
            }
        });
    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("sid", MyData.SID);
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETSTUTABLEROW)).sendToTarget();

            }
        }).start();
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETDATA:
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            phone.setText(map.get("jphone").toString());
                        }
                        break;
                    case CHECK:
                        Map map1 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map1 != null && !map1.isEmpty()) {
                            if (Integer.parseInt(map1.get("count").toString()) > 0) {
                                getActivity().getFragmentManager().popBackStack();
                                Toast.makeText(getActivity(), R.string.checked, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
            }
        }
    };
}

