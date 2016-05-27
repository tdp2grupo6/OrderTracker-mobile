package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Utils;

/**
 * Created by dgacitua on 30-03-16.
 */
public class PedidoItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<PedidoItem> mPedidoItems;
    private LayoutInflater mInflater;

    public PedidoItemAdapter(Context context, ArrayList<PedidoItem> pedidoItems) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mPedidoItems = pedidoItems;
    }

    @Override
    public int getCount() {
        return mPedidoItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mPedidoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PedidoItemViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_pedido_item, null, false);
            holder = new PedidoItemViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (PedidoItemViewHolder) convertView.getTag();
        }
        PedidoItem pedidoItem = mPedidoItems.get(position);

        holder.getCodigoText().setText("Codigo Art. # " + Utils.long2human(pedidoItem.productoId, (short)5));
        holder.getNombreText().setText(pedidoItem.producto.nombre);
        holder.getCantidadText().setText("Cantidad: " + Utils.long2human(pedidoItem.cantidad, (short)3));

        return convertView;
    }
}
