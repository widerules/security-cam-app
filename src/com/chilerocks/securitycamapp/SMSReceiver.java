package com.chilerocks.securitycamapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SecuritCamApp", "Sms received.");
		Bundle bundle = intent.getExtras();
		Settings settings = new Settings(context);
		
		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage;
		smsMessage = SmsMessage.createFromPdu((byte[]) (messages[messages.length - 1]));
		
		Log.d("SecuritCamApp", "Sms body: " + smsMessage.getDisplayMessageBody());
		/* check codeword in message body */
		if (smsMessage.getDisplayMessageBody().contains(settings.getCodeword())){
			//Intent i = new Intent();
			//i.setComponent(new ComponentName("com.chilerocks.securitycamapp", "com.chilerocks.securitycamapp.PictureTakerSenderActivity"));
			Intent i = new Intent(context, PictureTakerSenderActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
			PictureTakerSenderActivity.acquireWakelock(context);
		}
	}
}
