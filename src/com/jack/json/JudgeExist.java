package com.jack.json;

import org.json.JSONObject;

public class JudgeExist {
	public static boolean judgeExist(JSONObject json, String string) {
		return json.has(string);
	}
}
