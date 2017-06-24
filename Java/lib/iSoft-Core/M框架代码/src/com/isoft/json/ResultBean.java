package com.isoft.json;

public class ResultBean {
	private boolean success;

	private String message;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ResultBean getSuccessResult(String message) {
		ResultBean resultBean = new ResultBean();
		resultBean.setSuccess(true);
		resultBean.setMessage(message);

		return resultBean;
	}

	public static ResultBean getSuccessResult() {
		return getSuccessResult("");
	}

	public static ResultBean getFailureResult(String message) {
		ResultBean resultBean = new ResultBean();
		resultBean.setSuccess(false);
		resultBean.setMessage(message);

		return resultBean;
	}
}
