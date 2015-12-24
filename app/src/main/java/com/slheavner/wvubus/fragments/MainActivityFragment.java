package com.slheavner.wvubus.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.google.gson.Gson;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.utils.Logger;
import com.slheavner.wvubus.views.adapters.BusAdapter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main fragment with list of Bus CardViews.
 */
public class MainActivityFragment extends Fragment{

    public static String TAG = MainActivityFragment.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private RecyclerView recyclerView;
    private OkHttpClient client;
    private RecyclerView.LayoutManager layoutManager;
    private BusAdapter busAdapter;
    private List<Bus> busData = new ArrayList<Bus>();
    private String apiUrl = "";

    public MainActivityFragment() {    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        client = new OkHttpClient();
        apiUrl = getResources().getString(R.string.mbt_url_api);

        confirmBusData(true);

        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //setup SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        //get manager and adapter for RecyclerView
        layoutManager = new LinearLayoutManager(getActivity());
        busAdapter = new BusAdapter(busData, getActivity());

        //setup RecyclerView
        if(recyclerView == null){
            recyclerView = (RecyclerView) rootView.findViewById(R.id.list_view);
            //recyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(recyclerView));
//            recyclerView.getItemAnimator().setRemoveDuration(600);
//            recyclerView.getItemAnimator().setAddDuration(600);
        }
        if(recyclerView.getLayoutManager() == null){
            recyclerView.setLayoutManager(layoutManager);
        }
        if(recyclerView.getAdapter() == null){
            recyclerView.setAdapter(busAdapter);
        }

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        confirmBusData(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Logger.debug(this, "hidden state changed: " + hidden );
    }

    //public available to force color change
    public void updateAdapter(){
        if(busAdapter != null){
            busAdapter.notifyEnabledBusesChanged();
        }
    }

    //make sure bus data is available synchronously
    private void confirmBusData(boolean refresh){
        if(busData == null || busData.size() == 0){
            try{
                busData = readBusJson();
            }catch (IOException e){
                e.printStackTrace();
                installRawJson();

            }
        }
        if(refresh){
            refresh();
        }
    }

    private void refresh(){
        new UpdateBusAsyncTask().execute(apiUrl);
    }

    private void manualRefresh(){
        if(swipeRefreshLayout != null){
            swipeRefreshLayout.setRefreshing(true);
        }
        refresh();
    }

    /**
     * Reads the json in storage at /buses.json
     * @return List of buses
     * @throws IOException if File doesn't exist, should write a json if it isn't there
     */
    private List<Bus> readBusJson() throws IOException {
        FileInputStream fis = this.getContext().openFileInput("buses.json");
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        String json = new String(bytes);
        Bus[] buses = new Gson().fromJson(json, Bus[].class);
        return Arrays.asList(buses);
    }

    /**
     * Not sure this is needed with the new way the adapter updates are handled.
     * TODO: try to remove this
     * @param buses bus array to order
     * @return order List of buses
     */
//    private List<Bus> orderBusList(Bus[] buses){
//        List<String> busIds = Arrays.asList(getResources().getStringArray(R.array.bus_ids));
//        Bus[] busList = new Bus[buses.length];
//        for(Bus b : buses){
//            busList[busIds.indexOf(b.getId())] = b;
//        }
//        return Arrays.asList(busList);
//    }
//
//    /**
//     * Not sure this is needed with the new way the adapter updates are handled.
//     * TODO: try to remove this
//     * @param buses bus list to order
//     * @return order List of buses
//     */
//    private List<Bus> orderBusList(List<Bus> buses){
//        List<String> busIds = Arrays.asList(getResources().getStringArray(R.array.bus_ids));
//        Bus[] busList = new Bus[buses.size()];
//        for(Bus b : buses){
//            busList[busIds.indexOf(b.getId())] = b;
//        }
//        return Arrays.asList(busList);
//    }

    /**
     * Installs the json at res/raw/buses.json
     */
    private void installRawJson(){
        BufferedReader reader =  new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.buses)));
        this.busData = Arrays.asList( new Gson().fromJson(reader, Bus[].class));
    }

    /**
     * Refresh task. As simple as can be at this point, just gets the full list and saves it to disk.
     */
    private class UpdateBusAsyncTask extends AsyncTask<String, Integer, List<Bus>>{

        @Override
        protected List<Bus> doInBackground(String[] params) {
            ArrayList<Bus> buses = new ArrayList<Bus>();
            for(String url : params){
                try{
                    Gson gson = new Gson();
                    String busJson = getResponseBody(url);
                    buses.addAll(Arrays.asList(gson.fromJson(busJson, Bus[].class)));
                    if(buses.size() > 0  && getContext() != null){
                        //probably a better place to write this, while staying async.
                        FileOutputStream fos = getContext()
                                .openFileOutput("buses.json", Context.MODE_PRIVATE);
                        fos.write(busJson.getBytes());
                        fos.close();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return buses;
        }

        @Override
        protected void onPostExecute(List<Bus> buses) {
            super.onPostExecute(buses);
            if(swipeRefreshLayout != null){
                swipeRefreshLayout.setRefreshing(false);
            }
            if (buses.size() == 0 && getActivity() != null){
                Logger.debug(this, "no buses");
                Toast.makeText(getActivity(), "There was a problem getting bus data.", Toast.LENGTH_SHORT).show();
            }else{
                Logger.debug(this, "got " + buses.size() + " buses");
                if(MainActivityFragment.this.busAdapter != null){
                    MainActivityFragment.this.busData = buses;
                    busAdapter.setData(buses);
                    //busAdapter.notifyDataSetChanged();
                }
            }
        }

        private String getResponseBody(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            Logger.debug(this, body);
            return body;

        }
    }



}
