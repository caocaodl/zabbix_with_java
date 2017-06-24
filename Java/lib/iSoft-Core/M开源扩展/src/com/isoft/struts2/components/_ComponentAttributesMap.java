package com.isoft.struts2.components;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.struts2.StrutsException;

class _ComponentAttributesMap implements Map, Serializable {
    
    private static final long serialVersionUID = 1L;

    private static final Object[] EMPTY_ARGS = new Object[0];

    private AndurilComponent _component;
    private Map _attributes = null;    //We delegate instead of derive from HashMap, so that we can later optimize Serialization
    private transient Map _propertyDescriptorMap = null;

    _ComponentAttributesMap(AndurilComponent component) {
        _component = component;
        _attributes = new HashMap();
    }

    _ComponentAttributesMap(AndurilComponent component, Map attributes) {
        _component = component;
        _attributes = attributes;
    }

    public int size() {
        return _attributes.size();
    }

    public void clear() {
        _attributes.clear();
    }

    public boolean isEmpty() {
        return _attributes.isEmpty();
    }

    public boolean containsKey(Object key) {
        checkKey(key);
        if (getPropertyDescriptor((String) key) == null) {
            return _attributes.containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * @param value
     *            null is allowed
     */
    public boolean containsValue(Object value) {
        return _attributes.containsValue(value);
    }

    public Collection<Object> values() {
        return _attributes.values();
    }

    public void putAll(Map t) {
        for (Iterator it = t.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Entry) it.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public Set entrySet() {
        return _attributes.entrySet();
    }

    public Set keySet() {
        return _attributes.keySet();
    }

    public Object get(Object key) {
        checkKey(key);
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor((String) key);
        if (propertyDescriptor != null) {
            return getComponentProperty(propertyDescriptor);
        }
        Object mapValue = _attributes.get(key);
        if (mapValue != null) {
            return mapValue;
        }
        ValueBinding vb = _component.getValueBinding((String) key);
        if (vb != null) {
            return vb.getValue(_component.getStack());
        }
        return null;
    }

    public Object remove(Object key) {
        checkKey(key);
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor((String) key);
        if (propertyDescriptor != null) {
            throw new IllegalArgumentException(
                    "Cannot remove component property attribute");
        }
        return _attributes.remove(key);
    }

    /**
     * @param key
     *            String, null is not allowed
     * @param value
     *            null is allowed
     */
    public Object put(Object key, Object value) {
        checkKeyAndValue(key, value);
        PropertyDescriptor propertyDescriptor = getPropertyDescriptor((String) key);
        if (propertyDescriptor != null) {
            if (propertyDescriptor.getReadMethod() != null) {
                Object oldValue = getComponentProperty(propertyDescriptor);
                setComponentProperty(propertyDescriptor, value);
                return oldValue;
            } else {
                setComponentProperty(propertyDescriptor, value);
                return null;
            }
        } else {
            return _attributes.put(key, value);
        }
    }


    private PropertyDescriptor getPropertyDescriptor(String key) {
        if (_propertyDescriptorMap == null) {
            BeanInfo beanInfo;
            try {
                beanInfo = Introspector.getBeanInfo(_component.getClass());
            } catch (IntrospectionException e) {
                throw new StrutsException(e);
            }
            PropertyDescriptor[] propertyDescriptors = beanInfo
                    .getPropertyDescriptors();
            _propertyDescriptorMap = new HashMap();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor propertyDescriptor = propertyDescriptors[i];
                if (propertyDescriptor.getReadMethod() != null) {
                    _propertyDescriptorMap.put(propertyDescriptor.getName(),
                            propertyDescriptor);
                }
            }
        }
        return (PropertyDescriptor) _propertyDescriptorMap.get(key);
    }


    private Object getComponentProperty(PropertyDescriptor propertyDescriptor) {
        Method readMethod = propertyDescriptor.getReadMethod();
        if (readMethod == null) {
            throw new IllegalArgumentException("Component property "
                    + propertyDescriptor.getName() + " is not readable");
        }
        try {
            return readMethod.invoke(_component, EMPTY_ARGS);
        } catch (Exception e) {
            throw new StrutsException("Could not get property "
                    + propertyDescriptor.getName() + " of component ", e);
        }
    }


    private void setComponentProperty(PropertyDescriptor propertyDescriptor,
            Object value) {
        Method writeMethod = propertyDescriptor.getWriteMethod();
        if (writeMethod == null) {
            throw new IllegalArgumentException("Component property "
                    + propertyDescriptor.getName() + " is not writable");
        }
        try {
            writeMethod.invoke(_component, new Object[] { value });
        } catch (Exception e) {
            throw new StrutsException("Could not set property "
                    + propertyDescriptor.getName() + " of component ", e);
        }
    }


    private void checkKeyAndValue(Object key, Object value) {
        if (value == null)
            throw new NullPointerException("value");
        checkKey(key);
    }

    private void checkKey(Object key) {
        if (key == null)
            throw new NullPointerException("key");
        if (!(key instanceof String))
            throw new ClassCastException("key is not a String");
    }

    Map getUnderlyingMap() {
        return _attributes;
    }

    @Override
    public boolean equals(Object obj) {
        return _attributes.equals(obj);
    }

    @Override
    public int hashCode() {
        return _attributes.hashCode();
    }
}