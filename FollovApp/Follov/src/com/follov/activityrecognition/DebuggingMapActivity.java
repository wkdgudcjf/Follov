package com.follov.activityrecognition;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.follov.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public class DebuggingMapActivity extends FragmentActivity{

	public static final String TAG = DebuggingMapActivity.class.getSimpleName();
	
	GoogleMap mGoogleMap;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap(); 
	}
}
