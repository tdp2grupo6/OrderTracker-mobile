package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

/**
 * Created by pablo on 28/03/16.
 */
public class ClienteAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<Cliente> mFilteredData;
    private ArrayList<Cliente> mOriginalData;
    private LayoutInflater mInflater;

    public ClienteAdapter(Context context, ArrayList<Cliente> clientes) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mFilteredData = clientes;
        this.mOriginalData = clientes;
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
        ClienteViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_cliente, null, false);
            holder = new ClienteViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ClienteViewHolder) convertView.getTag();
        }
        Cliente cliente = mFilteredData.get(position);
        holder.mCliente = cliente;
        holder.getNombreView().setText(cliente.nombreCompleto);
        holder.getDireccionView().setText(cliente.direccion);

        if (holder.mCliente.estado == Cliente.ESTADO_VISITADO) {
            holder.getEstadoView().setText("V");
            holder.getEstadoView().setBackgroundResource(R.drawable.item_cliente_status_green);
        } else if (holder.mCliente.estado == Cliente.ESTADO_PENDIENTE) {
            holder.getEstadoView().setText("P");
            holder.getEstadoView().setBackgroundResource(R.drawable.item_cliente_status_yellow);
        } else {
            holder.getEstadoView().setText("N");
            holder.getEstadoView().setBackgroundResource(R.drawable.item_cliente_status_red);
        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Cliente> filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredData = (ArrayList<Cliente>) filterResults.values;
                ClienteAdapter.this.notifyDataSetChanged();
            }


            private ArrayList<Cliente> getFilteredResults(CharSequence constraint){
                // dgacitua: Modificado bajo criterio del cliente
                if (constraint.length() < 2){
                    return mOriginalData;
                }

                String find = constraint.toString().toLowerCase();
                ArrayList<Cliente> listResult = new ArrayList<Cliente>();
                for (Cliente obj : mOriginalData){
                    String nombreTemp = obj.nombre + " " + obj.apellido;
                    if (obj.nombre.toLowerCase().startsWith(find) ||
                        obj.apellido.toLowerCase().startsWith(find) ||
                        obj.razonSocial.toLowerCase().startsWith(find) ||
                        nombreTemp.toLowerCase().startsWith(find)) {
                        listResult.add(obj);
                    }
                }
                return listResult;
            }
        };
    }
}
