package com.follov;

import android.sax.*;

public class FollovSetUp {

	
	//데이트 종료 확정 시점 시간(분 단위 ex: 3분)
	public static final int TRACKING_END_INTERVAL_MIN = 30;
	
	//둘이 만난지 60초가 되면 데이트를 시작한것으로 판단한다.(데이트 시작 확정 시간)
	public static final int TRACKING_START_CHECK_IN_SECONDS = 60 * 1;

	//통신사 네트워크 (1차가설) 만남 감지 거리 (m 단위) ex: 500.0f *주의 실수단위로 하고 f를 꼭 붙일것
	public static final float CEILING_TRACKING_DISTANCE = 500.0f;
	//ap 네트워크 (2차가설) 만남 감지 거리 (m 단위) ex: 50.0f *주의 실수단위로 하고 f를 꼭 붙일것
	public static final float WIFI_TRACKING_DISTANCE = 60.0f;

	//데이트를 안하고 있을경우 위치측위 주기 (초단위) 
    public static final int UPDATE_INTERVAL_IN_SECONDS_NOT_DATING = 30;
    //데이트를 하고 있을경우 위치측위 주기 (초단위)
    public static final int UPDATE_INTERVAL_IN_SECONDS_IS_DATING = 13; 

    //데이트를 안하고 있을경우 위치측위 주기와 같게 설정 
    public static final int FAST_CEILING_IN_SECONDS_NOT_DATING = 30;
    //데이트를 하고 있을경우 위치측위 주기와 같게 설정 
    public static final int FAST_CEILING_IN_SECONDS_IS_DATING = 13;
    
    //특별장소로 인식되는 시간 (초단위)
    public static final int SPECIAL_POINT_CHECK_IN_SECONDS = 60 * 1;
    
    // 현재 측정은 되고 있지만 적용되지 않고 있는 변수
    public static final int DETECTION_INTERVAL_SECONDS = 10;
    
    //특별장소로 인식되기 위해서 특별장소로 인식되는 시간 만큼의 이전 좌표와 비교하는 거리 (m 단위) *주의 실수단위로 하고 f를 꼭 붙일것
    public static final float SPECAIL_POINT_CHECK_RADIUS = 15.0f;


    
    

    //////////밑으로는 만지지마시오!!!!!!!!!!!!!!!!
    
    
    
    
    public static final int MILLISECONDS_PER_SECOND = 1000;
    
  	public static final int TRACKING_END_INTERVAL_SECOND = 60;
  	
  	public static final int TRACKING_END_INTERVAL_MILLISECONDS = 1000
  			* TRACKING_END_INTERVAL_SECOND * TRACKING_END_INTERVAL_MIN;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS_NOT_DATING;
    
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS_IS_DATING = 
    		MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS_IS_DATING;

    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS_NOT_DATING =
            MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS_NOT_DATING;
    
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS_IS_DATING = 
    		MILLISECONDS_PER_SECOND * FAST_CEILING_IN_SECONDS_IS_DATING;
    
    public static final int DETECTION_INTERVAL_MILLISECONDS =
            MILLISECONDS_PER_SECOND * DETECTION_INTERVAL_SECONDS;
    
//    public static final int CHECK_SPECIAL_CNT = 
//    		SPECIAL_POINT_CHECK_IN_SECONDS / DETECTION_INTERVAL_SECONDS;
    public static final int CHECK_SPECIAL_CNT = 
    		SPECIAL_POINT_CHECK_IN_SECONDS / UPDATE_INTERVAL_IN_SECONDS_IS_DATING;
    
    public static final int CHECK_START_CNT = 
    		TRACKING_START_CHECK_IN_SECONDS / UPDATE_INTERVAL_IN_SECONDS_IS_DATING;
    		

}
