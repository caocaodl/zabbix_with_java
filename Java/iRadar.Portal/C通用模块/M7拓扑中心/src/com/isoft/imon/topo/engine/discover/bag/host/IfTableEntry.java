package com.isoft.imon.topo.engine.discover.bag.host;

import java.util.List;

import com.isoft.imon.topo.admin.factory.DictionaryFactory;
import com.isoft.imon.topo.engine.discover.bag.SimpleBag;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.platform.context.PollingPool;
import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;
import com.isoft.imon.topo.platform.policy.thresholdOver.ThresholdOverAnnotation;
import com.isoft.imon.topo.util.DateUtil;

/**
 * 设备接口
 * 
 * @author ldd 2014-2-21
 */
@AnalysableAnnotation(label = "设备接口")
public class IfTableEntry extends SimpleBag {
	private static final long serialVersionUID = 20130709152213L;
	// 管理状态，启用
	public static final int ADMIN_STATUS_UP = 1;
	// 管理状态，关闭
	public static final int ADMIN_STATUS_SHUTDOWN = 2;
	// 管理状态，尝试
	public static final int ADMIN_STATUS_TESTING = 3;
	// 操作状态，开机
	public static final int OPER_STATUS_UP = 1;
	// 操作状态，关机
	public static final int OPER_STATUS_DOWN = 2;
	// 操作状态，未知
	public static final int OPER_STATUS_UNKNOWN = 3;
	// 位置
	protected String index;
	// 描述
	protected String descr;
	// 类型
	protected int type;
	// 速度
	protected long speed;
	// MAC地址
	protected String mac;
	// IP地址
	protected String ipAddress;
	// 网络掩码
	protected String mask;
	// 管理状态
	protected int adminStatus;
	// 操作状态 1代表UP，2代表Down
	@AnalysableAnnotation(label = "接口${entity}操作状态", unit = "")
	protected int operStatus;
	// 别名
	protected String alias;
	// 接口下行流速
	@AnalysableAnnotation(label = "接口${entity}下行流速", unit = "KBps")
	protected long inSpeed;
	// 接口上行流速
	@AnalysableAnnotation(label = "接口${entity}上行流速", unit = "KBps")
	protected long outSpeed;
	// 下行流量
	protected long inVolume;
	// 上行流量
	protected long outVolume;
	// 下行丢失数
	protected long inDiscard;
	// 上行丢失数
	protected long outDiscard;
	// 下行错误数
	protected long inError;
	// 上行丢失数
	protected long outError;
	//
	protected long inUcastPkts;
	//
	protected long outUcastPkts;
	//
	protected long inNUcastPkts;
	//
	protected long outNUcastPkts;

	/**
	 * 接口类型
	 * 
	 * @author ldd 2014-2-21
	 */
	public final class InterfaceType {
		public static final int other = 1;
		public static final int regular1822 = 2;
		public static final int hdh1822 = 3;
		public static final int ddnX25 = 4;
		public static final int rfc877x25 = 5;
		public static final int ethernetCsmacd = 6;
		public static final int iso88023Csmacd = 7;
		public static final int iso88024TokenBus = 8;
		public static final int iso88025TokenRing = 9;
		public static final int iso88026Man = 10;
		public static final int starLan = 11;
		public static final int proteon10Mbit = 12;
		public static final int proteon80Mbit = 13;
		public static final int hyperchannel = 14;
		public static final int fddi = 15;
		public static final int lapb = 16;
		public static final int sdlc = 17;
		public static final int ds1 = 18;
		public static final int e1 = 19;
		public static final int basicISDN = 20;
		public static final int primaryISDN = 21;
		public static final int propPointToPointSerial = 22;
		public static final int ppp = 23;
		public static final int softwareLoopback = 24;
		public static final int eon = 25;
		public static final int ethernet3Mbit = 26;
		public static final int nsip = 27;
		public static final int slip = 28;
		public static final int ultra = 29;
		public static final int ds3 = 30;
		public static final int sip = 31;
		public static final int frameRelay = 32;
		public static final int rs232 = 33;
		public static final int para = 34;
		public static final int arcnet = 35;
		public static final int arcnetPlus = 36;
		public static final int atm = 37;
		public static final int miox25 = 38;
		public static final int sonet = 39;
		public static final int x25ple = 40;
		public static final int iso88022llc = 41;
		public static final int localTalk = 42;
		public static final int smdsDxi = 43;
		public static final int frameRelayService = 44;
		public static final int v35 = 45;
		public static final int hssi = 46;
		public static final int hippi = 47;
		public static final int modem = 48;
		public static final int aal5 = 49;
		public static final int sonetPath = 50;
		public static final int sonetVT = 51;
		public static final int smdsIcip = 52;
		public static final int propVirtual = 53;
		public static final int propMultiplexor = 54;
		public static final int ieee80212 = 55;
		public static final int fibreChannel = 56;
		public static final int hippiInterface = 57;
		public static final int frameRelayInterconnect = 58;
		public static final int aflane8023 = 59;
		public static final int aflane8025 = 60;
		public static final int cctEmul = 61;
		public static final int fastEther = 62;
		public static final int isdn = 63;
		public static final int v11 = 64;
		public static final int v36 = 65;
		public static final int g703at64k = 66;
		public static final int g703at2mb = 67;
		public static final int qllc = 68;
		public static final int fastEtherFX = 69;
		public static final int channel = 70;
		public static final int ieee80211 = 71;
		public static final int ibm370parChan = 72;
		public static final int escon = 73;
		public static final int dlsw = 74;
		public static final int isdns = 75;
		public static final int isdnu = 76;
		public static final int lapd = 77;
		public static final int ipSwitch = 78;
		public static final int rsrb = 79;
		public static final int atmLogical = 80;
		public static final int ds0 = 81;
		public static final int ds0Bundle = 82;
		public static final int bsc = 83;
		public static final int async = 84;
		public static final int cnr = 85;
		public static final int iso88025Dtr = 86;
		public static final int eplrs = 87;
		public static final int arap = 88;
		public static final int propCnls = 89;
		public static final int hostPad = 90;
		public static final int termPad = 91;
		public static final int frameRelayMPI = 92;
		public static final int x213 = 93;
		public static final int adsl = 94;
		public static final int radsl = 95;
		public static final int sdsl = 96;
		public static final int vdsl = 97;
		public static final int iso88025CRFPInt = 98;
		public static final int myrinet = 99;
		public static final int voiceEM = 100;
		public static final int voiceFXO = 101;
		public static final int voiceFXS = 102;
		public static final int voiceEncap = 103;
		public static final int voiceOverIp = 104;
		public static final int atmDxi = 105;
		public static final int atmFuni = 106;
		public static final int atmIma = 107;
		public static final int pppMultilinkBundle = 108;
		public static final int ipOverCdlc = 109;
		public static final int ipOverClaw = 110;
		public static final int stackToStack = 111;
		public static final int virtualIpAddress = 112;
		public static final int mpc = 113;
		public static final int ipOverAtm = 114;
		public static final int iso88025Fiber = 115;
		public static final int tdlc = 116;
		public static final int gigabitEthernet = 117;
		public static final int hdlc = 118;
		public static final int lapf = 119;
		public static final int v37 = 120;
		public static final int x25mlp = 121;
		public static final int x25huntGroup = 122;
		public static final int transpHdlc = 123;
		public static final int interleave = 124;
		public static final int fast = 125;
		public static final int ip = 126;
		public static final int docsCableMaclayer = 127;
		public static final int docsCableDownstream = 128;
		public static final int docsCableUpstream = 129;
		public static final int a12MppSwitch = 130;
		public static final int tunnel = 131;
		public static final int coffee = 132;
		public static final int ces = 133;
		public static final int atmSubInterface = 134;
		public static final int l2vlan = 135;
		public static final int l3ipvlan = 136;
		public static final int l3ipxvlan = 137;
		public static final int digitalPowerline = 138;
		public static final int mediaMailOverIp = 139;
		public static final int dtm = 140;
		public static final int dcn = 141;
		public static final int ipForward = 142;
		public static final int msdsl = 143;
		public static final int ieee1394 = 144;
		public static final int if_gsn = 145;
		public static final int dvbRccMacLayer = 146;
		public static final int dvbRccDownstream = 147;
		public static final int dvbRccUpstream = 148;
		public static final int atmVirtual = 149;
		public static final int mplsTunnel = 150;
		public static final int srp = 151;
		public static final int voiceOverAtm = 152;
		public static final int voiceOverFrameRelay = 153;
		public static final int idsl = 154;
		public static final int compositeLink = 155;
		public static final int ss7SigLink = 156;
		public static final int propWirelessP2P = 157;
		public static final int frForward = 158;
		public static final int rfc1483 = 159;
		public static final int usb = 160;
		public static final int ieee8023adLag = 161;
		public static final int bgppolicyaccounting = 162;
		public static final int frf16MfrBundle = 163;
		public static final int h323Gatekeeper = 164;
		public static final int h323Proxy = 165;
		public static final int mpls = 166;
		public static final int mfSigLink = 167;
		public static final int hdsl2 = 168;
		public static final int shdsl = 169;
		public static final int ds1FDL = 170;
		public static final int pos = 171;
		public static final int dvbAsiIn = 172;
		public static final int dvbAsiOut = 173;
		public static final int plc = 174;
		public static final int nfas = 175;
		public static final int tr008 = 176;
		public static final int gr303RDT = 177;
		public static final int gr303IDT = 178;
		public static final int isup = 179;
		public static final int propDocsWirelessMaclayer = 180;
		public static final int propDocsWirelessDownstream = 181;
		public static final int propDocsWirelessUpstream = 182;
		public static final int hiperlan2 = 183;
		public static final int propBWAp2Mp = 184;
		public static final int sonetOverheadChannel = 185;
		public static final int digitalWrapperOverheadChannel = 186;
		public static final int aal2 = 187;
		public static final int radioMAC = 188;
		public static final int atmRadio = 189;
		public static final int imt = 190;
		public static final int mvl = 191;
		public static final int reachDSL = 192;
		public static final int frDlciEndPt = 193;
		public static final int atmVciEndPt = 194;
		public static final int opticalChannel = 195;
		public static final int opticalTransport = 196;
		public static final int propAtm = 197;
		public static final int voiceOverCable = 198;
		public static final int infiniband = 199;
		public static final int teLink = 200;
		public static final int q2931 = 201;
		public static final int virtualTg = 202;
		public static final int sipTg = 203;
		public static final int sipSig = 204;
		public static final int docsCableUpstreamChannel = 205;
		public static final int econet = 206;
		public static final int pon155 = 207;
		public static final int pon622 = 208;
		public static final int bridge = 209;
		public static final int linegroup = 210;
		public static final int voiceEMFGD = 211;
		public static final int voiceFGDEANA = 212;
		public static final int voiceDID = 213;
		public static final int mpegTransport = 214;
		public static final int sixToFour = 215;
		public static final int gtp = 216;
		public static final int pdnEtherLoop1 = 217;
		public static final int pdnEtherLoop2 = 218;
		public static final int opticalChannelGroup = 219;
		public static final int homepna = 220;
		public static final int gfp = 221;
		public static final int ciscoISLvlan = 222;
		public static final int actelisMetaLOOP = 223;
		public static final int fcipLink = 224;
		public static final int rpr = 225;
		public static final int qam = 226;
		public static final int lmp = 227;
		public static final int cblVectaStar = 228;
		public static final int docsCableMCmtsDownstream = 229;
		public static final int adsl2 = 230;
		public static final int macSecControlledIF = 231;
		public static final int macSecUncontrolledIF = 232;
		public static final int aviciOpticalEther = 233;
		public static final int atmbond = 234;
		public static final int voiceFGDOS = 235;
		public static final int mocaVersion1 = 236;
		public static final int ieee80216WMAN = 237;
		public static final int adsl2plus = 238;
		public static final int dvbRcsMacLayer = 239;
		public static final int dvbTdm = 240;
		public static final int dvbRcsTdma = 241;
		public static final int x86Laps = 242;
		public static final int wwanPP = 243;
		public static final int wwanPP2 = 244;
		public static final int voiceEBS = 245;
		public static final int ifPwType = 246;
		public static final int ilan = 247;
		public static final int pip = 248;
		public static final int aluELP = 249;
		public static final int gpon = 250;
		public static final int vdsl2 = 251;
		public static final int capwapDot11Profile = 252;
		public static final int capwapDot11Bss = 253;
		public static final int capwapWtpVirtualRadio = 254;
		public static final int bits = 255;
		public static final int docsCableUpstreamRfPort = 256;
		public static final int cableDownstreamRfPort = 257;
		public static final int vmwareVirtualNic = 258;
		public static final int ieee802154 = 259;
		public static final int otnOdu = 260;
		public static final int otnOtu = 261;
		public static final int ifVfiType = 262;
		public static final int g9981 = 263;
		public static final int g9982 = 264;
		public static final int g9983 = 265;
		public static final int aluEpon = 266;
		public static final int aluEponOnu = 267;
		public static final int aluEponPhysicalUni = 268;
		public static final int aluEponLogicalLink = 269;
		public static final int aluGponOnu = 270;
		public static final int aluGponPhysicalUni = 271;
		public static final int vmwareNicTeam = 272;
	}

	// 下行带宽利用率
	@AnalysableAnnotation(label = "接口${entity}下行带宽利用率", unit = "%")
	@ThresholdOverAnnotation(enabled = false)
	protected float inPercentage;
	// 上行宽带利用率
	@AnalysableAnnotation(label = "接口${entity}上行带宽利用率", unit = "%")
	@ThresholdOverAnnotation(enabled = false)
	protected float outPercentage;
	// 丢包率
	protected long discardRate;
	// 错误率
	protected long errorRate;
	//
	protected long frameVolume;
	//
	protected long broadcastVolume;

	/**
	 * 获取索引
	 * 
	 * @return String
	 */
	public String getIndex() {
		return this.index;
	}

	/**
	 * 设置索引
	 * 
	 * @param index
	 *            void
	 */
	public void setIndex(String index) {
		this.index = index;
	}

	/**
	 * 获取描述
	 * 
	 * @return String
	 */
	public String getDescr() {
		return this.descr;
	}

	/**
	 * 设置描述
	 * 
	 * @param descr
	 *            void
	 */
	public void setDescr(String descr) {
		this.descr = descr;
		this.alias = descr;
	}

	/**
	 * 获取管理状态
	 * 
	 * @return int
	 */
	public int getAdminStatus() {
		return this.adminStatus;
	}

	/**
	 * 设置管理状态
	 * 
	 * @param adminStatus
	 *            void
	 */
	public void setAdminStatus(int adminStatus) {
		this.adminStatus = adminStatus;
	}

	/**
	 * 获取操作状态
	 * 
	 * @return int
	 */
	public int getOperStatus() {
		return this.operStatus;
	}

	/**
	 * 设置操作状态
	 * 
	 * @param operStatus
	 *            void
	 */
	public void setOperStatus(int operStatus) {
		this.operStatus = operStatus;
	}

	/**
	 * 获取下行流量
	 * 
	 * @return long
	 */
	public long getInVolume() {
		return this.inVolume;
	}

	/**
	 * 获取下行速度
	 * 
	 * @return long
	 */
	public long getInSpeed() {
		return this.inSpeed;
	}

	/**
	 * 设置下行速度
	 * 
	 * @param inSpeed
	 *            void
	 */
	public void setInSpeed(long inSpeed) {
		this.inSpeed = inSpeed;
	}

	/**
	 * 获取上行流量
	 * 
	 * @return long
	 */
	public long getOutVolume() {
		return this.outVolume;
	}

	/**
	 * 获取上行速度
	 * 
	 * @return long
	 */
	public long getOutSpeed() {
		return this.outSpeed;
	}

	/**
	 * 设置上行速度
	 * 
	 * @param outSpeed
	 *            void
	 */
	public void setOutSpeed(long outSpeed) {
		this.outSpeed = outSpeed;
	}

	/**
	 * 获取丢包率
	 * 
	 * @return long
	 */
	public long getDiscardRate() {
		return this.discardRate;
	}

	/**
	 * 设置丢包率
	 * 
	 * @param discardRate
	 *            void
	 */
	public void setDiscardRate(long discardRate) {
		this.discardRate = discardRate;
	}

	/**
	 * 获取错误率
	 * 
	 * @return long
	 */
	public long getErrorRate() {
		return this.errorRate;
	}

	/**
	 * 设置错误率
	 * 
	 * @param errorRate
	 *            void
	 */
	public void setErrorRate(long errorRate) {
		this.errorRate = errorRate;
	}

	/**
	 * 获取类型
	 * 
	 * @return int
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * 设置类型
	 * 
	 * @param type
	 *            void
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * 获取速度
	 * 
	 * @return long
	 */
	public long getSpeed() {
		return this.speed;
	}

	/**
	 * 设置速度
	 * 
	 * @param speed
	 *            void
	 */
	public void setSpeed(long speed) {
		this.speed = speed;
	}

	/**
	 * 获取Mac
	 * 
	 * @return String
	 */
	public String getMac() {
		return this.mac;
	}

	/**
	 * 设置Mac
	 * 
	 * @param mac
	 *            void
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}

	/**
	 * 获取IP地址
	 * 
	 * @return String
	 */
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * 
	 * @return long
	 */
	public long getFrameVolume() {
		return this.frameVolume;
	}

	/**
	 * 
	 * @param frameVolume
	 *            void
	 */
	public void setFrameVolume(long frameVolume) {
		this.frameVolume = frameVolume;
	}

	/**
	 * 
	 * @return long
	 */
	public long getBroadcastVolume() {
		return this.broadcastVolume;
	}

	/**
	 * 
	 * @param broadcastVolume
	 *            void
	 */
	public void setBroadcastVolume(long broadcastVolume) {
		this.broadcastVolume = broadcastVolume;
	}

	/**
	 * 设置IP地址
	 * 
	 * @param ipAddress
	 *            void
	 */
	public void setIpAddress(String ipAddress) {
		if (this.ipAddress == null) {
			this.ipAddress = ipAddress;
		} else {
			this.ipAddress = (this.ipAddress + "," + ipAddress);
		}
	}

	/**
	 * 获取第一个IP
	 * 
	 * @return String
	 */
	public String getFirstIpAddress() {
		if ((this.ipAddress != null) && (this.ipAddress.indexOf(",") >= 0))
			return this.ipAddress.split(",")[0];
		return this.ipAddress;
	}

	/**
	 * 是否是我的IP地址
	 * 
	 * @param ipAddress
	 * @return boolean
	 */
	public boolean isMyIpAddress(String ipAddress) {
		if ((this.ipAddress == null) || (ipAddress == null)) {
			return false;
		}
		if (ipAddress.equals(this.ipAddress)) {
			return true;
		}
		String[] ips = this.ipAddress.split(",");
		for (String ip : ips) {
			if (ip.equals(ipAddress)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取名称
	 * 
	 * @return String
	 */
	public String getAlias() {
		return this.alias;
	}

	/**
	 * 设置名称
	 * 
	 * @param alias
	 *            void
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * 获取下行丢失shu
	 * 
	 * @return long
	 */
	public long getInDiscard() {
		return this.inDiscard;
	}

	/**
	 * 设置下行丢失数
	 * 
	 * @param inDiscard
	 *            void
	 */
	public void setInDiscard(long inDiscard) {
		this.inDiscard = inDiscard;
	}

	/**
	 * 获取上行丢失数
	 * 
	 * @return long
	 */
	public long getOutDiscard() {
		return this.outDiscard;
	}

	/**
	 * 设置上行丢失数
	 * 
	 * @param outDiscard
	 *            void
	 */
	public void setOutDiscard(long outDiscard) {
		this.outDiscard = outDiscard;
	}

	/**
	 * 获取下行错误率
	 * 
	 * @return long
	 */
	public long getInError() {
		return this.inError;
	}

	/**
	 * 设置下行错误数
	 * 
	 * @param inError
	 *            void
	 */
	public void setInError(long inError) {
		this.inError = inError;
	}

	/**
	 * 获取上行错误数
	 * 
	 * @return long
	 */
	public long getOutError() {
		return this.outError;
	}

	/**
	 * 设置上行错误数
	 * 
	 * @param outError
	 *            void
	 */
	public void setOutError(long outError) {
		this.outError = outError;
	}

	/**
	 * 设置下行流量
	 * 
	 * @param inVolume
	 *            void
	 */
	public void setInVolume(long inVolume) {
		this.inVolume = inVolume;
	}

	/**
	 * 设置上行流量
	 * 
	 * @param outVolume
	 *            void
	 */
	public void setOutVolume(long outVolume) {
		this.outVolume = outVolume;
	}

	/**
	 * 获取下行带宽利用率
	 * 
	 * @return float
	 */
	public float getInPercentage() {
		return this.inPercentage;
	}

	/**
	 * 设置下行带宽利用率
	 * 
	 * @param inPercentage
	 *            void
	 */
	public void setInPercentage(float inPercentage) {
		this.inPercentage = inPercentage;
	}

	/**
	 * 获取上行带宽利用率
	 * 
	 * @return float
	 */
	public float getOutPercentage() {
		return this.outPercentage;
	}

	/**
	 * 设置上行带宽利用率
	 * 
	 * @param outPercentage
	 *            void
	 */
	public void setOutPercentage(float outPercentage) {
		this.outPercentage = outPercentage;
	}

	/**
	 * 
	 * @return long
	 */
	public long getInUcastPkts() {
		return this.inUcastPkts;
	}

	/**
	 * 
	 * @param inUcastPkts
	 *            void
	 */
	public void setInUcastPkts(long inUcastPkts) {
		this.inUcastPkts = inUcastPkts;
	}

	/**
	 * 
	 * @return long
	 */
	public long getOutUcastPkts() {
		return this.outUcastPkts;
	}

	/**
	 * 
	 * @param outUcastPkts
	 *            void
	 */
	public void setOutUcastPkts(long outUcastPkts) {
		this.outUcastPkts = outUcastPkts;
	}

	/**
	 * 
	 * @return long
	 */
	public long getInNUcastPkts() {
		return this.inNUcastPkts;
	}

	/**
	 * 
	 * @param inNUcastPkts
	 *            void
	 */
	public void setInNUcastPkts(long inNUcastPkts) {
		this.inNUcastPkts = inNUcastPkts;
	}

	/**
	 * 
	 * @return long
	 */
	public long getOutNUcastPkts() {
		return this.outNUcastPkts;
	}

	/**
	 * 
	 * @param outNUcastPkts
	 *            void
	 */
	public void setOutNUcastPkts(long outNUcastPkts) {
		this.outNUcastPkts = outNUcastPkts;
	}

	/**
	 * 获取实体描述
	 * 
	 * @return String
	 */
	public String getEntityDescr() {
		return this.descr;
	}

	/**
	 * 
	 * @return String
	 */
	public String getMask() {
		return this.mask;
	}

	/**
	 * 
	 * @param mask
	 *            void
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}

	/**
	 * 获取类型名称
	 * 
	 * @return String
	 */
	public String getTypeName() {
		String tn = DictionaryFactory.getFactory().getEntryValue("ifType",
				this.type);
		if (tn == null) {
			// 以太网
			tn = "ethernet";
		}
		return tn;
	}

	/**
	 * 获取操作状态名称
	 * 
	 * @return String
	 */
	public String getOperStatusName() {
		String status = DictionaryFactory.getFactory().getEntryValue(
				"operStatus", this.operStatus);
		if (status == null)
			return "unknown";
		return status;
	}

	/**
	 * 获取管理状态名称
	 * 
	 * @return String
	 */
	public String getAdminStatusName() {
		String status = DictionaryFactory.getFactory().getEntryValue(
				"adminStatus", this.adminStatus);
		if (status == null)
			return "unknown";
		return status;
	}

	/*
	 * (non-Javadoc) 获取实体信息
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		String e = this.getEntityDescr();
		return e == null ? this.index : e;
	}

	/**
	 * 是否是虚拟的
	 * 
	 * @return boolean
	 */
	public boolean isVirtual() {
		boolean b1 = (this.type == InterfaceType.propVirtual)
				|| (this.type == InterfaceType.l2vlan)
				|| (this.type == InterfaceType.l3ipvlan)
				|| (this.type == InterfaceType.l3ipxvlan);
		boolean b2 = true;
		if (this.descr != null)
			b2 = this.descr.toLowerCase().indexOf("vlan") != -1;
		return (b1) && (b2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int,
	 * java.lang.String, java.util.List)
	 */
	public void persist(int elementId, String logTime, List<String> sqls) {
		Host host = PollingPool.getPool().getHostByID(elementId);
		if (host == null) {
			return;
		}
		if (this.operStatus == 1) {
			StringBuffer sqlText = new StringBuffer(50);
			sqlText.append("INSERT INTO BAG_IFE_PERFORMANCE(ELEMENT_ID,IFE,IN_PERCENTAGE,OUT_PERCENTAGE,");
			sqlText.append("IN_SPEED,OUT_SPEED,FRAME_VOLUME,BROADCAST_VOLUME,DISCARD_RATE,ERROR_RATE,LOG_TIME)VALUES(");
			sqlText.append(elementId).append(",'").append(this.index)
					.append("',");
			sqlText.append(this.inPercentage).append(",")
					.append(this.outPercentage);
			sqlText.append(",").append(this.inSpeed).append(",")
					.append(this.outSpeed);
			sqlText.append(",").append(this.frameVolume).append(",")
					.append(this.broadcastVolume);
			sqlText.append(",").append(this.discardRate).append(",")
					.append(this.errorRate);
			sqlText.append(",'").append(logTime).append("')");
			sqls.add(sqlText.toString());
		}
		IfTableEntry ife = null;
		IfTable ifTable = (IfTable) host.getBag(IfTableEntry.class);
		if (ifTable != null) {
			ife = (IfTableEntry) ifTable.get(this.index);
		}
		if ((ife == null) || (ife.getOperStatus() != this.operStatus)) {
			StringBuffer sqlText2 = new StringBuffer(50);
			sqlText2.append("INSERT INTO BAG_IFE_STATUS(ELEMENT_ID,IFE,STATE,LOG_TIME)VALUES(");
			sqlText2.append(elementId).append(",'").append(this.index)
					.append("',").append(this.operStatus);
			sqlText2.append(",'").append(DateUtil.getCurrentDateTime())
					.append("')");
			sqls.add(sqlText2.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		IfTableEntry ife = (IfTableEntry) obj;
		if (this.index.equals(ife.index)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (this.index != null) {
			return Integer.parseInt(this.index);
		}
		return super.hashCode();
	}

}
