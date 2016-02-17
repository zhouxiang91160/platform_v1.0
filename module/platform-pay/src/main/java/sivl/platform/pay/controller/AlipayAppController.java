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
import sivl.platform.pay.sdk.alipay.app.AlipayAppUtil;

@Controller
@RequestMapping("/alipay/app")
public class AlipayAppController extends PaymentController {

	private static String className = "sivl.platform.pay.controller.AlipayAppController";

	/**
	 * 支付宝支付前台通知
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/return_payment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void returnUrl(HttpServletResponse response,
			HttpServletRequest request) {}

	/**
	 * 支付宝支付异步通知
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/notify_payment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void notifyUrl(HttpServletResponse response,
			HttpServletRequest request) {
		// 记录日志
		LogUtil.netLegalLog(
				JSONUtil.obj2json(new NetLogModel(request)
						.buildClassName(className)
						.buildInterfaceName("/alipay/app/notify_payment")
						.buildMsg("支付宝APP支付后台通知")), AlipayAppController.class);
		PaymentsModel payment = new PaymentsModel();
		payment.setRequest(request);
		ResultModel<Object> result = AlipayAppUtil.paymentNotifyVerify(payment);
		if (result.getCode().equals(ResultCons.SUCCESS)) {
			Map<String, Object> rs = result.getExt();
			String trade_status = MapUtil.getString(rs, "trade_status");
			if (trade_status.equals(ResultCons.AlipayCons.TRADE_SUCCESS)
					|| trade_status
							.equals(ResultCons.AlipayCons.TRADE_FINISHED)) {
				// 付款成功，商户逻辑处理
				System.out.println("APP付款成功，商户逻辑处理:" + JSONUtil.obj2json(rs));
				String outTradeNo = MapUtil.getString(rs, "out_trade_no");
				String tradeNo = MapUtil.getString(rs, "trade_no");
				String tradeFee = MapUtil.getString(rs, "total_fee");
				String tradeStatus = MapUtil.getString(rs, "trade_status");
				String buyerEmail = MapUtil.getString(rs, "buyer_email");
				String gmtPayment = MapUtil.getString(rs, "gmt_payment");
				writerJson(response, "success");
			} else {
				// 付款失败，商户逻辑处理
				System.out.println("APP付款失败，商户逻辑处理:" + JSONUtil.obj2json(rs));
				writerJson(response, "fail");
			}
		}
	}

	/**
	 * 支付宝退款异步通知
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/notify_refundment", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void notifyUrlRefundment(HttpServletResponse response,
			HttpServletRequest request) {

	}

	/**
	 * 提现到支付异步通知
	 * 
	 * @param response
	 * @param request
	 */
	@RequestMapping(value = "/notify_cash", method = { RequestMethod.GET,
			RequestMethod.POST })
	public void notifyUrlWithdrawal(HttpServletResponse response,
			HttpServletRequest request) {

	}

}
