package com.isoft.model;


public class SelectItemGroup extends SelectItem {
    private static final long serialVersionUID = 1L;

    private static final SelectItem[] EMPTY_SELECT_ITEMS = new SelectItem[0];

    // FIELDS
    private SelectItem[] _selectItems;

    // CONSTRUCTORS
    public SelectItemGroup() {
        super();
        _selectItems = EMPTY_SELECT_ITEMS;
    }

    public SelectItemGroup(String label) {
        super("", label, null, false);
        _selectItems = EMPTY_SELECT_ITEMS;
    }

    public SelectItemGroup(String label, String description, boolean disabled,
            SelectItem[] selectItems) {
        super("", label, description, disabled);
        if (selectItems == null)
            throw new NullPointerException("selectItems");
        _selectItems = selectItems;
    }

    // METHODS
    public SelectItem[] getSelectItems() {
        return _selectItems;
    }

    public void setSelectItems(SelectItem[] selectItems) {
        if (selectItems == null)
            throw new NullPointerException("selectItems");
        _selectItems = selectItems;
    }
}
