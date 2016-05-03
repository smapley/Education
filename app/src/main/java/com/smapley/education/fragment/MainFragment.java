package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Created by Smapley on 2015/4/18.
 */
public class MainFragment extends Fragment implements View.OnClickListener {

    private TextView notice;
    private TextView change;
    private ImageView item1;
    private ImageView item2;
    private ImageView item3;
    private ImageView item4;
    private ImageView item5;
    private ImageView item6;
    private final int TEACHER = 1;
    private final int PARENTS = 0;
    private final int STUDENT = 2;
    private TextView tag;
    private View countView;
    private ListViewFragment listViewFragment;
    /**
     * 是否退出
     */
    private static Boolean isExit = false;
    private SharedPreferences sp;
    private final int GETNOTICE = 3;
    private GradeFragment gradeFragment;
    private SyllabusFragment syllabusFragment;
    private HomeWorkFragment homeWorkFragment;
    private int utype;
    private int childrenSize = 1;
    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        countView = inflater.inflate(R.layout.main_layout, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initFragment();
        initVIew(countView);
        initData();
        return countView;
    }

    public void onchange() {
        initFragment();
        initVIew(countView);

        getData(MyData.UTYPE);

    }

    private void getData(final int type) {
        dialog.setMessage(getString(R.string.connect));
        new Thread(new Runnable() {
            /**
             * 0=家长,1=教师,2=学生
             */
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("salt", MyData.getKey());
                String url = "";
                switch (type) {
                    case PARENTS:
                        map.put("jphone", sp.getString("jphone", ""));
                        url = MyData.URL_GETSTUTABLE;
                        break;
                    case TEACHER:
                        map.put("phone", sp.getString("tphone", ""));
                        url = MyData.URL_GETSTINFO;
                        break;
                    case STUDENT:
                        map.put("stuphone", sp.getString("stuphone", ""));
                        url = MyData.URL_GETSTUTABLEEITHSTUPHONE;
                        break;
                }

                mhandler.obtainMessage(type, HttpUtils.updata(map, url)).sendToTarget();
            }
        }).start();
    }

    private void initData() {

        MyData.BACKSTRING = getString(R.string.main_first);
        utype = sp.getInt("utype", -1);
        if (utype == 1) {
            tag.setText(sp.getString("class_name", ""));
        } else if (utype == 0 || utype == 2) {
            tag.setText(sp.getString("classname", ""));
        } else {
            tag.setText(R.string.tag);
        }
        MyData.UTYPE = utype;
        MyData.PROV = Integer.parseInt(sp.getString("prov", "-1"));
        childrenSize = Integer.parseInt(sp.getString("childrensize", "0"));
        if (MyData.UTYPE == 0 && childrenSize > 1) {
            change.setVisibility(View.VISIBLE);
            change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MyData.NOWCHILDREN < childrenSize) {
                        MyData.NOWCHILDREN++;
                    } else {
                        MyData.NOWCHILDREN = 1;
                    }
                    Map<String, Object> map3 = JSON.parseObject(sp.getString("children", ""), new TypeReference<List<Map<String, Object>>>() {
                    }).get(MyData.NOWCHILDREN - 1);
                    for (Map.Entry<String, Object> entry : map3.entrySet()) {
                        SharedPreferences.Editor editor2 = sp.edit();
                        editor2.putString(entry.getKey(), entry.getValue().toString());
                        editor2.commit();
                    }
                    initData();
                    Toast.makeText(getActivity(), getString(R.string.nowchildren) + sp.getString("name", ""), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            change.setVisibility(View.GONE);
        }

        getNotice();
    }

    private void initFragment() {
        listViewFragment = new ListViewFragment();
        gradeFragment = new GradeFragment();
        syllabusFragment = new SyllabusFragment();
        homeWorkFragment = new HomeWorkFragment();
    }

    private void getNotice() {
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("tphone", sp.getString("tphone", ""));
                map.put("salt", MyData.getKey());
                map.put("msg_type", 0);
                mhandler.obtainMessage(GETNOTICE, HttpUtils.updata(map, MyData.URL_GETNEWMSG)).sendToTarget();
            }
        }).start();


    }

    private void initVIew(View view) {

        notice = (TextView) view.findViewById(R.id.main_notice);
        item1 = (ImageView) view.findViewById(R.id.main_item1);
        item2 = (ImageView) view.findViewById(R.id.main_item2);
        item3 = (ImageView) view.findViewById(R.id.main_item3);
        item4 = (ImageView) view.findViewById(R.id.main_item4);
        item5 = (ImageView) view.findViewById(R.id.main_item5);
        item6 = (ImageView) view.findViewById(R.id.main_item6);
        change = (TextView) view.findViewById(R.id.title_more);
        change.setText(R.string.change);

        tag = (TextView) view.findViewById(R.id.title_title);

        notice.setOnClickListener(this);
        notice.setMovementMethod(ScrollingMovementMethod.getInstance());
        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
        item5.setOnClickListener(this);
        item6.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.main_item1:
                getActivity().getFragmentManager().beginTransaction().hide(this)
                        .add(R.id.main_content, gradeFragment, "gradeFragment")
                        .addToBackStack(null).commit();
                break;
            case R.id.main_item2:
                MyData.SYLL_BACK = 0;
                getActivity().getFragmentManager().beginTransaction().hide(this)
                        .add(R.id.main_content, syllabusFragment, "syllabusFragment")
                        .addToBackStack(null).commit();
                break;
            case R.id.main_item3:
                getActivity().getFragmentManager().beginTransaction().hide(this)
                        .add(R.id.main_content, homeWorkFragment, "homeWorkFragment")
                        .addToBackStack(null).commit();
                break;
            case R.id.main_item4:
                MyData.SRC = ListViewFragment.ABSENT;
                getActivity().getFragmentManager().beginTransaction().hide(this)
                        .add(R.id.main_content, listViewFragment, "listViewFragment")
                        .addToBackStack(null).commit();
                break;
            case R.id.main_item5:
                MyData.SRC = ListViewFragment.HONOR;
                getActivity().getFragmentManager().beginTransaction().hide(this)
                        .add(R.id.main_content, listViewFragment, "listViewFragment")
                        .addToBackStack(null).commit();
                //  startActivity(new Intent(getActivity(), PieChartBuilder.class));

                break;
            case R.id.main_item6:
                MyData.SRC = ListViewFragment.LEAVE;
                getActivity().getFragmentManager().beginTransaction().hide(this)
                        .add(R.id.main_content, listViewFragment, "listViewFragment")
                        .addToBackStack(null).commit();
                //  startActivity(new Intent(getActivity(), XYChartBuilder.class));

                break;
            case R.id.main_notice:
                MyData.SRC = ListViewFragment.NOTICE;
                getActivity().getFragmentManager().beginTransaction()
                        .replace(R.id.main_content, listViewFragment, "listViewFragment")
                        .addToBackStack(null).commit();
                break;

        }

    }


    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case GETNOTICE:
                        dialog.dismiss();
                        Map<String, Object> map = JSON.parseObject(msg.obj.toString(), new TypeReference<Map<String, Object>>() {
                        });
                        if (map != null && !map.isEmpty()) {
                            if (!map.get("content").toString().equals("")) {
                                notice.setText(map.get("content").toString());
                            }

                        }

                        break;
                    case TEACHER:
                    case STUDENT:
                        dialog.dismiss();
                        Map<String, Object> map2 = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        if (map2 != null && !map2.isEmpty()) {
                            for (Map.Entry<String, Object> entry : map2.entrySet()) {

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString(entry.getKey(), entry.getValue().toString());
                                editor.commit();
                            }
                        }
                        initData();
                        break;
                    case PARENTS:
                        dialog.dismiss();
                        String childern = msg.obj.toString();
                        List<Map<String, Object>> list = JSON.parseObject(childern, new TypeReference<List>() {
                        });
                        if (list != null && !list.isEmpty()) {
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("childrensize", list.size() + "");
                            editor.putString("children", childern);
                            editor.commit();
                            Map<String, Object> map3 = JSON.parseObject(sp.getString("children", ""), new TypeReference<List<Map<String, Object>>>() {
                            }).get(0);
                            for (Map.Entry<String, Object> entry : map3.entrySet()) {

                                SharedPreferences.Editor editor2 = sp.edit();
                                editor2.putString(entry.getKey(), entry.getValue().toString());
                                editor2.commit();
                            }
                        }
                        initData();
                        break;


                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
            }
        }
    };


}
