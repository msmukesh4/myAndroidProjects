package com.example.trackingservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;



public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		final EditText userName = (EditText) findViewById(R.id.name);
		Button  b = (Button) findViewById(R.id.submitName);
		final Intent intent = new Intent(this, SecondActivity.class);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String s= userName.getText().toString();
				intent.putExtra("n", s);
				startActivity(intent);
				
			}
		});
		
		
		
	}
	
}
