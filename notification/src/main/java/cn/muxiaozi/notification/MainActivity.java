package cn.muxiaozi.notification;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private android.support.v4.app.NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notifyID = 1;
        mNotifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText("You've received new messages.")
                .setSmallIcon(R.mipmap.ic_launcher);

        for(int i = 0; i < 100; i++){
            mNotifyBuilder.setContentText("starting..." + i);
            mNotifyBuilder.setProgress(100, i, false);
            // Because the ID remains unchanged, the existing notification is
            // updated.
            mNotificationManager.notify(
                    notifyID,
                    mNotifyBuilder.build());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
