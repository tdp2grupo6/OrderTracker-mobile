package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ar.fiuba.tdp2grupo6.ordertracker.R;
import ar.fiuba.tdp2grupo6.ordertracker.business.ImagenBZ;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

public class PedidoProductoAdapter extends RecyclerView.Adapter<PedidoProductoViewHolder> implements Filterable {

    private ArrayList<Producto> mFilteredData;
    private ArrayList<Producto> mOriginalData;
    private boolean mTwoPane;

    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener {
        public void onItemClick(PedidoProductoViewHolder holder, int position);
        public void onItemPlusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad);
        public void onItemMinusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad);
        public void onItemQuantityClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad);
    }


    public PedidoProductoAdapter(OnItemClickListener listener, ArrayList<Producto> productosPedido, boolean twoPane) {
        this.mItemClickListener = listener;
        this.mFilteredData = productosPedido;
        this.mOriginalData = productosPedido;
        this.mTwoPane = twoPane;
    }

    @Override
    public PedidoProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_pedido, parent, false);
        return new PedidoProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PedidoProductoViewHolder holder, final int position) {
        holder.mProducto = mFilteredData.get(position);
        holder.getNombreView().setText(holder.mProducto.nombre);
        holder.getPrecioView().setText(holder.mProducto.precio.toString());
        //holder.getStockView().setText(String.valueOf(holder.mProducto.stock));
        holder.getQuantityText().setText("0");

        ImagenBZ imagenBZ = new ImagenBZ();
        Bitmap imagenMiniatura = imagenBZ.leer(holder.mProducto.getNombreImagenMiniatura());
        if (imagenMiniatura != null)
            holder.getImagenView().setImageBitmap(imagenMiniatura);

        holder.getMinusView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int viejaCantidad = Integer.parseInt(holder.getQuantityText().getText().toString());
                    int nuevaCantidad = viejaCantidad - 1;
                    mItemClickListener.onItemMinusClick(holder, position, viejaCantidad, nuevaCantidad);
                }
            }
        });

        holder.getPlusView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    int viejaCantidad = Integer.parseInt(holder.getQuantityText().getText().toString());
                    int nuevaCantidad = viejaCantidad + 1;
                    mItemClickListener.onItemPlusClick(holder, position, viejaCantidad, nuevaCantidad);
                }
            }
        });

        holder.getQuantityText().addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if (mItemClickListener != null) {
                    try {
                        if (holder.getQuantityText().hasFocus()) {
                            int viejaCantidad = Integer.parseInt(holder.getQuantityText().getText().toString());
                            int nuevaCantidad = Integer.parseInt(s.toString());
                            holder.getQuantityText().clearFocus();
                            mItemClickListener.onItemQuantityClick(holder, position, viejaCantidad, nuevaCantidad);
                        }
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

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
                PedidoProductoAdapter.this.notifyDataSetChanged();
            }


            private ArrayList<Producto> getFilteredResults(CharSequence constraint){
                // dgacitua: Modificado bajo criterio del cliente
                if (constraint.length() < 2){
                    return mOriginalData;
                }

                String find = constraint.toString().toLowerCase();
                ArrayList<Producto> listResult = new ArrayList<Producto>();
                for (Producto obj : mOriginalData){
                    if (obj.nombre.toLowerCase().startsWith(find)) {
                        listResult.add(obj);
                    }
                }
                return listResult;
            }
        };
    }
}

