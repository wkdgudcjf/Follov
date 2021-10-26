package com.follov.action;

import java.io.*;

import javax.servlet.http.HttpServletRequest;

import com.follov.db.vo.*;

public class ActionUtil {
	public static final int EMAIl_CHECK_COMPLETION = 1;
	public static final int EMAIl_CHECK_DUPLCATED = 2;
	static String getInputString(HttpServletRequest request) throws IOException
	{
	    InputStream is = request.getInputStream();
	    if (is != null)
	    {
	        Writer writer = new StringWriter();

	        char[] buffer = new char[request.getContentLength()];
	       
	        try
	        {
	            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

	            int n;
	            while ((n = reader.read(buffer)) != -1)
	            {
	                writer.write(buffer, 0, n);
	            }
	            reader.close();
	        }
	        finally
	        {
	            is.close();
	        }
	        
	        String result = writer.toString();
	        
	        writer.close();
	        return result;
	    }
	    else
	    {
	        return "";
	    }
	}
	
	public static UnUploadedClientData getUnUploadedClientData(HttpServletRequest request) throws IOException
	{
		ObjectInputStream in = new ObjectInputStream(request.getInputStream());
		
		UnUploadedClientData unUploadedData = null;
		
		try{
			unUploadedData = (UnUploadedClientData) in.readObject();
			in.close();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		
		return unUploadedData;
	}
}
