package com.isoft.struts2.views.tags.ui.ztree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.isoft.model.TreeAsyncItem;
import com.isoft.model.TreeNodeItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.TreeNodeHolder;
import com.isoft.struts2.components.TreeSettingHolder;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTree extends AndurilUIComponent implements TreeNodeHolder,
		TreeSettingHolder {

	public $ZTree(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	private List<TreeNodeItem> treeNodeList = null;

	@Override
	public void pushTreeNodeItem(TreeNodeItem treeNodeItem) {
		if (treeNodeList == null) {
			treeNodeList = new ArrayList<TreeNodeItem>(1);
		}
		treeNodeList.add(treeNodeItem);
	}

	private TreeAsyncItem asyncItem;

	@Override
	public void pushTreeAsyncItem(TreeAsyncItem asyncItem) {
		this.asyncItem = asyncItem;
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		linkCss(writer, $ZTree.class);
		linkJavaScript(writer, $ZTree.class);
		return true;
	}

	@Override
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
		writer.write("	},");
		writer.write("	callback:{");
		writer.write("	        beforeClick:beforeClick");
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

		writer.write("$(document).ready(function(){");
		writer
				.write("$.fn.zTree.init($('#" + getId()
						+ "'), setting, zNodes);");
		writer.write("});");
		writer.write("</script>");
		return false;
	}

	private void encodeTreeSettingAsync(HtmlResponseWriter writer)
			throws IOException {
		if (asyncItem != null) {
			writer.write("	async: {\n");
			if (StringUtils.isNotEmpty(asyncItem.getAutoParam())) {
				writer.write("autoParam:" + asyncItem.getAutoParam() + ",\n");
			}
			if (StringUtils.isNotEmpty(asyncItem.getContentType())) {
				writer.write("contentType:\"" + asyncItem.getContentType() + "\",\n");
			}
			if (StringUtils.isNotEmpty(asyncItem.getDataFilter())) {
				writer.write("dataFilter:" + asyncItem.getDataFilter() + ",\n");
			}
			if (StringUtils.isNotEmpty(asyncItem.getDataType())) {
				writer.write("dataType:\"" + asyncItem.getDataType() + "\",\n");
			}
			if (StringUtils.isNotEmpty(asyncItem.getOtherParam())) {
				writer.write("otherParam:" + asyncItem.getOtherParam() + ",\n");
			}
			if (StringUtils.isNotEmpty(asyncItem.getType())) {
				writer.write("type:\"" + asyncItem.getType() + "\",\n");
			}
			if (StringUtils.isNotEmpty(asyncItem.getUrl())) {
				writer.write("url:\"" + getContextPath() + asyncItem.getUrl() + "\",\n");
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
		if (StringUtils.isNotEmpty(node.getpId())) {
			writer.write(", pId:\"" + node.getpId() + "\"");
		}
		writer.write(", name:\"" + node.getName() + "\"");

		if (node.getChecked() != null) {
			writer.write(", checked:" + node.getChecked());
		}
		if (node.getChkDisabled() != null) {
			writer.write(", chkDisabled:" + node.getChkDisabled());
		}
		if (StringUtils.isNotEmpty(node.getClick())) {
			writer.write(", click:\"" + node.getClick() + "\"");
		}
		if (node.getHalfCheck() != null) {
			writer.write(", halfCheck:" + node.getHalfCheck());
		}
		if (StringUtils.isNotEmpty(node.getIcon())) {
			writer.write(", icon:\"" + ctx + node.getIcon() + "\"");
		}
		if (StringUtils.isNotEmpty(node.getIconClose())) {
			writer.write(", iconClose:\"" + ctx  + node.getIconClose() + "\"");
		}
		if (StringUtils.isNotEmpty(node.getIconOpen())) {
			writer.write(", iconOpen:\"" + ctx  + node.getIconOpen() + "\"");
		}
		if (StringUtils.isNotEmpty(node.getIconSkin())) {
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
		if (StringUtils.isNotEmpty(node.getTarget())) {
			writer.write(", target:\"" + node.getTarget() + "\"");
		}
		if (StringUtils.isNotEmpty(node.getUrl())) {
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
}
