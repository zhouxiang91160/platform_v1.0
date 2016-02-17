package sivl.platform.pay.model;

import java.util.Map;

import sivl.platform.common.utils.MapUtil;

public class RefundmentsModel {

	private String trade_no;// 第三方交易流水号
	private String out_trade_no;// 商户订单号
	private String total_fee;// 退款交易金额
	private String trade_time;// 退款交易时间
	private String trade_pwd;// 退款密码
	// 扩展
	private Map<String, Object> params;// 参数

	public String getTrade_no() {
		return trade_no;
	}

	public void setTrade_no(String trade_no) {
		this.trade_no = trade_no;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getTrade_time() {
		return trade_time;
	}

	public void setTrade_time(String trade_time) {
		this.trade_time = trade_time;
	}

	public String getTrade_pwd() {
		return trade_pwd;
	}

	public void setTrade_pwd(String trade_pwd) {
		this.trade_pwd = trade_pwd;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public RefundmentsModel() {

	}

	public RefundmentsModel(Map<String, Object> params) {
		this.params = params;
		this.trade_no = MapUtil.getString(params, "trade_no");
		this.out_trade_no = MapUtil.getString(params, "out_trade_no");
		this.total_fee = MapUtil.getString(params, "total_fee");
		this.trade_time = MapUtil.getString(params, "trade_time");
		this.trade_pwd = MapUtil.getString(params, "trade_pwd");
	}

}
