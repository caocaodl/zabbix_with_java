package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EMAIL;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EXEC;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_EZ_TEXTING;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_JABBER;
import static com.isoft.iradar.inc.Defines.MEDIA_TYPE_SMS;
import static com.isoft.types.CArray.map;

import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;

@CodeConfirmed("benne.2.2.6")
public class MediaUtil {

	private MediaUtil() {
	}
	
	public static CArray<String> media_type2str() {
		CArray<String> mediaTypes = map(
				MEDIA_TYPE_EMAIL, _("Email"),
				MEDIA_TYPE_EXEC, _("Script"), 
				MEDIA_TYPE_SMS, _("SMS"),
				MEDIA_TYPE_JABBER, _("Jabber"), 
				MEDIA_TYPE_EZ_TEXTING, _("Ez Texting"));
		mediaTypes.sort(true);
		return mediaTypes;
	}

	public static String media_type2str(int type) {
		CArray<String> mediaTypes = map(
			MEDIA_TYPE_EMAIL, _("Email"),
			MEDIA_TYPE_EXEC, _("Script"),
			MEDIA_TYPE_SMS, _("SMS"),
			MEDIA_TYPE_JABBER, _("Jabber"),
			MEDIA_TYPE_EZ_TEXTING, _("Ez Texting")
		);
		if (mediaTypes.containsKey(type)) {
			return mediaTypes.get(type);
		} else {
			return _("Unknown");
		}
	}
}
