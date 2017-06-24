package com.isoft.iradar.tree;

import static com.isoft.iradar.Cphp.count;
import static com.isoft.iradar.Cphp.isArray;
import static com.isoft.iradar.Cphp.isset;
import static com.isoft.iradar.Cphp.rtrim;
import static com.isoft.iradar.Cphp.str_replace;
import static com.isoft.iradar.inc.FuncsUtil.rda_strlen;
import static com.isoft.iradar.inc.JsUtil.rda_add_post_js;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.model.CWebUser;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CImg;
import com.isoft.iradar.tags.CJSScript;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CTable;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTag;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CTree {
	
	public CArray<Map> tree;
	public CArray<String> fields;
	public String treename;
	//private Integer size;
	private Integer maxlevel;

	public CTree(String treename) throws InstantiationException {
		this(treename, array() );
	}
	
	public CTree(String treename, CArray<Map> value) throws InstantiationException {
		this(treename, value, array());
	}
	
	public CTree(String treename, CArray<Map> value, CArray fields) throws InstantiationException {
		this.maxlevel = 0;
		this.tree = value;
		this.fields = fields;
		this.treename = treename;
		//this.size = count(value);
		
		if (!this.checkTree()) {
			this.destroy();
			throw new InstantiationException();
		} else {
			this.countDepth();
		}
	}
	
	public CArray<Map> getTree() {
		return this.tree;
	}
	
	public CArray getHTML(IIdentityBean idBean, SQLExecutor executor) {
		CArray html = new CArray();
		html.add(this.createJS());
		html.add(this.simpleHTML(idBean, executor));
		return html;
	}
	
	private CRow makeHeaders() {
		int c = 0;
		CRow tr = new CRow(this.fields.get("caption"), "header");
		this.fields.remove("caption");
		CArray<String> fields = array();
        for (Entry<Object, String> e : this.fields.entrySet()) {
            Object id = e.getKey();
            String caption = e.getValue();
			tr.addItem(caption);
			Nest.value(fields, c).$(id);
			c++;
		}
        this.fields = fields;
		return tr;
	}
	
	private CTableInfo simpleHTML(IIdentityBean idBean, SQLExecutor executor) {
		CTableInfo table = new CTableInfo();
		table.addRow(this.makeHeaders());

		for (Object id : this.tree.keySet()) {
			table.addRow(this.makeRow(idBean, executor, id));
		}
		return table;
	}
	
	private CRow makeRow(IIdentityBean idBean, SQLExecutor executor,Object id) {
		CTable table = new CTable();
		CRow tr = this.makeSImgStr(id);
		tr.addItem(Nest.value(this.tree,id,"caption").$());

		table.addRow(tr);

		tr = new CRow();
		tr.addItem(table);
		tr.setAttribute("id", "id_"+id);
		tr.setAttribute("style", Nest.value(this.tree,id,"parentid").asInteger() != 0 ? "display: none;" : "");

		for (String value : this.fields) {
			tr.addItem(this.makeCol(idBean, executor, id, value));
		}
		return tr;
	}
	
	/**
	 * Returns a column object for the given row and field.
	 *
	 * @param rowId
	 * @param colName
	 *
	 * @return CCol
	 */
	protected CCol makeCol(IIdentityBean idBean, SQLExecutor executor, Object rowId, String colName) {
		return new CCol(Nest.value(this.tree,rowId,colName).$());
	}
	
	private CRow makeSImgStr(Object id) {
		CRow tr = new CRow();
		String nodeimg = Nest.value(this.tree,id,"nodeimg").asString();
		int count = isset(nodeimg) ? rda_strlen(nodeimg) : 0;

		for (int i = 0; i < count; i++) {
			CCol td = new CCol();
			Object img = null;

			CTag div = null;
			switch (nodeimg.charAt(i)) {
				case 'O':
					img = new CImg("images/general/tree/zero.gif", "o", 22, 14);
					break;

				case 'I':
					td.setAttribute("style", "background-image: url(images/general/tree/pointc.gif);");
					img = new CImg("images/general/tree/zero.gif", "i", 22, 14);
					break;

				case 'L':
					td.setAttribute("valign", "top");
					div = new CTag("div", "yes");
					div.setAttribute("style", "height: 10px; background-image: url(images/general/tree/pointc.gif);");

					if (Nest.value(this.tree,id,"nodetype").asInteger() == 2) {
						img = new CImg("images/general/tree/plus.gif", "y", 22, 14);
						((CImg)img).setAttribute("onclick", treename+".closeSNodeX(\""+id+"\", this);");
						((CImg)img).setAttribute("id", "idi_"+id);
						((CImg)img).setAttribute("class", "pointer");
					} else {
						img = new CImg("images/general/tree/pointl.gif", "y", 22, 14);
					}
					div.addItem(img);
					img = div;
					break;

				case 'T':
					td.setAttribute("valign", "top");
					if (Nest.value(this.tree,id,"nodetype").asInteger() == 2) {
						td.setAttribute("style", "background-image: url(images/general/tree/pointc.gif);");
						img = new CImg("images/general/tree/plus.gif", "t", 22, 14);
						((CImg)img).setAttribute("onclick", treename+".closeSNodeX(\""+id+"\", this);");
						((CImg)img).setAttribute("id", "idi_"+id);
						((CImg)img).setAttribute("class", "pointer");
						((CImg)img).setAttribute("style", "top: 1px; position: relative;");
					} else {
						td.setAttribute("style", "background-image: url(images/general/tree/pointc.gif);");
						img = new CImg("images/general/tree/pointl.gif", "t", 22, 14);
					}
					break;
			}

			td.addItem(img);
			tr.addItem(td);
		}

		return tr;
	}
	
	private void countDepth() {
        for (Entry<Object, Map> e : this.tree.entrySet()) {
            Object _id = e.getKey();
            Map _rows = e.getValue();
            
			if ("0".equals(Nest.value(_rows,"id").asString())) {
				continue;
			}

			Object _parentid = Nest.value(this.tree,_id,"parentid").$();

			Nest.value(this.tree,_id,"nodeimg").$(this.getImg(_id, isset(Nest.value(this.tree,_parentid,"nodeimg").$()) ? Nest.value(this.tree,_parentid,"nodeimg").asString() : ""));
			Nest.value(this.tree,_parentid,"nodetype").$(2);
			Nest.value(this.tree,_id,"Level").$(isset(Nest.value(this.tree,_parentid,"Level").$()) ? Nest.value(this.tree,_parentid,"Level").asInteger() + 1 : 1);

			if (this.maxlevel > Nest.value(this.tree, _id, "Level").asInteger()) {
				this.maxlevel = Nest.value(this.tree, _id, "Level").asInteger();
			}
		}
	}
	
	public CJSScript createJS() {
		String _js = "<script src=\"js/class.ctree.js\" type=\"text/javascript\"></script>\n"+
				"<script type=\"text/javascript\"> var "+this.treename+"_tree = {};";

        for (Entry<Object, Map> e : this.tree.entrySet()) {
            Object _id = e.getKey();
            Map _rows = e.getValue();
			Object _parentid = Nest.value(_rows,"parentid").$();
			Nest.value(tree, _parentid, "nodelist").$(Nest.value(tree, _parentid, "nodelist").asString()+_id+",");
		}

        for (Entry<Object, Map> e : this.tree.entrySet()) {
            Object _id = e.getKey();
            Map _rows = e.getValue();
			if (Nest.value(_rows,"nodetype").asInteger() == 2) {
				Nest.value(_rows,"nodelist").$(rtrim(Nest.value(_rows,"nodelist").asString(), ','));
				_js += this.treename+"_tree[\""+_id+"\"] = { status: \"close\", nodelist : \""+Nest.value(_rows,"nodelist").$()+"\", parentid : \""+Nest.value(_rows,"parentid").$()+"\"};";
				_js += "\n";
			}
		}

		_js+= "var "+this.treename+" = null";
		_js+= "</script>"+"\n";

		rda_add_post_js(this.treename+" = new CTree(\"tree_"+CWebUser.get("alias")+"_"+this.treename+"\", "+treename+"_tree);");

		return new CJSScript(_js);
	}
	
	private String getImg(Object id, String img) {
		img = str_replace("T", "I", img);
		img = str_replace("L", "O", img);
		char ch = 'L';

		Object parentid = Nest.value(tree,id,"parentid").$();
		
		CArray childs = Nest.value(tree,parentid,"childnodes").asCArray();
		int childs_last = count(childs) - 1;

		if (isset(childs.get(childs_last)) && !id.equals(childs.get(childs_last))) {
			ch = 'T';
		}
		img += ch;
		return img;
	}
	
	private boolean checkTree() {
		if (!isArray(this.tree)) {
			return false;
		}
		
        for (Entry<Object, Map> e : this.tree.entrySet()) {
            Object id = e.getKey();
            Map cell = e.getValue();

            Nest.value(this.tree, id, "nodetype").$(0);

			Object parentid = Nest.value(cell, "parentid").$();
			if(!isset(Nest.value(this.tree, parentid, "childnodes").$())){
				Nest.value(this.tree, parentid, "childnodes").$(array());
			}
			Nest.value(this.tree, parentid, "childnodes").asCArray().add(id);
			Nest.value(this.tree, id, "nodelist").$("");
		}
		return true;
	}

	private void destroy() {
		this.tree = null;
		this.fields = null;
		this.treename = null;
		//this.size = null;
		this.maxlevel = null;
	}
}
