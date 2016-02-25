package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
public class HomeWorkContentFragment extends Fragment {

    private View contentView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private TextView add;
    private ListView listView;
    private AddNoticeFragment addNoticeFragment;
    private SharedPreferences sp;
    private final int GETDATA = 1;
    private final int DELECT = 2;
    private String subject;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.homeworkcontent, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        subject = MyData.SUBJECT;
        initFragment();
        initView(contentView);
        getData();

        return contentView;

    }

    private void initFragment() {
        addNoticeFragment = new AddNoticeFragment();
    }

    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.homework_tag);
        tag = (TextView) view.findViewById(R.id.title_title);
        add = (TextView) view.findViewById(R.id.title_more);
        listView = (ListView) view.findViewById(R.id.homeworkcontent_list);
        tag.setText(subject);
        MyData.BACKSTRING = tag.getText().toString();
        if (MyData.UTYPE == 1 || (MyData.UTYPE == 2 && MyData.PROV == 1)) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyData.SRC = ListViewFragment.HOMEWORK;
                    getActivity().getFragmentManager().beginTransaction().remove(HomeWorkContentFragment.this)
                            .add(R.id.main_content, addNoticeFragment, "addNoticeFragment")
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
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                map.put("tphone", sp.getString("tphone", ""));
                map.put("msg_type", 4);
                map.put("subject", subject);
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETZUOYELIST)).sendToTarget();

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
                        final List<Map<String, Object>> list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        listView.setAdapter(new SimpleAdapter(getActivity(), list, R.layout.layout_homework_item,
                                new String[]{
                                        "title", "body"
                                },
                                new int[]{
                                        R.id.homework_title, R.id.homework_content
                                }));
                        if (MyData.UTYPE == 1 || (MyData.UTYPE == 2 && MyData.PROV == 1)) {
                            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.delectmag);
                                    builder.setPositiveButton(R.string.delect, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int position) {
                                            dialog.show();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    HashMap map = new HashMap();
                                                    map.put("salt", MyData.getKey());
                                                    map.put("mid", ((Map) list.get(i)).get("mid").toString());
                                                    mhandler.obtainMessage(DELECT, HttpUtils.updata(map, MyData.URL_DELMSG)).sendToTarget();
                                                }
                                            }).start();
                                        }
                                    });
                                    builder.setNegativeButton(R.string.cancel, null);
                                    builder.create().show();
                                    return false;
                                }
                            });

                        }
                        break;
                    case DELECT:
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (Integer.parseInt(map.get("count").toString()) > 0) {
                            Toast.makeText(getActivity(), R.string.delected, Toast.LENGTH_SHORT).show();
                            getData();
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
