package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;
import ar.fiuba.tdp2grupo6.ordertracker.view.ClienteDetailActivity;
import ar.fiuba.tdp2grupo6.ordertracker.view.ClienteDetailFragment;
import ar.fiuba.tdp2grupo6.ordertracker.view.dummy.DummyContent;

public class ClienteAdapter_ extends RecyclerView.Adapter<ClienteViewHolder_> implements Filterable {

    private Fragment mFragment;
    private ArrayList<Cliente> mFilteredData;
    private ArrayList<Cliente> mOriginalData;
    private boolean mTwoPane;
    //private final List<DummyContent.DummyItem> mValues;

    public ClienteAdapter_(Fragment fragment, ArrayList<Cliente> clientes, boolean twoPane) {
        this.mFragment = fragment;
        //this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mValues = clientes;
        this.mFilteredData = clientes;
        this.mOriginalData = clientes;
        this.mTwoPane = twoPane;
    }

    @Override
    public ClienteViewHolder_ onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_cliente, parent, false);
        return new ClienteViewHolder_(view);
    }

    @Override
    public void onBindViewHolder(final ClienteViewHolder_ holder, int position) {
        holder.mCliente = mFilteredData.get(position);
        holder.getNombreView().setText(mFilteredData.get(position).nombreCompleto);
        holder.getDireccionView().setText(mFilteredData.get(position).direccion);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putLong(ClienteDetailFragment.ARG_ITEM_ID, holder.mCliente.id);
                    ClienteDetailFragment fragment = new ClienteDetailFragment();
                    fragment.setArguments(arguments);
                    mFragment.getFragmentManager().beginTransaction()
                            .replace(R.id.cliente_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ClienteDetailActivity.class);
                    intent.putExtra(ClienteDetailFragment.ARG_ITEM_ID, holder.mCliente.id);

                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredData.size();
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
                ClienteAdapter_.this.notifyDataSetChanged();
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

