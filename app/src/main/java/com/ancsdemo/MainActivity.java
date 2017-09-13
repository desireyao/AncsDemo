package com.ancsdemo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";


    private Button btn_check_sms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_check_sms = (Button) findViewById(R.id.btn_check_sms);
        btn_check_sms.setOnClickListener(this);
    }

    public void registerContentObserver() {
        ContentResolver resolver = getContentResolver();
        SmsObserver mObserver = new SmsObserver(resolver, new SmsObserver.SmsHandler(this));
        resolver.registerContentObserver(Uri.parse("content://sms"), true, mObserver);
    }

    @Override
    public void onClick(View v) {
        registerContentObserver();
    }
}
