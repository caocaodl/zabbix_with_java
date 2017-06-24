<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.*"%>
<%@ page import="com.isoft.iradar.inc.*"%>
<%@ page import="com.isoft.iradar.web.views.*"%>
<%@ page import="com.isoft.iradar.model.*"%>
<%@ page import="com.isoft.types.*"%>
<!doctype html>
<html>
<head>
    <title>WARNING! You are using an outdated browser.</title>
    <meta name="Author" content="iRadar SIA" />
    <meta charset="utf-8" />
    <link rel="shortcut icon" href="images/general/iradar.ico" />
    <link rel="stylesheet" type="text/css" href="styles/default.css" />
    <link rel="stylesheet" type="text/css" href="styles/pages.css" />
</head>
<body class="originalblue">
    <div style="display: table; height: 99%; width: 99%;">
        <div class="vertical-middle">
            <div class="browserwarningForm">
                <div style="position: relative;">
                    <div style="position: absolute; top: 28px; left: 18px;" class="loginLogo"></div>
                    <div style="position: absolute; top: 42px; left: 215px;" class="browserwarningCaution">WARNING! You are using an outdated browser.</div>
                    <div style="position: absolute; top: 90px; left: 50px; width: 510px;">
                        iRadar frontend is built on advanced, modern technologies and does not support old browsers.
                        It is highly recommended that you choose and install a modern browser.
                        It is free of charge and only takes a couple of minutes.
                    </div>
                    <a href="http://www.google.com/chrome" target="_blank"><div style="position: absolute; top: 175px; left: 55px;" class="browserwarningLogoChrome"></div></a>
                    <div style="position: absolute; top: 252px; left: 55px; text-align: center;" class="browserwarningLink">Google Chrome<br/><a href="http://www.google.com/chrome" target="_blank">Download page</a></div>
                    <a href="http://www.mozilla.org/firefox" target="_blank"><div style="position: absolute; top: 175px; left: 160px;" class="browserwarningLogoFirefox"></div></a>
                    <div style="position: absolute; top: 252px; left: 160px; text-align: center;" class="browserwarningLink">Mozilla Firefox<br/><a href="http://www.mozilla.org/firefox" target="_blank">Download page</a></div>
                    <a href="http://windows.microsoft.com/en-US/internet-explorer/downloads/ie" target="_blank"><div style="position: absolute; top: 175px; left: 265px;" class="browserwarningLogoIE"></div></a>
                    <div style="position: absolute; top: 252px; left: 265px; text-align: center;" class="browserwarningLink">Internet Explorer<br/><a href="http://windows.microsoft.com/en-US/internet-explorer/downloads/ie" target="_blank">Download page</a></div>
                    <a href="http://www.opera.com/download" target="_blank"><div style="position: absolute; top: 175px; left: 375px;" class="browserwarningLogoOpera"></div></a>
                    <div style="position: absolute; top: 252px; left: 375px; text-align: center;" class="browserwarningLink">Opera browser<br/><a href="http://www.opera.com/download" target="_blank">Download page</a></div>
                    <a href="http://www.apple.com/safari/download" target="_blank"><div style="position: absolute; top: 175px; left: 480px;" class="browserwarningLogoSafari"></div></a>
                    <div style="position: absolute; top: 252px; left: 480px; text-align: center;" class="browserwarningLink">Apple Safari<br/><a href="http://www.apple.com/safari/download" target="_blank">Download page</a></div>
                    <div style="position: absolute; top: 310px; left: 50px; width: 510px; font-size: 16px; font-weight: bold;">
                        Why is it recommended to upgrade the web browser?
                    </div>
                    <div style="position: absolute; top: 335px; left: 50px; width: 510px;">
                        New browsers usually come with support for new technologies, increasing web page speed, better privacy settings and so on. They also resolve security and functional issues.
                    </div>
                    <div style="position: absolute; top: 420px; left: 23px;" class="browserwarningCopyright">
                        <a href="http://www.i-soft.com.cn"><%=Cphp_s("iRadar %1$s Copyright %2$s-%3$s by iRadar SIA", IRADAR_VERSION, IRADAR_COPYRIGHT_FROM, IRADAR_COPYRIGHT_TO)%></a>
                    </div>
                    <div style="position: absolute; top: 400px; left: 400px;" class="browserwarningLink"><a href="index.php" onClick="javascript: document.cookie='browserwarning_ignore=yes';">Continue despite this warning</a> &raquo;</div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>