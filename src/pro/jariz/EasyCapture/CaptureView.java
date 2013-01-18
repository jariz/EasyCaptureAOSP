package pro.jariz.EasyCapture;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class CaptureView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_captureview);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		capture = getIntent().getExtras().getStringArray("CaptureData");
		
		TextView title = (TextView)findViewById(R.id.capTitle);
		TextView desc = (TextView)findViewById(R.id.capDesc);
		title.setText(capture[CaptureAdapter.CAP_TITLE]);
		
		String s = String.format(
			"%s ago\n"+
			"%s\n"+
			"%s hits\n"+
			"%s unique hits\n"+
			"%s bandwidth\n"
			, capture[CaptureAdapter.CAP_TIME],
			capture[CaptureAdapter.CAP_SIZE],
			capture[CaptureAdapter.CAP_HITS],
			capture[CaptureAdapter.CAP_UNIQUES],
			capture[CaptureAdapter.CAP_BANDWIDTH]
		);
		
		desc.setText(s);
		
		switch(Handlers.CapnameType.valueOf(capture[CaptureAdapter.CAP_TYPE])) {
			case image:
				findViewById(R.id.imageView1).setVisibility(View.VISIBLE);
				ImageDownloader dl = new ImageDownloader();
				dl.setMode(ImageDownloader.Mode.CORRECT);
				dl.download(capture[CaptureAdapter.CAP_URL], (ImageView)findViewById(R.id.imageView1));
				break;
			case text:
				WebView web = (WebView)findViewById(R.id.webView1);
				web.setVisibility(View.VISIBLE);
				web.loadUrl(capture[CaptureAdapter.CAP_URL]+"?mobile");
				break;
			case sound:
				//TODO
				break;
		}
		
	}
	
	String[] capture;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_captureview, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
