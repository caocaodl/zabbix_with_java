package com.isoft.i18n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {

	public static void main(String[] args) throws Exception {
		String file = "M国际化/locale/en_US/LC_MESSAGES/frontend.po";
		//String file = "M国际化/locale/zh_CN/LC_MESSAGES/frontend.po";
		Translate translate = new Translate();
		translate.doLocaleBundle(file);
	}

	private void doLocaleBundle(String file) throws Exception {
		File res = new File(file);
		InputStream is = new FileInputStream(res);
		Reader reader = new InputStreamReader(is);
		BufferedReader bufReader = new BufferedReader(reader);
		String line = null;
		Msg msg = null;
		int n =0;
		Pattern PATTERN = Pattern.compile("^.+ \"(.*)\"[\r\n]*$");
		Matcher matcher = null;
		List<Msg> msgList = new ArrayList();
		while ((line = bufReader.readLine()) != null) {
			if (line.startsWith("msgid ")) {
				msg = new Msg(++n);
				matcher = PATTERN.matcher(line);
				if (matcher.find()) {
					msg.id = matcher.group(1);
				}
				msgList.add(msg);
			} else if (line.startsWith("msgid_plural ")) {
				matcher = PATTERN.matcher(line);
				if (matcher.find()) {
					msg.ids = matcher.group(1);
				}
			} else if (line.startsWith("msgstr ")) {
				matcher = PATTERN.matcher(line);
				if (matcher.find()) {
					msg.msg = matcher.group(1);
				}
			} else if (line.startsWith("msgstr[0] ")) {
				msg.msg = matcher.group(1);
				msg.msgs = matcher.group(1);
			} else if (line.startsWith("msgstr[1] ")) {
				msg.msgs = matcher.group(1);
			}
		}

		bufReader.close();
		reader.close();
		is.close();
		
		doSaveBundler(msgList);
	}

	private void doSaveBundler(List<Msg> msgList) throws Exception {
		String file = "M整合PHP/src/frontend.xml";
		File res = new File(file);
		System.out.println(res.getAbsolutePath());
		FileWriter out = new FileWriter(res);
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		out.write("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">\n");
		out.write("<properties>\n");
		out.write("<comment>(frondend.po)</comment>\n");
		for (Msg msg : msgList) {
			if (msg.id != null && msg.id.length() > 0) {
				out.write(msg.toString());
				out.write("\n");
			}
		}
		out.write("</properties>");
		out.flush();
		out.close();
		System.out.println("Over...");
	}

	class Msg {
		private Msg(int index) {
			this.index = index;
		}

		private int index;
		private String id;
		private String ids;
		private String msg;
		private String msgs;
		
		private String translateKey(String key){
			return key.replaceAll("&", "&amp;").replaceAll("\\\\\"", "&quot;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		
		private String translateValue(String value){
			return value.replaceAll("\\\\\"", "\"");
		}

		@Override
		public String toString() {
			this.id = translateKey(this.id);
			this.msg = translateValue(this.msg);
			StringBuilder str = new StringBuilder();
			str.append("<!-- " + this.index + " -->\n");
			str.append("<entry key=\""+this.id+"\"><![CDATA["+this.msg+"]]></entry>\n");
			if (ids != null && ids.length() > 0) {
				this.ids = translateKey(this.ids);
				this.msgs = translateValue(this.msgs);
				str.append("<entry key=\""+this.ids+"\"><![CDATA["+this.msgs+"]]></entry>\n");
			}
			return str.toString();
		}

	}
}
