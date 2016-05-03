package com.smapley.education.fragment;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.adapter.AddGradeAdapter;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Smapley on 2015/4/25.
 */
public class AddGradefragment extends Fragment {

    private View contentView;
    private TextView save;
    private ListView listView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private SharedPreferences sp;
    private AddGradeAdapter addGradeAdapter;
    private final int GETDATA = 1;
    private final int UPDATA = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.addgrade, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        initView(contentView);
        getData();
        return contentView;
    }

    private void initView(View view) {
        save = (TextView) view.findViewById(R.id.title_more);
        save.setText(R.string.save);
        save.setVisibility(View.VISIBLE);
        listView = (ListView) view.findViewById(R.id.addgrade_list);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(MyData.EXAMNAME);
        tag = (TextView) view.findViewById(R.id.title_title);
        tag.setText(R.string.addgrade_tag);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upData();
            }
        });


    }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("eid", MyData.EID);
                map.put("ord", 0);
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETSCORELIST)).sendToTarget();
            }
        }).start();
    }

    private void upData() {

        final List<Map> list = addGradeAdapter.getData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String scoreid = "";
                String point = "";
                for (int i = 0; i < list.size(); i++) {
                    scoreid = scoreid + list.get(i).get("score_id").toString();
                    point = point + list.get(i).get("point").toString();
                    if (i + 1 < list.size()) {
                        scoreid = scoreid + ",";
                        point = point + ",";
                    }
                }
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("scoreid_str", scoreid);
                map.put("point_str", point);
                mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_UPDATESCOREBATCH)).sendToTarget();

            }
        }).start();
        for (int i = 0; i < list.size(); i++) {

            System.out.println("the text of " + i + "'s EditText：----------->" + list.get(i).get("point").toString()
            );
        }
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETDATA:
                        List list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        if (list != null && !list.isEmpty()) {
                            addGradeAdapter = new AddGradeAdapter(getActivity(), list);
                            listView.setAdapter(addGradeAdapter);
                        }
                        break;
                    case UPDATA:
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            if (Integer.parseInt(map.get("count").toString()) > 0) {
                                Toast.makeText(getActivity(), R.string.saveed, Toast.LENGTH_SHORT).show();
                                getActivity().getFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getActivity(), R.string.count0, Toast.LENGTH_SHORT).show();
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
