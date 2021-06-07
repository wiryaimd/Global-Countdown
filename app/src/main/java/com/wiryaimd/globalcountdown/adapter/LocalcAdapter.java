package com.wiryaimd.globalcountdown.adapter;

import android.app.Activity;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.globalcountdown.MainActivity;
import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.fragment.DetailcountdownDialog;
import com.wiryaimd.globalcountdown.fragment.InfoDialog;
import com.wiryaimd.globalcountdown.model.LocalCountdownModel;
import com.wiryaimd.globalcountdown.util.PreferencesManager;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

public class LocalcAdapter extends RecyclerView.Adapter<LocalcAdapter.MyHolder> {

    private Activity activity;
    private ArrayList<LocalCountdownModel> localcList;
    private ArrayList<MyHolder> localcHolder;

    private FragmentManager fm;
    private RecyclerView recyclerView;

    public LocalcAdapter(Activity activity, ArrayList<LocalCountdownModel> localcList, FragmentManager fm, RecyclerView recyclerView){
        this.activity = activity;
        this.localcList = localcList;
        this.fm = fm;
        this.recyclerView = recyclerView;

        localcHolder = new ArrayList<>();
        MainActivity.runLocalCountdown(localcHolder);
    }

    @NonNull
    @Override
    public LocalcAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_localcountdown, parent, false);
        return new MyHolder(view);
    }

    @Override
    public int getItemCount() {
        return localcList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull LocalcAdapter.MyHolder holder, int position) {

        LocalCountdownModel localCountdownModel = localcList.get(position);

        holder.setItem(localCountdownModel, position);
        holder.imgmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DetailcountdownDialog(activity, localcList, position, recyclerView).show(fm, "DetailFragment");
            }
        });

        synchronized (localcHolder) {
            localcHolder.add(holder);
        }

    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private LocalCountdownModel localCountdownModel;
        public LinearLayout lyear, lmonths, ldays, lhours, lminutes, lseconds;

        public TextView tvtitle, tvdate, year, months, days, hours, minutes, seconds;
        public ImageView imgmenu;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            imgmenu = itemView.findViewById(R.id.itemlocal_menu);
            tvtitle = itemView.findViewById(R.id.itemlocal_title);
            tvdate = itemView.findViewById(R.id.itemlocal_todate);
            year = itemView.findViewById(R.id.itemlocal_year);
            months = itemView.findViewById(R.id.itemlocal_months);
            days = itemView.findViewById(R.id.itemlocal_days);
            hours = itemView.findViewById(R.id.itemlocal_hours);
            minutes = itemView.findViewById(R.id.itemlocal_minutes);
            seconds = itemView.findViewById(R.id.itemlocal_seconds);

            lyear = itemView.findViewById(R.id.itemlocal_linearyears);
            lmonths = itemView.findViewById(R.id.itemlocal_linearmonths);
            ldays = itemView.findViewById(R.id.itemlocal_lineardays);
            lhours = itemView.findViewById(R.id.itemlocal_linearhours);
            lminutes = itemView.findViewById(R.id.itemlocal_linearminutes);
            lseconds = itemView.findViewById(R.id.itemlocal_linearsecond);

        }

        public void setItem(LocalCountdownModel localCountdownModel, int position){
            tvtitle.setText(localCountdownModel.getTitle());
            String date = localCountdownModel.getDate().getYear() + "-" + new DateFormatSymbols().getMonths()[Integer.parseInt(localCountdownModel.getDate().getMonth())] + "-" + localCountdownModel.getDate().getDay() + " " + String.format(Locale.getDefault(), "%02d", Integer.parseInt(localCountdownModel.getDate().getHours())) + ":" + String.format(Locale.getDefault(), "%02d", Integer.parseInt(localCountdownModel.getDate().getMinute()));
            tvdate.setText(date);

            year.setText("0");
            months.setText("0");
            days.setText("0");
            hours.setText("0");
            minutes.setText("0");
            seconds.setText("0");

            this.localCountdownModel = localCountdownModel;

            updateTime(System.currentTimeMillis(), position);
        }

        public void updateTime(long currentTime, int position){

//            System.out.println("pos: " + position);

            long diffTime = localCountdownModel.getMillisTime() - currentTime;

//            System.out.println("diff: " + diffTime);
//            System.out.println("=======================");

            int secondsTime = (int) (diffTime / 1000) % 60;
            int minutesTime = (int) ((diffTime / (1000 * 60)) % 60);
            int hoursTime = (int) ((diffTime / (1000 * 60 * 60)) % 24);
            int dayTime = (int) ((diffTime / (1000 * 60 * 60 * 24)) % MainActivity.maxDay);
//            int monthTime = Math.abs((int) ((diffTime / (1000 * 60 * 60 * 24 * maxMonth)) % 13));
            int yearTime = (int) ((diffTime / DateUtils.YEAR_IN_MILLIS));

            seconds.setText(String.valueOf(secondsTime));
            minutes.setText(String.valueOf(minutesTime));
            hours.setText(String.valueOf(hoursTime));
            days.setText(String.valueOf(dayTime));
//            months.setText(String.valueOf(monthTime));
            year.setText(String.valueOf(yearTime));

            if (yearTime <= 0) {
                lyear.setVisibility(View.GONE);
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

            if (diffTime < 0 && localcList.size() != 0 && localcHolder.size() != 0) {
                try {
                    new InfoDialog("Info", "Your " + localcList.get(position).getTitle() + " Countdown has been finished").show(fm, "CountdownFinish");
                }catch (IllegalStateException e){
                    System.out.println("nyrot: " + e);
                }

                localcList.remove(position);
                PreferencesManager.saveCountdown(activity, localcList);

                recyclerView.setLayoutManager(new LinearLayoutManager(activity));
                LocalcAdapter localcAdapter = new LocalcAdapter(activity, localcList, fm, recyclerView);

                recyclerView.setAdapter(localcAdapter);
            }

        }

    }
}
