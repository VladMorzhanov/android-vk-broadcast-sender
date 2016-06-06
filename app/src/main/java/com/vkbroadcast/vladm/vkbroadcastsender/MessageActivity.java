package com.vkbroadcast.vladm.vkbroadcastsender;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {

    private ArrayList<String> friendsID;

    private EditText editText;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {

            toolbar.setBackgroundColor(ContextCompat.getColor(getApplication(), R.color.toolbar));

            toolbar.setTitle("Create message");

            toolbar.setTitleTextColor(ContextCompat.getColor(getApplication(), R.color.vk_white));

        }

        friendsID = getIntent().getExtras().getStringArrayList("vk_ids");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();


                String message = editText.getText().toString();


                /**
                 * send this message to selected friends
                 */



            }
        });
    }
}