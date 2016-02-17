package sivl.platform.pay.sdk.tenpay.web;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.JSONUtil;
import sivl.platform.common.utils.MapUtil;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.constant.ResultCons.TenpayCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.tenpay.common.RequestHandler;
import sivl.platform.pay.sdk.tenpay.common.ResponseHandler;
import sivl.platform.pay.sdk.tenpay.common.TenpayConfig;
import sivl.platform.pay.sdk.tenpay.common.client.ClientResponseHandler;
import sivl.platform.pay.sdk.tenpay.common.client.TenpayHttpClient;
import sivl.platform.pay.sdk.tenpay.common.util.TenpayUtil;

public class TenpayWebServiceUtil {

	public static ResultModel<Object> payment(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 创建支付请求对象
		RequestHandler reqHandler = new RequestHandler(payment.getRequest(),
				payment.getResponse());
		reqHandler.init();
		// 设置密钥
		reqHandler.setKey(TenpayConfig.key);
		// 设置支付网关
		reqHandler.setGateUrl(TenpayConfig.PAY_WEB_API);

		// -----------------------------
		// 设置支付参数
		// -----------------------------
		reqHandler.setParameter("partner", TenpayConfig.partner); // 商户号
		reqHandler.setParameter("out_trade_no", payment.getOutTradeNo()); // 商家订单号
		reqHandler.setParameter("total_fee",
				String.valueOf(payment.getTradeFee().intValue())); // 商品金额,以分为单位
		reqHandler.setParameter("return_url", TenpayConfig.web_return_url); // 交易完成后跳转的URL
		reqHandler.setParameter("notify_url", TenpayConfig.web_notify_url); // 接收财付通通知的URL
		reqHandler.setParameter("body", payment.getBody()); // 商品描述
		reqHandler.setParameter("bank_type", "DEFAULT"); // 银行类型(中介担保时此参数无效)
		reqHandler.setParameter("spbill_create_ip", payment.getCreateIp()); // 用户的公网ip，不是商户服务器IP
		reqHandler.setParameter("fee_type", "1"); // 币种，1人民币
		reqHandler.setParameter("subject", payment.getSubject()); // 商品名称(中介交易时必填)

		// 系统可选参数
		reqHandler.setParameter("sign_type", "MD5"); // 签名类型,默认：MD5
		reqHandler.setParameter("service_version", "1.0"); // 版本号，默认为1.0
		reqHandler.setParameter("input_charset", TenpayConfig.CHARSET); // 字符编码
		reqHandler.setParameter("sign_key_index", "1"); // 密钥序号

		// 业务可选参数
		reqHandler.setParameter("time_start", TenpayUtil.getCurrTime()); // 订单生成时间，格式为yyyymmddhhmmss
		reqHandler.setParameter("time_expire", payment.getOvertime()); // 订单失效时间，格式为yyyymmddhhmmss

		// 请求的url
		try {
			String url = reqHandler.getRequestURL();
			if (StringUtil.isEmpty(url)) {
				result.setMsg("财富支付链接获取异常");
				result.setCode(ResultCons.FAIL);
			} else {
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
				result.setData(url);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result.setMsg("财富支付异常：" + e.getMessage());
			result.setCode(ResultCons.FAIL);
		}
		// 获取debug信息,建议把请求和debug信息写入日志，方便定位问题
		String debuginfo = reqHandler.getDebugInfo();
		System.out.println("debug:" + debuginfo);
		return result;
	}

	/**
	 * 前台返回结果解析
	 * 
	 * @param payment
	 * @return
	 */
	public static ResultModel<Object> paymentFrontVerify(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 创建支付应答对象
		ResponseHandler resHandler = new ResponseHandler(payment.getRequest(),
				payment.getResponse());
		resHandler.setKey(TenpayConfig.key);
		// 判断签名
		if (resHandler.isTenpaySign()) {
			// 通知id
			String notify_id = resHandler.getParameter("notify_id");
			// 商户订单号
			String out_trade_no = resHandler.getParameter("out_trade_no");
			// 财付通订单号
			String transaction_id = resHandler.getParameter("transaction_id");
			// 金额,以分为单位
			String total_fee = resHandler.getParameter("total_fee");
			// 支付结果 0 成功
			String trade_state = resHandler.getParameter("trade_state");
			// 交易模式，1即时到账，2中介担保
			String trade_mode = resHandler.getParameter("trade_mode");
			// 支付完成时间
			String time_end = resHandler.getParameter("time_end");
			// 买家帐号
			String buyer_alias = resHandler.getParameter("buyer_alias");

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("out_trade_no", out_trade_no);
			map.put("trade_no", transaction_id);
			map.put("total_fee", total_fee);
			map.put("trade_time", time_end);
			map.put("buyer", buyer_alias);
			map.put("trade_state", trade_state);

			if (TenpayCons.TRADE_MODE_1.equals(trade_mode)) { // 即时到账
				if (TenpayCons.TRADE_SUCCESS.equals(trade_state)) {
					result.setMsg(ResultCons.SUCCESS_MSG);
					result.setCode(ResultCons.SUCCESS);
					result.setData(map);
					result.setExt(map);
				} else {
					String pay_info = resHandler.getParameter("pay_info");
					result.setMsg(ResultCons.FAIL_MSG.concat(":即时到帐付款失败,")
							.concat(pay_info));
					result.setCode(ResultCons.FAIL);
					result.setData(map);
					result.setExt(map);
				}
			} else if ("2".equals(trade_mode)) {
			}
			{
				// 中介担保
			}
		} else {
			result.setMsg(ResultCons.FAIL_MSG.concat(":认证签名失败"));
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}

	public static ResultModel<Object> paymentBackVerify(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			// 创建支付应答对象
			ResponseHandler resHandler = new ResponseHandler(payment.getRequest(),
					payment.getResponse());
			resHandler.setKey(TenpayConfig.key);
			// 判断签名
			if (resHandler.isTenpaySign()) {
				// 通知id
				String notify_id = resHandler.getParameter("notify_id");
				// 创建请求对象
				RequestHandler queryReq = new RequestHandler(null, null);
				// 通信对象
				TenpayHttpClient httpClient = new TenpayHttpClient();
				// 应答对象
				ClientResponseHandler queryRes = new ClientResponseHandler();
				// 通过通知ID查询，确保通知来至财付通
				queryReq.init();
				queryReq.setKey(TenpayConfig.key);
				queryReq.setGateUrl("https://gw.tenpay.com/gateway/simpleverifynotifyid.xml");
				queryReq.setParameter("partner", TenpayConfig.partner);
				queryReq.setParameter("notify_id", notify_id);
				// 通信对象
				httpClient.setTimeOut(5);
				// 设置请求内容
				httpClient.setReqContent(queryReq.getRequestURL());
				System.out.println("验证ID请求字符串:" + queryReq.getRequestURL());
				// 后台调用
				if (httpClient.call()) {
					// 设置结果参数
					queryRes.setContent(httpClient.getResContent());
					System.out.println("验证ID返回字符串:" + httpClient.getResContent());
					queryRes.setKey(TenpayConfig.key);
					// 获取id验证返回状态码，0表示此通知id是财付通发起
					String retcode = queryRes.getParameter("retcode");
					// 商户订单号
					String out_trade_no = resHandler.getParameter("out_trade_no");
					// 财付通订单号
					String transaction_id = resHandler
							.getParameter("transaction_id");
					// 金额,以分为单位
					String total_fee = resHandler.getParameter("total_fee");
					// 如果有使用折扣券，discount有值，total_fee+discount=原请求的total_fee
					String discount = resHandler.getParameter("discount");
					// 支付结果
					String trade_state = resHandler.getParameter("trade_state");
					// 交易模式，1即时到账，2中介担保
					String trade_mode = resHandler.getParameter("trade_mode");
					//付款时间
					String time_end = resHandler.getParameter("time_end");
					//账户信息
					String buyer_alias = resHandler.getParameter("buyer_alias");
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("out_trade_no", out_trade_no);
					map.put("trade_no", transaction_id);
					map.put("total_fee", total_fee);
					map.put("trade_time", time_end);
					map.put("buyer", buyer_alias);
					map.put("trade_state", trade_state);
					// 判断签名及结果
					if (queryRes.isTenpaySign() && "0".equals(retcode)) {
						System.out.println("id验证成功");
						if (TenpayCons.TRADE_MODE_1.equals(trade_mode)) { // 即时到账
							result.setMsg(ResultCons.SUCCESS_MSG);
							result.setCode(ResultCons.SUCCESS);
						} else if ("2".equals(trade_mode)) {
							//中介担保交易
						}
					} else {
						// 错误时，返回结果未签名，记录retcode、retmsg看失败详情。
						result.setMsg("查询验证签名失败或id验证失败" + ",retcode:"
								+ queryRes.getParameter("retcode"));
						result.setCode(ResultCons.FAIL);
					}
					result.setData(map);
					result.setExt(map);
				} else {
					// 有可能因为网络原因，请求已经处理，但未收到应答。
					result.setMsg("后台调用通信失败，"+httpClient.getResponseCode()+":"+httpClient.getErrInfo());
					result.setCode(ResultCons.FAIL);
				}
			} else {
				result.setMsg("通知签名验证失败");
				result.setCode(ResultCons.FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("系统服务异常："+e.getMessage());
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}
}
