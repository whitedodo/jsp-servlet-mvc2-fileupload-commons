package com.fileWeb.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class HomeController
 */
public class FrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private String charset = null;
	
    public FrontController() {
        super();
    }

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doAction(req, res);
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doAction(req, res);
	}
	
	// FrontController 패턴 & Command 패턴
	protected void doAction(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		ServletConfig sc = this.getServletConfig();
		charset = sc.getInitParameter("charset");
		
		req.setAttribute("charset", charset);
		req.setCharacterEncoding(charset);
		res.setContentType("text/html; charset=" + charset);
		
		System.out.println(charset);
		
		String uri = req.getRequestURI();
		System.out.println("uri : " + uri);
		String conPath = req.getContextPath();
		System.out.println("conPath : " + conPath);
		String command = uri.substring(conPath.length());
		System.out.println("command : " + command);
		Controller subController = null;
		
		System.out.println("reqMapSize : " + req.getParameterMap().size());

		if(command.equals("/board/insert.do")){
			System.out.println("insert");
			System.out.println("----------------");

	    	subController = new BoardInsertController();
	    	subController.execute(req, res);
	    	
		}else if (command.equals("/board/insertResult.do")) {
			System.out.println("insertResult");
			System.out.println("----------------");

	    	subController = new BoardInsertResultController();
			subController.execute(req, res);
		}
		else if(command.equals("/board/insertMultiResult.do")){
			System.out.println("insertResult");
			System.out.println("----------------");

	    	subController = new BoardInsertMultiResultController();
			subController.execute(req, res);
			
		}else if(command.equals("/board/download.do")) {
			System.out.println("download");
			System.out.println("----------------");
			
	    	HttpUtil.fileUpload(req, res, null);
			
		}else if(command.equals("/board/update.do")){
			System.out.println("update");
			System.out.println("----------------");
		}else if(command.equals("/board/select.do")){
			System.out.println("select");
			System.out.println("----------------");
		}else if(command.equals("/board/delete.do")){
			System.out.println("delete");
			System.out.println("----------------");
		}
		
	}
	
}
