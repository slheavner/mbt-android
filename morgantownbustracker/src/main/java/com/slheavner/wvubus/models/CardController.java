package com.slheavner.wvubus.models;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.utils.PrefsUtil;
import com.slheavner.wvubus.views.StatusView;

/**
 * Created by Sam on 12/14/2015.
 */
public class CardController {
    private CardView cardView;
    private TextView name, number;
    private RelativeLayout header;
    private StatusView[] statusViews = new StatusView[3];
    private Bus bus;

    public CardController(CardView cardView){
        this.cardView = cardView;

        this.name = (TextView) cardView.findViewById(R.id.card_title_name);
        this.number = (TextView) cardView.findViewById(R.id.card_title_number);
        this.header = (RelativeLayout) cardView.findViewById(R.id.card_title);
        this.statusViews[0] = (StatusView) cardView.findViewById(R.id.bus_one);
        this.statusViews[1] = (StatusView) cardView.findViewById(R.id.bus_two);
        this.statusViews[2] = (StatusView) cardView.findViewById(R.id.bus_three);
    }

    public CardView getCardView(){
        return this.cardView;
    }

    public void setBus(Bus bus){
        this.bus = bus;
        if(PrefsUtil.useColorMain(this.getCardView().getContext())){
            this.header.setBackgroundColor(bus.getPolylineColor());
        }else{
            this.header.setBackgroundColor(this.getCardView().getContext()
                    .getResources().getColor(R.color.md_light_blue_900));
        }
        this.name.setText(bus.getName());
        this.number.setText(bus.getNumber());
        Bus.Location[] locs = bus.getLocations();
        setLocations(bus, statusViews);
    }

    public static void setLocations(Bus bus, StatusView[] statusViews){
        Bus.Location[] locs = bus.getLocations();
        long now = System.currentTimeMillis();
        if(!bus.get_id().equals("prt")){
            for(int i = 0; i < statusViews.length; i++){
                Bus.Location loc = locs[2-i];
                if(i > 0 && (now - loc.getTime()) > 2700){
                    statusViews[i].setVisibility(View.GONE);
                }else{
                    statusViews[i].setLocation(loc, bus.get_id());
                }
            }
            if(statusViews[1].getVisibility() == View.GONE){
                statusViews[0].hideDivider();
            }else if(statusViews[2].getVisibility() == View.GONE){
                statusViews[1].hideDivider();
            }
        }else{
            statusViews[0].setLocation(locs[0], bus.get_id());
            statusViews[0].hideDivider();
            statusViews[1].setVisibility(View.GONE);
            statusViews[2].setVisibility(View.GONE);
        }
    }

}
