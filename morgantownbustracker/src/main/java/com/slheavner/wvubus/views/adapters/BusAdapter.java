package com.slheavner.wvubus.views.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.models.CardController;
import com.slheavner.wvubus.utils.PrefsUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sam on 12/14/2015.
 */
public class BusAdapter extends RecyclerView.Adapter<BusAdapter.ViewHolder> implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Map<String, Bus> data;
    private View.OnClickListener cardClickListener;
    private List<String> visibleIds;
    private PrefsUtil prefs;
    private String[] idList;

    public BusAdapter(List<Bus> data, Activity activity){
        activity.getPreferences(Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
        prefs = new PrefsUtil(activity);
        visibleIds = prefs.onlyEnabled();
        idList = activity.getResources().getStringArray(R.array.bus_ids);
        this.setData(data, activity);
        try{
            this.cardClickListener = (View.OnClickListener) activity;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setData(List<Bus> busList, Activity activity){
        if(data == null){
            data = new HashMap<String, Bus>();
        }
        int index;
        for(Bus bus : busList){
            index = visibleIds.indexOf(bus.get_id());
            if(data.get(bus.get_id()) != bus){
                data.put(bus.get_id(), bus);
                if(index > -1){
                    this.notifyItemChanged(index);
                }
            }
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        List<String> enabledIds = prefs.onlyEnabled();
        String id;
        int counter = 0;
        for(int i = 0; i < idList.length; i++){
            id = idList[i];
            int visible = visibleIds.indexOf(id);
            int enabled = enabledIds.indexOf(id);
            if(visible > -1 && enabled == -1){
                visibleIds.remove(visible);
                this.notifyItemRemoved(visible);
            }else if(visible > -1 && enabled > -1){
                counter ++;
            }else if(visible == -1 && enabled > -1){
                visibleIds.add(counter, id);
                this.notifyItemInserted(counter);
                counter++;
            }
        }

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
