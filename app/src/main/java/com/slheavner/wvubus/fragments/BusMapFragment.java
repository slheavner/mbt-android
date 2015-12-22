package com.slheavner.wvubus.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.utils.FragmentController;
import com.slheavner.wvubus.utils.Logger;
import com.slheavner.wvubus.utils.PrefsUtil;

/**
 * Individual bus information Fragment. Fullscreen map with a bottom info sheet that is activated by the
 * FloatingActionButton. The MapFragment is inserted upon parent Fragment creation. All Map operations should be
 * done in onMapReady()
 */
public class BusMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = BusMapFragment.class.getSimpleName();
    public static final String MAP_TAG = "MapFragment";

    private BusMapFragmentListener listener;
    private Bus bus;
    //private BottomSheetLayout bottomSheetLayout;
    private SupportMapFragment mapFragment;

    public static BusMapFragment newInstance(Bus bus) {
        BusMapFragment map = new BusMapFragment();
        map.setBus(bus);
        return map;
    }

    public void setBusMapFragmentListener(BusMapFragmentListener listener) {
        this.listener = listener;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_bus_map, null);
        //bottomSheetLayout = (BottomSheetLayout) rootView.findViewById(R.id.map_info_sheet);

        //create start target, we don't stay there because the bounds are set by the route
        GoogleMapOptions mapOptions;
        if (!bus.getId().equals("prt")) {
            mapOptions = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(bus.getPolyline().get(0), 13));
        } else {
            mapOptions = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(new LatLng(39.643373, -79.958871), 13));

        }
        mapFragment = SupportMapFragment.newInstance(mapOptions);
        mapFragment.getMapAsync(this);  //this syncs the onMapReady event below
        FragmentController.replaceToFrame(
                (AppCompatActivity) getActivity(),
                mapFragment,
                MAP_TAG,
                false,
                R.id.mapfragment);

        //set the bus object as the tag for the floatingActionButton
        getActivity().findViewById(R.id.add_button).setTag(this.bus);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBarName(bus.getNumber() + "  " + bus.getName());
        if (listener != null) {
            listener.onMapResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        setActionBarName(getResources().getString(R.string.app_name));
    }

    private void setActionBarName(String name) {
        try {
            ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (bar != null) {
                bar.setTitle(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void trySetLocationEnabled(GoogleMap googleMap){
        try{
            int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck += ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED * 2){
                googleMap.setMyLocationEnabled(true);
            }else{
                Logger.debug(this, "Permissions not granted. :|");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        trySetLocationEnabled(googleMap);
        if (!bus.getId().equals("prt")) {
            Polygon polygon = googleMap.addPolygon(new PolygonOptions()
                    .addAll(bus.getPolyline())
                    .strokeColor(R.color.md_light_blue_900)
                    .strokeWidth(7)
                    .visible(true)
                    .zIndex(30));

            //check for color setting
            if (PrefsUtil.useColorMap(this.getContext())) {
                polygon.setStrokeColor(bus.getPolylineColor());
            }

            //get the bounds from the polyline
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for (LatLng ll : polygon.getPoints()) {
                builder.include(ll);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
            if(bus.getLocations()[bus.getLocations().length-1] != null){
                Bus.Location loc = bus.getLocations()[bus.getLocations().length-1];
                //Toast.makeText(getActivity(), loc.getLat() + ", " + loc.getLon(),  Toast.LENGTH_SHORT).show();
                if(loc.getLat() != 0 && loc.getLon() != 0){
                    MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(loc.getLon(), loc.getLat()))
                        .title(loc.getDesc());
                    googleMap.addMarker(marker);
                }
            }
        }else{
            googleMap.addMarker(new MarkerOptions().position(new LatLng(39.654830, -79.960229)).title("Medical"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(39.647121, -79.973185)).title("Evansdale"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(39.647592, -79.969016)).title("Towers"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(39.634853, -79.956218)).title("Beechurst"));
            googleMap.addMarker(new MarkerOptions().position(new LatLng(39.629976, -79.957220)).title("Walnut"));
        }
    }

    public interface BusMapFragmentListener{
        void onMapResume();
    }
}
