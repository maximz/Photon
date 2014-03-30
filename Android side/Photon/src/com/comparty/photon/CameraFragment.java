package com.comparty.photon;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.comparty.photon.camera.CameraPreview;

public class CameraFragment extends Fragment {

	private Camera mCamera;
	private CameraPreview mCameraPreview;

	TextView instructions;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camera, container,
				false);

		instructions = (TextView) rootView.findViewById(R.id.instructions);
		instructions.setVisibility(View.VISIBLE);

		mCamera = getCameraInstance();
		mCameraPreview = new CameraPreview(getActivity(), mCamera);
		FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview);

		ImageButton captureButton = (ImageButton) rootView.findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCamera.takePicture(null, null, mPicture);
			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mCamera != null)
			mCamera.startPreview();
	}

	/**
	 * Helper method to access the camera returns null if it cannot get the
	 * camera or does not exist
	 * 
	 * @return
	 */
	private Camera getCameraInstance() {
		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception e) {
			// cannot get camera or does not exist
		}
		return camera;
	}

	PictureCallback mPicture = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				instructions.setVisibility(View.GONE);
				
				//start spritz
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.alpha_in, R.animator.nochange);
				ft.add(R.id.container, new SpritzFragment());
				ft.commit();

				Bitmap bmp = BitmapFactory.decodeByteArray(data , 0, data.length);

				Matrix matrix = new Matrix();
				matrix.postRotate(90);
				bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
				byte[] rotatedData  = bos.toByteArray();

				ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");

				MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
				multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
				multipartEntity.addPart("image", bab);

				String testUrl = "http://httpbin.org/post";
				String serverUrl = "http://54.186.196.147:3000/web";

				ImageHttpRequest request = new ImageHttpRequest(multipartEntity);
				request.execute(serverUrl);

				bmp.recycle();
				System.gc();

				
			} catch(Exception e) {}
		}
	};

	private static File getOutputMediaFile() {
		File mediaStorageDir = new File(
				Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"Photon");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("Photon", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
		.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
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
