package com.follov.db;

import android.os.*;

public class DBConfig {

	public static final String ExternalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/follov/";

	public static final String DB_FILE_NAME = "follov.sqlite";
	

	public static final String DB_FILE_DIR = ExternalDirectory + "db/";
	
	public static final String DB_BACKUP_FILE_TEMP_DIR = ExternalDirectory + "dbbackup/";
	//public static final String MP3_DIR = ExternalDirectory + "sounds/";
	public static final String IMAGE_FILE_DIR = ExternalDirectory + "images/";
	//public static final String CONTENT_FILE_DIR = ExternalDirectory + "contents/";
	
	
   
}
