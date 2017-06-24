package com.isoft.iaas.openstack.ceilometer.v2.model;

import java.io.Serializable;

import com.isoft.iaas.openstack.base.client.OpenStackRequest;

public class Query {

	private String field;
	private String op;
	private Object value;

	private Query() {
	}

	public static Query lt(String field, Serializable value) {
		return filter(field, "lt", value);
	}

	public static Query le(String field, Serializable value) {
		return filter(field, "le", value);
	}

	public static Query eq(String field, Serializable value) {
		return filter(field, "eq", value);
	}

	public static Query ne(String field, Serializable value) {
		return filter(field, "ne", value);
	}

	public static Query ge(String field, Serializable value) {
		return filter(field, "ge", value);
	}

	public static Query gt(String field, Serializable value) {
		return filter(field, "gt", value);
	}

	private static Query filter(String field, String op, Serializable value) {
		Query q = new Query();
		q.field = field;
		q.value = value;
		q.op = "eq";
		return q;
	}

	public void marshal(OpenStackRequest request) {
		if ("period".equals(this.field)) {
			request.queryParam("period", this.value);
		} else {
			request.queryParam("q.field", this.field);
			request.queryParam("q.op", this.op);
			request.queryParam("q.value", this.value);
		}
	}

	public String getField() {
		return field;
	}

	public String getOp() {
		return op;
	}

	public Object getValue() {
		return value;
	}

}
