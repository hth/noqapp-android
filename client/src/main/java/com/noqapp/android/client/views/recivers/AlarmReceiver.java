package com.noqapp.android.client.views.recivers;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.LaunchActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, LaunchActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.mipmap.launcher)
                .setContentTitle(title)
                .setContentText(body).setSound(alarmSound)
                .setAutoCancel(true).setWhen(when)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(55, mNotifyBuilder.build());
    }
}

//        extends BroadcastReceiver {
//    int notifyId=1;
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        NotificationCompat.Builder mNotify=new NotificationCompat.Builder(context);
//        mNotify.setSmallIcon(R.mipmap.launcher);
//        mNotify.setContentTitle("Coding");
//        mNotify.setContentText("INVENTO: Coding competition is going to be conducted today.");
//        Intent resultIntent=new Intent(context,LaunchActivity.class);
//        TaskStackBuilder stackBuilder=TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(LaunchActivity.class); //add the to-be-displayed activity to the top of stack
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//        mNotify.setContentIntent(resultPendingIntent);
//        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(notifyId,mNotify.build());
//    }
//}