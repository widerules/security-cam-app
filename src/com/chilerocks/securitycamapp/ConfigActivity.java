package com.chilerocks.securitycamapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigActivity extends Activity {
	Settings settings;

	private OnClickListener buttonlistener = new OnClickListener() {
		public void onClick(View v) {

			if (v.getId() == R.id.buttonSave) {
				/* saving fields */
				settings.setUser(((TextView) (findViewById(R.id.user_text_input)))
						.getText().toString());
				settings.setPass(((TextView) (findViewById(R.id.password_text_input)))
						.getText().toString());
				settings.setFrom(((TextView) (findViewById(R.id.from_text_input)))
						.getText().toString());
				settings.setTo(((TextView) (findViewById(R.id.to_text_input)))
						.getText().toString());
				settings.setSmtp(((TextView) (findViewById(R.id.smtp_text_input)))
						.getText().toString());
				settings.setPort(((TextView) (findViewById(R.id.port_text_input)))
						.getText().toString());
				settings.setCodeword(((TextView) (findViewById(R.id.codeword_text_input)))
						.getText().toString());

				int hour = -1;
				int minute = -1;

				try {
					hour = Integer
							.parseInt(((TextView) (findViewById(R.id.hour_input)))
									.getText().toString());
					minute = Integer
							.parseInt(((TextView) (findViewById(R.id.minute_input)))
									.getText().toString());
				} catch (Exception e) {
					// doing nothing here because this is done on the else part
					// in the next if statement
				}

				if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60) {
					Log.d("app", "hour: " + hour + " minute: " + minute);
					settings.setHour(hour);
					settings.setMinute(minute);
					ScheduleReceiver.setRecurringAlarm(getApplicationContext(),
							hour, minute);
				} else {
					Toast.makeText(ConfigActivity.this, "Time format error.",
							Toast.LENGTH_SHORT).show();
				}

				// after the save is complete finish this activity and return to
				// previous
				finish();

			}
			if (v.getId() == R.id.ButtonCheck) {
				Log.d("image", "testing");
				/* test mail */
				Mail m = new Mail(settings.getUser(), settings.getPass());
				m.set_host(settings.getSmtp());
				m.set_port(settings.getPort());
				m.set_sport(settings.getPort());
				String[] toArr = { settings.getTo() };
				m.set_to(toArr);
				m.set_from(settings.getFrom());
				m.set_subject("Security Cam App delivery Test");
				m.setBody("Testing Message");
				Log.d("image", m.get_sport() + " " + m.get_sport() + " " + m.get_host());
				
				try {
					if (m.send()) {
						Toast.makeText(ConfigActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(ConfigActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show();
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		settings = new Settings(getApplicationContext());

		if (settings.hasSettings()) {
			/* restoring fields from stored settings */
			((TextView) (findViewById(R.id.user_text_input))).setText(settings.getUser());
			((TextView) (findViewById(R.id.password_text_input))).setText(settings.getPass());
			((TextView) (findViewById(R.id.from_text_input))).setText(settings.getFrom());
			((TextView) (findViewById(R.id.to_text_input))).setText(settings.getTo());
			((TextView) (findViewById(R.id.smtp_text_input))).setText(settings.getSmtp());
			((TextView) (findViewById(R.id.port_text_input))).setText(settings.getPort());
			((TextView) (findViewById(R.id.codeword_text_input))).setText(settings.getCodeword());
			((TextView) (findViewById(R.id.minute_input))).setText (settings.getMinute()+"" );
			((TextView) (findViewById(R.id.hour_input))).setText(settings.getHour()+"");

		}

		Button save = (Button) findViewById(R.id.buttonSave);
		Button check = (Button) findViewById(R.id.ButtonCheck);

		save.setOnClickListener(buttonlistener);
		check.setOnClickListener(buttonlistener);

	}
}
