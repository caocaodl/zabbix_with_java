/* This file is part of Zapcat.
 *
 * Zapcat is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Zapcat is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * Zapcat. If not, see <http://www.gnu.org/licenses/>.
 */

package com.isoft.iradar.trapitem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * A daemon thread that waits for and forwards data items to a iRadar server.
 * 
 * @author Kees Jan Koster Completely modified by Andrea Dalle Vacche
 */
public final class Sender implements Runnable {
	private static final Logger log = Logger.getLogger(Sender.class);

	private final BlockingQueue<iRadarItem> queue;

	private final Hashtable<String, Integer> iradarServers;

	private final String head;

	private final String host;

	private static final String middle = "</key><data>";

	private static final String tail = "</data></req>";

//	private final byte[] response = new byte[1024];

	private boolean stopping = false;

	private static final int retryNumber = 10;

	private static final int TIMEOUT = 30 * 1000;

	/**
	 * Create a new background sender.
	 * 
	 * @param queue
	 *            The queue to get data items from.
	 * @param iradarServer
	 *            The name or IP of the machine to send the data to.
	 * @param iradarPort
	 *            The port number on that machine.
	 * @param host
	 *            The host name, as defined in the host definition in iRadar.
	 * 
	 */
	public Sender(final BlockingQueue<iRadarItem> queue, Hashtable<String, Integer> iRadarServers, final String host, String tenantId) {
		this.queue = queue;
		this.iradarServers = iRadarServers;
		this.host = host;
		this.head = "<req><host>" + base64Encode(host) + "</host><tid>"+ base64Encode(tenantId) +"</tid><key>";
	}

	/**
	 * Indicate that we are about to stop.
	 */
	public void stopping() {
		stopping = true;
		/* interrupt(); */
	}

	/**
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			final iRadarItem item = queue.take();
			int retryCount = 0;
			trysend1: while (retryCount <= retryNumber) {
				try {
					send(item.getKey(), item.getValue());
					break;
				} catch (Exception e) {
					log.warn("Warning while sending item " + item.getKey() + " value " + item.getValue() + " on host " + host + " retry number " + retryCount + " error:" + e);
					Thread.sleep(1000);
					retryCount++;
					if (retryCount == retryNumber) {
						log.warn("Error i didn't sent item " + item.getKey() + " on iRadar server " + " on host " + host + " tried " + retryCount + " times");
					}
					continue trysend1;
				}
			}

		} catch (InterruptedException e) {
			if (!stopping) {
				log.warn("ignoring exception", e);
			}

		} catch (Exception e) {
			log.warn("ignoring exception", e);
		}

		// drain the queue
		while (queue.size() > 0) {
			final iRadarItem item = queue.remove();
			int retryCount = 0;
			trysend2: while (retryCount <= retryNumber) {
				try {
					send(item.getKey(), item.getValue());
					break;
				} catch (Exception e) {
					log.warn("Warning while sending item " + item.getKey() + " on host " + host + " retry number " + retryCount + " error:" + e);
					retryCount++;
					continue trysend2;
				}

			}
			if (retryCount == retryNumber) {
				log.warn("Error i didn't sent item " + item.getKey() + "  on host " + host + " tried " + retryCount);
			}
		}
	}

	/**
	 * Encodes data for transmission to the server.
	 * 
	 * This method encodes the data in the ASCII encoding, defaulting to the
	 * platform default encoding if that is somehow unavailable.
	 * 
	 * @param data
	 * @return byte[] containing the encoded data
	 */
	protected byte[] encodeString(String data) {
		/**
		 * 中文对应的ASCII码为问号，取消ASCII编码
		 */
		return data.getBytes();
	}

	protected String base64Encode(String data) {
		return new String(Base64.encodeBase64(encodeString(data)));
	}

	private void send(final String key, final String value) throws IOException {
		final StringBuilder message = new StringBuilder(head);
		// message.append(Base64.encode(key));
		message.append(base64Encode(key));
		message.append(middle);
		// message.append(Base64.encode(value == null ? "" : value));
		message.append(base64Encode(value == null ? "" : value));
		message.append(tail);

		if (log.isDebugEnabled()) {
			log.debug("sending " + message);
		}

		Socket iradar = null;
		OutputStreamWriter out = null;
		InputStream in = null;
		Enumeration<String> serverlist = iradarServers.keys();

		while (serverlist.hasMoreElements()) {
			String iradarServer = serverlist.nextElement();
			try {
				iradar = new Socket(iradarServer, iradarServers.get(iradarServer).intValue());
				iradar.setSoTimeout(TIMEOUT);

				out = new OutputStreamWriter(iradar.getOutputStream());
				out.write(message.toString());
				out.flush();

//				in = iradar.getInputStream();
//				final int read = in.read(response);
//				if (log.isDebugEnabled()) {
//					log.debug("received " +new String(response));
//				}
//				if (read != 2 || response[0] != 'O' || response[1] != 'K') {
//					log.warn("received unexpected response '" + new String(response) + "' for key '" + key + "'");
//				}
			} catch (Exception ex) {
				log.error("Error contacting iRadar server " + iradarServer + "  on port " + iradarServers.get(iradarServer));
			}

			finally {
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
				if (iradar != null) {
					iradar.close();
				}
			}
		}
	}
}
