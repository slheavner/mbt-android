package com.slheavner.wvubus.models;

import android.graphics.Color;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Bus model. Must be compatible with /res/raw/buses.json
 */
public class Bus {

    private String id;

    private String name, number, service, firstrun, lastrun, runtime, polylineColor;

    private long updateInfo, updateLocations, updateLine;

    private Location[] locations;

    private Coordinate[] polyline;

    public Bus(){

    }

    public static Bus emptyBusObject(String id){
        Bus bus = new Bus();
        bus.id = id;
        bus.name = "";
        bus.number = "";
        return bus;
    }

    public static class Coordinate{

        double lat;
        double lon;

        public Coordinate(){

        }

        public double getLat(){
            return lat;
        }
        public double getLon(){
            return lon;
        }
    }

    public static class Location{

        String desc;
        int bus;
        double lat;
        double lon;
        long time;

        public Location(){

        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public int getBus(){
            return bus;
        }

        public void setBus(int bus){
            this.bus = bus;
        }



    }



	/*
	 * =============================
	 * getters and setters
	 * =============================
	 */


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public long getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(long updateInfo) {
        this.updateInfo = updateInfo;
    }

    public long getUpdateLocations() {
        return updateLocations;
    }

    public void setUpdateLocations(long updateLocations) {
        this.updateLocations = updateLocations;
    }

    public long getUpdateLine() {
        return updateLine;
    }

    public void setUpdateLine(long updateLine) {
        this.updateLine = updateLine;
    }

    public Location[] getLocations() {
        return locations;
    }

    public void setLocations(Location[] locations) {
        this.locations = locations;
    }

    public List<LatLng> getPolyline(){
        if(polyline == null){
            return null;
        }
        List<LatLng> pline = new ArrayList<LatLng>();
        for(Coordinate c : polyline){
            pline.add(new LatLng(c.lat, c.lon));
        }
        return pline;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFirstrun() {
        return firstrun;
    }

    public void setFirstrun(String firstrun) {
        this.firstrun = firstrun;
    }

    public String getLastrun() {
        return lastrun;
    }

    public void setLastrun(String lastrun) {
        this.lastrun = lastrun;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public int getPolylineColor() {
        return Color.parseColor(polylineColor);
    }

    public void setPolylineColor(String polylineColor) {
        this.polylineColor = polylineColor;
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof Bus){
            Bus cBus = (Bus) o;
            if(!this.id.equals(((Bus) o).getId())){
                return false;
            }
            for(int i = 0; i < locations.length; i++){
                if(locations[i].getTime() != cBus.getLocations()[i].getTime()){
                    return false;
                }
            }
        }
        return false;
    }
}
