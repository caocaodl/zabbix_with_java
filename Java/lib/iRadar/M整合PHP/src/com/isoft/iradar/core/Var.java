package com.isoft.iradar.core;

import static com.isoft.iradar.Cphp.isPrimitive;
import static com.isoft.iradar.RadarContext.request;
import static com.isoft.iradar.RadarContext.session;
import static com.isoft.iradar.core.Scope.GLOBAL;
import static com.isoft.iradar.core.Scope.REQUEST;
import static com.isoft.iradar.core.Scope.SESSION;
import static com.isoft.lang.Clone.deepcopy;

import java.util.HashMap;
import java.util.Map;

public class Var<T> {

	private static Long ID = 0L;
	private final static Map DEFAULTS = new HashMap();
	private final static Map GLOBALVALUES = new HashMap();

	private String id;
	private Scope scope;

	protected Var(Scope scope) {
		this(scope, null);
	}
	
	protected Var(Scope scope, T defaultValue) {
		this.scope = scope;
		synchronized (ID) {
			this.id = Var.class.getName() + "_" + (++ID);
			DEFAULTS.put(this.id, defaultValue);
		}
	}

	public T $() {
		T v = null;
		if (REQUEST.equals(this.scope)) {
			v = (T) request().getAttribute(this.id);
		} else if (SESSION.equals(this.scope)) {
			v = (T) session().getAttribute(this.id);
		} else if (GLOBAL.equals(this.scope)) {
			v = (T) GLOBALVALUES.get(this.id);
		}
		if (v == null) {
			Object o = DEFAULTS.get(this.id);
			if (o == null) {
				v = null;
			} else if (isPrimitive(o)) {
				v = (T) o;
			} else {
				try {
					v = deepcopy((T) DEFAULTS.get(this.id).getClass().newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (REQUEST.equals(this.scope)) {
				request().setAttribute(this.id, v);
			} else if (SESSION.equals(this.scope)) {
				session().setAttribute(this.id, v);
			} else if (GLOBAL.equals(this.scope)) {
				GLOBALVALUES.put(this.id, v);
			}
		}
		return v;
	}

	public void $(T v) {
		if (REQUEST.equals(this.scope)) {
			request().setAttribute(this.id, v);
		} else if (SESSION.equals(this.scope)) {
			session().setAttribute(this.id, v);
		} else if (GLOBAL.equals(this.scope)) {
			GLOBALVALUES.put(this.id, v);
		}
	}
}
