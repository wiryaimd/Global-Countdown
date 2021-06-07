package com.wiryaimd.globalcountdown.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

public class UsernameDialog extends DialogFragment {

    private Activity activity;

    private EditText edtinput;
    private Button btnsave;

    private TextView tvusername;

    public UsernameDialog(Activity activity, TextView tvusername) {
        this.activity = activity;
        this.tvusername = tvusername;
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
        return inflater.inflate(R.layout.dialog_username, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        edtinput = view.findViewById(R.id.dialogusername_input);
        btnsave = view.findViewById(R.id.dialogusername_btnsave);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferencesManager.saveUsername(activity, edtinput.getText().toString());
                tvusername.setText(edtinput.getText().toString());
                getDialog().dismiss();
            }
        });

    }
}
