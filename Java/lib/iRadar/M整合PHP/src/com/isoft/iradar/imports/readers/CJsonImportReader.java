package com.isoft.iradar.imports.readers;

import static com.isoft.iradar.utils.CJs.decodeJson;

import com.isoft.types.CArray;

public class CJsonImportReader extends CImportReader {

	/**
	 * convert string with data in JSON format to array.
	 * @param String str
	 * @return CArray
	 */
	@Override
	public CArray read(String str) {
		return CArray.valueOf(decodeJson(str));
	}

}
