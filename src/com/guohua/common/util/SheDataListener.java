package com.guohua.common.util;


public interface SheDataListener {
	public void onSuccess(int tag, String data);

	public void onError(int tag, String errorMessage);
}
