package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.AgendaItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

public class AgendaAdapter extends RecyclerView.Adapter<AgendaViewHolder> { //} implements Filterable {

    private ArrayList<AgendaItem> mFilteredData;
    private ArrayList<AgendaItem> mOriginalData;

    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener {
        public void onItemClick(AgendaViewHolder holder, int position);
    }


    public AgendaAdapter(OnItemClickListener listener, ArrayList<AgendaItem> agendaItems) {
        this.mItemClickListener = listener;
        this.mFilteredData = agendaItems;
        this.mOriginalData = agendaItems;
    }

    @Override
    public AgendaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_cliente, parent, false);
        return new AgendaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AgendaViewHolder holder, final int position) {
        AgendaItem agendaItem = mFilteredData.get(position);
        holder.mCliente = agendaItem.cliente;
        holder.getNombreView().setText(agendaItem.cliente.nombreCompleto);
        holder.getDireccionView().setText(agendaItem.cliente.direccion);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(holder, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
    }

    /*
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<PedidoItem> filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredData = (ArrayList<PedidoItem>) filterResults.values;
                PedidoProductoAdapter.this.notifyDataSetChanged();
            }


            private ArrayList<PedidoItem> getFilteredResults(CharSequence constraint){
                // dgacitua: Modificado bajo criterio del cliente
                if (constraint.length() < 2){
                    return mOriginalData;
                }

                String find = constraint.toString().toLowerCase();
                ArrayList<PedidoItem> listResult = new ArrayList<PedidoItem>();
                for (PedidoItem obj : mOriginalData){
                    if (obj.producto.nombre.toLowerCase().startsWith(find)) {
                        listResult.add(obj);
                    }
                }
                return listResult;
            }
        };
    }
    */
}