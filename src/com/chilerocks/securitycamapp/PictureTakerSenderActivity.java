package com.chilerocks.securitycamapp;

import com.chilerocks.securitycamapp.PhotoMailUtilities.OnEmailSentListener;
import com.chilerocks.securitycamapp.PhotoMailUtilities.OnPhotoTakenListener;
import com.chilerocks.securitycamapp.util.Log;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.LinearLayout;

public class PictureTakerSenderActivity extends Activity {
	
	private static final String TAG = "PictureTakerSenderActivity";
	
	PhotoMailUtilities mPhotoMailUtilities;
	
	private static WakeLock mWakeLock = null;
	
	/* Wakelock keeps the processor running so that the phone doesn't got to sleep when we're taking
	 * a picture or sending an email */
	public static void acquireWakelock(Context context)
	{
		if (mWakeLock == null)
		{
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PictureTakerSenderActivity");
			mWakeLock.acquire();
			Log.d(TAG, "Wake Service");
		}
	}
	
	public static void releaseWakelock()
	{
		if (mWakeLock != null)
		{
			mWakeLock.release();
			mWakeLock = null;
			Log.d(TAG, "Sleep Service");
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		LinearLayout mainLayout = new LinearLayout(this);
		setContentView(mainLayout);
		mPhotoMailUtilities = new PhotoMailUtilities(this, mainLayout);
		
		mPhotoMailUtilities.registerEmailListener(new OnEmailSentListener() {
			
			@Override
			public void emailSuccess() {
				Log.d(TAG, "Mail Sent");
				PictureTakerSenderActivity.this.finish();
				releaseWakelock();
			}
			
			@Override
			public void emailError() {
				Log.e(TAG, "ERROR: SENDING MAIL FAILED");
				PictureTakerSenderActivity.this.finish();
				releaseWakelock();
			}
		});
		
		mPhotoMailUtilities.registerPhotoListener(new OnPhotoTakenListener() {
			
			@Override
			public void photoSuccess() {
				Log.d(TAG, "Photo Taken Successfully");
				mPhotoMailUtilities.sendphoto();				
			}
			
			@Override
			public void photoError() {
				Log.e(TAG, "Taking Photo Failed");
				PictureTakerSenderActivity.this.finish();
				releaseWakelock();
			}
		});
		
		mPhotoMailUtilities.takephoto();
	}
}
