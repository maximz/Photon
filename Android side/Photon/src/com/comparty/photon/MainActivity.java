package com.comparty.photon;

import java.io.File;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new CameraFragment()).commit();
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

		ImageView image;
		boolean hasImage = false;
		File imageLoc;
		Bitmap bm;

		String folder = "PhotonImages";
		String filename = "image.jpg";

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			image = (ImageView) rootView.findViewById(R.id.image);
			File imagesFolder = new File(Environment.getExternalStorageDirectory(), folder);
			imagesFolder.mkdirs(); // <----
			imageLoc = new File(imagesFolder, filename);
			if (imageLoc.exists()) {
				if (bm != null)
					bm.recycle();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				bm = BitmapFactory.decodeFile(imageLoc.getPath(), options);
				hasImage = true;
				image.setImageBitmap(bm);
			}

			Button b = (Button) rootView.findViewById(R.id.photo_button);
			b.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// clear last bitmap
					if (bm != null) {
						image.setImageResource(android.R.color.transparent);
						bm.recycle();
					}

					// create Intent to take a picture and return control to the calling application
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					File imagesFolder = new File(Environment.getExternalStorageDirectory(), folder);
					imagesFolder.mkdirs(); // <----
					imageLoc = new File(imagesFolder, filename);
					Uri uriSavedImage = Uri.fromFile(imageLoc);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

					// start the image capture Intent
					startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				}
			});

			Button send = (Button) rootView.findViewById(R.id.send_button);
			send.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (!hasImage) {
						Toast.makeText(PlaceholderFragment.this.getActivity(), "Take a pic first", Toast.LENGTH_SHORT).show();
						return;
					}

					// entity call stuff
					try {
						FileBody fileBody = new FileBody(imageLoc); 

						MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
						multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
						multipartEntity.addPart("image", fileBody);

						String testUrl = "http://httpbin.org/post";
						String serverUrl = "http://54.186.196.147:3000/web";

						ImageHttpRequest request = new ImageHttpRequest(multipartEntity);
						request.execute(serverUrl);

					} catch(Exception e) {}
				}
			});

			Button mycam = (Button) rootView.findViewById(R.id.mycam_button);
			mycam.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace(R.id.container, new CameraFragment());
					ft.commit();
				}
			});


			return rootView;
		}

		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public void onResume() {

			super.onResume();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
			if (resultCode == RESULT_OK) {
				if (bm != null)
					bm.recycle();

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				File imagesFolder = new File(Environment.getExternalStorageDirectory(), folder);
				imageLoc = new File(imagesFolder, filename);
				bm = BitmapFactory.decodeFile(imageLoc.getPath(), options);
				hasImage = true;
				image.setImageBitmap(bm);
			}

		}

		private class ImageHttpRequest extends AsyncTask<String, String, String>{
			// Inputs for the data and json parts of the request, like "email" or "password"
			private MultipartEntityBuilder params;

			// One of the types below
			private int type;

			// The status of the response
			private int status;

			// params may be null, but probably shouldn't be
			public ImageHttpRequest(MultipartEntityBuilder params) {
				this.params = params;
			}

			@Override
			protected String doInBackground(String... uri) {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost request;

				Log.d("JSONHttp", "type: "+type);
				try{

					request = new HttpPost(uri[0]); 
					if (params != null) {
						request.setEntity(params.build());
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
								int end = 1000;
								int current = 0;
								while((i=instream.read())!=-1  ) {//&& current < end) {
									sBuild.append((char) i);
									current++;
								}
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
				if (result != null)
					Log.d("Response", result+"");

			}
		}
	}

}
