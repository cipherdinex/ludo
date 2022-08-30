package com.codezon.ludofantacy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codezon.ludofantacy.MyApplication;
import com.codezon.ludofantacy.R;
import com.codezon.ludofantacy.api.ApiCalling;
import com.codezon.ludofantacy.helper.AppConstant;
import com.codezon.ludofantacy.helper.Function;
import com.codezon.ludofantacy.helper.Preferences;
import com.codezon.ludofantacy.helper.ProgressBar;
import com.codezon.ludofantacy.model.Token;
import com.codezon.ludofantacy.model.UserModel;
import com.codezon.ludofantacy.payu.ServiceWrapper;
import com.google.android.material.textfield.TextInputEditText;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DepositActivity extends AppCompatActivity {

    public RadioButton payTmRb, googlePayRb, phonePeRb;
    private TextInputEditText amountEt;
    public TextView signTv, noteTv, alertTv;
    private Button submitBt;

    private ProgressBar progressBar;
    private ApiCalling api;

    private String amountSt;
    private String mopSt;
    public String orderIdSt, paymentIdSt, checksumSt, tokenSt;

    private static final String TAG = DepositActivity.class.getSimpleName();
    private final Integer activityRequestCode = 2;

    private final PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    private PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBar = new ProgressBar(this, false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        getUserDetails();

        payTmRb = findViewById(R.id.payTmRb);
        googlePayRb = findViewById(R.id.googlePayRb);
        phonePeRb = findViewById(R.id.phonePeRb);
        amountEt = findViewById(R.id.amountEt);
        noteTv = findViewById(R.id.noteTv);
        alertTv = findViewById(R.id.alertTv);
        signTv = findViewById(R.id.signTv);
        submitBt = findViewById(R.id.submitBt);

        signTv.setText(AppConstant.CURRENCY_SIGN);
        alertTv.setText(String.format("Minimum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_DEPOSIT_LIMIT));

        submitBt.setOnClickListener(v -> {
            submitBt.setEnabled(false);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Random rand = new Random();
            int min =1000, max= 9999;

            // nextInt as provided by Random is exclusive of the top value so you need to add 1
            int randomNum = rand.nextInt((max - min) + 1) + min;
            orderIdSt = System.currentTimeMillis() + randomNum + Preferences.getInstance(this).getString(Preferences.KEY_USER_ID);
            paymentIdSt = orderIdSt;

            amountSt = Objects.requireNonNull(amountEt.getText()).toString();
            if (!amountSt.isEmpty()) {
                double payout = Integer.parseInt(amountEt.getText().toString());

                if (payout < AppConstant.MIN_DEPOSIT_LIMIT) {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText(String.format("Minimum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_DEPOSIT_LIMIT));
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                } else if (payout > AppConstant.MAX_DEPOSIT_LIMIT) {
                    submitBt.setEnabled(true);
                    alertTv.setVisibility(View.VISIBLE);
                    alertTv.setText(String.format("Maximum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MAX_DEPOSIT_LIMIT));
                    alertTv.setTextColor(Color.parseColor("#ff0000"));
                } else {
                    alertTv.setVisibility(View.GONE);
                    choosePaymentModePrompt(this);
                }
            } else {
                submitBt.setEnabled(true);
                alertTv.setVisibility(View.VISIBLE);
                alertTv.setText(String.format("Minimum Add Amount is %s%d", AppConstant.CURRENCY_SIGN, AppConstant.MIN_DEPOSIT_LIMIT));
                alertTv.setTextColor(Color.parseColor("#ff0000"));
            }
        });

    }

    private void getUserDetails() {
        Call<UserModel> call = api.getUserDetails(AppConstant.PURCHASE_KEY, Preferences.getInstance(this).getString(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel legalData = response.body();
                    List<UserModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            if (res.get(0).getIs_block() == 1) {
                                Preferences.getInstance(DepositActivity.this).setString(Preferences.KEY_IS_AUTO_LOGIN,"0");

                                Intent i = new Intent(DepositActivity.this, LoginActivity.class);
                                i.putExtra("finish", true);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                            else if (res.get(0).getIs_active() == 0) {
                                Preferences.getInstance(DepositActivity.this).setString(Preferences.KEY_IS_AUTO_LOGIN,"0");

                                Intent i = new Intent(DepositActivity.this, LoginActivity.class);
                                i.putExtra("finish", true);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void postDeposit() {
        progressBar.showProgressDialog();
        Call<UserModel> call = api.postDeposit(AppConstant.PURCHASE_KEY, Preferences.getInstance(this).getString(Preferences.KEY_USER_ID), orderIdSt, paymentIdSt, checksumSt, Double.parseDouble(amountSt), mopSt );
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                progressBar.hideProgressDialog();

                if (response.isSuccessful()) {
                    UserModel legalData = response.body();
                    List<UserModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        Function.showToast(DepositActivity.this, res.get(0).getMsg());
                        onBackPressed();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                progressBar.hideProgressDialog();
            }
        });
    }


    private void choosePaymentModePrompt(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_payment_mode, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(inflate);

        final CheckedTextView paytmTv = inflate.findViewById(R.id.paytmTv);
        final CheckedTextView upiTv = inflate.findViewById(R.id.upiTv);
        final CheckedTextView payuTv = inflate.findViewById(R.id.payuTv);

        if (AppConstant.MODE_OF_PAYMENT == 1) {
            paytmTv.setVisibility(View.VISIBLE);
            payuTv.setVisibility(View.GONE);
        }
        else if (AppConstant.MODE_OF_PAYMENT == 2) {
            paytmTv.setVisibility(View.GONE);
            payuTv.setVisibility(View.VISIBLE);
        }
        else {
            paytmTv.setVisibility(View.VISIBLE);
            payuTv.setVisibility(View.VISIBLE);
        }

        paytmTv.setChecked(true);
        mopSt= "PayTm";

        Button continueBt = inflate.findViewById(R.id.continueBt);
        Button cancelBt = inflate.findViewById(R.id.cancelBt);

        builder.setCancelable(false);
        AlertDialog create = builder.create();

        cancelBt.setOnClickListener(view -> create.cancel());

        create.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == 4) {
                create.dismiss();
                submitBt.setEnabled(true);
            }
            return Boolean.parseBoolean(null);
        });

        paytmTv.setOnClickListener(v -> {
            if (!paytmTv.isChecked()){
                paytmTv.setChecked(true);
                upiTv.setChecked(false);
                payuTv.setChecked(false);
                mopSt ="PayTm";
            }
        });

        upiTv.setOnClickListener(v -> {
            if (!upiTv.isChecked()){
                upiTv.setChecked(true);
                paytmTv.setChecked(false);
                payuTv.setChecked(false);
                mopSt ="UPI";
            }
        });

        payuTv.setOnClickListener(v -> {
            if (!payuTv.isChecked()){
                payuTv.setChecked(true);
                upiTv.setChecked(false);
                paytmTv.setChecked(false);
                mopSt ="PayUMoney";
            }
        });

        continueBt.setOnClickListener(view -> {
            try {
                switch (mopSt) {
                    case "PayTm":
                        getToken();
                        break;
                    case "UPI":
                        //pay();
                        break;
                    case "PayUMoney":
                        alertTv.setVisibility(View.GONE);
                        startPay();
                        break;
                }
                submitBt.setEnabled(true);
                create.dismiss();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        });

        create.show();
    }


    private void startPay() {
        builder.setAmount(amountSt)                                                                 // Payment amount
                .setTxnId(paymentIdSt)                                                              // Transaction ID
                .setPhone(Preferences.getInstance(this).getString(Preferences.KEY_MOBILE))          // User Phone number
                .setProductName("Wallet Balance")                                                   // Product Name or description
                .setFirstName(Preferences.getInstance(this).getString(Preferences.KEY_FULL_NAME))   // User First name
                .setEmail(Preferences.getInstance(this).getString(Preferences.KEY_EMAIL))           // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")               // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")               // Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                                                                  // Integration environment - true (Debug)/ false(Production)
                .setKey(AppConstant.MERCHANT_KEY)                                                   // Merchant key
                .setMerchantId(AppConstant.MERCHANT_ID);
        try {
            paymentParam = builder.build();
            getHashkey();

        } catch (Exception e) {
            Log.e(TAG, " error s "+e.toString());
        }
    }

    public void getHashkey(){
        ServiceWrapper service = new ServiceWrapper(null);
        Call<String> call = service.newHashCall(AppConstant.MERCHANT_KEY, paymentIdSt, amountSt, "Wallet Balance", Preferences.getInstance(this).getString(Preferences.KEY_FULL_NAME), Preferences.getInstance(this).getString(Preferences.KEY_EMAIL));

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                Log.e(TAG, "hash res "+response.body());
                String merchantHash= response.body();
                if (Objects.requireNonNull(merchantHash).isEmpty()) {
                    Toast.makeText(DepositActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "hash empty");
                } else {
                    // mPaymentParams.setMerchantHash(merchantHash);
                    paymentParam.setMerchantHash(merchantHash);
                    // Invoke the following function to open the checkout page.
                    // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, DepositActivity.this, R.style.AppTheme_default, false);
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.e(TAG, "hash error "+ t.toString());
            }
        });
    }



    private  void getToken(){
        Log.e(TAG, " get token start");
        progressBar.showProgressDialog();
        if (Function.checkNetworkConnection(DepositActivity.this)) {
            Call<Token> callToken = api.generateTokenCall("12345", AppConstant.M_ID, orderIdSt, amountSt);
            callToken.enqueue(new Callback<Token>() {
                @Override
                public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                    Log.e(TAG, " respo "+ response.isSuccessful() );
                    try {
                        if (response.isSuccessful() && response.body()!=null){
                            if (!response.body().getBody().getTxnToken().equals("")) {
                                progressBar.hideProgressDialog();
                                Log.e(TAG, " transaction token : "+response.body().getBody().getTxnToken());
                                startPaytmPayment(response.body().getBody().getTxnToken());
                            }else {
                                Log.e(TAG, " Token status false");
                                progressBar.hideProgressDialog();
                            }
                        }
                    }catch (Exception e){
                        Log.e(TAG, " error in Token Res "+e.toString());
                        progressBar.hideProgressDialog();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                    Log.e(TAG, " response error "+t.toString());
                    progressBar.hideProgressDialog();
                }
            });
        }
    }

    public void startPaytmPayment (String token){
        tokenSt = token;
        // for test mode use it
        //String host = "https://securegw-stage.paytm.in/";

        // for production mode use it
        String host = "https://securegw.paytm.in/";

        String callBackUrl = host + "theia/paytmCallback?ORDER_ID="+orderIdSt;
        Log.e(TAG, " callback URL "+callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdSt, AppConstant.M_ID, tokenSt, amountSt, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                Log.e(TAG, "Response (onTransactionResponse) : "+bundle.toString());

                String orderId = bundle.getString("ORDERID");
                String status = bundle.getString("STATUS");
                String txnId = bundle.getString("TXNID");
                String checksum = bundle.getString("CHECKSUMHASH");

                if(Objects.requireNonNull(status).equalsIgnoreCase("TXN_SUCCESS")) {
                    orderIdSt = orderId;
                    paymentIdSt = txnId;
                    checksumSt =checksum;

                    postDeposit();
                }
            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess "+ s);
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth "+s);
            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error "+s);
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web "+s+"--"+s1);
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel "+s);
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, activityRequestCode);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG ," result code "+resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){
                    //Success Transaction
                    checksumSt = "123";
                    postDeposit();
                } else{
                    //Failure Transaction
                    Toast.makeText(DepositActivity.this, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e(TAG, "tran "+payuResponse+"---"+ merchantResponse);
            }
        }
        else if (requestCode == activityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }

            try {
                JSONObject jsonObject = new JSONObject(Objects.requireNonNull(data.getStringExtra("response")));
                String status = jsonObject.getString("STATUS");

                if(status.equalsIgnoreCase("TXN_SUCCESS")) {
                    paymentIdSt = jsonObject.getString("TXNID");
                    checksumSt = jsonObject.getString("CHECKSUMHASH");
                    orderIdSt = jsonObject.getString("ORDERID");

                    postDeposit();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e(TAG, " TXNID "+  paymentIdSt);
            Log.e(TAG, " CHECKSUMHASH "+  checksumSt);
            Log.e(TAG, " ORDERID "+  orderIdSt);

            Log.e(TAG, " data "+  data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response - "+data.getStringExtra("response"));
        } else {
            Log.e(TAG, " payment failed");
        }
    }
}