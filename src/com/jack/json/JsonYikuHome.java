package com.jack.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonYikuHome {
	public static List<Map<String, Object>> JsonToList(String jsonString) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();

		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String RESULT = jsonObject.getString("result");
			if (RESULT.equalsIgnoreCase("SUCCESS")) {
				JSONArray jsonArray_list = jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray_list.length(); i++) {
					JSONObject jsonObject_list2 = jsonArray_list
							.getJSONObject(i);
					Map<String, Object> map = new HashMap<String, Object>();
					if (JudgeExist.judgeExist(jsonObject_list2, "id")) {
						map.put("id", jsonObject_list2.getString("id"));// 商品id
					}
					if (JudgeExist.judgeExist(jsonObject_list2, "cat_name")) {
						map.put("cat_name",
								jsonObject_list2.getString("cat_name"));// 商品分类名称
					}
					if (JudgeExist.judgeExist(jsonObject_list2, "cat_index")) {
						map.put("cat_index",
								jsonObject_list2.getString("cat_index"));// 商品分类下标
					}
					if (JudgeExist.judgeExist(jsonObject_list2, "store_id")) {
						map.put("store_id",
								jsonObject_list2.getString("store_id"));// 店铺id
					}

					list_json.add(map);
				}
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

	public static List<Map<String, Object>> JsonToListProduct(String jsonString) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String RESULT = jsonObject.getString("result");
			if (RESULT.equalsIgnoreCase("SUCCESS")) {
				JSONArray jsonArray_list = jsonObject.getJSONArray("data");
				for (int i = 0; i < jsonArray_list.length(); i++) {
					JSONObject jsonObject_list2 = jsonArray_list
							.getJSONObject(i);
					JSONArray jsonArray_items = jsonObject_list2
							.getJSONArray("items");
					for (int j = 0; j < jsonArray_items.length(); j++) {
						JSONObject jsonObject_items = jsonArray_items
								.getJSONObject(j);
						Map<String, Object> map = new HashMap<String, Object>();
						if (JudgeExist.judgeExist(jsonObject_items,
								"prod_basic_id")) {
							map.put("prod_basic_id",
									jsonObject_items.getString("prod_basic_id"));// 商品id

						}

						map.put("flag", false);// 商品标识

						if (JudgeExist.judgeExist(jsonObject_items, "name")) {
							map.put("name", jsonObject_items.getString("name"));// 商品名称
						}
						if (JudgeExist.judgeExist(jsonObject_items, "image")) {
							map.put("image",
									jsonObject_items.getString("image"));// 商品图片
						}
						if (JudgeExist.judgeExist(jsonObject_items, "unitname")) {
							map.put("unitname",
									jsonObject_items.getString("unitname"));// 单位
						}
						if (JudgeExist.judgeExist(jsonObject_items,
								"Short_desc")) {
							map.put("Short_desc",
									jsonObject_items.getString("Short_desc"));// 简单描述
						}
						if (JudgeExist.judgeExist(jsonObject_items, "property")) {
							JSONArray jsonArray_property = jsonObject_items
									.getJSONArray("property");
							List<Map<String, Object>> list_property = new ArrayList<Map<String, Object>>();
							for (int k = 0; k < jsonArray_property.length(); k++) {
								JSONObject jsonObject_property = jsonArray_property
										.getJSONObject(k);
								Map<String, Object> map_property = new HashMap<String, Object>();
								if (JudgeExist.judgeExist(jsonObject_property,
										"store_prod_id")) {
									map_property
											.put("store_prod_id",
													jsonObject_property
															.getString("store_prod_id"));// 商品id
								}
								if (JudgeExist.judgeExist(jsonObject_property,
										"price")) {
									map_property.put("price", String.format(
											"%.2f", jsonObject_property
													.getDouble("price") / 100));// 价格
								}
								if (JudgeExist.judgeExist(jsonObject_property,
										"isStock")) {
									map_property.put("isStock",
											jsonObject_property
													.getString("isStock"));//
								}
								if (JudgeExist.judgeExist(jsonObject_property,
										"stock")) {
									map_property.put("stock",
											jsonObject_property
													.getString("stock"));//
								}
								if (JudgeExist.judgeExist(jsonObject_property,
										"pro")) {
									map_property.put("pro", jsonObject_property
											.getString("pro"));// 规格
								}
								list_property.add(map_property);
							}
							map.put("property", list_property);
							map.put("property_index", 0);
							map.put("pro_count", 0);
							Log.e("123", "==list_property.size:"
									+ list_property.size());
						}
						list_json.add(map);
					}

				}
			}
			return list_json;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static List<Map<String, Object>> JsonToList(JSONObject json) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();
		if (json != null) {
			if (json.has("data")) {
				try {
					JSONArray array = json.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						Map<String, Object> map = new HashMap<String, Object>();
						JSONObject jsn = (JSONObject) array.get(i);
						if (jsn.has("id")) {
							map.put("id", jsn.getString("id"));
						}
						if (jsn.has("cat_name")) {
							map.put("cat_name", jsn.getString("cat_name"));
						}
						if (jsn.has("items")) {
							List<Map<String, Object>> list_json1 = new ArrayList<Map<String, Object>>();
							
							JSONArray arra = jsn.getJSONArray("items");
							
							for (int j = 0; j < arra.length(); j++) {
								JSONObject jsonObject_items = arra.getJSONObject(j);
								Map<String, Object> map1 = new HashMap<String, Object>();
								if (JudgeExist
										.judgeExist(jsonObject_items, "prod_basic_id")) {
									map1.put("prod_basic_id",
											jsonObject_items.getString("prod_basic_id"));// 商品id

								}

								map1.put("flag", false);// 商品标识

								if (JudgeExist.judgeExist(jsonObject_items, "name")) {
									map1.put("name", jsonObject_items.getString("name"));// 商品名称
								}
								if (JudgeExist.judgeExist(jsonObject_items, "image")) {
									map1.put("image", jsonObject_items.getString("image"));// 商品图片
								}
								if (JudgeExist.judgeExist(jsonObject_items, "unitname")) {
									map1.put("unitname",
											jsonObject_items.getString("unitname"));// 单位
								}
								if (JudgeExist.judgeExist(jsonObject_items, "Short_desc")) {
									map1.put("Short_desc",
											jsonObject_items.getString("Short_desc"));// 简单描述
								}
								if (JudgeExist.judgeExist(jsonObject_items, "property")) {
									JSONArray jsonArray_property = jsonObject_items
											.getJSONArray("property");
									List<Map<String, Object>> list_property = new ArrayList<Map<String, Object>>();
									for (int k = 0; k < jsonArray_property.length(); k++) {
										JSONObject jsonObject_property = jsonArray_property
												.getJSONObject(k);
										Map<String, Object> map_property = new HashMap<String, Object>();
										if (JudgeExist.judgeExist(jsonObject_property,
												"store_prod_id")) {
											map_property.put("store_prod_id",
													jsonObject_property
															.getString("store_prod_id"));// 商品id
										}
										if (JudgeExist.judgeExist(jsonObject_property,
												"price")) {
											map_property.put("price", String
													.format("%.2f", jsonObject_property
															.getDouble("price") / 100));// 价格
										}
										if (JudgeExist.judgeExist(jsonObject_property,
												"isStock")) {
											map_property.put("isStock", jsonObject_property
													.getString("isStock"));//
										}
										if (JudgeExist.judgeExist(jsonObject_property,
												"stock")) {
											map_property.put("stock",
													jsonObject_property.getString("stock"));//
										}
										if (JudgeExist.judgeExist(jsonObject_property,
												"pro")) {
											map_property.put("pro",
													jsonObject_property.getString("pro"));// 规格
										}
										list_property.add(map_property);
									}
									map1.put("property", list_property);
									map1.put("property_index", 0);
									map1.put("pro_count", 0);
								}
								list_json1.add(map1);
						}
						map.put("items", list_json1);
						}
						list_json.add(map);
					}

				} catch (JSONException e) {
					list_json.clear();
					e.printStackTrace();
				}
			}
		}
		return list_json;
	}

	public static List<Map<String, Object>> JsonToListProduct(
			JSONArray jsonString) {
		List<Map<String, Object>> list_json = new ArrayList<Map<String, Object>>();
		if (jsonString == null)
			return list_json;

		try {
				for (int j = 0; j < jsonString.length(); j++) {
					JSONObject jsonObject_items = jsonString.getJSONObject(j);
					Map<String, Object> map = new HashMap<String, Object>();
					if (JudgeExist
							.judgeExist(jsonObject_items, "prod_basic_id")) {
						map.put("prod_basic_id",
								jsonObject_items.getString("prod_basic_id"));// 商品id

					}

					map.put("flag", false);// 商品标识

					if (JudgeExist.judgeExist(jsonObject_items, "name")) {
						map.put("name", jsonObject_items.getString("name"));// 商品名称
					}
					if (JudgeExist.judgeExist(jsonObject_items, "image")) {
						map.put("image", jsonObject_items.getString("image"));// 商品图片
					}
					if (JudgeExist.judgeExist(jsonObject_items, "unitname")) {
						map.put("unitname",
								jsonObject_items.getString("unitname"));// 单位
					}
					if (JudgeExist.judgeExist(jsonObject_items, "Short_desc")) {
						map.put("Short_desc",
								jsonObject_items.getString("Short_desc"));// 简单描述
					}
					if (JudgeExist.judgeExist(jsonObject_items, "property")) {
						JSONArray jsonArray_property = jsonObject_items
								.getJSONArray("property");
						List<Map<String, Object>> list_property = new ArrayList<Map<String, Object>>();
						for (int k = 0; k < jsonArray_property.length(); k++) {
							JSONObject jsonObject_property = jsonArray_property
									.getJSONObject(k);
							Map<String, Object> map_property = new HashMap<String, Object>();
							if (JudgeExist.judgeExist(jsonObject_property,
									"store_prod_id")) {
								map_property.put("store_prod_id",
										jsonObject_property
												.getString("store_prod_id"));// 商品id
							}
							if (JudgeExist.judgeExist(jsonObject_property,
									"price")) {
								map_property.put("price", String
										.format("%.2f", jsonObject_property
												.getDouble("price") / 100));// 价格
							}
							if (JudgeExist.judgeExist(jsonObject_property,
									"isStock")) {
								map_property.put("isStock", jsonObject_property
										.getString("isStock"));//
							}
							if (JudgeExist.judgeExist(jsonObject_property,
									"stock")) {
								map_property.put("stock",
										jsonObject_property.getString("stock"));//
							}
							if (JudgeExist.judgeExist(jsonObject_property,
									"pro")) {
								map_property.put("pro",
										jsonObject_property.getString("pro"));// 规格
							}
							list_property.add(map_property);
						}
						map.put("property", list_property);
						map.put("property_index", 0);
						map.put("pro_count", 0);
						Log.e("123",
								"==list_property.size:" + list_property.size());
					}
					list_json.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list_json;
	}
}
