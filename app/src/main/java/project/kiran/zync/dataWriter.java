package project.kiran.zync;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Kiran on 13-09-2016.
*This class helps my projects to save json formatted files onto the external/internal storage without having to recode the entire thing
 */
public class dataWriter {
    static String fileName = "";
    public static void setFile(String DataFileName){
        fileName = DataFileName;
    }///method to set the filename in which the data is to be written --P.S filename with extension
    public static String getFile(Context context){
        return (context.getFilesDir().getPath() + "/" + fileName).toString();
    }///method to get the filename in which the data is to be written --P.S filename with extension
    public static void deleteFile(File file){
        file.delete();
    }
    public static void saveData(Context context, String mJsonResponse) { //method to save data
        try {
            FileWriter fileW = new FileWriter(context.getFilesDir().getPath() + "/" + fileName,false);
            fileW.write(mJsonResponse);
            fileW.flush();
            fileW.close();
        } catch (IOException e) {
            Log.e("TAG", "Error in Writing: " + e.getLocalizedMessage());
        }
    }

    public static String getData(Context context) { // fetch data from a previously written file
        try {
            File f = new File(context.getFilesDir().getPath() + "/" + fileName);
            //check whether file exists
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer);
        } catch (IOException e) {
            Log.e("TAG", "Error in Reading: " + e.getLocalizedMessage());
            return null;
        }
    }
    public static JSONObject formatData(Map<String,String> data, String objName){ //format data and prepare it in order for to be saved into file
        JSONObject jObj = new JSONObject();
        Iterator it = data.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            try {
                jObj.put(pair.getKey().toString(),pair.getValue().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            it.remove(); // avoids a ConcurrentModificationException
        }

        return jObj;
    }
}
