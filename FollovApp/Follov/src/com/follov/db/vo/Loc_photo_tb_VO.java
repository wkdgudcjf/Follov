package com.follov.db.vo;

import java.io.*;

/**
 * @author JHC
 *
 */
public class Loc_photo_tb_VO implements Serializable
{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;
   private int photo_no;
   private int date_code;
   private int loc_no;
   private String name;
   private String date;
   private String photo_taken_email;
   private String is_img_uploaded;
   
   public Loc_photo_tb_VO()
   {
      super();
   }
   public Loc_photo_tb_VO(int date_code, int loc_no,
         String name, String date, String photo_taken_email, String is_img_uploaded)
   {
      super();
      this.date_code = date_code;
      this.loc_no = loc_no;
      this.name = name;
      this.date = date;
      this.photo_taken_email = photo_taken_email;
      this.is_img_uploaded = is_img_uploaded;
   }
   public int getPhoto_no()
   {
      return photo_no;
   }
   public void setPhoto_no(int photo_no)
   {
      this.photo_no = photo_no;
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
   public String getPhoto_taken_email()
   {
      return photo_taken_email;
   }
   public void setPhoto_taken_email(String photo_taken_email)
   {
      this.photo_taken_email = photo_taken_email;
   }
   public String getIs_img_uploaded()
   {
      return is_img_uploaded;
   }
   public void setIs_img_uploaded(String is_img_uploaded)
   {
      this.is_img_uploaded = is_img_uploaded;
   }
   
   public void increaseLoc_no(){
      this.loc_no++;
   }
   
   @Override
   public String toString()
   {
      return "Loc_photo_tb_VO [photo_no=" + photo_no + ", date_code="
            + date_code + ", loc_no=" + loc_no + ", name=" + name
            + ", date=" + date + ", photo_taken_email=" + photo_taken_email
            + ", is_img_uploaded=" + is_img_uploaded + "]";
   }
   
   
   
   
   
   

}