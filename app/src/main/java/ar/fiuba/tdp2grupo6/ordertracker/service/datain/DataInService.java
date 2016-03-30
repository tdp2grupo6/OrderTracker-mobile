package ar.fiuba.tdp2grupo6.ordertracker.service.datain;

//import android.R;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import ar.fiuba.tdp2grupo6.ordertracker.business.ClienteBZ;
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

	public void sync(Context context) {
		String progressMensaje = null;

		try {

			// Sincroniza los clientes
			try {
				ClienteBZ clienteBZ =  new ClienteBZ(context);
				clienteBZ.Sincronizar();
			} catch (Exception e) {
				//progressMensaje = context.getResources().getString(R.string.text_datain_txarrastre) + ": " + e.getMessage();
			}

			// Sincroniza los productos
			try {
				ProductoBZ productoBZ =  new ProductoBZ(context);
				productoBZ.Sincronizar();
			} catch (Exception e) {
				//progressMensaje = context.getResources().getString(R.string.text_datain_txarrastre) + ": " + e.getMessage();
			}

			// Sincroniza transacciones comunes
			try {
				//TransaccionBZ.TransaccionComunSync(context, url, login.session, esNuevaSession, this);
			} catch (Exception e) {
				//progressMensaje = context.getResources().getString(R.string.text_datain_txcomun) + ": " + e.getMessage();
			}

		} catch (Exception e) {
			progressMensaje = e.getMessage();
		}

	}

}