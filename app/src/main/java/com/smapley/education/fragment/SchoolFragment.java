package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
 * Created by Smapley on 2015/4/18.
 */
public class SchoolFragment extends Fragment {
    private EditText item1;
    private EditText item2;
    private EditText item3;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private TextView save;
    private SharedPreferences sp;
    private View contenView;
    private final int GETDATA = 1;
    private final int UPDATA = 2;
    private String provinceString;
    private String schoolString;
    private String classesString;
    private ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contenView = inflater.inflate(R.layout.school, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initView(contenView);
        setData();
        return contenView;
    }


    private void initView(View view) {
        item1 = (EditText) view.findViewById(R.id.school_item1);
        item2 = (EditText) view.findViewById(R.id.school_item2);
        item3 = (EditText) view.findViewById(R.id.school_item3);
        save = (TextView) view.findViewById(R.id.title_more);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.set_tag);
        tag.setText(R.string.school_tag);
        save.setVisibility(View.VISIBLE);
        save.setText(R.string.save);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        provinceString = item1.getText().toString();
                                        schoolString = item2.getText().toString();
                                        classesString = item3.getText().toString();

                                        if (provinceString != null && !provinceString.equals("")) {
                                            if (schoolString != null && !schoolString.equals("")) {
                                                if (classesString != null && !classesString.equals("")) {
                                                    dialog.show();
                                                    new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            HashMap map = new HashMap();
                                                            map.put("salt", MyData.getKey());
                                                            map.put("phone", sp.getString("phone", ""));
                                                            map.put("city", provinceString);
                                                            map.put("school_name", schoolString);
                                                            map.put("class_name", classesString);
                                                            mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_UPDATESTINFO)).sendToTarget();
                                                        }
                                                    }).start();
                                                } else {
                                                    Toast.makeText(getActivity(), R.string.school_toast3, Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), R.string.school_toast2, Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(), R.string.school_toast1, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

        );

    }

    private void setData() {

        item1.setText(sp.getString("city", ""));
        item2.setText(sp.getString("school_name", ""));
        item3.setText(sp.getString("class_name", ""));

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("phone", sp.getString("phone", ""));
                map.put("salt", MyData.getKey());
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETSTINFO)).sendToTarget();
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
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            item1.setText(map.get("city").toString());
                            item2.setText(map.get("school_name").toString());
                            item3.setText(map.get("class_name").toString());
                        }
                        break;
                    case UPDATA:
                        dialog.dismiss();
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2.get("count").toString().equals("1")) {
                            MyData.UTYPECHANGED = true;
                            Toast.makeText(getActivity(), R.string.school_toast0, Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("city", provinceString);
                            editor.putString("school_name", schoolString);
                            editor.putString("class_name", classesString);
                            editor.commit();
                        } else {
                            Toast.makeText(getActivity(), R.string.count0, Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
            }
        }
    };

}
