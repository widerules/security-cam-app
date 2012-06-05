package com.chilerocks.securitycamapp;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import com.chilerocks.securitycamapp.util.*;



public class SecurityCamApplication extends Application {

	
	public final static String APP_LOG_DIR_NAME = "SecurityCam"; 
	
	/**
	 * Handle Uncaught Exceptions
	 * @author Hussein
	 *
	 */
	public class AppExceptionHandler implements UncaughtExceptionHandler
	{
		private UncaughtExceptionHandler exHandler;
		
		public AppExceptionHandler() {
			this.exHandler = Thread.getDefaultUncaughtExceptionHandler();
		}

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// TODO Auto-generated method stub
			Log.logCaughtException(ex);
			exHandler.uncaughtException(thread, ex);
		}		
	}

	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.d("app", "Application Created");
		Thread.setDefaultUncaughtExceptionHandler(new AppExceptionHandler());
	}
	

	
}
