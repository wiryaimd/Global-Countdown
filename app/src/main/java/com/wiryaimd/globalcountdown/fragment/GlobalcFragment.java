package com.wiryaimd.globalcountdown.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.adapter.GlobalcAdapter;
import com.wiryaimd.globalcountdown.model.DateModel;
import com.wiryaimd.globalcountdown.model.GlobalCountdownModel;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class GlobalcFragment extends Fragment {

    private Activity activity;
    private ArrayList<GlobalCountdownModel> globalcList;

    private ImageView imgbg, menulist;
    private TextView tvusername;
    private FloatingActionButton btncreate;
    private ProgressBar loading;
    private RecyclerView recyclerView;

    private DatabaseReference dbref;

    private DateModel dateModel;

    private String username;
    private int changeCount;

    public static int cdcount;

    public GlobalcFragment(Activity activity) {
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_globalc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dbref = FirebaseDatabase.getInstance().getReference();
        dateModel = new DateModel();
        changeCount = 0;
        cdcount = 0;

        imgbg = view.findViewById(R.id.globalc_imgbg);
        menulist = view.findViewById(R.id.globalc_menulist);
        tvusername = view.findViewById(R.id.globalc_username);
        btncreate = view.findViewById(R.id.globalc_btncreate);
        loading = view.findViewById(R.id.globalc_loading);
        recyclerView = view.findViewById(R.id.globalc_recyclerview);

        recyclerView.setNestedScrollingEnabled(false);
        loading.setVisibility(View.VISIBLE);

        Glide.with(activity).load(R.drawable.bghome2).into(imgbg);

        username = PreferencesManager.getUsername(activity);
        if (username != null){
            tvusername.setText(username);
        }else{
            if (getFragmentManager() != null) {
                UsernameDialog usernameDialog = new UsernameDialog(activity, tvusername);
                usernameDialog.setCancelable(false);
                usernameDialog.show(getFragmentManager(), "SaveUsername2");
            }
        }

        dbref.child("countdown").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                changeCount += 1;
                if (changeCount == 1) {
                    ArrayList<GlobalCountdownModel> globalcList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GlobalCountdownModel globalCountdownModel = dataSnapshot.getValue(GlobalCountdownModel.class);
                        globalcList.add(globalCountdownModel);
                        if (username != null){
                            if (globalCountdownModel != null && username.equalsIgnoreCase(globalCountdownModel.getUsername())){
                                cdcount += 1;
                            }
                        }
                    }
                    loading.setVisibility(View.GONE);
                    Collections.shuffle(globalcList);
                    GlobalcAdapter adapter = new GlobalcAdapter(activity, globalcList, getFragmentManager(), recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        dateModel.setHours(String.valueOf(hourOfDay));
                        dateModel.setMinute(String.valueOf(minute));
                        new SetcountdownGlobalDialog(activity, recyclerView, getFragmentManager(), dateModel).show(getFragmentManager(), "DetailSettingsGlobal");
                        dateModel = new DateModel();
                    }
                },
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                Calendar.SECOND,
                true
        );
        timePickerDialog.setAccentColor("#51BA55");

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        dateModel.setYear(String.valueOf(year));
                        dateModel.setMonth(String.valueOf(monthOfYear));
                        dateModel.setDay(String.valueOf(dayOfMonth));
                        timePickerDialog.show(getFragmentManager(), "Set Time Global");
                    }
                },
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH
        );
        datePickerDialog.setAccentColor("#51BA55");

        btncreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferencesManager.getUsername(activity) != null){
                    if (cdcount < 5) {
                        datePickerDialog.show(getFragmentManager(), "PickDateGlobal");
                    }else{
                        new InfoDialog("Alert", "You cannot create the global countdown more than 5, please wait until your other countdown finished").show(getFragmentManager(), "MaxCountdown");
                    }
                }else{
                    new UsernameDialog(activity, tvusername).show(getFragmentManager(), "SaveUsername");
                }
            }
        });

    }
}
