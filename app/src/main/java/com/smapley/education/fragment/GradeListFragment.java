package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
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
 * Created by Smapley on 2015/4/21.
 */
public class GradeListFragment extends Fragment {

    private View contentView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private TextView more;
    private ListView listView;
    private SharedPreferences sp;
    private AddGradefragment addGradefragment;
    private GradeContentFragment gradeContentFragment;
    private UpPicFragment upPicFragment;
    private final int GETDATA = 1;

    private final int XUEHAO = 0;
    private final int CHENGJI = 1;
    private int ORD = XUEHAO;
    private ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.gradelist, container, false);
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
        addGradefragment = new AddGradefragment();
        gradeContentFragment = new GradeContentFragment();

        upPicFragment=new UpPicFragment();
    }

    private void initView(View view) {

        tag = (TextView) view.findViewById(R.id.title_title);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.gradeitem_tag);
        listView = (ListView) view.findViewById(R.id.gradelist_list);
        more = (TextView) view.findViewById(R.id.title_more);
        more.setVisibility(View.VISIBLE);
        more.setText(R.string.more);
        tag.setText(MyData.EXAMNAME);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View view = inflater.inflate(R.layout.layout_gradelist_dialog, null);
                builder.setView(view);
                final Dialog dialog = builder.create();

                view.findViewById(R.id.gradelist_item1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ORD = XUEHAO;
                        getData();
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.gradelist_item2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ORD = CHENGJI;
                        getData();
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.gradelist_item3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyData.FIRSTADDGRADE = false;

                        getActivity().getFragmentManager().beginTransaction().remove(GradeListFragment.this)
                                .add(R.id.main_content, addGradefragment, "addGradefragment")
                                .addToBackStack(null).commit();
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.gradelist_item4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyData.FIRSTADDGRADE = false;

                        getActivity().getFragmentManager().beginTransaction().remove(GradeListFragment.this)
                                .add(R.id.main_content, upPicFragment, "upPicFragment")
                                .addToBackStack(null).commit();
                        dialog.dismiss();
                    }
                });
                view.findViewById(R.id.gradelist_item5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
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
                map.put("eid", MyData.EID);
                map.put("ord", ORD);
                mhanlder.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETSCORELIST)).sendToTarget();
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
                        dialog.dismiss();
                        final List list = JSON.parseObject(msg.obj.toString(), new TypeReference<List>() {
                        });
                        if (list != null && !list.isEmpty()) {
                            listView.setAdapter(new SimpleAdapter(getActivity(), list, R.layout.layout_gradelist_item,
                                    new String[]{"name", "point"
                                    },
                                    new int[]{
                                            R.id.gradelist_name, R.id.gradelist_num
                                    }));
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    MyData.STUNAME = ((Map) list.get(i)).get("name").toString();
                                    MyData.STUGRADE = Integer.parseInt(((Map) list.get(i)).get("point").toString());
                                    getActivity().getFragmentManager().beginTransaction().hide(GradeListFragment.this)
                                            .add(R.id.main_content, gradeContentFragment, "gradeContentFragment")
                                            .addToBackStack(null).commit();
                                }
                            });
                            break;
                        }
                }
            } catch (Exception e) {
                try{
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                }catch (Exception e1){

                }
            }
        }
    };
}
