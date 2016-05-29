package ar.fiuba.tdp2grupo6.ordertracker.business;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Comentario;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.ResponseObject;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.AutorizationException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.BusinessException;
import ar.fiuba.tdp2grupo6.ordertracker.contract.exceptions.ServiceException;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.SqlDA;
import ar.fiuba.tdp2grupo6.ordertracker.dataaccess.WebDA;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoBZ {
    private Context mContext;
    private WebDA mWeb;
    private SqlDA mSql;

    public PedidoBZ(Context context) {
        this.mContext = context;
        this.mWeb = new WebDA(context);
        this.mSql = new SqlDA(context);
    }

    public PedidoBZ(Context context, WebDA service) {
        this.mContext = context;
        this.mWeb = service;
    }

    public PedidoBZ(Context context, SqlDA dataBase) {
        this.mContext = context;
        this.mSql = dataBase;
    }

    public PedidoBZ(Context context, WebDA service, SqlDA dataBase) {
        this.mContext = context;
        this.mWeb = service;
        this.mSql = dataBase;
    }

    public Pedido actualizarConfirmado(Pedido pedido) throws ServiceException, BusinessException {
        pedido.estado = Pedido.ESTADO_CONFIRMADO;
        return  actualizar(pedido);
    }

    public Pedido actualizarNuevo(Pedido pedido) throws ServiceException, BusinessException {
        pedido.estado = Pedido.ESTADO_NUEVO;
        return  actualizar(pedido);
    }

    private Pedido actualizar(Pedido pedido) throws ServiceException, BusinessException {
        try {
            //Actualiza el pedido en la bd
            if (pedido.id == 0) {
                pedido = mSql.pedidoGuardar(pedido);
            } else {
                mSql.pedidoActualizar(pedido);
            }

            //Actualiza el pedido item en la bd
            for(Map.Entry<String, PedidoItem> entry : pedido.items.entrySet()) {
                String key = entry.getKey();
                PedidoItem item = entry.getValue();

                if (item.id == 0 && item.cantidad > 0) {
                    item = mSql.pedidoItemGuardar(pedido.id, item);
                } else if (item.id > 0 && item.cantidad > 0) {
                    mSql.pedidoItemActualizar(pedido.id, item);
                } else if (item.id == 0 && item.cantidad == 0) {
                    // no hace nada
                } else if (item.id > 0 && item.cantidad == 0) {
                    mSql.pedidoItemEliminar(pedido.id, item.id);
                } else {
                    // no hace nada
                }
            }

            //actualiza
            pedido.getImporte(true);
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return pedido;
    }

    public ArrayList<Pedido> buscar(long clienteId, long pedidoId, int estado, boolean fullCatalogo) throws BusinessException {
        ArrayList<Pedido> pedidos = null;
        try {
            ArrayList<PedidoItem> pedidoitems = null;
            pedidos = mSql.pedidoBuscar(pedidoId, 0, clienteId, estado, true);

            if (pedidos != null && pedidos.size() > 0) {
                long clienteIdAux = 0;
                Cliente cliente = null;

                for (Pedido pedido: pedidos) {

                    //Obtiene el cliente
                    if (clienteIdAux != pedido.clienteId) {
                        ClienteBZ clienteBZ = new ClienteBZ(this.mContext);
                        cliente = clienteBZ.obtener(pedido.clienteId, "");
                        clienteIdAux = pedido.clienteId;
                    }
                    pedido.cliente = cliente;

                    if (fullCatalogo) {
                        //Obtiene los datos grabados para ese Pedido
                        pedidoitems = mSql.pedidoItemBuscar(0, pedido.id, false);

                        //Carga el catalogo
                        ArrayList<Producto> catalogo = mSql.productoBuscar(0);
                        for (Producto producto : catalogo) {
                            PedidoItem item = new PedidoItem();
                            item.productoId = producto.id;
                            item.producto = producto;
                            item.cantidad = 0;

                            pedido.items.put(String.valueOf(producto.id), item);
                        }

                        //Actualiza el catalogo si ya tenia parte del pedido cargado
                        for (PedidoItem pedidoItem : pedidoitems) {
                            boolean borrarItem = true;

                            //Busca si el producto esta en el catalogo, sino lo borra
                            if (pedidoItem.cantidad > 0) {
                                PedidoItem pedidoItemExistente = pedido.items.get(String.valueOf(pedidoItem.productoId));
                                if (pedidoItemExistente != null) {
                                    borrarItem = false;

                                    //Actualiza los datos
                                    pedidoItemExistente.id = pedidoItem.id;
                                    pedidoItemExistente.cantidad = pedidoItem.cantidad;
                                }
                            }

                            //Si no esta el item del pedido en el catalogo actual, lo borra
                            if (borrarItem)
                                mSql.pedidoItemEliminar(pedidoItem.id, 0);
                        }

                    } else {
                        //Obtiene los datos grabados para ese Pedido
                        pedidoitems = mSql.pedidoItemBuscar(0, pedido.id, true);

                        //Actualiza el catalogo si ya tenia parte del pedido cargado
                        for (PedidoItem pedidoItem : pedidoitems) {
                            boolean borrarItem = true;

                            //Busca si el producto esta en el catalogo, sino lo borra
                            if (pedidoItem.cantidad > 0) {
                                borrarItem = false;

                                pedido.items.put(String.valueOf(pedidoItem.productoId), pedidoItem);
                            }

                            //Si no esta el item del pedido en el catalogo actual, lo borra
                            if (borrarItem)
                                mSql.pedidoItemEliminar(pedidoItem.id, 0);
                        }

                    }

                    //regenera los maps del pedido
                    pedido.generateMaps();
                    pedido.getImporte(true);
                }
            }
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }

        return pedidos;
    }

    public Pedido obtenerParaCliente(long visitaId, long clienteId) throws BusinessException {
        Pedido response = null;
        try {

            ArrayList<Pedido> pedidos = this.buscar(clienteId, 0, Pedido.ESTADO_NUEVO, true);
            if (pedidos != null && pedidos.size() > 0) {
                //Obtiene los datos grabados para ese Pedido
                response = pedidos.get(0);

            } else {
                //Borra todos los pendientes (solo puede existier un pedido pendiente)
                borrarPendientes();

                //busca el cliente
                ClienteBZ clienteBZ = new ClienteBZ(this.mContext);
                Cliente cliente = clienteBZ.obtener(clienteId, "");

                //Crea un pedido nuevo
                response = new Pedido();
                response.visitaId = visitaId;
                response.clienteId = clienteId;
                response.cliente = cliente;

                //Arma el catalogo
                ArrayList<Producto> catalogo = mSql.productoBuscar(0);
                for (Producto producto: catalogo) {
                    PedidoItem item = new PedidoItem();
                    item.productoId = producto.id;
                    item.producto = producto;
                    item.cantidad = 0;

                    response.items.put(String.valueOf(producto.id), item);
                }

                //regenera los maps del pedido
                response.generateMaps();
                response.getImporte(true);
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return response;
    }

    public Pedido obtenerParaConfirmar(long pedidoId) throws BusinessException {
        Pedido response = null;
        try {

            ArrayList<Pedido> pedidos = this.buscar(0, pedidoId, Pedido.ESTADO_NUEVO, false);
            if (pedidos != null && pedidos.size() > 0) {
                //Obtiene los datos grabados para ese Pedido
                response = pedidos.get(0);
            }
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
        return response;
    }

    public void confirmar(long pedidoId) throws BusinessException {
        try {

            ArrayList<Pedido> pedidos = mSql.pedidoBuscar(pedidoId, 0, 0, -1, false);

            if (pedidos != null && pedidos.size() > 0) {
                Pedido pedido = pedidos.get(0);
                pedido.estado =  Pedido.ESTADO_CONFIRMADO;

                mSql.pedidoActualizar(pedido);

                VisitaBZ visitaBZ = new VisitaBZ(mContext);
                visitaBZ.modificarEstado(pedido.visitaId, false, true);
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void confirmar(Pedido pedido, boolean trySend) throws AutorizationException, BusinessException {
        try {

            if (pedido != null && pedido.items.values().size() > 0) {

                // Se fija si algun item se borro
                ArrayList<PedidoItem> pedidosOld = mSql.pedidoItemBuscar(0, pedido.id, false);
                for (PedidoItem pedidoItem : pedidosOld) {
                    if (!pedido.items.containsKey(String.valueOf(pedidoItem.productoId)))
                        mSql.pedidoItemEliminar(pedidoItem.id, 0);
                }

                pedido.fechaRealizado = new Date();
                pedido.estado = Pedido.ESTADO_CONFIRMADO;
                mSql.pedidoActualizar(pedido);


                VisitaBZ visitaBZ = new VisitaBZ(mContext);
                visitaBZ.modificarEstado(pedido.visitaId, false, true);

                if (trySend) {
                    this.enviarPedido(pedido);
                }
            }
        } catch (AutorizationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void borrar(long pedidoId) throws BusinessException {
        try {

            mSql.pedidoItemEliminar(0, pedidoId);
            mSql.pedidoEliminar(pedidoId);

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void borrarPendientes() throws BusinessException {
        try {

            ArrayList<Pedido> pedidos = mSql.pedidoBuscar(0, 0, 0, Pedido.ESTADO_NUEVO, false);
            for (Pedido pedido: pedidos) {
                mSql.pedidoItemEliminar(0, pedido.id);
                mSql.pedidoEliminar(pedido.id);
            }

        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void sincronizarUp() throws AutorizationException, BusinessException {
        try {
            ArrayList<Pedido> pedidos = mSql.pedidoBuscar(0, 0, 0, Pedido.ESTADO_CONFIRMADO, false);
            if (pedidos != null & pedidos.size() > 0) {
                enviarPedido(pedidos.get(0));
            }
        } catch (AutorizationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void sincronizarDown() throws AutorizationException, BusinessException {
        try {
            AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
            ResponseObject response = mWeb.getPedidos(autenticacionBZ.getAutenticacion());
            if (response.getData() != null) {

                //Graba cada cliente en la BD
                try {
                    JSONArray data = new JSONArray(response.getData());
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject pedidoJson = data.getJSONObject(i);
                        long idserver = pedidoJson.getLong("id");
                        short nuevoEstado = (short)pedidoJson.getJSONObject("estado").getInt("id");

                        ArrayList<Pedido> pedidos = mSql.pedidoBuscar(0, idserver, 0, -1, false);
                        if (pedidos != null && pedidos.size() > 0) {
                            Pedido pedido = pedidos.get(0);

                            //Actualiza el estado del pedido
                            if (nuevoEstado != pedido.estado) {
                                pedido.estado = nuevoEstado;
                                mSql.pedidoActualizar(pedido);
                            }
                        }
                    }

                } catch (Exception e) {
                    throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), e.getMessage()));
                }
            }

        } catch (AutorizationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }

    public void enviarPedido(Pedido pedido) throws AutorizationException, BusinessException {

        try {

            AutenticacionBZ autenticacionBZ =  new AutenticacionBZ(mContext);
            ResponseObject response = mWeb.sendPedido(autenticacionBZ.getAutenticacion(), pedido);

            if (response.getData() != null) {


                //Graba cada cliente en la BD
                try {
                    JSONObject data = new JSONObject(response.getData());

                    //Actualiza el estado del pedido
                    pedido.idServer = data.getLong("id");
                    pedido.estado = Pedido.ESTADO_ENVIADO;
                    mSql.pedidoActualizar(pedido);
                } catch (JSONException jex) {
                    throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_respuesta_servidor), jex.getMessage()));
                }
            }
        } catch (AutorizationException ae) {
            throw ae;
        } catch (Exception e) {
            throw new BusinessException(String.format(mContext.getResources().getString(R.string.error_accediendo_bd), e.getMessage()));
        }
    }
}
