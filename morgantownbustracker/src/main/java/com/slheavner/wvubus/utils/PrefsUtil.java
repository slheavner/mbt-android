package com.slheavner.wvubus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import com.slheavner.wvubus.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sam on 12/14/2015.
 */
public class PrefsUtil {
    private SharedPreferences prefs;
    private Resources resources;
    public PrefsUtil(Activity activity){
        this.prefs = activity.getPreferences(Context.MODE_PRIVATE);
        this.resources = activity.getResources();
    }

    public HashMap<String, Boolean> getBusEnabled(){
        HashMap<String, Boolean> map = new HashMap<String, Boolean>();
        for(String id : resources.getStringArray(R.array.bus_ids)){
            map.put(id, prefs.getBoolean(id, false));
        }
        return map;
    }

    public boolean[] getBusEnabledBool(){
        String[] busIds = resources.getStringArray(R.array.bus_ids);
        boolean[] bools = new boolean[busIds.length];
        int counter = 0;
        for(String id : busIds){
            bools[counter] = prefs.getBoolean(id, false);
            counter++;
        }
        return bools;
    }

    public List<String> onlyEnabled(){
        List<String> idList = new ArrayList<String>();
        for(String s : resources.getStringArray(R.array.bus_ids)){
            if(isEnabled(s)){
                idList.add(s);
            }
        }
        return idList;
    }
    public List<String> onlyEnabled(SharedPreferences sp){
        List<String> idList = new ArrayList<String>();
        for(String s : resources.getStringArray(R.array.bus_ids)){
            if(isEnabled(s, sp)){
                idList.add(s);
            }
        }
        return idList;
    }

    public boolean isEnabled(String busId){
        return prefs.getBoolean(busId, false);
    }

    public boolean isEnabled(String busId, SharedPreferences sp){
        return sp.getBoolean(busId, false);
    }

    public void setBusEnabled(String id, boolean bool){
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(id, bool);
        apply(editor);
    }


    public void setBusEnabled(HashMap<String, Boolean> map){
        SharedPreferences.Editor editor = prefs.edit();
        for(String key : map.keySet()){
            editor.putBoolean(key, map.get(key));
        }
        apply(editor);
    }


    public void setBusEnabled(boolean[] bool){
        SharedPreferences.Editor editor = prefs.edit();
        int counter = 0;
        for(String id : resources.getStringArray(R.array.bus_ids)){
            editor.putBoolean(id, bool[counter]);
            counter++;
        }
        apply(editor);
    }

    public void apply(SharedPreferences.Editor editor){
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    public static boolean useColorMain(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("colors_main", false);
    }
    public static boolean useColorMap(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("colors_map", false);
    }
    public static boolean useColorInfo(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("colors_info", false);
    }

}
