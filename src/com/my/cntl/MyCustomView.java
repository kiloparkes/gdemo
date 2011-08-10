package com.my.cntl;

import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

public class MyCustomView implements View {

	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}

	public void render(Map model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("text/plain");
		response.addHeader("Content-disposition", "attachment; filename=output.txt");
		
		PrintWriter writer = response.getWriter();
		for (Iterator k = model.keySet().iterator(); k.hasNext();) {
		Object key = k.next();
		writer.print(key);
		writer.println(" contains:");
		writer.println(model.get(key));
		}
	}

}
