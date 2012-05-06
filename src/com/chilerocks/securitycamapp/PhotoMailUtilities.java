package com.chilerocks.securitycamapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.Toast;

public class PhotoMailUtilities {
	
	private Settings mSettings;
	private Context mContext;
	private File mFile;
	private String mCaptura = "capture.jpg";
	private String mAppdir = "SecurityCam";
	public  boolean mIsPictureBeingTaken = false;
	private ViewGroup mMainLayout;
	private SurfaceView mSurfaceView;
	private Camera mCamera;
	
	public OnPhotoTakenListener mOnPhotoTakenListener = null;
	public OnEmailSentListener mOnEmailSentListener = null;
	
	private PictureCallback mJpegCallback = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] _data, Camera _camera) {
			try {
				/* open a file in internal memory */
				File dir = createDirIfNotExists(mAppdir);
				mFile = new File(dir, mCaptura);
				if (mFile.exists())
					mFile.delete(); // Delete any previous recording
				try {
					mFile.createNewFile(); // Create the new file
				}

				catch (IOException e) {
					Log.e("file", "Failed to create " + mFile.toString());
				}
				Log.d("file1", mFile.getPath());
				FileOutputStream writer = new FileOutputStream(mFile);
				writer.write(_data);
				writer.flush();
				writer.close();
				Toast.makeText(mContext, "Picture successfully taken.", Toast.LENGTH_LONG).show();
				Log.d("image", "onPictureTaken - wrote bytes: " + _data.length);
			} catch (FileNotFoundException e) {
				Log.d("image", "filenotfound");
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("image", "ioexception");
				e.printStackTrace();
			}
			Log.d("image", "onPictureTaken - jpeg");
			
			_camera.release();
			
			mMainLayout.removeView(mSurfaceView);
			mSurfaceView = null;
			mIsPictureBeingTaken = false;
			if (mOnPhotoTakenListener != null)
				mOnPhotoTakenListener.photoSuccess();
		}
	};
	
	public interface OnPhotoTakenListener
	{
		void photoSuccess();
		void photoError();
	}
	
	public interface OnEmailSentListener
	{
		void emailSuccess();
		void emailError();
	}
		
	public void registerEmailListener(OnEmailSentListener onEmailSentListener)
	{
		 mOnEmailSentListener = onEmailSentListener;
	}
	
	public void registerPhotoListener(OnPhotoTakenListener onPhotoTakenListener)
	{
		mOnPhotoTakenListener = onPhotoTakenListener;
	}
	
	private File createDirIfNotExists(String path) {

		File dir = new File(Environment.getExternalStorageDirectory(), path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating Image folder");
				return null;
			}
		}
		return dir;
	}
	
	public PhotoMailUtilities(Context context, ViewGroup mainLayout)
	{
		mSettings = new Settings(context);
		mContext = context;
		mMainLayout = mainLayout;
	}
	
	/* take a photo and store in internal memory */
	public void takephoto() {
		mIsPictureBeingTaken = true;

		/* Just to be sure we only ever have at most on SurfaceView added to the main layout */
		if (mSurfaceView != null)
		{
			mMainLayout.removeView(mSurfaceView);
		}
		
		mSurfaceView = new SurfaceView(mContext);
		mMainLayout.addView(mSurfaceView);
		
		mSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
					int height) {
				
				if (width == 0 || height == 0 )
					return;
				
				mCamera = Camera.open();
				
				try {
					mCamera.setPreviewDisplay(holder);
				} catch (IOException e) {
					e.printStackTrace();
					mMainLayout.removeView(mSurfaceView);
					mSurfaceView = null;
					mIsPictureBeingTaken = false;
					if (mOnPhotoTakenListener != null)
						mOnPhotoTakenListener.photoError();
					return;
				}

				Camera.Parameters p = mCamera.getParameters();
				int imageWidth=-1, imageHeight=-1;
						
				p.setJpegQuality(100);// a value between 1 and 100
				p.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
				for (Size s : p.getSupportedPictureSizes())
				{
					if (s.width > imageWidth)
					{
						imageWidth = s.width;
						imageHeight = s.height;
					}
				}
				p.setPictureSize(imageWidth, imageHeight);
				mCamera.setParameters(p);
				mCamera.startPreview();
						
				/* Wait for the camera hardware initialize, otherwise we can just get a black image. */
				Handler myHandler = new Handler();
				myHandler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mCamera.takePicture(null, null, mJpegCallback);
					}
				}, 1000);
				
				/* Camera is released in jpegCallback */
			}
		});
		
		Toast.makeText(mContext, "Trying to take a picture. Please wait.", Toast.LENGTH_LONG).show();
	}
	
	private String getBatteryInfoString()
	{
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
	    Intent batteryInfo = mContext.registerReceiver(null, filter);
	    
	    StringBuilder out = new StringBuilder();
	    
	    int batteryLevel = batteryInfo.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int batteryScale = batteryInfo.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	    int phonePlugged = batteryInfo.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	    
	    if (phonePlugged == BatteryManager.BATTERY_PLUGGED_AC)
	    {
	    	out.append("Phone is plugged to the power source (AC).");
	    }
	    else
	    if (phonePlugged == BatteryManager.BATTERY_PLUGGED_USB)
	    {
	    	out.append("Phone is plugged to the power source (USB).");
	    }
	    else
	    {
	    	out.append("Phone is discharging.");
	    }
	    
	    out.append("\n");
	    out.append("Battery level: " + (int)(batteryLevel / (float)batteryScale * 100) + "%");
	    
	    return out.toString();
	}
	
	
	public void sendphoto() {
		Toast.makeText(mContext, "Trying to send an email.", Toast.LENGTH_LONG).show();
		
		mSettings = new Settings(mContext);

		/* set mail parameters */
		Mail m = new Mail(mSettings.getUser(), mSettings.getPass());
		m.set_host(mSettings.getSmtp());
		m.set_port(mSettings.getPort());
		m.set_sport(mSettings.getPort());
		String[] toArr = { mSettings.getTo() };
		m.set_to(toArr);
		m.set_from(mSettings.getFrom());
		m.set_subject("Security Cam App image");
		m.setBody("image \n" + getBatteryInfoString());
		/* attaching image */
		try {

			// re-initialize file if it's null
			if(mFile == null){
				File dir = new File(Environment.getExternalStorageDirectory(), mAppdir);
				mFile = new File(dir, mCaptura);
			}
			
			Log.d("file", mFile.getAbsolutePath());
			m.addAttachment(mFile.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
		/* send mail in background, can take a while */
		SendEmail task = new SendEmail();
		task.execute(new Mail[] { m });
	}
	
	
	private class SendEmail extends AsyncTask<Mail, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			Log.d("image", "sending");
		}

		@Override
		protected Boolean doInBackground(Mail... mails) {
			try {
				return mails[0].send();
			} catch (Exception e) {
				// you cannot call Toast.makeText in a background thread (this is handled in onPostExecute)
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (mOnEmailSentListener != null)
					mOnEmailSentListener.emailSuccess();
				Toast.makeText(mContext, "Email was sent successfully.", Toast.LENGTH_LONG).show();
			} else {
				if (mOnEmailSentListener != null)
					mOnEmailSentListener.emailError();
				Toast.makeText(mContext, "Email was not sent.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
