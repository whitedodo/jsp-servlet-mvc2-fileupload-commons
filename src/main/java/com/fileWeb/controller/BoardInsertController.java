package com.fileWeb.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BoardInsertController implements Controller {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse res) throws
	ServletException, IOException {
		
		HttpUtil.forward(req, res, "/WEB-INF/view/board/insert.jsp");
		
	}

}
