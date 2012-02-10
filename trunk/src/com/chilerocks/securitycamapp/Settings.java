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
	
	private final SharedPreferences settings;
    
	/**
	 * @param act The context from which to pick SharedPreferences
	 */
	public Settings (Context act) {
		 settings = act.getSharedPreferences(SecurityCamAppActivity.PREFS_NAME, Context.MODE_PRIVATE);
	}
	public void setSmtp(String smtp) {
		Editor editor = settings.edit();
		editor.putString(SMTP_KEY,smtp);
		editor.commit();
	}
 
	/**
	 * @return the password stored in prefs
	 */
	public String getSmtp() {
		return settings.getString(SMTP_KEY,"");
	}
	
	public void setPort(String port) {
		Editor editor = settings.edit();
		editor.putString(PORT_KEY,port);
		editor.commit();
	}
 
	/**
	 * @return the password stored in prefs
	 */
	public String getPort() {
		return settings.getString(PORT_KEY,"");
	}
	
	public void setUser(String user) {
		Editor editor = settings.edit();
		editor.putString(USER_KEY,user);
		editor.commit();
	}
 
	/**
	 * @return the password stored in prefs
	 */
	public String getUser() {
		return settings.getString(USER_KEY,"");
	}
	
	public void setPass(String pass) {
		Editor editor = settings.edit();
		editor.putString(PASSWORD_KEY,pass);
		editor.commit();
	}
 
	/**
	 * @return the password stored in prefs
	 */
	public String getPass() {
		return settings.getString(PASSWORD_KEY,"");
	}
	
	public void setFrom(String from) {
		Editor editor = settings.edit();
		editor.putString(FROM_KEY,from);
		editor.commit();
	}
 
	/**
	 * @return the password stored in prefs
	 */
	public String getFrom() {
		return settings.getString(FROM_KEY,"");
	}
	public void setTo(String to) {
		Editor editor = settings.edit();
		editor.putString(TO_KEY,to);
		editor.commit();
	}
 
	/**
	 * @return the password stored in prefs
	 */
	public String getTo() {
		return settings.getString(TO_KEY,"");
	}
	
	
	public boolean hasSettings() {
		return (!settings.getString(USER_KEY, "").equals(""));
	}
	
	}
 
