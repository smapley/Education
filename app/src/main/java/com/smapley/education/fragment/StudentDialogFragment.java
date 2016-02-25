package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.smapley.education.R;

/**
 * Created by Smapley on 2015/4/14.
 */
public class StudentDialogFragment extends DialogFragment {

    private RadioButton item1;
    private RadioButton item2;
    private RadioButton item4;
    private RadioGroup radioGroup;
    private int selectid;
    private OnItemClickListener mlistener;
    private final int type1 = 0;
    private final int type2 = 1;
    private final int type3 = 2;
    private int type0;
    private int msid;

    public void setlistener(OnItemClickListener listener, int sid, int type) {
        mlistener = listener;
        type0 = type;
        msid = sid;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_student_dialog, null);
        builder.setTitle(getString(R.string.student_dialog));
        builder.setView(view)
                // Add action buttons  
                .setPositiveButton(getString(R.string.Okay),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mlistener.OnClick(msid,selectid);
                            }
                        }).setNegativeButton(getString(R.string.cancel), null);
        initView(view);
        return builder.create();
    }

    private void initView(View view) {

        radioGroup = (RadioGroup) view.findViewById(R.id.radiogroup);
        item1 = (RadioButton) view.findViewById(R.id.student_dialog1);
        item2 = (RadioButton) view.findViewById(R.id.student_dialog2);
        item4 = (RadioButton) view.findViewById(R.id.student_dialog4);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = radioGroup.getCheckedRadioButtonId();
                int id1 = item1.getId();
                int id2 = item2.getId();
                int id4 = item4.getId();
                if (id == id1) {
                    selectid = 0;
                } else if (id == id2) {
                    selectid = 1;
                } else if (id == id4) {
                    selectid = 3;
                }

            }
        });
        int id = 0;
        if (type0 == type1) {
            id = item1.getId();
        } else if (type0 == type2) {
            id = item2.getId();
        }
        radioGroup.check(id);

    }


    public interface OnItemClickListener {
        public void OnClick(int sid,int position);
    }
}  