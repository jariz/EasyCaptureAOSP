package pro.jariz.EasyCapture;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


public class CaptureFragment extends Fragment {
		
		Handlers.CaptureType CapType;
		
		public Handlers.CaptureType getCapType() {
			return CapType;
		}
		
		@Override
		public void setArguments(Bundle bundle) {
			CapType = Handlers.CaptureType.values()[bundle.getInt("ctype")];
		}
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View v = inflater.inflate(R.layout.fragment_capture, container, false);
	        //GridView grid = (GridView)v.findViewById(R.id.gridview);
	        if(Handlers.CheckNetwork(container.getContext())) new itemLoader().execute(container.getContext());
	        //grid.setAdapter(new CaptureAdapter(CapType));
	        return v;
	    }
		
		private class itemLoader extends AsyncTask<Object, Object, Object> {
			
		    @Override
			protected Object[] doInBackground(Object... args) {
		    	JSONObject ob;
		    	final Activity ui = (Activity)args[0];
				try {
					String uri = "http://api.easycaptu.re/captures/"+CapType.toString()+"/"+Handlers.Settings.getString("userkey", "");
					Log.d("EC.itemLoader", uri);
					String resp = Handlers.downloadUrl(uri, ui);
					Log.d("EC.itemLoader", resp);
					ob = new JSONObject(resp);
					if(ob.optBoolean("error")) if(ob.getBoolean("error")) {
						Handlers.QuickErrorUI(ob.getString("message"), R.string.error, ui);	
					}
					return new Object[] { ob, args[0] };
				} catch (final Exception e) {
					Log.e("EC.itemLoader", "Failed to parse server response");
					e.printStackTrace();
					Handlers.QuickErrorUI(e.getMessage(), R.string.error, ui);	
					return new Object[] { new JSONArray(), ui };
				}
		    }
		    
		    @Override
		    protected void onPostExecute(Object result) {
		    	Object[] args = (Object[])result;
		    	Activity v = (Activity)args[1];
		    	v.findViewById(R.id.progbar).setVisibility(ViewGroup.GONE);
				v.findViewById(R.id.gridview).setVisibility(ViewGroup.VISIBLE);
		    	GridView grid = (GridView)v.findViewById(R.id.gridview);
		    	grid.setAdapter(new CaptureAdapter((JSONObject)args[0], CapType, v));
		   }
		}
		
		
	}
