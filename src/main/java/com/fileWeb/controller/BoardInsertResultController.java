package com.fileWeb.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BoardInsertResultController implements Controller {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws
	ServletException, IOException {

		Map<String, Object> reqMap = new HashMap<String, Object>();
		
		reqMap.put("usrID", req.getParameter("usrID"));
		reqMap.put("usrPasswd", req.getParameter("filename"));

        req.setAttribute("reqMap", reqMap);
        
		HttpUtil.forward(req, res, "/WEB-INF/view/board/insertResult.jsp");
		
	}

}
