package com.chilerocks.securitycamapp;

import java.io.File;
import java.io.IOException;



import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
public class SecurityCamAppActivity extends Activity {
	
	protected String _path; 
	ImageView imageview;
	SurfaceView  mSurfaceView;
	Camera camera;
	Preview preview;

	PictureCallback jpegCallback = new PictureCallback() {
		  public void onPictureTaken(byte[] _data, Camera _camera) {
		    // TODO Do something with the image JPEG data.
		     //File file = new File( _path );
		     //Uri outputFileUri = Uri.fromFile( file );
			 Log.d("callback","jpeg");
			 BitmapFactory.Options options = new BitmapFactory.Options();
			 options.inSampleSize = 4;
			 Bitmap bitmap = BitmapFactory.decodeByteArray(_data,0,_data.length );
			 //Log.d("image",""+_data.length);
			 //imageview.setImageBitmap(bitmap);
		  }
		};

	private OnClickListener buttonlistener = new OnClickListener() {
		public void onClick(View v) {
		/*camera.takePicture(null, null,jpegCallback);
		Log.d("image","saque");
        camera.release();*/
			
		preview.camera.takePicture(null, null,jpegCallback);
		preview.camera.release();
		}
};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        preview =new Preview(this);
        FrameLayout fr=((FrameLayout)findViewById(R.id.frameLayout1));
        fr.addView(preview);
        Button takeButton=(Button)findViewById(R.id.take);
        takeButton.setOnClickListener(buttonlistener);

    }
}