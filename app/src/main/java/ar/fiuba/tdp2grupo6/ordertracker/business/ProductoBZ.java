package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

/**
 * Created by dgacitua on 30-03-16.
 */
public class ProductoBZ {
    private Context mContext;
    private WebDA mWeb;
    private SqlDA mSql;

    public ProductoBZ(Context context) {
        this.mContext = context;
        this.mWeb = new WebDA(context);
        this.mSql = new SqlDA(context);
    }

    public ProductoBZ(Context context, WebDA service) {
        this.mContext = context;
        this.mWeb = service;
    }

    public ProductoBZ(Context context, SqlDA dataBase) {
        this.mContext = context;
        this.mSql = dataBase;
    }

    public ProductoBZ(Context context, WebDA service, SqlDA dataBase) {
        this.mContext = context;
        this.mWeb = service;
        this.mSql = dataBase;
    }

    public ArrayList<Producto> sincronizar() throws ServiceException, BusinessException {
        ArrayList<Producto> response = new ArrayList<Producto>();
        try {

            AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
            ResponseObject responseDA = mWeb.getProductos(autenticacionBZ.getAutenticacion());

            if (responseDA.getData() != null) {
                //Elimina todos los Productos
                mSql.productoVaciar();

                //Graba cada producto en la BD
                try {
                    JSONArray data = new JSONArray(responseDA.getData());
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject itemjson = data.getJSONObject(i);

                        Producto producto = new Producto(itemjson);
                        mSql.productoGuardar(producto);

                        response.add(producto);
                    }
                } catch (JSONException jex) {
                    throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), jex.getMessage()));
                }
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return response;
    }

    public void sincronizarImagenMini(Producto producto) throws ServiceException, BusinessException {

        try {

            AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
            ResponseObject responseDA = mWeb.getProductosImagenMiniatura(autenticacionBZ.getAutenticacion(), producto.rutaMiniatura);

            if (responseDA.getBitmap() != null) {
                //Graba en la SD
                ImagenBZ filaBZ  = new ImagenBZ();
                filaBZ.grabar(producto.getNombreImagenMiniatura(), responseDA.getBitmap());
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), e.getMessage()));
        }

    }

    public void sincronizarImagen(Producto producto) throws ServiceException, BusinessException {

        try {

            AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
            ResponseObject responseDA = mWeb.getProductosImagen(autenticacionBZ.getAutenticacion(), producto.rutaImagen);

            if (responseDA.getBitmap() != null) {
                //Graba en la SD
                ImagenBZ filaBZ  = new ImagenBZ();
                filaBZ.grabar(producto.getNombreImagen(), responseDA.getBitmap());
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), e.getMessage()));
        }

    }

    public ArrayList<Producto> listar() throws BusinessException {
        ArrayList<Producto> response = new ArrayList<Producto>();
        try {
            response = mSql.productoBuscar(0);
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return response;
    }
}
