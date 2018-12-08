package zyxhj.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * id生成帮助类
 * 
 */
public class IDUtils {

	private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

	// 随机数种子
	private static Random random;

	// 六位随机数模版
	private static DecimalFormat decimalFormat6;
	// 四位随机数模版
	private static DecimalFormat decimalFormat4;

	static {
		random = new Random();
		decimalFormat6 = new DecimalFormat("000000");// 6位数字
		decimalFormat4 = new DecimalFormat("0000");// 4位数字
	}

	/**
	 * 获取6位的随机数字验证码
	 */
	public static String getNumValidCode6() {
		return decimalFormat6.format(random.nextInt(1000000));
	}

	/**
	 * 获取4位的随机数字验证码
	 */
	public static String getNumValidCode4() {
		return decimalFormat4.format(random.nextInt(10000));
	}

	/**
	 * 获取SimpleId(简单客户端ID)</br>
	 * 因为JS规范中，能精准表示的最大整数是 2的53次方，即53位（一个符号位，52个数字位）</br>
	 * 以上参见http://www.cnblogs.com/snandy/p/4943138.html</br>
	 * 因此我们控制SimpleId，只使用long型的52位，以便直接跟js无缝对接</br>
	 * 日期换算后，我们占用44位表示时间(当前时间是42位左右,我有生之年无法溢出了)，剩余8位作为自增位</br>
	 */
	public static long getSimpleId() {

		// 右移8位，让出低8位用于存放自增计数
		long ntime = System.currentTimeMillis() << 8;
		// 获取整形计数器的后8位
		int ncounter = NEXT_COUNTER.getAndIncrement() & 0x000000ff;
		// 合并时间和随机数
		return ntime ^ ncounter;
	}

	public static String getHexSimpleId() {
		return Long.toHexString(getSimpleId());
	}

	/**
	 * 获取SimpleId中的时间戳
	 */
	public static long getTimeInSimpleId(long simpleId) {
		return (simpleId >>> 8);
	}

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {

		long time = System.currentTimeMillis();
		System.out.println(new Date(time).toGMTString());
		System.out.println(time);
		System.out.println("---time-len=---" + Long.toBinaryString(time).length());
		System.out.println("---time-maxlen=---" + Long.toBinaryString(99999999999999L).length());
		System.out.println("long-max=" + Long.MAX_VALUE);
		System.out.println("----------------------");

		long tid = getSimpleId();
		System.out.println("---xxx---" + Long.toString(tid));
		System.out.println("---xxx---" + Long.toHexString(tid));

		System.out.println("----------------------");
		System.out.println("t=" + Long.toBinaryString(time));
		long ntime = time << 8;// 位移4位
		System.out.println("n=" + Long.toBinaryString(ntime));

		int counter = NEXT_COUNTER.getAndIncrement();
		System.out.println("oc=" + Integer.toBinaryString(counter));
		int ncounter = counter & 0x00000fff;
		System.out.println("nc=" + Integer.toBinaryString(ncounter));

		long result = ntime ^ ncounter;
		System.out.println("x=" + Long.toBinaryString(result));
		System.out.println("f=" + Long.toBinaryString(0xffffffffffffffffL));

		long nr = result >>> 8;
		System.out.println("x=" + Long.toBinaryString(nr));
		System.out.println(new Date(nr).toGMTString());

		long simpleId = getSimpleId();
		long simpleTime = getTimeInSimpleId(simpleId);
		System.out.println(simpleId);
		System.out.println(simpleTime);

		System.out.println(Long.toHexString(simpleId));

		System.out.println("----------------------end");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output out = new Output(baos);
		try {
			out.writeLong(simpleId);

			out.flush();

			byte[] data = baos.toByteArray();
			out.close();
			baos.close();

			System.out.println(CodecUtils.byte2Simple64(data));
			System.out.println(Long.toHexString(simpleId));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
