package com.wiryaimd.globalcountdown.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.wiryaimd.globalcountdown.R;
import com.wiryaimd.globalcountdown.model.LocalCountdownModel;

import java.util.ArrayList;

public class LocalcReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (intent.getIntExtra("CountdownId", -1) != -1) {
            ArrayList<LocalCountdownModel> localcList = PreferencesManager.getCountdown(context);
            if (localcList != null) {
                localcList.remove(intent.getIntExtra("CountdownId", 0) - 1);
                PreferencesManager.saveCountdown(context, localcList);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CountdownNotification");
        builder.setSmallIcon(R.drawable.ic_notifon);
        builder.setContentTitle("[" + intent.getStringExtra("CountdownTitle") + "]" + " Local Countdown has been finished!");
        builder.setContentText("Countdown " + intent.getStringExtra("CountdownTitle") + " " + intent.getStringExtra("CountdownDate"));

        notification.notify(intent.getIntExtra("CountdownId", 0), builder.build());
    }

}
