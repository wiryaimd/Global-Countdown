package com.wiryaimd.globalcountdown.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.globalcountdown.R;

public class InfoDialog extends DialogFragment {

    private String title, msg;
    private TextView tvtitle, tvdesc;
    private Button btnok;

    public InfoDialog(String title, String msg) {
        this.title = title;
        this.msg = msg;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }catch (NullPointerException e){
            System.out.println(e);
        }
        return inflater.inflate(R.layout.dialog_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvtitle = view.findViewById(R.id.info_title);
        tvdesc = view.findViewById(R.id.info_desc);
        btnok = view.findViewById(R.id.info_btnok);

        tvtitle.setText(title);
        tvdesc.setText(msg);

        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

    }
}
