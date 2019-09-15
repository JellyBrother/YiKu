package com.jack.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTN {
	public static List<Map<String, Object>> JsonToList(String jsonString) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String RESULT = jsonObject.getString("result");
			if (RESULT.equalsIgnoreCase("SUCCESS")) {
				Map<String, Object> map = new HashMap<String, Object>();
				if (JudgeExist.judgeExist(jsonObject, "data")) {
					map.put("data", jsonObject.getString("data"));// tn号
				}

				list_json.add(map);
			}
			return list_json;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static List<Map<String, Object>> JsonToListFAILED(String jsonString) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("version", jsonObject.getInt("version"));// 版本号
			map.put("result", jsonObject.getString("result"));// 结果
			map.put("error_info", jsonObject.getString("error_info"));// 错误信息
			list.add(map);
			return list;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

}
