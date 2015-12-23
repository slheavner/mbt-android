package com.slheavner.wvubus.views.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.utils.CardHelper;
import com.slheavner.wvubus.utils.PrefsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 12/14/2015.
 *
 * Main RecyclerViewAdapter for bus CardViews
 */
public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder>{

    private Map<String, Bus> data;
    private View.OnClickListener cardClickListener;
    private List<String> visibleIds;
    private PrefsUtil prefs;
    private String[] idList;

    /**
     * Create and auto set data. The data list should be all buses, not just enabled. This adapter handles the
     * preference changes itself, so it needs all the data.
     * @param data All bus data.
     * @param activity Activity that extends OnClickListener to handle card clicks. Will handle exception if Activity
     *                 does not implement OnClickListener.
     */
    public BusAdapter(List<Bus> data, Activity activity){
        prefs = new PrefsUtil(activity);
        visibleIds = prefs.onlyEnabled();
        idList = activity.getResources().getStringArray(R.array.bus_ids);
        this.setData(data);
        try{
            this.cardClickListener = (View.OnClickListener) activity;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Set the data for the adapter. Should be used when the data changes, not on enabled state changes.
     * @param busList List of all bus data
     */
    public void setData(List<Bus> busList){
        if(data == null){
            data = new HashMap<String, Bus>();
        }
        int index;
        for(Bus bus : busList){
            index = visibleIds.indexOf(bus.getId());
            if(data.get(bus.getId()) != bus){
                data.put(bus.getId(), bus);
                if(index > -1){
                    //this.notifyItemChanged(index);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view, parent, false);
        v.setOnClickListener(this.cardClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Bus bus = data.get(visibleIds.get(position));
        holder.getCard().setBus(bus);
        holder.getCard().getCardView().setTag(bus);
    }

    @Override
    public int getItemCount() {
        return visibleIds.size();
    }

    public void notifyEnabledBusesChanged() {
        visibleIds = prefs.onlyEnabled();
        notifyDataSetChanged();
//        List<String> enabledIds = prefs.onlyEnabled();
//        String id;
//        int counter = 0;
//        for(int i = 0; i < idList.length; i++){
//            id = idList[i]; //current bus id
//            int visible = visibleIds.indexOf(id); //visible index
//            int enabled = enabledIds.indexOf(id); //enabled index
//            if(visible > -1 && enabled == -1){  //visible but not enabled
//                visibleIds.remove(visible); //remove it
//                this.notifyItemRemoved(visible); //show anim
//            }else if(visible > -1 && enabled > -1){ //visible and enabled
//                counter ++; //add to count
//            }else if(visible == -1 && enabled > -1){ //not visible but enabled
//                visibleIds.add(counter, id);  //add to visible
//                this.notifyItemInserted(counter); //show anim
//                counter++; //add to count
//            }
//        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardHelper card;

        public ViewHolder(View v){
            super(v);
            card = new CardHelper((CardView) v);
        }

        public CardHelper getCard(){
            return card;
        }
    }



}
