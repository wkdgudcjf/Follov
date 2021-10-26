package com.follov.activityrecognition;

import java.util.StringTokenizer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.follov.R;
import com.follov.Manager.AppManager;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.nostra13.universalimageloader.core.ImageLoader;

public class InfoWindow implements InfoWindowAdapter
{
	Context context;
	LayoutInflater inflater;
	View view;
	ImageView image;
		
	public InfoWindow(Context context) 
	{
		this.context = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view =inflater.inflate(R.layout.balloon,null);
		image = (ImageView) view.findViewById(R.id.balloon);
	}
// override되는 두개의 메소드 중 한가지를 선택하여 사용한다.(getInfoWindow좀더 깔끔하게 노출됨)
// null로 return이 되면 infowindow가 표시 되지 않는다.
// infowindow에서 button을 붙이더라도 click 이벤트의 focus가 infowindow에 고정 되어있어 따로
// button의 click 이벤트를 설정할수 없는 것으로 보인다.(google 사이트를 아무리 찾아봐도 따로 
// listener를  걸수가 없음)
	@Override
	public View getInfoContents(Marker marker)
	{
		return null;
	}
	@Override
	public View getInfoWindow(Marker marker)
	{
		view.setEnabled(false);
		if(marker.getTitle()==null)
		{
			return null;
		}
		
		StringTokenizer stk = new StringTokenizer(marker.getTitle(),"/");
		String url = "http://211.189.20.150:8080/FollovServer/upload/"+stk.nextToken()+"/"+stk.nextToken()+"/"+stk.nextToken()+"/"+stk.nextToken()+"/"+stk.nextToken();
		Log.i("여기니?", url);
		ImageLoader.getInstance().displayImage(url, image,AppManager.getInstance().getOptions());
		return view;
		//click한 infowindow의 marker가 전달됨.
	}
}

