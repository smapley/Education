package com.smapley.education.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.smapley.education.R;

/**
 * Created by Smapley on 2015/4/18.
 */
public class StudentDialogFragment2 extends DialogFragment {

    private EditText item1;
    private EditText item2;
    private EditText item3;
    private OnItemClickListener2 mlistener;
    private String mclassname;
    private String msno;

    public void setData(OnItemClickListener2 listener, String classname, int sno) {
        mlistener = listener;
        mclassname = classname;
        msno = sno+1+"";
        
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_student_dialog2, null);
        builder.setView(view)
                // Add action buttons  
                .setPositiveButton(getString(R.string.Okay),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mlistener.OnClick2(item1.getText().toString(), item2.getText().toString(), item3.getText().toString());
                            }
                        }).setNegativeButton(getString(R.string.cancel), null);
        initView(view);
        return builder.create();
    }

    private void initView(View view) {
        item1 = (EditText) view.findViewById(R.id.student_dialog21);
        item2 = (EditText) view.findViewById(R.id.student_dialog22);
        item3 = (EditText) view.findViewById(R.id.student_dialog23);
        item1.setText(mclassname);
        item2.setText(msno);
    }

    public interface OnItemClickListener2 {
        public void OnClick2(String item1, String item2, String item3);
    }
}
