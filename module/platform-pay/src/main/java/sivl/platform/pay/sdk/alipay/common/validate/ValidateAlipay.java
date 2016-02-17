package sivl.platform.pay.sdk.alipay.common.validate;

import java.util.Map;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.MapUtil;
import sivl.platform.common.utils.StringUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.enums.Channel;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.model.RefundmentsModel;
import sivl.platform.pay.sdk.alipay.common.config.AlipayConfig;

public class ValidateAlipay {

	/**
	 * 支付参数校验
	 * 
	 * @param payment
	 *            支付对象
	 * @return result
	 */
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
		String body = payment.getBody();
		if (StringUtil.isNotEmpty(body) && body.length() > 20) {
			body = body.substring(0, 20).concat("...");
			payment.setBody(body);
		}
		String overTime = payment.getOvertime();
		if (StringUtil.isEmpty(overTime)) {
			if (payment.getChannel() == Channel.ALIPAY_WAP.getCode()) {
				payment.setOvertime(AlipayConfig.pay_expire);
			} else {
				payment.setOvertime(AlipayConfig.it_b_pay);
			}
		} else {
			if (payment.getChannel() == Channel.ALIPAY_WAP.getCode()) {
				payment.setOvertime(overTime);
			} else {
				payment.setOvertime(overTime.concat("m"));
			}
		}
		return result;
	}

	public static ResultModel<Object> validateQuery(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		String out_trade_no = MapUtil.getString(params, "out_trade_no");
		String trade_no = MapUtil.getString(params, "trade_no");
		if (StringUtil.isEmpty(out_trade_no) && StringUtil.isEmpty(trade_no)) {
			result.setMsg(ResultCons.FAIL_MSG.concat(":参数异常，不能为空"));
			result.setCode(ResultCons.FAIL);
		} else {
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setExt(params);
			result.setData(params);
		}
		return result;
	}

	// 支付宝退款参数校验
	public static ResultModel<Object> validateRefundment(
			RefundmentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		if(StringUtil.isEmpty(payment.getTrade_no())
				||StringUtil.isEmpty(payment.getTotal_fee())){
			result.setMsg(ResultCons.FAIL_MSG.concat(":参数异常，不能为空"));
			result.setCode(ResultCons.FAIL);
		}else{
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
		}
		
		return result;
	}
}
