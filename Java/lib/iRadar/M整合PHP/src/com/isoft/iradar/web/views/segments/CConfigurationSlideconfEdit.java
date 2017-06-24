package com.isoft.iradar.web.views.segments;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.Cphp.empty;
import static com.isoft.iradar.inc.Defines.RDA_TEXTBOX_STANDARD_SIZE;
import static com.isoft.iradar.inc.Defines.SPACE;
import static com.isoft.iradar.inc.HtmlUtil.makeFormFooter;
import static com.isoft.iradar.inc.HtmlUtil.url_params;
import static com.isoft.iradar.inc.ViewsUtil.includeSubView;
import static com.isoft.types.CArray.array;

import java.util.Map;
import java.util.Map.Entry;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import com.isoft.iradar.inc.ScreensUtil;
import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CButtonCancel;
import com.isoft.iradar.tags.CButtonDelete;
import com.isoft.iradar.tags.CCol;
import com.isoft.iradar.tags.CDiv;
import com.isoft.iradar.tags.CForm;
import com.isoft.iradar.tags.CFormList;
import com.isoft.iradar.tags.CNumericBox;
import com.isoft.iradar.tags.CRow;
import com.isoft.iradar.tags.CSpan;
import com.isoft.iradar.tags.CSubmit;
import com.isoft.iradar.tags.CTabView;
import com.isoft.iradar.tags.CTableInfo;
import com.isoft.iradar.tags.CTextBox;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.CArray;
import com.isoft.types.Mapper.Nest;

public class CConfigurationSlideconfEdit extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		includeSubView("js/configuration.slideconf.edit.js");

		CWidget slideWidget = new CWidget();
		slideWidget.addPageHeader(_("CONFIGURATION OF SLIDE SHOWS"));

		// create form
		CForm slideForm = new CForm();
		slideForm.setName("slideForm");
		slideForm.addVar("form", Nest.value(data,"form").$());
		slideForm.addVar("slides", Nest.value(data,"slides_without_delay").$());
		if (!empty(Nest.value(data,"slideshowid").$())) {
			slideForm.addVar("slideshowid", Nest.value(data,"slideshowid").$());
		}

		// create slide form list
		CFormList slideFormList = new CFormList("slideFormList");
		CTextBox nameTextBox = new CTextBox("name", Nest.value(data,"name").asString(), RDA_TEXTBOX_STANDARD_SIZE);
		nameTextBox.attr("autofocus", "autofocus");
		slideFormList.addRow(_("Name"), nameTextBox);
		slideFormList.addRow(_("Default delay (in seconds)"), new CNumericBox("delay", Nest.value(data,"delay").asString(), 5, false, false, false));

		// append slide table
		CTableInfo slideTable = new CTableInfo(null, "formElementTable");
		slideTable.setAttribute("style", "min-width: 312px;");
		slideTable.setAttribute("id", "slideTable");
		slideTable.setHeader(array(
			new CCol(SPACE, null, null, "15"),
			new CCol(SPACE, null, null, "15"),
			_("Screen"),
			new CCol(_("Delay"), null, null, "70"),
			new CCol(_("Action"), null, null, "50")
		));

		int i = 1;
		for (Entry<Object, Map> e : ((CArray<Map>)Nest.value(data,"slides").asCArray()).entrySet()) {
		    Object step = e.getKey();
		    Map slides = e.getValue();
			String name = "";
			if (!empty(Nest.value(slides,"screenid").$())) {
				Map screen = ScreensUtil.get_screen_by_screenid(idBean, executor, Nest.value(slides,"screenid").asLong());
				if (!empty(screen)) {
					name = Nest.value(screen,"name").asString();
				}
			}

			CNumericBox delay = new CNumericBox("slides["+step+"][delay]", !empty(Nest.value(slides,"delay").$()) ? Nest.value(slides,"delay").asString() : "", 5, false, true, false);
			delay.setAttribute("placeholder", _("default"));

			CButton removeButton = new CButton("remove_"+step, _("Remove"), "javascript: removeSlide(this);", "link_menu");
			removeButton.setAttribute("remove_slide", step);

			CRow row = new CRow(
				array(
					new CSpan(null, "ui-icon ui-icon-arrowthick-2-n-s move"),
					new CSpan((i++)+":", "rowNum", "current_slide_"+step),
					name,
					delay,
					removeButton
				),
				"sortable",
				"slides_"+step
			);
			slideTable.addRow(row);
		}

		CCol addButtonColumn = new CCol(
			empty(Nest.value(data,"work_slide").$())
				? new CButton("add", _("Add"),
					"return PopUp(\"popup.action?srctbl=screens&srcfld1=screenid&dstfrm="+slideForm.getName()+
						"&multiselect=1&writeonly=1\", 450, 450)",
					"link_menu")
				: null,
			null,
			5
		);
		addButtonColumn.setAttribute("style", "vertical-align: middle;");
		slideTable.addRow(new CRow(addButtonColumn, null, "screenListFooter"));

		slideFormList.addRow(_("Slides"), new CDiv(slideTable, "objectgroup inlineblock border_dotted"));

		// append tabs to form
		CTabView slideTab = new CTabView();
		slideTab.addTab("slideTab", _("Slide"), slideFormList);
		slideForm.addItem(slideTab);

		// append buttons to form
		if (empty(Nest.value(data,"slideshowid").$())) {
			slideForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				new CButtonCancel()
			));
		} else {
			slideForm.addItem(makeFormFooter(
				new CSubmit("save", _("Save")),
				array(
					new CSubmit("clone", _("Clone")),
					new CButtonDelete(_("Delete slide show?"), url_params(idBean, array("form", "slideshowid"))),
					new CButtonCancel()
				)
			));
		}

		slideWidget.addItem(slideForm);

		return slideWidget;
	}

}
