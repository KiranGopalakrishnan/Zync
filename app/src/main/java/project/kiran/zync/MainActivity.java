package project.kiran.zync;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
        Session session = new Session(getApplicationContext());
        session.checkLogin(getApplicationContext());
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), contactSyncer.class));
        ActionBar ab =  getSupportActionBar();
        ab.setElevation(0);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#bd2616")));
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#9e190d"));
        }

        Cursor cursor =  managedQuery(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        int contactCount = cursor.getCount();
        TextView numberOfContacts = (TextView) findViewById(R.id.numberOfContacts);
        overrideFonts(getApplicationContext(),numberOfContacts,"Regular");
        numberOfContacts.setText(String.valueOf(contactCount));
        TextView contactText = (TextView) findViewById(R.id.numberText);
        overrideFonts(getApplicationContext(),contactText,"Thin");
        //TextView exportButton = (TextView) findViewById(R.id.exportButton);
        //exportButton.setOnClickListener(exportContacts);


       // overrideFonts(getApplicationContext(),exportButton,"Thin");
        TextView syncButton = (TextView) findViewById(R.id.syncButton);

        syncButton.setOnClickListener(syncBack);

        overrideFonts(getApplicationContext(),syncButton,"Thin");
    }
    View.OnClickListener exportContacts = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    View.OnClickListener syncBack = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
                    MainActivity.this);

// Setting Dialog Title
            alertDialog2.setTitle("Caution");

// Setting Dialog Message
            alertDialog2.setMessage("Contacts will be added to your contact list on this device,Do you want to continue ?");

// Setting Icon to Dialog

// Setting Positive "Yes" Btn
            alertDialog2.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            Intent i = new Intent(MainActivity.this,devices.class);
                            startActivity(i);
                        }
                    });

// Setting Negative "NO" Btn
            alertDialog2.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog
                            dialog.cancel();
                        }
                    });

// Showing Alert Dialog
            alertDialog2.show();
        }
    };
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
