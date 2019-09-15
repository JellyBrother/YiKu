/**
 * 
 */
package com.guahua.common.cache;

import android.graphics.drawable.Drawable;

/**
 * 
 * 图片下载回传接口. <br>
 * 类详细说明.
 * <p>
 * Copyright: Copyright (c) 2013-1-31 下午4:22:57
 * <p>
 * Company: 北京宽连十方数字技术有限公司
 * <p>
 * 
 * @author zhangb@cndatacom.com
 * @version 1.0.0
 */
public interface DrawableDownloadCallback {
	void onLoad(Drawable drawable);

	void onLoadFail();
}
