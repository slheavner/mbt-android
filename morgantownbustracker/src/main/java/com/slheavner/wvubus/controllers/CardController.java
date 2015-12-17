package com.slheavner.wvubus.controllers;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.views.StatusView;

/**
 * Created by Sam on 12/14/2015.
 */
public class CardController {
    private CardView cardView;
    private TextView name, number;
    private StatusView[] statusViews = new StatusView[3];
    private Bus bus;

    public CardController(CardView cardView){
        this.cardView = cardView;
        this.name = (TextView) cardView.findViewById(R.id.card_title_name);
        this.number = (TextView) cardView.findViewById(R.id.card_title_number);
        this.statusViews[0] = (StatusView) cardView.findViewById(R.id.bus_one);
        this.statusViews[1] = (StatusView) cardView.findViewById(R.id.bus_two);
        this.statusViews[2] = (StatusView) cardView.findViewById(R.id.bus_three);
    }

    public CardView getCardView(){
        return this.cardView;
    }

    public void setBus(Bus bus){
        this.bus = bus;
        this.name.setText(bus.getName());
        this.number.setText(bus.getNumber());
        Bus.Location[] locs = bus.getLocations();
        long now = System.currentTimeMillis();
        if(!bus.get_id().equals("prt")){
            for(int i = 0; i < statusViews.length; i++){
                Bus.Location loc = locs[2-i];
                if(i > 0 && (now - locs[i].getTime()) > 2700){
                    statusViews[i].setVisibility(View.GONE);
                }else{
                    statusViews[i].setLocation(loc, bus.get_id());
                }
            }
        }else{
            statusViews[0].setLocation(locs[0], bus.get_id());
            statusViews[1].setVisibility(View.GONE);
            statusViews[2].setVisibility(View.GONE);
        }
    }

}
