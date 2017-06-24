package com.isoft.biz.web.platform.topo;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.unset;
import static com.isoft.iradar.inc.DBUtil.DBend;
import static com.isoft.iradar.inc.DBUtil.DBstart;
import static com.isoft.iradar.inc.Defines.NOT_EMPTY;
import static com.isoft.iradar.inc.Defines.O_OPT;
import static com.isoft.iradar.inc.Defines.P_ACT;
import static com.isoft.iradar.inc.Defines.P_SYS;
import static com.isoft.iradar.inc.Defines.RDA_FLAG_DISCOVERY_CREATED;
import static com.isoft.iradar.inc.Defines.RDA_SORT_UP;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.T_RDA_INT;
import static com.isoft.iradar.inc.Defines.T_RDA_STR;
import static com.isoft.iradar.inc.FuncsUtil.clearCookies;
import static com.isoft.iradar.inc.FuncsUtil.getPagingLine;
import static com.isoft.iradar.inc.FuncsUtil.get_request;
import static com.isoft.iradar.inc.FuncsUtil.show_messages;
import static com.isoft.iradar.inc.FuncsUtil.validate_sort_and_sortorder;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_param;
import static com.isoft.iradar.inc.JsUtil.get_js;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.iradar.inc.ValidateUtil.check_fields;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.isoft.biz.daoimpl.common.AnnouncementDAO;
import com.isoft.biz.daoimpl.platform.topo.CabTopoDAO;
import com.isoft.biz.daoimpl.platform.topo.TPicDAO;
import com.isoft.biz.vo.platform.topo.TPic;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.imon.topo.util.DateUtil;
import com.isoft.imon.topo.util.TopoUtil;
import com.isoft.iradar.inc.EnhancesUtil;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CCheckBox;
import com.isoft.iradar.tags.CComboBox;
import com.isoft.iradar.tags.CComboItem;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CFile;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CToolBar;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.action.RadarBaseAction;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;
/**
 * 拓扑图片
 * @author guzhaohui
 *
 */
public class TPicAction extends RadarBaseAction {
	
	private File file;
	private String fileFileName;
	private String fileContentType;
	
	private String name;
	private String category;
	private String width;
	private String height;
	private String save;
	
	private static final int BUFFER_SIZE = 1024;
	
	
	@Override
	protected void doInitPage() {
		page("title", "拓扑图片");
		page("file", "TPicIndex.action");
	//	page("hist_arg", new String[] {});
		page("js", new String[] {"imon/browseTopo.js",});
		page("css", new String[] {"lessor/topocenter/topopic.css"});
	}
	
	@Override
	protected void doCheckFields(SQLExecutor executor) {
		// VAR	TYPE	OPTIONAL	FLAGS	VALIDATION	EXCEPTION
		CArray fields = map(
			"id" ,				array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"name" ,			array(T_RDA_STR, O_OPT, null,	NOT_EMPTY+"{}.length<10",	"isset({save})","拓扑名称"),
			"category",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"url",				array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"width",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"height",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"txtPicName",		array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			"dosubm",			array(T_RDA_STR, O_OPT, P_SYS,	null,	null),
			
			// actions
			"go" ,				array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"save" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"reset" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"clone" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"delete" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"filter" ,			array(T_RDA_STR, O_OPT, P_SYS|P_ACT,	null,	null),
			"form" ,			array(T_RDA_STR, O_OPT, P_SYS,			null,	null),
			"form_refresh" ,	array(T_RDA_INT, O_OPT, null,			null,	null)
		);
		check_fields(getIdentityBean(), fields);
		validate_sort_and_sortorder(getIdentityBean(), executor, "name", RDA_SORT_UP);
	}
	
	@Override
	protected void doPermissions(SQLExecutor executor) {
		
	}
	@Override
	protected boolean doAjax(SQLExecutor executor) {
		return false;
	}
	@Override
	public void doAction(final SQLExecutor executor) {
		
		_REQUEST.putAll(map(
				"name", name,
				"category", category,
				"width", width,
				"height", height,
				"save",save
			));
		if (isset(_REQUEST,"save")) {
			
			doSaveAction(executor);
		}else if (isset(_REQUEST,"dosubm")) {
			
		}else if ("delete".equals(Nest.value(_REQUEST,"go").asString())) {
			doDeleteAction(executor);
		}else if (isset(_REQUEST,"reset")) {
			 Nest.value(_REQUEST,"txtPicName").$("");
			
		}else if (isset(_REQUEST,"filter")) {
			doListView(executor, Nest.value(_REQUEST,"txtPicName").$());
			return;
		}
		
		if (isset(_REQUEST,"form")) {
			CArray data = map(
				"form", get_request("form"),
				"form_refresh", get_request("form_refresh", 0)
			);
			CWidget tPicWidget = new CWidget();
			
			// create form
			CForm tPicForm = new CForm();
			tPicForm.setAttribute("enctype", "multipart/form-data");
			tPicForm.setAttribute("onsubmit", "return doSubmit(this);");
			tPicForm.setAttribute("id", "tPicForm");
			tPicForm.setName("tPicForm");
			tPicForm.addVar("form", Nest.value(data,"form").$());
			
			// create form list
			CFormList tPicFormList = new CFormList("tPicFormList");
			//图片名称
			CTextBox nameTextBox = new CTextBox("name", name, RDA_TEXTBOX_STANDARD_SIZE,false,19);
			//CTextBox nameTextBox = new CTextBox("name", null);
			nameTextBox.setAttribute("style", "width:106px");
			tPicFormList.addRow("名称", nameTextBox);
			//图片类型
			CComboBox CB_Category = new CComboBox("category");
			CB_Category.setAttribute("style", "width:106px");
			CB_Category.addItems((CArray)map(
				"backgroup", "背景图片",
				"cabinet", "机柜图片",
				"room", "机房图片"
			));
			tPicFormList.addRow("类型", CB_Category);
			// //图片高度
			// CComboBox CB_Height = new CComboBox("height");
			// CB_Height.setAttribute("style", "width:106px");
			// CB_Height.addItems((CArray)map(
			// 	"100%", "100%",
			// 	"75%", "75%",
			// 	"50%", "50%",
			// 	"25%", "25%"
			// ));
			// tPicFormList.addRow("高度", CB_Height);
			// //图片宽度
			// CComboBox CB_Width = new CComboBox("width");
			// CB_Width.setAttribute("style", "width:106px");
			// CB_Width.addItems((CArray)map(
			// 	"100%", "100%",
			// 	"75%", "75%",
			// 	"50%", "50%",
			// 	"25%", "25%"
			// ));
			// tPicFormList.addRow("宽度", CB_Width);
			
			//选择图片
			CFile file = new CFile("file");
			file.setAttribute("onchange", "checkfile(this)");
			tPicFormList.addRow("图片", file);
			
			// append tabs to form
			CTabView tPicTab = new CTabView();
			tPicTab.addTab("tPicFormTab", "拓扑图片", tPicFormList);
			tPicForm.addItem(tPicTab);

			// append buttons to form

			tPicForm.addItem(makeFormFooter(
					new CSubmit("save", _("Save")),
					new CButtonCancel(url_param(getIdentityBean(), "config"))
				));
			
			// append form to widget
			tPicWidget.addItem(tPicForm);
			tPicWidget.show();
		} else {
			doListView(executor,"");
		}
	}
	/**
	 * 获取列表展示数据
	 * @param executor
	 */
	private void doListView(final SQLExecutor executor,Object tpicName) {
		CWidget tPicWidget = new CWidget();
		
		CForm tPicForm = new CForm();
		tPicForm.setName("topoForm");
		
		CToolBar tb = new CToolBar(tPicForm);
		tb.addSubmit("form", "添加图片", "", "orange create");
		
		CArray<CComboItem> goComboBox = array();
		
		CComboItem goOption = new CComboItem("delete", "删除图片");
		goOption.setAttribute("confirm", "确认删除所选的图片?");
		goOption.setAttribute("class", "orange delete");
		goComboBox.add(goOption);
		
		tb.addComboBox(goComboBox);
		
		rda_add_post_js("chkbxRange.pageGoName = \"tpic\";");
		
		CForm frmFilter = new CForm("get");
		frmFilter.setAttribute("name", "frmfilter");
		CTextBox txtPicName = new CTextBox("txtPicName",Nest.value(_REQUEST,"txtPicName").asString());
		CSubmit filter = new CSubmit("filter", _("GoFilter"));
		CSubmit reset = new CSubmit("reset",_("Reset"));
		frmFilter.addItem(array("图片名称：",txtPicName,filter,reset));
		tPicWidget.addHeader(frmFilter);
		
		CDiv headerActions = EnhancesUtil.get_table_header_actions(array(tb));
		tPicWidget.addItem(headerActions);
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		//判断检索条件中的拓扑图片名称是否为空
		if(!"".equals(tpicName)){
			paramMap.put("name",tpicName);
		}
		AnnouncementDAO an =new AnnouncementDAO(executor);
		List<Map> lis=an.doconfig();
		paramMap.put("search_limit",Long.parseLong(lis.get(0).get("search_limit").toString()));
		List<TPic> resultData =  new TPicDAO(executor).doTPicPage(null,paramMap);
	
		CTableInfo topoTable = new CTableInfo("没有发现拓扑图片");

		topoTable.setHeader(array(
				new CCheckBox("all_tpic", false, "checkAll(\""+tPicForm.getName()+"\", \"all_tpic\", \"tpic\");"),
				"图片名称",
				"图片类型",
				// "图片高度 ",
				// "图片宽度 ",
				"图片路径"
				));
		CArray arraydata = new CArray();
		TPic resultSingle = null;
		
		for (int i = 0; i < resultData.size(); i++) {
			resultSingle =  resultData.get(i);
			topoTable.addRow(
					array(
							new CCheckBox("tpic["+resultSingle.getId()+","+resultSingle.getUrl()+"]", false, null, resultSingle.getId()+","+resultSingle.getUrl()),
							resultSingle.getName(),
							changeImageLanuage(resultSingle.getCategory()),
							// resultSingle.getHeight(),
							// resultSingle.getWidth(),
							resultSingle.getUrl()
						)
					);
			arraydata.put(resultSingle);
		}
		CTable paging = getPagingLine(getIdentityBean(),executor,arraydata);
		tPicForm.addItem(array(topoTable,paging));
		tPicWidget.addItem(tPicForm);
		tPicWidget.show();
	}
	
	private String changeImageLanuage(String imageName){
		if(imageName == null){
			return null;
		}
		String result = null;
		if(imageName.equals("backgroup")){
			result = "背景图片";
		}else if(imageName.equals("cabinet")){
			result = "机柜图片";
		}else if(imageName.equals("room")){
			result = "机房图片";
		}
		return result;
	}
	
	/**
	 * 保存数据
	 * @param executor
	 */
	private void doSaveAction(final SQLExecutor executor) {
		
		String url = "mappic/";
		File targetFile = null;
		if (file != null && fileFileName.length() != 0) {
			int pos = this.fileFileName.lastIndexOf(".");
			String targetFileName = DateUtil.getCurrentTimeAsID()
					+ this.fileFileName.substring(pos);
			url += targetFileName;
			targetFile = new File(TopoUtil.webRootUrl + "platform/iradar/mappic/" + targetFileName);
			if (!targetFile.exists()) {
				copyFile(file, targetFile);
			}
		}
		
		//准备数据
		final Map topoMap = map(
				"name", Nest.value(_REQUEST,"name").$(),
				"category", Nest.value(_REQUEST,"category").$(),
				// "width", Nest.value(_REQUEST,"width").$(),
				// "height", Nest.value(_REQUEST,"height").$(),
				"width", "100",
				"height", "100",
				"url",url,
				"tenantId", getIdentityBean().getTenantId(),
				"userId", getIdentityBean().getUserId(),
				"username", getIdentityBean().getUserName()
			);
		
		//执行数据库操作——开始
		DBstart(executor);
		String msgOk = "拓扑图片已添加";
		String msgFail ;
		
		Object[] obj = new TPicDAO(executor).doTPicAdd(topoMap);
		msgFail=empty(obj[1].equals("名称已存在!"))? "无法添加同名的拓扑图片":"无法添加拓扑图片";
		//保存数据
		boolean result = !empty(obj[0]);
		
		//执行数据库操作——结束
		result = DBend(executor, result);
		
		//显示操作结果提示
		show_messages(result, msgOk, msgFail);
		
		//清空
		if(result){
			unset(_REQUEST,"form");
		}
		clearCookies(result);
	}
	
	/**
	 * 删除数据
	 * @param executor
	 */
	private void doDeleteAction(final SQLExecutor executor) {
		
		//准备数据
		
		List<Long> tpIdList = new ArrayList<Long>();
		
		List<String> tpUrlList = new ArrayList<String>();
		CArray<Map> nodeId=new CArray<Map>();
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		//通过request获取选中数据的id和图片url组成的数组
		final String[] result = Nest.array(_REQUEST,"tpic").asString();
		
		//解析数据并遍历
		for (int i = 0; i < result.length; i++) {
			//得到单个数据
			String resultSingle = result[i];
			//通过“，”将该字符串分离开
			String[] idAndUrl = resultSingle.split(",");
			//得到id，用于删除数据库中的图片记录
			String id = idAndUrl[0];
			CArray<Map> node=new CArray<Map>();
			node.put("picid", id);
			nodeId.push(node);
			tpIdList.add(Long.parseLong(id));
			//得到url，用于删除后台文件夹里的图片文件
			String url = idAndUrl[1];
			String fileUrl = "platform\\iradar\\" + url.replace("/", "\\");
			tpUrlList.add(fileUrl);
		}
		paramMap.put("tpIdList", tpIdList);
		
		//执行数据库操作——开始
		DBstart(executor);
		
		//删除数据
		boolean goResult = false;
		
		//得到删除拓扑图片的状态（false：删除失败；true：删除成功；error：因为删除系统图片而导致的删除失败）
		String state = new TPicDAO(executor).doTPicDel(paramMap);
		CabTopoDAO cd =new CabTopoDAO(executor);
		for (Entry<Object, Map> e : nodeId.entrySet()) {
			Map dle = e.getValue();
			List piclist = cd.doCabinetTopoListBypicid(dle);
			if (piclist.size() > 0) {
				cd.doCabinetNodeDelByPicId(dle);
			}
		}
		
		if(("false").equals(state)){	//false：删除失败
			goResult = DBend(executor, goResult);
			show_messages(goResult, "拓扑图片已删除", "无法删除拓扑图片");
			
		} else if (("error").equals(state)) {	//error：因为删除系统图片而导致的删除失败
			goResult = DBend(executor, goResult);	
			show_messages(goResult, "拓扑图片已删除", "无法删除系统初始化图片");
			
		} else {	//true：删除成功
			goResult = true;
			goResult = DBend(executor, goResult);
			
			//进入到这里说明前端拓扑图片删除成功，则需要相应删除后台mapppic文件夹里的图片文件
			for (int i = 0; i < tpUrlList.size(); i++) {
				String fileUrl = tpUrlList.get(i);
				File file = new File(TopoUtil.webRootUrl+fileUrl);
				if(file.exists()){
					file.delete();
				}
			}
			
			show_messages(goResult, "拓扑图片已删除", "无法删除拓扑图片");
		}
		
		//清空
		clearCookies(goResult);
	}
	
	private static void copyFile(File uploadFile, File targetFile) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(uploadFile),
					BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(targetFile),
					BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			while (in.read(buffer) > 0) {
				out.write(buffer);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public void setUpload(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public void setHeight(String height) {
		this.height = height;
	}
	
	public void setSave(String save) {
		this.save = save;
	}
}