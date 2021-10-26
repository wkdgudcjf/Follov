package com.follov.servlet;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.follov.action.FollovCode;
import com.follov.daoManager.PhotoManager;
import com.follov.daoManager.UserManager;
import com.follov.db.DBManager;
import com.follov.jdbc.dao.*;

/**
 * Servlet implementation class ImageServlet
 */
public class ImageServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ImageServlet() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("이미지 하나 왓다.");
		String date=null;
		String date_code = null;
		String loc_no=null;
		String name=null;
		String email=null;
		String pw=null;
		String coupleid=null;
		
		OutputStream out = response.getOutputStream();
		DataOutputStream dataOut = new DataOutputStream(out);

		request.setCharacterEncoding("euc-kr");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (!isMultipart) 
		{

		}
		else
		{
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List items = null;
			try
			{
				items = upload.parseRequest(request);
			} catch (FileUploadException e)
			{
				e.printStackTrace();
			}
			Iterator itr = items.iterator();
			while (itr.hasNext())
			{
				FileItem item = (FileItem) itr.next();
				if (item.isFormField()) { // 파일이 아닌 폼필드에 입력한 내용을 가져옴.
					if(item!=null && item.getFieldName().equals("email")) {
						email = item.getString("KSC5601");
						System.out.println("email:"+email);
					}
					else if(item!=null && item.getFieldName().equals("password")) {
						pw = item.getString("KSC5601");
						System.out.println("password:"+pw);
					}
					else if(item!=null && item.getFieldName().equals("coupleid")) {
						coupleid = item.getString("KSC5601");
						System.out.println("coupleid:"+coupleid);
					}
					else if(item!=null && item.getFieldName().equals("name")) {
						name = item.getString("KSC5601");
						System.out.println("name:"+name);
					}
				}
				else
				{ // 폼 필드가 아니고 파일인 경우

					Connection conn = DBManager.getInstance().dbConn();
					if(checkPassword(conn, email, pw)){

						try {
							String itemName = item.getName();//로컬 시스템 상의 파일경로 및 파일 이름 포함
							if(itemName==null || itemName.equals("")) continue;
							String fileName = FilenameUtils.getName(itemName);// 경로없이 파일이름만 추출함

							File f = checkExist(fileName,coupleid);
							item.write(f);// 지정 경로에 파일을 저장함
							try{
								//updatePhtoToDB
								if(Loc_photo_tb_DAOImpl.getInstance().updateLoc_photo(conn, coupleid, name, "y"))
								{
									if(DBManager.DEBUG_MODE)
										System.out.println("PHOTO_SAVED_SUCESS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
									dataOut.writeInt(FollovCode.PHOTO_SAVED_SUCESS);
								}
								else
								{
									response.getOutputStream().write(FollovCode.PHOTO_SAVED_FAILED);
									if(DBManager.DEBUG_MODE)
										System.out.println("PHOTO_SAVED_FAILED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
								}
							}catch(IOException e){
								e.printStackTrace();
							}
						}
						catch (Exception e) 
						{
							dataOut.writeInt(FollovCode.PHOTO_SAVED_FAILED);
							e.printStackTrace();
						}

						
					}else{
						System.out.println("포토 저장 이메일과 비밀번호가 일치하지 않습니다.");
						dataOut.writeInt(FollovCode.PHOTO_SAVED_FAILED);
					}

				} // end of else
			}  // end of while
		}   // end of else

	}  // end of doPost

	private boolean checkPassword(Connection conn, String email, String pw)
	{
		if(UserManager.getInstance().checkPassword(conn, email, pw))
		{
			return true;
		}
		if(DBManager.DEBUG_MODE)
			System.out.println("insertDate, 비빌번호가 일치하지 않습니다.");
		return false;
	}

	/* 이미 동일이름의 파일이 존재하면 새로 저장되는 파일의 이름 변경 */
	private File checkExist(String fileName,String couple_id)
	{
		String contentDirectory =null;
		contentDirectory = "C:\\Users\\JHC\\workspace2\\FollovServer\\WebContent\\upload";
		contentDirectory +="\\"+couple_id+"\\";
		//test
		System.out.println(contentDirectory);
		File f = new File(contentDirectory);
		if(!f.exists())
		{
			f.mkdirs();
		}
		f = new File(contentDirectory+fileName);
		if(f.exists())
		{
			System.out.println(contentDirectory+"여기왓냐?");
			StringBuffer sb = new StringBuffer(fileName);
			sb.insert(sb.lastIndexOf("."),"a");
			f = new File(contentDirectory+sb.toString());
		}
		return f;
	}
}
/*	response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("utf-8"); 
		String contentDirectory = getServletContext().getRealPath("upload");
		String paramName[] = new String [10] ; 
		 int maxSize=10*1024*1024;
		// MultipartParser를 이용하여 parameter를 얻어온다! 
		MultipartParser mp = new MultipartParser(request, maxSize) ; 
		Part part ; 
		int i=0;
		String date = null;
		String datenum = null;
		String locatenum = null;
		String coupleid = null;
		while ((part = mp.readNextPart())!= null )
		{ 
			paramName[i] = part.getName() ; 
			if ( part.isParam() )
			{ 
				if(i<6)
				{
					ParamPart paramPart = (ParamPart) part ; 
					String value = paramPart.getStringValue() ; 
					if ( paramName[i].equals("date") )
					{
						date = value;
					// 적절한 파라미터 값을 얻는다. 
					} 
					if ( paramName[i].equals("coupleid") )
					{
						coupleid = value;
					// 적절한 파라미터 값을 얻는다. 
					} 
					else if ( paramName[i].equals("datenum") ) 
					{ 
						datenum=value;
					// 적절한 파라미터 값을 얻는다. 
					}
					else if ( paramName[i].equals("locatenum") ) 
					{ 
						locatenum=value;
					// 적절한 파라미터 값을 얻는다. 
					} 
					System.out.println("param[" + i + "] : name = " + paramName[i] + "; value = " + value) ; 
				}
				else
				{
					/*File saveDirectory = new File(contentDirectory+"/"+coupleid+"/"+date+"/"+datenum+"/"+locatenum) ; 

					if ( !saveDirectory.exists() )
					{ 
						// Directory Creating... 
						saveDirectory.mkdir() ; 
					} 

					// It's a file part 
					FilePart filePart = (FilePart)part ; 
					String fileName = filePart.getFileName() ; 

					if ( fileName != null ) 
					{ 
						System.out.println("File name : " + fileName ) ; 
						// the part actually contained a file 
						long size = filePart.writeTo(saveDirectory) ; 

						System.out.println("File size : " + size) ; 
					} 
					MultipartRequest multi = null;
					  try
					  {
						  //File saveDirectory = new File(contentDirectory+"\\"+coupleid+"\\"+date+"\\"+datenum+"\\"+locatenum+"\\"+"test.txt") ; 
						 // if ( !saveDirectory.exists() )
						 // { 
						//	  System.out.println("이거하니?");
						//	  // Directory Creating... 
						//	  saveDirectory.mkdir() ; 
						//  } 
						//  String path = contentDirectory+"\\"+coupleid+"\\"+date+"\\"+datenum+"\\"+locatenum;
						  multi = new MultipartRequest(request,contentDirectory , maxSize, "euc-kr", new DefaultFileRenamePolicy());
				  }
				  catch (Exception e)
				  {
					   e.printStackTrace();

				   }
				}
			}
			else
			{ 
				System.out.println("It's not post method or parameter error") ; 
			} 
			i++ ; 
		}
		System.out.println(i);
		response.getOutputStream().write(65);
		response.setContentType("text/html;charset=UTF-8");
		request.setCharacterEncoding("utf-8"); 
		String folderTypePath = getServletContext().getRealPath("upload");
		System.out.println(folderTypePath);
		 System.out.println("post message");
		  MultipartRequest multi = null;
		  try
		  {

		   int maxSize=10*1024*1024;
		   multi = new MultipartRequest(request, folderTypePath, maxSize, "UTF-8", new DefaultFileRenamePolicy());
		   response.getOutputStream().write(1);
		  }
	  catch (Exception e) {
		   e.printStackTrace();

		  }*/

