package com.follov.listener;

import javax.servlet.*;

public class ContextListener implements ServletContextListener
{
	public void contextInitialized(ServletContextEvent event)
	{
/*		MemberManagement memberManagement = MemberManagement.getInstance();
		ScheduleManagement scheduleManagement = ScheduleManagement.getInstance();
		
		memberManagement.setMemberList(MemberDAOManager.getInstance().loadFromDB());
		scheduleManagement.setScheduleList(ScheduleDAOManager.getInstance().loadFromDB());
		memberManagement.setFriendManager(FriendDAOManager.getInstance().loadFromDB());
		scheduleManagement.setEntryManager(EntryDAOManager.getInstance().loadFromDB());
		scheduleManagement.setRequestManager(RequestDAOManager.getInstance().loadFromDB());
		scheduleManagement.setCommentManager(CommentDAOManager.getInstance().loadFromDB());
		memberManagement.setFriendRequestManager(FriendRequestDAOManager.getInstance().loadFromDB());
		memberManagement.setIdmap(GcmDAOManager.getInstance().loadFromDB());		
		
		
		ServletContext context=event.getServletContext();
		context.setAttribute("memberlist", memberManagement.getMemberList().getList());
		context.setAttribute("schedulelist", scheduleManagement.getScheduleList());
		context.setAttribute("friendmap", memberManagement.getFriendMap());
		context.setAttribute("entrymap", scheduleManagement.getEntryManager().getScheduleEntryByMemberId());
		context.setAttribute("entry2map", scheduleManagement.getEntryManager().getMemberEntryByScheduleNo());
		context.setAttribute("requestmap", scheduleManagement.getRequestManager().getRequestMap());
		context.setAttribute("commentmap", scheduleManagement.getCommentManager().getCommentMap());
		context.setAttribute("friendrequestmap", memberManagement.getFriendRequestManager().getRequestMap());
		context.setAttribute("gcmmap", memberManagement.getIdmap());*/
		
	}
	public void contextDestroyed(ServletContextEvent event)
	{
	/*	GcmDAOManager.getInstance().deleteAll();
		FriendRequestDAOManager.getInstance().deleteAll();
		CommentDAOManager.getInstance().deleteAll(); 
		RequestDAOManager.getInstance().deleteAll(); 
		EntryDAOManager.getInstance().deleteAll();
		ScheduleDAOManager.getInstance().deleteAll();
		FriendDAOManager.getInstance().deleteAll();
		MemberDAOManager.getInstance().deleteAll();
			
		MemberDAOManager.getInstance().saveToDB(MemberManagement.getInstance().getMemberList().getList());
		FriendDAOManager.getInstance().saveToDB(MemberManagement.getInstance().getFriendMap());	
		ScheduleDAOManager.getInstance().saveToDB(ScheduleManagement.getInstance().getScheduleList());
		EntryDAOManager.getInstance().saveToDB(ScheduleManagement.getInstance().getEntryManager().getMemberEntryByScheduleNo());
		RequestDAOManager.getInstance().saveToDB(ScheduleManagement.getInstance().getRequestManager().getRequestMap());
		CommentDAOManager.getInstance().saveToDB(ScheduleManagement.getInstance().getCommentManager().getCommentMap());
		FriendRequestDAOManager.getInstance().saveToDB(MemberManagement.getInstance().getFriendRequestManager().getRequestMap());
		GcmDAOManager.getInstance().saveToDB(MemberManagement.getInstance().getIdmap());*/
	}
}
