package com.follov.activity;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.follov.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FollovJoinMapActivity extends FragmentActivity implements
		OnClickListener
{
	protected GoogleMap mMap;
	private ImageButton searchBt;
	private ImageButton nextb;

	private ProgressDialog progressDialog;
	private String errorString = "";
	private GoogleMapkiUtil httpUtil;
	private AlertDialog errorDialog;

	private LatLng my_loction;
	double[] my_gps;
	
	Dialog dialog;
	Dialog dialog2;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		setUpMapIfNeeded();

		
		mMap.setMyLocationEnabled(true);

		my_gps = MyLocation.getMyLocation(FollovJoinMapActivity.this);
		my_loction = new LatLng(my_gps[0],my_gps[1]);

		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my_loction, 15));

		searchBt = (ImageButton) findViewById(R.id.mapview_search);
		searchBt.setOnClickListener(this);

		nextb = (ImageButton) findViewById(R.id.nextb);
		nextb.setOnClickListener(this);

		httpUtil = new GoogleMapkiUtil();
		
		displayAlertDialog();
		
		errorDialog = new AlertDialog.Builder(this).setTitle("에러")
				.setMessage(errorString).setPositiveButton("취소", null).create();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.mapview_search:
			{
				final LinearLayout linear = (LinearLayout) View.inflate(
						FollovJoinMapActivity.this, R.layout.dialog_map_namesearch,
						null);
				TextView addrTv = (TextView) linear.findViewById(R.id.dialog_map_search_addr);
				String str = getAddres(my_gps[0], my_gps[1]);
				addrTv.setText(str);
				new AlertDialog.Builder(FollovJoinMapActivity.this)
						.setTitle("주소 검색").setView(linear)
						.setPositiveButton("확인", onClickNameSearch)
						.setNegativeButton("취소", null).show();
				break;
			}
			case R.id.nextb:
			{
				// 여기서 위치들 서버로 넘기면됨.
				Intent i = new Intent(FollovJoinMapActivity.this, FollovMainActivity.class);
				startActivity(i);
				finish();
				break;
			}
			case R.id.nextstep1:
			{
				dismissAlertDialog();
				displayAlertDialog2();
				break;
			}
			case R.id.nextstep2:
			{
				dismissAlertDialog2();
				break;
			}
		}
	}

	private DialogInterface.OnClickListener onClickNameSearch = new DialogInterface.OnClickListener()
	{

		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			AlertDialog ad = (AlertDialog) dialog;
			EditText nameEt = (EditText) ad
					.findViewById(R.id.dialog_map_search_et);
			TextView addrTv = (TextView) ad
					.findViewById(R.id.dialog_map_search_addr);

			if (nameEt.getText().length() > 0)
			{
				if (progressDialog != null && progressDialog.isShowing())
					return;
				progressDialog = ProgressDialog.show(
						FollovJoinMapActivity.this, "Wait", "잠시만 기다려 주세요...");

				httpUtil.requestMapSearch(new ResultHandler(
						FollovJoinMapActivity.this), nameEt.getText()
						.toString(), addrTv.getText().toString());

				final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(nameEt.getWindowToken(), 0);
			}
		}
	};

	private void setUpMapIfNeeded()
	{
		if (mMap == null)
		{
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null)
			{
				setUpMap();
			}
		}
	}

	private void setUpMap()
	{

		mMap.setMapType(MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);

	}

	static class ResultHandler extends Handler
	{
		private final WeakReference<FollovJoinMapActivity> mActivity;

		ResultHandler(FollovJoinMapActivity mapActivity)
		{
			mActivity = new WeakReference<FollovJoinMapActivity>(mapActivity);
		}

		@Override
		public void handleMessage(Message msg)
		{
			FollovJoinMapActivity activity = mActivity.get();
			if (activity != null)
			{
				activity.handleMessage(msg);
			}
		}
	}

	private void handleMessage(Message msg)
	{
		progressDialog.dismiss();

		String result = msg.getData().getString(GoogleMapkiUtil.RESULT);
		ArrayList<String> searchList = new ArrayList<String>();

		if (result.equals(GoogleMapkiUtil.SUCCESS_RESULT))
		{
			searchList = msg.getData().getStringArrayList("searchList");
		} 
		else if (result.equals(GoogleMapkiUtil.TIMEOUT_RESULT))
		{
			errorString = "연결 할 수 없습니다.";
			errorDialog.setMessage(errorString);
			errorDialog.show();
			return;
		}
		else if (result.equals(GoogleMapkiUtil.FAIL_MAP_RESULT))
		{
			errorString = "연결 할 수 없습니다.";
			errorDialog.setMessage(errorString);
			errorDialog.show();
			return;
		}
		else
		{
			errorString = httpUtil.stringData;
			errorDialog.setMessage(errorString);
			errorDialog.show();
			return;
		}

		String[] searches = searchList.toArray(new String[searchList.size()]);
		adjustToPoints(searches);
	}

	protected void adjustToPoints(String[] results)
	{
		mMap.clear();
		
		my_gps[0]=Float.valueOf(results[1]);
		my_gps[1]=Float.valueOf(results[2]);
		my_loction = new LatLng(my_gps[0], my_gps[1]);

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(my_loction, 15));
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setUpMapIfNeeded();
	}

	private String getAddres(double lat, double lng)
	{
		Geocoder gcK = new Geocoder(getApplicationContext(), Locale.KOREA);
		String res = "";
		try
		{
			List<Address> addresses = gcK.getFromLocation(lat, lng, 1);
			StringBuilder sb = new StringBuilder();

			if (null != addresses && addresses.size() > 0)
			{
				Address address = addresses.get(0);
				sb.append(address.getLocality()).append("/");
				sb.append(address.getThoroughfare()).append("/");
				sb.append(address.getFeatureName());
				res = sb.toString();
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return res;
	}
	private void displayAlertDialog(){
		dialog=new Dialog(this);
        dialog.setOwnerActivity(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_image);
    
        ImageButton imageView = (ImageButton)dialog.findViewById(R.id.nextstep1);
        imageView.setOnClickListener(this);
        dialog.show();
    }
	private void dismissAlertDialog(){
		dialog.dismiss();
    }
	private void displayAlertDialog2(){
		dialog2=new Dialog(this);
        dialog2.setOwnerActivity(this);
        dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog2.setContentView(R.layout.dialog_image2);
     
        ImageButton imageView = (ImageButton)dialog2.findViewById(R.id.nextstep2);
        imageView.setOnClickListener(this);
        dialog2.show();
    }
	private void dismissAlertDialog2(){
		dialog2.dismiss();
    }
}
