package com.comparty.photon;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.comparty.photon.camera.CameraPreview;

public class CameraFragment extends Fragment {

	private Camera mCamera;
	private CameraPreview mCameraPreview;

	TextView instructions;

	SpritzFragment spritz;

	View rootView;

	long startTime;

	ImageHttpRequest request;

	ImageView picView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_camera, container,
				false);

		instructions = (TextView) rootView.findViewById(R.id.instructions);
		instructions.setVisibility(View.VISIBLE);

		mCamera = getCameraInstance();

		picView = (ImageView) rootView.findViewById(R.id.pic);

		Parameters p = mCamera.getParameters();
		p.setFlashMode(Parameters.FLASH_MODE_TORCH);
		mCamera.setParameters(p);
		mCameraPreview = new CameraPreview(getActivity(), mCamera);
		FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
		preview.addView(mCameraPreview);

		ImageButton captureButton = (ImageButton) rootView.findViewById(R.id.button_capture);
		captureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					mCamera.autoFocus(new AutoFocusCallback() {
						public void onAutoFocus(boolean success, Camera camera) {
							if (success)
								camera.takePicture(null, null, mPicture);
						}
					});
				} catch(Exception e) {}
			}
		});

		return rootView;
	}

	@Override 
	public void onPause() {
		super.onPause();
		if (mCamera != null){
			mCamera.release();        // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void startPreview() {
		if (request != null && !request.isCancelled())
			request.cancel(true);

		if ((mCamera = getCameraInstance()) != null) {
			picView.setVisibility(View.GONE);

			Parameters p = mCamera.getParameters();
			p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(p);
			mCameraPreview = new CameraPreview(getActivity(), mCamera);
			FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
			preview.addView(mCameraPreview);
		}
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
				startTime = System.currentTimeMillis();

				//				Parameters p = mCamera.getParameters();
				//				p.setFlashMode(Parameters.FLASH_MODE_OFF);
				//				mCamera.setParameters(p);



				//mCamera.stopPreview();
				//mCamera.release();

				instructions.setVisibility(View.GONE);

				spritz = new SpritzFragment();
				spritz.setCameraFragment(CameraFragment.this);

				//start spritz
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.setCustomAnimations(R.animator.alpha_in, R.animator.nochange);
				ft.add(R.id.container, spritz);
				ft.commit();

				String testUrl = "http://httpbin.org/post";
				String serverUrl = "http://54.186.196.147:3000/web";
				String fasterUrl = "http://ec2-54-186-169-91.us-west-2.compute.amazonaws.com:3000/web";
				String backupUrl = "http://ec2-54-186-240-101.us-west-2.compute.amazonaws.com:3000/web";

				request = new ImageHttpRequest(data);
				request.execute(fasterUrl);



			} catch(Exception e) {}
		}
	};

	private class ImageHttpRequest extends AsyncTask<String, String, String>{

		// One of the types below
		private int type;

		// The status of the response
		private int status;

		private byte[] data;

		// params may be null, but probably shouldn't be
		public ImageHttpRequest(byte[] data) {
			this.data = data;
		}

		@Override
		protected String doInBackground(String... uri) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request;

			Bitmap bmpprelim = BitmapFactory.decodeByteArray(data , 0, data.length);

			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			final Bitmap bmp = Bitmap.createBitmap(bmpprelim, 0, 0, bmpprelim.getWidth(), bmpprelim.getHeight(), matrix, true);

			picView.post(new Runnable(){ public void run(){ 
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int width = size.x;
				int height = size.y;
				
				Bitmap scaled = Bitmap.createScaledBitmap(bmp, width, height, true);
				
				picView.setImageBitmap(scaled);
				picView.setVisibility(View.VISIBLE);


				FrameLayout preview = (FrameLayout) rootView.findViewById(R.id.camera_preview);
				preview.removeAllViews();

			}
			});

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			byte[] rotatedData  = bos.toByteArray();

			ByteArrayBody bab = new ByteArrayBody(data, "forest.jpg");

			MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
			multipartEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipartEntity.addPart("image", bab);
			
			long beforeServer = System.currentTimeMillis();
			Log.d("time to server call:"," "+(beforeServer-startTime));

			Log.d("HttpRequest", "started");
			//testing
			//return null;
			try{

				request = new HttpPost(uri[0]); 
				request.setEntity(multipartEntity.build());

				HttpResponse response = httpclient.execute(request);
				HttpEntity entity = response.getEntity();

				Log.d("Status code", response.getStatusLine().getStatusCode()+"");
				Log.d("Status line", response.getStatusLine().getReasonPhrase());

				status = response.getStatusLine().getStatusCode();

				bmp.recycle();
				System.gc();

				if (entity != null) {
					try {
						InputStream instream = entity.getContent();
						StringBuilder sBuild = new StringBuilder();
						try {
							int i;
							while((i=instream.read())!=-1  ) {//&& current < end) {
								sBuild.append((char) i);
							}
						} finally {
							instream.close();
						}

						Log.d("server and total:", " "+(System.currentTimeMillis() - beforeServer) +"\t" + (System.currentTimeMillis() - startTime));
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

			try {
				JSONObject json = new JSONObject(result);
				((MainActivity) getActivity()).currentText = json.getString("text").replaceAll("\n", " ");
				//((MainActivity) getActivity()).currentText = "One two three four five six";
				spritz.showSpritzer();
			} catch(Exception e) {}



		}
	}
}
