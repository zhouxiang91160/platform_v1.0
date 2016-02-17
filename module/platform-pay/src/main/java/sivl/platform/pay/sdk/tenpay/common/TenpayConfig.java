package sivl.platform.pay.sdk.tenpay.common;

public class TenpayConfig {

	// 收款方
	public static String spname = null;
	// 商户号
	public static String partner = null;
	// 密钥
	public static String key = null;
	// 证书路径
	public static String cert_path = null;
	// 证书密码
	public static String cert_pwd = null;
	// 交易完成后跳转的URL
	public static String web_return_url = null;
	// 接收财付通通知的URL
	public static String web_notify_url = null;
	// 手机支付参数（账户权限统一，参数可以省略）--------------------------
	// 商户号
	public static String wap_partner = null;
	// 密钥
	public static String wap_key = null;
	// 证书路径
	public static String wap_cert_path = null;
	// 证书密码
	public static String wap_cert_pwd = null;
	// 交易完成后跳转的URL
	public static String wap_return_url = null;
	// 接收财付通通知的URL
	public static String wap_notify_url = null;
	// 手机支付参数（账户权限统一，参数可以省略）--------------------------

	public static String PAY_WEB_API = "https://gw.tenpay.com/gateway/pay.htm";
	public static String PAY_WAP_API = "https://wap.tenpay.com/cgi-bin/wappayv2.0/wappay_init.cgi";
	public static String CHARSET = "UTF-8";

}
