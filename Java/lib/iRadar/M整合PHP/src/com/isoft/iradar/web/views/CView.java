package com.isoft.iradar.web.views;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.isset;
import static org.apache.commons.lang.StringUtils.capitaliseAllWords;

import java.lang.reflect.Method;
import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;

public class CView {

	private final static String classPkg = CView.class.getPackage().getName()
			+ ".segments.C";

	private String segment;
	private Object template;
	private Map data;

	protected CView() {
	}

	/**
	 * Creates a new view based on provided template file.
	 * @param string view name of a view, located under include/views
	 * @param array data deprecated parameter, use set() and get() methods for passing variables to views
	 * @example scriptForm = new CView('administration.script.edit');
	 */
	public CView(String view, Map data) {
		this.assign(view);
		this.data = data;
	}

	public void assign(String view) {
		this.segment = view;
	}

	public Object render(IIdentityBean idBean, SQLExecutor executor) {
		@SuppressWarnings("deprecation")
		String clazz = classPkg
				+ capitaliseAllWords(this.segment.replace('.', ' ')).replace(
						" ", "");
		try {
			CViewSegment vs = (CViewSegment) Class.forName(clazz).newInstance();
			this.template = vs.doWidget(idBean, executor, this.data);
			return this.template;
		} catch (Exception e) {
			e.printStackTrace();
			return new RuntimeException(e);
		}
	}

	public void show() {
		if (!isset(this.template)) {
			throw new RuntimeException(_("View is not rendered."));
		}
		try {
			Method method = this.template.getClass().getMethod("show");
			method.invoke(this.template);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
