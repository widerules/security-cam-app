package com.chilerocks.securitycamapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class SecurityCamAppActivity extends Activity {

	protected String _path;
	ImageView imageview;
	Settings settings;
	private PhotoMailUtilities mPhotoMailUtilities;
	
	public static final String PREFS_NAME = "pref.dat";

	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	private OnClickListener buttonlistener = new OnClickListener() {
		public void onClick(View v) {
			if (mPhotoMailUtilities.mIsPictureBeingTaken == false)
			{
				mPhotoMailUtilities.takephoto();
			}
		}
	};
	private OnClickListener sendlistener = new OnClickListener() {
		public void onClick(View v) {
			mPhotoMailUtilities.sendphoto();
		}
	};
	private OnClickListener configlistener = new OnClickListener() {
		public void onClick(View v) {
			/* go to settings screen */
			Intent myIntent = new Intent(getApplicationContext(), ConfigActivity.class);
			startActivityForResult(myIntent, 0);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String app_ver = null;
		try {
			app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		setTitle("Cam app version " + app_ver);
		setContentView(R.layout.main);

		mPhotoMailUtilities = new PhotoMailUtilities(this, (ViewGroup) findViewById(R.id.main_layout));
		
		Button takeButton = (Button) findViewById(R.id.take);
		Button sendButton = (Button) findViewById(R.id.send);
		Button configButton = (Button) findViewById(R.id.configbutton);

		takeButton.setOnClickListener(buttonlistener);
		sendButton.setOnClickListener(sendlistener);
		configButton.setOnClickListener(configlistener);
	}
}