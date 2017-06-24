package com.isoft.struts2.views.tags.ui.ztree;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts2.components.IteratorComponent;

import com.isoft.model.TreeNodeItem;
import com.isoft.struts2.components.TreeNodeHolder;
import com.opensymphony.xwork2.util.ValueStack;

public class $ZTreeNodeIterator extends IteratorComponent implements
		TreeNodeHolder {

	public $ZTreeNodeIterator(ValueStack stack) {
		super(stack);
	}

	@Override
	public boolean usesBody() {
		return false;
	}

	private List<TreeNodeItem> children = null;

	@Override
	public void pushTreeNodeItem(TreeNodeItem treeNodeItem) {
		if (children == null) {
			children = new ArrayList<TreeNodeItem>(1);
		}
		children.add(treeNodeItem);
	}

	@Override
	protected void popComponentStack() {
		super.popComponentStack();
		Object component = this.getComponentStack().peek();
		if (component instanceof TreeNodeHolder) {
			if (children != null && !children.isEmpty()) {
				for (TreeNodeItem item : children) {
					((TreeNodeHolder) component).pushTreeNodeItem(item);
				}
				this.children = null;
			}
		}
	}

}
