package com.vishalaksh.personal.flashlight;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ActivityMain extends Activity {

	private final static String LOG_TAG = "FlashLight";

	private Camera mCamera;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_activity_main);
		mCamera = Camera.open();
		flashOn();

	}

	@Override
	protected void onRestart() {

		mCamera = Camera.open();
		flashOn();
		super.onRestart();
	}


	private void processOffClick() {
		Log.d(LOG_TAG, "processOffClick() called");
		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(params);
			mCamera.stopPreview();

		}
	}

	private void flashOn() {
		Log.d(LOG_TAG, "processOnClick() called");
		if (mCamera != null) {
			Parameters params = mCamera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_ON);
			mCamera.setParameters(params);
			mCamera.startPreview();
			registerCameraAutoFocus(mCamera, new Handler());
			processOffClick();

		}
	}



	@Override
	protected void onStop() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
		super.onStop();
	}

	void registerCameraAutoFocus(Camera camera, Handler h) {
		Log.d(LOG_TAG, "going to register for autofocus");
		AutoFocusCallback autoFocusCallback = new AutoFocusCallback();
		autoFocusCallback.setHandler(h, 0);
		camera.autoFocus(autoFocusCallback);
	}

	class AutoFocusCallback implements Camera.AutoFocusCallback {

		private Handler autoFocusHandler;
		private int autoFocusMessage;

		void setHandler(Handler autoFocusHandler, int autoFocusMessage) {
			this.autoFocusHandler = autoFocusHandler;
			this.autoFocusMessage = autoFocusMessage;
		}

		public void onAutoFocus(boolean success, Camera camera) {
			Log.d(LOG_TAG, "camera autofocussed");
			Message message = autoFocusHandler.obtainMessage(autoFocusMessage,
					success);
			autoFocusHandler.sendMessage(message);

		}
	}
}
