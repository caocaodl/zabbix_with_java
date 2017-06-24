package com.isoft.web.bean.iface;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.isoft.biz.dao.common.IProfDAO;

import com.isoft.biz.dao.portserves.ISendMessageInterfaceDAO;
import com.isoft.biz.dto.ParamDTO;
import com.isoft.biz.handler.common.IProfHandler;
import com.isoft.biz.handler.portserves.ISendMessageInterfaceHandler;
import com.isoft.consts.ModuleConstants;
import com.isoft.framework.common.IdentityBean;
import com.isoft.framework.common.RequestEvent;
import com.isoft.framework.common.interfaces.IResponseEvent;
import com.isoft.web.bean.iface.mail.MailSenderInfo;
import com.isoft.web.bean.iface.mail.SimpleMailSender;
import com.isoft.web.common.IaasPageAction;
@Path("/send") 
public class SendMessageInterface extends IaasPageAction{
	private IdentityBean idBean = new IdentityBean();

	protected IdentityBean getIdentityBean() {
	return this.idBean;
	}
	
	    @Path("/messsageMail")  
	    @POST  
	    @Consumes("application/x-www-form-urlencoded")   
	    @Produces("text/plain")  
	    public String doSendMessageMail(
	    		@FormParam("objectMessage") String objectMessage,
	    		@FormParam("contentMessage") String contentMessage,
	    		@FormParam("fromAddress") String fromAddress,
	    		@FormParam("userName") String userName,
	    		@FormParam("password") String password,
	    		@FormParam("subject") String subject,
	    		@FormParam("mailServerHost") String mailServerHost,
	    		@FormParam("mailServerPort") String mailServerPort){
	    	  String value=null;
	    	  Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
	    	  Matcher matcher = pattern.matcher(objectMessage);
	    	   if(matcher.matches()){
	    		  
	    		   Matcher matcher1 = pattern.matcher(fromAddress); 
	    	   if(matcher1.matches()){
	    		   MailSenderInfo mailInfo = new MailSenderInfo();   
	 	          mailInfo.setMailServerHost(mailServerHost);   
	 	          mailInfo.setMailServerPort(mailServerPort);   
	 	          mailInfo.setValidate(true);   
	 	          mailInfo.setUserName(userName);   
	 	          mailInfo.setPassword(password);//您的邮箱密码   
	 	          mailInfo.setFromAddress(fromAddress);   
	 	          mailInfo.setToAddress(objectMessage);   
	 	          mailInfo.setSubject(subject);   
	 	          mailInfo.setContent(contentMessage);   
	 	             //这个类主要来发送邮件  
	 	          SimpleMailSender sms = new SimpleMailSender();  
	 	        boolean flag=sms.sendTextMail(mailInfo);//发送文体格式   
	 	        if(flag){
	 	        	value="发送成功";
	 	        }else{
	 	        	value="发送失败";
	 	        }
	    			 
	    	  	
	    		}else{
	    			value="请输入正确发送者邮箱格式";
	    			
	    		}
	    		   
	    	   }else{
	    		   value="请输入正确接收人的邮箱格式";
	    		   
	    	   }
	    	 
	         // sms.sendHtmlMail(mailInfo);//发送html格式  
	    	
	    	return value.toString();
	    	
	    }
	    @Path("/messsageTel")  
	    @POST  
	    @Consumes("application/x-www-form-urlencoded")   
	    @Produces("text/plain")  
	    public String doSendMessageTel(
	    		@FormParam("objectMessage") String objectMessage,  //接受者手机号
	    		@FormParam("contentMessage") String contentMessage, //短信你内容
	    		@FormParam("TelNumbmer") String TelNumbmer   //接受者手机号
	    		){
	    	String value="";
	    	RequestEvent request = new RequestEvent();
	    	request.setCallDAOIF(ISendMessageInterfaceDAO.class);
			request.setCallHandlerIF(ISendMessageInterfaceHandler.class);
			request.setCallHandlerMethod(ISendMessageInterfaceHandler.doSendMessageTel);
			request.setModuleName(ModuleConstants.MODULE_INTERFACE_SERVISE);
			//request.setDataSource(DataSourceEnum.IRADAR);
			ParamDTO paramDTO = new ParamDTO();
			Map param = getVo();
			param.put("objectMessage", objectMessage);
			param.put("contentMessage",contentMessage);
			param.put("TelNumbmer", TelNumbmer);
			paramDTO.setMapParam(param);
			request.setDTO(paramDTO);

			IResponseEvent response = delegator(request);
			ParamDTO dto = (ParamDTO) response.getDTO();
			boolean success = dto.getBoolParam();
	        if(success){
	        	value="发送成功";
	        }else{
	        	value="发送失败";
	        	
	        }  
			return value.toString();
			
			
	    }
	   
		
}

