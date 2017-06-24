package com.isoft.imon.topo.engine.discover.credence;

import org.snmp4j.mp.SnmpConstants;

import com.isoft.imon.topo.engine.discover.Credence;
import com.isoft.imon.topo.engine.discover.Sniffer;
import com.isoft.imon.topo.engine.discover.sniffer.SnmpHostSniffer;
import com.isoft.imon.topo.util.CommonUtil;
import com.isoft.iradar.inc.Defines;

/**
 * Snmp凭证类型
 * 
 * @author ldd 2014-2-18
 */
public class SnmpCredence extends Credence {
	// 凭证类型
	public static final String TYPE = "SNMP";
	// 默认端口
	public static final int DEFAULT_PORT = 161;
	// 默认超时时间
	public static final int DEFAULT_TIMEOUT = 1000;
	// 重试次数
	public static final int DEFAULT_RETRIES = 2;
	// 读共同体
	private String community;
	// 写共同体
	private String writeCommunity;
	// 端口号
	private int port;
	// 超时
	private int timeout;
	// 尝试次数
	private int retries;
	// 版本号
	private int version;
	// 嗅探器、分析器
	private Sniffer modeller;
	// SNMP OID
	private String snmpOid;
	// 上下文名称
	private String contextName;
	// 安全名称
	private String securityName;
	// 安全级别
	private int securitylevel;
	/**
	 * 认证协议 //0-->MD5 1-->SHA
	 */
	private int authprotocol;
	// 验证口令
	private String authpassphrase;
	/**
	 * 加密协议 0-->DES 1-->AES
	 */
	private int privprotocol;
	// 隐私密码
	private String privpassphrase;

	/**
	 * 获取上下文名称
	 * 
	 * @return
	 */
	public String getContextName() {
		return contextName;
	}

	/**
	 * 设置上下文名称
	 * 
	 * @param contextName
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	/**
	 * 获取安全名称
	 * 
	 * @return
	 */
	public String getSecurityName() {
		return securityName;
	}

	/**
	 * 设置安全名称
	 * 
	 * @param securityName
	 */
	public void setSecurityName(String securityName) {
		this.securityName = securityName;
	}

	/**
	 * 获取安全级别
	 * 
	 * @return
	 */
	public int getSecuritylevel() {
		return securitylevel;
	}

	/**
	 * 设置安全级别
	 * 
	 * @param securitylevel
	 */
	public void setSecuritylevel(int securitylevel) {
		this.securitylevel = securitylevel;
	}

	/**
	 * 认证协议 0-->MD5 1-->SHA
	 * 
	 * @return String
	 */
	public int getAuthprotocol() {
		return authprotocol;
	}

	/**
	 * 设置认证协议
	 * 
	 * @param authprotocol
	 */
	public void setAuthprotocol(int authprotocol) {
		this.authprotocol = authprotocol;
	}

	/**
	 * 获取验证口令
	 * 
	 * @return
	 */
	public String getAuthpassphrase() {
		return authpassphrase;
	}

	/**
	 * 设置验证口令
	 * 
	 * @param authpassphrase
	 */
	public void setAuthpassphrase(String authpassphrase) {
		this.authpassphrase = authpassphrase;
	}

	/**
	 * 加密协议 0-->DES 1-->AES
	 * 
	 * @return int
	 */
	public int getPrivprotocol() {
		return privprotocol;
	}

	/**
	 * 设置加密协议
	 * 
	 * @param privprotocol
	 */
	public void setPrivprotocol(int privprotocol) {
		this.privprotocol = privprotocol;
	}

	/**
	 * 获取隐私密码
	 * 
	 * @return
	 */
	public String getPrivpassphrase() {
		return privpassphrase;
	}

	/**
	 * 设置隐私密码
	 * 
	 * @param privpassphrase
	 */
	public void setPrivpassphrase(String privpassphrase) {
		this.privpassphrase = privpassphrase;
	}

	/**
	 * 构造方法 version = v2c
	 */
	public SnmpCredence() {
		this.type = TYPE;
		this.port = DEFAULT_PORT;
		this.timeout = DEFAULT_TIMEOUT;
		this.retries = DEFAULT_RETRIES;
		this.version = SnmpConstants.version2c;
	}

	public SnmpCredence(int version) {
		this.type = TYPE;
		this.port = DEFAULT_PORT;
		this.timeout = DEFAULT_TIMEOUT;
		this.retries = DEFAULT_RETRIES;
		this.version = version;
	}

	/**
	 * 获取读共同体
	 * 
	 * @return
	 */
	public String getCommunity() {
		return this.community;
	}

	/**
	 * 设置读共同体
	 * 
	 * @param community
	 */
	public void setCommunity(String community) {
		this.community = community;
	}

	/**
	 * 获取端口号
	 * 
	 * @return
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * 设置端口号
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * 获取超时时间
	 * 
	 * @return
	 */
	public int getTimeout() {
		return this.timeout;
	}

	/**
	 * 设置超时时间
	 * 
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 获取尝试次数
	 * 
	 * @return
	 */
	public int getRetries() {
		return this.retries;
	}

	/**
	 * 设置尝试次数
	 * 
	 * @param retries
	 */
	public void setRetries(int retries) {
		this.retries = retries;
	}

	/**
	 * 获取写共同体
	 * 
	 * @return
	 */
	public String getWriteCommunity() {
		return this.writeCommunity;
	}

	/**
	 * 设置写共同体
	 * 
	 * @param writeCommunity
	 */
	public void setWriteCommunity(String writeCommunity) {
		this.writeCommunity = writeCommunity;
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public int getVersion() {
		return this.version;
	}

	/**
	 * 设置版本号
	 * 
	 * @param version
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/*
	 * 获取snmp服务器嗅探器 (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.Credence#getSniffer()
	 */
	@Override
	public Sniffer getSniffer() {
		if (this.modeller == null) {
			this.modeller = new SnmpHostSniffer();
		}
		return this.modeller;
	}

	/*
	 * (non-Javadoc) 拆分信息
	 * 
	 * @see com.isoft.engine.discover.Credence#unmarshal(java.lang.String)
	 */
	@Override
	public void unmarshal(String config) {
		String[] cfgs = config.split(";");
		for (String cfg : cfgs) {
			String[] kv = cfg.split("=");
			if ("community".equals(kv[0])) {
				this.community = kv[1];
			}
			if ("writeCommunity".equals(kv[0])) {
				this.writeCommunity = kv[1];
			} else if ("port".equals(kv[0])) {
				this.port = Integer.parseInt(kv[1]);
			} else if ("timeout".equals(kv[0])) {
				this.timeout = Integer.parseInt(kv[1]);
			} else if ("retries".equals(kv[0])) {
				this.retries = Integer.parseInt(kv[1]);
			} else if ("version".equals(kv[0])) {
				this.version = Integer.parseInt(kv[1]);
			} else if ("snmpOid".equals(kv[0])) {
				this.snmpOid = kv[1];
			} else if ("contextName".equals(kv[0])) {
				this.contextName = kv[1];
			} else if ("securityName".equals(kv[0])) {
				this.securityName = kv[1];
			} else if ("securitylevel".equals(kv[0])) {
				this.securitylevel = Integer.valueOf(kv[1]);
			} else if ("authprotocol".equals(kv[0])) {
				this.authprotocol = Integer.valueOf(kv[1]);
			} else if ("authpassphrase".equals(kv[0])) {
				this.authpassphrase = kv[1];
			} else if ("privprotocol".equals(kv[0])) {
				this.privprotocol = Integer.valueOf(kv[1]);
			} else if ("privpassphrase".equals(kv[0])) {
				this.privpassphrase = kv[1];
			}
		}
		if (this.port == 0) {
			this.port = DEFAULT_PORT;
		}
		if (this.timeout == 0) {
			this.timeout = DEFAULT_TIMEOUT;
		}
		if (this.retries == 0) {
			this.retries = DEFAULT_RETRIES;
		}
	}

	/*
	 * (non-Javadoc) 汇总信息
	 * 
	 * @see com.isoft.engine.discover.Credence#marshal()
	 */
	@Override
	public String marshal() {
		StringBuffer v = new StringBuffer(100);
		if (this.port != DEFAULT_PORT) {
			v.append("port=").append(this.port).append(";");
		}
		if (this.timeout != DEFAULT_TIMEOUT) {
			v.append("timeout=").append(this.timeout).append(";");
		}
		if (this.retries != DEFAULT_RETRIES) {
			v.append("retries=").append(this.retries).append(";");
		}
		if (this.version != Defines.SVC_SNMPv3) {
			if (!CommonUtil.isEmpty(this.community)) {
				v.append("community=").append(this.community).append(";");
			}
			if (!CommonUtil.isEmpty(this.writeCommunity)) {
				v.append("writeCommunity=").append(this.writeCommunity).append(";");
			}
		} else {
			if (!CommonUtil.isEmpty(this.securityName)) {
				v.append("securityName=").append(this.securityName).append(";");
			}
			if (this.securitylevel < 4) {
				v.append("securitylevel=").append(this.securitylevel).append(";");
			}
			if (this.securitylevel == Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHNOPRIV) {
				if (this.authprotocol < 2) {
					v.append("authprotocol=").append(this.authprotocol).append(";");
				}
				if (!CommonUtil.isEmpty(this.authpassphrase)) {
					v.append("authpassphrase=").append(this.authpassphrase).append(";");
				}
			} else if (this.securitylevel == Defines.ITEM_SNMPV3_SECURITYLEVEL_AUTHPRIV) {
				if (this.authprotocol < 2) {
					v.append("authprotocol=").append(this.authprotocol).append(";");
				}
				if (!CommonUtil.isEmpty(this.authpassphrase)) {
					v.append("authpassphrase=").append(this.authpassphrase).append(";");
				}
				if (this.privprotocol < 2) {
					v.append("privprotocol=").append(this.privprotocol).append(";");
				}
				if (!CommonUtil.isEmpty(this.privpassphrase)) {
					v.append("privpassphrase=").append(this.privpassphrase).append(";");
				}
			}
		}
		v.append("version=").append(this.version);
		return v.toString();
	}

	/**
	 * 从配置字符串中，匹配snmp服务的属性值
	 * 
	 * @param configString
	 *            void
	 */
	public void bornFromConfig(String configString) {
		String[] cfgs = configString.split(";");
		for (String cfg : cfgs) {
			String[] kv = cfg.split("=");
			if (kv[0].equals("community"))
				this.community = kv[1];
			if (kv[0].equals("writeCommunity")) {
				this.writeCommunity = kv[1];
			} else if (kv[0].equals("port")) {
				this.port = Integer.parseInt(kv[1]);
			} else if (kv[0].equals("timeout")) {
				this.timeout = Integer.parseInt(kv[1]);
			} else if (kv[0].equals("retries")) {
				this.retries = Integer.parseInt(kv[1]);
			} else if (kv[0].equals("version")) {
				this.version = Integer.parseInt(kv[1]);
			} else if ("snmpOid".equals(kv[0])) {
				this.snmpOid = kv[1];
			} else if ("contextName".equals(kv[0])) {
				this.contextName = kv[1];
			} else if ("securityName".equals(kv[0])) {
				this.securityName = kv[1];
			} else if ("securitylevel".equals(kv[0])) {
				this.securitylevel = Integer.valueOf(kv[1]);
			} else if ("authprotocol".equals(kv[0])) {
				this.authprotocol = Integer.valueOf(kv[1]);
			} else if ("authpassphrase".equals(kv[0])) {
				this.authpassphrase = kv[1];
			} else if ("privprotocol".equals(kv[0])) {
				this.privprotocol = Integer.valueOf(kv[1]);
			} else if ("privpassphrase".equals(kv[0])) {
				this.privpassphrase = kv[1];
			}
		}
		if (this.port == 0) {
			this.port = 161;
		}
		if (this.timeout == 0) {
			this.timeout = 1000;
		}
		if (this.retries == 0) {
			this.retries = 2;
		}
	}

	/**
	 * 获取SNMP的OID
	 * 
	 * @return
	 */
	public String getSnmpOid() {
		return snmpOid;
	}

	/**
	 * 设置SNMP的OID
	 * 
	 * @param snmpOid
	 */
	public void setSnmpOid(String snmpOid) {
		this.snmpOid = snmpOid;
	}
}
