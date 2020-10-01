package com.fileWeb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


public class HttpUtil extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static String charset = null;										// 문자열

	private static int SIZETHRESHOLD = 4096;
	private static String UPLOAD_FOLDER = "upload";
	private static String UPLOAD_TMP_FOLDER = File.separator + "WEB-INF" + File.separator + "temp";
	private static long MAX_UPLOAD_SIZE = 3 * 1024 * 1024;
	private static Map<String, Object> reqMap = null;							// req 정보(MultiRequest)
	private static Map<Integer, Map<String, Object>> fileMap = null; 			// 다중 파일 지원
	private static int num = 0;
	

	public static void forward(HttpServletRequest req, HttpServletResponse res,
			String path) throws ServletException, IOException {
		
		try {
			RequestDispatcher dispatcher = req.getRequestDispatcher(path);
			
			dispatcher.forward(req, res);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void uploadFile(HttpServletRequest req, HttpServletResponse res) throws
		ServletException, IOException {

		fileMap = new HashMap<Integer, Map<String, Object>>(); 
		reqMap = new HashMap<>();
		num = 1;
        
        PrintWriter out = res.getWriter();
        out.println("<HTML><HEAD><TITLE>Multipart Test</TITLE></HEAD><BODY>");
		
		try {
            
            //디스크상의 프로젝트 실제 경로얻기
            //String contextRootPath = "c:" + File.separator + "upload";
			String dirName = UPLOAD_FOLDER ; 
			// String dirName = "upload"; 
			String contextRootPath = req.getSession().getServletContext().getRealPath("/") + dirName;
						
            System.out.println("실제경로:" + contextRootPath);
            
            //1. 메모리나 파일로 업로드 파일 보관하는 FileItem의 Factory 설정
            DiskFileItemFactory diskFactory = new DiskFileItemFactory(); // 디스크 파일 아이템 공장
            diskFactory.setSizeThreshold(SIZETHRESHOLD); 				 // 업로드시 사용할 임시 메모리
            // diskFactory.setSizeThreshold(4096); 						 // 업로드시 사용할 임시 메모리
            diskFactory.setRepository(new File(contextRootPath + UPLOAD_TMP_FOLDER)); 		// 임시저장폴더
            // diskFactory.setRepository(new File(contextRootPath + UPLOAD_TMP_FOLDER));	// 임시저장폴더
            
            //2. 업로드 요청을 처리하는 ServletFileUpload생성
            ServletFileUpload upload = new ServletFileUpload(diskFactory);

            // upload.setSizeMax(3 * 1024 * 1024); //3MB : 전체 최대 업로드 파일 크기
            upload.setSizeMax(MAX_UPLOAD_SIZE); 		// 전체 최대 업로드 파일 크기
            
            //3. 업로드 요청파싱해서 FileItem 목록구함​​
            List<FileItem> items = upload.parseRequest(req); 

            Iterator<FileItem> iter = items.iterator(); //반복자(Iterator)로 받기​            
            while(iter.hasNext()) { //반목문으로 처리​    
                FileItem item = (FileItem) iter.next(); //아이템 얻기
                 //4. FileItem이 폼 입력 항목인지 여부에 따라 알맞은 처리
                
                if(item.isFormField()){ 
                	//파일이 아닌경우
                    processFormField(out, item);
                    
                } else {
                	//파일인 경우
                	System.out.println("오류:" + item.getName());
                	
                	// 버그 개선 item 이름값 비어있을 때
                	if ( item.getName() != "") {
                		processUploadFile(out, item, contextRootPath);
                	}
                	System.out.println("오류2:");
                }
            }
            
        } catch(Exception e) {
            out.println("<PRE>");
            e.printStackTrace(out);
            out.println("</PRE>");
        }
		
		out.println( "usrID(Map): " + reqMap.get("usrID") );
		out.println( "usrPasswd(Map):" + reqMap.get("usrPasswd") );
        
        out.println("</BODY></HTML>");
		
		// req.setAttribute("usrID", reqMap.get("usrID"));
		// req.setAttribute("login", 1);//Object Type으로 넘어감
        req.setAttribute("reqMap", reqMap);
        req.setAttribute("fileMap", fileMap);
        
        // 방법3
        for( Integer key : fileMap.keySet() ){

            Map<String, Object> fileMapNode = fileMap.get(key);
            System.out.println( String.format("키 : %s, 값: %s", key, fileMapNode.get("fileName") ));
            
        }
        
		// System.out.println("오류3:" + reqMap.get("usrID"));
		
	}

	//업로드한 정보가 파일인경우 처리
	private static void processUploadFile(PrintWriter out, FileItem item, String contextRootPath)
			throws Exception {
		
		Map<String, Object> fileNode = new HashMap<String, Object>();

		String dirName = UPLOAD_FOLDER ; 
		String name = item.getFieldName(); 					// 파일의 필드 이름 얻기
		String fileName = item.getName();					 	// 파일명 얻기
		
		// 임시 - 실제 원본 이름 추출
		File originalFile = new File(fileName);
		String originalFileName = originalFile.getName();
		
		System.out.println("임시:" + originalFileName );
		
		String contentType = item.getContentType();		// 컨텐츠 타입 얻기
		long fileSize = item.getSize(); 								// 파일의 크기 얻기
		
		// 업로드 파일명을 현재시간으로 변경후 저장
		String fileExt = fileName.substring(fileName.lastIndexOf("."));
		String uploadedFileName = System.currentTimeMillis() + ""; 
		System.out.println(fileExt);
		System.out.println(uploadedFileName);
		
		// 저장할 절대 경로로 파일 객체 생성
		String realUploadFile = File.separator + dirName + File.separator + uploadedFileName;
		System.out.println("실제 저장직전폴더:" + contextRootPath + realUploadFile);
		File uploadedFile = new File(contextRootPath + realUploadFile);
		item.write(uploadedFile); //파일 저장
		
		//========== 뷰단에 출력 =========//
		out.println("<P>");
		out.println("파라미터 이름:" + name + "<BR>");
		out.println("파일 이름:" + fileName + "<BR>");
		out.println("콘텐츠 타입:" + contentType + "<BR>");
		out.println("파일 사이즈:" + fileSize + "<BR>");
		
		//확장자가 이미지인겨우 이미지 출력
		if(".jpg.jpeg.bmp.png.gif".contains(fileExt.toLowerCase())) {
			out.println("<IMG SRC='upload/" 
					+ uploadedFileName 
					+ "' width='300'><BR>");
		}
		
		out.println("</P>");
		out.println("<HR>");
		out.println("실제저장경로 : "+uploadedFile.getPath()+"<BR>");
		out.println("<HR>");
		
		// 파일 정보
		fileNode.put("name", name);
		fileNode.put("fileName", originalFileName);
		fileNode.put("contentType", contentType);
		fileNode.put("fileSize", fileSize);
		fileNode.put("fileExt", fileExt);
		fileNode.put("uploadedFileName", uploadedFileName);
		fileNode.put("realName", uploadedFile.getName());
		fileNode.put("realPath", uploadedFile.getPath());
		
		fileMap.put(num, fileNode);
		
		num++;
		
	}
	
	private static void processFormField(PrintWriter out, FileItem item) 
		throws Exception{
		
		String name = item.getFieldName(); //필드명 얻기
		Object value = item.getString("UTF-8"); //UTF-8형식으로 필드에 대한 값읽기
		
		// out.println(name + ":" + value + "<BR>"); //출력
		reqMap.put(name, value);
		
	}
	
	/*
	 * 다운로드(Download)
	 * 
	 */
	public static void fileDownload(HttpServletRequest req, HttpServletResponse res,
			String path) throws ServletException, IOException {

		charset = (String) req.getAttribute("charset");
		
		System.out.println(charset);
		
		PrintWriter out = res.getWriter();
		
		// 파일 업로드된 경로
		String root = req.getSession().getServletContext().getRealPath("/");
		String savePath = root + UPLOAD_FOLDER + File.separator + UPLOAD_FOLDER ;
		// String savePath = root + "upload" + File.separator + "upload";
		
		// 서버에 실제 저장된 파일명
		String filename = "1601561525229" ;
		
		System.out.println("파일 실제 폴더경로:" + savePath);
		System.out.println("실제 파일명:" + filename);
		
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
			// e.printStackTrace();
			
			System.out.println("오류:" + e.getMessage());
			
		}
		    
	}
	

	/*
	 *  파일 삭제, 폴더 삭제
	 */
	public static void removeFile(HttpServletRequest req, HttpServletResponse res) 
			throws ServletException, IOException{
		

		// 파일 업로드된 경로
		String root = req.getSession().getServletContext().getRealPath("/");
		String savePath = root + UPLOAD_FOLDER + File.separator + UPLOAD_FOLDER ;
		
		String filename = "1601561525229" ;

        File file = new File(savePath, filename);
		
        // 파일이 존재할 떄
        if (file.exists()) {
        	file.delete();
        }
        
        removeDirectory(req, res, savePath);
		
	}
	
	/*
	 * 
	 */
	private static boolean removeDirectory(HttpServletRequest req, HttpServletResponse res,
			String path) throws ServletException, IOException {
	
		boolean result = false;
		
		File usrDir = new File(path);
		
		if(!usrDir.exists()) {                 		// 경로 존재 여부
            result = false;     	
        }
		else {
		
	        File[] lowFiles = usrDir.listFiles();     	// 경로 내의 파일 리스트
	        
	        // 폴더 삭제
	        if ( usrDir.isDirectory() 
	        		&& lowFiles.length == 0 ) {
	    		
	        	System.out.println("폴더 삭제처리 완료");
	        	usrDir.delete();
	        	
	        	return true;
	        	
	    	}else{
	    		result = false;
	    	}
        
        }
        
        return result;
		
	}

}
