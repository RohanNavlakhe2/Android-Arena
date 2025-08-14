package com.yog.androidarena.firebase_notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.yog.androidarena.R;
import com.yog.androidarena.activity.MainActivity;
import com.yog.androidarena.util.Constants;

import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

public class FirebaseNotificationService extends FirebaseMessagingService {

    //This string is a device token (This method is called whenver Firebase generates new Device Token)
    @Override
    public void onNewToken(@NonNull String s) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Timber.d(task.getException(), "Task is not successful in onNewToken Method");
                        return;
                    }

                    // Get new Instance ID token
                    Timber.d("Task Successful in onNewToken");
                    String token = task.getResult();
                    Timber.d("Token :%s",token);
                });
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Timber.d("On Message Received");

        Map<String, String> data = remoteMessage.getData();
        String body = data.get("body");
        String title = data.get("title");
        String tab = data.get("tab");
        Timber.tag("TabNoti").d("Tab In Service: "+tab);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(Constants.TAB_ON_NOTIFICATION,tab);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Timber.tag("TabNoti").d("Tab In Service 2: "+intent.getStringExtra(Constants.TAB_ON_NOTIFICATION));

        //Here PendingIntent.FLAG_UPDATE_CURRENT plays important role because previously when I had put 0 then
        //it was not updating the data everytime when I was sending notification data (here tab) which I was passing
        // in intent as extra.

        //So PendingIntent.FLAG_UPDATE_CURRENT updates the extra.
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent,
               PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build();

            channel = new NotificationChannel("222", "my_channel", NotificationManager.IMPORTANCE_HIGH);
            assert nm != null;
            nm.createNotificationChannel(channel);
        }


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        getApplicationContext(), "222")
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.app_logo)) //ic_launcher_background
                        .setSmallIcon(R.drawable.app_logo)    //ic_launcher_foreground
                        //.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.electro))
                        .setContentText(body)
                        .setContentIntent(pi);


        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        assert nm != null;
        nm.notify(101, builder.build());
    }
}

