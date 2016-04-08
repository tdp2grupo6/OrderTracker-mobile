package ar.fiuba.tdp2grupo6.ordertracker.service.datain;

//import android.R;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

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

			// Sincroniza los productos
			try {
				ProductoBZ productoBZ =  new ProductoBZ(context);
				ArrayList<Producto> productos = productoBZ.sincronizar();

				if (productos != null && productos.size() > 0) {
					//Sincroniza la imagen de los productos que ho hayan sido ya descargados
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
					//Descarga las imagenes para los productos si no lo tiene

				}

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