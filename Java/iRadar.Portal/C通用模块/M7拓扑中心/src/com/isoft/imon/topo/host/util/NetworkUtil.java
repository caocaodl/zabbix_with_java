package com.isoft.imon.topo.host.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.StringTokenizer;

/**关于网络的通用操作类
 * @author soft
 *
 */
public final class NetworkUtil {
	private static final long ipTotal = Long.parseLong("4294967295");

	private static final long aAddressStart = ipToLong("10.0.0.0");
	private static final long aAddressEnd = ipToLong("10.255.255.255");

	private static final long bAddressStart = ipToLong("172.16.0.0");
	private static final long bAddressEnd = ipToLong("172.31.255.255");

	private static final long cAddressStart = ipToLong("192.168.0.0");
	private static final long cAddressEnd = ipToLong("192.168.255.255");

	public static boolean isPrivateAddress(String ipAddress) {
		return isPrivateAddress(ipToLong(ipAddress));
	}

	public static boolean isPrivateAddress(long ipLong) {
		if ((ipLong >= aAddressStart) && (ipLong <= aAddressEnd))
			return true;
		if ((ipLong >= bAddressStart) && (ipLong <= bAddressEnd)) {
			return true;
		}
		return (ipLong >= cAddressStart) && (ipLong <= cAddressEnd);
	}

	/**ip地址 转换long类型的
	 * @param ipAddress
	 * @return
	 */
	public static long ipToLong(String ipAddress) {
		int[] ipSegment = parseIp(ipAddress);
		if (ipSegment == null)
			return 0L;

		long longIp = 0L;
		int k = 24;
		for (int i = 0; i < ipSegment.length; i++) {
			longIp += (ipSegment[i] << k);
			k -= 8;
		}
		return longIp;
	}

	/**long类型转换成ip地址
	 * @param ip
	 * @return
	 */
	public static String longToIp(long ip) {
		int[] b = new int[4];
		b[0] = (int) (ip >> 24 & 0xFF);
		b[1] = (int) (ip >> 16 & 0xFF);
		b[2] = (int) (ip >> 8 & 0xFF);
		b[3] = (int) (ip & 0xFF);
		return b[0] + "." + b[1] + "." + b[2] + "." + b[3];
	}

	/**转化ip变为int类型的数组
	 * @param ipAddress
	 * @return
	 */
	public static int[] parseIp(String ipAddress) {
		if (!checkIp(ipAddress))
			return null;
		int[] ipSegment = new int[4];

		StringTokenizer st = new StringTokenizer(ipAddress, ".");
		for (int i = 0; i < 4; i++)
			ipSegment[i] = Integer.parseInt(st.nextToken());
		return ipSegment;
	}

	/**判断是不是ip
	 * @param ipAddress
	 * @return
	 */
	public static boolean checkIp(String ipAddress) {
		boolean isValid = true;
		try {
			StringTokenizer st = new StringTokenizer(ipAddress, ".");
			int len = st.countTokens();
			if (len != 4)
				return false;

//			int ipSegment = 0;
			for (int i = 0; i < len; i++) {
				int ipSegment = Integer.parseInt(st.nextToken());
				if ((ipSegment < 0) || (ipSegment > 255)) {
					isValid = false;
					break;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return isValid;
	}

	public static boolean isNetAddress(String ipAddress, String netMask) {
		if (("0.0.0.0".equals(ipAddress))|| ("255.255.255.255".equals(netMask))	|| (ipAddress.equals(netMask))) {
			return false;
		}
		int[] ips = parseIp(ipAddress);
		int[] masks = parseIp(netMask);
		if(ips==null || masks==null){
			return false;
		}
//		String result = null;
		StringBuffer result = null;
		for (int i = 0; i < 4; i++) {
			if (result == null){
//				result = String.valueOf(ips[i] & masks[i]);
				result = new StringBuffer(String.valueOf(ips[i] & masks[i]));
			}else {
//				result = result + "." + (ips[i] & masks[i]);
				result.append("." + (ips[i] & masks[i]));
			}
		}
		
		if (result == null) {
			return false;
		}
	
		return result.toString().equals(ipAddress);
	}

	public static String getNetAddress(String ipAddress, String netMask) {
		try {
			int[] ips = parseIp(ipAddress);
			int[] masks = parseIp(netMask);
//			String result = null;
			StringBuffer result = null;
			if(ips == null || masks == null){
				return null;
			}
			
			for (int i = 0; i < 4; i++) {
				if (result == null){
//					result = String.valueOf(ips[i] & masks[i]);
					result = new StringBuffer(String.valueOf(ips[i] & masks[i]));
				}else{
//					result = result + "." + (ips[i] & masks[i]);
					result.append("." + (ips[i] & masks[i]));
				}
			}
			return result.toString();
		} catch (Exception e) {
			System.out.println(ipAddress + "===" + netMask);
		}
		return "";
	}

	/**得到子网总数
	 * @param netMask
	 * @return
	 */
	public static int getSubnetIPTotal(String netMask) {
		int[] masks = parseIp(netMask);
		if (masks == null)
			return 0;

		int ipTotal = 0;
		for (int i = 0; i < 4; i++) {
			if (masks[i] != 255) {
				int distance = (256 - masks[i]);
				if(i == 3) {
					ipTotal = distance;
				}else if(i == 2) {
					ipTotal = distance * 256;
				}else if(i == 1) {
					ipTotal = distance * 256 * 256;
				}
				break;
			}
		}
		return ipTotal - 2;
	}

	public static boolean isValidIP(long netAddress, long netMask,
			String ipAddress) {
		long ipAddressLong = ipToLong(ipAddress);
		long ipTotalOfSubnet = ipTotal - netMask;

		return (ipAddressLong > netAddress)
				&& (ipAddressLong < netAddress + ipTotalOfSubnet);
	}

	public static boolean isValidIP(String netAddress, String netMask,
			String ipAddress) {
		return isValidIP(ipToLong(netAddress), ipToLong(netMask), ipAddress);
	}

	public static boolean ipInRange(String ip, String startIp, String endIp) {
		long ipLong = ipToLong(ip);
		long startIpLong = ipToLong(startIp);
		long endIpLong = ipToLong(endIp);

		return (ipLong >= startIpLong) && (ipLong <= endIpLong);
	}

	public static boolean ipInRange(long ip, long startIp, long endIp) {
		return (ip >= startIp) && (ip <= endIp);
	}

	public static void main(String args[]){
		getLocalIP();
	}
	/**得到本地ip
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String getLocalIP() {
		try {
			Enumeration nis = NetworkInterface.getNetworkInterfaces();
			while (nis.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) nis.nextElement();
				Enumeration ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress ia = (InetAddress) ips.nextElement();
					if ((!ia.isLoopbackAddress())
							&& (checkIp(ia.getHostAddress())))
						return ia.getHostAddress();
				}
			}
			return InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "127.0.0.1";
	}

	
	public static String getHexAddress(String address) {
		try {
			StringBuffer hexAddress = null;
			String[] adds = address.split("\\.");
			for (int i = 0; i < adds.length; i++) {
				String digit = Integer.toHexString(new Integer(adds[i])
						.intValue());
				if (digit.length() == 1)
					digit = "0" + digit;
				if (hexAddress == null){
					hexAddress = new StringBuffer(digit);
				}else{
					hexAddress.append(":" + digit);
				}
			}
			if(hexAddress!=null){
				return hexAddress.toString().toLowerCase();				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**返回ping命令得到的报告
	 * @param ipAddress
	 * @return
	 */
	public static String pingReport(String ipAddress) {
		Process process = null;
		BufferedReader in = null;
		String pingInfo = null;
		try {
//			String cmd = null;
			if (System.getProperty("os.name").indexOf("Windows") != -1){
				String cmd = "ping -n 2 " + ipAddress;
				process = Runtime.getRuntime().exec(cmd);
			}else{
				String cmd = "ping -c 2 " + ipAddress;
				process = Runtime.getRuntime().exec(cmd);
			}
//			process = Runtime.getRuntime().exec(cmd);
			in = new BufferedReader(new InputStreamReader(process
					.getInputStream(), "GBK"));
			StringBuffer sb = new StringBuffer(50);
			sb.append("From ").append(getLocalIP()).append("\n");
			String line = null;
			while ((line = in.readLine()) != null)
				sb.append(line + "\n");
			pingInfo = sb.toString().toLowerCase();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if(process != null){
					process.destroy();
				}
				if(in != null){ in.close(); }
				
			} catch (Exception localException1) {
				localException1.printStackTrace();
			}
		}
		return pingInfo;
	}

	public static boolean ping(String ipAddress) {
		String report = pingReport(ipAddress);
		if(report == null){
			return false;
		}
		return report.indexOf("ttl=") >= 1;
	}
	

	public static String getNetAddress(String ipAddress) {
		int dotLoc = ipAddress.lastIndexOf(".");
		return ipAddress.substring(0, dotLoc) + ".0";
	}

	public static String getMaskAddress(int len) {
		if (len > 32)
			return null;

		StringBuffer ipstr = new StringBuffer(32);
		StringBuffer temp = new StringBuffer();
		for (int i = 0; i < 32; i++) {
			if (i < len)
				temp.append("1");
			else
				temp.append("0");
			if ((i + 1) % 8 == 0) {
				ipstr.append(Integer.parseInt(temp.toString(), 2));
				if (i < 31)
					ipstr.append(".");
				temp = new StringBuffer();
			}
		}
		return ipstr.toString();
	}

	/**判断是否可达
	 * @param ip
	 * @param timeout
	 * @return
	 */
	public static synchronized boolean isReachable(String ip, int timeout) {
		try {
			InetAddress address = InetAddress.getByName(ip);
			return address.isReachable(timeout);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String toHexString(String str) {
		try {
			byte[] bytes = str.getBytes();
			StringBuffer mac = new StringBuffer(17);
			for (int i = 0; i < bytes.length; i++) {
				String temp = Integer.toHexString(0xFF & bytes[i]);
				if (temp.length() < 2)
					mac.append(0);
				if (i > 0)
					mac.append(":");
				mac.append(temp.toLowerCase());
			}
			return mac.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**获取最大子网数
	 * @param netMask
	 * @return
	 */
	public static int getMaskBit(String netMask) {
		String[] ips = netMask.split("\\.");
		StringBuffer binary = new StringBuffer();
		for (String ip : ips) {
			binary.append(Integer.toBinaryString(Integer.parseInt(ip))); 
		}
		int bit = 0;
		for (int i = 0; i < binary.toString().length(); i++) {
			if (binary.toString().charAt(i) == '1')
				bit++;
		}
		return bit;
	}
	

	public static String getIPFromHex(String octetString) {
		try {
			String[] temps = octetString.split(":");
			int[] bs = new int[4];
			for (int i = 0; i < temps.length; i++) {
				bs[i] = Integer.parseInt(temps[i], 16);
			}
			return bs[0] + "." + bs[1] + "." + bs[2] + "." + bs[3];
		} catch (Exception e) {
			System.out.println("error IPFromHex octetString=" + octetString);
		}
		return null;
	}
	
	//TODO: com.isoft.ims.host.util.NetworkUtil.jnaPing(String)
	public static Integer jnaPing(String ipAddress) {
		throw new UnsupportedOperationException();
	}
	
}
