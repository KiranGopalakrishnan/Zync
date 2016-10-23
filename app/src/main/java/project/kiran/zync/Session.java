package project.kiran.zync;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Kiran on 27-03-2016.
 */
public class Session {

    private SharedPreferences prefs;

    public Session(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setUserId(String usename) {
        prefs.edit().putString("userId", usename).commit();
    }
    public void setDeviceId(String device) {
        prefs.edit().putString("deviceId", device).commit();
    }

    public void setLatitude(String usename) {
        prefs.edit().putString("latitude", usename).commit();
    }
    public void setLongitude(String usename) {
        prefs.edit().putString("longitude", usename).commit();
    }


    public String getLongitude() {
        String usename = prefs.getString("longitude","");
        return usename;
    }

    public String getUserId() {
        String usename = prefs.getString("userId","");
        return usename;
    }
    public String getDeviceId() {
        String device = prefs.getString("deviceId","");
        return device;
    }
    public String setLogin() {
        prefs.edit().putString("loggedIn", "true").commit();
        return "true";
    }
    public boolean checkLogin(Context cxt) {
        if(prefs.contains("loggedIn")){
            return true;
        }else{

            Intent i = new Intent(cxt, login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            cxt.startActivity(i);
            return false;
        }
    }
    public String getLatitude() {
        String usename = prefs.getString("latitude","");
        return usename;
    }
}