/**
 * 
 */
package com.guahua.common.cache;

import android.graphics.drawable.Drawable;


/**
 * 
 * 图片缓存加载接口. <br>
 * 类详细说�?
 * <p>
 * Copyright: Copyright (c) 2013-1-31 下午7:08:21
 * <p>
 * Company: 北京宽连十方数字�?��有限公司
 * <p>
 * @author dingyj@c-platform.com
 * @version 1.0.0
 */
public interface DrawableDownloadCacheListener {
	
	void returnDrawable(Drawable drawable,Object ... params);
}
