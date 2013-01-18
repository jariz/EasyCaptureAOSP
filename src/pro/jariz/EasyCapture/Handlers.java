package pro.jariz.EasyCapture;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Handlers {
	enum CaptureType { Image, Text, Sound, All };
	enum CapnameType { image, text, sound };
	
	public static void QuickErrorUI(final String msg, final int title, final Activity activity) {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				Handlers.QuickError(msg, title, activity);
			}
		});
	}
	
	static SharedPreferences Settings = null;
	public static SharedPreferences getSettings(Context c) {
		if(Settings == null) Settings = c.getSharedPreferences("easycap", 0);
		return Settings;
	}
	
	// Reads an InputStream and converts it to a String.
		public static String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		    Reader reader = null;
		    reader = new InputStreamReader(stream, "UTF-8");        
		    char[] buffer = new char[len];
		    reader.read(buffer);
		    return new String(buffer);
		}

		public static String downloadUrl(String myurl, Context context) throws IOException {
			InputStream is = null;
		    // Only display the first 500 characters of the retrieved
		    // web page content.
		    int len = 100000;
		        
		    try {
		        URL url = new URL(myurl);
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("GET");
		        conn.setDoInput(true);
		        // Starts the query
		        conn.connect();
		        int response = conn.getResponseCode();
		        Log.d("EC", "responseCode: " + response);
		        is = conn.getInputStream();

		        // Convert the InputStream into a string
		        String contentAsString = readIt(is, len);
		        return contentAsString;
		    }
		    catch(Exception e) {
		    	QuickError(e.getMessage(), context);
		    	return null;
		    } finally {
		        if (is != null) {
		            is.close();
		        } 
		    }
		}
		
		public static void QuickError(String msg, Context con) {
			QuickError(msg, R.string.error, con);
		}
		
		public static void QuickError(String msg, int title, Context con) {
			AlertDialog.Builder builder = new AlertDialog.Builder(con, AlertDialog.THEME_HOLO_LIGHT);
		    builder.setTitle(title)
		    .setIcon(R.drawable.ic_launcher)
		    .setMessage(msg)
		    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
				  @Override
				  public void onClick(DialogInterface dialog, int which) {
				    dialog.dismiss();
				  }
				})
		    .show();
		    
		}
		
		public static boolean CheckNetwork(Context context) { 
			ConnectivityManager connMgr = (ConnectivityManager) 
			context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null) if(!networkInfo.isConnected()) {
			    QuickError("No networkconnection detected", context);
			    return false;
			}
			return true;
		}
}
