package com.isoft.iradar.inc;

import static com.isoft.iradar.Cphp._;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;

@CodeConfirmed("benne.2.2.6")
public class LocalesUtil {

	private LocalesUtil() {
	}

	/**
	 * Returns a list of all used locales.
	 *
	 * Each locale has the following properties:
	 * - name       - the full name of the locale
	 * - display    - whether to display the locale in the frontend
	 *
	 * @return array    an array of locales with locale codes as keys and arrays as values
	 */
	public static CArray<Map> getLocales() {
		return map(
			"en_GB", map("name", _("English (en_GB)"),	"display", true),
			"en_US", map("name", _("English (en_US)"),	"display", true),
			"bg_BG", map("name", _("Bulgarian (bg_BG)"),	"display", false),
			"zh_CN", map("name", _("Chinese (zh_CN)"),	"display", false),
			"zh_TW", map("name", _("Chinese (zh_TW)"),	"display", false),
			"cs_CZ", map("name", _("Czech (cs_CZ)"),		"display", false),
			"nl_NL", map("name", _("Dutch (nl_NL)"),		"display", false),
			"fi_FI", map("name", _("Finnish (fi_FI)"),	"display", false),
			"fr_FR", map("name", _("French (fr_FR)"),		"display", true),
			"de_DE", map("name", _("German (de_DE)"),		"display", false),
			"el_GR", map("name", _("Greek (el_GR)"),		"display", false),
			"hu_HU", map("name", _("Hungarian (hu_HU)"),	"display", false),
			"id_ID", map("name", _("Indonesian (id_ID)"),	"display", false),
			"it_IT", map("name", _("Italian (it_IT)"),	"display", true),
			"ko_KR", map("name", _("Korean (ko_KR)"),		"display", false),
			"ja_JP", map("name", _("Japanese (ja_JP)"),	"display", true),
			"lv_LV", map("name", _("Latvian (lv_LV)"),	"display", false),
			"lt_LT", map("name", _("Lithuanian (lt_LT)"),	"display", false),
			"fa_IR", map("name", _("Persian (fa_IR)"),	"display", false),
			"pl_PL", map("name", _("Polish (pl_PL)"),		"display", true),
			"pt_BR", map("name", _("Portuguese (pt_BR)"),	"display", true),
			"pt_PT", map("name", _("Portuguese (pt_PT)"),	"display", false),
			"ro_RO", map("name", _("Romanian (ro_RO)"),	"display", false),
			"ru_RU", map("name", _("Russian (ru_RU)"),	"display", true),
			"sk_SK", map("name", _("Slovak (sk_SK)"),		"display", true),
			"es_ES", map("name", _("Spanish (es_ES)"),	"display", false),
			"sv_SE", map("name", _("Swedish (sv_SE)"),	"display", false),
			"tr_TR", map("name", _("Turkish (tr_TR)"),	"display", false),
			"uk_UA", map("name", _("Ukrainian (uk_UA)"),	"display", false),
			"vi_VN", map("name", _("Vietnamese (vi_VN)"),	"display", false)
		);
	}
}
