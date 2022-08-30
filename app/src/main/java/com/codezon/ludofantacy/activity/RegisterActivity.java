package com.codezon.ludofantacy.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codezon.ludofantacy.MyApplication;
import com.codezon.ludofantacy.R;
import com.codezon.ludofantacy.api.ApiCalling;
import com.codezon.ludofantacy.helper.AppConstant;
import com.codezon.ludofantacy.helper.Function;
import com.codezon.ludofantacy.helper.Preferences;
import com.codezon.ludofantacy.helper.ProgressBar;
import com.codezon.ludofantacy.model.UserModel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private EditText editTextName, editTextEmail, editTextMobile, editTextPassword, editTextReferral;
    public ImageView btnFB, btnGoogle;
    private CountryCodePicker countryCodePicker;

    private String strName, strUsername, strEmail, strCountryCode, strMobile, strPassword, strReferral, strDeviceId;
    public String emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private ProgressBar progressBar;
    private ApiCalling api;

    private LoginButton btnSignInFB;
    private CallbackManager callbackManager;

    public SignInButton btnSignInGoogle;
    private GoogleApiClient googleApiClient;

    private static final int REQ_CODE =9001;
    private static final String TAG = "SignInActivity";

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_register);
        api = MyApplication.getRetrofit().create(ApiCalling.class);
        progressBar = new ProgressBar(this, false);
        strDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        changeStatusBarColor();

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextReferral = findViewById(R.id.editTextReferral);
        countryCodePicker = findViewById(R.id.countryCodePicker);

        if (getIntent().getExtras() != null) {
            strName = getIntent().getExtras().getString("FULL_NAME_KEY");
            strUsername = getIntent().getExtras().getString("USERNAME_KEY");
            strEmail = getIntent().getExtras().getString("EMAIL_KEY");
            strPassword = getIntent().getExtras().getString("PASSWORD_KEY");

            try {
                if (strName != null) {
                    editTextName.setText(strName);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (strEmail != null) {
                    editTextEmail.setText(strEmail);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (strPassword != null) {
                    editTextPassword.setText(strPassword);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }

        btnSignInGoogle = findViewById(R.id.btnSignInGoogle);
        btnGoogle = findViewById(R.id.btnGoogle);

        btnSignInFB =  findViewById(R.id.btnSignInFB);
        btnFB = findViewById(R.id.btnFb);

        btnFB.setOnClickListener(v -> {
            if(Function.checkNetworkConnection(RegisterActivity.this)) {
                btnSignInFB.performClick();
            }
            else {
                Toast.makeText(getApplicationContext(), "Please check your connection", Toast.LENGTH_LONG).show();
            }
        });


        btnSignInFB.setReadPermissions(Arrays.asList("email", "public_profile"));
        btnSignInFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // Retrieving access token using the LoginResult
                AccessToken accessToken = loginResult.getAccessToken();
                useLoginInformation(accessToken);
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "cancel to Login Facebook", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(getApplicationContext(), "error to Login Facebook", Toast.LENGTH_SHORT).show();
            }
        });


        btnGoogle.setOnClickListener(v -> {
            if(Function.checkNetworkConnection(RegisterActivity.this)) {
                try {
                    if (googleApiClient != null && googleApiClient.isConnected()) {
                        googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(status -> {
                            googleApiClient.disconnect();
                            signIn();
                        });
                    }
                    else {
                        signIn();
                    }
                } catch (Exception e) {
                    Log.d("DISCONNECT ERROR", e.toString());
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Please check your connection", Toast.LENGTH_LONG).show();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */,0, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        }
    }

    public void onLoginClick(View view){
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

    }

    public void onMainClick(View view){
        try {
            InputMethodManager imm = (InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        strName = editTextName.getText().toString().trim();
        strEmail = editTextEmail.getText().toString().trim();
        strCountryCode = countryCodePicker.getSelectedCountryCode();
        strMobile = editTextMobile.getText().toString().trim();
        strPassword = editTextPassword.getText().toString().trim();
        strReferral = editTextReferral.getText().toString().trim();

        if (strName.equals("") && strEmail.equals("") && strMobile.equals("") && strPassword.equals("")) {
            Toast.makeText(this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
        } else if(!strEmail.matches(emailPattern)) {
            Toast.makeText(this, "Enter valid email id", Toast.LENGTH_SHORT).show();
        } else if (strMobile.equals("")) {
            Toast.makeText(this, "Please enter mobile no", Toast.LENGTH_SHORT).show();
        }  else if (strMobile.length() != 10) {
            Toast.makeText(this, "Please enter valid mobile no", Toast.LENGTH_SHORT).show();
        } else if (strPassword.equals("")) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
        } else if (strPassword.length() < 7) {
            Toast.makeText(this, "Minimum 6 character", Toast.LENGTH_SHORT).show();
        } else {
            try {
                if (strEmail != null){
                    String[] resArray = strEmail.split("@");
                    strUsername = resArray[0];
                }
            } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                e.printStackTrace();
            }

            verifyUserRegister();
        }

    }

    private void verifyUserRegister() {
        progressBar.showProgressDialog();
        Call<UserModel> call = api.verifyUserRegister(AppConstant.PURCHASE_KEY, strDeviceId, strMobile, strEmail, strUsername);
        call.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(@NonNull Call<UserModel> call, @NonNull Response<UserModel> response) {
                if (response.isSuccessful()) {
                    UserModel legalData = response.body();
                    List<UserModel.Result> res;
                    if (legalData != null) {
                        res = legalData.getResult();
                        if (res.get(0).getSuccess() == 1) {
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_FULL_NAME, strName);
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_USERNAME, strUsername);
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_EMAIL, strEmail);
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_COUNTRY_CODE, strCountryCode);
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_MOBILE, strMobile);
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_WHATSAPP, res.get(0).getWhatsapp_no());
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_PASSWORD, strPassword);
                            Preferences.getInstance(RegisterActivity.this).setString(Preferences.KEY_REFER_CODE, strReferral);

                            Intent i = new Intent(RegisterActivity.this, OTPActivity.class);
                            i.putExtra("PAGE_KEY","Register");
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            Function.fireIntentWithData(RegisterActivity.this, i);
                            overridePendingTransition(R.anim.slide_in_left,android.R.anim.slide_out_right);

                            progressBar.hideProgressDialog();
                        } else {
                            progressBar.hideProgressDialog();
                            Function.showToast(RegisterActivity.this, res.get(0).getMsg());
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserModel> call, @NonNull Throwable t) {
                progressBar.hideProgressDialog();
            }
        });
    }


    private void useLoginInformation(AccessToken accessToken) {
        strUsername = (accessToken.getUserId());

        //OnCompleted is invoked once the GraphRequest is successful
        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                try {
                    if (!object.getString("name").equals("null")){
                        strName = object.getString("name");
                    }
                } catch (JSONException | NullPointerException  e) {
                    e.printStackTrace();
                }

                try {
                    if (!object.getString("email").equals("null")){
                        strEmail = object.getString("email");
                    }
                } catch (JSONException | NullPointerException  e) {
                    e.printStackTrace();
                }

                try{
                    String[] resArray = strEmail.split("@");
                    strUsername = resArray[0];
                }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
                    e.printStackTrace();
                }

                strPassword = (accessToken.getUserId());

                try{
                    try {
                        if (strName != null) {
                            editTextName.setText(strName);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (strEmail != null) {
                            editTextEmail.setText(strEmail);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (strPassword != null) {
                            editTextPassword.setText(strPassword);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email");
        request.setParameters(parameters);

        // Initiate the GraphRequest
        request.executeAsync();
    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> LoginManager.getInstance().logOut()).executeAsync();
    }

    @Override
    public void onStart() {
        super.onStart();
        disconnectFromFacebook();
    }



    public void handleResult(GoogleSignInResult result){
        try {
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();

                assert account != null;
                strName = account.getDisplayName();
                strEmail = account.getEmail();
                strPassword = account.getId();

                try {
                    if (strEmail != null){
                        String[] resArray1 = strEmail.split("@");
                        strUsername = resArray1[0];
                    }
                } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                }

                try{
                    try {
                        if (strName != null) {
                            editTextName.setText(strName);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (strEmail != null) {
                            editTextEmail.setText(strEmail);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    try {
                        if (strPassword != null) {
                            editTextPassword.setText(strPassword);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //Facebook login
        callbackManager.onActivityResult(requestCode, responseCode, intent);
        super.onActivityResult(requestCode, responseCode, intent);

        //Google login
        if (requestCode == REQ_CODE){
            GoogleSignInResult result  = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleResult(result);
        }
    }
}