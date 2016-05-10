package ar.fiuba.tdp2grupo6.ordertracker.view.app;

import android.app.Application;
import android.content.Intent;

import ar.fiuba.tdp2grupo6.ordertracker.business.AutenticacionBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.service.BootReceiver;

public class OrderTrackerApplication extends Application {
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

	public void clearAutentication() {
		if (OrderTrackerApplication.autenticacionResponse != null) {
			AutenticacionBZ autenticacionBZ = new AutenticacionBZ(this);
			autenticacionBZ.logout();

			OrderTrackerApplication.autenticacionResponse = null;
		}
	}

	public void runService() {
		// Corre el servicio, si ya esta corriendo es ignorada
		this.sendBroadcast(new Intent(this, BootReceiver.class));
	}

}