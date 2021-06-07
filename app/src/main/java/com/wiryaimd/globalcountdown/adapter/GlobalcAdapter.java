package com.wiryaimd.globalcountdown.adapter;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wiryaimd.globalcountdown.MainActivity;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.fragment.DetailcountdownGlobalDialog;
import com.wiryaimd.globalcountdown.fragment.InfoDialog;
import com.wiryaimd.globalcountdown.model.GlobalCountdownModel;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class GlobalcAdapter extends RecyclerView.Adapter<GlobalcAdapter.GlobalHolder> {

    private Activity activity;
    private FragmentManager fm;

    private ArrayList<GlobalCountdownModel> globalcList;
    private ArrayList<GlobalHolder> globalHolder;

    private RecyclerView recyclerView;
    private DatabaseReference dbref;

    public GlobalcAdapter(Activity activity, ArrayList<GlobalCountdownModel> globalcList, FragmentManager fm, RecyclerView recyclerView) {
        this.activity = activity;
        this.globalcList = globalcList;
        this.fm = fm;
        this.recyclerView = recyclerView;

        globalHolder = new ArrayList<>();
        MainActivity.runGlobalCountdown(globalHolder);
        dbref = FirebaseDatabase.getInstance().getReference();
    }

    @NonNull
    @Override
    public GlobalcAdapter.GlobalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_globalcountdown, parent, false);
        return new GlobalHolder(view);
    }

    @Override
    public int getItemCount() {
        return globalcList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalcAdapter.GlobalHolder holder, int position) {
        GlobalCountdownModel globalCountdownModel = globalcList.get(position);
        holder.setItem(globalCountdownModel, position);

        synchronized (globalHolder){
            globalHolder.add(holder);
        }
    }

    public class GlobalHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView tvtitle, tvusername, tvvote, tvyears, tvdays, tvhours, tvminutes, tvseconds;
        public ImageView imgvote, imgbg;
        public LinearLayout lvotes, lyears, ldays, lhours, lminutes, lseconds;

        private GlobalCountdownModel globalCountdownModel;

        public GlobalHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.itemglobal_cardview);
            tvtitle = itemView.findViewById(R.id.itemglobal_title);
            tvusername = itemView.findViewById(R.id.itemglobal_userpost);
            tvvote = itemView.findViewById(R.id.itemglobal_upvotescount);
            tvyears = itemView.findViewById(R.id.itemglobal_year);
            tvdays = itemView.findViewById(R.id.itemglobal_days);
            tvhours = itemView.findViewById(R.id.itemglobal_hours);
            tvminutes = itemView.findViewById(R.id.itemglobal_minutes);
            tvseconds = itemView.findViewById(R.id.itemglobal_seconds);
            imgvote = itemView.findViewById(R.id.itemglobal_upvotes);
            imgbg = itemView.findViewById(R.id.itemglobal_imgbg);
            lyears = itemView.findViewById(R.id.itemglobal_linearyears);
            ldays = itemView.findViewById(R.id.itemglobal_lineardays);
            lhours = itemView.findViewById(R.id.itemglobal_linearhours);
            lminutes = itemView.findViewById(R.id.itemglobal_linearminutes);
            lseconds = itemView.findViewById(R.id.itemglobal_linearsecond);
            lvotes = itemView.findViewById(R.id.itemglobal_linearvotes);

        }

        public void setItem(GlobalCountdownModel globalCountdownModel, int position){
            tvtitle.setText(globalCountdownModel.getTitle());
            String date = globalCountdownModel.getDateModel().getYear() + "-" + new DateFormatSymbols().getMonths()[Integer.parseInt(globalCountdownModel.getDateModel().getMonth())] + "-" + globalCountdownModel.getDateModel().getDay() + " " + String.format(Locale.getDefault(), "%02d", Integer.parseInt(globalCountdownModel.getDateModel().getHours())) + ":" + String.format(Locale.getDefault(), "%02d", Integer.parseInt(globalCountdownModel.getDateModel().getMinute()));

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DetailcountdownGlobalDialog(activity, globalCountdownModel, date).show(fm, "DetailCountdownGlobal");
                }
            });

            if (globalCountdownModel.getImgurl() != null && !globalCountdownModel.getImgurl().equalsIgnoreCase("none")){
                Glide.with(activity).load(globalCountdownModel.getImgurl()).centerCrop().into(imgbg);
            }

            tvusername.setText(globalCountdownModel.getUsername());

            if (globalCountdownModel.getImgurl() != null) {
                if (!globalCountdownModel.getImgurl().equalsIgnoreCase("none")) {
                    tvtitle.setBackgroundResource(R.drawable.randg1);
                    tvusername.setBackgroundResource(R.drawable.randg1);
                    lvotes.setBackgroundResource(R.drawable.randg1);
                }
            }

            if (globalCountdownModel.getVoter() != null) {
                tvvote.setText(String.valueOf(globalCountdownModel.getVoter().size()));
            }else{
                tvvote.setText("0");
            }
            tvyears.setText("0");
            tvdays.setText("0");
            tvhours.setText("0");
            tvminutes.setText("0");
            tvseconds.setText("0");

            this.globalCountdownModel = globalCountdownModel;

            updateTime(System.currentTimeMillis(), position);
        }

        public void updateTime(long currentTime, int position){

            long diffTime = globalCountdownModel.getMillisTime() - currentTime;

            int secondsTime = (int) (diffTime / 1000) % 60;
            int minutesTime = (int) ((diffTime / (1000 * 60)) % 60);
            int hoursTime = (int) ((diffTime / (1000 * 60 * 60)) % 24);
            int dayTime = (int) ((diffTime / (1000 * 60 * 60 * 24)) % MainActivity.maxDay);
//            int monthTime = Math.abs((int) ((diffTime / (1000 * 60 * 60 * 24 * maxMonth)) % 13));
            int yearTime = (int) ((diffTime / DateUtils.YEAR_IN_MILLIS));

            tvseconds.setText(String.valueOf(secondsTime));
            tvminutes.setText(String.valueOf(minutesTime));
            tvhours.setText(String.valueOf(hoursTime));
            tvdays.setText(String.valueOf(dayTime));
//            months.setText(String.valueOf(monthTime));
            tvyears.setText(String.valueOf(yearTime));

            if (yearTime <= 0) {
                lyears.setVisibility(View.GONE);
            }
            if (dayTime <= 0 && yearTime <= 0) {
                ldays.setVisibility(View.GONE);
            }
            if (hoursTime <= 0 && dayTime <= 0 && yearTime <= 0) {
                lhours.setVisibility(View.GONE);
            }
            if (minutesTime <= 0 && hoursTime <= 0 && dayTime <= 0 && yearTime <= 0) {
                lminutes.setVisibility(View.GONE);
            }

            if (diffTime < 0 && globalcList.size() != 0 && globalHolder.size() != 0) {
                try {
                    new InfoDialog("Info", "Your " + globalcList.get(position).getTitle() + " Global Countdown has been finished").show(fm, "CountdownFinishGlobal");
                }catch (IllegalStateException e){
                    System.out.println("nyrot: " + e);
                }

                dbref.child("countdown").child(globalcList.get(position).getUuid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        globalcList.remove(position);
                        GlobalcAdapter adapter = new GlobalcAdapter(activity, globalcList, fm, recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                        recyclerView.setAdapter(adapter);
                    }
                });
            }

        }

    }
}
