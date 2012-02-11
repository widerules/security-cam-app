package com.chilerocks.securitycamapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.audiofx.EnvironmentalReverb;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
	SurfaceView  mSurfaceView;
	Camera camera;
	File file;
	Settings settings;
	String captura= "capture.jpg";
	public static final String PREFS_NAME = "pref.dat";

	PictureCallback jpegCallback = new PictureCallback() {
		  public void onPictureTaken(byte[] _data, Camera _camera) {
		try{
			FileOutputStream outStream;

			//file = File.createTempFile(captura, ".jpeg");
			outStream = openFileOutput(captura, SecurityCamAppActivity.MODE_WORLD_READABLE);			
			outStream.write(_data);
			outStream.close();
			Log.d("image", "onPictureTaken - wrote bytes: " + _data.length);
			} catch (FileNotFoundException e) {
				Log.d("image", "filenotfound");
				e.printStackTrace();
			} catch (IOException e) {
				Log.d("image", "ioexception");
				e.printStackTrace();
			} finally {
			}
			Log.d("image", "onPictureTaken - jpeg");
		  }
	};

	private OnClickListener buttonlistener = new OnClickListener() {
		public void onClick(View v) {
		
        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        camera= Camera.open();
        try {
			camera.setPreviewDisplay(mSurfaceHolder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Camera.Parameters p = camera.getParameters();
        p.setJpegQuality(100);//a value between 1 and 100
        p.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
        camera.setParameters(p);
        camera.startPreview();
		Log.d("image","click");
		camera.takePicture(null, null,jpegCallback);
		try{
	        Thread.sleep(1000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    camera.release();
		}
};

private OnClickListener sendlistener = new OnClickListener() {
	public void onClick(View v) {
		 Log.d("image", "enviando");
		 //put your details
		  
		 
		 
		  settings= new Settings(getApplicationContext());
		  
		  Mail m = new Mail(settings.getUser(),settings.getPass());
		  m.set_host(settings.getSmtp());
		  m.set_port(settings.getPort());
		  m.set_sport(settings.getPort());
		  String[] toArr = {settings.getTo()};
	      m.set_to(toArr);
	      m.set_from(settings.getFrom());
	      m.set_subject("Security Cam App image");
	      m.setBody("image");
          try {
        	  file = getFileStreamPath(captura);
        	  m.addAttachment(file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		} 
          SendEmail task= new SendEmail();
          task.execute(new Mail[]{m});
	}
};
private OnClickListener configlistener = new OnClickListener() {
	public void onClick(View v) {
	
		Intent myIntent = new Intent(getApplicationContext(), ConfigActivity.class);
        startActivityForResult(myIntent,0);
	}
};

private class SendEmail extends AsyncTask<Mail, Integer, Boolean> {

    @Override
    protected void onPreExecute() {
    	Log.d("image","enviando");
    }
	@Override
	protected Boolean doInBackground(Mail... mails) {
		try{
		return mails[0].send();
		}
		catch (Exception e) {
			
			return false;
		}
	}
	@Override
	protected void onPostExecute(Boolean result) {
		if(result){
			Toast.makeText(SecurityCamAppActivity.this, "Email was sent successfully.", Toast.LENGTH_LONG).show(); 
	        } else { 
	        Toast.makeText(SecurityCamAppActivity.this, "Email was not sent.", Toast.LENGTH_LONG).show(); 
	        }
	}
}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mSurfaceView=new SurfaceView(this);
        
        Button takeButton=(Button)findViewById(R.id.take);
        Button sendButton=(Button)findViewById(R.id.send);
        Button configButton= (Button)findViewById(R.id.configbutton);
        
        takeButton.setOnClickListener(buttonlistener);
        sendButton.setOnClickListener(sendlistener);
        configButton.setOnClickListener(configlistener);
    }
}