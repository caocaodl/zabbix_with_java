package org.apache.commons.el;

import java.io.IOException;
import java.io.Writer;

/**
 * Converts Strings so that they can be used within HTML-Code.
 * Encodes the given string, so that it can be used within a html page.
 * 
 * @param string
 *            the string to convert
 * @param encodeNewline
 *            if true newline characters are converted to &lt;br&gt;'s
 * @param encodeSubsequentBlanksToNbsp
 *            if true subsequent blanks are converted to &amp;nbsp;'s
 */
public abstract class HTMLEncoder {

    public static String encode(String string, boolean encodeNewline,
            boolean encodeSubsequentBlanksToNbsp) {
        if (string == null) {
            return "";
        }

        StringBuffer sb = null; // create later on demand
        String app;
        char c;
        for (int i = 0; i < string.length(); ++i) {
            app = null;
            c = string.charAt(i);
            switch (c) {
            case '"':
                app = "&quot;";
                break; // "
            case '&':
                app = "&amp;";
                break; // &
            case '<':
                app = "&lt;";
                break; // <
            case '>':
                app = "&gt;";
                break; // >
            case ' ':
                if (encodeSubsequentBlanksToNbsp
                        && (i == 0 || (i - 1 >= 0 && string.charAt(i - 1) == ' '))) {
                    // Space at beginning or after another space
                    app = "&#160;";
                }
                break;
            case '\n':
                if (encodeNewline) {
                    app = "<br/>";
                }
                break;

            // german umlauts
            case '\u00E4':
                app = "&auml;";
                break;
            case '\u00C4':
                app = "&Auml;";
                break;
            case '\u00F6':
                app = "&ouml;";
                break;
            case '\u00D6':
                app = "&Ouml;";
                break;
            case '\u00FC':
                app = "&uuml;";
                break;
            case '\u00DC':
                app = "&Uuml;";
                break;
            case '\u00DF':
                app = "&szlig;";
                break;

            // misc
            // case 0x80: app = "&euro;"; break; sometimes euro symbol is ascii
            // 128, should we suport it?
            case '\u20AC':
                app = "&euro;";
                break;
            case '\u00AB':
                app = "&laquo;";
                break;
            case '\u00BB':
                app = "&raquo;";
                break;
            case '\u00A0':
                app = "&#160;";
                break;

            default:
                if (((int) c) >= 0x80) {
                    // encode all non basic latin characters
                    app = "&#" + ((int) c) + ";";
                }
                break;
            }
            if (app != null) {
                if (sb == null) {
                    sb = new StringBuffer(string.substring(0, i));
                }
                sb.append(app);
            } else {
                if (sb != null) {
                    sb.append(c);
                }
            }
        }

        if (sb == null) {
            return string;
        } else {
            return sb.toString();
        }
    }
    
    public static String encode(char cbuf[], int off, int len, boolean encodeNewline,
            boolean encodeSubsequentBlanksToNbsp) {
        if (cbuf == null) {
            throw new NullPointerException("cbuf name must not be null");
        }
        if (cbuf.length < off + len) {
            throw new IndexOutOfBoundsException((off + len) + " > " + cbuf.length);
        }

        StringBuffer sb = null; // create later on demand
        String app;
        char c;
        for (int i = off; i < len; ++i) {
            app = null;
            c = cbuf[i];
            switch (c) {
            case '"':
                app = "&quot;";
                break; // "
            case '&':
                app = "&amp;";
                break; // &
            case '<':
                app = "&lt;";
                break; // <
            case '>':
                app = "&gt;";
                break; // >
            case ' ':
                if (encodeSubsequentBlanksToNbsp
                        && (i == 0 || (i - 1 >= 0 && cbuf[i - 1] == ' '))) {
                    // Space at beginning or after another space
                    app = "&#160;";
                }
                break;
            case '\n':
                if (encodeNewline) {
                    app = "<br/>";
                }
                break;

            // german umlauts
            case '\u00E4':
                app = "&auml;";
                break;
            case '\u00C4':
                app = "&Auml;";
                break;
            case '\u00F6':
                app = "&ouml;";
                break;
            case '\u00D6':
                app = "&Ouml;";
                break;
            case '\u00FC':
                app = "&uuml;";
                break;
            case '\u00DC':
                app = "&Uuml;";
                break;
            case '\u00DF':
                app = "&szlig;";
                break;

            // misc
            // case 0x80: app = "&euro;"; break; sometimes euro symbol is ascii
            // 128, should we suport it?
            case '\u20AC':
                app = "&euro;";
                break;
            case '\u00AB':
                app = "&laquo;";
                break;
            case '\u00BB':
                app = "&raquo;";
                break;
            case '\u00A0':
                app = "&#160;";
                break;

            default:
                if (((int) c) >= 0x80) {
                    // encode all non basic latin characters
                    app = "&#" + ((int) c) + ";";
                }
                break;
            }
            if (app != null) {
                if (sb == null) {
                    sb = new StringBuffer();
                    sb.append(cbuf, off, i);
                }
                sb.append(app);
            } else {
                if (sb != null) {
                    sb.append(c);
                }
            }
        }

        if (sb == null) {
            return new String(cbuf, off, len);
        } else {
            return sb.toString();
        }
    }
    
    public static void encode(Writer writer, String string, boolean encodeNewline,
            boolean encodeSubsequentBlanksToNbsp) throws IOException {
        if (string == null) {
            return;
        }

        boolean isCommited = false;
        String app;
        char c;
        for (int i = 0; i < string.length(); ++i) {
            app = null;
            c = string.charAt(i);
            switch (c) {
            case '"':
                app = "&quot;";
                break; // "
            case '&':
                app = "&amp;";
                break; // &
            case '<':
                app = "&lt;";
                break; // <
            case '>':
                app = "&gt;";
                break; // >
            case ' ':
                if (encodeSubsequentBlanksToNbsp
                        && (i == 0 || (i - 1 >= 0 && string.charAt(i - 1) == ' '))) {
                    // Space at beginning or after another space
                    app = "&#160;";
                }
                break;
            case '\n':
                if (encodeNewline) {
                    app = "<br/>";
                }
                break;

            // german umlauts
            case '\u00E4':
                app = "&auml;";
                break;
            case '\u00C4':
                app = "&Auml;";
                break;
            case '\u00F6':
                app = "&ouml;";
                break;
            case '\u00D6':
                app = "&Ouml;";
                break;
            case '\u00FC':
                app = "&uuml;";
                break;
            case '\u00DC':
                app = "&Uuml;";
                break;
            case '\u00DF':
                app = "&szlig;";
                break;

            // misc
            // case 0x80: app = "&euro;"; break; sometimes euro symbol is ascii
            // 128, should we suport it?
            case '\u20AC':
                app = "&euro;";
                break;
            case '\u00AB':
                app = "&laquo;";
                break;
            case '\u00BB':
                app = "&raquo;";
                break;
            case '\u00A0':
                app = "&#160;";
                break;

            default:
                if (((int) c) >= 0x80) {
                    // encode all non basic latin characters
                    app = "&#" + ((int) c) + ";";
                }
                break;
            }
            if (app != null) {
                if (!isCommited) {
                    writer.write(string.substring(0, i));
                }
                writer.write(app);
                isCommited = true;
            } else {
                if (isCommited) {
                    writer.write(c);
                }
            }
        }

        if (!isCommited) {
            writer.write(string);
        }
    }
    
    public static void encode(Writer writer, char cbuf[], int off, int len, boolean encodeNewline,
            boolean encodeSubsequentBlanksToNbsp) throws IOException {
        if (cbuf == null) {
            throw new NullPointerException("cbuf name must not be null");
        }
        if (cbuf.length < off + len) {
            throw new IndexOutOfBoundsException((off + len) + " > " + cbuf.length);
        }
        
        boolean isCommited = false;
        String app;
        char c;
        for (int i = off; i < len; ++i) {
            app = null;
            c = cbuf[i];
            switch (c) {
            case '"':
                app = "&quot;";
                break; // "
            case '&':
                app = "&amp;";
                break; // &
            case '<':
                app = "&lt;";
                break; // <
            case '>':
                app = "&gt;";
                break; // >
            case ' ':
                if (encodeSubsequentBlanksToNbsp
                        && (i == 0 || (i - 1 >= 0 && cbuf[i - 1] == ' '))) {
                    // Space at beginning or after another space
                    app = "&#160;";
                }
                break;
            case '\n':
                if (encodeNewline) {
                    app = "<br/>";
                }
                break;

            // german umlauts
            case '\u00E4':
                app = "&auml;";
                break;
            case '\u00C4':
                app = "&Auml;";
                break;
            case '\u00F6':
                app = "&ouml;";
                break;
            case '\u00D6':
                app = "&Ouml;";
                break;
            case '\u00FC':
                app = "&uuml;";
                break;
            case '\u00DC':
                app = "&Uuml;";
                break;
            case '\u00DF':
                app = "&szlig;";
                break;

            // misc
            // case 0x80: app = "&euro;"; break; sometimes euro symbol is ascii
            // 128, should we suport it?
            case '\u20AC':
                app = "&euro;";
                break;
            case '\u00AB':
                app = "&laquo;";
                break;
            case '\u00BB':
                app = "&raquo;";
                break;
            case '\u00A0':
                app = "&#160;";
                break;

            default:
                if (((int) c) >= 0x80) {
                    // encode all non basic latin characters
                    app = "&#" + ((int) c) + ";";
                }
                break;
            }
            if (app != null) {
                if (!isCommited) {
                    writer.write(cbuf, off, i);
                }
                writer.write(app);
                isCommited = true;
            } else {
                if (isCommited) {
                    writer.write(c);
                }
            }
        }

        if (!isCommited) {
            writer.write(cbuf, off, len);
        }
    }

    
    /**
     * <p>Description: ��BBCode ������Ӧת��</p>
     * ����ʱ�� : Mar 20, 2009-2:55:30 PM
     * @author BluE
     * @version $Revision: 1.0 $
     *
     * ����˵����
     * @param bbcode    BB��
     * @param isPDF        true: PDF,   false: HTML
     * @return
     */
    public static String encodeBBCode(String bbcode, boolean isPDF, boolean isCmBill) {
        if (bbcode == null || bbcode.length()==0){
            return "";
        }        
        
        bbcode = bbcode.replaceAll("\\[([bu])\\]\\[\\/\\1\\]", "");
        
        if(isPDF) {
            bbcode = bbcode.replaceAll("&nbsp;", String.valueOf((char)0xA0));
            bbcode = bbcode.replaceAll("\\[br\\/\\]", "\n[br/]\n");
            
            bbcode = bbcode.replaceAll("\\[size\\=([^\\]]+)\\]", "\n[size=$1]\n");
            bbcode = bbcode.replaceAll("\\[\\/size\\]", "\n[/size]\n");
            
            bbcode = bbcode.replaceAll("\\[b\\]", "\n[b]\n");
            bbcode = bbcode.replaceAll("\\[\\/b\\]", "\n[/b]\n");
            
            bbcode = bbcode.replaceAll("\\[u\\]", "\n[u]\n");
            bbcode = bbcode.replaceAll("\\[\\/u\\]", "\n[/u]\n");
        }else {
            // Convert < and > to their HTML entities.
            bbcode = bbcode.replaceAll("<", "&lt;");
            bbcode = bbcode.replaceAll(">", "&gt;");
            
            // Convert line breaks to <br>.
            bbcode = bbcode.replaceAll("\\[br\\/\\]", "<br>");
            
            //[size]
            bbcode = bbcode.replaceAll("\\[size\\=([^\\]]+)\\]", "<font style='font-size:$1'>");
            bbcode = bbcode.replaceAll("\\[\\/size\\]", "</font>");

            // [b]
            bbcode = bbcode.replaceAll("\\[b\\]", "<b>");
            bbcode = bbcode.replaceAll("\\[\\/b\\]", "</b>");

            // [u]
            bbcode = bbcode.replaceAll("\\[u\\]", "<u>");
            bbcode = bbcode.replaceAll("\\[\\/u\\]", "</u>"); 
        } 
        
        return bbcode;
    }
}
