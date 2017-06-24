package com.isoft.iradar.tags;

import static com.isoft.types.CArray.array;

import com.isoft.types.CArray;

public class FrontendSetup {
	
	public static String MIN_PHP_VERSION = "5.3.0";
	public static int MIN_PHP_MEMORY_LIMIT = 134217728; // 128*1024*1024
	public static int MIN_PHP_POST_MAX_SIZE = 16777216; // 16*1024*1024
	public static int MIN_PHP_UPLOAD_MAX_FILESIZE = 2097152; // 2*1024*1024
	public static int MIN_PHP_MAX_EXECUTION_TIME = 300;
	public static int MIN_PHP_MAX_INPUT_TIME = 300;
	public static String MIN_PHP_GD_VERSION = "2.0";
	public static String MIN_PHP_LIBXML_VERSION = "2.6.15";

	/**
	 * Check OK, setup can continue.
	 */
	public static int  CHECK_OK = 1;

	/**
	 * Check failed, but setup can still continue. A warning will be displayed.
	 */
	public static int  CHECK_WARNING = 2;

	/**
	 * Check failed, setup cannot continue. An error will be displayed.
	 */
	public static int  CHECK_FATAL = 3;

	
	public CArray checkRequirements() {
		//TODO: com.isoft.iradar.tags.FrontendSetup.checkRequirements()
		return array();
	}
	
}
