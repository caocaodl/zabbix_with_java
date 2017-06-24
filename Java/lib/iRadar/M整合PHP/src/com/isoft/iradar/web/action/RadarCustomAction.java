package com.isoft.iradar.web.action;

import com.isoft.Feature;
import com.isoft.web.common.BasePageAction;

public class RadarCustomAction extends BasePageAction {
	protected boolean isIgnoreFooter() {
		return Feature.ignorePageFooter;
	}

	protected boolean isIgnoreHeader() {
		return Feature.ignorePageHeader;
	}
}
