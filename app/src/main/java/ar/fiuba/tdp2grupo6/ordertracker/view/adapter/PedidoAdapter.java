package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Utils;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Pedido> mPedidos;
    private LayoutInflater mInflater;

    public PedidoAdapter(Context context, ArrayList<Pedido> pedidos) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPedidos = pedidos;
    }

    @Override
    public int getCount() {
        return mPedidos.size();
    }

    @Override
    public Object getItem(int position) {
        return mPedidos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PedidoViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_pedido, null, false);
            holder = new PedidoViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PedidoViewHolder) convertView.getTag();
        }
        Pedido pedido = mPedidos.get(position);

        holder.getClienteText().setText(pedido.cliente.nombreCompleto);
        holder.getFechaText().setText(Utils.date2human(pedido.fechaRealizado,true));
        holder.getNumeroText().setText("# " + Utils.long2human(pedido.idServer, (short)5));
        holder.getImporteText().setText("$ " + Utils.double2human(pedido.getImporte(false), (short)2));


        //Los estados son Enviado (E: gris), Aceptado (A: amarillo), Despachado (D: verde), Cancelado (C: rojo)
        //Agregagos: Nuevo (N: blanco), Confirmado (R: Celeste)
        String estadoText = "";
        int estadoBackgroudResourceId = 0;
        if (pedido.estado == Pedido.ESTADO_NUEVO) {
            estadoText = "N";
            estadoBackgroudResourceId = R.drawable.item_pedido_status_white;
        } else if (pedido.estado == Pedido.ESTADO_CONFIRMADO) {
            estadoText = "R";
            estadoBackgroudResourceId = R.drawable.item_pedido_status_blue;
        } else if (pedido.estado == Pedido.ESTADO_ENVIADO) {
            estadoText = "E";
            estadoBackgroudResourceId = R.drawable.item_pedido_status_gray;
        } else if (pedido.estado == Pedido.ESTADO_ACEPTADO) {
            estadoText = "A";
            estadoBackgroudResourceId = R.drawable.item_pedido_status_yellow;
        } else if (pedido.estado == Pedido.ESTADO_DESPACHADO) {
            estadoText = "D";
            estadoBackgroudResourceId = R.drawable.item_pedido_status_green;
        } else if (pedido.estado == Pedido.ESTADO_CANCELADO) {
            estadoText = "C";
            estadoBackgroudResourceId = R.drawable.item_pedido_status_red;
        }

        holder.getEstadoText().setText(estadoText);
        holder.getEstadoText().setBackgroundResource(R.drawable.item_cliente_status_green);

        return convertView;
    }
}
