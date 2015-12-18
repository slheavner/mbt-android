package com.slheavner.wvubus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.OnSheetDismissedListener;
import com.google.android.gms.maps.MapView;
import com.slheavner.wvubus.fragments.*;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.models.CardController;
import com.slheavner.wvubus.utils.FragmentController;
import com.slheavner.wvubus.utils.PrefsUtil;
import com.slheavner.wvubus.views.StatusView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, OnSheetDismissedListener, FragmentManager.OnBackStackChangedListener {

    private FloatingActionButton button;
    private BusMapFragment mapFragment;
    private MainActivityFragment mainFragment;
    private Animation slideOut, slideIn;
    private HideOnSlideEndListener hideOnSlideEndListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (FloatingActionButton) this.findViewById(R.id.add_button);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
        slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        hideOnSlideEndListener = new HideOnSlideEndListener(button);
        slideOut.setAnimationListener(hideOnSlideEndListener);
        mainFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            showPreferencesFragment();
            return true;
        }else if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }else if(id == R.id.action_about){
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            showAboutFragment();
        }

        return super.onOptionsItemSelected(item);
    }



    public void floatingButtonClick(View view){
        View mapInfo = this.findViewById(R.id.map_info_sheet);
        if(mapInfo != null && mapInfo.getVisibility() == View.VISIBLE){
            BottomSheetLayout bottomSheet = (BottomSheetLayout) mapInfo;
            bottomSheet.addOnSheetDismissedListener(this);
            View sheet = LayoutInflater.from(this).inflate(R.layout.info_layout, bottomSheet, false);
            createInfoSheet(sheet, (Bus) view.getTag());
            bottomSheet.setPeekSheetTranslation(bottomSheet.getMaxSheetTranslation() / 2);
            bottomSheet.showWithSheetView(sheet);
            buttonSlideOut(button);
        }else{
            DialogFragment df = new AddBusDialog();
            df.show(getSupportFragmentManager(), "busesSelect");
        }
    }

    private void createInfoSheet(View sheet, Bus bus){
        getTextView(sheet, R.id.map_info_name).setText(bus.getName());
        getTextView(sheet, R.id.map_info_number).setText(bus.getNumber());
        getTextView(sheet, R.id.map_info_service_text).setText(bus.getService());
        getTextView(sheet, R.id.map_info_firstrun_text).setText(bus.getFirstrun());
        getTextView(sheet, R.id.map_info_lastrun_text).setText(bus.getLastrun());
        getTextView(sheet, R.id.map_info_runtime_text).setText(bus.getRuntime());
        if(PrefsUtil.useColorInfo(this)){
            sheet.findViewById(R.id.map_info).setBackgroundColor(bus.getPolylineColor());
        }
        StatusView[] statusViews = new StatusView[3];
        statusViews[0] = (StatusView) sheet.findViewById(R.id.info_bus_one);
        statusViews[1] = (StatusView) sheet.findViewById(R.id.info_bus_two);
        statusViews[2] = (StatusView) sheet.findViewById(R.id.info_bus_three);
        CardController.setLocations(bus, statusViews);
    }

    private TextView getTextView(View v, int id){
        return (TextView) v.findViewById(id);
    }

    private void showBusMap(Bus bus){
        FragmentController.setMainFragment(this, BusMapFragment.newInstance(bus), BusMapFragment.TAG, true);
        switchButtonIcon(button, R.drawable.ic_view_list_white_24dp);
    }

    private void showPreferencesFragment(){
        SettingsFragment sf = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("Preferences");
        if(sf == null){
            sf = new SettingsFragment();
        }
        FragmentController.setMainFragment(this, sf, "Preferences", true);
        buttonSlideOut(button);

    }

    private void showAboutFragment(){
        AboutFragment sf = (AboutFragment) getSupportFragmentManager().findFragmentByTag("About");
        if(sf == null){
            sf = new AboutFragment();
        }
        FragmentController.setMainFragment(this, sf, "About", true);
        buttonSlideOut(button);

    }

    @Override
    public void onClick(View v) {
        Log.d("mbt", "MainActivity on click");
        Object tag = v.getTag();
        if(tag instanceof Bus){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            showBusMap((Bus) tag);
        }
    }

    @Override
    public void onDismissed(BottomSheetLayout bottomSheetLayout) {
        buttonSlideIn(button, 0);
    }

    @Override
    public void onBackStackChanged() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0 && this.findViewById(R.id.swipe_view) != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            if(button.isShown()){
                switchButtonIcon(button, android.R.drawable.ic_input_add);
            }else{
                buttonSlideIn(button, android.R.drawable.ic_input_add);
            }
            this.mainFragment.updateAdapter();
        }
    }

    public void switchButtonIcon(FloatingActionButton button, int resource){
        slideOut.setAnimationListener(new SwitchAnimationListener(button, resource));
        buttonSlideOut(button);
    }

    public void buttonSlideOut(FloatingActionButton button){
        button.startAnimation(slideOut);
    }

    public void buttonSlideIn(FloatingActionButton button, int resource){
        button.setVisibility(View.VISIBLE);
        if(resource != 0){
            button.setImageResource(resource);
        }
        button.startAnimation(slideIn);
    }

    private class HideOnSlideEndListener implements Animation.AnimationListener{

        FloatingActionButton button;

        public HideOnSlideEndListener(FloatingActionButton button){
            this.button = button;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            button.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private class SwitchAnimationListener implements Animation.AnimationListener{

        int r;
        FloatingActionButton button;

        public SwitchAnimationListener(FloatingActionButton button, int resource){
            this.r = resource;
            this.button = button;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            buttonSlideIn(button, this.r);
            animation.setAnimationListener(MainActivity.this.hideOnSlideEndListener);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
