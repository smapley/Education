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
 * Created by Smapley on 2015/4/26.
 */
public class BoundStudentFragment extends Fragment {

    private View contentView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private ListView listView;
    private final int GETDATA = 1;
    private final int BOUND = 2;
    private SharedPreferences sp;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.boundstudent, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));
        initView(contentView);
        getData();
        return contentView;
    }


    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        tag = (TextView) view.findViewById(R.id.title_title);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag.setText(R.string.boundstudent_tag);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.bound_tag);
        listView = (ListView) view.findViewById(R.id.boundstudnet_list);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }

    private void getData() {
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("phone", MyData.BOUNDPHONE);
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETSTULIST2)).sendToTarget();

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
                        final List<Map<String, Object>> list = JSON.parseObject(msg.obj.toString(), new TypeReference<List<Map<String, Object>>>() {
                        });
                        if (list != null && !list.isEmpty()) {
                            listView.setAdapter(new SimpleAdapter(getActivity(), list, R.layout.layout_boundstudent_item,
                                    new String[]{
                                            "name"
                                    },
                                    new int[]{
                                            R.id.boundstudent_name
                                    }));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    dialog.show();
                                    final String stuname = list.get(i).get("name").toString();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            HashMap map = new HashMap();
                                            map.put("salt", MyData.getKey());
                                            map.put("jphone", sp.getString("jphone", ""));
                                            map.put("tphone", MyData.BOUNDPHONE);
                                            map.put("stu", stuname);
                                            mhandler.obtainMessage(BOUND, HttpUtils.updata(map, MyData.URL_BANGSTU)).sendToTarget();

                                        }
                                    }).start();
                                }
                            });
                        }

                        break;
                    case BOUND:
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            if (Integer.parseInt(map.get("count").toString()) > 0) {
                                Toast.makeText(getActivity(), R.string.boundsend, Toast.LENGTH_SHORT).show();
                                getActivity().getFragmentManager().popBackStack();
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
