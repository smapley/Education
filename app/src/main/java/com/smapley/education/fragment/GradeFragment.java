package com.smapley.education.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
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
 * Created by Smapley on 2015/4/20.
 */
public class GradeFragment extends Fragment {

    private View contentView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private GridLayout gridLayout;
    private TextView add;
    private String[] a;
    private SharedPreferences sp;
    private final int GETDATA = 1;
    private AddCourseFragment addCourseFragment;
    private GradeItemFragment gradeItemFragment;
    private ProgressDialog dialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        contentView = inflater.inflate(R.layout.grade, container, false);
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
        addCourseFragment = new AddCourseFragment();
        gradeItemFragment = new GradeItemFragment();
    }

    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        backtext.setText(R.string.main_first);
        back.setVisibility(View.VISIBLE);
        tag = (TextView) view.findViewById(R.id.title_title);
        MyData.BACKSTRING = getString(R.string.grade_tag);
        tag.setText(MyData.BACKSTRING);
        gridLayout = (GridLayout) view.findViewById(R.id.grade_gridlayout);
        add = (TextView) view.findViewById(R.id.title_more);
        if (MyData.UTYPE == 1) {
            add.setVisibility(View.VISIBLE);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyData.ADDCOURSETYPE = 1;
                    getActivity().getFragmentManager().beginTransaction().remove(GradeFragment.this)
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

        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("tphone", sp.getString("tphone", ""));
                map.put("salt", MyData.getKey());
                mhandler.obtainMessage(GETDATA, HttpUtils.updata(map, MyData.URL_GETEXAMSUBJECT)).sendToTarget();
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
                        Map resultmap = JSON.parseObject(msg.obj.toString(), new TypeReference<Map>() {
                        });
                        String result = resultmap.get("subject_str").toString();
                        a = result.split(",");
                        MyData.GRADELIST.clear();
                        for (int i = 0; i < a.length; i++) {
                            if (!a[i].equals("")) {
                                MyData.GRADELIST.add(a[i]);
                            }
                        }

                        List<String> list = MyData.GRADELIST;
                        int count = list.size();
                        int num = 0;
                        int i = 0;
                        while (count > 0) {
                            for (int j = 0; j < 2; j++) {
                                final Button textView = new Button(getActivity());
                                textView.setText(list.get(num));
                                textView.setWidth(getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2 - 100);
                                textView.setHeight(getActivity().getWindowManager().getDefaultDisplay().getWidth() / 2 - 100);
                                textView.setBackgroundResource(R.drawable.textview_edge_circle);

                                GridLayout.Spec rowSpec = GridLayout.spec(i);     //设置它的行和列  
                                GridLayout.Spec columnSpec = GridLayout.spec(j);
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, columnSpec);
                                params.setGravity(Gravity.LEFT);
                                params.setMargins(50, 50, 50, 50);
                                gridLayout.addView(textView, params);
                                textView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        MyData.GRADENAME = textView.getText().toString();
                                        getActivity().getFragmentManager().beginTransaction().hide(GradeFragment.this)
                                                .add(R.id.main_content, gradeItemFragment, "gradeItemFragment")
                                                .addToBackStack(null).commit();
                                    }
                                });
                                count--;
                                num++;
                                if (count == 0) {
                                    break;
                                }
                            }
                            i++;
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
