package com.wiryaimd.globalcountdown.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.adapter.LocalcAdapter;
import com.wiryaimd.globalcountdown.model.DateModel;
import com.wiryaimd.globalcountdown.model.LocalCountdownModel;
import com.wiryaimd.globalcountdown.util.LocalcReceiver;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SetcountdownDialog extends DialogFragment {

    private Button btnsave;
    private EditText edttitle, edtdesc;
    private Switch notification;

    private Activity activity;
    private RecyclerView recyclerView;

    private ArrayList<LocalCountdownModel> localcList;
    private DateModel strdate;

    public SetcountdownDialog(Activity activity, RecyclerView recyclerView, DateModel date) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.strdate = date;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }catch (NullPointerException e){
            System.out.println(e);
        }

        return inflater.inflate(R.layout.dialog_setcountdown, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        btnsave = view.findViewById(R.id.setcountdown_btnsave);
        edttitle = view.findViewById(R.id.setcountdown_edttitle);
        edtdesc = view.findViewById(R.id.setcountdown_edtdesc);
        notification = view.findViewById(R.id.setcountdown_notification);
        notification.setChecked(true);

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = null;
                if (!edttitle.getText().toString().isEmpty()) {
                    ArrayList<LocalCountdownModel> listCountdown = PreferencesManager.getCountdown(activity);
                    if (listCountdown != null) {
                        localcList = listCountdown;
                    } else {
                        localcList = new ArrayList<>();
                    }

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm", Locale.getDefault());

                    String mydate = strdate.getYear() + "-" +
                            String.format(Locale.getDefault(), "%02d", Integer.parseInt(strdate.getMonth()) + 1) + "-" +
                            strdate.getDay() + "-" +
                            strdate.getHours() + "-" +
                            strdate.getMinute();
                    try {
                        date = dateFormat.parse(mydate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                    if (date != null && alarmManager != null) {
                        if (date.getTime() > System.currentTimeMillis()) {
                            LocalCountdownModel localCountdownModel = new LocalCountdownModel(
                                    edttitle.getText().toString(),
                                    edtdesc.getText().toString(),
                                    strdate,
                                    notification.isChecked(),
                                    date.getTime()
                            );
                            localcList.add(localCountdownModel);

                            PreferencesManager.saveCountdown(activity, localcList);

                            if (localCountdownModel.isNotification()) {
                                Intent intent = new Intent(activity, LocalcReceiver.class);
                                intent.putExtra("CountdownTitle", localCountdownModel.getTitle());
                                intent.putExtra("CountdownDate", mydate);
                                intent.putExtra("CountdownId", localcList.size());
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, localcList.size(), intent, 0);

                                long currentTime = System.currentTimeMillis();
                                alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTime + (localCountdownModel.getMillisTime() - currentTime), pendingIntent);
                            }

                            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                            LocalcAdapter adapter = new LocalcAdapter(activity, localcList, getFragmentManager(), recyclerView);
                            recyclerView.setAdapter(adapter);
                        } else {
                            new InfoDialog("Alert", "The date is overdue").show(getFragmentManager(), "isOverdue");
                        }
                    } else {
                        new InfoDialog("Alert", "Failed to save Countdown").show(getFragmentManager(), "isFailed");
                    }
                    getDialog().dismiss();
                }else{
                    Toast.makeText(activity, "Title required", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
