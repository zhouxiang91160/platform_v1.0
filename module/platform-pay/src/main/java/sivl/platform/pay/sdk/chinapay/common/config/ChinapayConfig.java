package sivl.platform.pay.sdk.chinapay.common.config;

import java.util.Date;

import sivl.platform.common.utils.DateUtil;

public class ChinapayConfig {
	
	public static String merId = null;
	public static String frontUrl = null;
	public static String backUrl = null;
	public static String wap_frontUrl = null;
	public static String wap_backUrl = null;
	public static String encoding_utf8 = "UTF-8";
	public static String app_backUrl = null;
	
	public static String getCurrentTime(){
		return DateUtil.fmtDateToStr(new Date(), "yyyyMMddHHmmss");
	}

}
