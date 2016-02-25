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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.smapley.education.R;
import com.smapley.education.adapter.AddressAdapter;
import com.smapley.education.http.HttpUtils;
import com.smapley.education.utils.MyData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Smapley on 2015/4/19.
 */
public class AddressListFragment extends Fragment {

    private ListView listView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private List list;
    private SharedPreferences sp;
    private final int GETDATA = 1;
    private View contentView;
    private ProgressDialog dialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.addresslist, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);

        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initView(contentView);
        getData();
        return contentView;
    }

    private void initView(View view) {
        listView = (ListView) view.findViewById(R.id.addresslist_list);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        tag = (TextView) view.findViewById(R.id.title_title);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.set_tag);
        tag.setText(R.string.addresslist_tag);
        listView.setDivider(null);


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
                upmap.put("phone", sp.getString("phone", ""));
                upmap.put("salt", MyData.getKey());
                String resultString = HttpUtils.updata(upmap, MyData.URL_GETBOOKLIST);
                mHandler.obtainMessage(GETDATA, resultString).sendToTarget();
            }
        }).start();

    }


    private Handler mHandler = new Handler() {
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
                        if (!list.isEmpty() && list != null) {
                            listView.setAdapter(new AddressAdapter(getActivity(), list));

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
