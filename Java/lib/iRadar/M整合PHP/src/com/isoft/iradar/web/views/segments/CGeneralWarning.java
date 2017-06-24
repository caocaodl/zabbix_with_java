package com.isoft.iradar.web.views.segments;

import java.util.Map;

import com.isoft.framework.common.interfaces.IIdentityBean;
import com.isoft.framework.persistlayer.SQLExecutor;
import static com.isoft.iradar.Cphp.*;
import static com.isoft.iradar.inc.HtmlUtil.*;
import com.isoft.types.CArray;
import static com.isoft.types.CArray.*;
import static com.isoft.iradar.inc.Defines.*;

import com.isoft.iradar.tags.CButton;
import com.isoft.iradar.tags.CWarning;
import com.isoft.iradar.tags.CWidget;
import com.isoft.iradar.tags.html.CPageHeader;
import com.isoft.iradar.web.views.CViewSegment;
import com.isoft.types.Mapper.Nest;

public class CGeneralWarning extends CViewSegment {

	@Override
	public CWidget doWidget(IIdentityBean idBean, SQLExecutor executor, Map data) {
		CPageHeader pageHeader = new CPageHeader(_("Warning")+" ["+_s("refreshed every %1$s sec.", 30)+"]");
		pageHeader.addCssInit();
		pageHeader.display();
		
		echo("<body>");
		
		// check if a CWarning object is passed
		CWarning warning = (CWarning)Nest.value(data,"warning").$();
		if (warning == null) {
			CArray message = Nest.value(data,"message").asCArray();

			if (isset(message,"header")) {
				message = array(bold(Nest.value(message,"header").asString()), BR(), Nest.value(message,"text").$());
			}

			// if not - render a standard warning with a message
			warning = new CWarning("iRadar "+IRADAR_VERSION, message);
			warning.setButtons(array(new CButton("login", _("Retry"), "document.location.reload();", "formlist")));
		}

		warning.show();
		
		echo("<script type=\"text/javascript\">");
		echo("	setTimeout(\"document.location.reload();\", 30000);");
		echo("</script>");
		echo("</body>");
		echo("</html>");
		return null;
	}

}
