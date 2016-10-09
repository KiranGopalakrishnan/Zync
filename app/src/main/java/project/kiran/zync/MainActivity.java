package project.kiran.zync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), contactSyncer.class));
        ActionBar ab =  getSupportActionBar();
        ab.setElevation(0);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#bd2616")));
        Cursor cursor =  managedQuery(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int contactCount = cursor.getCount();
        TextView numberOfContacts = (TextView) findViewById(R.id.numberOfContacts);
        overrideFonts(getApplicationContext(),numberOfContacts,"Regular");
        numberOfContacts.setText(String.valueOf(contactCount));
        TextView contactText = (TextView) findViewById(R.id.numberText);
        overrideFonts(getApplicationContext(),contactText,"Thin");
    }
    private class mainCount extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
           /* String url= "http://anoudis.com/users/core/register.php";
            serverRequest lp = new serverRequest();
            String cname,cnumber,id;
            cname=params[0].get(i);
            cnumber=params[1];
            id=params[3];
            HashMap<String, String> data = new HashMap<String,String>();
            data.put("email",username);
            data.put("password", password);
            data.put("sex",sex);
            data.put("dob", dob);
            data.put("accountType", "U");
            data.put("firstname",firstname);
            data.put("lastname", lastname);

            String result = lp.sendRequest(url, data);

            //Log.i("Response  : ",result.toString());
            return  result.toString();*/
            return null;
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
