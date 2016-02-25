package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.chart.AChartExamole;
import com.smapley.education.chart.PathFragment;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Smapley on 2015/4/26.
 */
public class GradeContentFragment extends Fragment {
    private View contentView;
    private SharedPreferences sp;
    private final int GETDATA = 1;
    private final int GONEXT = 2;
    private TextView tag;
    private TextView more;
    private LinearLayout back;
    private TextView backtext;
    private TextView item1;
    private TextView item2;
    private TextView item3;
    private TextView item4;
    private TextView item5;
    private TextView item6;
    private TextView item7;
    private TextView item8;
    private TextView item9;
    private ProgressDialog dialog;
    private int rank1 = 0;
    private int rank2 = 0;
    private int rank3 = 0;
    private int rank4 = 0;
    private int rank5 = 0;
    private int rank6 = 0;
    private PathFragment pathFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.gradecontent, container, false);
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
        pathFragment = new PathFragment();
    }

    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(MyData.EXAMNAME);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        more = (TextView) view.findViewById(R.id.title_more);
        more.setText(R.string.more);
        more.setVisibility(View.VISIBLE);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.caidan);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View contentView = inflater.inflate(R.layout.layout_gradecontent_dialog, null);
                builder.setView(contentView);
                final Dialog dialogs = builder.create();
                dialogs.show();
                contentView.findViewById(R.id.gradecontent_dialog1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogs.dismiss();

                        dialog.show();
                        final String stuname = MyData.STUNAME;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                HashMap map = new HashMap();
                                map.put("salt", MyData.getKey());
                                map.put("tphone", sp.getString("tphone", ""));
                                map.put("subject", MyData.GRADENAME);
                                List list = JSON.parseObject(HttpUtils.updata(map, MyData.URL_GETEXAMLIST), new TypeReference<List>() {
                                });
                                int[] value = new int[list.size()];
                                for (int i = 0; i < list.size(); i++) {
                                    HashMap map2 = new HashMap();
                                    map2.put("salt", MyData.getKey());
                                    map2.put("eid", ((Map) list.get(i)).get("eid").toString());
                                    map2.put("ord", 1);
                                    List list2 = JSON.parseObject(HttpUtils.updata(map2, MyData.URL_GETSCORELIST), new TypeReference<List>() {
                                    });
                                    for (int j = 0; j < list2.size(); j++) {
                                        if (stuname.equals(((Map) list2.get(j)).get("name"))) {
                                            value[i] = j + 1;
                                        }
                                    }
                                }

                                mhandler.obtainMessage(GONEXT, value).sendToTarget();
                            }
                        }).start();


                    }
                });
                contentView.findViewById(R.id.gradecontent_dialog2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent achartIntent = new AChartExamole(new double[]{rank1, rank2, rank3, rank4, rank5, rank6})
                                    .ececute(getActivity());
                            startActivity(achartIntent);
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.d("oncreate", e.getMessage());
                        }
                        dialogs.dismiss();

                    }
                });
                contentView.findViewById(R.id.gradecontent_dialog3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogs.dismiss();

                    }
                });


            }
        });
        tag = (TextView) view.findViewById(R.id.title_title);
        tag.setText(MyData.GRADENAME + "-" + MyData.STUNAME);
        item1 = (TextView) view.findViewById(R.id.gradecontent_item1);
        item2 = (TextView) view.findViewById(R.id.gradecontent_item2);
        item3 = (TextView) view.findViewById(R.id.gradecontent_item3);
        item4 = (TextView) view.findViewById(R.id.gradecontent_item4);
        item5 = (TextView) view.findViewById(R.id.gradecontent_item5);
        item6 = (TextView) view.findViewById(R.id.gradecontent_item6);
        item7 = (TextView) view.findViewById(R.id.gradecontent_item7);
        item8 = (TextView) view.findViewById(R.id.gradecontent_item8);
        item9 = (TextView) view.findViewById(R.id.gradecontent_item9);
    }

    private void getData() {
        dialog.show();
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
                            upView(list);
                        }

                        break;
                    case GONEXT:
                        dialog.dismiss();
                        MyData.PATHVALUE = (int[]) msg.obj;
                        if (MyData.PATHVALUE != null && MyData.PATHVALUE.length > 0) {
                            getActivity().getFragmentManager().beginTransaction()
                                    .hide(GradeContentFragment.this)
                                    .add(R.id.main_content, pathFragment, "pathFragment")
                                    .addToBackStack(null).commit();
                            break;
                        } else {
                            Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                        }
                }
            } catch (Exception e) {
                try {
                    Toast.makeText(getActivity(), R.string.connectfild, Toast.LENGTH_SHORT).show();
                } catch (Exception e1) {

                }
            }
        }
    };

    private void upView(List<Map> list) {
        int average = 0;
        int mgrade = 0;
        int mrank = 1;
        int sum = 0;
        int grade;

        rank1 = 0;
        rank2 = 0;
        rank3 = 0;
        rank4 = 0;
        rank5 = 0;
        rank6 = 0;

        if (MyData.UTYPE == 1) {
            mgrade = MyData.STUGRADE;
        } else {
            String name = sp.getString("name", "");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).get("name").toString().equals(name)) {
                    mgrade = Integer.parseInt(list.get(i).get("point").toString());
                    MyData.STUGRADE = mgrade;
                }

            }
        }


        for (int i = 0; i < list.size(); i++) {
            grade = Integer.parseInt(list.get(i).get("point").toString());
            if (grade < 60) {
                rank1++;
            } else if (grade < 70) {
                rank2++;
            } else if (grade < 80) {
                rank3++;
            } else if (grade < 90) {
                rank4++;
            } else if (grade < 100) {
                rank5++;
            } else {
                rank6++;
            }
            sum = sum + grade;
            if (mgrade < grade) {
                mrank++;
            }

        }
        average = sum / list.size();

        item1.setText(mgrade + "");
        item2.setText(average + "");
        item3.setText(mrank + "");
        item4.setText(rank1 + "");
        item5.setText(rank2 + "");
        item6.setText(rank3 + "");
        item7.setText(rank4 + "");
        item8.setText(rank5 + "");
        item9.setText(rank6 + "");


    }
}
