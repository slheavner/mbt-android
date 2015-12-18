package com.slheavner.wvubus.fragments;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.utils.FragmentController;
import com.slheavner.wvubus.utils.PrefsUtil;

public class BusMapFragment extends Fragment implements OnMapReadyCallback{

    public static final String TAG = "BusMapFragment";
    public static final String MAP_TAG = "MapFragment";

    private Bus bus;
    private BottomSheetLayout bottomSheetLayout;
    private SupportMapFragment mapFragment;


    public static BusMapFragment newInstance(Bus bus){
        BusMapFragment map = new BusMapFragment();
        map.setBus(bus);
        return map;
    }

    public void setBus(Bus bus){
        this.bus = bus;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_map, null);
        this.bottomSheetLayout = (BottomSheetLayout) view.findViewById(R.id.map_info_sheet);
        GoogleMapOptions mapOptions;
        if(!bus.get_id().equals("prt")){
            mapOptions = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(bus.getPolyline().get(0), 13));
        }else{
            mapOptions = new GoogleMapOptions().camera(CameraPosition.fromLatLngZoom(new LatLng(39.643373, -79.958871), 13));

        }
        mapFragment = SupportMapFragment.newInstance(mapOptions);
        mapFragment.getMapAsync(this);
        FragmentController.replaceToFrame((AppCompatActivity) getActivity(), mapFragment, MAP_TAG, false, R.id.mapfragment);
        getActivity().findViewById(R.id.add_button).setTag(this.bus);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(!bus.get_id().equals("prt")){
            Polygon polygon = googleMap.addPolygon(new PolygonOptions().addAll(bus.getPolyline()).strokeColor(R.color.md_light_blue_900).strokeWidth(5).visible(true).zIndex(30));

            if(PrefsUtil.useColorMap(this.getContext())){
                polygon.setStrokeColor(bus.getPolylineColor());
            }
            LatLngBounds.Builder builder = LatLngBounds.builder();
            for(LatLng ll : polygon.getPoints()){
                builder.include(ll);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
            if(bus.getLocations()[bus.getLocations().length-1] != null){

                Bus.Location loc = bus.getLocations()[bus.getLocations().length-1];
                Toast.makeText(getActivity(), loc.getLat() + ", " + loc.getLon(),  Toast.LENGTH_SHORT).show();
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

}
