package com.schneeloch.tcoredirect;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
		if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW))
		{
			Uri oldUri = getIntent().getData();
			ForwardAsyncTask forwardAsyncTask = new ForwardAsyncTask(this, oldUri);
			forwardAsyncTask.execute();
		}
		else
		{
			report(getResources().getString(R.string.err_reading_url), null);
		}
	}

	private void report(String message, Throwable e) {
		String exceptionMessage = e != null ? e.toString() : null;
		Toast.makeText(this, message + ": " + exceptionMessage, Toast.LENGTH_LONG).show();
		Log.e("TcoRedirect", exceptionMessage);
	}
}