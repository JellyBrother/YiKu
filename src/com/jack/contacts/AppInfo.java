package com.jack.contacts;

public class AppInfo {

	private static String packageName;

	public static String getPackageName() {
		
		if (packageName == null) {
			String name = AppContext_city.getCurrentActivity().getPackageName();
			packageName = name.substring(name.lastIndexOf(".") + 1);
		}
		
		return packageName;
	}
	
	public static void exit() {
	}
}
