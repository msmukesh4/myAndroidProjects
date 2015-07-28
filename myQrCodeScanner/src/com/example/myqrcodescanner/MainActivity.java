package com.example.myqrcodescanner;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
 
public class MainActivity extends Activity {
 
	private static final int ACTIVITY_RESULT_QR_DRDROID = 0;
	
	TextView report;
	Button scan;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		report = (TextView) findViewById(R.id.textView2);
		scan = (Button) findViewById(R.id.button1);
		
		scan.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent i = new Intent("la.droid.qr.scan");
				
				try {
					
					startActivityForResult(i, ACTIVITY_RESULT_QR_DRDROID);
				} 
				catch (ActivityNotFoundException activity) {
					
					MainActivity.qrDroidRequired(MainActivity.this);
				}
			}
		});
	}	
 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if( ACTIVITY_RESULT_QR_DRDROID == requestCode 
				&& data != null && data.getExtras() != null ) {
			
			String result = data.getExtras().getString("la.droid.qr.result");
			
			report.setText(result);
			report.setVisibility(View.VISIBLE);
		}
	}
 
	/* 
	 * 
	 * If we don't have QRDroid Application in our Device, 
	 * It will call below method (qrDroidRequired)
	 * 
	 */
	
	protected static void qrDroidRequired(final MainActivity activity) {
		// TODO Auto-generated method stub
		
		AlertDialog.Builder AlertBox = new AlertDialog.Builder(activity);
		
		AlertBox.setMessage("QRDroid Missing");
		
		AlertBox.setPositiveButton("Direct Download", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				
				activity.startActivity(new Intent(Intent.ACTION_VIEW, 
					Uri.parse("http://droid.la/apk/qr/")));
			}
		});
		
		AlertBox.setNeutralButton("From Market", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				activity.startActivity(new Intent(Intent.ACTION_VIEW, 
					Uri.parse("http://market.android.com/details?id=la.droid.qr")));
			}
		});
		
		AlertBox.setNegativeButton("Cancel", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dialog.cancel();
			}
		});
		
		AlertBox.create().show();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
