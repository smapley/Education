package com.smapley.education.chart;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smapley.education.R;
import com.smapley.education.utils.MyData;

/**
 * Created by Smapley on 2015/4/28.
 */
public class PathFragment extends Fragment {
    private View contentView;
    private final int GETDATA = 1;
    private SharedPreferences sp;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;
    private PathView pw;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.path, container, false);
        sp = getActivity().getSharedPreferences(MyData.SP_USER, getActivity().MODE_PRIVATE);
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle(getString(R.string.tips));
        dialog.setMessage(getString(R.string.connect));

        initView(contentView);
        return contentView;

    }

    private void initView(View view) {
        tag = (TextView) view.findViewById(R.id.title_title);
        back = (LinearLayout) view.findViewById(R.id.title_back);
        backtext=(TextView)view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(MyData.GRADENAME + "-" + MyData.STUNAME);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        tag.setText(R.string.path_tag);
        pw = (PathView) view.findViewById(R.id.pv);
        initPath();

    }

    private void initPath() {
        int max = 0;
        int min = 1000;
        for (int i = 0; i < MyData.PATHVALUE.length; i++) {
            if (max < MyData.PATHVALUE[i]) {
                max = MyData.PATHVALUE[i];
            }
            if (min > MyData.PATHVALUE[i]) {
                min = MyData.PATHVALUE[i];
            }
        }
        int length = MyData.PATHVALUE.length;
        int[] value = new int[length];
        int avg = (max + min) / 2;
        for (int i = 0; i < MyData.PATHVALUE.length; i++) {
            value[length - 1 - i] = MyData.PATHVALUE[i];
//            if (value[length - 1 - i] > avg) {
//                value[length - 1 - i] = value[length - 1 - i] - (value[length - 1 - i] - avg) * 2;
//            }
//            if (value[length - 1 - i] < avg) {
//                value[length - 1 - i] = value[length - 1 - i] + (avg - value[length - 1 - i]) * 2;
//            }
        }
        pw.setXCount(max, min);
        pw.setDate(value);
    }


}
