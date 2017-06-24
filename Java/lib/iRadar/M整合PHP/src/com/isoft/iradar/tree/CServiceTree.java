package com.isoft.iradar.tree;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.in_array;
import static com.isoft.iradar.Cphp.is_numeric;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_INFORMATION;
import static com.isoft.iradar.inc.Defines.TRIGGER_SEVERITY_NOT_CLASSIFIED;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityCaption;
import static com.isoft.iradar.inc.TriggersUtil.getSeverityStyle;
import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CSpan;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CServiceTree extends CTree {

	public CServiceTree(String treename, CArray<Map> value, CArray fields) throws InstantiationException {
		super(treename, value, fields);
	}

	public CServiceTree(String treename, CArray<Map> value) throws InstantiationException {
		super(treename, value);
	}

	public CServiceTree(String treename) throws InstantiationException {
		super(treename);
	}

	/**
	 * Returns a column object for the given row and field. Add additional service tree related formatting.
	 *
	 * @param rowId
	 * @param colName
	 *
	 * @return CCol
	 */
	@Override
	protected CCol makeCol(IIdentityBean idBean, SQLExecutor executor, Object rowId, String colName) {
		String styleclass = null;
		if ("status".equals(colName) && is_numeric(Nest.value(this.tree,rowId,colName).$()) && Nest.value(this.tree,rowId,"id").asInteger() > 0) {
			int status = Nest.value(this.tree,rowId,colName).asInteger();

			// do not show the severity for information and unclassified triggers
			if (in_array(status, array(TRIGGER_SEVERITY_INFORMATION, TRIGGER_SEVERITY_NOT_CLASSIFIED))) {
				Nest.value(this.tree,rowId,colName).$(new CSpan(_("OK"), "green"));
			} else {
				Nest.value(this.tree,rowId,colName).$(getSeverityCaption(idBean, executor,status));
				styleclass = getSeverityStyle(status);
			}
		}
		
		CCol col = super.makeCol(idBean, executor, rowId, colName);
		col.addClass(styleclass);

		return col;
	}
}
