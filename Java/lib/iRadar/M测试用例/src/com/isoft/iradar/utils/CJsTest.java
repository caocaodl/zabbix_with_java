package com.isoft.iradar.utils;

import org.junit.Test;

public class CJsTest {

	@Test
	public void test() {
		String s = "{\"jsonrpc\": \"2.0\", \"method\": \"message.settings\", \"params\": {}, \"auth\": \"0b0584f8b52fea5cd4fe7565ca4aeeef\", \"id\": 1}";
		Object o = CJs.decodeJson(s);
		System.out.println(o.getClass());
	}

}
