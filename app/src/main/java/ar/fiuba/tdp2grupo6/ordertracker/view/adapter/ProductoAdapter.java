package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

/**
 * Created by dgacitua on 30-03-16.
 */
public class ProductoAdapter extends BaseAdapter implements Filterable {
    private Context mContext;
    private ArrayList<Producto> mFilteredData;
    private ArrayList<Producto> mOriginalData;
    private LayoutInflater mInflater;

    public ProductoAdapter(Context context, ArrayList<Producto> productos) {
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mOriginalData = productos;
        this.mFilteredData = productos;
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
        ProductoViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_item_catalogo, null, false);
            holder = new ProductoViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ProductoViewHolder) convertView.getTag();
        }
        Producto producto = mFilteredData.get(position);
        holder.getUpperText().setText(producto.nombre);
        holder.getLowerText1().setText(producto.caracteristicas);
        holder.getLowerText2().setText(producto.mostrarPrecio());
        holder.getSideText().setText(producto.mostrarStock());

        ImagenBZ imagenBZ = new ImagenBZ();
        Bitmap imagenMiniatura = imagenBZ.leer(producto.getNombreImagenMiniatura());
        if (imagenMiniatura != null)
            holder.getImage().setImageBitmap(imagenMiniatura);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Producto> filteredResult = getFilteredResults(charSequence);

                FilterResults results = new FilterResults();
                results.values = filteredResult;
                results.count = filteredResult.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredData = (ArrayList<Producto>) filterResults.values;
                ProductoAdapter.this.notifyDataSetChanged();
            }


            private ArrayList<Producto> getFilteredResults(CharSequence constraint){
                // dgacitua: Modificado bajo criterio del cliente
                if (constraint.length() < 2){
                    return mOriginalData;
                }

                String find = constraint.toString().toLowerCase();
                ArrayList<Producto> listResult = new ArrayList<Producto>();
                for (Producto obj : mOriginalData) {
                    if (obj.nombre.toLowerCase().startsWith(find)) {
                        listResult.add(obj);
                    }
                }
                return listResult;
            }
        };
    }
}
