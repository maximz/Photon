package com.comparty.photon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.comparty.photon.camera.CameraPreview;

public class CameraFragment extends Fragment {
	
	private Camera mCamera;
	private CameraPreview mCameraPreview;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_camera, container,
				false);

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
			File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {
				return;
			}
			try {
				Bitmap bmp = BitmapFactory.decodeByteArray(data , 0, data.length);
				
				Matrix matrix = new Matrix();
			    matrix.postRotate(90);
			    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
				
				FileOutputStream fos = new FileOutputStream(pictureFile);
				bmp.compress(Bitmap.CompressFormat.JPEG, 85, fos);
		        fos.flush();
				fos.close();
				bmp.recycle();
				System.gc();
			} catch (FileNotFoundException e) {

			} catch (IOException e) {
			}
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
}
