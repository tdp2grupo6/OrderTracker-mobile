package ar.fiuba.tdp2grupo6.ordertracker.view.app;

import android.app.Application;
import android.content.Intent;

import ar.fiuba.tdp2grupo6.ordertracker.service.BootReceiver;

public class OrderTrackerApplication extends Application {


	@Override
	public void onCreate() {
		super.onCreate();

		// Corre el servicio, si ya esta corriendo es ignorada
		this.sendBroadcast(new Intent(this, BootReceiver.class));
	}

}