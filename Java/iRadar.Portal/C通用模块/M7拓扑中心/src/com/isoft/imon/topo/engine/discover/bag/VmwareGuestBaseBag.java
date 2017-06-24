package com.isoft.imon.topo.engine.discover.bag;

import java.util.List;

import com.isoft.imon.topo.platform.policy.AnalysableAnnotation;

/**
 * 云主机客户机基础包
 * 
 * @author Administrator
 * 
 * @date 2014年8月6日
 */
@AnalysableAnnotation(label = "VmwareGuestBaseBag")
public final class VmwareGuestBaseBag extends SimpleBag {

	private static final long serialVersionUID = 1L;
	private String name;
	private String uuid;
	private String ipAddress;
	private String fullName;
	private String pathName;
	private int cpuCapacity;
	private int cpuNum;
	private int memorySize;
	private int diskCapacity;
	private int diskNum;
	private boolean existed;

	/**
	 * 获取名称
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * 设置名称
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取UUID
	 * 
	 * @return
	 */
	public String getUuid() {
		return this.uuid;
	}

	/**
	 * 设置UUID
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * 获取IP地址
	 * 
	 * @return
	 */
	public String getIpAddress() {
		return this.ipAddress;
	}

	/**
	 * 设置IP地址
	 * 
	 * @param ipAddress
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * 获取全名
	 * 
	 * @return
	 */
	public String getFullName() {
		return this.fullName;
	}

	/**
	 * 设置全名
	 * 
	 * @param fullName
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * 获取路径名
	 * 
	 * @return
	 */
	public String getPathName() {
		return this.pathName;
	}

	/**
	 * 设置路径名
	 * 
	 * @param pathName
	 */
	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	/**
	 * 获取CPU容量
	 * 
	 * @return
	 */
	public int getCpuCapacity() {
		return this.cpuCapacity;
	}

	/**
	 * 设置CPU容量
	 * 
	 * @param cpuCapacity
	 */
	public void setCpuCapacity(int cpuCapacity) {
		this.cpuCapacity = cpuCapacity;
	}

	/**
	 * 获取CPU数目
	 * 
	 * @return
	 */
	public int getCpuNum() {
		return this.cpuNum;
	}

	/**
	 * 设置CPU数目
	 * 
	 * @param cpuNum
	 */
	public void setCpuNum(int cpuNum) {
		this.cpuNum = cpuNum;
	}

	/**
	 * 获取内存大小
	 * 
	 * @return
	 */
	public int getMemorySize() {
		return this.memorySize;
	}

	/**
	 * 设置内存大小
	 * 
	 * @param memorySize
	 */
	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	/**
	 * 获取硬盘容量
	 * 
	 * @return
	 */
	public int getDiskCapacity() {
		return this.diskCapacity;
	}

	/**
	 * 设置硬盘容量
	 * 
	 * @param diskCapacity
	 */
	public void setDiskCapacity(int diskCapacity) {
		this.diskCapacity = diskCapacity;
	}

	/**
	 * 获取硬盘数目
	 * 
	 * @return
	 */
	public int getDiskNum() {
		return this.diskNum;
	}

	/**
	 * 设置硬盘数目
	 * 
	 * @param diskNum
	 */
	public void setDiskNum(int diskNum) {
		this.diskNum = diskNum;
	}

	/*
	 * (non-Javadoc) 获取实体UUID
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#getEntity()
	 */
	public String getEntity() {
		return this.uuid;
	}

	/*
	 * (non-Javadoc) 持久化SQL语句
	 * 
	 * @see com.isoft.engine.discover.bag.SimpleBag#persist(int,
	 * java.lang.String, java.util.List)
	 */
	protected void persist(int elementId, String logTime, List<String> sqls) {
		StringBuilder sqlText = new StringBuilder();
		if (this.existed) {
			sqlText.append("UPDATE ELEMENT_VMWARE_GUEST SET NAME='")
					.append(this.name).append("',IP_ADDRESS='");
			sqlText.append(this.ipAddress).append("',CPU_NUM=")
					.append(this.cpuNum).append(",CPU_CAPACITY=");
			sqlText.append(this.cpuCapacity).append(",MEMORY_SIZE=")
					.append(this.memorySize).append(",DISK_CAPACITY=");
			sqlText.append(this.diskCapacity).append(",DISK_NUM=")
					.append(this.diskNum).append(",LOG_TIME='");
			sqlText.append(logTime).append("' WHERE UUID='").append(this.uuid)
					.append("'");
		} else {
			sqlText.append("INSERT INTO ELEMENT_VMWARE_GUEST(ELEMENT_ID,UUID,NAME,IP_ADDRESS,VM_PATH,FULL_NAME,CPU_NUM,");
			sqlText.append(
					"CPU_CAPACITY,MEMORY_SIZE,DISK_NUM,DISK_CAPACITY,LOG_TIME)VALUES(")
					.append(elementId).append(",'");
			sqlText.append(this.uuid).append("','").append(this.name)
					.append("','").append(this.ipAddress).append("','")
					.append(this.pathName);
			sqlText.append("','").append(this.fullName).append("',")
					.append(this.cpuNum).append(",").append(this.cpuCapacity)
					.append(",").append(this.memorySize);
			sqlText.append(",").append(this.diskNum).append(",")
					.append(this.diskCapacity).append(",'").append(logTime)
					.append("')");
		}
		System.out.println(sqlText.toString());
		sqls.add(sqlText.toString());
	}
}
