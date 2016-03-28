package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Cliente;

/**
 * Created by pablo on 28/03/16.
 */
public class ClienteAdapter extends BaseAdapter {

            private Context mContext;
            private ArrayList<Cliente> mClientes;
            private LayoutInflater mInflater;

            public ClienteAdapter(Context context, ArrayList<Cliente> clientes) {
                this.mContext = context;
                this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                this.mClientes = clientes;
            }

            @Override
            public int getCount() {
                return mClientes.size();
            }

            @Override
            public Object getItem(int position) {
                return mClientes.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ClienteViewHolder holder = null;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.cliente_item_layout, null, false);
                    holder = new ClienteViewHolder(convertView);
                    convertView.setTag(holder);
                }
                else {
                    holder = (ClienteViewHolder) convertView.getTag();
                }
                holder.getUpperText().setText(mClientes.get(position).nombre);
                holder.getLowerText().setText(mClientes.get(position).direccion);

                return convertView;
            }
        }
