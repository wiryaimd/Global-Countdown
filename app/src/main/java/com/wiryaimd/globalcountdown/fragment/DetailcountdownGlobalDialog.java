package com.wiryaimd.globalcountdown.fragment;

import android.app.Activity;
import android.app.AlarmManager;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.model.GlobalCountdownModel;
import com.wiryaimd.globalcountdown.util.GlobalcReciver;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.util.ArrayList;
import java.util.Random;

public class DetailcountdownGlobalDialog extends DialogFragment {

    private Activity activity;

    private GlobalCountdownModel globalCountdownModel;
    private String cduser, todate;
    private boolean isVoted, isNotif;

    private TextView tvtitle, tvdesc, tvdate, tvusername, tvvotescount;
    private ImageView imgbg, imgvote;
    private Button btnnotif;
    private LinearLayout lvotes;

    private DatabaseReference dbref;

    private ArrayList<String> userVote, userNotif;
    private Random random;

    private int clickCount = 0;

    public DetailcountdownGlobalDialog(Activity activity, GlobalCountdownModel globalCountdownModel, String todate) {
        this.activity = activity;
        this.globalCountdownModel = globalCountdownModel;
        this.todate = todate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (NullPointerException e){
            System.out.println(e);
        }

        return inflater.inflate(R.layout.dialog_detailcountdownglobal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        dbref = FirebaseDatabase.getInstance().getReference();
        random = new Random();
        userVote = new ArrayList<>();
        userNotif = new ArrayList<>();
        isVoted = false;
        isNotif = false;

        cduser = PreferencesManager.getUsername(activity);

        tvusername = view.findViewById(R.id.detailcountdownglobal_username);
        tvtitle = view.findViewById(R.id.detailcountdownglobal_title);
        tvdesc = view.findViewById(R.id.detailcountdownglobal_desc);
        tvdate = view.findViewById(R.id.detailcountdownglobal_todate);
        tvvotescount = view.findViewById(R.id.detailcountdownglobal_votescount);
        imgbg = view.findViewById(R.id.detailcountdownglobal_img);
        imgvote = view.findViewById(R.id.detailcountdownglobal_voteimg);
        btnnotif = view.findViewById(R.id.detailcountdownglobal_btnnotif);
        lvotes = view.findViewById(R.id.detailcountdownglobal_linear1);

        dbref.child("countdown").child(globalCountdownModel.getUuid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clickCount += 1;
                if (clickCount == 1) {
                    for (DataSnapshot data : snapshot.child("voter").getChildren()) {
                        String username = data.getValue(String.class);
                        userVote.add(username);
                        if (username != null && username.equalsIgnoreCase(cduser)){
                            isVoted = true;
                        }
                    }

                    for (DataSnapshot data : snapshot.child("notifier").getChildren()) {
                        String username = data.getValue(String.class);
                        userNotif.add(username);
                        if (username != null && username.equalsIgnoreCase(cduser)){
                            isNotif = true;
                        }
                    }

                    if (globalCountdownModel.getUsername().equalsIgnoreCase(cduser)){
                        isNotif = true;
                    }

                    if (isVoted){
                        imgvote.setImageResource(R.drawable.upvoted);
                        tvvotescount.setText(String.valueOf(userVote.size()));
                    }

                    if (isNotif){
                        btnnotif.setBackgroundResource(R.drawable.customcolor5);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (globalCountdownModel.getImgurl() != null && !globalCountdownModel.getImgurl().equalsIgnoreCase("none")) {
            Glide.with(activity).load(globalCountdownModel.getImgurl()).into(imgbg);
        }else{
            imgbg.setVisibility(View.GONE);
        }

        tvusername.setText(globalCountdownModel.getUsername());
        tvtitle.setText(globalCountdownModel.getTitle());
        tvdesc.setText(globalCountdownModel.getDesc());
        tvdate.setText(todate);

        if (globalCountdownModel.getVoter() != null){
            tvvotescount.setText(String.valueOf(globalCountdownModel.getVoter().size()));
        }else{
            tvvotescount.setText("0");
        }

        btnnotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNotif){
                    userNotif.add(cduser);
                    dbref.child("countdown").child(globalCountdownModel.getUuid()).child("notifier").setValue(userNotif).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            int globalcSize = 10 + random.nextInt(300 - 10 + 1);

                            AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
                            Intent intent = new Intent(activity, GlobalcReciver.class);
                            intent.putExtra("CountdownTitle", globalCountdownModel.getTitle());
                            intent.putExtra("CountdownDate", todate);
                            intent.putExtra("CountdownId", globalcSize);
                            intent.putExtra("CountdownUUID", globalCountdownModel.getUuid());
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, globalcSize, intent, 0);

                            long currentTime = System.currentTimeMillis();
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, currentTime + (globalCountdownModel.getMillisTime() - currentTime), pendingIntent);

                            btnnotif.setBackgroundResource(R.drawable.customcolor5);
                        }
                    });
                }
            }
        });

        lvotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isVoted){
                    userVote.add(cduser);
                    dbref.child("countdown").child(globalCountdownModel.getUuid()).child("voter").setValue(userVote).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            imgvote.setImageResource(R.drawable.upvoted);
                            tvvotescount.setText(String.valueOf(userVote.size()));
                        }
                    });
                }
            }
        });


    }
}
