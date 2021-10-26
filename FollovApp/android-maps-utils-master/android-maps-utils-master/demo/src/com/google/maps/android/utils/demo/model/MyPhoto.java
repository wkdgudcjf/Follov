package com.google.maps.android.utils.demo.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyPhoto implements ClusterItem
{
   private int date_code;
   private int loc_no;
   private String name;
   private String date;
   private String couple_id;
   private LatLng point;
   public MyPhoto()
   {
      super();
   }

   public MyPhoto(int date_code, int loc_no, String name, String date,
         String couple_id, LatLng point)
   {
      super();
      this.date_code = date_code;
      this.loc_no = loc_no;
      this.name = name;
      this.date = date;
      this.couple_id = couple_id;
      this.point = point;
   }

   public int getDate_code()
   {
      return date_code;
   }
   public void setDate_code(int date_code)
   {
      this.date_code = date_code;
   }
   public int getLoc_no()
   {
      return loc_no;
   }
   public void setLoc_no(int loc_no)
   {
      this.loc_no = loc_no;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public String getDate()
   {
      return date;
   }
   public void setDate(String date)
   {
      this.date = date;
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
