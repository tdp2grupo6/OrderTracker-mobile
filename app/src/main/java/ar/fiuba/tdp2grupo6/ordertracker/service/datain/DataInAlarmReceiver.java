package ar.fiuba.tdp2grupo6.ordertracker.service.datain;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import ar.fiuba.tdp2grupo6.ordertracker.service.dataout.DataOutService;

public class DataInAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent newintent = new Intent(context, DataInService.class);
		context.startService(newintent);
	}

	public static void startAlarm(Context context) {
		final int periodo = 120000;//600000;

		// Genera un servicio que se corre cada PERIOD de tiempo
		AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, DataInAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
		// Establece la repeticion de la alarma
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), periodo, pi);
	}
}