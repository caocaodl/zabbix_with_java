<%@ page contentType="text/html;charset=UTF-8"%>
<c:set var="LOADING_ROOT" value="${ctx}/scripts/loading"/>
    <link rel="stylesheet" type="text/css" href="${LOADING_ROOT}/loading.css" />
    <div id="loading-mask"></div>
    <div id="loading">
        <div class="loading-indicator"><img src="${LOADING_ROOT}/extanim32.gif" align="absmiddle"/>正在加载数据...</div>
    </div>
    <script type="text/javascript" src="${LOADING_ROOT}/Loading.js"></script>