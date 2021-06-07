package com.wiryaimd.globalcountdown.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.adapter.LocalcAdapter;
import com.wiryaimd.globalcountdown.model.LocalCountdownModel;
import com.wiryaimd.globalcountdown.util.LocalcReceiver;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class DetailcountdownDialog extends DialogFragment {

    private Activity activity;
    private ArrayList<LocalCountdownModel> localCountdownList;

    private int localcIndex;

    private RecyclerView recyclerView;

    private TextView tvtitle, tvdesc, tvdate;
    private ImageView imgnotif;
    private Button btndelete;

    public DetailcountdownDialog(Activity activity, ArrayList<LocalCountdownModel> localCountdownList, int localcIndex, RecyclerView recyclerView) {
        this.activity = activity;
        this.localCountdownList = localCountdownList;
        this.localcIndex = localcIndex;
        this.recyclerView = recyclerView;
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

        return inflater.inflate(R.layout.dialog_detailcountdown, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tvtitle = view.findViewById(R.id.detailcountdown_title);
        tvdesc = view.findViewById(R.id.detailcountdown_desc);
        tvdate = view.findViewById(R.id.detailcountdown_date);
        imgnotif = view.findViewById(R.id.detailcountdown_notification);
        btndelete = view.findViewById(R.id.detailcountdown_btndelete);

        tvtitle.setText(localCountdownList.get(localcIndex).getTitle());
        tvdesc.setText(localCountdownList.get(localcIndex).getDesc());

        String date = localCountdownList.get(localcIndex).getDate().getYear() + "-" +
                new DateFormatSymbols().getMonths()[Integer.parseInt(localCountdownList.get(localcIndex).getDate().getMonth())] + "-" +
                localCountdownList.get(localcIndex).getDate().getDay() + " " +
                String.format(Locale.getDefault(), "%02d", Integer.parseInt(localCountdownList.get(localcIndex).getDate().getHours())) + ":" +
                String.format(Locale.getDefault(), "%02d", Integer.parseInt(localCountdownList.get(localcIndex).getDate().getMinute()));
        tvdate.setText(date);

        if (localCountdownList.get(localcIndex).isNotification()) {
            Glide.with(activity).load(R.drawable.ic_notifon).into(imgnotif);
        }else{
            Glide.with(activity).load(R.drawable.ic_notifoff).into(imgnotif);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    Intent intent = new Intent(activity, LocalcReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, localcIndex + 1, intent, 0);
                    alarmManager.cancel(pendingIntent);
                }

                localCountdownList.remove(localcIndex);
                PreferencesManager.saveCountdown(activity, localCountdownList);
                LocalcAdapter adapter = new LocalcAdapter(activity, localCountdownList, getFragmentManager(), recyclerView);
                recyclerView.setAdapter(adapter);
                getDialog().dismiss();
            }
        });

    }
}
