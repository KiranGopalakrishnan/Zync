package project.kiran.zync;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class syncWithDevice extends AppCompatActivity {
contactSyncer cs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_with_device);

        ActionBar ab =  getSupportActionBar();
        ab.setElevation(0);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#bd2616")));

        TextView count = (TextView) findViewById(R.id.syncedNumber);
        TextView syncedText = (TextView) findViewById(R.id.syncedText);

        overrideFonts(getApplicationContext(),syncedText,"Thin");
        overrideFonts(getApplicationContext(),count,"Thin");

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#9e190d"));
        }
        new getContacts().execute();

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
    private  class getContacts extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            String url = "https://anoudis.com/levels8.com/readContacts.php";
            sendPost sp = new sendPost();
            HashMap<String,String> data = new HashMap<String,String>();
            Session session = new Session(getApplicationContext());
            data.put("userId",session.getUserId());
            String result = sp.sendRequest(url,data);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray ja = new JSONArray(result);
                ProgressBar pb = (ProgressBar)findViewById(R.id.syncingBar);
                pb.setMax(ja.length()-1);
                for (int i=0;i<ja.length();i++){
                    JSONObject jo = ja.getJSONObject(i);
                    String mimetype,type,name,number,id;
                    name = jo.getString("name");
                    number = jo.getString("number");

                    Intent serviceIntent = new Intent(getApplicationContext(), contactSyncer.class);
                    serviceIntent.addCategory("set_state_true");
                    startService(serviceIntent);

                    addContact(number,name);
                    setProgressBar(i);
                    //Log.d("BLA",name);
                }

                startService(new Intent(getApplicationContext(), contactSyncer.class));
                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                        syncWithDevice.this);

// Setting Dialog Title
                alertDialog2.setTitle("Syncing Successful");

// Setting Dialog Message
                alertDialog2.setMessage("Your contact list have been updated !");

// Setting Icon to Dialog

// Setting Positive "Yes" Btn
                alertDialog2.setPositiveButton("Go Back",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Intent i = new Intent(syncWithDevice.this,MainActivity.class);
                                startActivity(i);
                            }
                        });

// Showing Alert Dialog
                alertDialog2.show();
                //Log.d("BLA",ja.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        @Override
        protected void onPreExecute() {
            stopService(new Intent(getApplicationContext(), contactSyncer.class));
        }
    }
    public void setProgressBar(int progress){
        ProgressBar pb = (ProgressBar) findViewById(R.id.syncingBar);
        pb.setProgress(progress);

    }
    public void addContact(String number,String name){
        String DisplayName = name;
        String MobileNumber = number;
        String HomeNumber = "";
        String WorkNumber = "";
        String emailID = "";
        String company = "";
        String jobTitle = "";

        ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        //------------------------------------------------------ Names
        if (DisplayName != null) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(
                            ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                            DisplayName).build());
        }

        //------------------------------------------------------ Mobile Number
        if (MobileNumber != null) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        //------------------------------------------------------ Home Numbers
        if (HomeNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, HomeNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        //------------------------------------------------------ Work Numbers
        if (WorkNumber != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, WorkNumber)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Email
        if (emailID != null) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //------------------------------------------------------ Organization
        if (!company.equals("") && !jobTitle.equals("")) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, company)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, jobTitle)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext() , "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

