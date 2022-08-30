package com.codezon.ludofantacy.services;

import com.codezon.ludofantacy.MyApplication;
import com.codezon.ludofantacy.api.ApiCalling;
import com.codezon.ludofantacy.helper.AppConstant;
import com.codezon.ludofantacy.helper.Function;
import com.codezon.ludofantacy.helper.Preferences;
import com.codezon.ludofantacy.model.UserModel;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.List;

import androidx.annotation.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private ApiCalling api;

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        api = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Preferences.getInstance(getApplicationContext()).getString(Preferences.KEY_USER_ID) != null) {
            updateUserProfileFCM(FirebaseInstanceId.getInstance().getToken());
        }
    }

    private void updateUserProfileFCM(String token) {
        if (Function.checkNetworkConnection(this)) {
            Call<UserModel> callToken = api.updateUserProfileToken(AppConstant.PURCHASE_KEY, Preferences.getInstance(getApplicationContext()).getString(Preferences.KEY_USER_ID), token);
            callToken.enqueue(new Callback<UserModel>() {
                @Override
                public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {

                    if (response.isSuccessful()) {
                        UserModel legalData = response.body();
                        List<UserModel.Result> res;
                    }

                }

                @Override
                public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {

                }
            });
        }
    }

}
