package com.isoft.biz.vo.platform.topo;

import com.isoft.imon.topo.util.TopoUtil;

public class TPic {
	private String id;
	private String name;
	private String category;
	private String width;
	private String height;
	private String url;
	private String tenantId;
	private String userId;
	private String modifiedAt;
	private String modifiedUser;
	private String createdAt;
	private String createdUser;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getModifiedAt() {
		return modifiedAt;
	}
	public void setModifiedAt(String modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	public String getModifiedUser() {
		return modifiedUser;
	}
	public void setModifiedUser(String modifiedUser) {
		this.modifiedUser = modifiedUser;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getCreatedUser() {
		return createdUser;
	}
	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}
	
	public String getTagName(){
		if(getCategory().equals(TopoUtil.TOPO_PIC_CABINET_CATEGORY)){
			return TopoUtil.TOPO_TAGNAME_CABINET;
		}else if(getCategory().equals(TopoUtil.TOPO_PIC_ROOM_CATEGORY)){
			return TopoUtil.TOPO_TAGNAME_ROOM;
		}
		return "";
	}
	
//	public String getIcon(){
//		return TopoUtil.webRootUrl
//	}

	public String toJson(){
		 return "{" +
	            "id :" + TopoUtil.INIT_NODE_ID + ","+	
				"hostId:" + TopoUtil.INIT_HOST_ID + "," +
				"picId:" + getId() + "," +
				"tagName:'" + getTagName() + "'," +
				"category:'" + getCategory() + "'," +
				"name:'" + getName() + "'," +
				"searchName:'" + getName() + "'," +
				"image:'"+ getUrl() + "'," +
				"width:"+getWidth()+"," +
				"height:"+getHeight()+"," +
			"}";
	}
}
