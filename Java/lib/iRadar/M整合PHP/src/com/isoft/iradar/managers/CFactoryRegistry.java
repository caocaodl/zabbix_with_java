package com.isoft.iradar.managers;

import static com.isoft.types.CArray.array;

import java.lang.reflect.Constructor;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.core.g;
import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;

@CodeConfirmed("blue.2.2.5")
public class CFactoryRegistry {
	
	/**
	 * An array of created object instances.
	 *
	 * @var
	 */
	@CodeConfirmed("blue.2.2.5")
	protected CArray<Object> cacheObjects = array();
	
	public CFactoryRegistry(){
	}

	/**
	 * Returns an instance of the factory object.
	 *
	 * @param string clazz
	 *
	 * @return CFactoryRegistry
	 */
	@CodeConfirmed("blue.2.2.5")
	public static CFactoryRegistry getInstance(Class<? extends CFactoryRegistry> clazz) {
		CFactoryRegistry instance = g.factoryRegistryInstance.$();
		if (instance == null) {
			try {
				Constructor<? extends CFactoryRegistry> c = clazz.getConstructor();
				instance = c.newInstance();
				g.factoryRegistryInstance.$(instance);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		return instance;
	}

	/**
	 * Creates and returns an object from the given class.
	 *
	 * @param clazz
	 *
	 * @return mixed
	 */
	@CodeConfirmed("blue.2.2.5")
	protected <T> T getObject(Class<T> clazz, IIdentityBean idBean, SQLExecutor executor) {
		try {
			Constructor c = clazz.getDeclaredConstructor(IIdentityBean.class, SQLExecutor.class);
			c.setAccessible(true);
			return (T)c.newInstance(idBean, executor);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

}
