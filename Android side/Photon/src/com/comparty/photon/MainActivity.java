package com.comparty.photon;

import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
		private Uri fileUri;

		ImageView image;

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			image = (ImageView) rootView.findViewById(R.id.image);

			Button b = (Button) rootView.findViewById(R.id.photo_button);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// create Intent to take a picture and return control to the calling application
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
					imagesFolder.mkdirs(); // <----
					File imageLoc = new File(imagesFolder, "image_001.jpg");
					Uri uriSavedImage = Uri.fromFile(imageLoc);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

					// start the image capture Intent
					startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}
			});

			return rootView;
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_OK) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				File imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
				File imageLoc = new File(imagesFolder, "image_001.jpg");
				Bitmap bm = BitmapFactory.decodeFile(imageLoc.getPath(), options);
				image.setImageBitmap(bm);
			}

		}

		private class JSONHttpRequest extends AsyncTask<String, String, String>{
			// Inputs for the data and json parts of the request, like "email" or "password"
			private JSONObject params;

			// Cookies for protected calls
			private CookieStore cookieStore;

			// One of the types below
			private int type;

			// The status of the response
			private int status;

			// params may be null
			public JSONHttpRequest(JSONObject params) {
				this.params = params;
			}

			@Override
			protected String doInBackground(String... uri) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpRequestBase request;

				Log.d("JSONHttp", "type: "+type);
				try{

					request = new HttpPost(uri[0]); 
					if (params != null) {
						StringEntity se = new StringEntity(params.toString());  
						se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
						((HttpPost) request).setEntity(se);
					}	

					HttpResponse response = httpclient.execute(request);
					HttpEntity entity = response.getEntity();

					Log.d("Status code", response.getStatusLine().getStatusCode()+"");
					Log.d("Status line", response.getStatusLine().getReasonPhrase());

					status = response.getStatusLine().getStatusCode();

					if (entity != null) {
						try {
							InputStream instream = entity.getContent();
							StringBuilder sBuild = new StringBuilder();
							try {
								int i;

								while((i=instream.read())!=-1)
									sBuild.append((char) i);
							} finally {
								instream.close();
							}

							return sBuild.toString();
						} catch (Exception e) { Log.e("ServerUtils","getStringFromEntity: Error making string from entity"); }
						return null;
					}

				}catch (Exception e) {
					Log.d("Broke",e.toString());
				}

				return null;

			} 

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				Log.d("Status", status + "");
				Log.d("Response", result+"");

			}
		}
	}

}
