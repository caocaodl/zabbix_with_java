import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

public class MonitorJMS {
	private static MBeanServerConnection connection;
	private static JMXConnector connector;
	private static final ObjectName service;
	// Initializing the object name for DomainRuntimeServiceMBean
	// so it can be used throughout the class.
	static {
		try {
			service = new ObjectName("com.bea:Name=DomainRuntimeService, Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean");
		} catch (MalformedObjectNameException e) {
			throw new AssertionError(e.getMessage());
		}
	}

	/*
	 * Initialize connection to the Domain Runtime MBean Server
	 */
	public static void initConnection(String hostname, String portString, String username, String password) throws IOException, MalformedURLException {
		String protocol = "t3";
		Integer portInteger = Integer.valueOf(portString);
		int port = portInteger.intValue();
		String jndiroot = "/jndi/";
		String mserver = "weblogic.management.mbeanservers.domainruntime";
		JMXServiceURL serviceURL = new JMXServiceURL(protocol, hostname, port, jndiroot + mserver);
		Hashtable h = new Hashtable();
		h.put(Context.SECURITY_PRINCIPAL, username);
		h.put(Context.SECURITY_CREDENTIALS, password);
		h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "weblogic.management.remote");
		connector = JMXConnectorFactory.connect(serviceURL, h);
		connection = connector.getMBeanServerConnection();
	}

	/*
	 * Get an array of ServerRuntimeMBeans
	 */
	public static ObjectName[] getServerRuntimes() throws Exception {
		return (ObjectName[]) connection.getAttribute(service, "ServerRuntimes");
	}

	public void getJmsQueueInfo() throws Exception {
		ObjectName[] serverRT = getServerRuntimes();
		ObjectName JMSRT = (ObjectName) connection.getAttribute(serverRT[0], "JMSRuntime");
		ObjectName[] JMSServers = (ObjectName[]) connection.getAttribute(JMSRT, "JMSServers");
		int JMSServer_Length = (int) JMSServers.length;
		for (int x = 0; x < JMSServer_Length; x++) {
			// jmsserver名称
			String JMSServer_name = (String) connection.getAttribute(JMSServers[x], "Name");
			ObjectName[] JMSDests = (ObjectName[]) connection.getAttribute(JMSServers[x], "Destinations");
			int JMSdest_Length = (int) JMSDests.length;
			for (int y = 0; y < JMSdest_Length; y++) {
				// queue名称
				String queue_name = (String) connection.getAttribute(JMSDests[y], "Name");
				long pendingmcount = (Long) connection.getAttribute(JMSDests[y], "MessagesPendingCount");
				// 当前队列中有多少条记录
				long currentcount = (Long) connection.getAttribute(JMSDests[y], "MessagesCurrentCount");

				System.out.println(y + "--[jms server name]: " + JMSServer_name + "  [jms name]: " + queue_name + "   [pending]: " + pendingmcount+ "   [current]: " + currentcount);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		String hostname = "127.0.0.1";
		String portString = "7001";
		String username = "weblogic";
		String password = "weblogic";
		MonitorJMS s = new MonitorJMS();
		initConnection(hostname, portString, username, password);
		s.getJmsQueueInfo();
		connector.close();
	}
}