package project.kiran.zync;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import static android.R.attr.data;

public class login extends AppCompatActivity implements View.OnClickListener {
GoogleApiClient mGoogleApiClient;
    Context context;
    Activity act;
    WebView loading;
    ImageView signInButton;
    private static final int RC_SIGN_IN = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView txthint = (TextView) findViewById(R.id.txtHint);
        overrideFonts(getApplicationContext(),txthint,"Thin");
        getSupportActionBar().hide();
        signInButton = (ImageView) findViewById(R.id.sign_in_button);
        signInButton.setVisibility(View.INVISIBLE);
        loading = (WebView)findViewById(R.id.loader);
        loading.loadUrl("file:///android_asset/loading.gif");
        loading.setBackgroundColor(Color.TRANSPARENT);
        loading.setVisibility(View.VISIBLE);
        context = getApplicationContext();
        new loadApp().execute();

    }
    View.OnClickListener signInListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    signIn();
                    break;
            }
        }
    };
    GoogleApiClient.OnConnectionFailedListener fListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d("BLA-E","CONNECTION FAILED");
        }
    };
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("BLA", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String email = acct.getEmail();
            final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
            String[] params = {email,deviceId};
            new signIn().execute(params);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
           // updateUI(false);
        }
    }

    @Override
    public void onClick(View v) {

    }

    private class loadApp extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {

            // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
// Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .enableAutoManage(login.this /* FragmentActivity */, fListener /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            return null;
        }
        @Override
        protected void onPostExecute(String result) {

            findViewById(R.id.sign_in_button).setOnClickListener(signInListener);
            loading.setVisibility(View.INVISIBLE);
            signInButton.setVisibility(View.VISIBLE);

        }
    }

    private class signIn extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... params) {
            String url= "https://anoudis.com/levels8.com/signIn.php";
            serverRequest lp = null;
            String result = "";
            try {
                lp = new serverRequest(url);
                String email;
                lp.addFormField("email",params[0]);
                lp.addFormField("device",params[1]);
                result = lp.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("Response  : ",result.toString());
            return  result.toString();
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray ja = new JSONArray(result);
                JSONObject jo = ja.getJSONObject(0);
               // Log.d("BLA--",);
                String userId = jo.get("userId").toString();
                Session session = new Session(getApplicationContext());
                session.setUserId(userId);
                session.setLogin();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
            @Override
        protected void onPreExecute() {

        }

    }
    private void overrideFonts(final Context context, final View v, String Style) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child,Style);
                }
            } else if (v instanceof TextView) {
                String fontStyle = "Roboto-"+Style+".ttf"; //Thin/Medium/Bold/Regular/Light
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(),fontStyle ));
            }
        } catch (Exception e) {
        }
    }

}
