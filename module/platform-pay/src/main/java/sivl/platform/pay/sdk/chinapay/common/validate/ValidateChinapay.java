package sivl.platform.pay.sdk.chinapay.common.validate;

import java.util.Calendar;
import java.util.Map;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.DateUtil;
import sivl.platform.common.utils.MapUtil;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.model.PaymentsModel;

public class ValidateChinapay {

	public static ResultModel<Object> validatePayment(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		if (StringUtil.isEmpty(payment.getOutTradeNo())) {
			result.setMsg("商户订单号不能为空");
			result.setCode(ResultCons.FAIL);
		} else if (payment.getTradeFee() == null) {
			result.setMsg("订单金额不能为空");
			result.setCode(ResultCons.FAIL);
		} else if (payment.getTradeFee() <= 0) {
			result.setMsg("订单金额不能小于0");
			result.setCode(ResultCons.FAIL);
		} else {
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
		}
		// 金额转成分
		Double tradeFee = payment.getTradeFee();
		payment.setTradeFee(tradeFee * 100);
		// 超时支付
		String overtime = payment.getOvertime();
		if (StringUtil.isNotEmpty(overtime)) {
			Calendar currentTime = Calendar.getInstance();
			currentTime.add(Calendar.MINUTE, Integer.parseInt(overtime));
			String _overtime = DateUtil.fmtDateToStr(currentTime.getTime(),
					"yyyyMMddHHmmss");
			payment.setOvertime(_overtime);
		} else {
			Calendar currentTime = Calendar.getInstance();
			currentTime.add(Calendar.DATE, 30);
			String _overtime = DateUtil.fmtDateToStr(currentTime.getTime(),
					"yyyyMMddHHmmss");
			payment.setOvertime(_overtime);
		}
		if (StringUtil.isEmpty(payment.getBody())) {
			payment.setBody("银联渠道");
		}
		return result;
	}

	public static ResultModel<Object> validateQuery(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		String out_trade_no = MapUtil.getString(params, "out_trade_no");
		String trade_time = MapUtil.getString(params, "trade_time");
		if (StringUtil.isEmpty(out_trade_no)) {
			result.setMsg(ResultCons.FAIL_MSG.concat(":参数异常，商户订单号不能为空"));
			result.setCode(ResultCons.FAIL);
		} else if (StringUtil.isEmpty(trade_time)) {
			result.setMsg(ResultCons.FAIL_MSG.concat(":参数异常，订单交易时间不能为空"));
			result.setCode(ResultCons.FAIL);
		} else {
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setExt(params);
			result.setData(params);
		}
		return result;
	}
}
