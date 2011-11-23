package com.schneeloch.tcoredirect;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class ForwardAsyncTask extends AsyncTask<Object, Integer, Object>
{
	private String exceptionMessage;
	private ProgressDialog progressDialog;
	private final Main context;
	private String newUrl;
	private final Uri oldUri;
	
	public ForwardAsyncTask(Main context, Uri uri)
	{
		this.context = context;
		this.oldUri = uri;
	}
	
	
	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Opening " + oldUri);
		progressDialog.show();
	}
	
	@Override
	protected Object doInBackground(Object... arg0) {
		doStuff();
		return null;
	}
	
	
	protected Object doStuff()
	{
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
						newUrl = header.getValue();
					}
					else
					{
						report("Invalid t.co link: " + uri.toString());
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
		
		return null;
	}
	
	private void report(String message)
	{
		exceptionMessage = message;
	}
	
	private void report(String message, Throwable e) {
		report(message + ": " + e.toString());
	}


	@Override
	protected void onPostExecute(Object result) {
		progressDialog.dismiss();
		
		if (newUrl != null)
		{
			Intent newIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newUrl));
			context.startActivity(newIntent);
		}
		else if (exceptionMessage != null)
		{
			Toast.makeText(context, exceptionMessage, Toast.LENGTH_LONG).show();
		}
		
		context.finish();
	}
}
