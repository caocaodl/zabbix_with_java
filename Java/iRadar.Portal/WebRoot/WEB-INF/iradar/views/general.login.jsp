<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.isoft.iradar.*"%>
<%@ page import="static com.isoft.iradar.inc.FuncsUtil.*"%>
<%@ page import="static com.isoft.iradar.inc.Defines.*"%>
<%@ page import="com.isoft.iradar.web.views.*"%>
<%@ page import="com.isoft.types.CMap"%>
<%@ page import="com.isoft.iradar.model.CWebUser"%>
<%@ page import="com.isoft.Feature" %>
<%@ page import="com.isoft.types.Mapper.Nest"%>
<%@ page import="static com.isoft.iradar.Cphp.*"%>
<%
define("RDA_PAGE_NO_HEADER", 1);
define("RDA_PAGE_NO_FOOTER", 1);

CMap _REQUEST = RadarContext._REQUEST();
String requestUrl =get_request("request", "");
String message =get_request("message", "");
CViewPageHeader.renderAndShow(RadarContext.getIdentityBean());
%>
<div class="login">
    <div id="glow">
        <div class="loginForm">
            <div style="position: relative; color: #FFF; height: 100%;">
                <!-- Help & Support -->
                <div style="position: absolute; top: 0px; right: 10px;">
                    <a class="highlight" href="http://www.i-soft.com.cn/documentation"><%=_("Help")%></a>
                    &nbsp;|&nbsp;
                    <a class="highlight" href="http://www.i-soft.com.cn/support"><%=_("Support")%></a>
                </div>

                <!-- Copyright -->
                <div style="float: left; width: 250px; height: 100%;">
                    <div style="position: absolute; top: 39%; left: 30px;" class="loginLogo"></div>
                    <div style="position: absolute; bottom: 2px;">
                            <span class="bold textwhite" style="margin: 0 0 4px 4px; font-size: 0.9em;">
                                <%=_s("iRadar %1$s Copyright %2$s-%3$s by iRadar SIA",
                                    IRADAR_VERSION, IRADAR_COPYRIGHT_FROM, IRADAR_COPYRIGHT_TO)%>
                            </span>
                    </div>
                </div>

                <!-- Login Form -->
                <div style="height: 100%; padding-top: 58px; padding-right: 40px; margin-left: 275px;">
                    <div style="float: right;">
                        <form action="index.action" method="post">
                            <input type="hidden" name="request" class="input hidden" value="<%=requestUrl%>" />
                            <ul style="list-style-type: none;">
                                <li style="padding-right: 6px; height: 22px;">
                                    <div class="ui-corner-all textwhite bold" style="padding: 2px 4px; float: right; background-color: #D60900; visibility: <%=empty(message)?"hidden":"visible"%>" >
                                        <span class="nowrap"><%=message%></span>
                                    </div>
                                </li>
                                <li style="margin-top: 10px; padding-top: 1px; height: 22px; width: 265px; white-space: nowrap;" >
                                    <div class="label"><%=_("Username")%></div><input type="text" id="name" name="name" class="input" value="<%=Feature.defaultUser%>"/>
                                </li>
                                <li style="margin-top: 10px; padding-top: 1px; height: 22px; width: 265px; white-space: nowrap;" >
                                    <div class="label"><%=_("Password")%></div><input type="password" id="password" name="password" class="input" value="<%=Feature.defaultPassword%>"/>
                                </li>
                                <li style="margin-top: 8px; text-align: center;">
                                    <input type="checkbox" id="autologin" name="autologin" value="1" <%if(get_request("autologin", 1)==1){%>checked="checked<%}%> />
                                    <label for="autologin" class="bold" style="line-height: 20px; vertical-align: top;">
                                        <%=_("Remember me for 30 days")%>
                                    </label>
                                    <div style="height: 8px;"></div>
                                    <input type="submit" class="input jqueryinput" name="enter" id="enter" value="<%=_("Sign in")%>" />
                                    <%if(Nest.as(CWebUser.get("userid")).asLong() > 0L) {%>
                                        <span style="margin-left: 14px;">
                                                <a class="highlight underline" href="dashboard.action"><%=_("Login as Guest")%></a>
                                            </span>
                                    <% } %>
                                </li>
                            </ul>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    jQuery(document).ready(function() {
        jQuery('body').css('background-color', '#E8EAEF');
        jQuery('#enter').button();
        jQuery('#name').focus();
    });
</script>
<%CViewPageFooter.renderAndShow();%>
