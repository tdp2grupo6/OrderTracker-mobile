package ar.fiuba.tdp2grupo6.ordertracker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import ar.fiuba.tdp2grupo6.ordertracker.service.dataout.DataOutAlarmReceiver;

//Clase que implementa un receptor al iniciar el OS
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {

			DataOutAlarmReceiver.startAlarm(context);

		} catch (Exception e) {
		}
	}

}