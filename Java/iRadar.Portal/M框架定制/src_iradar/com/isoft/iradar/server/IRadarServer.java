package com.isoft.iradar.server;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.strlen;
import static com.isoft.types.CArray.map;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.isoft.Feature;
import com.isoft.iradar.utils.CJs;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

/**
 * A class for interacting with the iRadar server.
 * Class IRadarServer
 */
public class IRadarServer {
	
	/**
	 * Return item queue overview.
	 */
	public final static String QUEUE_OVERVIEW = "overview";

	/**
	 * Return item queue overview by proxy.
	 */
	public final static String QUEUE_OVERVIEW_BY_PROXY = "overview by proxy";

	/**
	 * Return a detailed item queue.
	 */
	public final static String QUEUE_DETAILS = "details";

	/**
	 * Response value if the request has been executed successfully.
	 */
	final static String RESPONSE_SUCCESS = "success";

	/**
	 * Response value if an error occurred.
	 */
	final static String RESPONSE_FAILED = "failed";

	/**
	 * iRadar server host name.
	 *
	 * @var string
	 */
	protected String host;

	/**
	 * iRadar server port number.
	 *
	 * @var int
	 */
	protected int port;

	/**
	 * Request timeout.
	 *
	 * @var int
	 */
	protected int timeout;

	/**
	 * Maximum response size. If the size of the response exceeds this value, an error will be triggered.
	 *
	 * @var int
	 */
	protected int totalBytesLimit;

	/**
	 * Bite count to read from the response with each iteration.
	 *
	 * @var int
	 */
	protected int readBytesLimit = 8192;

	/**
	 * iRadar server socket resource.
	 *
	 * @var resource
	 */
	protected Socket socket;

	/**
	 * Error message.
	 *
	 * @var string
	 */
	protected String error;
	
	/**
	 * Class constructor.
	 *
	 * @param string host
	 * @param int port
	 * @param int timeout
	 * @param int totalBytesLimit
	 */
	public IRadarServer(String host, int port, int timeout, int totalBytesLimit) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.totalBytesLimit = totalBytesLimit;
	}
	
	/**
	 * Executes a script on the given host and returns the result.
	 *
	 * @param scriptId
	 * @param hostId
	 *
	 * @return bool|array
	 */
	public Object executeScript(String scriptId, String hostId) {
		return executeScript(Nest.as(scriptId).asLong(),Nest.as(hostId).asLong());
	}
	
	public Object executeScript(long scriptId, long hostId) {
		return request(map(
			"request", "command",
			"nodeid", "0",
			"scriptid", scriptId,
			"hostid", hostId,
			"tenantid", Feature.defaultTenantId
		));
	}
	
	/**
	 * Retrieve item queue information.
	 *
	 * Possible type values:
	 * - self::QUEUE_OVERVIEW
	 * - self::QUEUE_OVERVIEW_BY_PROXY
	 * - self::QUEUE_DETAILS
	 *
	 * @param string type
	 * @param string sid   user session ID
	 *
	 * @return bool|array
	 */
	public Object getQueue(String type, String sid) {
		return request(map(
			"request", "queue.get",
			"sid", sid,
			"type", type
		));
	}
	
	/**
	 * Returns true if the iRadar server is running and false otherwise.
	 *
	 * @return bool
	 */
	public boolean isRunning() {
		return connect();
	}
	
	public void close(){
		if (this.socket != null) {
			IOUtils.closeQuietly(this.socket);
		}
	}

	/**
	 * Returns the error message.
	 *
	 * @return string
	 */
	public String getError() {
		return this.error;
	}
	
	/**
	 * Executes a given JSON request and returns the result. Returns false if an error has occurred.
	 *
	 * @param array params
	 *
	 * @return mixed    the output of the script if it has been executed successfully or false otherwise
	 */
	protected Object request(CArray params) {
		// connect to the server
		if (!connect()) {
			return false;
		}
		StringBuilder echo = new StringBuilder();
		try {
			DataOutputStream out = new DataOutputStream(this.socket.getOutputStream());  
			BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			out.write(CJs.encodeJson(params).getBytes());
			out.flush();
			String line = null;
			while ((line = in.readLine()) != null) {
				echo.append(line);
			}
			out.close();  
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			this.error = _(e.getMessage());
			return false;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}

		String responseStr = echo.toString();
		// check if the response is empty
		if (strlen(responseStr)==0) {
			this.error = _s("Empty response received from iRadar server \"%1$s\".", host);
			return false;
		}
		Map response = CJs.decodeJson(responseStr);
		if (empty(response) || !validateResponse(response)) {
			this.error = _s("Incorrect response received from iRadar server \"%1$s\".", host);
			return false;
		}

		// request executed successfully
		if (RESPONSE_SUCCESS.equals(Nest.value(response,"response").asString())) {
			return Nest.value(response,"data").$();
		} else {// an error on the server side occurred
			this.error = Nest.value(response, "info").asString();
			return false;
		}
	}

	/**
	 * Opens a socket to the iRadar server. Returns the socket resource if the connection has been established or
	 * false otherwise.
	 *
	 * @return boolean
	 */
	protected boolean connect() {
		if (this.socket == null) {
			if (this.host == null || this.host.length() == 0 || this.port == 0) {
				return false;
			}

			this.socket = new Socket();
			SocketAddress endpoint = new InetSocketAddress(this.host, this.port);
			try {
				this.socket.connect(endpoint, timeout*1000);
			} catch (IOException e) {
				this.error = _s("Cannot connect to the iradar server:port:%1$s:%2$d",this.host, this.port);
				
				this.close();
				this.socket = null;
				
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Returns true if the response received from the iRadar server is valid.
	 * @param array response
	 * @return bool
	 */
	protected boolean validateResponse(Map response) {
		return (isset(response,"response")
					&& (
							(RESPONSE_SUCCESS.equals(Nest.value(response,"response").asString()) && isset(response,"data"))
						 || (RESPONSE_FAILED.equals(Nest.value(response,"response").asString()) && isset(response,"info"))
					));
	}
}
