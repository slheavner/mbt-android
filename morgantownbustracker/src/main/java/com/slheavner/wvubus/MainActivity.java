package com.slheavner.wvubus;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.slheavner.wvubus.utils.CardHelper;
import com.slheavner.wvubus.utils.FragmentController;
import com.slheavner.wvubus.utils.Logger;
import com.slheavner.wvubus.utils.PrefsUtil;
import com.slheavner.wvubus.views.StatusView;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, OnSheetDismissedListener, FragmentManager.OnBackStackChangedListener,
        FragmentController.FragmentTransactionListener, BusMapFragment.BusMapFragmentListener,
        BusSelectDialogFragment.BusSelectClosedListener{

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
        //Google Maps preload for better transition to map fragment
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            showPreferencesFragment();
            return true;
        }else if(id == android.R.id.home){
            this.onBackPressed();
            return true;
        }else if(id == R.id.action_about){
            showAboutFragment();
        }
        return super.onOptionsItemSelected(item);
    }

    public void floatingButtonClick(View view){
        View mapInfo = this.findViewById(R.id.map_info_sheet);
        if(mapInfo != null && mapInfo.getVisibility() == View.VISIBLE){
            buttonSlideOut(button);
            BottomSheetLayout bottomSheet = (BottomSheetLayout) mapInfo;
            if(bottomSheet.isSheetShowing()){
                bottomSheet.dismissSheet();
            }else{
                bottomSheet.addOnSheetDismissedListener(this);
                View sheet = LayoutInflater.from(this).inflate(R.layout.info_layout, bottomSheet, false);
                createInfoSheet(sheet, (Bus) view.getTag());
                bottomSheet.setPeekSheetTranslation(bottomSheet.getMaxSheetTranslation() / 2);
                bottomSheet.showWithSheetView(sheet);
            }
        }else{
            DialogFragment df = BusSelectDialogFragment.newInstance(this);
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
        CardHelper.setLocations(bus, statusViews);
    }

    private TextView getTextView(View v, int id){
        return (TextView) v.findViewById(id);
    }

    private void showBusMap(Bus bus){
        BusMapFragment busMapFragment = BusMapFragment.newInstance(bus);
        busMapFragment.setBusMapFragmentListener(this);
        FragmentController.setMainFragment(this, busMapFragment, BusMapFragment.TAG, true);
        switchButtonIcon(button, R.drawable.ic_view_list_white_24dp);
    }

    private void showPreferencesFragment(){
        SettingsFragment settingsFragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG);
        if(settingsFragment == null){
            settingsFragment = new SettingsFragment();
        }
        if(!settingsFragment.isVisible()){
            FragmentController.setMainFragment(this, settingsFragment, SettingsFragment.TAG, true);
            buttonSlideOut(button);
        }
    }

    private void showAboutFragment(){
        AboutFragment aboutFragment = (AboutFragment) getSupportFragmentManager().findFragmentByTag(AboutFragment.TAG);
        if(aboutFragment == null){
            aboutFragment = new AboutFragment();
        }
        if(!aboutFragment.isVisible()){
            FragmentController.setMainFragment(this, aboutFragment, AboutFragment.TAG, true);
            buttonSlideOut(button);
        }
    }

    @Override
    public void onClick(View v) {
        Logger.debug(this, "MainActivity on click");
        Object tag = v.getTag();
        if(tag instanceof Bus){
            showBusMap((Bus) tag);
        }
    }

    @Override
    public void onDismissed(BottomSheetLayout bottomSheetLayout) {
        buttonSlideIn(button, 0);
    }

    @Override
    public void onBackStackChanged() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0){
            showBackButton(true);
        }else{
            showBackButton(false);
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
        if(button.isShown()){
            button.startAnimation(slideOut);
        }
    }

    public void buttonSlideIn(FloatingActionButton button, int resource){
        if(resource != 0){
            button.setImageResource(resource);
        }
        if(!button.isShown()){
            button.setVisibility(View.VISIBLE);
            button.startAnimation(slideIn);
        }
    }

    @Override
    public void onTransaction(String oldTag, String newTag) {
        //do something
    }

    @Override
    public void onMapResume() {
        if(!button.isShown()){
            buttonSlideIn(button, R.drawable.ic_view_list_white_24dp);
        }
    }

    private void showBackButton(boolean show){
        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.setDisplayHomeAsUpEnabled(show);
        }
    }

    @Override
    public void onSave() {
        if(mainFragment != null){
            mainFragment.updateAdapter();
        }
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
            button.setVisibility(View.INVISIBLE);
            animation.setAnimationListener(MainActivity.this.hideOnSlideEndListener);
            buttonSlideIn(button, this.r);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

}
