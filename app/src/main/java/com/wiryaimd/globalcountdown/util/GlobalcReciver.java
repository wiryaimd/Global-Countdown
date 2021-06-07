package com.wiryaimd.globalcountdown.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wiryaimd.globalcountdown.R;

public class GlobalcReciver extends BroadcastReceiver {

    private DatabaseReference dbref;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        dbref = FirebaseDatabase.getInstance().getReference();
        String uuid = intent.getStringExtra("CountdownUUID");
        System.out.println("cek entod");
        if (uuid != null) {
            dbref.child("countdown").child(uuid).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {

                }
            });

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CountdownNotification");
            builder.setSmallIcon(R.drawable.ic_notifon);
            builder.setContentTitle("[" + intent.getStringExtra("CountdownTitle") + "]" + " Global Countdown has been finished!");
            builder.setContentText("Countdown " + intent.getStringExtra("CountdownTitle") + " " + intent.getStringExtra("CountdownDate"));

            System.out.println("notif: " + intent.getIntExtra("CountdownId", 0));
            notification.notify(intent.getIntExtra("CountdownId", 0), builder.build());
        }
    }

}
