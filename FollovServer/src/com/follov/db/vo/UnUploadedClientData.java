package com.follov.db.vo;

import java.io.*;
import java.util.*;


public class UnUploadedClientData implements Serializable{

   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private String couple_id = "";
   private String email = "";
   private String pw = "";
   private ArrayList<Integer> modified_complete = new ArrayList<Integer>();
   private ArrayList<Integer> get_data_after_merge = new ArrayList<Integer>();
   private ArrayList<Date_tb_client_VO> dates = new ArrayList<Date_tb_client_VO>();
   
   private HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_routes
      = new HashMap<Integer, ArrayList<Loc_route_tb_VO>>();
   
   private HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> loc_photos 
      = new HashMap<Integer, HashMap<Integer,ArrayList<Loc_photo_tb_VO>>>();
   
   public UnUploadedClientData() {
      super();
   }

   public UnUploadedClientData(
         String couple_id,
         String email,
         String pw,
         ArrayList<Integer> modified_complete,
         ArrayList<Integer> get_data_after_merge,
         ArrayList<Date_tb_client_VO> dates,
         HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_routes,
         HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> loc_photos) {
      
      super();
      this.couple_id = couple_id;
      this.email = email;
      this.pw = pw;
      this.modified_complete = modified_complete;
      this.get_data_after_merge = get_data_after_merge; 
      this.dates = dates;
      this.loc_routes = loc_routes;
      this.loc_photos = loc_photos;
   }

   public String getCouple_id()
   {
      return couple_id;
   }

   public void setCouple_id(String couple_id)
   {
      this.couple_id = couple_id;
   }

   public String getEmail()
   {
      return email;
   }

   public void setEmail(String email)
   {
      this.email = email;
   }

   public String getPw()
   {
      return pw;
   }

   public void setPw(String pw)
   {
      this.pw = pw;
   }

   public ArrayList<Integer> getModified_complete()
   {
      return modified_complete;
   }

   public void setModified_complete(ArrayList<Integer> modified_complete)
   {
      this.modified_complete = modified_complete;
   }

   public ArrayList<Integer> getGet_data_after_merge()
   {
      return get_data_after_merge;
   }

   public void setGet_data_after_merge(ArrayList<Integer> get_data_after_merge)
   {
      this.get_data_after_merge = get_data_after_merge;
   }

   public ArrayList<Date_tb_client_VO> getDates()
   {
      return dates;
   }

   public void setDates(ArrayList<Date_tb_client_VO> dates)
   {
      this.dates = dates;
   }

   public HashMap<Integer, ArrayList<Loc_route_tb_VO>> getLoc_routes()
   {
      return loc_routes;
   }

   public void setLoc_routes(
         HashMap<Integer, ArrayList<Loc_route_tb_VO>> loc_routes)
   {
      this.loc_routes = loc_routes;
   }

   public HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> getLoc_photos()
   {
      return loc_photos;
   }

   public void setLoc_photos(
         HashMap<Integer, HashMap<Integer, ArrayList<Loc_photo_tb_VO>>> loc_photos)
   {
      this.loc_photos = loc_photos;
   }

   @Override
   public String toString()
   {
      return "UnUploadedClientData [couple_id=" + couple_id + ", email="
            + email + ", pw=" + pw + ", modified_complete="
            + modified_complete + ", get_data_after_merge="
            + get_data_after_merge + ", dates=" + dates + ", loc_routes="
            + loc_routes + ", loc_photos=" + loc_photos + "]";
   }

   
   
   
}