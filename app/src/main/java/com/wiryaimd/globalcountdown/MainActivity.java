package com.wiryaimd.globalcountdown;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wiryaimd.globalcountdown.adapter.GlobalcAdapter;
import com.wiryaimd.globalcountdown.adapter.LocalcAdapter;
import com.wiryaimd.globalcountdown.fragment.GlobalcFragment;
import com.wiryaimd.globalcountdown.fragment.LocalcFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Timer timer;
    private Handler handler = new Handler();
    private static Runnable runnable;
    public static int maxDay;

    private static int adsCount = 0;
    private static Activity activity;

    private static boolean isAdfbloaded = false;
    private static InterstitialAd interstitialAds;
    private static com.facebook.ads.InterstitialAd interstitialAdfb;

    public static void runLocalCountdown(ArrayList<LocalcAdapter.MyHolder> localcHolder) {
        runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (localcHolder) {
                    if (adsCount <= 25){
                        adsCount += 1;
                        System.out.println("asu tenan llll:  " + adsCount);
                        if (adsCount == 25){
                            if (interstitialAds != null) {
                                interstitialAds.show(activity);
                            }else if (isAdfbloaded){
                                interstitialAdfb.show();
                            }
                        }
                    }
                    long currentTime = System.currentTimeMillis();
                    for (int i = 0; i < localcHolder.size(); i++) {
                        LocalcAdapter.MyHolder myHolder = localcHolder.get(i);
                        myHolder.updateTime(currentTime, i);
                    }
                }
            }
        };
    }

    public static void runGlobalCountdown(ArrayList<GlobalcAdapter.GlobalHolder> globalHolder) {
        runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (globalHolder) {
                    if (adsCount <= 25){
                        adsCount += 1;
                        System.out.println("asu tenan:  " + adsCount);
                        if (adsCount == 25){
                            if (interstitialAds != null) {
                                interstitialAds.show(activity);
                            }else if (isAdfbloaded){
                                interstitialAdfb.show();
                            }
                        }
                    }
                    long currentTime = System.currentTimeMillis();
                    for (int i = 0; i < globalHolder.size(); i++) {
                        GlobalcAdapter.GlobalHolder myHolder = globalHolder.get(i);
                        myHolder.updateTime(currentTime, i);
                    }
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationChannel("CountdownNotification", "ngentod lo semua");

        activity = MainActivity.this;

        bottomNav = findViewById(R.id.main_bottomnav);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 1000, 1000);

        Calendar calendar = Calendar.getInstance();
        maxDay = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);

        AdRequest adRequest = new AdRequest.Builder().build();

        // untuk testing mode katanya mamank
//        AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);

        AudienceNetworkAds.initialize(MainActivity.this);

        interstitialAdfb = new com.facebook.ads.InterstitialAd(this, "fb_ads_id");
        InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                System.out.println("fb suckess");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                System.out.println("wasuuu: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                isAdfbloaded = true;
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };

        InterstitialAd.load(MainActivity.this, getString(R.string.inters1), adRequest, new InterstitialAdLoadCallback(){
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                System.out.println("wanjrod successss");
                interstitialAds = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                System.out.println("tai kucing sangean: " + loadAdError.getMessage());
                interstitialAds = null;
                interstitialAdfb.loadAd(interstitialAdfb.buildLoadAdConfig().withAdListener(interstitialAdListener).build());
            }
        });

        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.localcoutdown){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_left, R.anim.slide_out_right);
                    ft.replace(R.id.main_frame, new LocalcFragment(MainActivity.this)).commit();
                    return true;
                }

                if (item.getItemId() == R.id.globalcoutdown){
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.setCustomAnimations(R.anim.slide_right, R.anim.slide_out_left);
                    ft.replace(R.id.main_frame, new GlobalcFragment(MainActivity.this)).commit();
                    return true;
                }

                return false;
            }
        });

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new GlobalcFragment(MainActivity.this));
        ft.commit();
    }

    public void notificationChannel(String title, String msg){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CountdownNotification", title, importance);
            channel.setDescription(msg);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
