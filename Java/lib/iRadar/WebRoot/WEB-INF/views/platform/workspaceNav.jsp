<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" errorPage="/error.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ page import="com.isoft.utils.CacheUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="com.isoft.model.FuncItem" %>
<%
	FuncItem selectedFunc = null;
	FuncItem selectedFuncItem = null;
	String funcId = request.getParameter("funcId");
	if (funcId != null && funcId.length() > 0) {
		List<FuncItem> funcList = CacheUtil.getNavFuncList();
		for (FuncItem func : funcList) {
			if (func.getId().equals(funcId)) {
				selectedFunc = func;
				break;
			}
		}
	}

	if (selectedFunc != null) {
		if(!selectedFunc.getSubFuncList().isEmpty()){
			selectedFuncItem = selectedFunc.getSubFuncList().get(0);
			while(!selectedFuncItem.getSubFuncList().isEmpty()){
				selectedFuncItem = selectedFuncItem.getSubFuncList().get(0);
			}
		}
	}
	String ctxPath = request.getContextPath();
	String selectedUrl = "";
	if (selectedFuncItem != null) {
		selectedUrl = ctxPath + selectedFuncItem.getFuncUrl();
	}
%>
<META HTTP-EQUIV="CONTENT-TYPE" CONTENT="TEXT/HTML; CHARSET=UTF-8">
<HTML><HEAD><TITLE>功能导航</TITLE><META CHARSET="UTF-8"/></HEAD>
<%
	if (selectedFunc != null) {
		if (!selectedFunc.getSubFuncList().isEmpty()) {
%>
<frameset cols="179,*" frameborder="NO" border="0" framespacing="0">
    <frame src="workspaceMenu.action?funcId=<%=funcId%>" id="leftMenu" name="leftMenu" noresize scrolling="no">
    <frame src="<%=selectedUrl%>" id="rightConsole" name="rightConsole">
</frameset>
		<%
			} else {
		%>
<frameset cols="*" frameborder="NO" border="0" framespacing="0">
    <frame src="<%=(ctxPath + selectedFunc.getFuncUrl())%>" id="rightConsole" name="rightConsole">
</frameset>
        <%
        	}
        	}
        %>
<noframes><body></body></noframes>
</HTML>