package pro.jariz.EasyCapture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import org.json.*;

import pro.jariz.EasyCapture.Handlers.CaptureType;

public class CaptureAdapter extends BaseAdapter {
	
	public Handlers.CaptureType getCapType() {
		return CapType;
	}
	
	public Handlers.CaptureType CapType;
	public Context context;
	public Activity activity;
	
	final ImageDownloader imageDownloader = new ImageDownloader();
	
	public CaptureAdapter(JSONObject data, CaptureType capType2, Context context) {
		try {
			items = data.getJSONArray("thumbs");
			captures = data.getJSONArray("items");
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		CapType = capType2;
		
		imageDownloader.setMode(ImageDownloader.Mode.CORRECT);
		
		this.context = context;
	}

	JSONArray items = new JSONArray();
	JSONArray captures = new JSONArray();
	
	public int getCount() {
        return items.length();
    }
	
	public String getItem(int pos) {
		try {
			return items.getString(pos);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public long getItemId(int pos) {
		try {
			return items.getString(pos).hashCode();
		} catch (JSONException e) {
			return 0;
		}
	}
	
	public static final int CAP_TYPE = 0;
	public static final int CAP_HASH = 1;
	public static final int CAP_TITLE = 2;
	public static final int CAP_TIME = 3;
	public static final int CAP_HITS = 4;
	public static final int CAP_UNIQUES = 5;
	public static final int CAP_BANDWIDTH = 6;
	public static final int CAP_URL = 7;
	public static final int CAP_TEXT_TYPE = 8;
	public static final int CAP_SOUND_DURATION = 9;
	public static final int CAP_SIZE = 10;
	
	public View getView(final int position, View view, ViewGroup parent) {
		if (view == null) {
            view = new ImageView(parent.getContext());
            view.setBackgroundResource(android.R.color.transparent);
            view.setLayoutParams(new GridView.LayoutParams(-2, -2));
            ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            view.setPadding(0, 10, 0, 10);
            final Context con = parent.getContext();
            view.setOnClickListener(new View.OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				try {
    				Intent i = new Intent(con, CaptureView.class);
    				
    				JSONObject raw = captures.getJSONObject(position);
    				String[] data = new String[11];
    				data[0] = raw.getString("type");
    				data[1] = raw.getString("hash");
    				data[2] = raw.getString("title");
    				data[3] = raw.getString("time");
    				data[4] = Integer.toString(raw.getInt("hits"));
    				data[5] = Integer.toString(raw.getInt("uniques"));
    				data[6] = raw.getString("bandwidth");
    				data[7] = raw.getString("url");
    				data[8] = raw.getString("text.type");
    				data[9] = raw.getString("sound.duration");
    				data[10] = raw.getString("size");
    				
    				i.putExtra("CaptureData", data);
    				
    				con.startActivity(i);
    				} catch(Exception z) { 
    					Handlers.QuickErrorUI("Unable to load capture", R.string.error, (Activity)con);
    					z.printStackTrace();
    				}
    			}
    		});
        }
        
        try {
        	Log.d("EC.CaptureAdapter", "Receiving "+items.getString(position));
			imageDownloader.download(items.getString(position), (ImageView) view);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return view;
    }
}
