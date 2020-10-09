package com.noqapp.android.client.views.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.noqapp.android.client.R;
import com.noqapp.android.client.views.activities.LaunchActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String body = intent.getStringExtra("body");
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
            .getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "channel-02";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.notification_icon);
        Intent notificationIntent = new Intent(context, LaunchActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
            notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
            context, channelId)
            .setColor(ContextCompat.getColor(context, R.color.colorMobile))
            .setSmallIcon(getNotificationIcon())
            .setLargeIcon(bm)
            .setContentTitle(title)
            .setContentText(body).setSound(alarmSound)
            .setAutoCancel(true).setWhen(when)
            .setContentIntent(pendingIntent)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        notificationManager.notify(55, mNotifyBuilder.build());
    }

    private int getNotificationIcon() {
        return R.mipmap.notification_icon;
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