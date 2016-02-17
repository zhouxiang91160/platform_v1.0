package sivl.platform.pay.sdk.alipay.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.DateUtil;
import sivl.platform.common.utils.JSONUtil;
import sivl.platform.common.utils.MapUtil;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.constant.ResultCons.AlipayCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.model.RefundmentsModel;
import sivl.platform.pay.sdk.alipay.common.config.AlipayConfig;
import sivl.platform.pay.sdk.alipay.common.model.AlipayQueryParam;
import sivl.platform.pay.sdk.alipay.common.util.AlipayNotify;
import sivl.platform.pay.sdk.alipay.common.util.AlipaySubmit;
import sivl.platform.pay.sdk.alipay.common.util.UtilDate;
import sivl.platform.pay.sdk.alipay.common.util.XmlUtils;
import sivl.platform.pay.sdk.alipay.common.util.XstreamUtil;

public class AlipayWebUtil {

	/**
	 * 调用支付宝支付接口
	 * 
	 * @param payments
	 * @return
	 */
	public static ResultModel<Object> payment(PaymentsModel payments) {
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			Map<String, String> param = new HashMap<String, String>();
			// 把请求参数打包成数组
			param.put("service", AlipayConfig.service_payment);
			param.put("partner", AlipayConfig.partner);
			param.put("_input_charset", AlipayConfig.input_charset);
			param.put("payment_type", AlipayConfig.payment_type);
			param.put("return_url", AlipayConfig.web_payment_return);
			param.put("notify_url", AlipayConfig.web_payment_notify);
			param.put("seller_email", AlipayConfig.seller);
			param.put("out_trade_no", payments.getOutTradeNo());
			param.put("subject", payments.getSubject());
			param.put("total_fee", Double.toString(payments.getTradeFee()));
			param.put("body", payments.getBody());
			param.put("it_b_pay", payments.getOvertime());
			String table = AlipaySubmit.buildRequest(param, "post", "确认");
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setData(table);
		} catch (Exception e) {
			result.setCode(ResultCons.FAIL);
			result.setMsg("发起支付宝请求异常：" + e.getMessage());
		}
		return result;
	}

	/**
	 * 支付宝回调校验
	 * 
	 * @param payments
	 * @param is_notify
	 *            true--后台通知 false--前台通知
	 * @return
	 */
	public static ResultModel<Object> paymentVerify(PaymentsModel payments,
			boolean is_notify) {
		HttpServletRequest request = payments.getRequest();
		ResultModel<Object> result = new ResultModel<Object>();
		// 获取支付宝GET过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}

		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		Map<String, Object> rsMap = new HashMap<String, Object>();
		// 商户订单号
		String out_trade_no = new String(request.getParameter("out_trade_no"));
		rsMap.put("out_trade_no", out_trade_no);
		// 支付宝交易号
		String trade_no = new String(request.getParameter("trade_no"));
		rsMap.put("trade_no", trade_no);
		// 交易状态
		String trade_status = new String(request.getParameter("trade_status"));
		rsMap.put("trade_status", trade_status);
		// 交易金额
		String total_fee = new String(request.getParameter("total_fee"));
		rsMap.put("total_fee", total_fee);
		// 买家帐号
		String seller_email = new String(request.getParameter("seller_email"));
		rsMap.put("seller_email", seller_email);
		if (is_notify) {
			// 付款时间
			String gmt_payment = new String(request.getParameter("gmt_payment"));
			rsMap.put("gmt_payment", gmt_payment);
		}
		// 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		// 计算得出通知验证结果
		boolean verify_result = AlipayNotify.verify(params);

		if (verify_result) {
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
		} else {
			result.setMsg(ResultCons.FAIL_MSG.concat("支付宝回调,参数验证失败。"));
			result.setCode(ResultCons.FAIL);
		}
		result.setExt(rsMap);
		result.setData(rsMap);
		return result;
	}

	@SuppressWarnings("unchecked")
	public static ResultModel<Object> query(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		String trade_no = MapUtil.getString(params, "tradeNo");
		String out_trade_no = MapUtil.getString(params, "outTradeNo");
		try {
			// 把请求参数打包成数组
			Map<String, String> requestMap = new HashMap<String, String>();
			requestMap.put("service", "single_trade_query");
			requestMap.put("partner", AlipayConfig.partner);
			requestMap.put("_input_charset", AlipayConfig.input_charset);
			if (StringUtil.isNotEmpty(trade_no)) {
				requestMap.put("trade_no", trade_no);
			}
			if (StringUtil.isNotEmpty(out_trade_no)) {
				requestMap.put("out_trade_no", out_trade_no);
			}
			// 建立请求
			String xml = AlipaySubmit.buildRequest("", "", requestMap);
			// 解析返回xml
			AlipayQueryParam rsxml = XstreamUtil.XML2Bean(xml,
					AlipayQueryParam.class);
			if (rsxml != null) {
				String is_success = rsxml.getIsSuccess();
				// T：代表成功
				if (is_success.equals(AlipayCons.IS_SUCCESS)) {
					Map<String, Object> rsmap = new HashMap<String, Object>();
					rsmap.put("trade_no", rsxml.getResponse().getTrade()
							.getTrade_no());
					rsmap.put("out_trade_no", rsxml.getResponse().getTrade()
							.getOut_trade_no());
					rsmap.put("buyer_alis", rsxml.getResponse().getTrade()
							.getBuyer_email());
					rsmap.put("trade_time", rsxml.getResponse().getTrade()
							.getGmt_payment());
					rsmap.put("trade_status", rsxml.getResponse().getTrade()
							.getTrade_status());
					rsmap.put("total_fee", rsxml.getResponse().getTrade()
							.getTotal_fee());
					result.setMsg(ResultCons.SUCCESS_MSG);
					result.setCode(ResultCons.SUCCESS);
					result.setExt(rsmap);
					result.setData(rsmap);
				} else {
					result.setMsg(ResultCons.FAIL_MSG.concat(rsxml.getError()));
					result.setCode(ResultCons.FAIL);
				}
			} else {
				result.setMsg(ResultCons.FAIL_MSG.concat(":解析结果xml异常"));
				result.setCode(ResultCons.FAIL);
			}
		} catch (Exception e) {
			result.setMsg(ResultCons.FAIL_MSG.concat(":" + e.getMessage()));
			result.setCode(ResultCons.FAIL);
		}

		return result;
	}
	
	public static ResultModel<Object> refundment(RefundmentsModel refundment) {
		ResultModel<Object> result = new ResultModel<Object>();
		StringBuilder detail_data = new StringBuilder(refundment.getTrade_no());
		detail_data.append("^");
		detail_data.append(Double.valueOf(refundment.getTotal_fee()).toString());
		detail_data.append("^协商退款");
		System.out.println("detail_data="+detail_data.toString());
		// 把请求参数打包成数组
		Map<String, String> requestParam = new HashMap<String, String>();
		requestParam.put("service", "refund_fastpay_by_platform_pwd");
		requestParam.put("partner", AlipayConfig.partner);
		requestParam.put("_input_charset", AlipayConfig.input_charset);
		requestParam.put("notify_url", AlipayConfig.refundment_notify);
		requestParam.put("seller_email", AlipayConfig.seller);
		requestParam.put("refund_date", UtilDate.getDateFormatter());
		//当日退款笔数不能超过1000笔，有此需求，需重写batch_no生成方法
		requestParam.put("batch_no", UtilDate.getDate().concat(UtilDate.getThree()));
		requestParam.put("batch_num", "1");
		requestParam.put("detail_data", detail_data.toString());
		// 建立请求
		String res = AlipaySubmit.buildRequest(requestParam, "get", "确认");
		if(StringUtil.isEmpty(res)){
			result.setMsg(ResultCons.FAIL_MSG);
			result.setCode(ResultCons.FAIL);
		}else{
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setData(res);
		}
		return result;
	}

	public static ResultModel<Object> refundmentVerify(
			HttpServletRequest request, HttpServletResponse response) {
		ResultModel<Object> result = new ResultModel<Object>();
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			params.put(name, valueStr);
		}
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//批次号
		String batch_no = new String(request.getParameter("batch_no"));
		//批量退款数据中转账成功的笔数
		String success_num = new String(request.getParameter("success_num"));
		//批量退款数据中的详细信息
		String result_details = new String(request.getParameter("result_details"));
		//退款成功，解析数据
		Map<String,Object> rsmap = new HashMap<String, Object>();
		rsmap.put("batch_no", batch_no);
		if(Integer.parseInt(success_num)>0){
			String[] _result_details = result_details.split("\\^");
			rsmap.put("trade_no", _result_details[0]);
			rsmap.put("total_fee", _result_details[1]);
			rsmap.put("trade_status", _result_details[2]);
		}
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
		if(AlipayNotify.verify(params)){//验证成功
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setData(rsmap);
			result.setExt(rsmap);
		}else{
			//验证失败
			result.setMsg(ResultCons.FAIL_MSG+":验证失败");
			result.setCode(ResultCons.FAIL);
			result.setData(rsmap);
			result.setExt(rsmap);
		}
		return result;
	}

}
