package ar.fiuba.tdp2grupo6.ordertracker.view.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import ar.fiuba.tdp2grupo6.ordertracker.business.AutenticacionBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.service.BootReceiver;

public class OrderTrackerApplication extends Application {
	public static final int NOTIFICACION_ID_FCM = 30003;

	private static AutenticacionResponse autenticacionResponse;


	@Override
	public void onCreate() {
		super.onCreate();
	}

	public AutenticacionResponse getAutentication() {
		if (OrderTrackerApplication.autenticacionResponse == null) {
			AutenticacionBZ autenticacionBZ = new AutenticacionBZ(this);
			OrderTrackerApplication.autenticacionResponse = autenticacionBZ.getAutenticacion();
		}
		return OrderTrackerApplication.autenticacionResponse;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	public void clearAutentication() {
		AutenticacionBZ autenticacionBZ = new AutenticacionBZ(this);
		autenticacionBZ.logout();

		OrderTrackerApplication.autenticacionResponse = null;
	}

	public void runService() {
		// Corre el servicio, si ya esta corriendo es ignorada
		this.sendBroadcast(new Intent(this, BootReceiver.class));
	}

}