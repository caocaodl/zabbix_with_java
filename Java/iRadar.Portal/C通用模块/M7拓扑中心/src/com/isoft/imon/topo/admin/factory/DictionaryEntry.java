 package com.isoft.imon.topo.admin.factory;
 
 /**
 * 
 * 数据字典实体类
 * @author Administrator
 *
 */
public final class DictionaryEntry
 {
   private String key;
   private String value;
 
   public String getValue()
   {
     return this.value;
   }
 
   public void setValue(String value) {
     this.value = value;
   }
 
   public String getKey() {
     return this.key;
   }
 
   public void setKey(String key) {
     this.key = key;
   }
 }

