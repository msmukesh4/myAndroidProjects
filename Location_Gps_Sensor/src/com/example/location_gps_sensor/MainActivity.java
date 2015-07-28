package com.example.location_gps_sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements SensorEventListener,OnClickListener,OnSeekBarChangeListener{
	
	SensorManager Smanager;
	Sensor lsensor,rotationSensor;
	SensorEventListener listner,orientationlistner;
	int steps=0;
	TextView coordinates,step,seekthreshold;
	float yprev=0,threshold=(float) 1.7;
	float[] mr = new float[9];
	Button reset;
	SeekBar seekbar;
	int left=0,right=0,straight=0,rold=0,lold=0,sold=0;
	float rollInDegress=0,oldRollInDegree=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Smanager = (SensorManager) getSystemService(SENSOR_SERVICE);
		lsensor= Smanager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		rotationSensor = Smanager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		coordinates =  (TextView) findViewById(R.id.textView1);
		step=(TextView) findViewById(R.id.textView2);
		reset=(Button) findViewById(R.id.button1);
		seekbar=(SeekBar) findViewById(R.id.seekBar1);
		seekthreshold=(TextView) findViewById(R.id.textView3);
		seekthreshold.setText("Threshold set to : 2");
		step.setText("steps : 0");
		Log.v("LEE", "sensor manager created"+lsensor);
		Smanager.registerListener((SensorEventListener) this, lsensor,SensorManager.SENSOR_DELAY_NORMAL);
		Log.v("LEE", "sensor manager created"+rotationSensor);
		Smanager.registerListener((SensorEventListener) this, rotationSensor,SensorManager.SENSOR_DELAY_NORMAL);
		
		seekbar.setOnSeekBarChangeListener(this);
		reset.setOnClickListener(this);
		
	}
	
	@Override
	protected void onResume() {
		Log.v("LEE", "sensor registered");
		Smanager.registerListener((SensorEventListener) this, lsensor,SensorManager.SENSOR_DELAY_NORMAL);
		Smanager.registerListener((SensorEventListener) this, rotationSensor,SensorManager.SENSOR_DELAY_NORMAL);
		oldRollInDegree=rollInDegress;
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		Log.v("LEE", "Sensor Unregisterd");
		Smanager.unregisterListener(this, lsensor);
		Smanager.unregisterListener(this,rotationSensor);
		
		super.onPause();
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		//Log.v("LEE", "sensor value changed");
		//float val = event.values[0];
		if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION){
			float x = event.values[0];
			float y = event.values[1];
			if(y<0){
				y=y*-1;
			}
			float z = event.values[2];
			if((y-yprev)>threshold){
				steps++;
				Log.v("LEE", "Steps : "+steps);
				step.setText("steps :"+steps);
				seekthreshold.setText("Threshold set to : "+threshold);
			}
			coordinates.setText("X :"+x+
					"\nY: "+y+
					"\nZ: "+z);
			yprev=y;
		}
		if(event.sensor.getType()==Sensor.TYPE_ROTATION_VECTOR){
			
			float mAzimuth = event.values[0];
            float mPitch = event.values[1];
            float mRoll = event.values[2];
            float azimuthInDegress = (float)(Math.toDegrees(mAzimuth)+360)%360;
            float pitchInDegress = (float)(Math.toDegrees(mPitch)+360)%360;
            rollInDegress = (float)(Math.toDegrees(mRoll)+360)%360;
            

				
	            if ((mAzimuth>=.2 && mAzimuth<.45)&&
	            		(mPitch<-.35 && mPitch>=-.8)&&
	            		(mRoll<.55 && mRoll>=-.85)){
	            	//Log.e("position", "you are standing straight ");
	            	straight=1; right=0; left=0; lold=0; rold=0;
				}
	            if ((mPitch>=.37 && mPitch<.8)&&
	            		(mAzimuth>.18 && mAzimuth<=.52)&&
	            		(mRoll>.35 && mRoll<.75)
	            		){
	            	//Log.e("position", "you turned left");
	            	left=1; straight=0; right=0;rold=0;sold=0;
	            
	            }
	            if ((mAzimuth>=-.23 && mAzimuth<=.27)&&
	            		(mPitch>=-.88 && mPitch<=.88)&&
	            		(mRoll>=-.88 && mRoll<=.7)
	            		){
	            	//Log.e("position", "you turned right ");
	            	right=1; straight=0; left=0; sold=0; lold=0;
	            }
	            if(rold!=right){
	            	Log.e("position", "turned right");
	            	rold=right;
	            }
	            if (sold!=straight) {
	            	Log.e("position", "standing straight");
	            	sold=straight;
				}
	            if (lold!=left) {
	            	Log.e("position", "turned left");
	            	lold=left;
				}
	            
	            
	            Log.d("rotate-azimuth","mAzimuth :"+mAzimuth+" degree : "+azimuthInDegress);
	            Log.d("rotate-pitch","mPitch :"+mPitch+" degree : "+pitchInDegress);
	            Log.d("rotate-roll","mRoll :"+mRoll+" degree : "+rollInDegress);
	           // Log.d("rotate","mRoll : "+costhetaby2);
	            float diff=oldRollInDegree-rollInDegress;
	            if (oldRollInDegree==0) {
					oldRollInDegree=rollInDegress;
				}else if (diff>250) {
					Log.v("turning", "You turned right");
					oldRollInDegree=rollInDegress;
					
				}
	            
	            else if((diff)>=15){
	            	oldRollInDegree=rollInDegress;
	            	Log.v("turning", "You turned right");
	            }else if (rollInDegress-oldRollInDegree>=15) {
					Log.v("turning", "You turned left");
					oldRollInDegree=rollInDegress;
				}else if (rollInDegress-oldRollInDegree<-340) {
					Log.v("turning", "You turned left");
					oldRollInDegree=rollInDegress;
				}
		}
		
	}
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		Log.v("LEE", "sensor accuracy changed");
		
	}

	@Override
	public void onClick(View arg0) {
		steps=0;
		step.setText("steps : 0");
		right=0;
		left=0;
		straight=0;
		
	}


	
    @Override
     public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
    	threshold=progresValue;
    	seekthreshold.setText("Threshold set to : "+threshold);
         Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
     }
     @Override
     public void onStartTrackingTouch(SeekBar seekBar) {
         Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
     }
     @Override
     public void onStopTrackingTouch(SeekBar seekBar) {
         Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
     }


}
