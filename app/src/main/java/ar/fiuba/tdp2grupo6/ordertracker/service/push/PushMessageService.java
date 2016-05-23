package ar.fiuba.tdp2grupo6.ordertracker.service.push;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;


public class PushMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();

        Log.d("LOG", "Refreshed token: From->" + from + " data->" + data.toString());
    }
}