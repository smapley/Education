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
 * Created by Smapley on 2015/4/19.
 */
public class AddNoticeFragment extends Fragment {
    private LinearLayout back;
    private TextView backtext;
    private TextView save;
    private EditText title;
    private EditText content;
    private TextView tag;
    private String titleString;
    private String contentString;
    private View contentView;
    private SharedPreferences sp;
    private final int UPDATA = 1;
    private final int SENDMSN = 2;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.addnotice, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));
        initView(contentView);
        return contentView;
    }


    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        back.setVisibility(View.VISIBLE);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        backtext.setText(MyData.BACKSTRING);
        MyData.BACKSTRING = getString(R.string.main_first);

        save = (TextView) view.findViewById(R.id.title_more);
        save.setText(R.string.save);
        save.setVisibility(View.VISIBLE);
        title = (EditText) view.findViewById(R.id.addnotice_title);
        content = (EditText) view.findViewById(R.id.addnotice_content);
        tag = (TextView) view.findViewById(R.id.title_title);
        switch (MyData.SRC) {
            case ListViewFragment.NOTICE:
                tag.setText(R.string.addnotice_tag);
                break;
            case ListViewFragment.HONOR:
                tag.setText(R.string.addhonor_tag);
                break;
            case ListViewFragment.LEAVE:
                tag.setText(R.string.addleave_tag);
                break;
            case ListViewFragment.ABSENT:
                tag.setText(R.string.addabsent_tag);
                break;
            case ListViewFragment.HOMEWORK:
                tag.setText(R.string.addhomework_tag);
                break;

        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().getFragmentManager().popBackStack();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HashMap map = new HashMap();
                        map.put("tphone", sp.getString("tphone", ""));
                        map.put("salt", MyData.getKey());
                        if (sp.getInt("utype", -1) == 2) {
                            map.put("sender", sp.getString("name", ""));
                        } else {
                            map.put("sender", getString(R.string.teacher));
                        }
                        map.put("subject", MyData.SUBJECT);
                        map.put("msg_type", MyData.SRC);
                        map.put("title", title.getText().toString());
                        if (MyData.SRC == ListViewFragment.LEAVE) {
                            map.put("content", content.getText().toString());
                        } else {
                            map.put("content", content.getText().toString() + " [" + map.get("sender").toString() + "]");
                        }
                        contentString = content.getText().toString();
                        mhandler.obtainMessage(UPDATA, HttpUtils.updata(map, MyData.URL_ADDMSG)).sendToTarget();
                    }
                }).start();
            }
        });
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case UPDATA:
                        dialog.dismiss();
                        Map map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (!map.isEmpty() && map != null) {
                            if (Integer.parseInt(map.get("newid").toString()) > 0) {
                                if (MyData.SRC == ListViewFragment.NOTICE) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.tips);
                                    builder.setMessage(R.string.sendnotice);
                                    builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            getActivity().getFragmentManager().popBackStack();
                                        }
                                    });
                                    builder.setPositiveButton(R.string.Okay, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    HashMap map1 = new HashMap();
                                                    map1.put("salt", MyData.getKey());
                                                    map1.put("phone", sp.getString("phone", ""));
                                                    map1.put("content", contentString);
                                                    mhandler.obtainMessage(SENDMSN, HttpUtils.updata(map1, MyData.URL_SENDMSG)).sendToTarget();
                                                }
                                            }).start();
                                        }
                                    });
                                    builder.create().show();
                                } else {
                                    Toast.makeText(getActivity(), R.string.added, Toast.LENGTH_SHORT).show();
                                    getActivity().getFragmentManager().popBackStack();
                                }

                            }
                        }
                        break;
                    case SENDMSN:
                        Map map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2 != null && !map2.isEmpty()) {
                            if (Integer.parseInt(map2.get("count").toString()) >= 0) {
                                Toast.makeText(getActivity(), R.string.sendsucc, Toast.LENGTH_SHORT).show();
                                getActivity().getFragmentManager().popBackStack();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
