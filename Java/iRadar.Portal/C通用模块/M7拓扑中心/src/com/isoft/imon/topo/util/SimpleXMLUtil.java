package com.isoft.imon.topo.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPath;
import org.xml.sax.InputSource;

/**
 * 简单xml辅助类
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("deprecation")
public final class SimpleXMLUtil {
	/**
	 * file转换成文档
	 * 
	 * @param xmlPath
	 * @return
	 */
	public static Document file2Doc(String xmlPath) {
		return file2Doc(xmlPath, false);
	}

	/**
	 * file转换成文档
	 * 
	 * @param xmlPath
	 * @param validate
	 * @return
	 */
	public static Document file2Doc(String xmlPath, boolean validate) {
		if (xmlPath == null) {
			throw new NullPointerException();
		}
		SAXBuilder builder = new SAXBuilder(validate);
		// Document doc = null;
		Document doc = new Document();// k9扫描bug修改
		try {
			doc = builder.build(new File(xmlPath.replace("%20", " ")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 文档转换成字符串
	 * 
	 * @param doc
	 * @return
	 */
	public static String doc2String(Document doc) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter pw = new PrintWriter(baos);
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			XMLOutputter xmlop = new XMLOutputter();
			xmlop.setFormat(format);
			xmlop.output(doc, pw);

			return baos.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 字符串转换成文档
	 * 
	 * @param xml
	 * @return
	 */
	public static Document string2Doc(String xml) {
		if (xml == null)
			return null;

		Document doc = null;
		try {
			StringReader sr = new StringReader(xml);
			InputSource is = new InputSource(sr);
			SAXBuilder builder = new SAXBuilder(false);
			doc = builder.build(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	/**
	 * 更新XML
	 * 
	 * @param xmlFullPath
	 * @param doc
	 */
	public static void updateXML(String xmlFullPath, Document doc) {
		FileOutputStream fos = null;
		try {
			Format format = Format.getCompactFormat();
			format.setEncoding("UTF-8");
			format.setIndent("\t");
			XMLOutputter serializer = new XMLOutputter(format);
			fos = new FileOutputStream(xmlFullPath.replace("%20", " "));
			serializer.output(doc, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取元素列表
	 * 
	 * @param root
	 * @param path
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List getElements(Element root, String path) {
		try {
			return XPath.selectNodes(root, path);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取元素
	 * 
	 * @param root
	 * @param path
	 * @return
	 */
	public static Element getElement(Element root, String path) {
		try {
			return (Element) XPath.selectSingleNode(root, path);
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return null;
	}
}