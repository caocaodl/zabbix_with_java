package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp._s;
import static com.isoft.iradar.inc.Defines.RDA_TEXTAREA_BIG_WIDTH;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.macros.CMacrosResolverHelper;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTextArea;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CMonitoringTriggerComment extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/monitoring.triggerComment.js",data);

		CWidget commentWidget = new CWidget("triggerComment");
		commentWidget.addPageHeader(_("TRIGGER COMMENTS"));

		// create form
		CForm commentForm = new CForm();
		commentForm.setName("commentForm");
		commentForm.addVar("triggerid", Nest.value(data,"triggerid").$());

		// create form list
		CFormList commentFormList = new CFormList("commentFormList");

		CTextArea commentTextArea = new CTextArea("comments", CMacrosResolverHelper.resolveTriggerDescription(idBean, executor, Nest.value(data,"trigger").asCArray()), (Map)map(
			"rows" , 25, "width" , RDA_TEXTAREA_BIG_WIDTH, "readonly" , Nest.value(data,"isCommentExist").$()
		));
		commentTextArea.attr("autofocus", "autofocus");
		commentFormList.addRow(_("Comments"), commentTextArea);

		// append tabs to form
		CTabView commentTab = new CTabView();
		commentTab.addTab("commentTab", _s("Comments for \"%s\".", Nest.value(data,"trigger","description").$()), commentFormList);
		commentForm.addItem(commentTab);

		// append buttons to form
		CSubmit saveButton = new CSubmit("save", _("Save"));
		saveButton.setEnabled(!Nest.value(data,"isCommentExist").asBoolean());

		CButton editButton = null;
		if (Nest.value(data,"isCommentExist").asBoolean()) {
			editButton  = new CButton("edit", _("Edit"));
			editButton.setEnabled(Nest.value(data,"isTriggerEditable").asBoolean());
		} else {
			editButton = null;
		}

		commentForm.addItem(makeFormFooter(
			saveButton,
			array(editButton, new CButtonCancel("&triggerid="+Nest.value(data,"triggerid").$()))
		));

		commentWidget.addItem(commentForm);

		return commentWidget;
	}

}
