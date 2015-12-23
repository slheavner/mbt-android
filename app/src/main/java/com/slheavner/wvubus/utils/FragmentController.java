package com.slheavner.wvubus.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.slheavner.wvubus.R;

/**
 * Created by Sam on 12/17/2015.
 *
 * Convenience Class for handling fragment transactions.
 */
public class FragmentController {

    //private static Fragment lastFragment = null;

    /**
     * Set a fragment to the main container. Id of view is hardcoded.
     * @param appCompatActivity The activity to get the FragmentManager from
     * @param fragment The fragment to put in position
     * @param tag The tag to add to the Fragment
     * @param addToBackstack Should the existing fragment be added to the backstack (probably yes)
     */
    public static void setMainFragment(AppCompatActivity appCompatActivity, Fragment fragment, String tag, boolean addToBackstack){
        FragmentManager fm = appCompatActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.setCustomAnimations(R.anim.slide_in_bottom, R.anim.anim_delay_450, R.anim.anim_delay_450, R.anim.slide_out_bottom);
        ft.replace(R.id.fragment, fragment, tag);
        if(addToBackstack){
            ft.addToBackStack(null);
        }
        ft.commit();
        //lastFragment = fragment;
    }


//    public static void restoreLastFragment(AppCompatActivity aba, String tag){
//        setMainFragment(aba, lastFragment, tag, false);
//    }

    /**
     * Replace/add a fragment to a specified frame/view. No transition.
     * @param appCompatActivity Activity to get FragmentManager from
     * @param fragment The Fragment to add
     * @param tag The tag to use
     * @param addToBackstack Add to backstack? (Probably not)
     * @param id The id of the frame/view to put the Fragment
     */
    public static void replaceToFrame(AppCompatActivity appCompatActivity, Fragment fragment, String tag, boolean addToBackstack, int id){
        FragmentManager fm = appCompatActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(id, fragment, tag);
        if(addToBackstack){
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    public interface FragmentTransactionListener{
        void onTransaction(String oldTag, String newTag);
    }



}
