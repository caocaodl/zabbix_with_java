package com.isoft.imon.topo.engine.discover.poller.host;

import java.text.DecimalFormat;
import java.util.List;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.isoft.imon.topo.engine.discover.bag.host.IfTable;
import com.isoft.imon.topo.engine.discover.bag.host.IfTableEntry;
import com.isoft.imon.topo.engine.discover.discovery.SnmpDiscovery;
import com.isoft.imon.topo.engine.discover.discovery.operator.SnmpOperator;
import com.isoft.imon.topo.engine.discover.element.Host;
import com.isoft.imon.topo.host.util.HostConstants;
import com.isoft.imon.topo.platform.context.ContextFactory;
import com.isoft.imon.topo.util.CommonUtil;
import com.isoft.imon.topo.util.DateUtil;

/**
 * 接口表轮询器
 * 
 * @author Administrator
 * @date 2014年8月7日
 */
public class IfTablePoller extends SnmpPoller {
	private static final DecimalFormat intFormatter = new DecimalFormat("#");

	/*
	 * (non-Javadoc) 采集接口数据
	 * 
	 * @see
	 * com.isoft.engine.discover.Poller#collect(com.isoft.engine.discover.NetElement
	 * , java.lang.Object)
	 */
	public IfTable collect(Host host, SnmpOperator snmp) {
		List<TableEvent> rows = null;
		int version = CommonUtil.getSnmpVersionByHost(host);
		if (ContextFactory.getFactory().isDiscoveryStatus()) {
			rows = snmp.createTable(HostConstants.IF_DISCOVERY_OIDS, version);
		} else {
			rows = snmp.createTable(HostConstants.IF_OIDS, version);
		}
		if (rows == null)
			return null;

		IfTable ifTable = new IfTable();
		ifTable.setElementId(host.getId());
		long loopbackSpeed = 0L;
		for (TableEvent row : rows) {
			VariableBinding[] vb = row.getColumns();
			if ((vb == null) || (vb[0] == null))
				continue;
			IfTableEntry entry = new IfTableEntry();
			entry.setIndex(snmp.getValue(vb[0]));
			entry.setDescr(formatIfDescr(snmp.getValue(vb[1])));
			entry.setType(snmp.getIntValue(vb[2]));
			entry.setSpeed(snmp.getLongValue(vb[3]));
			if (entry.getType() == 24)
				loopbackSpeed = entry.getSpeed();
			entry.setMac(snmp.getValue(vb[4]));
			if ((entry.getMac() != null) && (entry.getMac().length() != 17)) {
				entry.setMac(null);
			}
			entry.setAdminStatus(snmp.getIntValue(vb[5]));
			entry.setOperStatus(snmp.getIntValue(vb[6]));
			if (ContextFactory.getFactory().isDiscoveryStatus()) {
				ifTable.add(entry);
			} else if (row.getColumns().length > HostConstants.IF_DISCOVERY_OIDS.length) {
				entry.setInVolume(snmp.getLongValue(vb[7]));
				entry.setOutVolume(snmp.getLongValue(vb[8]));
				entry.setInError(snmp.getLongValue(vb[9]));
				entry.setOutError(snmp.getLongValue(vb[10]));
				entry.setInDiscard(snmp.getLongValue(vb[11]));
				entry.setOutDiscard(snmp.getLongValue(vb[12]));
				entry.setInUcastPkts(snmp.getLongValue(vb[13]));
				entry.setOutUcastPkts(snmp.getLongValue(vb[14]));
				entry.setInNUcastPkts(snmp.getLongValue(vb[15]));
				entry.setOutNUcastPkts(snmp.getLongValue(vb[16]));
				ifTable.add(entry);
			}
		}
		for (IfTableEntry entry : ifTable.getEntities()) {
			if (entry.getSpeed() == 0L)
				entry.setSpeed(loopbackSpeed);
		}

		List<TableEvent> ipRows = snmp.createTable(HostConstants.IP_OIDS,
				version);
		if (ipRows != null) {
			for (TableEvent row : ipRows) {
				VariableBinding[] vb = row.getColumns();
				if (vb == null)
					continue;
				IfTableEntry ife = ifTable.getIfByIndex(snmp.getValue(vb[1]));
				if (ife != null) {
					ife.setIpAddress(snmp.getValue(vb[0]));
					ife.setMask(snmp.getValue(vb[2]));
				}
			}
		}
		if (ContextFactory.getFactory().isPollingStatus()) {
			IfTable oldTable = (IfTable) host.getBag(IfTableEntry.class);
			if (oldTable != null)
				compute(ifTable, oldTable);
		}
		return ifTable;
	}

	/**
	 * 格式化接口描述
	 * 
	 * @param descr
	 * @return
	 */
	private String formatIfDescr(String descr) {
		if (descr != null) {
			if (descr.indexOf(";") >= 1)
				descr = descr.substring(0, descr.indexOf(";"));
			else if (descr.indexOf("") >= 1)
				descr = descr.substring(0, descr.indexOf(""));
			if (descr.indexOf(":") >= 1)
				descr = SnmpDiscovery.getChinese(descr);
			if ((descr != null) && (descr.indexOf("#") >= 1)) {
				String[] temp = descr.split("#");
				descr = temp[(temp.length - 1)];
			}
			if (descr != null) {
				descr = descr.trim();
			}
		}
		return descr;
	}

	/**
	 * 计算新老接口值
	 * 
	 * @param newTable
	 * @param oldTable
	 */
	private void compute(IfTable newTable, IfTable oldTable) {
		long diffTime = DateUtil.getDiffSeconds(newTable.getLogTime(),
				oldTable.getLogTime());
		if (diffTime == 0L)
			return;

		for (IfTableEntry ife : newTable.getEntities())
			try {
				IfTableEntry ife2 = (IfTableEntry) oldTable
						.get(ife.getEntity());
				if ((ife.getOperStatus() != 1) || (ife2 == null))
					continue;
				long dfInOctets = getDiffValue(ife.getInVolume(),
						ife2.getInVolume());
				long dfOutOctets = getDiffValue(ife.getOutVolume(),
						ife2.getOutVolume());
				long dfInError = getDiffValue(ife.getInError(),
						ife2.getInError());
				long dfOutError = getDiffValue(ife.getOutError(),
						ife2.getOutError());
				long dfInDiscard = getDiffValue(ife.getInDiscard(),
						ife2.getInDiscard());
				long dfOutDiscard = getDiffValue(ife.getOutDiscard(),
						ife2.getOutDiscard());
				long dfInUcastPkts = getDiffValue(ife.getInUcastPkts(),
						ife2.getInUcastPkts());
				long dfOutUcastPkts = getDiffValue(ife.getOutUcastPkts(),
						ife2.getOutUcastPkts());
				long dfInNUcastPkts = getDiffValue(ife.getInNUcastPkts(),
						ife2.getInNUcastPkts());
				long dfOutNUcastPkts = getDiffValue(ife.getOutNUcastPkts(),
						ife2.getOutNUcastPkts());

				ife.setInSpeed(CommonUtil.formatLong(dfInOctets * 8L,
						1000L * diffTime));
				ife.setOutSpeed(CommonUtil.formatLong(dfOutOctets * 8L,
						1000L * diffTime));

				ife.setInPercentage(CommonUtil.formatFloat(
						dfInOctets * 8L * 100L, ife.getSpeed() * diffTime));
				ife.setOutPercentage(CommonUtil.formatFloat(
						dfOutOctets * 8L * 100L, ife.getSpeed() * diffTime));
				if (ife.getInPercentage() > 100)
					ife.setInPercentage(100);
				if (ife.getOutPercentage() > 100) {
					ife.setOutPercentage(100);
				}

				ife.setDiscardRate(CommonUtil.formatLong(dfInDiscard
						+ dfOutDiscard, diffTime));
				ife.setErrorRate(CommonUtil.formatLong(dfInError + dfOutError,
						diffTime));

				ife.setFrameVolume(CommonUtil.formatLong(dfInUcastPkts
						+ dfOutUcastPkts, diffTime));

				ife.setBroadcastVolume(CommonUtil.formatLong(dfInNUcastPkts
						+ dfOutNUcastPkts, diffTime));
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private long getDivLong(long val1, long val2) {
		return val2 == 0L ? 0L : Long.parseLong(intFormatter.format(val1 / val2
				/ 2L));
	}

	/**
	 * 获取差值
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	protected long getDiffValue(long val1, long val2) {
		return val1 - val2 >= 0L ? val1 - val2 : val1 - val2
				+ Long.parseLong("4294967295");
	}
}
