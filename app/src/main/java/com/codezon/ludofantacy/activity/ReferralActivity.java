package com.codezon.ludofantacy.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.codezon.ludofantacy.R;
import com.codezon.ludofantacy.helper.Function;
import com.codezon.ludofantacy.helper.Preferences;

import java.util.Objects;

public class ReferralActivity extends AppCompatActivity {

    private TextView referTv;
    public Button shareBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        referTv = findViewById(R.id.referTv);
        shareBt = findViewById(R.id.shareBt);

        referTv.setText(Preferences.getInstance(this).getString(Preferences.KEY_USERNAME));

        shareBt.setOnClickListener(v -> Function.shareApp(ReferralActivity.this, referTv.getText().toString()));
    }
}