package sivl.platform.pay.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import sivl.platform.common.model.ResultModel;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.alipay.common.validate.ValidateAlipay;
import sivl.platform.pay.sdk.alipay.web.AlipayWebUtil;
import sivl.platform.pay.sdk.chinapay.common.validate.ValidateChinapay;
import sivl.platform.pay.sdk.chinapay.web.ChinaWebUtil;
import sivl.platform.pay.service.PaymentService;

@Service("chinapayWebService")
public class ChinapayWebServiceImpl implements PaymentService {

	public ResultModel<Object> payment(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 参数不为空验证
		PaymentsModel payment = new PaymentsModel(params);
		result = ValidateChinapay.validatePayment(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			// 调起支付支付
			result = ChinaWebUtil.payment(payment);
		}
		return result;
	}

	public ResultModel<Object> refundment(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		return result;
	}

	public ResultModel<Object> withdrawal(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		return result;
	}

	public ResultModel<Object> getResult(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		// 参数验证
		result = ValidateChinapay.validateQuery(params);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			Map<String, Object> map = result.getExt();
			// 调起查询
			result = ChinaWebUtil.query(map);
		}
		return result;
	}

	public ResultModel<Object> checking(Map<String, Object> params) {
		ResultModel<Object> result = new ResultModel<Object>();
		return result;
	}

}
