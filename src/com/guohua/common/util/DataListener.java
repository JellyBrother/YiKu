package com.guohua.common.util;

import org.json.JSONObject;

public interface DataListener {
	public void onSuccess(int tag,JSONObject data);
	public void onError(int tag,String errorMessage);
	
}
