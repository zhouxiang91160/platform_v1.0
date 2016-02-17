package sivl.platform.pay.sdk.tenpay.wap;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.constant.ResultCons.TenpayCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.tenpay.common.TenpayConfig;
import sivl.platform.pay.sdk.tenpay.common.client.TenpayHttpClient;
import sivl.platform.pay.sdk.tenpay.common.client.XMLClientResponseHandler;
import sivl.platform.pay.sdk.tenpay.common.util.TenpayUtil;
import sivl.platform.pay.sdk.tenpay.common.wap.WapPayInitRequestHandler;
import sivl.platform.pay.sdk.tenpay.common.wap.WapPayPageResponseHandler;
import sivl.platform.pay.sdk.tenpay.common.wap.WapPayRequestHandler;

public class TenpayWapServiceUtil {

	public static ResultModel<Object> payment(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			// 创建支付初始化请求示例
			WapPayInitRequestHandler reqHandler = new WapPayInitRequestHandler(
					payment.getRequest(), payment.getResponse());
			// 初始化
			reqHandler.init();
			// 设置密钥
			reqHandler.setKey(TenpayConfig.key);
			// -----------------------------
			// 设置请求参数
			// -----------------------------
			// 当前时间 yyyyMMddHHmmss
			// 订单号，必须保持唯一。此处 用 时间+4个随机数 模拟 ，商户可自行替换
			reqHandler.setParameter("sp_billno", payment.getOutTradeNo());
			reqHandler.setParameter("desc", payment.getBody());
			reqHandler.setParameter("bargainor_id", TenpayConfig.wap_partner);
			reqHandler.setParameter("total_fee",
					String.valueOf(payment.getTradeFee().intValue()));
			reqHandler.setParameter("notify_url", TenpayConfig.wap_notify_url);
			reqHandler
					.setParameter("callback_url", TenpayConfig.wap_return_url);
			reqHandler.setParameter("time_start", TenpayUtil.getCurrTime());
			reqHandler.setParameter("time_expire", payment.getOvertime());
			// 获取请求带参数的url
			String requestUrl = reqHandler.getRequestURL();

			// 获取debug信息
			String debuginfo = reqHandler.getDebugInfo();
			System.out.println("debuginfo:" + debuginfo);
			System.out.println("requestUrl:" + requestUrl);

			// 创建TenpayHttpClient，后台通信
			TenpayHttpClient httpClient = new TenpayHttpClient();

			// 设置请求内容
			httpClient.setReqContent(requestUrl);
			// 远程调用
			if (httpClient.call()) {
				String resContent = httpClient.getResContent();
				System.out.println("responseContent:" + resContent);
				// ----------------------
				// 应答处理,获取token_id
				// ----------------------
				XMLClientResponseHandler resHandler = new XMLClientResponseHandler();
				resHandler.setContent(resContent);
				String token_id = resHandler.getParameter("token_id");
				if (StringUtil.isNotEmpty(token_id)) {
					// 生成支付请求
					WapPayRequestHandler wapPayRequestHandler = new WapPayRequestHandler(
							payment.getRequest(), payment.getResponse());
					wapPayRequestHandler.init();
					wapPayRequestHandler.setParameter("token_id", token_id);
					String url = wapPayRequestHandler.getRequestURL();
					if (StringUtil.isNotEmpty(url)) {
						result.setMsg(ResultCons.SUCCESS_MSG);
						result.setCode(ResultCons.SUCCESS);
						result.setData(url);
					} else {
						// 支付url获取失败
						result.setMsg("支付url获取失败支付url获取失败:"
								+ resHandler.getParameter("err_info"));
						result.setCode(ResultCons.FAIL);
					}
				} else {
					// 获取token_id调用失败 ，显示错误 页面
					result.setMsg("财付通wap获取token_id调用失败:"
							+ resHandler.getParameter("err_info"));
					result.setCode(ResultCons.FAIL);
				}
			} else {
				// 后台调用失败 ，显示错误 页面
				result.setMsg("财付通wap后台调用失败:" + httpClient.getResponseCode()
						+ httpClient.getErrInfo());
				result.setCode(ResultCons.FAIL);
			}
		} catch (Exception e) {
			result.setMsg("财付通wap:" + e.getMessage());
			result.setCode(ResultCons.FAIL);
		}

		return result;
	}

	public static ResultModel<Object> paymentVerify(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			// 创建实例
			WapPayPageResponseHandler resHandler = new WapPayPageResponseHandler(
					payment.getRequest(), payment.getResponse());
			resHandler.setKey(TenpayConfig.key);
			// uri编码，tomcat需要
			resHandler.setUriEncoding(TenpayConfig.CHARSET);
			// 判断签名
			if (resHandler.isTenpaySign()) {
				// 支付结果
				String pay_result = resHandler.getParameter("pay_result");
				String sp_billno = resHandler.getParameter("sp_billno");//商户订单号
				String transaction_id = resHandler.getParameter("transaction_id");//财付通订单号
				String total_fee = resHandler.getParameter("total_fee");//交易金额
				String time_end = resHandler.getParameter("time_end");//支付时间
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("out_trade_no", sp_billno);
				map.put("trade_no", transaction_id);
				map.put("total_fee", total_fee);
				map.put("trade_time", time_end);
				
				if (pay_result.equals(TenpayCons.TRADE_SUCCESS)) {
					result.setMsg(ResultCons.SUCCESS_MSG);
					result.setCode(ResultCons.SUCCESS);
					result.setData(map);
					result.setExt(map);
				} else {
					result.setMsg("支付失败,pay_result=" + pay_result);
					result.setCode(ResultCons.FAIL);
					result.setData(map);
					result.setExt(map);
				}

			} else {
				result.setMsg("验证签名失败");
				result.setCode(ResultCons.FAIL);
			}
			String debugInfo = resHandler.getDebugInfo();
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			result.setMsg(ResultCons.FAIL_MSG.concat(e.getMessage()));
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}

}
