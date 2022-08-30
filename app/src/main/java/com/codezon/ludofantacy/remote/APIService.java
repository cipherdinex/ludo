package com.codezon.ludofantacy.remote;

import com.codezon.ludofantacy.helper.AppConstant;
import com.codezon.ludofantacy.model.MyResponse;
import com.codezon.ludofantacy.model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key="+ AppConstant.AUTHORIZATION_KEY
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}

