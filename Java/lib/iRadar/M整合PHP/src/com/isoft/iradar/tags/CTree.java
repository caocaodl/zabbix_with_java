package com.isoft.iradar.tags;

import static com.isoft.iradar.RadarContext.getContextPath;
import static com.isoft.iradar.RadarContext.request;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.isoft.model.TreeAsyncItem;
import com.isoft.model.TreeNodeItem;
import com.isoft.server.RunParams;
import com.isoft.struts2.views.tags.HtmlResponseWriter;

public class CTree extends CTag {

	private static final long serialVersionUID = 1L;

	private final static String ATTR_CONTAINER = "_container_";

	protected HttpServletRequest request;

	private String id;

	private List<TreeNodeItem> treeNodeList = null;
	
	public CTree(String id, List<TreeNodeItem> treeNodeList) {
		this.id = id;
		this.treeNodeList = treeNodeList;
		this.request = request();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private TreeAsyncItem asyncItem;

	public void pushTreeAsyncItem(TreeAsyncItem asyncItem) {
		this.asyncItem = asyncItem;
	}

	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		linkCss(writer, "/assets/f/import/jquery.ztree/zTreeStyle.css");
		linkJavaScript(writer,"/assets/f/import/jquery.ztree/jquery.ztree.all-3.5.min.js");
		return true;
	}

	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		writer.write("<div class='zTreeDemoBackground left'>");
		writer.write("	<ul id='" + getId() + "' class='ztree'></ul>");
		writer.write("</div>");

		writer.write("<script type='text/javascript'>");
		writer.write("var setting = {");
		writer.write("	check: {");
		writer.write("		enable: true");
		writer.write("	},");

		encodeTreeSettingAsync(writer);

		writer.write("	data: {");
		writer.write("		simpleData: {");
		writer.write("			enable: true");
		writer.write("		}");
		writer.write("	}");
		writer.write("};");

		writer.write("var zNodes =[\n");
		boolean first = true;
		if (treeNodeList != null && !treeNodeList.isEmpty()) {
			for (TreeNodeItem node : treeNodeList) {
				if (!first) {
					writer.write(",\n");
				} else {
					first = false;
				}
				encodeTreeNode(writer, node);
			}
		}
		writer.write("\n];");

		writer.write("jQuery(function(){");
		writer.write("jQuery.fn.zTree.init(jQuery('#" + getId() + "'), setting, zNodes);");
		writer.write("});");
		writer.write("</script>");
		return false;
	}

	private void encodeTreeSettingAsync(HtmlResponseWriter writer)
			throws IOException {
		if (asyncItem != null) {
			writer.write("	async: {\n");
			if (isNotEmpty(asyncItem.getAutoParam())) {
				writer.write("autoParam:" + asyncItem.getAutoParam() + ",\n");
			}
			if (isNotEmpty(asyncItem.getContentType())) {
				writer.write("contentType:\"" + asyncItem.getContentType()
						+ "\",\n");
			}
			if (isNotEmpty(asyncItem.getDataFilter())) {
				writer.write("dataFilter:" + asyncItem.getDataFilter() + ",\n");
			}
			if (isNotEmpty(asyncItem.getDataType())) {
				writer.write("dataType:\"" + asyncItem.getDataType() + "\",\n");
			}
			if (isNotEmpty(asyncItem.getOtherParam())) {
				writer.write("otherParam:" + asyncItem.getOtherParam() + ",\n");
			}
			if (isNotEmpty(asyncItem.getType())) {
				writer.write("type:\"" + asyncItem.getType() + "\",\n");
			}
			if (isNotEmpty(asyncItem.getUrl())) {
				writer.write("url:\"" + getContextPath() + asyncItem.getUrl()
						+ "\",\n");
			}
			writer.write("enable:" + asyncItem.getEnable());
			writer.write("},\n");
		}
	}

	private void encodeTreeNode(HtmlResponseWriter writer, TreeNodeItem node)
			throws IOException {
		String ctx = getContextPath();
		writer.write("{");
		writer.write("id:\"" + node.getId() + "\"");
		if (isNotEmpty(node.getpId())) {
			writer.write(", pId:\"" + node.getpId() + "\"");
		}
		writer.write(", name:\"" + node.getName() + "\"");

		if (node.getChecked() != null) {
			writer.write(", checked:" + node.getChecked());
		}
		if (node.getChkDisabled() != null) {
			writer.write(", chkDisabled:" + node.getChkDisabled());
		}
		if (isNotEmpty(node.getClick())) {
			writer.write(", click:\"" + node.getClick() + "\"");
		}
		if (node.getHalfCheck() != null) {
			writer.write(", halfCheck:" + node.getHalfCheck());
		}
		if (isNotEmpty(node.getIcon())) {
			writer.write(", icon:\"" + ctx + node.getIcon() + "\"");
		}
		if (isNotEmpty(node.getIconClose())) {
			writer.write(", iconClose:\"" + ctx + node.getIconClose() + "\"");
		}
		if (isNotEmpty(node.getIconOpen())) {
			writer.write(", iconOpen:\"" + ctx + node.getIconOpen() + "\"");
		}
		if (isNotEmpty(node.getIconSkin())) {
			writer.write(", iconSkin:\"" + node.getIconSkin() + "\"");
		}
		if (node.getIsHidden() != null) {
			writer.write(", isHidden:" + node.getIsHidden());
		}
		if (node.getIsParent() != null) {
			writer.write(", isParent:" + node.getIsParent());
		}
		if (node.getNoCheck() != null) {
			writer.write(", nocheck:" + node.getNoCheck());
		}
		if (node.getOpen() != null) {
			writer.write(", open:" + node.getOpen());
		}
		if (isNotEmpty(node.getTarget())) {
			writer.write(", target:\"" + node.getTarget() + "\"");
		}
		if (isNotEmpty(node.getUrl())) {
			writer.write(", url:\"" + node.getUrl() + "\"");
		}

		boolean first = true;
		List<TreeNodeItem> children = node.getChildren();
		if (children != null && !children.isEmpty()) {
			writer.write(", children: [\n");
			for (TreeNodeItem cnode : children) {
				if (!first) {
					writer.write(",\n");
				} else {
					first = false;
				}
				encodeTreeNode(writer, cnode);
			}
			writer.write("]\n");
		}

		writer.write("}");
	}

	protected void linkCss(Writer writer, String src) throws IOException {
		if (isEmpty(src)) {
			return;
		}
		Map<String, Integer> container = (Map<String, Integer>) request
				.getAttribute(ATTR_CONTAINER);
		if (container == null) {
			container = new HashMap<String, Integer>();
			request.setAttribute(ATTR_CONTAINER, container);
		}

		if (!container.containsKey(src)) {
			container.put(src, 0);
			writer.write("<link href='");
			writer.write(getContextPath());
			writer.write(src);
			writer.write("?ts=" + RunParams.RELEASE_VERSION);
			writer.write("' rel='stylesheet' type='text/css' charset='utf-8'/>\n");
		}
	}

	protected void linkJavaScript(Writer writer, String src) throws IOException {
		if (isEmpty(src)) {
			return;
		}
		Map<String, Integer> container = (Map<String, Integer>) request
				.getAttribute(ATTR_CONTAINER);
		if (container == null) {
			container = new HashMap<String, Integer>();
			request.setAttribute(ATTR_CONTAINER, container);
		}

		if (!container.containsKey(src)) {
			container.put(src, 0);
			writer.write("<script src='");
			writer.write(getContextPath());
			writer.write(src);
			writer.write("?ts=" + RunParams.RELEASE_VERSION);
			writer.write("' type='text/javascript' charset='utf-8'></script>\n");
		}
	}

	@Override
	public String toString() {
		StringWriter r = new StringWriter();
		HtmlResponseWriter writer = new HtmlResponseWriter(r);
		try {
			encodeBegin(writer);
			encodeEnd(writer);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return r.getBuffer().toString();
	}

}
