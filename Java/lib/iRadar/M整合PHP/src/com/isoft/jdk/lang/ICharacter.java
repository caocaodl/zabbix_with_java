package com.isoft.jdk.lang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;


public class ICharacter {
	
	static final int ERROR = 0xFFFFFFFF;
	
	public static boolean isSurrogate(char ch) {
        return ch >= Character.MIN_SURROGATE && ch < (Character.MAX_SURROGATE + 1);
    }
	
	/**
     * Determines if the specified character (Unicode code point) is an alphabet.
     * <p>
     * A character is considered to be alphabetic if its general category type,
     * provided by {@link Character#getType(int) getType(codePoint)}, is any of
     * the following:
     * <ul>
     * <li> <code>UPPERCASE_LETTER</code>
     * <li> <code>LOWERCASE_LETTER</code>
     * <li> <code>TITLECASE_LETTER</code>
     * <li> <code>MODIFIER_LETTER</code>
     * <li> <code>OTHER_LETTER</code>
     * <li> <code>LETTER_NUMBER</code>
     * </ul>
     * or it has contributory property Other_Alphabetic as defined by the
     * Unicode Standard.
     *
     * @param   codePoint the character (Unicode code point) to be tested.
     * @return  <code>true</code> if the character is a Unicode alphabet
     *          character, <code>false</code> otherwise.
     * @since   1.7
     */
    public static boolean isAlphabetic(int codePoint) {
        return (((((1 << Character.UPPERCASE_LETTER) |
            (1 << Character.LOWERCASE_LETTER) |
            (1 << Character.TITLECASE_LETTER) |
            (1 << Character.MODIFIER_LETTER) |
            (1 << Character.OTHER_LETTER) |
            (1 << Character.LETTER_NUMBER)) >> Character.getType(codePoint)) & 1) != 0) ||
            CharacterData.of(codePoint).isOtherAlphabetic(codePoint);
    }
	
    /**
     * Determines if the specified character (Unicode code point) is a CJKV
     * (Chinese, Japanese, Korean and Vietnamese) ideograph, as defined by
     * the Unicode Standard.
     *
     * @param   codePoint the character (Unicode code point) to be tested.
     * @return  <code>true</code> if the character is a Unicode ideograph
     *          character, <code>false</code> otherwise.
     * @since   1.7
     */
    public static boolean isIdeographic(int codePoint) {
        return CharacterData.of(codePoint).isIdeographic(codePoint);
    }
	
    
    /**
     * A family of character subsets representing the character scripts
     * defined in the <a href="http://www.unicode.org/reports/tr24/">
     * <i>Unicode Standard Annex #24: Script Names</i></a>. Every Unicode
     * character is assigned to a single Unicode script, either a specific
     * script, such as {@link Character.UnicodeScript#LATIN Latin}, or
     * one of the following three special values,
     * {@link Character.UnicodeScript#INHERITED Inherited},
     * {@link Character.UnicodeScript#COMMON Common} or
     * {@link Character.UnicodeScript#UNKNOWN Unknown}.
     *
     * @since 1.7
     */
    public static enum UnicodeScript {
        /**
         * Unicode script "Common".
         */
        COMMON,

        /**
         * Unicode script "Latin".
         */
        LATIN,

        /**
         * Unicode script "Greek".
         */
        GREEK,

        /**
         * Unicode script "Cyrillic".
         */
        CYRILLIC,

        /**
         * Unicode script "Armenian".
         */
        ARMENIAN,

        /**
         * Unicode script "Hebrew".
         */
        HEBREW,

        /**
         * Unicode script "Arabic".
         */
        ARABIC,

        /**
         * Unicode script "Syriac".
         */
        SYRIAC,

        /**
         * Unicode script "Thaana".
         */
        THAANA,

        /**
         * Unicode script "Devanagari".
         */
        DEVANAGARI,

        /**
         * Unicode script "Bengali".
         */
        BENGALI,

        /**
         * Unicode script "Gurmukhi".
         */
        GURMUKHI,

        /**
         * Unicode script "Gujarati".
         */
        GUJARATI,

        /**
         * Unicode script "Oriya".
         */
        ORIYA,

        /**
         * Unicode script "Tamil".
         */
        TAMIL,

        /**
         * Unicode script "Telugu".
         */
        TELUGU,

        /**
         * Unicode script "Kannada".
         */
        KANNADA,

        /**
         * Unicode script "Malayalam".
         */
        MALAYALAM,

        /**
         * Unicode script "Sinhala".
         */
        SINHALA,

        /**
         * Unicode script "Thai".
         */
        THAI,

        /**
         * Unicode script "Lao".
         */
        LAO,

        /**
         * Unicode script "Tibetan".
         */
        TIBETAN,

        /**
         * Unicode script "Myanmar".
         */
        MYANMAR,

        /**
         * Unicode script "Georgian".
         */
        GEORGIAN,

        /**
         * Unicode script "Hangul".
         */
        HANGUL,

        /**
         * Unicode script "Ethiopic".
         */
        ETHIOPIC,

        /**
         * Unicode script "Cherokee".
         */
        CHEROKEE,

        /**
         * Unicode script "Canadian_Aboriginal".
         */
        CANADIAN_ABORIGINAL,

        /**
         * Unicode script "Ogham".
         */
        OGHAM,

        /**
         * Unicode script "Runic".
         */
        RUNIC,

        /**
         * Unicode script "Khmer".
         */
        KHMER,

        /**
         * Unicode script "Mongolian".
         */
        MONGOLIAN,

        /**
         * Unicode script "Hiragana".
         */
        HIRAGANA,

        /**
         * Unicode script "Katakana".
         */
        KATAKANA,

        /**
         * Unicode script "Bopomofo".
         */
        BOPOMOFO,

        /**
         * Unicode script "Han".
         */
        HAN,

        /**
         * Unicode script "Yi".
         */
        YI,

        /**
         * Unicode script "Old_Italic".
         */
        OLD_ITALIC,

        /**
         * Unicode script "Gothic".
         */
        GOTHIC,

        /**
         * Unicode script "Deseret".
         */
        DESERET,

        /**
         * Unicode script "Inherited".
         */
        INHERITED,

        /**
         * Unicode script "Tagalog".
         */
        TAGALOG,

        /**
         * Unicode script "Hanunoo".
         */
        HANUNOO,

        /**
         * Unicode script "Buhid".
         */
        BUHID,

        /**
         * Unicode script "Tagbanwa".
         */
        TAGBANWA,

        /**
         * Unicode script "Limbu".
         */
        LIMBU,

        /**
         * Unicode script "Tai_Le".
         */
        TAI_LE,

        /**
         * Unicode script "Linear_B".
         */
        LINEAR_B,

        /**
         * Unicode script "Ugaritic".
         */
        UGARITIC,

        /**
         * Unicode script "Shavian".
         */
        SHAVIAN,

        /**
         * Unicode script "Osmanya".
         */
        OSMANYA,

        /**
         * Unicode script "Cypriot".
         */
        CYPRIOT,

        /**
         * Unicode script "Braille".
         */
        BRAILLE,

        /**
         * Unicode script "Buginese".
         */
        BUGINESE,

        /**
         * Unicode script "Coptic".
         */
        COPTIC,

        /**
         * Unicode script "New_Tai_Lue".
         */
        NEW_TAI_LUE,

        /**
         * Unicode script "Glagolitic".
         */
        GLAGOLITIC,

        /**
         * Unicode script "Tifinagh".
         */
        TIFINAGH,

        /**
         * Unicode script "Syloti_Nagri".
         */
        SYLOTI_NAGRI,

        /**
         * Unicode script "Old_Persian".
         */
        OLD_PERSIAN,

        /**
         * Unicode script "Kharoshthi".
         */
        KHAROSHTHI,

        /**
         * Unicode script "Balinese".
         */
        BALINESE,

        /**
         * Unicode script "Cuneiform".
         */
        CUNEIFORM,

        /**
         * Unicode script "Phoenician".
         */
        PHOENICIAN,

        /**
         * Unicode script "Phags_Pa".
         */
        PHAGS_PA,

        /**
         * Unicode script "Nko".
         */
        NKO,

        /**
         * Unicode script "Sundanese".
         */
        SUNDANESE,

        /**
         * Unicode script "Batak".
         */
        BATAK,

        /**
         * Unicode script "Lepcha".
         */
        LEPCHA,

        /**
         * Unicode script "Ol_Chiki".
         */
        OL_CHIKI,

        /**
         * Unicode script "Vai".
         */
        VAI,

        /**
         * Unicode script "Saurashtra".
         */
        SAURASHTRA,

        /**
         * Unicode script "Kayah_Li".
         */
        KAYAH_LI,

        /**
         * Unicode script "Rejang".
         */
        REJANG,

        /**
         * Unicode script "Lycian".
         */
        LYCIAN,

        /**
         * Unicode script "Carian".
         */
        CARIAN,

        /**
         * Unicode script "Lydian".
         */
        LYDIAN,

        /**
         * Unicode script "Cham".
         */
        CHAM,

        /**
         * Unicode script "Tai_Tham".
         */
        TAI_THAM,

        /**
         * Unicode script "Tai_Viet".
         */
        TAI_VIET,

        /**
         * Unicode script "Avestan".
         */
        AVESTAN,

        /**
         * Unicode script "Egyptian_Hieroglyphs".
         */
        EGYPTIAN_HIEROGLYPHS,

        /**
         * Unicode script "Samaritan".
         */
        SAMARITAN,

        /**
         * Unicode script "Mandaic".
         */
        MANDAIC,

        /**
         * Unicode script "Lisu".
         */
        LISU,

        /**
         * Unicode script "Bamum".
         */
        BAMUM,

        /**
         * Unicode script "Javanese".
         */
        JAVANESE,

        /**
         * Unicode script "Meetei_Mayek".
         */
        MEETEI_MAYEK,

        /**
         * Unicode script "Imperial_Aramaic".
         */
        IMPERIAL_ARAMAIC,

        /**
         * Unicode script "Old_South_Arabian".
         */
        OLD_SOUTH_ARABIAN,

        /**
         * Unicode script "Inscriptional_Parthian".
         */
        INSCRIPTIONAL_PARTHIAN,

        /**
         * Unicode script "Inscriptional_Pahlavi".
         */
        INSCRIPTIONAL_PAHLAVI,

        /**
         * Unicode script "Old_Turkic".
         */
        OLD_TURKIC,

        /**
         * Unicode script "Brahmi".
         */
        BRAHMI,

        /**
         * Unicode script "Kaithi".
         */
        KAITHI,

        /**
         * Unicode script "Unknown".
         */
        UNKNOWN;

        private static final int[] scriptStarts = {
            0x0000,   // 0000..0040; COMMON
            0x0041,   // 0041..005A; LATIN
            0x005B,   // 005B..0060; COMMON
            0x0061,   // 0061..007A; LATIN
            0x007B,   // 007B..00A9; COMMON
            0x00AA,   // 00AA..00AA; LATIN
            0x00AB,   // 00AB..00B9; COMMON
            0x00BA,   // 00BA..00BA; LATIN
            0x00BB,   // 00BB..00BF; COMMON
            0x00C0,   // 00C0..00D6; LATIN
            0x00D7,   // 00D7..00D7; COMMON
            0x00D8,   // 00D8..00F6; LATIN
            0x00F7,   // 00F7..00F7; COMMON
            0x00F8,   // 00F8..02B8; LATIN
            0x02B9,   // 02B9..02DF; COMMON
            0x02E0,   // 02E0..02E4; LATIN
            0x02E5,   // 02E5..02E9; COMMON
            0x02EA,   // 02EA..02EB; BOPOMOFO
            0x02EC,   // 02EC..02FF; COMMON
            0x0300,   // 0300..036F; INHERITED
            0x0370,   // 0370..0373; GREEK
            0x0374,   // 0374..0374; COMMON
            0x0375,   // 0375..037D; GREEK
            0x037E,   // 037E..0383; COMMON
            0x0384,   // 0384..0384; GREEK
            0x0385,   // 0385..0385; COMMON
            0x0386,   // 0386..0386; GREEK
            0x0387,   // 0387..0387; COMMON
            0x0388,   // 0388..03E1; GREEK
            0x03E2,   // 03E2..03EF; COPTIC
            0x03F0,   // 03F0..03FF; GREEK
            0x0400,   // 0400..0484; CYRILLIC
            0x0485,   // 0485..0486; INHERITED
            0x0487,   // 0487..0530; CYRILLIC
            0x0531,   // 0531..0588; ARMENIAN
            0x0589,   // 0589..0589; COMMON
            0x058A,   // 058A..0590; ARMENIAN
            0x0591,   // 0591..05FF; HEBREW
            0x0600,   // 0600..060B; ARABIC
            0x060C,   // 060C..060C; COMMON
            0x060D,   // 060D..061A; ARABIC
            0x061B,   // 061B..061D; COMMON
            0x061E,   // 061E..061E; ARABIC
            0x061F,   // 061F..061F; COMMON
            0x0620,   // 0620..063F; ARABIC
            0x0640,   // 0640..0640; COMMON
            0x0641,   // 0641..064A; ARABIC
            0x064B,   // 064B..0655; INHERITED
            0x0656,   // 0656..065E; ARABIC
            0x065F,   // 065F..065F; INHERITED
            0x0660,   // 0660..0669; COMMON
            0x066A,   // 066A..066F; ARABIC
            0x0670,   // 0670..0670; INHERITED
            0x0671,   // 0671..06DC; ARABIC
            0x06DD,   // 06DD..06DD; COMMON
            0x06DE,   // 06DE..06FF; ARABIC
            0x0700,   // 0700..074F; SYRIAC
            0x0750,   // 0750..077F; ARABIC
            0x0780,   // 0780..07BF; THAANA
            0x07C0,   // 07C0..07FF; NKO
            0x0800,   // 0800..083F; SAMARITAN
            0x0840,   // 0840..08FF; MANDAIC
            0x0900,   // 0900..0950; DEVANAGARI
            0x0951,   // 0951..0952; INHERITED
            0x0953,   // 0953..0963; DEVANAGARI
            0x0964,   // 0964..0965; COMMON
            0x0966,   // 0966..096F; DEVANAGARI
            0x0970,   // 0970..0970; COMMON
            0x0971,   // 0971..0980; DEVANAGARI
            0x0981,   // 0981..0A00; BENGALI
            0x0A01,   // 0A01..0A80; GURMUKHI
            0x0A81,   // 0A81..0B00; GUJARATI
            0x0B01,   // 0B01..0B81; ORIYA
            0x0B82,   // 0B82..0C00; TAMIL
            0x0C01,   // 0C01..0C81; TELUGU
            0x0C82,   // 0C82..0CF0; KANNADA
            0x0D02,   // 0D02..0D81; MALAYALAM
            0x0D82,   // 0D82..0E00; SINHALA
            0x0E01,   // 0E01..0E3E; THAI
            0x0E3F,   // 0E3F..0E3F; COMMON
            0x0E40,   // 0E40..0E80; THAI
            0x0E81,   // 0E81..0EFF; LAO
            0x0F00,   // 0F00..0FD4; TIBETAN
            0x0FD5,   // 0FD5..0FD8; COMMON
            0x0FD9,   // 0FD9..0FFF; TIBETAN
            0x1000,   // 1000..109F; MYANMAR
            0x10A0,   // 10A0..10FA; GEORGIAN
            0x10FB,   // 10FB..10FB; COMMON
            0x10FC,   // 10FC..10FF; GEORGIAN
            0x1100,   // 1100..11FF; HANGUL
            0x1200,   // 1200..139F; ETHIOPIC
            0x13A0,   // 13A0..13FF; CHEROKEE
            0x1400,   // 1400..167F; CANADIAN_ABORIGINAL
            0x1680,   // 1680..169F; OGHAM
            0x16A0,   // 16A0..16EA; RUNIC
            0x16EB,   // 16EB..16ED; COMMON
            0x16EE,   // 16EE..16FF; RUNIC
            0x1700,   // 1700..171F; TAGALOG
            0x1720,   // 1720..1734; HANUNOO
            0x1735,   // 1735..173F; COMMON
            0x1740,   // 1740..175F; BUHID
            0x1760,   // 1760..177F; TAGBANWA
            0x1780,   // 1780..17FF; KHMER
            0x1800,   // 1800..1801; MONGOLIAN
            0x1802,   // 1802..1803; COMMON
            0x1804,   // 1804..1804; MONGOLIAN
            0x1805,   // 1805..1805; COMMON
            0x1806,   // 1806..18AF; MONGOLIAN
            0x18B0,   // 18B0..18FF; CANADIAN_ABORIGINAL
            0x1900,   // 1900..194F; LIMBU
            0x1950,   // 1950..197F; TAI_LE
            0x1980,   // 1980..19DF; NEW_TAI_LUE
            0x19E0,   // 19E0..19FF; KHMER
            0x1A00,   // 1A00..1A1F; BUGINESE
            0x1A20,   // 1A20..1AFF; TAI_THAM
            0x1B00,   // 1B00..1B7F; BALINESE
            0x1B80,   // 1B80..1BBF; SUNDANESE
            0x1BC0,   // 1BC0..1BFF; BATAK
            0x1C00,   // 1C00..1C4F; LEPCHA
            0x1C50,   // 1C50..1CCF; OL_CHIKI
            0x1CD0,   // 1CD0..1CD2; INHERITED
            0x1CD3,   // 1CD3..1CD3; COMMON
            0x1CD4,   // 1CD4..1CE0; INHERITED
            0x1CE1,   // 1CE1..1CE1; COMMON
            0x1CE2,   // 1CE2..1CE8; INHERITED
            0x1CE9,   // 1CE9..1CEC; COMMON
            0x1CED,   // 1CED..1CED; INHERITED
            0x1CEE,   // 1CEE..1CFF; COMMON
            0x1D00,   // 1D00..1D25; LATIN
            0x1D26,   // 1D26..1D2A; GREEK
            0x1D2B,   // 1D2B..1D2B; CYRILLIC
            0x1D2C,   // 1D2C..1D5C; LATIN
            0x1D5D,   // 1D5D..1D61; GREEK
            0x1D62,   // 1D62..1D65; LATIN
            0x1D66,   // 1D66..1D6A; GREEK
            0x1D6B,   // 1D6B..1D77; LATIN
            0x1D78,   // 1D78..1D78; CYRILLIC
            0x1D79,   // 1D79..1DBE; LATIN
            0x1DBF,   // 1DBF..1DBF; GREEK
            0x1DC0,   // 1DC0..1DFF; INHERITED
            0x1E00,   // 1E00..1EFF; LATIN
            0x1F00,   // 1F00..1FFF; GREEK
            0x2000,   // 2000..200B; COMMON
            0x200C,   // 200C..200D; INHERITED
            0x200E,   // 200E..2070; COMMON
            0x2071,   // 2071..2073; LATIN
            0x2074,   // 2074..207E; COMMON
            0x207F,   // 207F..207F; LATIN
            0x2080,   // 2080..208F; COMMON
            0x2090,   // 2090..209F; LATIN
            0x20A0,   // 20A0..20CF; COMMON
            0x20D0,   // 20D0..20FF; INHERITED
            0x2100,   // 2100..2125; COMMON
            0x2126,   // 2126..2126; GREEK
            0x2127,   // 2127..2129; COMMON
            0x212A,   // 212A..212B; LATIN
            0x212C,   // 212C..2131; COMMON
            0x2132,   // 2132..2132; LATIN
            0x2133,   // 2133..214D; COMMON
            0x214E,   // 214E..214E; LATIN
            0x214F,   // 214F..215F; COMMON
            0x2160,   // 2160..2188; LATIN
            0x2189,   // 2189..27FF; COMMON
            0x2800,   // 2800..28FF; BRAILLE
            0x2900,   // 2900..2BFF; COMMON
            0x2C00,   // 2C00..2C5F; GLAGOLITIC
            0x2C60,   // 2C60..2C7F; LATIN
            0x2C80,   // 2C80..2CFF; COPTIC
            0x2D00,   // 2D00..2D2F; GEORGIAN
            0x2D30,   // 2D30..2D7F; TIFINAGH
            0x2D80,   // 2D80..2DDF; ETHIOPIC
            0x2DE0,   // 2DE0..2DFF; CYRILLIC
            0x2E00,   // 2E00..2E7F; COMMON
            0x2E80,   // 2E80..2FEF; HAN
            0x2FF0,   // 2FF0..3004; COMMON
            0x3005,   // 3005..3005; HAN
            0x3006,   // 3006..3006; COMMON
            0x3007,   // 3007..3007; HAN
            0x3008,   // 3008..3020; COMMON
            0x3021,   // 3021..3029; HAN
            0x302A,   // 302A..302D; INHERITED
            0x302E,   // 302E..302F; HANGUL
            0x3030,   // 3030..3037; COMMON
            0x3038,   // 3038..303B; HAN
            0x303C,   // 303C..3040; COMMON
            0x3041,   // 3041..3098; HIRAGANA
            0x3099,   // 3099..309A; INHERITED
            0x309B,   // 309B..309C; COMMON
            0x309D,   // 309D..309F; HIRAGANA
            0x30A0,   // 30A0..30A0; COMMON
            0x30A1,   // 30A1..30FA; KATAKANA
            0x30FB,   // 30FB..30FC; COMMON
            0x30FD,   // 30FD..3104; KATAKANA
            0x3105,   // 3105..3130; BOPOMOFO
            0x3131,   // 3131..318F; HANGUL
            0x3190,   // 3190..319F; COMMON
            0x31A0,   // 31A0..31BF; BOPOMOFO
            0x31C0,   // 31C0..31EF; COMMON
            0x31F0,   // 31F0..31FF; KATAKANA
            0x3200,   // 3200..321F; HANGUL
            0x3220,   // 3220..325F; COMMON
            0x3260,   // 3260..327E; HANGUL
            0x327F,   // 327F..32CF; COMMON
            0x32D0,   // 32D0..3357; KATAKANA
            0x3358,   // 3358..33FF; COMMON
            0x3400,   // 3400..4DBF; HAN
            0x4DC0,   // 4DC0..4DFF; COMMON
            0x4E00,   // 4E00..9FFF; HAN
            0xA000,   // A000..A4CF; YI
            0xA4D0,   // A4D0..A4FF; LISU
            0xA500,   // A500..A63F; VAI
            0xA640,   // A640..A69F; CYRILLIC
            0xA6A0,   // A6A0..A6FF; BAMUM
            0xA700,   // A700..A721; COMMON
            0xA722,   // A722..A787; LATIN
            0xA788,   // A788..A78A; COMMON
            0xA78B,   // A78B..A7FF; LATIN
            0xA800,   // A800..A82F; SYLOTI_NAGRI
            0xA830,   // A830..A83F; COMMON
            0xA840,   // A840..A87F; PHAGS_PA
            0xA880,   // A880..A8DF; SAURASHTRA
            0xA8E0,   // A8E0..A8FF; DEVANAGARI
            0xA900,   // A900..A92F; KAYAH_LI
            0xA930,   // A930..A95F; REJANG
            0xA960,   // A960..A97F; HANGUL
            0xA980,   // A980..A9FF; JAVANESE
            0xAA00,   // AA00..AA5F; CHAM
            0xAA60,   // AA60..AA7F; MYANMAR
            0xAA80,   // AA80..AB00; TAI_VIET
            0xAB01,   // AB01..ABBF; ETHIOPIC
            0xABC0,   // ABC0..ABFF; MEETEI_MAYEK
            0xAC00,   // AC00..D7FB; HANGUL
            0xD7FC,   // D7FC..F8FF; UNKNOWN
            0xF900,   // F900..FAFF; HAN
            0xFB00,   // FB00..FB12; LATIN
            0xFB13,   // FB13..FB1C; ARMENIAN
            0xFB1D,   // FB1D..FB4F; HEBREW
            0xFB50,   // FB50..FD3D; ARABIC
            0xFD3E,   // FD3E..FD4F; COMMON
            0xFD50,   // FD50..FDFC; ARABIC
            0xFDFD,   // FDFD..FDFF; COMMON
            0xFE00,   // FE00..FE0F; INHERITED
            0xFE10,   // FE10..FE1F; COMMON
            0xFE20,   // FE20..FE2F; INHERITED
            0xFE30,   // FE30..FE6F; COMMON
            0xFE70,   // FE70..FEFE; ARABIC
            0xFEFF,   // FEFF..FF20; COMMON
            0xFF21,   // FF21..FF3A; LATIN
            0xFF3B,   // FF3B..FF40; COMMON
            0xFF41,   // FF41..FF5A; LATIN
            0xFF5B,   // FF5B..FF65; COMMON
            0xFF66,   // FF66..FF6F; KATAKANA
            0xFF70,   // FF70..FF70; COMMON
            0xFF71,   // FF71..FF9D; KATAKANA
            0xFF9E,   // FF9E..FF9F; COMMON
            0xFFA0,   // FFA0..FFDF; HANGUL
            0xFFE0,   // FFE0..FFFF; COMMON
            0x10000,  // 10000..100FF; LINEAR_B
            0x10100,  // 10100..1013F; COMMON
            0x10140,  // 10140..1018F; GREEK
            0x10190,  // 10190..101FC; COMMON
            0x101FD,  // 101FD..1027F; INHERITED
            0x10280,  // 10280..1029F; LYCIAN
            0x102A0,  // 102A0..102FF; CARIAN
            0x10300,  // 10300..1032F; OLD_ITALIC
            0x10330,  // 10330..1037F; GOTHIC
            0x10380,  // 10380..1039F; UGARITIC
            0x103A0,  // 103A0..103FF; OLD_PERSIAN
            0x10400,  // 10400..1044F; DESERET
            0x10450,  // 10450..1047F; SHAVIAN
            0x10480,  // 10480..107FF; OSMANYA
            0x10800,  // 10800..1083F; CYPRIOT
            0x10840,  // 10840..108FF; IMPERIAL_ARAMAIC
            0x10900,  // 10900..1091F; PHOENICIAN
            0x10920,  // 10920..109FF; LYDIAN
            0x10A00,  // 10A00..10A5F; KHAROSHTHI
            0x10A60,  // 10A60..10AFF; OLD_SOUTH_ARABIAN
            0x10B00,  // 10B00..10B3F; AVESTAN
            0x10B40,  // 10B40..10B5F; INSCRIPTIONAL_PARTHIAN
            0x10B60,  // 10B60..10BFF; INSCRIPTIONAL_PAHLAVI
            0x10C00,  // 10C00..10E5F; OLD_TURKIC
            0x10E60,  // 10E60..10FFF; ARABIC
            0x11000,  // 11000..1107F; BRAHMI
            0x11080,  // 11080..11FFF; KAITHI
            0x12000,  // 12000..12FFF; CUNEIFORM
            0x13000,  // 13000..167FF; EGYPTIAN_HIEROGLYPHS
            0x16800,  // 16800..16A38; BAMUM
            0x1B000,  // 1B000..1B000; KATAKANA
            0x1B001,  // 1B001..1CFFF; HIRAGANA
            0x1D000,  // 1D000..1D166; COMMON
            0x1D167,  // 1D167..1D169; INHERITED
            0x1D16A,  // 1D16A..1D17A; COMMON
            0x1D17B,  // 1D17B..1D182; INHERITED
            0x1D183,  // 1D183..1D184; COMMON
            0x1D185,  // 1D185..1D18B; INHERITED
            0x1D18C,  // 1D18C..1D1A9; COMMON
            0x1D1AA,  // 1D1AA..1D1AD; INHERITED
            0x1D1AE,  // 1D1AE..1D1FF; COMMON
            0x1D200,  // 1D200..1D2FF; GREEK
            0x1D300,  // 1D300..1F1FF; COMMON
            0x1F200,  // 1F200..1F200; HIRAGANA
            0x1F201,  // 1F210..1FFFF; COMMON
            0x20000,  // 20000..E0000; HAN
            0xE0001,  // E0001..E00FF; COMMON
            0xE0100,  // E0100..E01EF; INHERITED
            0xE01F0   // E01F0..10FFFF; UNKNOWN

        };

        private static final UnicodeScript[] scripts = {
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            BOPOMOFO,
            COMMON,
            INHERITED,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COMMON,
            GREEK,
            COPTIC,
            GREEK,
            CYRILLIC,
            INHERITED,
            CYRILLIC,
            ARMENIAN,
            COMMON,
            ARMENIAN,
            HEBREW,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            ARABIC,
            INHERITED,
            ARABIC,
            INHERITED,
            COMMON,
            ARABIC,
            INHERITED,
            ARABIC,
            COMMON,
            ARABIC,
            SYRIAC,
            ARABIC,
            THAANA,
            NKO,
            SAMARITAN,
            MANDAIC,
            DEVANAGARI,
            INHERITED,
            DEVANAGARI,
            COMMON,
            DEVANAGARI,
            COMMON,
            DEVANAGARI,
            BENGALI,
            GURMUKHI,
            GUJARATI,
            ORIYA,
            TAMIL,
            TELUGU,
            KANNADA,
            MALAYALAM,
            SINHALA,
            THAI,
            COMMON,
            THAI,
            LAO,
            TIBETAN,
            COMMON,
            TIBETAN,
            MYANMAR,
            GEORGIAN,
            COMMON,
            GEORGIAN,
            HANGUL,
            ETHIOPIC,
            CHEROKEE,
            CANADIAN_ABORIGINAL,
            OGHAM,
            RUNIC,
            COMMON,
            RUNIC,
            TAGALOG,
            HANUNOO,
            COMMON,
            BUHID,
            TAGBANWA,
            KHMER,
            MONGOLIAN,
            COMMON,
            MONGOLIAN,
            COMMON,
            MONGOLIAN,
            CANADIAN_ABORIGINAL,
            LIMBU,
            TAI_LE,
            NEW_TAI_LUE,
            KHMER,
            BUGINESE,
            TAI_THAM,
            BALINESE,
            SUNDANESE,
            BATAK,
            LEPCHA,
            OL_CHIKI,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            LATIN,
            GREEK,
            CYRILLIC,
            LATIN,
            GREEK,
            LATIN,
            GREEK,
            LATIN,
            CYRILLIC,
            LATIN,
            GREEK,
            INHERITED,
            LATIN,
            GREEK,
            COMMON,
            INHERITED,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            INHERITED,
            COMMON,
            GREEK,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            BRAILLE,
            COMMON,
            GLAGOLITIC,
            LATIN,
            COPTIC,
            GEORGIAN,
            TIFINAGH,
            ETHIOPIC,
            CYRILLIC,
            COMMON,
            HAN,
            COMMON,
            HAN,
            COMMON,
            HAN,
            COMMON,
            HAN,
            INHERITED,
            HANGUL,
            COMMON,
            HAN,
            COMMON,
            HIRAGANA,
            INHERITED,
            COMMON,
            HIRAGANA,
            COMMON,
            KATAKANA,
            COMMON,
            KATAKANA,
            BOPOMOFO,
            HANGUL,
            COMMON,
            BOPOMOFO,
            COMMON,
            KATAKANA,
            HANGUL,
            COMMON,
            HANGUL,
            COMMON,
            KATAKANA,
            COMMON,
            HAN,
            COMMON,
            HAN,
            YI,
            LISU,
            VAI,
            CYRILLIC,
            BAMUM,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            SYLOTI_NAGRI,
            COMMON,
            PHAGS_PA,
            SAURASHTRA,
            DEVANAGARI,
            KAYAH_LI,
            REJANG,
            HANGUL,
            JAVANESE,
            CHAM,
            MYANMAR,
            TAI_VIET,
            ETHIOPIC,
            MEETEI_MAYEK,
            HANGUL,
            UNKNOWN,
            HAN,
            LATIN,
            ARMENIAN,
            HEBREW,
            ARABIC,
            COMMON,
            ARABIC,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            ARABIC,
            COMMON,
            LATIN,
            COMMON,
            LATIN,
            COMMON,
            KATAKANA,
            COMMON,
            KATAKANA,
            COMMON,
            HANGUL,
            COMMON,
            LINEAR_B,
            COMMON,
            GREEK,
            COMMON,
            INHERITED,
            LYCIAN,
            CARIAN,
            OLD_ITALIC,
            GOTHIC,
            UGARITIC,
            OLD_PERSIAN,
            DESERET,
            SHAVIAN,
            OSMANYA,
            CYPRIOT,
            IMPERIAL_ARAMAIC,
            PHOENICIAN,
            LYDIAN,
            KHAROSHTHI,
            OLD_SOUTH_ARABIAN,
            AVESTAN,
            INSCRIPTIONAL_PARTHIAN,
            INSCRIPTIONAL_PAHLAVI,
            OLD_TURKIC,
            ARABIC,
            BRAHMI,
            KAITHI,
            CUNEIFORM,
            EGYPTIAN_HIEROGLYPHS,
            BAMUM,
            KATAKANA,
            HIRAGANA,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            INHERITED,
            COMMON,
            GREEK,
            COMMON,
            HIRAGANA,
            COMMON,
            HAN,
            COMMON,
            INHERITED,
            UNKNOWN
        };

        private static HashMap<String, ICharacter.UnicodeScript> aliases;
        static {
            aliases = new HashMap(128);
            aliases.put("ARAB", ARABIC);
            aliases.put("ARMI", IMPERIAL_ARAMAIC);
            aliases.put("ARMN", ARMENIAN);
            aliases.put("AVST", AVESTAN);
            aliases.put("BALI", BALINESE);
            aliases.put("BAMU", BAMUM);
            aliases.put("BATK", BATAK);
            aliases.put("BENG", BENGALI);
            aliases.put("BOPO", BOPOMOFO);
            aliases.put("BRAI", BRAILLE);
            aliases.put("BRAH", BRAHMI);
            aliases.put("BUGI", BUGINESE);
            aliases.put("BUHD", BUHID);
            aliases.put("CANS", CANADIAN_ABORIGINAL);
            aliases.put("CARI", CARIAN);
            aliases.put("CHAM", CHAM);
            aliases.put("CHER", CHEROKEE);
            aliases.put("COPT", COPTIC);
            aliases.put("CPRT", CYPRIOT);
            aliases.put("CYRL", CYRILLIC);
            aliases.put("DEVA", DEVANAGARI);
            aliases.put("DSRT", DESERET);
            aliases.put("EGYP", EGYPTIAN_HIEROGLYPHS);
            aliases.put("ETHI", ETHIOPIC);
            aliases.put("GEOR", GEORGIAN);
            aliases.put("GLAG", GLAGOLITIC);
            aliases.put("GOTH", GOTHIC);
            aliases.put("GREK", GREEK);
            aliases.put("GUJR", GUJARATI);
            aliases.put("GURU", GURMUKHI);
            aliases.put("HANG", HANGUL);
            aliases.put("HANI", HAN);
            aliases.put("HANO", HANUNOO);
            aliases.put("HEBR", HEBREW);
            aliases.put("HIRA", HIRAGANA);
            // it appears we don't have the KATAKANA_OR_HIRAGANA
            //aliases.put("HRKT", KATAKANA_OR_HIRAGANA);
            aliases.put("ITAL", OLD_ITALIC);
            aliases.put("JAVA", JAVANESE);
            aliases.put("KALI", KAYAH_LI);
            aliases.put("KANA", KATAKANA);
            aliases.put("KHAR", KHAROSHTHI);
            aliases.put("KHMR", KHMER);
            aliases.put("KNDA", KANNADA);
            aliases.put("KTHI", KAITHI);
            aliases.put("LANA", TAI_THAM);
            aliases.put("LAOO", LAO);
            aliases.put("LATN", LATIN);
            aliases.put("LEPC", LEPCHA);
            aliases.put("LIMB", LIMBU);
            aliases.put("LINB", LINEAR_B);
            aliases.put("LISU", LISU);
            aliases.put("LYCI", LYCIAN);
            aliases.put("LYDI", LYDIAN);
            aliases.put("MAND", MANDAIC);
            aliases.put("MLYM", MALAYALAM);
            aliases.put("MONG", MONGOLIAN);
            aliases.put("MTEI", MEETEI_MAYEK);
            aliases.put("MYMR", MYANMAR);
            aliases.put("NKOO", NKO);
            aliases.put("OGAM", OGHAM);
            aliases.put("OLCK", OL_CHIKI);
            aliases.put("ORKH", OLD_TURKIC);
            aliases.put("ORYA", ORIYA);
            aliases.put("OSMA", OSMANYA);
            aliases.put("PHAG", PHAGS_PA);
            aliases.put("PHLI", INSCRIPTIONAL_PAHLAVI);
            aliases.put("PHNX", PHOENICIAN);
            aliases.put("PRTI", INSCRIPTIONAL_PARTHIAN);
            aliases.put("RJNG", REJANG);
            aliases.put("RUNR", RUNIC);
            aliases.put("SAMR", SAMARITAN);
            aliases.put("SARB", OLD_SOUTH_ARABIAN);
            aliases.put("SAUR", SAURASHTRA);
            aliases.put("SHAW", SHAVIAN);
            aliases.put("SINH", SINHALA);
            aliases.put("SUND", SUNDANESE);
            aliases.put("SYLO", SYLOTI_NAGRI);
            aliases.put("SYRC", SYRIAC);
            aliases.put("TAGB", TAGBANWA);
            aliases.put("TALE", TAI_LE);
            aliases.put("TALU", NEW_TAI_LUE);
            aliases.put("TAML", TAMIL);
            aliases.put("TAVT", TAI_VIET);
            aliases.put("TELU", TELUGU);
            aliases.put("TFNG", TIFINAGH);
            aliases.put("TGLG", TAGALOG);
            aliases.put("THAA", THAANA);
            aliases.put("THAI", THAI);
            aliases.put("TIBT", TIBETAN);
            aliases.put("UGAR", UGARITIC);
            aliases.put("VAII", VAI);
            aliases.put("XPEO", OLD_PERSIAN);
            aliases.put("XSUX", CUNEIFORM);
            aliases.put("YIII", YI);
            aliases.put("ZINH", INHERITED);
            aliases.put("ZYYY", COMMON);
            aliases.put("ZZZZ", UNKNOWN);
        }

        /**
         * Returns the enum constant representing the Unicode script of which
         * the given character (Unicode code point) is assigned to.
         *
         * @param   codePoint the character (Unicode code point) in question.
         * @return  The {@code UnicodeScript} constant representing the
         *          Unicode script of which this character is assigned to.
         *
         * @exception IllegalArgumentException if the specified
         * {@code codePoint} is an invalid Unicode code point.
         * @see Character#isValidCodePoint(int)
         *
         */
        public static UnicodeScript of(int codePoint) {
            if (!Character.isValidCodePoint(codePoint))
                throw new IllegalArgumentException();
            int type = Character.getType(codePoint);
            // leave SURROGATE and PRIVATE_USE for table lookup
            if (type == Character.UNASSIGNED)
                return UNKNOWN;
            int index = Arrays.binarySearch(scriptStarts, codePoint);
            if (index < 0)
                index = -index - 2;
            return scripts[index];
        }

        /**
         * Returns the UnicodeScript constant with the given Unicode script
         * name or the script name alias. Script names and their aliases are
         * determined by The Unicode Standard. The files Scripts&lt;version&gt;.txt
         * and PropertyValueAliases&lt;version&gt;.txt define script names
         * and the script name aliases for a particular version of the
         * standard. The {@link Character} class specifies the version of
         * the standard that it supports.
         * <p>
         * Character case is ignored for all of the valid script names.
         * The en_US locale's case mapping rules are used to provide
         * case-insensitive string comparisons for script name validation.
         * <p>
         *
         * @param scriptName A {@code UnicodeScript} name.
         * @return The {@code UnicodeScript} constant identified
         *         by {@code scriptName}
         * @throws IllegalArgumentException if {@code scriptName} is an
         *         invalid name
         * @throws NullPointerException if {@code scriptName} is null
         */
        public static final UnicodeScript forName(String scriptName) {
            scriptName = scriptName.toUpperCase(Locale.ENGLISH);
                                 //.replace(' ', '_'));
            UnicodeScript sc = aliases.get(scriptName);
            if (sc != null)
                return sc;
            return valueOf(scriptName);
        }
    }
}
