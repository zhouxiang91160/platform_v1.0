package sivl.platform.pay.constant;

/**
 * 结果常量
 * @author Zhouxiang
 *
 */
public class ResultCons {
	
	public static String SUCCESS = "S";
	public static String FAIL = "F";
	
	public static String SUCCESS_MSG = "返回成功";
	public static String FAIL_MSG = "返回失败";
	
	public interface AlipayCons{
		//交易结束，无法进行后续操作（如：退款）
		public static String TRADE_FINISHED ="TRADE_FINISHED";
		//交易成功，可进行后续操作（如：退款）
		public static String TRADE_SUCCESS ="TRADE_SUCCESS";
		//查询成功
		public static String IS_SUCCESS = "T";
		//退款通知成功状态
		public static String REFUND_NOTIFY_STATUS = "SUCCESS";
	}
	
	public interface ChinapayCons{
		//交易结束，无法进行后续操作（如：退款）
		public static String respCode ="00";
		//交易成功，可进行后续操作（如：退款）
		public static String respMsg ="交易成功";
	}
	
	public interface TenpayCons{
		//即时交易
		public static String TRADE_MODE_1 = "1";
		//交易成功
		public static String TRADE_SUCCESS = "0";
	}

}
