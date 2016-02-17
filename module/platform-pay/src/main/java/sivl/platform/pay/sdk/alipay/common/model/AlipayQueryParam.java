package sivl.platform.pay.sdk.alipay.common.model;

import java.util.List;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
//xml rootElement
@XStreamAlias("alipay")
public class AlipayQueryParam {

	/**基本参数*/
	//是否成功  T代表成功 F代表失败
    @XStreamAlias("is_success")
    private String isSuccess;
    //查询交易信息失败的错误代码
    @XStreamAlias("error")
    private String error;
    //请求参数
    @XStreamAlias("request")
    private Request request;
    //返回参数
    @XStreamAlias("response")
    private Response response;
     
    public static class Request{
         
        @XStreamImplicit(itemFieldName="param")
        private List<Param> param;
 
        public List<Param> getParam() {
            return param;
        }
 
        public void setParam(List<Param> param) {
            this.param = param;
        }
         
         
    }
     
    @XStreamAlias("param")
    public static class Param{
         
        @XStreamAsAttribute
        private String name;
        @XStreamAsAttribute
        private String content;
        
        public String getName() {
            return name;
        }
 
        public void setName(String name) {
            this.name = name;
        }
        
        public String getContent() {  
            return content;  
        }  
  
        public void setContent(String content) {  
            this.content = content;  
        }  
         
    }
     
     
    public static class Response{
         
        @XStreamAlias("trade")
        private Trade trade;

		public Trade getTrade() {
			return trade;
		}

		public void setTrade(Trade trade) {
			this.trade = trade;
		}
         
    }
     
     
     
    public static class Trade{
        //商品描叙
        @XStreamAlias("body")
        private String body;
        //买家支付宝账号
        @XStreamAlias("buyer_email")
        private String buyer_email;
        //买家支付宝账号对应的支付宝唯一用户号
        @XStreamAlias("buyer_id")
        private String buyer_id;
        //使用红包的金额
        @XStreamAlias("coupon_used_fee")
        private String coupon_used_fee;
        //折扣
        @XStreamAlias("discount")
        private String discount;
        //交易冻结状态 1表示冻结0表示未冻结
        @XStreamAlias("flag_trade_locked")
        private String flag_trade_locked;
        //交易创建时间
        @XStreamAlias("gmt_create")
        private String gmt_create;
        //交易关闭时间
        @XStreamAlias("gmt_close")
        private String gmt_close;
        //交易最近一次修改时间
        @XStreamAlias("gmt_last_modified_time")
        private String gmt_last_modified_time;
        //付款时间
        @XStreamAlias("gmt_payment")
        private String gmt_payment;
        //交易金额是否调整过
        @XStreamAlias("is_total_fee_adjust")
        private String is_total_fee_adjust;
        //交易的创建人角色 B：买家 S：卖家
        @XStreamAlias("operator_role")
        private String operator_role;
        //商户网站唯一订单号
        @XStreamAlias("out_trade_no")
        private String out_trade_no;
        //收款类型
        @XStreamAlias("payment_type")
        private String payment_type;
        //商品单价
        @XStreamAlias("price")
        private String price;
        //购买数量
        @XStreamAlias("quantity")
        private String quantity;
        //卖家的支付宝账号
        @XStreamAlias("seller_email")
        private String seller_email;
        //卖家支付宝账号对应的支付宝唯一用户号
        @XStreamAlias("seller_id")
        private String seller_id;
        //商品名称
        @XStreamAlias("subject")
        private String subject;
        //累计的已经退款金额
        @XStreamAlias("to_buyer_fee")
        private String to_buyer_fee;
        //累计的已打款给买家的金额
        @XStreamAlias("to_seller_fee")
        private String to_seller_fee;
        //交易总金额
        @XStreamAlias("total_fee")
        private String total_fee;
        //支付宝根据商户请求，创建订单生成的支付宝交易号
        @XStreamAlias("trade_no")
        private String trade_no;
        //交易状态
        @XStreamAlias("trade_status")
        private String trade_status;
        //是否使用过红包 使用过－T 未使用过－F
        @XStreamAlias("use_coupon")
        private String use_coupon;
        //退款状态
        @XStreamAlias("refund_status")
        private String refund_status;
        //退款时间
        @XStreamAlias("gmt_refund")
        private String gmt_refund;
        //退款金额
        @XStreamAlias("refund_fee")
        private String refund_fee;
        //退款流程
        @XStreamAlias("refund_flow_type")
        private String refund_flow_type;
        //邮费
        @XStreamAlias("logistics_fee")
        private String logistics_fee;
        //红包折扣
        @XStreamAlias("coupon_discount")
        private String coupon_discount;
        //物流状态
        @XStreamAlias("logistics_status")
        private String logistics_status;
        //交易附加状态
        @XStreamAlias("additional_trade_status")
        private String additional_trade_status;
        //卖家发货时间
        @XStreamAlias("gmt_send_goods")
        private String gmt_send_goods;
        //主超时时间
        @XStreamAlias("time_out")
        private String time_out;
        //物流状态更新时间
        @XStreamAlias("gmt_logistics_modify")
        private String gmt_logistics_modify;
        //主超时间类型
        @XStreamAlias("time_out_type")
        private String time_out_type;
        //退款ID
        @XStreamAlias("refund_id")
        private String refund_id;
        //退现金金额
        @XStreamAlias("refund_cash_fee")
        private String refund_cash_fee;
        //退红包金额
        @XStreamAlias("refund_coupon_fee")
        private String refund_coupon_fee;
        //退积分金额
        @XStreamAlias("refund_agent_pay_fee")
        private String refund_agent_pay_fee;
        //资金单据列表
        @XStreamAlias("fund_bill_list")
        private String fund_bill_list;
        
        public String getBody() {
            return body;
        }
        public void setBody(String body) {
            this.body = body;
        }
        public String getBuyer_email() {
            return buyer_email;
        }
        public void setBuyer_email(String buyer_email) {
            this.buyer_email = buyer_email;
        }
        public String getBuyer_id() {
            return buyer_id;
        }
        public void setBuyer_id(String buyer_id) {
            this.buyer_id = buyer_id;
        }
        public String getCoupon_used_fee() {
            return coupon_used_fee;
        }
        public void setCoupon_used_fee(String coupon_used_fee) {
            this.coupon_used_fee = coupon_used_fee;
        }
        public String getDiscount() {
            return discount;
        }
        public void setDiscount(String discount) {
            this.discount = discount;
        }
        public String getFlag_trade_locked() {
            return flag_trade_locked;
        }
        public void setFlag_trade_locked(String flag_trade_locked) {
            this.flag_trade_locked = flag_trade_locked;
        }
        public String getGmt_create() {
            return gmt_create;
        }
        public void setGmt_create(String gmt_create) {
            this.gmt_create = gmt_create;
        }
        public String getGmt_last_modified_time() {
            return gmt_last_modified_time;
        }
        public void setGmt_last_modified_time(String gmt_last_modified_time) {
            this.gmt_last_modified_time = gmt_last_modified_time;
        }
        public String getGmt_payment() {
            return gmt_payment;
        }
        public void setGmt_payment(String gmt_payment) {
            this.gmt_payment = gmt_payment;
        }
        public String getIs_total_fee_adjust() {
            return is_total_fee_adjust;
        }
        public void setIs_total_fee_adjust(String is_total_fee_adjust) {
            this.is_total_fee_adjust = is_total_fee_adjust;
        }
        public String getOperator_role() {
            return operator_role;
        }
        public void setOperator_role(String operator_role) {
            this.operator_role = operator_role;
        }
        public String getOut_trade_no() {
            return out_trade_no;
        }
        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }
        public String getPayment_type() {
            return payment_type;
        }
        public void setPayment_type(String payment_type) {
            this.payment_type = payment_type;
        }
        public String getPrice() {
            return price;
        }
        public void setPrice(String price) {
            this.price = price;
        }
        public String getQuantity() {
            return quantity;
        }
        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }
        public String getSeller_email() {
            return seller_email;
        }
        public void setSeller_email(String seller_email) {
            this.seller_email = seller_email;
        }
        public String getSeller_id() {
            return seller_id;
        }
        public void setSeller_id(String seller_id) {
            this.seller_id = seller_id;
        }
        public String getSubject() {
            return subject;
        }
        public void setSubject(String subject) {
            this.subject = subject;
        }
        public String getTo_buyer_fee() {
            return to_buyer_fee;
        }
        public void setTo_buyer_fee(String to_buyer_fee) {
            this.to_buyer_fee = to_buyer_fee;
        }
        public String getTo_seller_fee() {
            return to_seller_fee;
        }
        public void setTo_seller_fee(String to_seller_fee) {
            this.to_seller_fee = to_seller_fee;
        }
        public String getTotal_fee() {
            return total_fee;
        }
        public void setTotal_fee(String total_fee) {
            this.total_fee = total_fee;
        }
        public String getTrade_no() {
            return trade_no;
        }
        public void setTrade_no(String trade_no) {
            this.trade_no = trade_no;
        }
        public String getTrade_status() {
            return trade_status;
        }
        public void setTrade_status(String trade_status) {
            this.trade_status = trade_status;
        }
        public String getUse_coupon() {
            return use_coupon;
        }
        public void setUse_coupon(String use_coupon) {
            this.use_coupon = use_coupon;
        }
		public String getRefund_status() {
			return refund_status;
		}
		public void setRefund_status(String refund_status) {
			this.refund_status = refund_status;
		}
		public String getGmt_refund() {
			return gmt_refund;
		}
		public void setGmt_refund(String gmt_refund) {
			this.gmt_refund = gmt_refund;
		}
		public String getRefund_fee() {
			return refund_fee;
		}
		public void setRefund_fee(String refund_fee) {
			this.refund_fee = refund_fee;
		}
		public String getGmt_close() {
			return gmt_close;
		}
		public void setGmt_close(String gmt_close) {
			this.gmt_close = gmt_close;
		}
		public String getRefund_flow_type() {
			return refund_flow_type;
		}
		public void setRefund_flow_type(String refund_flow_type) {
			this.refund_flow_type = refund_flow_type;
		}
		public String getLogistics_fee() {
			return logistics_fee;
		}
		public void setLogistics_fee(String logistics_fee) {
			this.logistics_fee = logistics_fee;
		}
		public String getCoupon_discount() {
			return coupon_discount;
		}
		public void setCoupon_discount(String coupon_discount) {
			this.coupon_discount = coupon_discount;
		}
		public String getLogistics_status() {
			return logistics_status;
		}
		public void setLogistics_status(String logistics_status) {
			this.logistics_status = logistics_status;
		}
		public String getAdditional_trade_status() {
			return additional_trade_status;
		}
		public void setAdditional_trade_status(String additional_trade_status) {
			this.additional_trade_status = additional_trade_status;
		}
		public String getGmt_send_goods() {
			return gmt_send_goods;
		}
		public void setGmt_send_goods(String gmt_send_goods) {
			this.gmt_send_goods = gmt_send_goods;
		}
		public String getTime_out() {
			return time_out;
		}
		public void setTime_out(String time_out) {
			this.time_out = time_out;
		}
		public String getGmt_logistics_modify() {
			return gmt_logistics_modify;
		}
		public void setGmt_logistics_modify(String gmt_logistics_modify) {
			this.gmt_logistics_modify = gmt_logistics_modify;
		}
		public String getTime_out_type() {
			return time_out_type;
		}
		public void setTime_out_type(String time_out_type) {
			this.time_out_type = time_out_type;
		}
		public String getRefund_id() {
			return refund_id;
		}
		public void setRefund_id(String refund_id) {
			this.refund_id = refund_id;
		}
		public String getRefund_cash_fee() {
			return refund_cash_fee;
		}
		public void setRefund_cash_fee(String refund_cash_fee) {
			this.refund_cash_fee = refund_cash_fee;
		}
		public String getRefund_coupon_fee() {
			return refund_coupon_fee;
		}
		public void setRefund_coupon_fee(String refund_coupon_fee) {
			this.refund_coupon_fee = refund_coupon_fee;
		}
		public String getRefund_agent_pay_fee() {
			return refund_agent_pay_fee;
		}
		public void setRefund_agent_pay_fee(String refund_agent_pay_fee) {
			this.refund_agent_pay_fee = refund_agent_pay_fee;
		}
		public String getFund_bill_list() {
			return fund_bill_list;
		}
		public void setFund_bill_list(String fund_bill_list) {
			this.fund_bill_list = fund_bill_list;
		}
        
          
    }
     
    @XStreamAlias("sign")
    private String sign;
     
 
    @XStreamAlias("sign_type")
    private String sign_type;
 
    public String getIsSuccess() {
        return isSuccess;
    }
    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getSign() {
        return sign;
    }
    public void setSign(String sign) {
        this.sign = sign;
    }
    public String getSign_type() {
        return sign_type;
    }
    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }
    public Request getRequest() {
        return request;
    }
    public void setRequest(Request request) {
        this.request = request;
    }
    public Response getResponse() {
        return response;
    }
    public void setResponse(Response response) {
        this.response = response;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
     
}
