package sivl.platform.pay.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sivl.platform.common.model.NetLogModel;
import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.JSONUtil;
import sivl.platform.common.utils.LogUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.tenpay.wap.TenpayWapServiceUtil;

@Controller
@RequestMapping("/tenpay/wap")
public class TenpayWapController extends PaymentController {

	private static String className = "sivl.platform.pay.controller.TenpayWapController";

	/**
	 * 财付通支付前台通知
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/front_payment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void returnUrl(HttpServletResponse response,
			HttpServletRequest request) {
		// 记录日志
		LogUtil.netLegalLog(
				JSONUtil.obj2json(new NetLogModel(request)
						.buildClassName(className)
						.buildInterfaceName("/tenpay/wap/front_payment")
						.buildMsg("财付通支付前台通知")), TenpayWapController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		payment.setResponse(response);
		ResultModel<Object> result = TenpayWapServiceUtil
				.paymentVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			System.out.println("付款成功，商户逻辑处理:" + JSONUtil.obj2json(result));
			// TODO 页面跳转
			writerJson(response, result);
		} else {
			// 付款失败，商户逻辑处理
			System.out.println("付款失败，商户逻辑处理:" + JSONUtil.obj2json(result));
			// TODO 页面跳转
			writerJson(response, result);
		}
	}

	/**
	 * 财付通支付后台通知
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/back_payment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void notifyUrl(HttpServletResponse response,
			HttpServletRequest request) {
		// 记录日志
		LogUtil.netLegalLog(
				JSONUtil.obj2json(new NetLogModel(request)
						.buildClassName(className)
						.buildInterfaceName("/tenpay/wap/back_payment")
						.buildMsg("财付通支付后台通知")), TenpayWapController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		payment.setResponse(response);
		ResultModel<Object> result = TenpayWapServiceUtil
				.paymentVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			System.out.println("付款成功，商户逻辑处理:" + JSONUtil.obj2json(result));
			writerJson(response, "success");
		} else {
			// 付款失败，商户逻辑处理
			System.out.println("付款失败，商户逻辑处理:" + JSONUtil.obj2json(result));
			writerJson(response, "fail");
		}
	}
}
