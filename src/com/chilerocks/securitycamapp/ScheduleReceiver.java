package com.chilerocks.securitycamapp;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.chilerocks.securitycamapp.util.*;

public class ScheduleReceiver extends BroadcastReceiver {
	
	private static final String TAG = "ScheduleReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Alarm Received");
		Intent i = new Intent(context, PictureTakerSenderActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
		
		/* Processor may go to sleep after we return from onReceive() but before activity i
		 * started, let's prevent that. */
		PictureTakerSenderActivity.acquireWakelock(context);
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