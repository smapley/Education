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
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.adapter.BoundPhoneAdapter;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Smapley on 2015/4/19.
 */
public class BoundFragment extends Fragment {
    private ListView listView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private List list;
    private TextView add;
    private SharedPreferences sp;
    public final int GETDATA = 1;
    private View contentView;
    private BoundStudentFragment boundStudentFragment;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.boundphone, container, false);
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
        boundStudentFragment = new BoundStudentFragment();
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.boundphone_list);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.set_tag);
        tag.setText(R.string.boundphone_tag);
        add = (TextView) view.findViewById(R.id.title_more);
        add.setVisibility(View.VISIBLE);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText editText = new EditText(getActivity());
                editText.setHint(getString(R.string.boundphone_dialog));
                editText.setBackgroundResource(R.drawable.textview_edge);
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.boundphone_tag))
                        .setView(editText)
                        .setPositiveButton(getString(R.string.Okay), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                MyData.BOUNDPHONE = editText.getText().toString();
                                getActivity().getFragmentManager().beginTransaction().remove(BoundFragment.this)
                                        .add(R.id.set_content, boundStudentFragment, "boundStudentFragment")
                                        .addToBackStack(null).commit();

                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show();
            }
        });
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
                HashMap<String, Object> upmap = new HashMap();
                upmap.put("jphone", sp.getString("jphone", ""));
                upmap.put("salt", MyData.getKey());
                String resultString = HttpUtils.updata(upmap, MyData.URL_GETCHILD);
                mhandler.obtainMessage(GETDATA, resultString).sendToTarget();
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
                        list = JSON.parseObject(msg.obj.toString(),
                                new TypeReference<List<HashMap<String, String>>>() {
                                });

                        listView.setAdapter(new BoundPhoneAdapter(getActivity(), list));

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

