package com.slheavner.wvubus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.SharedPreferencesCompat;
import com.slheavner.wvubus.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam on 12/14/2015.
 *
 * Convenience Class for accessing Preferences
 */
public class PrefsUtil {
    private SharedPreferences prefs;
    private Resources resources;

    public PrefsUtil(Activity activity){
        this.prefs = activity.getPreferences(Context.MODE_PRIVATE);
        this.resources = activity.getResources();
    }

    /**
     * Get a full array of enabled states for all bus ids in order
     * @return boolean array of enabled states in order of bus id
     */
    public boolean[] getBusEnabledBool(){
        String[] busIds = resources.getStringArray(R.array.bus_ids);
        boolean[] enabled = new boolean[busIds.length];
        for(int i = 0; i < busIds.length; i++){
            enabled[i] = prefs.getBoolean(busIds[i], false);
        }
        return enabled;
    }

    /**
     * Get a list of only the buses that are enabled
     * @return List of enabled buses only
     */
    public List<String> onlyEnabled(){
        List<String> idList = new ArrayList<String>();
        for(String s : resources.getStringArray(R.array.bus_ids)){
            if(isEnabled(s)){
                idList.add(s);
            }
        }
        return idList;
    }

    /**
     * Return whether a busId is enabled
     * @param busId id to check
     * @return true if enabled
     */
    public boolean isEnabled(String busId){
        return prefs.getBoolean(busId, false);
    }

    /**
     * Save a bus as enabled in preferences
     * @param bool boolean array of bus states
     */
    public void setBusesEnabled(boolean[] bool){
        SharedPreferences.Editor editor = prefs.edit();
        int counter = 0;
        for(String id : resources.getStringArray(R.array.bus_ids)){
            editor.putBoolean(id, bool[counter]);
            counter++;
        }
        apply(editor);
    }

    private void apply(SharedPreferences.Editor editor){
        SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
    }

    /**
     * Get Main color preference
     * @param context Context for prefs
     * @return true if should use colors
     */
    public static boolean useColorMain(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources()
                        .getString(R.string.colors_main_key), false);
    }

    /**
     * Get Map route color preference
     * @param context Context for prefs
     * @return true if should use colors
     */
    public static boolean useColorMap(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources()
                        .getString(R.string.colors_map_key), false);
    }

    /**
     * Get Info Sheet color preference
     * @param context Context for prefs
     * @return true if should use colors
     */
    public static boolean useColorInfo(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getResources()
                        .getString(R.string.colors_info_key), false);
    }

}
