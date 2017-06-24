package com.isoft.iradar.imports.importers;

import static com.isoft.types.CArray.array;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.iradar.imports.CImportReferencer;
import com.isoft.types.CArray;

public abstract class CImporter {

	/**
	 * @var CImportReferencer
	 */
	protected CImportReferencer referencer;

	/**
	 * @var array
	 */
	protected CArray options = array();

	/**
	 * @param array             options
	 * @param CImportReferencer referencer
	 */
	public CImporter(CArray options, CImportReferencer referencer) {
		this.options = options;
		this.referencer = referencer;
	}

	/**
	 * @abstract
	 *
	 * @param Carray elements
	 *
	 * @return mixed
	 */
	abstract public void doImport(IIdentityBean idBean, CArray<Map> elements) throws Exception;
}
