package ar.fiuba.tdp2grupo6.ordertracker.view.app;

import android.app.Application;
import android.content.Intent;

import ar.fiuba.tdp2grupo6.ordertracker.business.AutenticacionBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AutenticacionResponse;
import ar.fiuba.tdp2grupo6.ordertracker.service.BootReceiver;

public class OrderTrackerApplication extends Application {
	public AutenticacionResponse autenticacionResponse;


	@Override
	public void onCreate() {
		super.onCreate();
	}

	public AutenticacionResponse getAutentication() {
		if (autenticacionResponse == null) {
			AutenticacionBZ autenticacionBZ = new AutenticacionBZ(this);
			autenticacionResponse = autenticacionBZ.getAutenticacion();
		}
		return autenticacionResponse;
	}

	public void clearAutentication() {
		if (autenticacionResponse != null) {
			AutenticacionBZ autenticacionBZ = new AutenticacionBZ(this);
			autenticacionBZ.logout();

			autenticacionResponse = null;
		}
	}

	public void runService() {
		// Corre el servicio, si ya esta corriendo es ignorada
		this.sendBroadcast(new Intent(this, BootReceiver.class));
	}

}