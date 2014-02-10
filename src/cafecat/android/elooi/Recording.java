package cafecat.android.elooi;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class Recording extends Activity {
	private boolean isRecording = false;
	private boolean isAudioPlaying = false;
	private ImageButton bt_recStartStop = null;
	private ImageButton bt_audioPlayPause = null;
	private MediaPlayer mPlayer = null;
	private MediaRecorder mRecorder = null;
	private Timer animateTimer = null;
	private TextView txv_sec = null;
	private myStopwatch stopwatch = new myStopwatch();
	private Timer secondTimer;
	private Bitmap mbmp=null;
	private Bitmap resizebitmap=null;
	private ImageView imgv_grayEffect=null;
	
	private TextView debugTxt = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recording);
		
		bt_recStartStop = (ImageButton)findViewById(R.id.imgbtn_left);
		bt_audioPlayPause = (ImageButton)findViewById(R.id.imgbtn_right);
		txv_sec = (TextView)findViewById(R.id.textv_sec);
		imgv_grayEffect = (ImageView)findViewById(R.id.imagev_gray_effect);
		debugTxt = (TextView)findViewById(R.id.textView1);
		
		bt_recStartStop.setOnClickListener(mOnClickAddAudio);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.recording, menu);
		return true;
	}
	
	private OnClickListener mOnClickAddAudio = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				case R.id.imgbtn_left:{
					if(isRecording==false)
					{	
						if(isAudioPlaying==false)
						{	
							RecordingNow(true);
						}else{
							//PlaySound(false, false, true,false);
						}
					}else{
						RecordingNow(false);
					}
					break;
				}
				case R.id.imgbtn_right:{
					if(isAudioPlaying==false){
						//PlaySound(true, false, false,false);
					}else{
						if (mPlayer.isPlaying()) {
							//PlaySound(false, true, false,false);
				        }else{
				        	//PlaySound(false, false, false, true);
				        }			
					}
					break;
				}
			}
		}
	};
	
	private void RecordingNow(Boolean startRecording)
    {
    	if(startRecording==true)
    	{
	    	bt_audioPlayPause.setEnabled(false);
			
			isRecording=true; 
			bt_recStartStop.setBackgroundResource( R.drawable.bt_stop_enabled );
			bt_audioPlayPause.setBackgroundResource(R.drawable.bt_play_disabled);
			
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			File audiofile = null;
			File sampleDir = Environment.getExternalStorageDirectory();
		       try
		       { 
		          audiofile = File.createTempFile("elooiaudio", ".mp4", sampleDir);
		       }
		       catch (IOException e) 
		       {
		           //Log.e(TAG,"sdcard access error");
		           return;
		       }
			mRecorder.setOutputFile(audiofile.getAbsolutePath());
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			try{
				mRecorder.prepare();
			}catch(IOException e){
				//Log.e(LOG_TAG, "prepare() failed"); 
			}
			mRecorder.start();
			CountSecond(true,false);
			
			animateTimer = new Timer();
			animateTimer.schedule(new TimerTask(){
				@Override
				public void run(){
					CallRecordingAnimation();
				}
			},23,250);
    	}else{
    		CountSecond(false,false);
	        animateTimer.cancel();
	        resizebitmap = Bitmap.createScaledBitmap(mbmp,320,350,true);
	        imgv_grayEffect.setImageBitmap(resizebitmap);
	        mbmp=null; resizebitmap=null;
	        
	        
	        mRecorder.stop();
	        mRecorder.release();
	        mRecorder = null;
	        
    		bt_audioPlayPause.setEnabled(true);
			isRecording=false;
			bt_recStartStop.setBackgroundResource(R.drawable.bt_record_enabled);
			bt_audioPlayPause.setBackgroundResource(R.drawable.bt_play_enabled);
			
    	}
    }
	
	private void CallRecordingAnimation()
	{
		this.runOnUiThread(recordingAnimation_Tick);
	}
	
	private Runnable recordingAnimation_Tick = new Runnable()
	{
		public void run(){
			int amplitude = mRecorder.getMaxAmplitude();
			String debugTxt="";
			debugTxt = scaleGrayGradiance(amplitude, R.drawable.gray, 25000, 320, 350);
			//txv_debug.setTag(debugTxt);
		}
	};
	
	private String scaleGrayGradiance(int amplitude,int resourceID,int maximumAmplitude,int graphicWidth, int graphicHeight)
	{
		String result="";
		mbmp = BitmapFactory.decodeResource(getResources(),resourceID);
		int width = graphicWidth;
		int height = graphicHeight;
		float newheight=0;
		int maxvalue=maximumAmplitude;
		if(amplitude<=maxvalue)
		{ 
			newheight = ((float)height/(float)maxvalue)*amplitude;
			height = height - Math.round(newheight);
			resizebitmap = Bitmap.createScaledBitmap(mbmp,width,height,true);
			imgv_grayEffect.setImageBitmap(resizebitmap);
		}else{
			imgv_grayEffect.layout(0, 0, 0, 1);
			mbmp=null;
		}
		result = "amp"+Integer.toString(amplitude)+" h"+Integer.toString(height);
		return result;
	}
	
	private void CountSecond(Boolean start, Boolean isResume)
    {
    	if(start)
    	{
    		if(isResume==false)
    		{
    			txv_sec.setText("00:00");
    			stopwatch.start();
    		}else{
    			stopwatch.resume();
    		}
    		secondTimer = new Timer();
			secondTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					UpdateSecondDisplay();
				}
			},0,1000);
    	}else{
    		secondTimer.cancel();
    		stopwatch.stop();
    		int secNow = stopwatch.getAvergeCleanSecs();
    		txv_sec.setText("00:"+stopwatch.formatDigitSecs(secNow));
    	}
    }
	
	private void UpdateSecondDisplay()
	{
		this.runOnUiThread(SecondTimer_Tick);
	}
	
	private Runnable SecondTimer_Tick = new Runnable()
	{
		public void run(){
			int sec = stopwatch.getAvergeCleanSecs();
			txv_sec.setText("00:"+stopwatch.formatDigitSecs(sec));
			if(sec==52)
			{
				//txv_debug.setText("hang up now");
				//long[] pattern = { 500,250,400,200};
				//audioRecording.v.vibrate(pattern,-1);
			}
			if(sec==60)
			{
				RecordingNow(false);
			}
		}
	};
}
