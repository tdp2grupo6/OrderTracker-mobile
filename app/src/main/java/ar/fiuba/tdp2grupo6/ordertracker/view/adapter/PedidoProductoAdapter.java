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
import ar.fiuba.tdp2grupo6.ordertracker.contract.PedidoItem;
import ar.fiuba.tdp2grupo6.ordertracker.contract.Producto;

public class PedidoProductoAdapter extends RecyclerView.Adapter<PedidoProductoViewHolder> implements Filterable {

    private ArrayList<PedidoItem> mFilteredData;
    private ArrayList<PedidoItem> mOriginalData;
    private boolean mTwoPane;

    private OnItemClickListener mItemClickListener;
    public interface OnItemClickListener {
        public void onItemClick(PedidoProductoViewHolder holder, int position);
        public void onItemPlusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad);
        public void onItemMinusClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad);
        public void onItemQuantityClick(PedidoProductoViewHolder holder, int position, int viejaCantidad, int nuevaCantidad);
    }


    public PedidoProductoAdapter(OnItemClickListener listener, ArrayList<PedidoItem> pedidoItems, boolean twoPane) {
        this.mItemClickListener = listener;
        this.mFilteredData = pedidoItems;
        this.mOriginalData = pedidoItems;
        this.mTwoPane = twoPane;
    }

    @Override
    public PedidoProductoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_pedido, parent, false);
        return new PedidoProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PedidoProductoViewHolder holder, final int position) {
        holder.mPedidoItem = mFilteredData.get(position);
        holder.getNombreView().setText(holder.mPedidoItem.producto.nombre);
        holder.getPrecioView().setText("$" + holder.mPedidoItem.producto.precio.toString());
        holder.getStockView().setText("Stock: " + String.valueOf(holder.mPedidoItem.producto.stock));
        holder.getQuantityText().setText(String.valueOf(holder.mPedidoItem.cantidad));

        ImagenBZ imagenBZ = new ImagenBZ();
        Bitmap imagenMiniatura = imagenBZ.leer(holder.mPedidoItem.producto.getNombreImagenMiniatura());
        if (imagenMiniatura != null)
            holder.getImagenView().setImageBitmap(imagenMiniatura);
        holder.getImagenView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(holder, position);
                }
            }
        });

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

        holder.getQuantityText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder dataDialogBuilder = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
                dataDialogBuilder.setTitle(context.getResources().getString(R.string.title_popup_ingrese_cantidad));
                dataDialogBuilder.setView(input);
                dataDialogBuilder.setCancelable(false).setPositiveButton(context.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (input.getText().toString().trim().length() > 0) {
                            int nuevaCantidad = Integer.parseInt(input.getText().toString());
                            if (mItemClickListener != null && nuevaCantidad > 0) {
                                int viejaCantidad = Integer.parseInt(holder.getQuantityText().getText().toString());
                                mItemClickListener.onItemQuantityClick(holder, position, viejaCantidad, nuevaCantidad);
                            }
                            dialog.cancel();
                        }
                    }
                }).setNegativeButton(context.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                // create an alert dialog
                dataDialogBuilder.create().show();
            }
        });

        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(holder, position);
                }
            }
        });
        */
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
}

