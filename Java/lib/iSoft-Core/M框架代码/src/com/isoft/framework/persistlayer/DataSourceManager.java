package com.isoft.framework.persistlayer;

import javax.sql.DataSource;

import com.isoft.utils.DataSourceUtil;
import com.isoft.web.listener.DataSourceEnum;

public class DataSourceManager {
    public static DataSource getDataSource(DataSourceEnum dataSource){
//        if(DebugUtil.isDebugEnabled()){
//            DebugUtil.debug("[[[[[[[[[[[[[[获得数据源]]]]]]]]]]]]]]]]]]]");
//        }
        return DataSourceUtil.getDataSource(dataSource.getDsName());
    }
}
