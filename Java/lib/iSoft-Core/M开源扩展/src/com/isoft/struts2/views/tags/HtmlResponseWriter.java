package com.isoft.struts2.views.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.el.HTMLEncoder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HtmlResponseWriter extends Writer {
    private static final Log log = LogFactory.getLog(HtmlResponseWriter.class);

    private Writer _writer;
    private String _startElementName;
    private Boolean _isScript;
    private Boolean _isStyle;
    private Boolean _isTextArea;
    private boolean _startTagOpen;

    private static final Set<String> s_emptyHtmlElements = new HashSet<String>();

    static {
        s_emptyHtmlElements.add("area");
        s_emptyHtmlElements.add("br");
        s_emptyHtmlElements.add("base");
        s_emptyHtmlElements.add("basefont");
        s_emptyHtmlElements.add("col");
        s_emptyHtmlElements.add("frame");
        s_emptyHtmlElements.add("hr");
        s_emptyHtmlElements.add("img");
        s_emptyHtmlElements.add("input");
        s_emptyHtmlElements.add("isindex");
        s_emptyHtmlElements.add("link");
        s_emptyHtmlElements.add("meta");
        s_emptyHtmlElements.add("param");
    }

    public HtmlResponseWriter(Writer writer) {
        _writer = writer;
    }

    @Override
    public void flush() throws IOException {
        // API doc says we should not flush the underlying writer
        // _writer.flush();
        // but rather clear any values buffered by this ResponseWriter:
        closeStartTagIfNecessary();
        // _writer.flush();
    }

    public void startElement(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("elementName name must not be null");
        }

        closeStartTagIfNecessary();
        _writer.write('<');
        _writer.write(name);

        resetStartedElement();

        _startElementName = name;
        _startTagOpen = true;
    }

    private void closeStartTagIfNecessary() throws IOException {
        if (_startTagOpen) {
            if (s_emptyHtmlElements.contains(_startElementName.toLowerCase())) {
                _writer.write(" />");
                // make null, this will cause NullPointer in some invalid
                // element nestings
                // (better than doing nothing)
                resetStartedElement();
            } else {
                _writer.write('>');

                if (isScriptOrStyle()) {
                    _writer.write("<!--\n");
                }
            }
            _startTagOpen = false;
        }
    }

    private void resetStartedElement() {
        _startElementName = null;
        _isScript = null;
        _isStyle = null;
        _isTextArea = null;
    }

    public void endElement(String name) throws IOException {
        if (name == null) {
            throw new NullPointerException("elementName name must not be null");
        }

        if (log.isWarnEnabled()) {
            if (_startElementName != null && !name.equals(_startElementName)) {
                if (log.isWarnEnabled())
                    log.warn("HTML nesting warning on closing " + name
                            + ": element " + _startElementName
                            + " not explicitly closed");
            }
        }

        if (_startTagOpen) {

            // we will get here only if no text or attribute was written after
            // the start element was opened
            // now we close out the started tag - if it is an empty tag, this is
            // then fully closed
            closeStartTagIfNecessary();

            // tag was no empty tag - it has no accompanying end tag now.
            if (_startElementName != null) {
                // write closing tag
                writeEndTag(name);
            }
        } else {
            if (s_emptyHtmlElements.contains(name.toLowerCase())) {
                if (log.isWarnEnabled())
                    log
                            .warn("HTML nesting warning on closing "
                                    + name
                                    + ": This element must not contain nested elements or text in HTML");
            } else {
                writeEndTag(name);
            }
        }

        resetStartedElement();
    }

    private void writeEndTag(String name) throws IOException {
        if (isScriptOrStyle()) {
            if (isScript())
                _writer.write("\n//-->");
            else
                _writer.write("\n-->");
        }

        _writer.write("</");
        _writer.write(name);
        _writer.write('>');
    }

    public void writeAttribute(String name, Object value) throws IOException {
        if (name == null) {
            throw new NullPointerException(
                    "attributeName name must not be null");
        }
        if(value == null) {
        	return;
        }
        
        if (!_startTagOpen) {
            throw new IllegalStateException(
                    "Must be called before the start element is closed (attribute '"
                            + name + "')");
        }

        if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue()) {
                _writer.write(' ');
                _writer.write(name);
                _writer.write("=\"");
                _writer.write(name);
                _writer.write('"');
            }
        } else {
            String strValue = value.toString();
            _writer.write(' ');
            _writer.write(name);
            _writer.write("=\"");
            HTMLEncoder.encode(_writer,strValue, false, false);
            _writer.write('"');
        }
    }
    
    public void writeURIAttribute(String name, Object value) throws IOException {
        if (name == null) {
            throw new NullPointerException(
                    "attributeName name must not be null");
        }
        if (!_startTagOpen) {
            throw new IllegalStateException(
                    "Must be called before the start element is closed (attribute '"
                            + name + "')");
        }

        String strValue = value.toString();
        _writer.write(' ');
        _writer.write(name);
        _writer.write("=\"");
        HTMLEncoder.encode(_writer,strValue, false, false);
        _writer.write('"');
    }

    public void writeComment(Object value) throws IOException {
        if (value == null) {
            throw new NullPointerException("comment name must not be null");
        }

        closeStartTagIfNecessary();
        _writer.write("<!--");
        _writer.write(value.toString()); // TODO: Escaping: must not have
        // "-->" inside!
        _writer.write("-->");
    }

    public void writeText(Object value) throws IOException {
        if (value == null) {
            throw new NullPointerException("text name must not be null");
        }

        closeStartTagIfNecessary();

        String strValue = value.toString();

        if (isScriptOrStyle()) {
            _writer.write(strValue);
        } else if (isTextarea()) {
            // For textareas we must *not* map successive spaces to &nbsp or Newlines to <br/>
            HTMLEncoder.encode(_writer, strValue, false, false);
        } else {
            // We map successive spaces to &nbsp; and Newlines to <br/>
            HTMLEncoder.encode(_writer, strValue, true, true);
        }
    }

    public void writeText(char cbuf[], int off, int len) throws IOException {
        if (cbuf == null) {
            throw new NullPointerException("cbuf name must not be null");
        }
        if (cbuf.length < off + len) {
            throw new IndexOutOfBoundsException((off + len) + " > " + cbuf.length);
        }

        closeStartTagIfNecessary();

        if (isScriptOrStyle()) {
            _writer.write(cbuf, off, len);
        } else if (isTextarea()) {
            // For textareas we must *not* map successive spaces to &nbsp or Newlines to <br/>
            HTMLEncoder.encode(_writer, cbuf, off, len, false, false);
        } else {
            // We map successive spaces to &nbsp; and Newlines to <br/>
            HTMLEncoder.encode(_writer, cbuf, off, len, true, true);
        }
    }

    private boolean isScriptOrStyle() {
        initializeStartedTagInfo();

        return (_isStyle != null && _isStyle.booleanValue())
                || (_isScript != null && _isScript.booleanValue());
    }

    private boolean isScript() {
        initializeStartedTagInfo();
        return (_isScript != null && _isScript.booleanValue());
    }

    private boolean isTextarea() {
        initializeStartedTagInfo();
        return _isTextArea != null && _isTextArea.booleanValue();
    }

    private void initializeStartedTagInfo() {
        if (_startElementName != null) {
            if (_isScript == null) {
                if (_startElementName.equalsIgnoreCase(HTML.SCRIPT_ELEM)) {
                    _isScript = Boolean.TRUE;
                    _isStyle = Boolean.FALSE;
                    _isTextArea = Boolean.FALSE;
                } else {
                    _isScript = Boolean.FALSE;
                }
            }
            if (_isStyle == null) {
                if (_startElementName.equalsIgnoreCase(HTML.STYLE_ELEM)) {
                    _isStyle = Boolean.TRUE;
                    _isTextArea = Boolean.FALSE;
                } else {
                    _isStyle = Boolean.FALSE;
                }
            }

            if (_isTextArea == null) {
                if (_startElementName.equalsIgnoreCase(HTML.TEXTAREA_ELEM)) {
                    _isTextArea = Boolean.TRUE;
                } else {
                    _isTextArea = Boolean.FALSE;
                }
            }
        }
    }

    // Writer methods

    @Override
    public void close() throws IOException {
        closeStartTagIfNecessary();
        _writer.close();
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
        closeStartTagIfNecessary();
        _writer.write(cbuf, off, len);
    }

    @Override
    public void write(int c) throws IOException {
        closeStartTagIfNecessary();
        _writer.write(c);
    }

    @Override
    public void write(char cbuf[]) throws IOException {
        closeStartTagIfNecessary();
        _writer.write(cbuf);
    }

    @Override
    public void write(String str) throws IOException {
        closeStartTagIfNecessary();
        // empty string commonly used to force the start tag to be closed.
        // in such case, do not call down the writer chain
        if (str.length() > 0) {
            _writer.write(str);
        }
    }
    
    public void writeLine(String str) throws IOException {
        closeStartTagIfNecessary();
        // empty string commonly used to force the start tag to be closed.
        // in such case, do not call down the writer chain
        if (str.length() > 0) {
            _writer.write(str);
            _writer.write('\n');
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        closeStartTagIfNecessary();
        _writer.write(str.substring(off, off + len));
    }
}
