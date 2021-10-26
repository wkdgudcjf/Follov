package com.follov.user;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyPhoto implements ClusterItem
{
   private String name;
   private String couple_id;
   private LatLng point;
   public MyPhoto()
   {
      super();
   }

   public MyPhoto(String couple_id,String name, LatLng point)
   {
      super();
      this.name = name;
      this.couple_id = couple_id;
      this.point = point;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getCouple_id()
   {
      return couple_id;
   }
   public void setCouple_id(String couple_id)
   {
      this.couple_id = couple_id;
   }
   public LatLng getPoint()
   {
      return point;
   }
   public void setPoint(LatLng point)
   {
      this.point = point;
   }
   @Override
   public LatLng getPosition()
   {
      return point;
   }
}
