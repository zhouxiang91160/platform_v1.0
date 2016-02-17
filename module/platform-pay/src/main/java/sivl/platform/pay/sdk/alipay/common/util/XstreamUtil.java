package sivl.platform.pay.sdk.alipay.common.util;

import java.io.IOException;
import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Xstream解析xml工具类
 * @author zhouxiang
 * @version 1.0
 */
public class XstreamUtil {

	/**
	 * xml文件
	 * @param input InputStream
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static <T> T XML2Bean(InputStream input, Class<T> clazz)
			throws IOException {

		XStream xstream = new XStream(new DomDriver());

		xstream.processAnnotations(clazz);

		return (T) xstream.fromXML(input);
	}
	/**
	 * xml字符串
	 * @param input  String
	 * @param clazz
	 * @return
	 * @throws IOException
	 */
	public static <T> T XML2Bean(String input, Class<T> clazz)
			throws IOException {

		XStream xstream = new XStream(new DomDriver());

		xstream.processAnnotations(clazz);

		return (T) xstream.fromXML(input);
	}
}
