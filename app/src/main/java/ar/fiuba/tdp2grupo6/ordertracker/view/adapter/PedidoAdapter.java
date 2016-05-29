package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Pedido;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Utils;

public class PedidoAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<Pedido> mFilteredData;
    private ArrayList<Pedido> mOriginalData;
    private LayoutInflater mInflater;

    public PedidoAdapter(Context context, ArrayList<Pedido> pedidos) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mFilteredData = pedidos;
        this.mOriginalData = pedidos;
    }

    @Override
    public int getCount() {
        return mFilteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilteredData.get(position);
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
        Pedido pedido = mFilteredData.get(position);

        holder.getClienteText().setText(pedido.cliente.nombreCompleto);
        holder.getFechaText().setText(Utils.date2human(pedido.fechaRealizado,true));
        holder.getNumeroText().setText("# " + Utils.long2human(pedido.idServer, (short)5));
        holder.getImporteText().setText("$ " + Utils.double2human(pedido.getImporte(false), (short)2));


        //Los estados son Enviado (E: gris), Aceptado (A: amarillo), Despachado (D: verde), Cancelado (C: rojo)
        //Agregagos: Nuevo (N: blanco), Confirmado (R: Celeste)
        try {
            TextView tv = holder.getEstadoText();
            if (pedido.estado == Pedido.ESTADO_NUEVO) {
                tv.setText("N");
                tv.setBackgroundResource(R.drawable.item_pedido_status_white);
            } else if (pedido.estado == Pedido.ESTADO_CONFIRMADO) {
                tv.setText("R");
                tv.setBackgroundResource(R.drawable.item_pedido_status_blue);
            } else if (pedido.estado == Pedido.ESTADO_ENVIADO) {
                tv.setText("E");
                tv.setBackgroundResource(R.drawable.item_pedido_status_gray);
            } else if (pedido.estado == Pedido.ESTADO_ACEPTADO) {
                tv.setText("A");
                tv.setBackgroundResource(R.drawable.item_pedido_status_yellow);
            } else if (pedido.estado == Pedido.ESTADO_DESPACHADO) {
                tv.setText("D");
                tv.setBackgroundResource(R.drawable.item_pedido_status_green);
            } else if (pedido.estado == Pedido.ESTADO_CANCELADO) {
                tv.setText("C");
                tv.setBackgroundResource(R.drawable.item_pedido_status_red);
            }
        } catch (Exception e) {
            String err = e.getLocalizedMessage();
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Pedido> filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredData = (ArrayList<Pedido>) filterResults.values;
                PedidoAdapter.this.notifyDataSetChanged();
            }


            private ArrayList<Pedido> getFilteredResults(CharSequence constraint){
                if (constraint.length() < 2){
                    return mOriginalData;
                }

                String find = constraint.toString().toLowerCase();
                ArrayList<Pedido> listResult = new ArrayList<Pedido>();
                for (Pedido obj : mOriginalData){
                    String nombreTemp = obj.cliente.nombreCompleto;
                    if (obj.cliente.nombre.toLowerCase().startsWith(find) ||
                            obj.cliente.apellido.toLowerCase().startsWith(find) ||
                            String.valueOf(obj.idServer).toLowerCase().startsWith(find)) {
                        listResult.add(obj);
                    }
                }
                return listResult;
            }
        };
    }
}
