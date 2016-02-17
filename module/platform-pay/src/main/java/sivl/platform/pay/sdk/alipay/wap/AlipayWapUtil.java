package sivl.platform.pay.sdk.alipay.wap;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.alipay.common.config.AlipayConfig;
import sivl.platform.pay.sdk.alipay.common.util.UtilDate;

/**
 * 支付宝无线快捷支付 区别web支付：采用RSA签名（安全性更好）
 * 
 * @author Zhouxiang
 * 
 */
public class AlipayWapUtil {

	/**
	 * 调用支付宝支付接口
	 * 
	 * @param payments
	 * @return
	 */
	public static ResultModel<Object> payment(PaymentsModel payments) {
		ResultModel<Object> result = new ResultModel<Object>();
		result = getToken(payments);
		if (result.getCode().equals(ResultCons.FAIL)) {
			return result;
		}
		String req_data = "<auth_and_execute_req><request_token>"
				+ result.getData() + "</request_token></auth_and_execute_req>";

		Map<String, String> tradeMap = new HashMap<String, String>();

		try {
			tradeMap.put("service", AlipayConfig.service_trade);
			tradeMap.put("partner", AlipayConfig.partner);
			tradeMap.put("_input_charset", AlipayConfig.input_charset);
			tradeMap.put("sec_id", AlipayConfig.wap_sign_type);
			tradeMap.put("format", "xml");
			tradeMap.put("v", "2.0");
			tradeMap.put("req_data", req_data);
			String html = AlipayWapSubmit.buildRequest(
					AlipayConfig.ALIPAY_GATEWAY_NEW, tradeMap, "get", "确认");
			if (StringUtil.isNotEmpty(html)) {
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
				result.setData(html);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("支付宝WAP支付授权token请求异常：" + e.getMessage());
			result.setCode(ResultCons.FAIL);
		}

		return result;
	}

	/**
	 * wap支付授权token
	 * 
	 * @return
	 */
	private static ResultModel<Object> getToken(PaymentsModel payments) {
		ResultModel<Object> result = new ResultModel<Object>();
		String req_dataToken = "<direct_trade_create_req>" + "<notify_url>"
				+ AlipayConfig.wap_payment_notify + "</notify_url>"
				+ "<call_back_url>" + AlipayConfig.wap_payment_return
				+ "</call_back_url>" + "<seller_account_name>"
				+ AlipayConfig.seller + "</seller_account_name>"
				+ "<out_trade_no>" + payments.getOutTradeNo()
				+ "</out_trade_no>" + "<subject>" + payments.getSubject()
				+ "</subject>" + "<total_fee>" + payments.getTradeFee()
				+ "</total_fee>" + "<merchant_url></merchant_url>"
				+ "<pay_expire>" + payments.getOvertime() + "</pay_expire>"
				+ "</direct_trade_create_req>";
		System.out.println(req_dataToken);
		Map<String, String> tokenMap = new HashMap<String, String>();
		tokenMap.put("service", AlipayConfig.service_auth);
		tokenMap.put("partner", AlipayConfig.partner);
		tokenMap.put("_input_charset", AlipayConfig.input_charset);
		tokenMap.put("sec_id", AlipayConfig.wap_sign_type);
		tokenMap.put("format", "xml");
		tokenMap.put("v", "2.0");
		tokenMap.put("req_id", UtilDate.getOrderNum());
		tokenMap.put("req_data", req_dataToken);
		System.out.println(tokenMap.toString());
		try {
			// 建立请求
			String token = AlipayWapSubmit.buildRequest(
					AlipayConfig.ALIPAY_GATEWAY_NEW, "", "", tokenMap);
			// URLDECODE返回的信息
			token = URLDecoder.decode(token, AlipayConfig.input_charset);
			System.out.println(token);
			// 获取token
			String request_token = AlipayWapSubmit.getRequestToken(token);
			if (StringUtil.isNotEmpty(request_token)) {
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
				result.setData(request_token);
			} else {
				result.setMsg("支付宝WAP支付授权token获取失败");
				result.setCode(ResultCons.FAIL);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.setMsg("支付宝WAP支付授权token请求异常：" + e.getMessage());
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}

	/**
	 * 支付宝WAP后台回调校验
	 * 
	 * @param payments
	 * @return
	 * @throws Exception
	 */
	public static ResultModel<Object> paymentNotifyVerify(PaymentsModel payments) {
		HttpServletRequest request = payments.getRequest();
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			// 获取支付宝GET过来反馈信息
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"),
				// "utf-8");
				params.put(name, valueStr);
			}

			// RSA签名解密
			if (AlipayConfig.wap_sign_type.equals("0001")) {
				params = AlipayWapNotify.decrypt(params);
			}
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			Map<String, Object> rsMap = new HashMap<String, Object>();
			// XML解析notify_data数据
			Document doc_notify_data = DocumentHelper.parseText(params
					.get("notify_data"));
			// 商户订单号
			String out_trade_no = doc_notify_data.selectSingleNode(
					"//notify/out_trade_no").getText();
			rsMap.put("out_trade_no", out_trade_no);
			// 支付宝交易号
			String trade_no = doc_notify_data.selectSingleNode(
					"//notify/trade_no").getText();
			rsMap.put("trade_no", trade_no);
			// 交易状态
			String trade_status = doc_notify_data.selectSingleNode(
					"//notify/trade_status").getText();
			rsMap.put("trade_status", trade_status);
			// 交易金额
			String total_fee = doc_notify_data.selectSingleNode(
					"//notify/total_fee").getText();
			rsMap.put("total_fee", total_fee);
			// 支付时间
			String gmt_payment = doc_notify_data.selectSingleNode(
					"//notify/gmt_payment").getText();
			rsMap.put("gmt_payment", gmt_payment);
			// 买家帐号
			String buyer_email = doc_notify_data.selectSingleNode(
					"//notify/buyer_email").getText();
			rsMap.put("buyer_email", buyer_email);
			// 计算得出通知验证结果
			boolean verify_result = AlipayWapNotify.verifyNotify(params);

			if (verify_result) {
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
			} else {
				result.setMsg(ResultCons.FAIL_MSG.concat("支付宝WAP回调,参数验证失败。"));
				result.setCode(ResultCons.FAIL);
			}
			result.setExt(rsMap);
			result.setData(rsMap);
		} catch (Exception e) {
			result.setMsg(ResultCons.FAIL_MSG.concat(":" + e.getMessage()));
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}

	/**
	 * 支付宝WAP前台回调校验
	 * 
	 * @param payments
	 * @return
	 * @throws Exception
	 */
	public static ResultModel<Object> paymentFrontVerify(PaymentsModel payments) {
		HttpServletRequest request = payments.getRequest();
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			// 获取支付宝GET过来反馈信息
			Map<String, String> params = new HashMap<String, String>();
			Map requestParams = request.getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter
					.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				// valueStr = new String(valueStr.getBytes("ISO-8859-1"),
				// "utf-8");
				params.put(name, valueStr);
			}
			Map<String, Object> rsMap = new HashMap<String, Object>();
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			// 商户订单号
			String out_trade_no = new String(
					request.getParameter("out_trade_no"));
			rsMap.put("out_trade_no", out_trade_no);
			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no"));
			rsMap.put("trade_no", trade_no);
			// 交易状态
			String trade_status = new String(request.getParameter("result"));
			rsMap.put("trade_status", trade_status);
			// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			// 计算得出通知验证结果
			boolean verify_result = AlipayWapNotify.verifyReturn(params);
			if (verify_result) {
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
			} else {
				result.setMsg(ResultCons.FAIL_MSG.concat(":支付宝WAP回调,参数验证失败。"));
				result.setCode(ResultCons.FAIL);
			}
			result.setExt(rsMap);
			result.setData(rsMap);
		} catch (Exception e) {
			result.setMsg(ResultCons.FAIL_MSG.concat(":" + e.getMessage()));
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}

}
