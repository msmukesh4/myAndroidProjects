package com.example.myqrcodegeneretor;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
 
public class MainActivity extends Activity {
	
	private static final int ACTIVITY_RESULT_QR_DRDROID = 0;
	
	Button generate;
	EditText edit;
	ImageView imgResult;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		generate = (Button) findViewById(R.id.button1);
		edit = (EditText) findViewById(R.id.editText1);
		imgResult = ( ImageView ) findViewById(R.id.imageView1);
		imgResult.setVisibility(View.GONE);
		
		generate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				String code = edit.getText().toString();
				
				if(code.trim().length() == 0) {
					
					Toast.makeText(getBaseContext(), 
						"Enter Text", Toast.LENGTH_SHORT ).show();
					return;
				}
				
				Intent encode = new Intent("la.droid.qr.encode"); 
				encode.putExtra("la.droid.qr.code", code);			
				encode.putExtra("la.droid.qr.image", true);
				encode.putExtra("la.droid.qr.size", 0);
								
				try {
					
					startActivityForResult(encode, ACTIVITY_RESULT_QR_DRDROID);
				}
				catch (ActivityNotFoundException activity) {
					
	//				qrDroidRequired(MainActivity.this);
				}
			}
		});
	}
 
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(ACTIVITY_RESULT_QR_DRDROID == requestCode 
				&& data != null && data.getExtras() != null ) {
			
							
			String qrCode = data.getExtras().getString("la.droid.qr.result");
				
			if(qrCode == null || qrCode.trim().length() == 0) {
					
				Toast.makeText(getBaseContext(), "QR Code Image " +
					"is not Saved", Toast.LENGTH_LONG).show();
				return;
			}
				
			Toast.makeText(getBaseContext(), "QR Code Image is Saved" 
				+ " " + qrCode, Toast.LENGTH_LONG).show();
				
			 Uri chosenImageUri = data.getData();

		        Bitmap mBitmap = null;
		        try {
					mBitmap = Media.getBitmap(this.getContentResolver(), chosenImageUri);
				} catch (Exception e) {
					Log.v("url","File not found");
					e.printStackTrace();
				}
			imgResult.setImageBitmap(mBitmap);
				
			imgResult.setVisibility( View.VISIBLE );			
		}
	}	
	
//	/* 
//	 * 
//	 * If we don't have QRDroid Application in our Device, 
//	 * It will call below method (qrDroidRequired)
//	 * 
//	 */
//	
//	protected static void qrDroidRequired(final MainActivity activity) {
//		// TODO Auto-generated method stub
//		
//		AlertDialog.Builder AlertBox = new AlertDialog.Builder(activity);
//		
//		AlertBox.setMessage("QRDroid Missing");
//		
//		AlertBox.setPositiveButton("Direct Download", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				// TODO Auto-generated method stub
//				
//				activity.startActivity(new Intent(Intent.ACTION_VIEW, 
//					Uri.parse("http://droid.la/apk/qr/")));
//			}
//		});
//		
//		AlertBox.setNeutralButton("From Market", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//				activity.startActivity(new Intent(Intent.ACTION_VIEW, 
//					Uri.parse("http://market.android.com/details?id=la.droid.qr")));
//			}
//		});
//		
//		AlertBox.setNegativeButton("Cancel", new OnClickListener() {
//			
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//				dialog.cancel();
//			}
//		});
//		
//		AlertBox.create().show();
//	}
//	
//	@Override
//    public void onConfigurationChanged(Configuration newConfig) {
//    	super.onConfigurationChanged(newConfig);
//    	//Nothing
//    }
}