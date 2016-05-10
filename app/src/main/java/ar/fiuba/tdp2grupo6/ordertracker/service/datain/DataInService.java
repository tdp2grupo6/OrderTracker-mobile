package ar.fiuba.tdp2grupo6.ordertracker.service.datain;

//import android.R;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.business.AgendaBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.business.ProductoBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

//Servicio que asegura terminar su proceso en caso que algun pedido de interrupcion de OS
public class DataInService extends IntentService {

	public DataInService() {
		super("DataInService");
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
		Log.d("OT-LOG", "Sincronizando servicios de entrada!");
		Toast.makeText(this, "Sincronizando servicios de entrada", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent,flags,startId);
	}

	public void sync(Context context) {
		String progressMensaje = null;

		try {

			// Sincroniza los clientes
			try {
				ClienteBZ clienteBZ =  new ClienteBZ(context);
				clienteBZ.sincronizar();
			} catch (Exception e) {
				//progressMensaje = context.getResources().getString(R.string.text_datain_txarrastre) + ": " + e.getMessage();
			}

			// Sincroniza los items
			try {
				ProductoBZ productoBZ =  new ProductoBZ(context);
				ArrayList<Producto> productos = productoBZ.sincronizar();

				if (productos != null && productos.size() > 0) {
					//Sincroniza la imagen de los items que ho hayan sido ya descargados
					ImagenBZ imagenBZ =  new ImagenBZ();
					for(Producto producto: productos) {
						boolean existeimagen = imagenBZ.existe(producto.getNombreImagenMiniatura());
						if (!existeimagen) {
							productoBZ.sincronizarImagenMini(producto);
						}

						existeimagen = imagenBZ.existe(producto.getNombreImagen());
						if (!existeimagen) {
							productoBZ.sincronizarImagen(producto);
						}
					}
				}
			} catch (Exception e) {
				//progressMensaje = context.getResources().getString(R.string.text_datain_txarrastre) + ": " + e.getMessage();
			}

			// Sincroniza la agenda
			try {
				AgendaBZ agendaBZ =  new AgendaBZ(context);
				agendaBZ.sincronizar(false);
			} catch (Exception e) {
				//progressMensaje = context.getResources().getString(R.string.text_datain_txcomun) + ": " + e.getMessage();
			}

		} catch (Exception e) {
			progressMensaje = e.getMessage();
		}

	}

}