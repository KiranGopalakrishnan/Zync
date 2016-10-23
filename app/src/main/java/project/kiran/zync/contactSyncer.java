package project.kiran.zync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class contactSyncer extends Service {
    private boolean runningSync =  false ;
    long lastTimeofCall = 0L;
    long lastTimeofUpdate = 0L;
    long threshold_time = 10000;
    BroadcastReceiver mReceiver;
    Session session;
    String userId,UUID;
    public contactSyncer() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        session = new Session(getApplicationContext());
        userId = session.getUserId();
        UUID = session.getDeviceId();
        getContentResolver()
                .registerContentObserver(
                        ContactsContract.Contacts.CONTENT_URI, true,
                        new contactChangeObserver());
        mReceiver = new  BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                    if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                        dataWriter dw = new dataWriter();
                        dw.setFile(UUID + ".json");
                        String fileName = dw.getFile(getApplicationContext());
                        File file = new File(fileName);
                        if(file.exists()){
                            Log.d("Network", "Yay internet :(");
                            new sendContacts().execute(new File(dw.getFile(getApplicationContext())));
                        }
                    } else if (networkInfo != null && networkInfo.getDetailedState() == NetworkInfo.DetailedState.DISCONNECTED) {
                        Log.d("Network", "No internet :(");
                    }
                }
            }
        };
        this.registerReceiver(mReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    class contactChangeObserver extends ContentObserver {
        public contactChangeObserver() {
            super(null);
        }
        @Override
        public void onChange(boolean selfChange) {

            if(runningSync==false) {
                super.onChange(selfChange);
                runningSync = true;
                Log.d("BLA", "~~~~~~" + selfChange);
                String previousId = "0";

                Map<String, String> data = new HashMap<>();
                JSONArray ja=new JSONArray();
                Cursor cursor = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
                dataWriter dw = new dataWriter();
                while (cursor.moveToNext()) {
                    data.put("name",cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                    data.put("number",cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    JSONObject jo = dw.formatData(data,"CONTACT");
                    ja.put(jo);
                    //Log.d("BLA",dw.getData(getApplicationContext()));
                }
                cursor.close();

                dw.setFile(session.getDeviceId()+".json");
                dw.saveData(getApplicationContext(), String.valueOf(ja));

                Log.d("BLA", dw.getData(getApplicationContext()));
                if(isNetworkAvailable()) {
                    new sendContacts().execute(new File(dw.getFile(getApplicationContext())));
                }
            }
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }
    private class sendContacts extends AsyncTask<File,Void,String>{
        File file;
        @Override
        protected String doInBackground(File... params) {
            String result = "";
            try {
                file = params[0];
                serverRequest sr = new serverRequest("http://levels8.com/zync/handleContacts.php");
                sr.addFilePart("contactList",file);
                result =  sr.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("BLA",result);
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            dataWriter dw = new dataWriter();
            dw.deleteFile(file);
            runningSync = false;
        }

    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setRunningState(Boolean state){
        runningSync = state;
        Log.d("serbla",String.valueOf(runningSync));
        final int secs = 15;
        new CountDownTimer((secs +1) * 1000, 1000) // Wait 5 secs, tick every 1 sec
        {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public final void onFinish()
            {
                runningSync = false;
            }
        }.start();
    }
}
