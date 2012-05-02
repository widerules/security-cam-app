package com.chilerocks.securitycamapp;

import com.chilerocks.securitycamapp.PhotoMailUtilities.OnEmailSentListener;
import com.chilerocks.securitycamapp.PhotoMailUtilities.OnPhotoTakenListener;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.widget.LinearLayout;

public class PictureTakerSenderActivity extends Activity {
	
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
		}
	}
	
	public static void releaseWakelock()
	{
		if (mWakeLock != null)
		{
			mWakeLock.release();
			mWakeLock = null;
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
				PictureTakerSenderActivity.this.finish();
				releaseWakelock();
			}
			
			@Override
			public void emailError() {
				PictureTakerSenderActivity.this.finish();
				releaseWakelock();
			}
		});
		
		mPhotoMailUtilities.registerPhotoListener(new OnPhotoTakenListener() {
			
			@Override
			public void photoSuccess() {
				mPhotoMailUtilities.sendphoto();
			}
			
			@Override
			public void photoError() {
				PictureTakerSenderActivity.this.finish();
				releaseWakelock();
			}
		});
		
		mPhotoMailUtilities.takephoto();
	}
}
