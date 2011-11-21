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
		
		finish();
	}

	private void handleIntent(Intent intent) {
		if (intent != null && intent.getAction().equals(Intent.ACTION_VIEW))
		{
			Uri oldUri = getIntent().getData();
			try {
				final URI uri = new URI(oldUri.getScheme(), oldUri.getUserInfo(), oldUri.getHost(), 
						oldUri.getPort(), oldUri.getPath(), oldUri.getQuery(), oldUri.getFragment());

				HttpHead head = new HttpHead(uri);
				DefaultHttpClient httpClient = new DefaultHttpClient();
				httpClient.setRedirectHandler(new RedirectHandler() {

					@Override
					public boolean isRedirectRequested(HttpResponse response,
							HttpContext context) {
						Header header = response.getFirstHeader("Location");
						if (header != null)
						{
							String newUrl = header.getValue();

							Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUrl));
							startActivity(newIntent);
						}
						else
						{
							Toast.makeText(Main.this, "Invalid t.co link: " + uri.toString(), Toast.LENGTH_LONG).show();
						}
						return false;
					}

					@Override
					public URI getLocationURI(HttpResponse response, HttpContext context)
					throws ProtocolException {
						return null;
					}
				});
				httpClient.execute(head);

			}
			catch (ClientProtocolException e) {
				report("Problem reading t.co link", e);
			} catch (IOException e) {
				report("Problem reading input", e);
			} catch (URISyntaxException e) {
				report("Problem parsing uri", e);
			}
			catch (Throwable t)
			{
				report("Unknown problem", t);
			}
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