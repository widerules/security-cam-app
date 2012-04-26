package com.chilerocks.securitycamapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ScheduleReceiver extends BroadcastReceiver {

	Settings settings;
	String captura = "capture.jpg";
	String appdir = "SecurityCam";
	SurfaceView mSurfaceView;
	Camera camera;
	File file;

	private void takephoto(Context ctx) {
		/* Hidden surface because we don't want a preview */
		mSurfaceView = new SurfaceView(ctx);
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

	private void sendphoto(Context ctx) {
		settings = new Settings(ctx);

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
				return false;
			}
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("app", "message");
		takephoto(context);
		sendphoto(context);
		Log.d("app", "routine done");

	}

	public static void cancelRecurringAlarm(Context context) {
		Intent downloader = new Intent(context, ScheduleReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(recurringDownload);
	}

	public static void setRecurringAlarm(Context context, int hour, int minute) {
		Calendar updateTime = Calendar.getInstance();
		// updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
		Log.d("app", updateTime.toString());
		Log.d("app", updateTime.getTime().toLocaleString());

		updateTime.set(Calendar.HOUR_OF_DAY, hour);
		updateTime.set(Calendar.MINUTE, minute);
		
		Log.d("app", updateTime.getTime().toLocaleString());
		
		Intent downloader = new Intent(context, ScheduleReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, recurringDownload);
	}

}