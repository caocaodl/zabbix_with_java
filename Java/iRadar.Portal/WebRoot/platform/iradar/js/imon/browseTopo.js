/**
 * 浏览拓扑
 * @param type 拓扑类型
 * @param id 拓扑ID
 * @param topoName 拓扑名称
 */

function setUrl(type, id, topoName) {
    if ("nettopo" == type) {
        var url = window.top.ctxpath + "/platform/iradar/NetTopoIndex.action?topoId=" + id + "&topoName=" + topoName;
        window.top.jQuery.workspace.openTab("物理链路拓扑" + topoName, url);
    } else if ("hosttopo" == type) {
        var url = window.top.ctxpath + "/platform/iradar/CloudTopoIndex.action?topoId=" + id + "&topoName=" + topoName;
        window.top.jQuery.workspace.openTab("云主机从属拓扑" + topoName, url);
    } else if ("cabtopo" == type) {
        var url = window.top.ctxpath + "/platform/iradar/CabTopoIndex.action?topoId=" + id + "&topoName=" + topoName;
        window.top.jQuery.workspace.openTab("机房拓扑" + topoName, url);
    } else if ("biztopo" == type) {
        var url = window.top.ctxpath + "/platform/iradar/BizTopoIndex.action?topoId=" + id + "&topoName=" + topoName;
        window.top.jQuery.workspace.openTab("业务拓扑" + topoName, url);
    } else if ("virtlinktopo" == type) {
        var url = window.top.ctxpath + "/platform/iradar/VirtLinkTopoIndex.action?topoId=" + id + "&topoName=" + topoName;
        window.top.jQuery.workspace.openTab("虚拟链路拓扑" + topoName, url);
    }
}
/**
 * 提交校验
 * @param obj
 * @returns {Boolean}
 */

function doSubmit(obj) {
    if (jQuery.trim(jQuery('#name', '#tPicForm').val()) == '') {
        alert('图片名称不能为空！', '信息提示', function() {
            jQuery('#name', '#tPicForm').focus();
        });
        return false;
    }

    var re = /^[0-9a-zA-Z\u4e00-\u9fa5]+$/;
    if (!re.exec(jQuery.trim(jQuery('#name', '#tPicForm').val()))) {
        alert('只能输入汉字、字母或数字！', '信息提示', function() {
            jQuery('#name', '#cu-widget').focus();
        });
        return false;
    }

    if (jQuery.trim(jQuery('#file', '#tPicForm').val()) == '') {
        alert('请选择图片！', '信息提示', function() {
            jQuery('#file', '#tPicForm').focus();
        });
        return false;
    }

    var filePath = jQuery('#file', '#tPicForm').val();
    var fileExt = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
    if (!checkFileExt(fileExt)) {
        alert("您上传的文件不是图片文件,请重新上传！", "信息提示");
        return false;
    }
}

function checkFileExt(ext) {
    if (!ext.match(/.jpg|.jpeg|.gif|.png/i)) {
        return false;
    }
    return true;
}

/**
 * 验证上传的文件大小是否超标
 * @param obj
 * @returns {Boolean}
 */

function checkfile(obj) {

    var fileSize = 0;
    var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
    if (isIE && !obj.files) {
        var filePath = obj.value;
        var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
        var file = fileSystem.GetFile(filePath);
        fileSize = file.Size;
    } else {
        fileSize = obj.files[0].size;
    }

    fileSize = Math.round(fileSize / 1024 * 100) / 100; //单位为KB

    if (fileSize >= 10240) {
        alert('文件大小不能超过10M，请重新上传！', '信息提示', function() {
            jQuery('#file', '#tPicForm').focus();
        });
        obj.value = "";
        return false;
    }
    jQuery('#file').val(obj.value);
}
