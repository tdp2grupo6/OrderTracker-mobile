package ar.fiuba.tdp2grupo6.ordertracker.service.push;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.view.LoginActivity;
import ar.fiuba.tdp2grupo6.ordertracker.view.app.OrderTrackerApplication;


public class PushMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message){
        try {

            String from = message.getFrom();
            Map data = message.getData();
            String titleText = message.getNotification().getTitle();
            String messageText = message.getNotification().getBody();

            showNotification(titleText, messageText);
            //Log.d("LOG", "Refreshed token: From->" + from + " data->" + data.toString());
        } catch (Exception e) {

        }
    }

    public void showNotification (String notificationTitle, String notificationMensaje){

        try {
            NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            if (mBuilder != null && mNotificationManager != null) {


                Intent resultIntent = new Intent(this, LoginActivity.class);
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_ONE_SHOT);

                // Prepara la notificacion para mostrar
                mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setContentIntent(contentIntent);
                mBuilder.setTicker(notificationTitle);
                mBuilder.setContentTitle(notificationTitle);
                mBuilder.setContentText(notificationMensaje);
                mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMensaje));
                mBuilder.setSmallIcon(R.drawable.sync_green);
                mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.sync_green));
                mBuilder.setProgress(0, 0, false);
                mBuilder.setAutoCancel(true);
                mBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                mNotificationManager.notify(OrderTrackerApplication.NOTIFICACION_ID_FCM, mBuilder.build());
            }
        } catch (Exception e) {
            String err = e.getLocalizedMessage();
        }
    }
}