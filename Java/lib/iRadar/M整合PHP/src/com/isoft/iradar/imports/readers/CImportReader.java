package com.isoft.iradar.imports.readers;

import com.isoft.types.CArray;

public abstract class CImportReader {

	/**
	 * convert string with data in format supported by reader to array.
	 * @abstract
	 * @param str
	 * @return CArray
	 */
	abstract public CArray read(String str);

}
