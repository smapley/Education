package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Smapley on 2015/4/21.
 */
public class HomeWorkFragment extends Fragment {

    private View contentView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private ListView listView;
    private TextView add;
    private SharedPreferences sp;
    private HomeWorkContentFragment homeWorkContentFragment;
    private AddCourseFragment addCourseFragment;
    private final int GETDATA = 1;
    private List<Map<String, Object>> list = null;
    private final String TAG = "HomeWorkFragment";
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.homework, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initFragment();
        initView(contentView);
        getData();
        return contentView;
    }

    private void initFragment() {
        homeWorkContentFragment = new HomeWorkContentFragment();
        addCourseFragment = new AddCourseFragment();
    }


    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.main_first);
        tag.setText(R.string.homework_tag);
        MyData.BACKSTRING = tag.getText().toString();
        listView = (ListView) view.findViewById(R.id.homework_list);
        add = (TextView) view.findViewById(R.id.title_more);
        if (MyData.UTYPE == 1 || (MyData.UTYPE == 2 && MyData.PROV == 1)) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyData.ADDCOURSETYPE = 3;
                    getActivity().getFragmentManager().beginTransaction().remove(HomeWorkFragment.this)
                            .add(R.id.main_content, addCourseFragment, "addCourseFragment")
                            .addToBackStack(null).commit();
                }
            });

        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    private void getData() {
        try {
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HashMap map = new HashMap<>();
                    map.put("salt", MyData.getKey());
                    map.put("tphone", sp.getString("tphone", ""));
                    try {
                        list = JSON.parseObject(HttpUtils.updata(map, MyData.URL_GETTEASUBJECTLIST2), new TypeReference<List>() {
                        });
                    } catch (Exception e) {

                    }

                    MyData.GRADELIST.clear();
                    if (list != null && !list.isEmpty()) {

                        Log.i(TAG, "--------------------" + MyData.GRADELIST.toString());
                        for (int i = 0; i < list.size(); i++) {

                            try {
                                final String tphone = list.get(i).get("tphone").toString();
                                final String subject = list.get(i).get("subject").toString();
                                MyData.GRADELIST.add(subject);
                                Log.i(TAG, "--------------------" + MyData.GRADELIST.toString());
                                HashMap map2 = new HashMap();
                                map2.put("tphone", tphone);
                                map2.put("salt", MyData.getKey());
                                map2.put("subject", subject);
                                map2.put("msg_type", 4);
                                Map map3 = JSON.parseObject(HttpUtils.updata(map2, MyData.URL_GETZUOYE), new TypeReference<Map>() {
                                });
                                Map map4 = JSON.parseObject(map3.get("data").toString(), new TypeReference<Map>() {
                                });
                                list.get(i).put("content", map4.get("body").toString());
                            } catch (Exception e) {
                            }
                        }
                    }
                    mhandler.obtainMessage(GETDATA, list).sendToTarget();
                }
            }).start();

        } catch (Exception e) {

        }
    }

    public Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETDATA:
                        dialog.dismiss();
                        list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        if (list != null && !list.isEmpty()) {
                            listView.setAdapter(new SimpleAdapter(getActivity(), list, R.layout.layout_homework_item,
                                    new String[]{
                                            "subject", "content"
                                    },
                                    new int[]{
                                            R.id.homework_title, R.id.homework_content
                                    }));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    MyData.SUBJECT = list.get(i).get("subject").toString();
                                    getActivity().getFragmentManager().beginTransaction().remove(HomeWorkFragment.this)
                                            .add(R.id.main_content, homeWorkContentFragment, "homeWorkContentFragment")
                                            .addToBackStack(null).commit();
                                }
                            });
                        }
                        break;


                }
            } catch (Exception e) {
                try {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {

                }
            }
        }
    };
}
