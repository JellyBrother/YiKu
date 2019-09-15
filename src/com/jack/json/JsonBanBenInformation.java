package com.jack.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonBanBenInformation {
	public static List<Map<String, Object>> JsonToList(String jsonString) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String RESULT = jsonObject.getString("result");
			if (RESULT.equalsIgnoreCase("SUCCESS")) {
				JSONObject jsonObject_list2 = jsonObject.getJSONObject("data");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("version_id", jsonObject_list2.getString("version_id"));// id
				if (JudgeExist.judgeExist(jsonObject_list2, "platform")) {
					map.put("platform", jsonObject_list2.getString("platform"));// 应用平台
				}
				if (JudgeExist.judgeExist(jsonObject_list2, "app_name")) {
					map.put("app_name", jsonObject_list2.getString("app_name"));// 应用名称
				}
				if (JudgeExist.judgeExist(jsonObject_list2, "app_version")) {
					map.put("app_version",
							jsonObject_list2.getString("app_version"));// 版本号
				}
				if (JudgeExist.judgeExist(jsonObject_list2, "url")) {
					map.put("url", jsonObject_list2.getString("url"));// 网络地址
				}
				if (JudgeExist.judgeExist(jsonObject_list2, "des")) {
					map.put("des", jsonObject_list2.getString("des"));// 更新信息
				}
				if (JudgeExist.judgeExist(jsonObject_list2, "update_time")) {
					map.put("update_time",
							jsonObject_list2.getString("update_time"));// 更新时间
				}
				list_json.add(map);
			}
			return list_json;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}
}
