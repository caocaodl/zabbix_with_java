import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;


public class Deamon {
	static String ip = "127.0.0.1";
	
	//WebSphere
	

	/**
	 * s持久堆使用大小-Used
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object PermGenUsageUsed(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName4PS = null;
		ObjectName objectName = null;
		ObjectName objectName4CMS = null;
		MemoryUsage mu = null;
		try{
			objectName4PS = new ObjectName("java.lang:type=MemoryPool,name=PS Perm Gen");
			objectName = new ObjectName("java.lang:type=MemoryPool,name=Perm Gen");
			objectName4CMS = new ObjectName("java.lang:type=MemoryPool,name=Code Cache");
			mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4PS, "Usage"));
		}catch(Exception e){
		try{
		mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		}catch(Exception ine){
			try {
		        mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4CMS, "Usage"));
			} catch (Exception ine1) {
				// TODO Auto-generated catch block
				ine1.printStackTrace();
			} 
		}
		}
		return mu.getUsed();
	}
	/**
	 * s持久堆使用大小-max
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object PermGenUsageMax(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName4PS = null;
		ObjectName objectName = null;
		ObjectName objectName4CMS = null;
		MemoryUsage mu = null;
		try{
			objectName4PS = new ObjectName("java.lang:type=MemoryPool,name=PS Perm Gen");
			objectName = new ObjectName("java.lang:type=MemoryPool,name=Perm Gen");
			objectName4CMS = new ObjectName("java.lang:type=MemoryPool,name=Code Cache");
			mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4PS, "Usage"));
		}catch(Exception e){
		try{
		mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		}catch(Exception ine){
			try {
		        mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4CMS, "Usage"));
			} catch (Exception ine1) {
				// TODO Auto-generated catch block
				ine1.printStackTrace();
			} 
		}
		}
		return mu.getUsed();
	}
	/**
	 * JVM历史最大线程数-jvmPeakThreadCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jvmPeakThreadCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Threading");
		Object o = mbsc.getAttribute(objectName, "PeakThreadCount");
		return o;
	}
	
	/**
	 * JVM活动守护线程数-jvmDaemonThreadCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jvmDaemonThreadCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Threading");
		Object o = mbsc.getAttribute(objectName, "DaemonThreadCount");
		return o;
	}
	
	/**
	 * JVM活动线程总数-jvmThreadCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jvmThreadCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Threading");
		Object o = mbsc.getAttribute(objectName, "ThreadCount");
		return o;
	}
	
	/**
	 * JVM堆内存使用量--当前
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jvmHeapUsageUsed(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=MemoryPool,name=Java heap");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		return mu.getUsed();
	}
	
	/**
	 * JVM堆内存使用量--最大
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jvmHeapUsageMax(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=MemoryPool,name=Java heap");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		return mu.getMax();
	}
	
	/**
	 * 杂项非堆内存使用量-当前
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object miscellaneousNoneHeapUsageUsed(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=MemoryPool,name=miscellaneous non-heap storage");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		return mu.getUsed();
	}
	
	/**
	 * 杂项非堆内存使用量-最大
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object miscellaneousNoneHeapUsageMax(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=MemoryPool,name=miscellaneous non-heap storage");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		return mu.getMax()==-1?mu.getUsed():mu.getMax();
	}
	
	
	//Weblogic
	
	/**
	 * AdminServer当前JMS服务数-JMSServersCurrentCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jmsServersCurrentCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("com.bea:ServerRuntime=AdminServer,Name=AdminServer.jms,Type=JMSRuntime");
		return mbsc.getAttribute(objectName, "JMSServersCurrentCount");
	}
	
	/**
	 * AdminServer自启动以来最大JMS服务数-JMSServersHighCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jmsServersHighCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("com.bea:ServerRuntime=AdminServer,Name=AdminServer.jms,Type=JMSRuntime");
		return mbsc.getAttribute(objectName, "JMSServersHighCount");
	}
	
	/**
	 * AdminServer当前JMS连接数-ConnectionsCurrentCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jmsConnectionsCurrentCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("com.bea:ServerRuntime=AdminServer,Name=AdminServer.jms,Type=JMSRuntime");
		return mbsc.getAttribute(objectName, "ConnectionsCurrentCount");
	}
	
	/**
	 * AdminServer自启动以来JMS最大连接数-ConnectionsHighCount
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object jmsConnectionsHighCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("com.bea:ServerRuntime=AdminServer,Name=AdminServer.jms,Type=JMSRuntime");
		return mbsc.getAttribute(objectName, "ConnectionsHighCount");
	}
	

	
	//Tomcat
	
	/**
	 * 持久堆使用大小-Max
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object psPermGenUsageMax(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName4PS = null;
		ObjectName objectName = null;
		ObjectName objectName4CMS = null;
		MemoryUsage mu = null;
		try{
			objectName4PS = new ObjectName("java.lang:type=MemoryPool,name=PS Perm Gen");
			objectName = new ObjectName("java.lang:type=MemoryPool,name=Perm Gen");
			objectName4CMS = new ObjectName("java.lang:type=MemoryPool,name=CMS Perm Gen");
			mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4PS, "Usage"));
		}catch(Exception e){
			try{
	            mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
			}catch(Exception ine){
				try {
			        mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4CMS, "Usage"));
				} catch (Exception ine1) {
					// TODO Auto-generated catch block
					ine1.printStackTrace();
				} 
			}
		}
		return mu.getMax();
	}
	
	/**
	 * 持久堆使用大小-Used
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object psPermGenUsageUsed(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName4PS = null;
		ObjectName objectName = null;
		ObjectName objectName4CMS = null;
		MemoryUsage mu = null;
		try{
			objectName4PS = new ObjectName("java.lang:type=MemoryPool,name=PS Perm Gen");
			objectName = new ObjectName("java.lang:type=MemoryPool,name=Perm Gen");
			objectName4CMS = new ObjectName("java.lang:type=MemoryPool,name=CMS Perm Gen");
			mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4PS, "Usage"));
		}catch(Exception e){
		try{
		mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "Usage"));
		}catch(Exception ine){
			try {
		        mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName4CMS, "Usage"));
			} catch (Exception ine1) {
				// TODO Auto-generated catch block
				ine1.printStackTrace();
			} 
		}
		}
		return mu.getUsed();
	}
	
	/**
	 * 非堆使用大小-Max
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object nonHeapMemoryUsageMax(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Memory");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "NonHeapMemoryUsage"));
		return mu.getMax()==-1?mu.getUsed():mu.getMax();
	}
	
	/**
	 * 非堆使用大小-Used
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object nonHeapMemoryUsageUsed(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Memory");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "NonHeapMemoryUsage"));
		return mu.getUsed();
	}
	
	/**
	 * 堆使用大小-Max
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object heapMemoryUsageMax(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Memory");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "HeapMemoryUsage"));
		return mu.getMax();
	}
	
	/**
	 * 堆使用大小-Used
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object heapMemoryUsageUsed(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Memory");
		MemoryUsage mu = MemoryUsage.from((CompositeDataSupport)mbsc.getAttribute(objectName, "HeapMemoryUsage"));
		return mu.getUsed();
	}
	
	/**
	 * 启动时间
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object startTime(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Runtime");
		Long time = (Long)mbsc.getAttribute(objectName, "StartTime");
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
	}
	
	/**
	 * 运行时间
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object uptime(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objectName = new ObjectName("java.lang:type=Runtime");
		Long time = (Long)mbsc.getAttribute(objectName, "Uptime");
		return time;
	}
	
	/**
	 * 总会话数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object sessionCounter(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName queryObjectName = new ObjectName("Catalina:type=Manager,*");
		Set<ObjectName> objNames = mbsc.queryNames(queryObjectName, null);
		
		long total = 0;
		for(ObjectName objName: objNames) {
			Object count = mbsc.getAttribute(objName, "sessionCounter");
			total += Long.valueOf(String.valueOf(count));
		}
		return total;
	}
	
	/**
	 * 当前会话数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object activeSessions(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName queryObjectName = new ObjectName("Catalina:type=Manager,*");
		Set<ObjectName> objNames = mbsc.queryNames(queryObjectName, null);
		
		int total = 0;
		for(ObjectName objName: objNames) {
			Object count = mbsc.getAttribute(objName, "activeSessions");
			total += (Integer)count;
		}
		return total;
	}
	
	
	/**
	 * 错误请求数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object errorCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objName = queryName(mbsc, tomcatPort, "Catalina:type=GlobalRequestProcessor,*"); 
		if(objName != null) {
			Object o = mbsc.getAttribute(objName, "errorCount");
			return o;
		}
		return -1;
	}
	
	/**
	 * HTTP请求数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object requestCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objName = queryName(mbsc, tomcatPort, "Catalina:type=GlobalRequestProcessor,*"); 
		if(objName != null) {
			Object o = mbsc.getAttribute(objName, "requestCount");
			return o;
		}
		return -1;
	}
	
	/**
	 * 是否启用gzip压缩
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object gzip(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objName = new ObjectName("Catalina:type=ProtocolHandler,port="+tomcatPort);
		Object o = mbsc.getAttribute(objName, "compression");
		return "on".equals(o)? 1: 0;
	}

	/**
	 * 最大线程数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object maxThreads(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objName = queryName(mbsc, tomcatPort, "Catalina:type=ThreadPool,*"); 
		if(objName != null) {
			Object o = mbsc.getAttribute(objName, "maxThreads");
			return o;
		}
		return -1;
	}
	
	/**
	 * 当前线程数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object currentThreadCount(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objName = queryName(mbsc, tomcatPort, "Catalina:type=ThreadPool,*"); 
		if(objName != null) {
			Object o = mbsc.getAttribute(objName, "currentThreadCount");
			return o;
		}
		return -1;
	}
	
	/**
	 * 繁忙线程数
	 * 
	 * @param mbsc
	 * @param tomcatPort
	 * @return
	 * @throws Exception
	 */
	public static Object currentThreadsBusy(MBeanServerConnection mbsc, String tomcatPort) throws Exception {
		ObjectName objName = queryName(mbsc, tomcatPort, "Catalina:type=ThreadPool,*"); 
		if(objName != null) {
			Object o = mbsc.getAttribute(objName, "currentThreadsBusy");
			return o;
		}
		return -1;
	}
	
	/**
	 * 查询tomcat对应端口的信息
	 * 
	 * @param mbsc
	 * @param port
	 * @param queryName
	 * @return
	 * @throws Exception
	 */
	private static ObjectName queryName(MBeanServerConnection mbsc, String port, String queryName) throws Exception {
		ObjectName queryObjectName = new ObjectName(queryName);
		Set<ObjectName> objNames = mbsc.queryNames(queryObjectName, null);
		for(ObjectName objName: objNames) {
			String name = objName.getKeyProperty("name");
			if(name.endsWith("\"")) {
				name = name.substring(0, name.length()-1);
			}
			if(name.endsWith("-"+port)) {
				return objName;
			}
		}
		return null;
	}
	
	private static void showUsage(List<String> cmds) {
		System.out.println("java -jar cmd-jmx.jar <cmd> <tomcat_port> <jmx_port> [<jmx_ip>]");
		System.out.print("cmd:  ");
		for(String cmd: cmds) {
			System.out.print(cmd + " | ");
		}
		System.out.println();
	}
	
	private static void call(String jmxPort, String tomcatPort, String cmd) {
		JMXConnector connector = null;
		try {
			String jmxURL;
			if(jmxPort.equals("6888")){
				jmxURL="service:jmx:iiop:///jndi/corbaname::1.2@"+ip+":"+jmxPort+"#jmx/rmi/RMIConnectorServer";

			}
			else{
				jmxURL = "service:jmx:rmi:///jndi/rmi://"+ip+":"+jmxPort+"/jmxrmi";// tomcat jmx url
			}
			JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);
			Map map = new HashMap();
			String[] credentials = new String[] { "monitorRole", "QED" };
			if(jmxPort.equals("6888")){
				credentials = new String[] { "admin", "Mjy461628134" };
			}
			
			map.put("jmx.remote.credentials", credentials);
			
			//中创中间件（临时）
			if(jmxPort.equals("8686")){
				connector = JMXConnectorFactory.connect(serviceURL);
			}else{
				connector = JMXConnectorFactory.connect(serviceURL, map);
			}
			MBeanServerConnection mbsc = connector.getMBeanServerConnection();
			
			Method m = Deamon.class.getMethod(cmd, new Class[] {MBeanServerConnection.class, String.class});
			Object o = m.invoke(null, mbsc, tomcatPort);
			System.out.println(o);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(connector != null) {
				try {
					connector.close();
				} catch (IOException e) {
				}
				connector = null;
			}
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List<String> cmds = new ArrayList<String>();
		
		//args=new String[]{"psPermGenUsageMax","8080","9001"};
		Method[] methods = Deamon.class.getMethods();
		for(Method m: methods) {
			Class[] params = m.getParameterTypes();
			if(params.length>0 && params[0].equals(MBeanServerConnection.class)) {
				String name = m.getName();
				cmds.add(name);
			}
		}
		
		if(args.length >= 3) {
			String cmd = args[0];
			String tomcatPort = args[1];
			String jmxPort = args[2];
			if(args.length >= 4) {
				ip = args[3];
			}
			
			if(cmds.contains(cmd)) {
				call(jmxPort, tomcatPort, cmd);
				return;
			}
		}
		showUsage(cmds);
	}
	
}