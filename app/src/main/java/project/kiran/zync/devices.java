package project.kiran.zync;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class devices extends AppCompatActivity {

    ListView listView;
    List<RowItem> rowItems,rowItems2;

    final ArrayList<String> tempNames = new ArrayList<String>();
    final ArrayList<String> tempPrices = new ArrayList<String>();
    final ArrayList<String> tempIds = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);
        ActionBar ab =  getSupportActionBar();
        ab.setElevation(0);
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#bd2616")));
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#9e190d"));
        }
        new getDevices().execute();

        listView = (ListView) findViewById(R.id.myDevices);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedDevice = tempIds.get(position);
                Log.d("CLIECKED ITEM",clickedDevice);
               Intent i= new Intent(devices.this,syncWithDevice.class);
                i.putExtra("selectedDevice",clickedDevice);
                startActivity(i);
            }
        });

    }
    private class getDevices extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            String url = "http://levels8.com/zync/getDevices.php";
            Session session =  new Session(getApplicationContext());
            String userId =  session.getUserId();
            serverRequest srp;
            //Log.d("SESSIONBLA",userId);
            String result="";
            try {
                srp =  new serverRequest(url);
                srp.addFormField("userId",userId);
                srp.addFormField("use",userId);
                result = srp.finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(String response){
            String result = response;
            Log.d("HAHAHA",result);
            String dName,uuid;
            rowItems = new ArrayList<RowItem>();
            rowItems2 = new ArrayList<RowItem>();
            listView = (ListView) findViewById(R.id.myDevices);
            try {
                JSONArray ja = new JSONArray(response);
                for(int i=0;i<ja.length();i++){
                    JSONObject jo = ja.getJSONObject(i);
                   dName = jo.getString("deviceName");
                    uuid = jo.getString("UUID");
                    tempIds.add(uuid);
                    tempNames.add(dName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String[] arrName=new  String[tempNames.size()];
            arrName=tempNames.toArray(arrName);
           /* String[] arrPrice=new  String[tempCount.size()];
            arrPrice=tempPrices.toArray(arrPrice);*/
            String[] arrId=new String[tempIds.size()];
            arrId=tempIds.toArray(arrId);
            for (int j = 0; j < arrName.length; j++) {
                RowItem item = new RowItem(arrName[j],arrId[j]);
                rowItems.add(item);
            }
            // Log.i("hehe",arrName[0].toString());
            customListViewAdapter adapter = new customListViewAdapter(devices.this,
                    R.layout.list_item, rowItems);
            listView.setAdapter(adapter);
        }
    }
}
