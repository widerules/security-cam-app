package com.chilerocks.securitycamapp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
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
	public static final String PREFS_NAME = "pref.dat";

	PictureCallback jpegCallback = new PictureCallback() {
		  public void onPictureTaken(byte[] _data, Camera _camera) {
		try{
			FileOutputStream outStream = null;
			String captura= "captura";
			file = File.createTempFile(captura, ".jpeg");
			outStream = new FileOutputStream(file);
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
        p.setJpegQuality(10);//a value between 1 and 100
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
		 Mail m = new Mail("dev@chilerocks.org", "your_pass");
		 String[] toArr = {"dev@chilerocks.org"}; 
	      m.set_to(toArr); 
	      m.set_from("dev@chilerocks.org"); 
	      m.set_subject("imagen"); 
	      m.setBody("aquiestalaimagen"); 
          try {
        	  m.addAttachment(file.getPath());
		} catch (Exception e) {
			e.printStackTrace();
		} 
          SendEmail task= new SendEmail();
          task.execute(new Mail[]{m});
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
        
        takeButton.setOnClickListener(buttonlistener);
        sendButton.setOnClickListener(sendlistener);

    }
}