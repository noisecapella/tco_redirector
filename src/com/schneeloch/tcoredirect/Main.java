package com.schneeloch.tcoredirect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;



import com.schneeloch.tcoredirect.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.widget.RemoteViews.ActionException;

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
			report("Problem reading url", null);
		}
	}

	private void report(String message, Throwable e) {
		String exceptionMessage = e != null ? e.toString() : null;
		Toast.makeText(this, message + ": " + exceptionMessage, Toast.LENGTH_LONG).show();
		Log.e("TcoRedirect", exceptionMessage);
	}
}