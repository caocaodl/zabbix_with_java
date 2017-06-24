package com.isoft.iradar.tags;

import static com.isoft.iradar.Cphp.echo;
import static com.isoft.types.CArray.array;
import static com.isoft.types.CArray.map;

import java.util.Map;

import com.isoft.lang.CodeConfirmed;
import com.isoft.types.CArray;

/**
 * Class for standard ajax response generation.
 */
@CodeConfirmed("benne.2.2.6")
public class AjaxResponse {
	
	private boolean _result = true;
	private Map _data = array();
	private CArray<Map<String,String>> _errors = array();
	
	public AjaxResponse() {
		this(null);
	}
	
	public AjaxResponse(Map data) {
		if (data != null) {
			success(data);
		}
	}
	
	/**
	 * Add error to ajax response. All errors are returned as array in "errors" part of response.
	 *
	 * @param string error text
	 * @return void
	 */
	public void error(String error) {
		_result = false;
		_errors.add((Map)map("error", error));
	}
	
	/**
	 * Assigns data that is returned in "data" part of ajax response.
	 * If any error was added previously, this method does nothing.
	 *
	 * @param array data
	 * @return void
	 */
	public void success(Map data) {
		if (_result) {
			_data = data;
		}
	}
	
	/**
	 * Output ajax response. If any error was added, "result" is false, otherwise true.
	 *
	 * @return void
	 */
	public void send() {
		CJSON json = new CJSON();
		if (_result) {
			echo(json.encode(map("result", true, "data", _data)));
		} else {
			echo(json.encode(map("result", false, "errors", _errors)));
		}
	}

}
