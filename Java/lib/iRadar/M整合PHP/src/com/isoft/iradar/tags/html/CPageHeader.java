package com.isoft.iradar.tags.html;

import static com.isoft.Feature.originalStyle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.isoft.iradar.Cphp;
import com.isoft.iradar.RadarContext;

public class CPageHeader {

	protected String title;
	protected List<String> cssFiles = new ArrayList<String>();
	protected List<String> styles = new ArrayList<String>();
	protected List<String> jsFiles = new ArrayList<String>();
	protected List<String> jsBefore = new ArrayList<String>();
	protected List<String> js = new ArrayList<String>();

	public CPageHeader(String title) {
		if (title == null) {
			title = "";
		}
		this.title = title;
	}

	/**
	 * Add path to css file to render in page head.
	 */
	public void addCssFile(String path) {
		this.cssFiles.add(path);
	}
	
	/**
	 * Add initial css files.
	 */
	public void addCssInit() {
		this.cssFiles.add("styles/default.css");
		this.cssFiles.add("styles/color.css");
		this.cssFiles.add("styles/icon.css");
		this.cssFiles.add("styles/blocks.css");
		this.cssFiles.add("styles/pages.css");
		if (!originalStyle) {
			this.cssFiles.add("styles/icon.isoft.css");
		}
	}
	
	/**
	 * Add css style to render in page head.
	 */
	public void addStyle(String style) {
		this.styles.add(style);
	}
	
	/**
	 * Add path to js file to render in page head.
	 */
	public void addJsFile(String path) {
		this.jsFiles.add(path);
	}
	
	/**
	 * Add js script to render in page head after js file includes are rendered.
	 */
	public void addJs(String js) {
		this.js.add(js);
	}
	
	/**
	 * Add js script to render in page head before js file includes are rendered.
	 */
	public void addJsBeforeScripts(String js) {
		this.jsBefore.add(js);
	}
	
	public void display(){
		RadarContext ctx = RadarContext.getContext();
		String ctxPath = RadarContext.getContextPath();
		if (ctxPath.length() == 0) {
			ctxPath = "";
		}
		try {
			PrintWriter out = ctx.getResponse().getWriter();
			out.write("<!doctype html>\n");
			out.write("<html>\n");
			out.write("<head>\n");
			out.write("<title>"+this.title+"</title>\n");
			out.write("<meta name=\"renderer\" content=\"webkit\" />\n");	//让360浏览器使用chrome内核
			out.write("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\"/>\n");
			out.write("<meta name=\"Author\" content=\"i-soft\" />\n");
			out.write("<meta charset=\"utf-8\" />\n");
			out.write("<link rel=\"shortcut icon\" href=\""+ctxPath+"/favicon.ico\" />\n");
			
			for(String f:this.cssFiles){
				out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\""+f+"\" />\n");
			}

			if(!this.styles.isEmpty()){
				out.write("<style type=\"text/css\">\n");
				out.write(Cphp.implode("\n", this.styles.toArray()));
				out.write("</style>\n");
			}
			
			if(!this.jsBefore.isEmpty()){
				out.write("<script>\n");
				out.write(Cphp.implode("\n", this.jsBefore.toArray()));
				out.write("</script>\n");
			}
			
			for(String f:this.jsFiles){
				out.write("<script src=\""+f+"\"></script>\n");
			}
			
			if(!this.js.isEmpty()){
				out.write("<script>\n");
				out.write(Cphp.implode("\n", this.js.toArray()));
				out.write("</script>\n");
			}
			out.write("</head>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
