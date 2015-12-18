package com.slheavner.wvubus.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.Gson;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;
import com.slheavner.wvubus.views.adapters.BusAdapter;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import jp.wasabeef.recyclerview.animators.LandingAnimator;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment{

    public static String TAG = "MainActivityFragment";

    public static String API_URL = "https://morgantownbustracker.herokuapp.com/initialize";

    private FloatingActionButton floatingActionButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View rootView;
    private RecyclerView listView;
    private OkHttpClient client;
    private RecyclerView.LayoutManager layoutManager;
    private BusAdapter adapter;
    private List<Bus> busData = new ArrayList<Bus>();

    public MainActivityFragment() {
        this.client = new OkHttpClient();
    }

    private List<Bus> readBusJson() throws IOException {
        FileInputStream fis = this.getContext().openFileInput("buses.json");
        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        String json = new String(bytes);
        Bus[] buses = new Gson().fromJson(json, Bus[].class);
        return orderBusList(buses);
    }

    private List<Bus> orderBusList(Bus[] buses){
        List<String> busIds = Arrays.asList(getResources().getStringArray(R.array.bus_ids));
        Bus[] busList = new Bus[buses.length];
        for(Bus b : buses){
            busList[busIds.indexOf(b.get_id())] = b;
        }
        return Arrays.asList(busList);
    }

    private List<Bus> orderBusList(List<Bus> buses){
        List<String> busIds = Arrays.asList(getResources().getStringArray(R.array.bus_ids));
        Bus[] busList = new Bus[buses.size()];
        for(Bus b : buses){
            busList[busIds.indexOf(b.get_id())] = b;
        }
        return Arrays.asList(busList);
    }

    private void installRawJson(){
        BufferedReader reader =  new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.buses)));
        this.busData = Arrays.asList( new Gson().fromJson(reader, Bus[].class));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {

            this.busData = readBusJson();
        } catch (IOException e) {
            installRawJson();
            new UpdateBusAsyncTask().execute(API_URL);
            e.printStackTrace();
        }
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        this.rootView = rootView;
        this.floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.add_button);
        this.swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_view);
        this.listView = (RecyclerView) rootView.findViewById(R.id.list_view);
        listView.setItemAnimator(new LandingAnimator());
        listView.getItemAnimator().setRemoveDuration(700);
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateBusAsyncTask().execute(API_URL);
            }
        });
        this.layoutManager = new LinearLayoutManager(getActivity());
        this.listView.setLayoutManager(layoutManager);
        this.adapter = new BusAdapter(busData, getActivity());
        this.listView.setAdapter(adapter);
        return rootView;
    }

    private class UpdateBusAsyncTask extends AsyncTask<String, Integer, List<Bus>>{

        @Override
        protected List<Bus> doInBackground(String[] params) {
            ArrayList<Bus> buses = new ArrayList<Bus>();
            for(String url : params){
                try{
                    Gson gson = new Gson();
                    String busJson = getResponseBody(url);
                    buses.addAll(Arrays.asList(gson.fromJson(busJson, Bus[].class)));
                    if(buses.size() > 0){
                        FileOutputStream fos = getContext()
                                .openFileOutput("buses.json", Context.MODE_PRIVATE);
                        fos.write(busJson.getBytes());
                        fos.close();
                    }else{
                        return null;
                    }
                }catch (Exception e){
                    Log.d("mbt",e.toString());
                }
            }
            return orderBusList(buses);
        }

        @Override
        protected void onPostExecute(List<Bus> buses) {
            super.onPostExecute(buses);
            swipeRefreshLayout.setRefreshing(false);
            if (buses.size() == 0){
                Log.d("mbt", "no buses");
            }else{
                Log.d("mbt", "many buses");
                if(MainActivityFragment.this.adapter != null){
                    MainActivityFragment.this.busData = buses;
                    adapter.setData(buses, MainActivityFragment.this.getActivity());
                    //adapter.notifyDataSetChanged();
                }
            }
        }

        private String getResponseBody(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            Log.d("mbt", body);
            return body;

        }
    }

    public void updateAdapter(){
        this.adapter.notifyDataSetChanged();
    }


}
