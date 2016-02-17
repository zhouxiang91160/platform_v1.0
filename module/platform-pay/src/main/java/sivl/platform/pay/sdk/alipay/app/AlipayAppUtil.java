package sivl.platform.pay.sdk.alipay.app;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.alipay.common.config.AlipayConfig;
import sivl.platform.pay.sdk.alipay.common.sign.RSA;
import sivl.platform.pay.sdk.alipay.common.util.UtilDate;
import sivl.platform.pay.sdk.alipay.wap.AlipayWapNotify;
import sivl.platform.pay.sdk.alipay.wap.AlipayWapSubmit;

/**
 * 支付宝无线快捷支付 区别web支付：采用RSA签名（安全性更好）
 * 
 * @author Zhouxiang
 * 
 */
public class AlipayAppUtil {

	/**
	 * 调用支付宝支付接口
	 * 
	 * @param payments
	 * @return
	 */
	public static ResultModel<Object> payment(PaymentsModel payments) {
		ResultModel<Object> result = new ResultModel<Object>();
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("partner", AlipayConfig.partner);
		requestParams.put("seller_id", AlipayConfig.seller);
		requestParams.put("out_trade_no", payments.getOutTradeNo());
		requestParams.put("subject", payments.getSubject());
		requestParams.put("body", payments.getBody());
		requestParams.put("total_fee", Double.toString(payments.getTradeFee()));
		requestParams.put("notify_url", AlipayConfig.app_payment_notify);
		requestParams.put("service", AlipayConfig.app_service);
		requestParams.put("payment_type", AlipayConfig.payment_type);
		requestParams.put("_input_charset", AlipayConfig.input_charset);
		requestParams.put("it_b_pay", payments.getOvertime());
		StringBuffer contentValues = new StringBuffer();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = (String) requestParams.get(name);
			contentValues.append(name + "=\"").append(value + "\"&");
		}

		String content = contentValues.toString().substring(0,
				contentValues.toString().length() - 1);

		String sign = RSA.sign(content, AlipayConfig.private_key,
				AlipayConfig.input_charset);
		try {
			sign = URLEncoder.encode(sign, AlipayConfig.input_charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		contentValues.append("sign=\"").append(sign)
				.append("\"&sign_type=\"" + AlipayConfig.app_sign_type + "\"");

		String contents = contentValues.toString();
		System.out.println("contents=" + contents);
		if (StringUtil.isNotEmpty(contents)) {
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setData(contents);
		} else {
			result.setMsg(ResultCons.FAIL_MSG);
			result.setCode(ResultCons.FAIL);
			result.setData(contents);
		}

		return result;
	}

	public static ResultModel<Object> paymentNotifyVerify(PaymentsModel payment) {
		HttpServletRequest request = payment.getRequest();
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
			// 商户订单号
			String out_trade_no = new String(
					request.getParameter("out_trade_no"));
			// 支付宝交易号
			String trade_no = new String(request.getParameter("trade_no"));
			// 交易状态
			String trade_status = new String(
					request.getParameter("trade_status"));
			// 交易金额
			String total_fee = new String(request.getParameter("total_fee"));
			// 买家帐号
			String seller_email = new String(
					request.getParameter("seller_email"));
			// 付款时间
			String gmt_payment = new String(request.getParameter("gmt_payment"));
			rsMap.put("out_trade_no", out_trade_no);
			rsMap.put("trade_no", trade_no);
			rsMap.put("trade_status", trade_status);
			rsMap.put("total_fee", total_fee);
			rsMap.put("seller_email", seller_email);
			rsMap.put("gmt_payment", gmt_payment);
			// 计算得出通知验证结果
			boolean verify_result = AlipayAppNotify.verify(params);

			if (verify_result) {
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
			} else {
				result.setMsg(ResultCons.FAIL_MSG.concat("支付宝APP回调,参数验证失败。"));
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
