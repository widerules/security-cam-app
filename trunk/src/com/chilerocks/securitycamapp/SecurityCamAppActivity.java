package com.chilerocks.securitycamapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SecurityCamAppActivity extends Activity {

	protected String _path;
	ImageView imageview;
	SurfaceView mSurfaceView;
	Camera camera;
	File file;
	Settings settings;
	String captura = "capture.jpg";
	String appdir = "SecurityCam";
	public static final String PREFS_NAME = "pref.dat";

	public static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

	public static File createDirIfNotExists(String path) {

		File dir = new File(Environment.getExternalStorageDirectory(), path);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e("TravellerLog :: ", "Problem creating Image folder");
				return null;
			}
		}
		return dir;
	}

	/* take a photo and store in internal memory */
	private void takephoto() {
		/* Hidden surface because we don't want a preview */
		mSurfaceView = new SurfaceView(SecurityCamAppActivity.this);
		SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();

		camera = Camera.open();
		try {
			camera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Camera.Parameters p = camera.getParameters();
		p.setJpegQuality(100);// a value between 1 and 100
		p.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
		camera.setParameters(p);
		camera.startPreview();
		camera.takePicture(null, null, jpegCallback);
		/* time to write the file and later send the email */
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		/* always release camera */
		camera.release();
	}

	private void sendphoto() {
		settings = new Settings(getApplicationContext());

		/* set mail parameters */
		Mail m = new Mail(settings.getUser(), settings.getPass());
		m.set_host(settings.getSmtp());
		m.set_port(settings.getPort());
		m.set_sport(settings.getPort());
		String[] toArr = { settings.getTo() };
		m.set_to(toArr);
		m.set_from(settings.getFrom());
		m.set_subject("Security Cam App image");
		m.setBody("image");
		/* attaching image */
		try {

			// re-initialize file if it's null
			if(file == null){
				File dir = new File(Environment.getExternalStorageDirectory(), appdir);
				file = new File(dir, captura);
			}
			
			Log.d("file", file.getAbsolutePath());
			m.addAttachment(file.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
		}
		/* send mail in background, can take a while */
		SendEmail task = new SendEmail();
		task.execute(new Mail[] { m });
	}

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] _data, Camera _camera) {
			try {
				/* open a file in internal memory */
				File dir = createDirIfNotExists(appdir);
				file = new File(dir, captura);
				if (file.exists())
					file.delete(); // Delete any previous recording
				try {
					file.createNewFile(); // Create the new file
				}

				catch (IOException e) {
					Log.e("file", "Failed to create " + file.toString());
				}
				Log.d("file1", file.getPath());
				FileOutputStream writer = new FileOutputStream(file);
				writer.write(_data);
				writer.flush();
				writer.close();
				Log.d("image", "onPictureTaken - wrote bytes: " + _data.length);
			} catch (FileNotFoundException e) {
				Log.d("image", "filenotfound");
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("image", "ioexception");
				e.printStackTrace();
			}
			Log.d("image", "onPictureTaken - jpeg");
		}
	};

	private OnClickListener buttonlistener = new OnClickListener() {
		public void onClick(View v) {
			takephoto();
		}
	};
	private OnClickListener sendlistener = new OnClickListener() {
		public void onClick(View v) {
			sendphoto();
		}
	};
	private OnClickListener configlistener = new OnClickListener() {
		public void onClick(View v) {
			/* go to settings screen */
			Intent myIntent = new Intent(getApplicationContext(), ConfigActivity.class);
			startActivityForResult(myIntent, 0);
		}
	};

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
				Toast.makeText(SecurityCamAppActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(SecurityCamAppActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
			}
		}
	}

	/* sms receiver handler */
	BroadcastReceiver receiver_SMS = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();

			Object messages[] = (Object[]) bundle.get("pdus");
			SmsMessage smsMessage;
			smsMessage = SmsMessage.createFromPdu((byte[]) (messages[messages.length - 1]));
			settings = new Settings(getApplicationContext());
			/* check codeword in message body */
			if (smsMessage.getDisplayMessageBody().contains(settings.getCodeword())){
			Log.d("code",settings.getCodeword());
			receivedMessage();}
			Toast toast = Toast.makeText(context, "Received SMS: " + smsMessage.getDisplayMessageBody(),
					Toast.LENGTH_LONG);
			toast.show();
		}

	};
	private void receivedMessage() {
		takephoto();
		sendphoto();
		Log.d("mail", "received message");
	}

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
		file = null;
		setTitle("Cam app version " + app_ver);
		setContentView(R.layout.main);
		mSurfaceView = new SurfaceView(this);

		Button takeButton = (Button) findViewById(R.id.take);
		Button sendButton = (Button) findViewById(R.id.send);
		Button configButton = (Button) findViewById(R.id.configbutton);

		takeButton.setOnClickListener(buttonlistener);
		sendButton.setOnClickListener(sendlistener);
		configButton.setOnClickListener(configlistener);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// register the BroadcastReceiver for photo take schedule
		IntentFilter filter = new IntentFilter(SMS_RECEIVED);
		registerReceiver(receiver_SMS, filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// unregister the BroadcastReceiver
		unregisterReceiver(receiver_SMS);
	}
}