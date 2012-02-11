package com.chilerocks.securitycamapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings {
 
	private static final String SMTP_KEY = "host";
	private static final String PORT_KEY = "port";
	private static final String USER_KEY = "user";
	private static final String PASSWORD_KEY = "password";
	private static final String FROM_KEY = "from";
	private static final String TO_KEY = "to";
	private static final String CODEWORD_KEY="codeword";
	private final SharedPreferences settings;
    
	public Settings (Context act) {
		 settings = act.getSharedPreferences(SecurityCamAppActivity.PREFS_NAME, Context.MODE_PRIVATE);
	}
	public void setSmtp(String smtp) {
		Editor editor = settings.edit();
		editor.putString(SMTP_KEY,smtp);
		editor.commit();
	}
 
	public String getSmtp() {
		return settings.getString(SMTP_KEY,"");
	}
	
	public void setPort(String port) {
		Editor editor = settings.edit();
		editor.putString(PORT_KEY,port);
		editor.commit();
	}

	public String getPort() {
		return settings.getString(PORT_KEY,"");
	}
	
	public void setUser(String user) {
		Editor editor = settings.edit();
		editor.putString(USER_KEY,user);
		editor.commit();
	}
 
	public String getUser() {
		return settings.getString(USER_KEY,"");
	}
	
	public void setPass(String pass) {
		Editor editor = settings.edit();
		editor.putString(PASSWORD_KEY,pass);
		editor.commit();
	}

	public String getPass() {
		return settings.getString(PASSWORD_KEY,"");
	}
	
	public void setFrom(String from) {
		Editor editor = settings.edit();
		editor.putString(FROM_KEY,from);
		editor.commit();
	}
	
	public String getFrom() {
		return settings.getString(FROM_KEY,"");
	}
	
	public void setTo(String to) {
		Editor editor = settings.edit();
		editor.putString(TO_KEY,to);
		editor.commit();
	}
	
	public String getTo() {
		return settings.getString(TO_KEY,"");
	}
	
	public void setCodeword(String codeword) {
		Editor editor = settings.edit();
		editor.putString(CODEWORD_KEY,codeword);
		editor.commit();
	}

	public String getCodeword() {
		return settings.getString(CODEWORD_KEY,"");
	}
	
	
	public boolean hasSettings() {
		return (!settings.getString(USER_KEY, "").equals(""));
	}
	
}
 
