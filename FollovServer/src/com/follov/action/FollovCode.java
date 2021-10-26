package com.follov.action;

public class FollovCode {
	public static final int EMAIl_CHECK_COMPLETION = 1;
	public static final int EMAIl_CHECK_DUPLCATED = 2;
	public static final int JOIN_COMPLETE = 3;
	public static final int HTTP_ERROR = 404;
	public static final int JOIN_ERROR = 4;
	public static final int CANCELOK = 5;
	public static final int CANCELNO = 6;
	public static final int REQUEST = 7;
	public static final int RESPONSE = 8;
	public static final int PROFILE_COMPLETE = 9;
	public static final int PROFILE_ERROR = 10;

	public static final int DATE_INSERT_REQUEST = 11;
	public static final int DATE_INSERT_SUCCESS = 12;
	public static final int DATE_INSERT_FAILED = 13;

	public static final int LOCATION_INSERT_REQUEST = 14;
	public static final int LOCATION_INSERT_SUCCESS = 15;
	public static final int LOCATION_INSERT_FAILED = 16;

	//없애야 할지도
	public static final int SERVER_DB_SYNCHRONIZE_TO_CLIENT_REQUEST = 17;
	public static final int SERVER_DB_SYNCHRONIZE_TO_CLIENT_SUCCESS = 18;
	public static final int SERVER_DB_SYNCHRONIZE_TO_CLIENT_FAILED = 19;
	//


	public static final int DATE_END_TIME_UPDATE_REQUEST = 20;
	public static final int DATE_END_TIME_UPDATE_SUCCESS = 21;
	public static final int DATE_END_TIME_UPDATE_FAILED = 22;


	//없애야 할지도
	public static final int CLIENT_DB_SYNCHRONIZE_TO_SERVER_REQUEST = 23;
	public static final int CLIENT_DB_SYNCHRONIZE_TO_SERVER_SUCCESS = 24;
	public static final int CLIENT_DB_SYNCHRONIZE_TO_SERVER_FAILED = 25;
	//

	public static final int SYNCHRONIZE_TO_SERVER_REQUEST = 26;
	public static final int SYNCHRONIZE_FAILED = 27;
	public static final int GET_NEW_SYNCHRONIZE_DATA_FROM_SERVER = 28;
	public static final int SYNCHONIZE_SUCCESS_NO_DATA_CHANGED = 29;
	
	public static final int RECENT_SERVER_DATE_INFO_REQUEST = 30;
	public static final int RECENT_SERVER_DATE_INFO_RECEIVED = 31;
	public static final int SYNCHRONIZE_NOT_REQUIRE = 32;
	
	public static final int GET_ALL_DATA_FROM_SERVER_REQUEST = 33;
	public static final int GET_ALL_DATA_FROM_SERVER_SUCCESS = 34;
	public static final int GET_ALL_DATA_FROM_SERVER_FAILED = 35;
	
	public static final int GET_MODIFIED_DATA_FROM_SERVER_REQUEST = 36;
	public static final int GET_MODIFIED_DATA_FROM_SERVER_SUCCESS = 37;
	public static final int NO_MODIFIED_DATA_FROM_SERVER = 38;
	
	public static final int SEND_DATE_INFO_TO_SERVER_REQUEST = 39;
	public static final int DATE_INFO_SAVED_SUCCESS = 40;
	public static final int DATE_INFO_SAVED_FAILED = 41;
	
	public static final int MERGED_DATA_RECEIVED = 42;
	public static final int MERGED_COMPLETE = 43;
	public static final int MERGED_COMPLETE_RECEIVED = 44;

	public static final int PHOTO_SAVED_SUCESS = 100;
	public static final int PHOTO_SAVED_FAILED = 101;

	public static final int LOGIN_COMPLETE = 2000;
	public static final int LOGIN_ERROR = 2001;
	public static final int LOGIN_ERROR_ID_NOT_FOUND = 2002;
	public static final int LOGIN_ERROR_PWD_NOT_VALID = 2003;
	public static final int LOGIN_DATA_COMPLETE = 2004;
}
