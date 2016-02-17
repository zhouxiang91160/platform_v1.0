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
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.chinapay.web.ChinaWebUtil;

@Controller
@RequestMapping("/chinapay/web")
public class ChinaWebController extends PaymentController{
	
	private static String className = "sivl.platform.pay.controller.ChinaWebController";
	
	/**
	 * 银联卡支付前台通知
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
						.buildInterfaceName("/chinapay/web/front_payment")
						.buildMsg("银联卡支付前台通知")), ChinaWebController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		ResultModel<Object> result = ChinaWebUtil.paymentVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			Map<String, Object> rs =  (Map<String, Object>)result.getData();
			String trade_status = MapUtil.getString(rs, "respCode");
			if (trade_status.equals(ResultCons.ChinapayCons.respCode)) {
				// 付款成功，商户逻辑处理
				System.out.println("付款成功，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result);
			} else {
				// 付款失败，商户逻辑处理
				System.out.println("付款失败，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result.getMsg());
			}
		}
	}
	
	/**
	 * 银联卡支付后台通知
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
						.buildInterfaceName("/chinapay/web/back_payment")
						.buildMsg("银联卡支付后台通知")), ChinaWebController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		ResultModel<Object> result = ChinaWebUtil.paymentVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			Map<String, Object> rs = (Map<String, Object>)result.getData();
			String trade_status = MapUtil.getString(rs, "respCode");
			if (trade_status.equals(ResultCons.ChinapayCons.respCode)) {
				// 付款成功，商户逻辑处理
				System.out.println("付款成功，商户逻辑处理:" + JSONUtil.obj2json(rs));
				String tradeStatus = ResultCons.SUCCESS;
				String outTradeNo = MapUtil.getString(rs, "orderId");
				String tradeNo = MapUtil.getString(rs, "queryId");
				String tradeFee = MapUtil.getString(rs, "txnAmt");//单位分
				String buyer = MapUtil.getString(rs, "accNo");
				String gmtPayment = MapUtil.getString(rs, "traceTime");
				writerJson(response, "ok");
			} else {
				// 付款失败，商户逻辑处理
				System.out.println("付款失败，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, result.getMsg());
			}
		}
	}

	
}
