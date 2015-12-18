package com.slheavner.wvubus.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import com.slheavner.wvubus.R;

/**
 * Created by Sam on 12/17/2015.
 */
public class FragmentController {

    private static Fragment lastFragment = null;

    public static void setMainFragment(AppCompatActivity aba, Fragment f, String tag, boolean addToBackstack){
        FragmentManager fm = aba.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_bottom, 0, 0, R.anim.slide_out_bottom);

        ft.replace(R.id.fragment, f, tag);
        if(addToBackstack){
            ft.addToBackStack(System.currentTimeMillis() + "");
        }

        ft.commit();
        lastFragment = f;
    }

    public static void restoreLastFragment(AppCompatActivity aba, String tag){
        setMainFragment(aba, lastFragment, tag, false);
    }

    public static void replaceToFrame(AppCompatActivity aba, Fragment f, String tag, boolean addToBackstack, int id){
        FragmentManager fm = aba.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(id, f, tag);
        if(addToBackstack){
            ft.addToBackStack(System.currentTimeMillis() + "");
        }
        ft.commit();
    }



}
