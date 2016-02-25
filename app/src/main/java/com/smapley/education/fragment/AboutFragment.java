package com.smapley.education.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smapley.education.R;

/**
 * Created by Administrator on 2015/4/19.
 */
public class AboutFragment extends Fragment {

    private View contentView;
    private LinearLayout back;
    private TextView backtext;
    private TextView tag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView = inflater.inflate(R.layout.about, container, false);
        initView(contentView);
        return contentView;
    }


    private void initView(View view) {
        back = (LinearLayout) view.findViewById(R.id.title_back);
        tag = (TextView) view.findViewById(R.id.title_title);
        backtext = (TextView) view.findViewById(R.id.title_backtext);
        back.setVisibility(View.VISIBLE);
        backtext.setText(R.string.set_tag);
        tag.setText(R.string.about_tag);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
    }
}
