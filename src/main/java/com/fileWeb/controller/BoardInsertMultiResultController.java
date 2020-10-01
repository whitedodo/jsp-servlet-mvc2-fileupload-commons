package com.fileWeb.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fileWeb.util.HttpUtil;

public class BoardInsertMultiResultController implements Controller {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws
	ServletException, IOException {
		
		HttpUtil.uploadFile(req, res);
		HttpUtil.forward(req, res, "/WEB-INF/view/board/insertResult.jsp");
		
	}
	
}
