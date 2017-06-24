package com.isoft.biz.dao;

import java.io.Serializable;

import com.isoft.biz.daoimpl.DBIDGeneratorException;

public interface IDGenerator extends Serializable {
	String next() throws DBIDGeneratorException;
}
