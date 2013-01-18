package pro.jariz.EasyCapture;

import java.io.*;

import android.app.ActionBar;
import android.net.*;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.app.AlertDialog;
import android.content.*;
import android.os.AsyncTask;
import org.json.*;

import android.util.Log;

public class Login extends FragmentActivity {
	
	boolean CheckNetwork() { 
		ConnectivityManager connMgr = (ConnectivityManager) 
		getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null) if(!networkInfo.isConnected()) {
		    QuickError("No networkconnection detected");
		    
		    return false;
		}
		return true;
	}
	
	void switchLoad() {
		View v = findViewById(R.id.button1);
		if(v.getVisibility() == View.VISIBLE) {
			findViewById(R.id.button1).setVisibility(View.INVISIBLE);
			findViewById(R.id.editText1).setVisibility(View.INVISIBLE);
			findViewById(R.id.editText2).setVisibility(View.INVISIBLE);
			findViewById(R.id.progressBar1).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.button1).setVisibility(View.VISIBLE);
			findViewById(R.id.editText1).setVisibility(View.VISIBLE);
			findViewById(R.id.editText2).setVisibility(View.VISIBLE);
			findViewById(R.id.progressBar1).setVisibility(View.INVISIBLE);
		}
	}
	
	void QuickError(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK);
	    builder.setTitle(R.string.error)
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(Handlers.getSettings(this).getString("userkey", "") == "")
		{
			setContentView(R.layout.activity_login);
		
		final ActionBar actionBar = getActionBar();
		actionBar.setSubtitle(R.string.login_title);
		
		final Button button = (Button) findViewById(R.id.button1);
		final FragmentActivity login = this;
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				switchLoad();
				String usr = ((EditText)findViewById(R.id.editText1)).getText().toString();
				String pwd = ((EditText)findViewById(R.id.editText2)).getText().toString();
				
				if(CheckNetwork()) new doLogin().execute(String.format("http://api.easycaptu.re/login/%s/%s", usr, pwd), login);
			}
		});
		
		} else this.startActivity(new Intent(this, Main.class));
	}
	
	private class doLogin extends AsyncTask<Object, Object, Object> {
		Activity activity;
		
	    @Override
		protected String doInBackground(Object... args) {
	          activity = (Activity) args[1];
	        // params comes from the execute() call: params[0] is the url.
	        try {
	            return Handlers.downloadUrl((String)args[0], activity);
	        } catch (IOException e) {
	            return "{error:true, message:\"Unable to recieve response from server\"}";
	        }
	    }
	    // onPostExecute displays the results of the AsyncTask.
	    @Override
	    protected void onPostExecute(Object result) {
	        try {
	        	Log.d("EC",  (String)result);
				JSONObject ob = new JSONObject((String)result);
				if(ob.getBoolean("error")) {
					QuickError(ob.getString("message"));
				} else {
					Handlers.Settings.edit().putString("userkey", ob.getString("userkey"))
					.apply();
					
					switchLoad();
			    	
					activity.runOnUiThread(new Runnable() {
					    public void run() {
					    	activity.startActivity(new Intent(activity, Main.class));
					}});
				}
			} catch (JSONException e) {
				
				QuickError("Unable to parse content from server: "+e.getMessage());
				switchLoad();
			}
	        
	   }
	}
	
}


