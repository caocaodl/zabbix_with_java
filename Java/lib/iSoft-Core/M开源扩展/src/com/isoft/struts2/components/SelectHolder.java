package com.isoft.struts2.components;

import java.util.List;

import com.isoft.model.SelectItem;

public interface SelectHolder {
    public void pushSelectItem(SelectItem selectItem);
    public void pushSelectItems(List<SelectItem> selectItemList);
}
