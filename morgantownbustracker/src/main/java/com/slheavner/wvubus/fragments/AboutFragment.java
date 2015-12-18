package com.slheavner.wvubus.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.slheavner.wvubus.R;

/**
 * Created by Sam on 12/18/2015.
 */
public class AboutFragment extends Fragment {

    final String description = "&emsp;This app was made out of a need to track the buses around Morgantown. " +
            "The owner, Samuel Heavner, also made it because of his love of programming. Any and all feedback " +
            "is welcomed with open arms." +
            "<br><br>" +
            "&emsp;Furthermore, this app is now <b>open source</b>! That means that you can view all of the code that " +
            "makes the app run, and even fix bugs or add features yourself! ";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.about, null);
        ((TextView) root.findViewById(R.id.about_desc)).setText(Html.fromHtml(description));
        ((ImageButton)root.findViewById(R.id.about_github)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com/slheavner";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        return root;
    }


}
