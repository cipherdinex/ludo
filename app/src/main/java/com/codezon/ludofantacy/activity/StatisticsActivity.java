package com.codezon.ludofantacy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.codezon.ludofantacy.MyApplication;
import com.codezon.ludofantacy.R;
import com.codezon.ludofantacy.adapter.StatisticsAdapter;
import com.codezon.ludofantacy.api.ApiCalling;
import com.codezon.ludofantacy.helper.AppConstant;
import com.codezon.ludofantacy.helper.Function;
import com.codezon.ludofantacy.helper.Preferences;
import com.codezon.ludofantacy.helper.ProgressBar;
import com.codezon.ludofantacy.model.StatisticsModel;

import java.util.List;
import java.util.Objects;

public class StatisticsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTv;
    private ProgressBar progressBar;
    private ApiCalling api;
    private StatisticsAdapter statisticsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBar = new ProgressBar(this, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if(Function.checkNetworkConnection(StatisticsActivity.this)) {
            getStatistics();
        }
    }

    private void getStatistics() {
        recyclerView = findViewById(R.id.recyclerView);
        noDataTv = findViewById(R.id.noDataTv);
        progressBar.showProgressDialog();

        Call<List<StatisticsModel>> call = api.getStatistics(AppConstant.PURCHASE_KEY, Preferences.getInstance(this).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<List<StatisticsModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<StatisticsModel>> call, @NonNull Response<List<StatisticsModel>> response) {
                progressBar.hideProgressDialog();

                if (response.isSuccessful()) {
                    List<StatisticsModel> legalData = response.body();
                    if (legalData != null) {
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StatisticsActivity.this);
                        recyclerView.setLayoutManager(linearLayoutManager);
                        statisticsAdapter = new StatisticsAdapter(StatisticsActivity.this, legalData);

                        if (statisticsAdapter.getItemCount() != 0) {
                            statisticsAdapter.notifyDataSetChanged();
                            recyclerView.setAdapter(statisticsAdapter);

                            recyclerView.setVisibility(View.VISIBLE);
                            noDataTv.setVisibility(View.GONE);
                        }
                        else {
                            recyclerView.setVisibility(View.GONE);
                            noDataTv.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<StatisticsModel>> call, @NonNull Throwable t) {
                progressBar.hideProgressDialog();
            }
        });
    }
}