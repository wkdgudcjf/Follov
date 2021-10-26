package com.follov.component;

import com.follov.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class FollovDialog
{
	public static void showDialog(String str,Activity activity)
	{
	    AlertDialog.Builder alt_bld = new AlertDialog.Builder(activity);
	    alt_bld.setMessage(str).setCancelable(false)
	    .setPositiveButton("확인",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int id) {
	        	dialog.cancel();
	        }
	        });
	    AlertDialog alert = alt_bld.create();
	    alert.setTitle("FollovApp");
	    alert.setIcon(R.drawable.alert);
	    alert.show();
	}
}
