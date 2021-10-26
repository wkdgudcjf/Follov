package com.follov.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
 
import android.location.Location;
 
import android.location.LocationManager;
 
public class MyLocation
{
 
   public static double[] getMyLocation(Context context)
   {
        double[] result = null;
  
        Criteria criteria = new Criteria();
 
        criteria.setAccuracy(Criteria.ACCURACY_COARSE); // ��Ȯ��
 
        criteria.setAltitudeRequired(false); // �?
 
        criteria.setBearingRequired(false); // ..
 
        criteria.setSpeedRequired(false); // �ӵ�
 
        LocationManager locationManager;
 
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
 
        //true=���� �̿밡���� ����� ����
 
        String provider = locationManager.getBestProvider(criteria, true);// "gps";
  
        if (provider == null)
        {
            provider = "network";
        }
        Location location = locationManager.getLastKnownLocation(provider);
 
        if(location==null)
        {
            Intent intent = new Intent(context, FollovJoinMapActivity.class);
 
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
 
            context.startActivity(intent);
         }
        else
        {
             result = new double[]
           	 {
                 location.getLatitude(), location.getLongitude()
             };
 
        }
        return result; 
     }
 
}

 
 

  