package sivl.platform.pay.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import sivl.platform.common.model.ResultModel;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.model.RefundmentsModel;
import sivl.platform.pay.sdk.alipay.common.validate.ValidateAlipay;
import sivl.platform.pay.sdk.alipay.wap.AlipayWapUtil;
import sivl.platform.pay.sdk.alipay.web.AlipayWebUtil;
import sivl.platform.pay.service.PaymentService;

@Service("alipayWapService")
public class AlipayWapServiceImpl implements PaymentService {

	/**
	 * 发起支付接口
	 */
	public ResultModel<Object> payment(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 参数不为空验证
		PaymentsModel payment = new PaymentsModel(params);
		result = ValidateAlipay.validatePayment(payment);
		if(result.getCode().equals(ResultCons.SUCCESS)){
			//调起支付支付
			result = AlipayWapUtil.payment(payment);
		}
		return result;
	}

	public ResultModel<Object> refundment(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 参数不为空验证
		RefundmentsModel refundment = new RefundmentsModel(params);
		result = ValidateAlipay.validateRefundment(refundment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			// 调起支付支付
			result = AlipayWebUtil.refundment(refundment);
		}
		return result;
	}

	public ResultModel<Object> withdrawal(Map<String, Object> params) {
		return null;
	}

	public ResultModel<Object> getResult(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 参数验证
		result = ValidateAlipay.validateQuery(params);
		if(result.getCode().equals(ResultCons.SUCCESS)){
			Map<String,Object> map = result.getExt();
			//调起支付支付
			result = AlipayWebUtil.query(map);
		}
		return result;
	}

	public ResultModel<Object> checking(Map<String, Object> params) {
		return null;
	}

}
