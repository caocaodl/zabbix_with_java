package com.isoft.struts2.views.tags.jquery.easyui.datagrid;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.isoft.model.SelectItem;
import com.isoft.struts2.components.AndurilUIComponent;
import com.isoft.struts2.components.ValueBinding;
import com.isoft.struts2.util.UIComponentTagUtils;
import com.isoft.struts2.views.tags.HtmlResponseWriter;
import com.opensymphony.xwork2.util.ValueStack;

public class $DataGrid extends AndurilUIComponent {

	public $DataGrid(ValueStack stack, HttpServletRequest request,
			HttpServletResponse response) {
		super(stack, request, response);
	}

	@Override
	protected boolean encodeBegin(HtmlResponseWriter writer) throws IOException {
		writer.writeLine("<table id='" + getId() + "'></table>");
		return true;
	}

	@Override
	protected boolean encodeEnd(HtmlResponseWriter writer) throws IOException {
		if (this.model.subGrid != null) {
			linkJavaScript(writer, $DataGrid.class, "detailview");
		}
		if (this.model.groupView != null) {
			this.linkJavaScript(writer, $DataGrid.class,"groupview");
		}
		String baseUrl = getContextPath();
		writer.writeLine("<script type='text/javascript'>");
		writer.writeLine("$(document).ready(function(){");
		renderGrid(writer, baseUrl, null, model);
		writer.writeLine("});");
		writer.writeLine("</script>");

		return false;
	}

	private void renderGrid(HtmlResponseWriter writer, String baseUrl, DataGridItem p, DataGridItem m)
			throws IOException {
		if (model == null) {
			return;
		}
		
		if (m.subGrid != null) {
			m.setView("detailview");
		} else if (m.groupView != null) {
			m.setView("groupview");
		}
		
		if (p != null) {
			writer.writeLine("	$('#" + m.id + "'+index).datagrid({");
		} else {
			writer.writeLine("	$('#" + m.id + "').datagrid({");
		}

		if (!m.headerList.isEmpty()) {
			writer.writeLine("		columns:[");
			for (int j = 0; j < m.headerList.size(); j++) {
				HeaderItem hi = m.headerList.get(j);
				renderGridHeader(writer, j, hi);
			}
			writer.writeLine("		],");
		}
		if (!m.frozenHeaderList.isEmpty()) {
			writer.writeLine("		frozenColumns:[");
			for (int j = 0; j < m.frozenHeaderList.size(); j++) {
				HeaderItem hi = m.frozenHeaderList.get(j);
				renderGridHeader(writer, j, hi);
			}
			writer.writeLine("		],");
		}

		if (m.fitColumns != null) {
			writer.writeLine("		fitColumns:" + m.fitColumns+",");
		}
		if (m.resizeHandle != null && m.resizeHandle.length() > 0) {
			writer.writeLine("		resizeHandle:'" + m.resizeHandle + "',");
		}
		if (m.autoRowHeight != null) {
			writer.writeLine("		autoRowHeight:" + m.autoRowHeight+",");
		}
		if(!m.buttonList.isEmpty()){
			writer.writeLine("		toolbar:[");
			for (int j = 0; j < m.buttonList.size(); j++) {
				ButtonItem bi = m.buttonList.get(j);
				if(j>0){
					writer.writeLine("		  ,'-',");
				}
				writer.writeLine("		  {");
				if(bi.getCaption()!=null && bi.getCaption().length()>0){
					writer.writeLine("		    text:'"+bi.getCaption()+"',");
				}
				if(bi.getIcon()!=null && bi.getIcon().length()>0){
					writer.writeLine("		    iconCls:'"+bi.getIcon()+"',");
				}
				writer.writeLine("		    handler:function(){"+bi.getOnClick()+"();}");
				writer.writeLine("		  }");
			}
			writer.writeLine("		],");
		}else if (m.toolbar != null && m.toolbar.length()>0) {
			if(m.toolbar.startsWith("#")){
				writer.writeLine("		toolbar:'" + m.toolbar+"',");
			} else {
				writer.writeLine("		toolbar:" + m.toolbar+",");
			}
		}
		if (m.striped != null) {
			writer.writeLine("		striped:" + m.striped+",");
		}
		if (m.method != null && m.method.length() > 0) {
			writer.writeLine("		method:'" + m.method + "',");
		}
		if (m.nowrap != null) {
			writer.writeLine("		nowrap:" + m.nowrap+",");
		}
		if (m.idField != null && m.idField.length() > 0) {
			writer.writeLine("		idField:'" + m.idField + "',");
		}
		if (m.loadMsg != null && m.loadMsg.length() > 0) {
			writer.writeLine("		loadMsg:'" + m.loadMsg + "',");
		}
		if (m.pagination != null) {
			writer.writeLine("		pagination:" + m.pagination+",");
		}
		if (m.rownumbers != null) {
			writer.writeLine("		rownumbers:" + m.rownumbers+",");
		}
		if (m.singleSelect != null) {
			writer.writeLine("		singleSelect:" + m.singleSelect+",");
		}
		if (m.checkOnSelect != null) {
			writer.writeLine("		checkOnSelect:" + m.checkOnSelect+",");
		}
		if (m.selectOnCheck != null) {
			writer.writeLine("		selectOnCheck:" + m.selectOnCheck+",");
		}
		if (m.pagePosition != null && m.pagePosition.length() > 0) {
			writer.writeLine("		pagePosition:'" + m.pagePosition + "',");
		}
		if (m.pageNumber != null) {
			writer.writeLine("		pageNumber:" + m.pageNumber+",");
		}
		if (m.pageSize != null) {
			writer.writeLine("		pageSize:" + m.pageSize+",");
		}
		if (m.pageList != null && m.pageList.length()>0) {
			writer.writeLine("		pageList:" + m.pageList+",");
		}
		if (m.sortName != null && m.sortName.length() > 0) {
			writer.writeLine("		sortName:'" + m.sortName + "',");
		}
		if (m.sortOrder != null && m.sortOrder.length() > 0) {
			writer.writeLine("		sortOrder:'" + m.sortOrder + "',");
		}
		if (m.multiSort != null) {
			writer.writeLine("		multiSort:" + m.multiSort+",");
		}
		if (m.remoteSort != null) {
			writer.writeLine("		remoteSort:" + m.remoteSort+",");
		}
		if (m.showHeader != null) {
			writer.writeLine("		showHeader:" + m.showHeader+",");
		}
		if (m.showFooter != null) {
			writer.writeLine("		showFooter:" + m.showFooter+",");
		}
		if (m.scrollbarSize != null) {
			writer.writeLine("		scrollbarSize:" + m.scrollbarSize+",");
		}
		if (m.rowStyler != null && m.rowStyler.length() > 0) {
			writer.writeLine("		rowStyler:'" + m.rowStyler + "',");
		}
		if (m.loader != null && m.loader.length() > 0) {
			writer.writeLine("		loader:'" + m.loader + "',");
		}
		if (m.loadFilter != null && m.loadFilter.length() > 0) {
			writer.writeLine("		loadFilter:'" + m.loadFilter + "',");
		}
		if (m.editors != null && m.editors.length() > 0) {
			writer.writeLine("		editors:'" + m.editors + "',");
		}
		if (m.view != null && m.view.length() > 0) {
			writer.writeLine("		view:" + m.view + ",");
			if (m.groupView != null){
				writer.writeLine("		groupField:'"+m.groupView.getField()+"',");
				if (m.groupView.getFormatter() != null
						&& m.groupView.getFormatter().length() > 0) {
					writer.writeLine("		groupFormatter:"+m.groupView.getFormatter()+",");
				} else {
					writer.writeLine("		groupFormatter:function(value,rows){return value + ' - ' + rows.length + ' Item(s)';},");
				}
			}
		}

		if (m.collapsible != null) {
			//writer.writeLine("		collapsible:" + m.collapsible + ",");
		}
		writer.writeLine("		collapsible:false,");
		
		if (m.title != null && m.title.length() > 0) {
			writer.writeLine("		title:'" + m.title + "',");
		}
		
		if (m.url != null && m.url.length() > 0) {
			String url;
			if(m.url.startsWith("#{") && UIComponentTagUtils.isValueReference(m.url)){
				ValueBinding vb = UIComponentTagUtils.createValueBinding(m.url);
				url = String.valueOf(vb.getValue(this.getStack()));
			} else {
				url = m.url;
			}
			
			if(url.startsWith("'/")){
				writer.writeLine("		url:" + url.substring(0,1)+baseUrl + url.substring(1) + ",");
			} else if(url.startsWith("'")){
				writer.writeLine("		url:" + url + ",");
			} else if(url.startsWith("/")){
				writer.writeLine("		url:'" + baseUrl + url + "',");
			} else {
				writer.writeLine("		url:'" + url + "',");
			}
		}
		
		if (m.subGrid != null) {
			DataGridItem s = m.subGrid;
			writer.writeLine("		detailFormatter:function(index,row){");
			writer.writeLine("			return '<div style=\"padding:2px\"><table id=\""+s.id+"' + index + '\"></table></div>';");
			writer.writeLine("		},");
			writer.writeLine("		onExpandRow: function(index,row){");
			renderGrid(writer, baseUrl, m, s);
			writer.writeLine("			$('#"+m.id+"').datagrid('fixDetailRowHeight',index);");
			writer.writeLine("		},");
		}
		
		if (p != null) {
			writer.writeLine("		onResize:function(){");
			writer.writeLine("			$('#"+p.id+"').datagrid('fixDetailRowHeight',index);");
			writer.writeLine("		},");
			
			writer.writeLine("		onLoadSuccess:function(){");
			writer.writeLine("			setTimeout(function(){");
			writer.writeLine("				$('#"+p.id+"').datagrid('fixDetailRowHeight',index);");
			writer.writeLine("			},0);");
			writer.writeLine("		},");
		}
		writer.writeLine("		queryParams:getSearchFilter('"+this.getId()+"'),");
		writer.writeLine("		author:'isoft'");
		writer.writeLine("	});");
	}

	private void renderGridHeader(HtmlResponseWriter writer, int index,
			HeaderItem hi) throws IOException {
		if (index > 0) {
			writer.writeLine(",");
		}
		writer.writeLine("		         [");
		for (int i = 0; i < hi.columnList.size(); i++) {
			ColumnItem ci = hi.columnList.get(i);
			if (i > 0) {
				writer.writeLine(",");
			}
			writer.write("			  {");
			if (ci.getWidth() != null) {
				writer.write("width:" + ci.getWidth() + ",");
			}
			if (ci.getRowspan() != null && ci.getRowspan() > 1) {
				writer.write("rowspan:" + ci.getRowspan() + ",");
			}
			if (ci.getColspan() != null && ci.getColspan() > 1) {
				writer.write("colspan:" + ci.getColspan() + ",");
			}
			if (ci.getAlign() != null) {
				writer.write("align:'" + ci.getAlign() + "',");
			}
			if (ci.getHalign() != null) {
				writer.write("halign:'" + ci.getHalign() + "',");
			}
			if (ci.getSortable() != null && ci.getSortable()) {
				writer.write("sortable:" + ci.getSortable() + ",");
			}
			if (ci.getOrder() != null) {
				writer.write("order:'" + ci.getOrder() + "',");
			}
			if (ci.getResizable() != null && ci.getResizable()) {
				writer.write("resizable:" + ci.getResizable() + ",");
			}
			if (ci.getFixed() != null && ci.getFixed()) {
				writer.write("fixed:" + ci.getFixed() + ",");
			}
			if (ci.getHidden() != null && ci.getHidden()) {
				writer.write("hidden:" + ci.getHidden() + ",");
			}
			if (ci.getCheckbox() != null && ci.getCheckbox()) {
				writer.write("checkbox:" + ci.getCheckbox() + ",");
			}
			if (ci.getFormatter() != null) {
				if(ci.getFormatter().startsWith("#{") && UIComponentTagUtils.isValueReference(ci.getFormatter())){
					ValueBinding vb = UIComponentTagUtils.createValueBinding(ci.getFormatter());
					List<SelectItem> siList = (List<SelectItem>)vb.getValue(this.getStack());
					String qm = "";
					if(ci.getDitemtype()==null || ci.getDitemtype().length()==0 || "String".equals(ci.getDitemtype())){
						qm = "'";
					}
					if (siList != null) {
						writer.write("formatter:function(val,row){ switch(val){");
						for(SelectItem si : siList){
							writer.write("case "+qm+si.getValue()+qm+":return '"+si.getLabel()+"';break;");
						}
						writer.write("default:return '';");
						writer.write("}},");
					}
				} else {
				   writer.write("formatter:" + ci.getFormatter() + ",");
				}
			}
			if (ci.getStyler() != null) {
				writer.write("styler:" + ci.getStyler() + ",");
			}
			if (ci.getSorter() != null) {
				writer.write("sorter:" + ci.getSorter() + ",");
			}
			if (ci.getEditor() != null) {
				writer.write("editor:'" + ci.getEditor() + "',");
			}
			if (ci.getColspan() == null || ci.getColspan() <= 1) {
				writer.write("field:'" + ci.getField() + "',");
			}
			writer.write("title:'" + ci.getTitle() + "'");
			writer.write("}");
		}
		writer.writeLine("\n		         ]");
	}


	private DataGridItem model = new DataGridItem();

	public DataGridItem getModel() {
		return model;
	}

	public void setModel(DataGridItem model) {
		this.model = model;
	}
}
