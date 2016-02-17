package sivl.platform.pay.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import sivl.platform.common.model.NetLogModel;
import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.JSONUtil;
import sivl.platform.common.utils.LogUtil;
import sivl.platform.common.utils.MapUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.constant.ResultCons.TenpayCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.tenpay.web.TenpayWebServiceUtil;

@Controller
@RequestMapping("/tenpay/web")
public class TenpayWebController extends PaymentController {

	private static String className = "sivl.platform.pay.controller.TenpayWebController";

	/**
	 * 财付通支付前台通知
	 * 
	 * @param response
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/front_payment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void returnUrl(HttpServletResponse response,
			HttpServletRequest request) {
		// 记录日志
		LogUtil.netLegalLog(
				JSONUtil.obj2json(new NetLogModel(request)
						.buildClassName(className)
						.buildInterfaceName("/tenpay/web/front_payment")
						.buildMsg("财付通支付前台通知")), TenpayWebController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		payment.setResponse(response);
		ResultModel<Object> result = TenpayWebServiceUtil
				.paymentFrontVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			Map<String, Object> rs = (Map<String, Object>) result.getData();
			String trade_status = MapUtil.getString(rs, "trade_state");
			if (trade_status.equals(TenpayCons.TRADE_SUCCESS)) {
				// 付款成功，商户逻辑处理
				System.out.println("付款成功，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result);
			} else {
				// 付款失败，商户逻辑处理
				System.out.println("付款失败，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result);
			}
		}
	}

	/**
	 * 财付通支付后台通知
	 * 
	 * @param response
	 * @param request
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/back_payment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void notifyUrl(HttpServletResponse response,
			HttpServletRequest request) {
		// 记录日志
		LogUtil.netLegalLog(
				JSONUtil.obj2json(new NetLogModel(request)
						.buildClassName(className)
						.buildInterfaceName("/tenpay/web/back_payment")
						.buildMsg("财付通支付后台通知")), TenpayWebController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		payment.setResponse(response);
		ResultModel<Object> result = TenpayWebServiceUtil
				.paymentBackVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			Map<String, Object> rs = (Map<String, Object>) result.getData();
			String trade_status = MapUtil.getString(rs, "trade_state");
			if (trade_status.equals(TenpayCons.TRADE_SUCCESS)) {
				// 付款成功，商户逻辑处理
				System.out.println("付款成功，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result);
			} else {
				// 付款失败，商户逻辑处理
				System.out.println("付款失败，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result);
			}
		}
	}
}
