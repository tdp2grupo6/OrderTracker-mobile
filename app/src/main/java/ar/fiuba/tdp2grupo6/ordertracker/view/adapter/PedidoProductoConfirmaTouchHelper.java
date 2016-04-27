package ar.fiuba.tdp2grupo6.ordertracker.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class PedidoProductoConfirmaTouchHelper extends ItemTouchHelper.SimpleCallback {
    private PedidoProductoConfirmaAdapter mPedidoProductoConfirmaAdapter;

    public PedidoProductoConfirmaTouchHelper(PedidoProductoConfirmaAdapter pedidoProductoConfirmaAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mPedidoProductoConfirmaAdapter = pedidoProductoConfirmaAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        mPedidoProductoConfirmaAdapter.remove(viewHolder.getAdapterPosition());
    }
}