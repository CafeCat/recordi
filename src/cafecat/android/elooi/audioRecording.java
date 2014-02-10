package cafecat.android.elooi;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import android.media.audiofx.Visualizer;


public class audioRecording extends Activity{
	private boolean isRecording = false;
	private boolean isAudioPlaying = false;
	private ImageButton bt_recStartStop=null;
	private ImageButton bt_audioPlayPause=null;
	private ImageButton btn_takepic=null;
	private MediaRecorder mRecorder = null; 
	private MediaPlayer mPlayer = null;
	private static final String LOG_TAG = "AudioRecordTest";
	private int length;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 4742;
	private TextView txv_sec = null;
	private myStopwatch stopwatch = new myStopwatch();
	private Timer secondTimer;
	private Timer animateTimer;
	private ImageView imgv_grayEffect=null;
	private Bitmap mbmp=null;
	private Bitmap resizebitmap=null;
	private Visualizer mVisualizer;
	private TextView txv_debug=null;
	private static Vibrator v;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audio_recording);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        bt_recStartStop = (ImageButton)findViewById(R.id.bt_rec_start_stop);
		bt_recStartStop.setOnClickListener(mOnClickAddAudio);
		bt_audioPlayPause = (ImageButton)findViewById(R.id.bt_play_start_pause);
		bt_audioPlayPause.setOnClickListener(mOnClickAddAudio);
        txv_sec = (TextView)findViewById(R.id.textv_sec);
        imgv_grayEffect = (ImageView)findViewById(R.id.imagev_gray_effect);
        txv_debug = (TextView)findViewById(R.id.txv_debug);
        txv_debug.setText("");
        
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
	}
	
	private OnClickListener mOnClickAddAudio = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId())
			{
				case R.id.bt_rec_start_stop:{
					if(isRecording==false)
					{	
						if(isAudioPlaying==false)
						{	
							Recording(true);							
						}else{
							PlaySound(false, false, true,false);
						}
					}else{
						Recording(false);
					}
					break;
				}
				case R.id.bt_play_start_pause:{
					if(isAudioPlaying==false){
						PlaySound(true, false, false,false);
					}else{
						if (mPlayer.isPlaying()) {
							PlaySound(false, true, false,false);
				        }else{
				        	PlaySound(false, false, false, true);
				        }			
					}
					break;
				}
			}
		}
	};
    private void Recording(Boolean startRecording)
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
			mRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Elooi/Data/elooiaudio.mp4");
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			try{
				mRecorder.prepare();
			}catch(IOException e){
				Log.e(LOG_TAG, "prepare() failed"); 
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
    private void PlaySound(Boolean startPlaying, Boolean pause, Boolean stopPlaying, Boolean resume)
    {
    	if(startPlaying==true)
    	{
    		isAudioPlaying=true; CountSecond(true,false);
			bt_recStartStop.setBackgroundResource( R.drawable.bt_stop_enabled);
			bt_audioPlayPause.setBackgroundResource(R.drawable.bt_pause_enabled);
			
			mPlayer = new MediaPlayer();
	        try {
	            mPlayer.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Elooi/Data/elooiaudio.mp4");
	            mPlayer.prepare();
	            mPlayer.start(); 
	            setupVisualizrEffect();
	            mVisualizer.setEnabled(true);
	            mPlayer.setOnCompletionListener(new OnCompletionListener() {
	    			
	    			@Override
	    			public void onCompletion(MediaPlayer mp) {
	    				// TODO Auto-generated method stub
	    				CountSecond(false,false); 
	    				isAudioPlaying=false;
						bt_audioPlayPause.setBackgroundResource(R.drawable.bt_play_enabled);
						
						bt_audioPlayPause.setEnabled(true);
						mPlayer.stop();
						mPlayer.release();
						mPlayer = null;
						mVisualizer.setEnabled(false);
						bt_recStartStop.setBackgroundResource( R.drawable.bt_record_enabled );
	    			}
	    		});
	        } catch (IOException e) {
	            Log.e(LOG_TAG, "prepare() failed");
	        }
    	}
    	if(pause==true)
    	{
    		mPlayer.pause(); CountSecond(false,false);//stopwatch.pause(); 
			mVisualizer.setEnabled(false);
			length =  mPlayer.getCurrentPosition();
			bt_audioPlayPause.setBackgroundResource(R.drawable.bt_play_enabled);
    	}
    	if(resume==true)
    	{
    		mPlayer.seekTo(length);  CountSecond(true,true); //stopwatch.resume();
        	mPlayer.start();
        	mVisualizer.setEnabled(true);
        	bt_audioPlayPause.setBackgroundResource(R.drawable.bt_pause_enabled);
    	}
    	if(stopPlaying==true)
    	{
    		isAudioPlaying=false; CountSecond(false,false);
			bt_audioPlayPause.setBackgroundResource(R.drawable.bt_play_enabled);
			mVisualizer.setEnabled(false);
			
			bt_audioPlayPause.setEnabled(true);
			mPlayer.stop(); 
			mPlayer.release();
			mPlayer = null;
			bt_recStartStop.setBackgroundResource( R.drawable.bt_record_enabled );
    	}
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
				long[] pattern = { 500,250,400,200};
				audioRecording.v.vibrate(pattern,-1);
			}
			if(sec==60)
			{
				Recording(false);
			}
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
	private void CallRecordingAnimation()
	{
		this.runOnUiThread(recordingAnimation_Tick);
	}
	private Runnable recordingAnimation_Tick = new Runnable()
	{
		public void run(){
			int amplitude = mRecorder.getMaxAmplitude();
			String debugTxt="";
			debugTxt = scaleGrayGradiance(amplitude, R.drawable.audio_record_effect_layer2, 25000, 320, 350);
			txv_debug.setTag(debugTxt);
		}
	};
	private void setupVisualizrEffect()
	{
		mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener(){
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) 
			{	
			}
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer,
					byte[] waveform, int samplingRate) {
				// TODO Auto-generated method stub
				scaleGrayGradiance(Math.round(waveFormToAmplitude(waveform)), R.drawable.audio_record_effect_layer2, 32000, 320, 350);
				txv_debug.setText("amp"+Float.toString(waveFormToAmplitude(waveform)));
			} 
		},Visualizer.getMaxCaptureRate()/2,true,false);
		
	}
	private float waveFormToAmplitude(byte[] data)
	{
		float result=0;
		float different=0;
		for (int i = 0; i < data.length - 1; i++) {
			if(i<data.length-2)
			{
				different = different+Math.abs(data[i+1]-data[i]);
			}
		}
		result = different;
		return result;
	}
	
	@Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && mPlayer != null) {
            mVisualizer.release();
            mPlayer.release();
            mPlayer = null;
        }
    }
}
