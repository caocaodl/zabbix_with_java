package com.isoft.iradar.imports.readers;

import static com.isoft.iradar.Cphp._s;

public class CImportReaderFactory {

	private CImportReaderFactory() {
	}

	public static final String XML = "xml";
	public static final String JSON = "json";

	/**
	 * Get reader class for required format.
	 * @static
	 * @throws Exception
	 * @param string format
	 * @return CImportReader
	 */
	public static CImportReader getReader(String format) throws Exception {
		if (XML.equals(format)) {
			return new CXmlImportReader();
		}

		if (JSON.equals(format)) {
			return new CJsonImportReader();
		}

		throw new Exception(_s("Unsupported import format \"%1$s\".", format));
	}

	/**
	 * Converts file extension to associated import format.
	 * @static
	 * @throws Exception
	 * @param String ext
	 * @return string
	 */
	public static String fileExt2ImportFormat(String ext) throws Exception {
		if (XML.equals(ext)) {
			return XML;
		}

		if (JSON.equals(ext)) {
			return JSON;
		}

		throw new Exception(_s("Unsupported import file extension \"%1$s\".", ext));
	}

}
