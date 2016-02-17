package sivl.platform.pay.sdk.chinapay.web;

import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import sivl.platform.common.model.ResultModel;
import sivl.platform.common.utils.MapUtil;
import sivl.platform.pay.constant.ResultCons;
import sivl.platform.pay.constant.ResultCons.ChinapayCons;
import sivl.platform.pay.model.PaymentsModel;
import sivl.platform.pay.sdk.chinapay.common.config.ChinapayConfig;
import sivl.platform.pay.sdk.chinapay.common.utils.SDKConfig;
import sivl.platform.pay.sdk.chinapay.common.utils.SDKUtil;

public class ChinaWebUtil {

	/**
	 * 支付交易
	 * 
	 * @param payment
	 * @return
	 */

	public static ResultModel<Object> payment(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		try {
			Map<String, String> requestData = new HashMap<String, String>();

			/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
			requestData.put("version", SDKUtil.version); // 版本号，全渠道默认值
			requestData.put("encoding", SDKUtil.encoding_UTF8); // 字符集编码，可以使用UTF-8,GBK两种方式
			requestData.put("signMethod", "01"); // 签名方法，只支持 01：RSA方式证书加密
			requestData.put("txnType", "01"); // 交易类型 ，01：消费
			requestData.put("txnSubType", "01"); // 交易子类型， 01：自助消费
			requestData.put("bizType", "000201"); // 业务类型，B2C网关支付，手机wap支付
			requestData.put("channelType", "07"); // 渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板
													// 08：手机
			/*** 商户接入参数 ***/
			requestData.put("merId", ChinapayConfig.merId); // 商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
			requestData.put("accessType", "0"); // 接入类型，0：直连商户
			requestData.put("orderId", payment.getOutTradeNo()); // 商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
			requestData.put("txnTime", ChinapayConfig.getCurrentTime()); // 订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
			requestData.put("currencyCode", "156"); // 交易币种（境内商户一般是156 人民币）
			requestData.put("txnAmt",
					new DecimalFormat("#").format(payment.getTradeFee())); // 交易金额，单位分，不要带小数点
			requestData.put("reqReserved", payment.getBody()); // 请求方保留域，透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节
			requestData.put("payTimeout", payment.getOvertime());
			requestData.put("frontUrl", ChinapayConfig.frontUrl);

			// 5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d
			// 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
			requestData.put("backUrl", ChinapayConfig.backUrl);

			/** 请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面 **/
			Map<String, String> submitFromData = SDKUtil.signData(requestData,
					SDKUtil.encoding_UTF8); // 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

			String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl(); // 获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
			String html = SDKUtil.createAutoFormHtml(requestFrontUrl,
					submitFromData, SDKUtil.encoding_UTF8); // 生成自动跳转的Html表单
			result.setMsg(ResultCons.SUCCESS_MSG);
			result.setCode(ResultCons.SUCCESS);
			result.setData(html);
		} catch (Exception e) {
			result.setMsg("银联支付接口异常：" + e.getMessage());
			result.setCode(ResultCons.FAIL);
		}

		return result;
	}

	/**
	 * 交易返回参数验证
	 * 
	 * @param payment
	 * @return
	 */
	public static ResultModel<Object> paymentVerify(PaymentsModel payment) {
		ResultModel<Object> result = new ResultModel<Object>();
		Map<String, String> respParam = getAllRequestParam(payment.getRequest());
		Map<String, String> valideData = null;
		if (null != respParam && !respParam.isEmpty()) {
			Iterator<Entry<String, String>> it = respParam.entrySet()
					.iterator();
			valideData = new HashMap<String, String>(respParam.size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				valideData.put(key, value);
			}
		}
		if (!SDKUtil.validate(valideData, ChinapayConfig.encoding_utf8)) {
			result.setMsg(ResultCons.FAIL_MSG.concat(",验证签名结果:失败"));
			result.setCode(ResultCons.FAIL);
			result.setData(valideData);
		} else {
			result.setMsg(ResultCons.SUCCESS_MSG.concat(",验证签名结果:成功"));
			result.setCode(ResultCons.SUCCESS);
			result.setData(valideData);
		}

		return result;
	}

	/**
	 * 获取请求参数中所有的信息
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> getAllRequestParam(
			final HttpServletRequest request) {
		Map<String, String> res = new HashMap<String, String>();
		Enumeration<?> temp = request.getParameterNames();
		if (null != temp) {
			while (temp.hasMoreElements()) {
				String en = (String) temp.nextElement();
				String value = request.getParameter(en);
				res.put(en, value);
				// 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
				if (res.get(en) == null || "".equals(res.get(en))) {
					res.remove(en);
				}
			}
		}
		return res;
	}

	public static ResultModel<Object> query(Map<String, Object> map) {
		ResultModel<Object> result = new ResultModel<Object>();
		Map<String, String> data = new HashMap<String, String>();
		/*** 银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改 ***/
		data.put("version", SDKUtil.version); // 版本号
		data.put("encoding", SDKUtil.encoding_UTF8); // 字符集编码 可以使用UTF-8,GBK两种方式
		data.put("signMethod", "01"); // 签名方法 目前只支持01-RSA方式证书加密
		data.put("txnType", "00"); // 交易类型 00-默认
		data.put("txnSubType", "00"); // 交易子类型 默认00
		data.put("bizType", "000201"); // 业务类型 B2C网关支付，手机wap支付
		/*** 商户接入参数 ***/
		data.put("merId", ChinapayConfig.merId); // 商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
		data.put("accessType", "0"); // 接入类型，商户接入固定填0，不需修改
		/*** 要调通交易以下字段必须修改 ***/
		data.put("orderId", MapUtil.getString(map, "out_trade_no")); // ****商户订单号，每次发交易测试需修改为被查询的交易的订单号
		data.put("txnTime", MapUtil.getString(map, "trade_time")); // ****订单发送时间，每次发交易测试需修改为被查询的交易的订单发送时间
		/** 请求参数设置完毕，以下对请求参数进行签名并发送http post请求，接收同步应答报文-------------> **/
		Map<String, String> submitFromData = SDKUtil.signData(data,
				SDKUtil.encoding_UTF8);// 报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
		String url = SDKConfig.getConfig().getSingleQueryUrl();
		// 发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过
		Map<String, String> resmap = SDKUtil.submitUrl(submitFromData, url,
				SDKUtil.encoding_UTF8);
		/** 对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考-------------> **/
		// 应答码规范参考open.unionpay.com帮助中心 下载 产品接口规范 《平台接入接口规范-第5部分-附录》
		if (resmap.get("respCode").equals(ChinapayCons.respCode)) {// 如果查询交易成功
			// 处理被查询交易的应答码逻辑
			if (resmap.get("origRespCode").equals(ChinapayCons.respCode)) {
				// 交易成功，更新商户订单状态
				result.setMsg(ResultCons.SUCCESS_MSG);
				result.setCode(ResultCons.SUCCESS);
			} else if (resmap.get("origRespCode").equals("03")
					|| resmap.get("origRespCode").equals("04")
					|| resmap.get("origRespCode").equals("05")) {
				// 需再次发起交易状态查询交易
				// TODO
			} else {
				// 其他应答码为失败请排查原因
				result.setMsg("code="+resmap.get("origRespCode")+",msg："+resmap.get("origRespMsg"));
				result.setCode(ResultCons.FAIL);
			}
		} else {// 查询交易本身失败，或者未查到原交易，检查查询交易报文要素
			result.setMsg("查询交易失败：code="+resmap.get("respCode")+",msg="+resmap.get("respMsg"));
			result.setCode(ResultCons.FAIL);
		}
		return result;
	}

}
