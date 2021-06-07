package com.wiryaimd.globalcountdown.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.adapter.LocalcAdapter;
import com.wiryaimd.globalcountdown.model.DateModel;
import com.wiryaimd.globalcountdown.model.LocalCountdownModel;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.util.ArrayList;
import java.util.Calendar;

public class LocalcFragment extends Fragment {

    private FloatingActionButton btncreate;
    private ImageView imgbg, imgaddcd;
    private RecyclerView recyclerView;
    private TextView tvaddcd;

    private Activity activity;
    private Calendar calendar;

    private DateModel dateModel;

    public LocalcFragment(Activity activity){
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_localc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dateModel = new DateModel();

        imgbg = view.findViewById(R.id.localc_imgbg);
        imgaddcd = view.findViewById(R.id.localc_addcd);
        tvaddcd = view.findViewById(R.id.localc_tvaddcd);
        btncreate = view.findViewById(R.id.localc_btncreate);
        recyclerView = view.findViewById(R.id.localc_recyclerview);

        Glide.with(activity).load(R.drawable.bghome).into(imgbg);

        calendar = Calendar.getInstance();

        if (PreferencesManager.getCountdown(activity) != null){
            imgaddcd.setVisibility(View.GONE);
            tvaddcd.setVisibility(View.GONE);
            ArrayList<LocalCountdownModel> localcList = PreferencesManager.getCountdown(activity);
            LocalcAdapter adapter = new LocalcAdapter(activity, localcList, getFragmentManager(), recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            recyclerView.setAdapter(adapter);
        }else{
            tvaddcd.setVisibility(View.VISIBLE);
            imgaddcd.setVisibility(View.VISIBLE);
        }

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        dateModel.setHours(String.valueOf(hourOfDay));
                        dateModel.setMinute(String.valueOf(minute));
                        new SetcountdownDialog(activity, recyclerView, dateModel).show(getFragmentManager(), "DetailSettings");
                        dateModel = new DateModel();
                    }
                },
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                Calendar.SECOND,
                true
        );
        timePickerDialog.setAccentColor("#4062B8");

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        dateModel.setYear(String.valueOf(year));
                        dateModel.setMonth(String.valueOf(monthOfYear));
                        dateModel.setDay(String.valueOf(dayOfMonth));
                        timePickerDialog.show(getFragmentManager(), "Set Time");
                    }
                },
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
        );
        datePickerDialog.setAccentColor("#4062B8");

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgaddcd.setVisibility(View.GONE);
                tvaddcd.setVisibility(View.GONE);
                ArrayList<LocalCountdownModel> listCountdown = PreferencesManager.getCountdown(activity);
                if (listCountdown != null && listCountdown.size() >= 10){
                    new InfoDialog("Alert", "You have reached the maximum of the Countdown (Max 10)").show(getFragmentManager(), "ReachedMax");
                }else{
                    datePickerDialog.show(getFragmentManager(), "Set Date");
                }
            }
        });

    }
}
