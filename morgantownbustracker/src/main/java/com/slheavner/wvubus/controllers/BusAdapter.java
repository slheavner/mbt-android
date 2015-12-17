package com.slheavner.wvubus.controllers;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;

import java.util.List;

/**
 * Created by Sam on 12/14/2015.
 */
public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder> {

    private List<Bus> data;

    public BusAdapter(List<Bus> data){
        this.data = data;
    }

    public void setData(List<Bus> data){
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.getCard().setBus(data.get(position));
        holder.getCard().getCardView().setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("mbt", data.get(position).get_id() + " was clicked");
            }
        });

    }



    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardController card;

        public ViewHolder(View v){
            super(v);
            card = new CardController((CardView) v);
        }

        public CardController getCard(){
            return card;
        }
    }

}
