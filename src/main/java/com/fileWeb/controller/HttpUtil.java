package com.fileWeb.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HttpUtil extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static String charset = null;

	public static void forward(HttpServletRequest req, HttpServletResponse res,
			String path) throws ServletException, IOException {
		
		try {
			RequestDispatcher dispatcher = req.getRequestDispatcher(path);
			
			dispatcher.forward(req, res);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void fileUpload(HttpServletRequest req, HttpServletResponse res,
			String path) throws ServletException, IOException {

		charset = (String) req.getAttribute("charset");
		
		System.out.println(charset);
		
		PrintWriter out = res.getWriter();
		
		// 파일 업로드된 경로
		String root = req.getSession().getServletContext().getRealPath("/");
		String savePath = root + "upload" + File.separator + "upload";
		
		// 서버에 실제 저장된 파일명
		String filename = "1600955663095" ;
		
		System.out.println("파일 실제 폴더경로:" + savePath);
		
		// 실제 내보낼 파일명
		
		String orgfilename = "license한글.txt" ;
		
		req.setCharacterEncoding(charset);
		res.setCharacterEncoding(charset);
		
		InputStream in = null;
		OutputStream os = null;
		
		File file = null;
		boolean skip = false;
		String client = "";
		
		try{
		
		    // 파일을 읽어 스트림에 담기
		    try{
		        file = new File(savePath, filename);
		        in = new FileInputStream(file);
		
		    }catch(FileNotFoundException fe){
		
		        skip = true;
		    }
		
		    client = req.getHeader("User-Agent");
		
		    // 파일 다운로드 헤더 지정
		    res.reset() ;
		    res.setContentType("application/octet-stream");
		    res.setHeader("Content-Description", "JSP Generated Data");
		
		    if(!skip){
		
		        // IE
		        if(client.indexOf("MSIE") != -1){
		            res.setHeader ("Content-Disposition", "attachment; filename="+new String(orgfilename.getBytes("KSC5601"),"ISO8859_1"));
		
		        }else{
		
		            // 한글 파일명 처리
		            orgfilename = new String(orgfilename.getBytes("KSC5601"),"iso-8859-1");
		
		            res.setHeader("Content-Disposition", "attachment; filename=\"" + orgfilename + "\"");
		            res.setHeader("Content-Type", "application/octet-stream; charset=utf-8");
		        }  
		
		        res.setHeader ("Content-Length", ""+file.length() );
		
		        os = res.getOutputStream();
		        
		        byte b[] = new byte[(int)file.length()];
		        int leng = 0;
		
		        while( (leng = in.read(b)) > 0 ){
		            os.write(b,0,leng);
		        }
		
		    }else{
		    	// 한글 깨짐 - 해결
		    	res.setContentType("text/html;charset=" + charset);
		        out.println("<html><head>");
		        out.println("<script language='javascript'>alert('파일을 찾을 수 없습니다.');history.back();</script>");
		        out.println("</head><body></body></html>");
		
		    }
		
		    in.close();
		    os.close();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		    
	}

}
