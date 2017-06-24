package com.isoft.iradar.model.params;

public class CImageGet extends CParamGet {
	
	private static final long serialVersionUID = 1L;
	
	private Long[] imageIds;
	private Long[] sysmapIds;
	private Object selectImage;

	public Long[] getImageIds() {
		return imageIds;
	}

	public void setImageIds(Long... imageIds) {
		this.imageIds = imageIds;
	}

	public Long[] getSysmapIds() {
		return sysmapIds;
	}

	public void setSysmapIds(Long... sysmapIds) {
		this.sysmapIds = sysmapIds;
	}

	public Object getSelectImage() {
		return selectImage;
	}

	public void setSelectImage(Object selectImage) {
		this.selectImage = selectImage;
	}

}
