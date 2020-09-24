package com.fileWeb.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class BoardInsertMultiResultController implements Controller {

	private Map<String, Object> reqMap = null;										// req 정보(MultiRequest)
	private Map<Integer, Map<String, Object>> fileMap = null; 				// 다중 파일 지원
	private static int num = 0; 
	
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws
	ServletException, IOException {
		
		fileMap = new HashMap<Integer, Map<String, Object>>(); 
		reqMap = new HashMap<>();
		num = 1;
        
        PrintWriter out = res.getWriter();
        out.println("<HTML><HEAD><TITLE>Multipart Test</TITLE></HEAD><BODY>");
		
		try {
            
            //디스크상의 프로젝트 실제 경로얻기
            //String contextRootPath = "c:" + File.separator + "upload";
			String dirName = "upload" ; 
			String contextRootPath = req.getSession().getServletContext().getRealPath("/") + dirName;
						
            System.out.println("실제경로:" + contextRootPath);
            
            //1. 메모리나 파일로 업로드 파일 보관하는 FileItem의 Factory 설정
            DiskFileItemFactory diskFactory = new DiskFileItemFactory(); //디스크 파일 아이템 공장
            diskFactory.setSizeThreshold(4096); //업로드시 사용할 임시 메모리
            diskFactory.setRepository(new File(contextRootPath + "/WEB-INF/temp")); //임시저장폴더
            
            //2. 업로드 요청을 처리하는 ServletFileUpload생성
            ServletFileUpload upload = new ServletFileUpload(diskFactory);
            upload.setSizeMax(3 * 1024 * 1024); //3MB : 전체 최대 업로드 파일 크기
            
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
                	// System.out.println("오류:");
                    processUploadFile(out, item, contextRootPath);
                	// System.out.println("오류2:");
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
		HttpUtil.forward(req, res, "/WEB-INF/view/board/insertResult.jsp");
		
	}
	

	//업로드한 정보가 파일인경우 처리
	private void processUploadFile(	PrintWriter out, FileItem item, String contextRootPath)
			throws Exception {
		
		Map<String, Object> fileNode = new HashMap<String, Object>();
				
		String name = item.getFieldName(); 					// 파일의 필드 이름 얻기
		String fileName = item.getName();					 	// 파일명 얻기
		
		// 임시 - 실제 원본 이름 추출
		File originalFile = new File(fileName);
		String originalFileName = originalFile.getName();
		
		// System.out.println("임시:" + originalFileName );
		
		String contentType = item.getContentType();		// 컨텐츠 타입 얻기
		long fileSize = item.getSize(); 								// 파일의 크기 얻기
		
		// 업로드 파일명을 현재시간으로 변경후 저장
		String fileExt = fileName.substring(fileName.lastIndexOf("."));
		String uploadedFileName = System.currentTimeMillis() + ""; 
		System.out.println(fileExt);
		System.out.println(uploadedFileName);
		
		// 저장할 절대 경로로 파일 객체 생성
		File uploadedFile = new File(contextRootPath + "/upload/" + uploadedFileName);
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
	
	private void processFormField(PrintWriter out, FileItem item) 
		throws Exception{
		
		String name = item.getFieldName(); //필드명 얻기
		Object value = item.getString("UTF-8"); //UTF-8형식으로 필드에 대한 값읽기
		
		// out.println(name + ":" + value + "<BR>"); //출력
		reqMap.put(name, value);
		
	}

}
