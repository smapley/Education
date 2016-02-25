package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.adapter.AddCourseAdapter;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Smapley on 2015/4/20.
 */
public class AddCourseFragment extends Fragment {
    private ListView listView;
    private TextView save;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private final int GETDATA = 1;
    private final int UPDATA = 2;
    private SharedPreferences sp;
    private View contentView;
    private final String TAG = "AddCourseFragment";
    private ImageView check;
    private final int ADDHOMEWORK = 3;
    private final int DELECTHOMEWORK = 4;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.addcourse, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initView(contentView);
        getData();
        return contentView;
    }


    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.addcourse_list);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        tag.setText(R.string.addcourse_tag);
        backtext.setText(MyData.BACKSTRING);
        back.setVisibility(View.VISIBLE);
        MyData.BACKSTRING = getString(R.string.main_first);
        save = (TextView) view.findViewById(R.id.title_more);
        save.setText(R.string.save);
        if (MyData.ADDCOURSETYPE == 1) {
            save.setVisibility(View.VISIBLE);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String str = "";
                            for (int i = 0; i < MyData.GRADELIST.size(); i++) {
                                if (i != 0) {
                                    str = str + ",";
                                    str = str + MyData.GRADELIST.get(i);
                                } else {
                                    str = MyData.GRADELIST.get(i);
                                }
                            }
                            HashMap map = new HashMap();
                            map.put("salt", MyData.getKey());
                            map.put("tphone", sp.getString("tphone", ""));
                            map.put("subject_str", str);
                            mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_UPDATAEXAMSUBJECT)).sendToTarget();
                        }
                    }).start();
                }
            });
        }


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, int position, long l) {
                check = (ImageView) view.findViewById(R.id.addcourse_check);
                final TextView name = (TextView) view.findViewById(R.id.addcourse_name);
                if (MyData.ADDCOURSETYPE == 1) {
                    Log.i("addcourseadapter", "----------------------->>asdfasdfas");
                    if (check.getVisibility() == View.VISIBLE) {
                        check.setVisibility(View.GONE);
                        for (int i = 0; i < MyData.GRADELIST.size(); i++) {
                            if (MyData.GRADELIST.get(i).equals(name.getText().toString())) {
                                MyData.GRADELIST.remove(i);
                            }
                        }
                    } else {
                        check.setVisibility(View.VISIBLE);
                        MyData.GRADELIST.add(name.getText().toString());
                    }
                } else if (MyData.ADDCOURSETYPE == 2) {
                    MyData.SYLL_BACK = 1;
                    MyData.SYLL_SELEC = name.getText().toString();
                    getActivity().getFragmentManager().popBackStack();
                } else if (MyData.ADDCOURSETYPE == 3) {
                    if (check.getVisibility() == View.VISIBLE) {
                        check.setVisibility(View.GONE);
                        dialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap map = new HashMap();
                                map.put("tphone", sp.getString("tphone", ""));
                                map.put("salt", MyData.getKey());
                                map.put("subject", name.getText().toString());
                                mhandler.obtainMessage(DELECTHOMEWORK, HttpUtils.updata(map, MyData.URL_DELTEASUBJECTWITHTPHONE)).sendToTarget();
                            }
                        }).start();
                    } else {
                        check.setVisibility(View.VISIBLE);
                        dialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HashMap map = new HashMap();
                                map.put("tphone", sp.getString("tphone", ""));
                                map.put("salt", MyData.getKey());
                                map.put("subject", name.getText().toString());
                                mhandler.obtainMessage(ADDHOMEWORK, HttpUtils.updata(map, MyData.URL_ADDTEASUBJECT)).sendToTarget();
                            }
                        }).start();
                    }
                }

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
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETSUBJECTLIST)).sendToTarget();
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
                        List list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        if (list != null && !list.isEmpty()) {

                            listView.setAdapter(new AddCourseAdapter(getActivity(), list));

                        }

                        break;
                    case UPDATA:
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            if (Integer.parseInt(map.get("count").toString()) > 0) {
                                Toast.makeText(getActivity(), R.string.saveed, Toast.LENGTH_SHORT).show();
                                getActivity().getFragmentManager().popBackStack();
                            }
                        }
                        break;
                    case ADDHOMEWORK:
                        dialog.dismiss();
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2 != null && !map2.isEmpty()) {
                            if (Integer.parseInt(map2.get("newid").toString()) > 0) {
                                getActivity().getFragmentManager().popBackStack();
                            }
                        }
                        break;
                    case DELECTHOMEWORK:
                        dialog.dismiss();
                        Map map3 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map3 != null && !map3.isEmpty()) {
                            if (Integer.parseInt(map3.get("count").toString()) > 0) {
                                getActivity().getFragmentManager().popBackStack();
                            } else {
                                Toast.makeText(getActivity(), R.string.count0, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                }
            } catch (Exception e) {
                try {
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e2) {

                }
            }
        }
    };
}
