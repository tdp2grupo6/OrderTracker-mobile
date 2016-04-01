package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

/**
 * Created by dgacitua on 30-03-16.
 */
public class ProductoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Producto> mProductos;
    private LayoutInflater mInflater;

    public ProductoAdapter(Context context, ArrayList<Producto> productos) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mProductos = productos;
    }

    @Override
    public int getCount() {
        return mProductos.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO
        ProductoViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_catalogo, null, false);
            holder = new ProductoViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ProductoViewHolder) convertView.getTag();
        }
        holder.getUpperText().setText(mProductos.get(position).nombre);
        holder.getLowerText1().setText(mProductos.get(position).caracteristicas);
        holder.getLowerText2().setText(mProductos.get(position).mostrarPrecio());
        holder.getSideText().setText(mProductos.get(position).mostrarStock());

        return convertView;
    }
}
