package ar.fiuba.tdp2grupo6.ordertracker.service.dataout;

import java.util.ArrayList;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import ar.fiuba.tdp2grupo6.ordertracker.business.ComentarioBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.PedidoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.service.datain.DataInService;


//Servicio que asegura terminar su proceso en caso que algun pedido de interrupcion de OS
public class DataOutService extends IntentService {


	public DataOutService() {
		super("DataOutService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			sync(this);
		} catch (Exception e) {
			// Restore interrupt status.
			Thread.currentThread().interrupt();
		}
	}

	// dgacitua
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("OT-LOG", "Sincronizando servicios de salida!");
		//Toast.makeText(this, "Sincronizando servicios de salida", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent,flags,startId);
	}

	public void sync(Context context) {


        try {
            String progressMensaje = null;

            try {
                // Sincroniza los pedidos pendientes
                try {
					PedidoBZ pedidoBZ = new PedidoBZ(context);
					pedidoBZ.sincronizarUp();
                } catch (Exception e) {
                    //progressMensaje = context.getResources().getString(R.string.text_dataout_tx) + ": " + e.getMessage();
                }

				// dgacitua: Sincronizar comentarios enviados
				try {
					ComentarioBZ cbz = new ComentarioBZ(context);
					cbz.sincronizar();
				} catch (Exception e) {
					e.printStackTrace();
				}

                // Sincroniza geo
                try {
                    //ArrayList<Geo> listGeo = GeoBZ.GeoBuscar(context, null, false, null);
                    //for (Geo geo : listGeo) {
                    //    GeoBZ.GeoGuardarWS(context, url, login.session, registrationId, macId, login, geo);
                    //}
                } catch (Exception e) {
                    //progressMensaje = context.getResources().getString(R.string.text_dataout_geo) + ": " + e.getMessage();
                }

                // Depuracion de datos
                try {
                    //Depura las geoposiciones
                    //Geo ultimoGeo = GeoBZ.GeoObtenerUltimo(context, null);
                    //GeoBZ.GeoDepurar(context, ultimoGeo);

                    //Depura las tareas: deja las ultimas 100
                    //TareaBZ.tareaDepurar(context);
                } catch (Exception e) {
                    //progressMensaje = context.getResources().getString(R.string.text_dataout_depuracion) + ": " + e.getMessage();
                }

                //Broadcast de finalizacion de servicio
                //Intent i = new Intent(DATA_OUT_FINISH_BROADCAST);
                //LocalBroadcastManager.getInstance(context).sendBroadcast(i);

            } catch (Exception e) {
                progressMensaje = e.getMessage();
            }

            // Llama al servicio in para hacerlo secuencia
            Intent newintent = new Intent(context, DataInService.class);
            context.startService(newintent);
            //DataInService service = new DataInService();
            //service.sync(context);

        } catch (Exception e) {
            // Graba en el Log
            String err = e.getMessage();
        }

	}
}