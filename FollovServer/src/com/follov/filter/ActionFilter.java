package com.follov.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.follov.action.*;


/**
 * Servlet Filter implementation class BrainMappingFilter
 */
public class ActionFilter implements Filter {

	private FilterConfig filterConfig = null;
    /**
     * Default constructor. 
     */
    public ActionFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		
		String action = filterConfig.getInitParameter("action");
		System.out.println("The action is: " + action);
		
		ServletAction sc = null;
		
		if(action.equals("locationAction")) sc = new LocationAction();
		else if(action.equals("SynchronizeAction")) sc = new SynchronizeAction();
		else if(action.equals("join")) sc = new UserJoinAction();
		else if(action.equals("checkemail")) sc = new CheckEmailAction();
		else if(action.equals("couplematching")) sc = new CoupleMatchingAction();
		else if(action.equals("couplematchingcancel")) sc = new CoupleMatchingCancelAction();
		else if(action.equals("profile")) sc = new profileAction();
		else if(action.equals("login")) sc = new UserLoginAction();
		else if(action.equals("logindata")) sc = new UserLoginDataAction();
		try {
			if(sc!=null)
			sc.execute((HttpServletRequest)request, (HttpServletResponse)response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
		this.filterConfig = fConfig;
	}
}
