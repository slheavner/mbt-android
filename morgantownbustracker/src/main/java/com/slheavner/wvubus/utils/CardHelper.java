package com.slheavner.wvubus.utils;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.utils.PrefsUtil;
import com.slheavner.wvubus.views.StatusView;

/**
 * Created by Sam on 12/14/2015.
 *
 * Controls the construction of CardViews and StatusViews.
 * TODO: Should probably name this more appropriately.
 */
public class CardHelper {
    private CardView cardView;
    private TextView name, number;
    private RelativeLayout header;
    private StatusView[] statusViews = new StatusView[3];
    private Bus bus;

    public CardHelper(CardView cardView){
        this.cardView = cardView;
        name = (TextView) cardView.findViewById(R.id.card_title_name);
        number = (TextView) cardView.findViewById(R.id.card_title_number);
        header = (RelativeLayout) cardView.findViewById(R.id.card_title);
        statusViews[0] = (StatusView) cardView.findViewById(R.id.bus_one);
        statusViews[1] = (StatusView) cardView.findViewById(R.id.bus_two);
        statusViews[2] = (StatusView) cardView.findViewById(R.id.bus_three);
    }

    public CardView getCardView(){
        return this.cardView;
    }
    public Bus getBus(){
        return bus;
    }

    /**
     * Sets the bus for the CardView. All views are set when this method is called, including any color updates.
     * @param bus the bus to create this card
     */
    public void setBus(Bus bus){
        this.bus = bus;
        if(PrefsUtil.useColorMain(this.getCardView().getContext())){
            header.setBackgroundColor(bus.getPolylineColor());
        }else{
            //use deprecated becuase we are supporting API 14
            header.setBackgroundColor(this.getCardView().getContext()
                    .getResources().getColor(R.color.blue_secondary));
        }
        name.setText(bus.getName());
        number.setText(bus.getNumber());
        setLocations(bus, statusViews);
    }


    /**
     * Static method for creating StatusViews. It is static because the same logic is used in the Info sheet.
     * @param bus bus to get location data from
     * @param statusViews view to create, should already be inflated
     */
    public static void setLocations(Bus bus, StatusView[] statusViews){
        Bus.Location[] locations = bus.getLocations();
        long now = System.currentTimeMillis();
        if(!bus.getId().equals("prt")){
            //normal buses
            for(int i = 0; i < statusViews.length; i++){
                Bus.Location location = locations[2-i];
                //check index, if old and > 45 min, hide
                if(i > 0 && (now - location.getTime()) > 2700){
                    statusViews[i].setVisibility(View.GONE);
                }else{
                    statusViews[i].setLocation(location, bus.getId());
                }
            }
            //divider logic
            if(statusViews[1].getVisibility() == View.GONE){
                statusViews[0].hideDivider();
            }else if(statusViews[2].getVisibility() == View.GONE){
                statusViews[1].hideDivider();
            }
        }else{
            //special PRT stuff
            statusViews[0].setLocation(locations[0], bus.getId());
            statusViews[0].hideDivider();
            statusViews[1].setVisibility(View.GONE);
            statusViews[2].setVisibility(View.GONE);
        }
    }

}
