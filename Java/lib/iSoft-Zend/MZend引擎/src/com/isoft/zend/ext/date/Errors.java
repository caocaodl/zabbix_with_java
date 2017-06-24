package com.isoft.zend.ext.date;

public class Errors{
	protected final static int E_ERROR = (1 << 0);
	protected final static int E_WARNING = (1 << 1);
	protected final static int E_PARSE = (1 << 2);
	protected final static int E_NOTICE = (1 << 3);
	protected final static int E_CORE_ERROR = (1 << 4);
	protected final static int E_CORE_WARNING = (1 << 5);
	protected final static int E_COMPILE_ERROR = (1 << 6);
	protected final static int E_COMPILE_WARNING = (1 << 7);
	protected final static int E_USER_ERROR = (1 << 8);
	protected final static int E_USER_WARNING = (1 << 9);
	protected final static int E_USER_NOTICE = (1 << 10);
	protected final static int E_STRICT = (1 << 11);
	protected final static int E_RECOVERABLE_ERROR = (1 << 12);
	protected final static int E_DEPRECATED = (1 << 13);
	protected final static int E_USER_DEPRECATED = (1 << 14);
	
	protected final static int E_ALL = (E_ERROR | E_WARNING | E_PARSE | E_NOTICE | E_CORE_ERROR | E_CORE_WARNING | E_COMPILE_ERROR | E_COMPILE_WARNING | E_USER_ERROR | E_USER_WARNING | E_USER_NOTICE | E_RECOVERABLE_ERROR | E_DEPRECATED | E_USER_DEPRECATED | E_STRICT);
	protected final static int E_CORE = (E_CORE_ERROR | E_CORE_WARNING);
}