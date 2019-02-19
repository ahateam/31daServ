package zyxhj.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;

/**
 * 编解码常用工具集</br>
 * 
 * HamcSha1，md5，AES，UrlEncode</br>
 * 
 */
public class CodecUtils {

	private static Logger log = LoggerFactory.getLogger(CodecUtils.class);

	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	public static final String ENCODING_UTF8 = "UTF-8";

	private static final String HMACSHA1 = "HmacSHA1";

	private static URLCodec urlCodec = new URLCodec(ENCODING_UTF8);

	private static final String AES = "AES";

	/**
	 * 使用 HMAC-SHA1 签名方法对对src进行签名，然后转换为url可传输的HEX形式
	 * 
	 * @param src
	 *            被签名的字符串
	 * @param secret
	 *            密钥
	 * @return
	 * @throws Exception
	 */
	public static String verifyByHmacSHA1ToHEX(String src, String secret) throws Exception {
		byte[] hmacsha1 = hamcsha1(src.getBytes(CHARSET_UTF8), secret);
		return Hex.encodeHexString(hmacsha1);
	}

	public static byte[] hamcsha1(byte[] data, String secret) throws Exception {
		// 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(secret.getBytes(CHARSET_UTF8), HMACSHA1);
		// 生成一个指定 Mac 算法 的 Mac 对象
		Mac mac = Mac.getInstance(HMACSHA1);
		// 用给定密钥初始化 Mac 对象
		mac.init(secretKey);

		return mac.doFinal(data);
	}

	public static byte[] byte2Md5(byte[] data) throws Exception {
		MessageDigest mdInst = MessageDigest.getInstance("MD5");
		mdInst.update(data);
		return mdInst.digest();
	}

	public static String md52Hex(byte[] data) throws Exception {
		return Hex.encodeHexString(byte2Md5(data));
	}

	public static String md52Hex(String text, Charset charset) throws Exception {
		return Hex.encodeHexString(byte2Md5(text.getBytes(charset)));
	}

	public static String byte2Hex(byte[] data) {
		return Hex.encodeHexString(data);
	}

	public static byte[] hex2Byte(String hex) throws Exception {
		return Hex.decodeHex(hex.toCharArray());
	}

	public static String byte2Base64(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
	}

	public static byte[] base642Byte(String base64) {
		return Base64.getDecoder().decode(base64);
	}

	public static String byte2Simple64(byte[] data) {
		return Simple64.encodeToString(data);
	}

	public static byte[] simple642Byte(String id64) {
		return Simple64.decodeFromString(id64);
	}

	/**
	 * 获取密钥
	 *
	 * @param password
	 *            加密密码
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static SecretKeySpec getKey(String password) throws NoSuchAlgorithmException {
		// 密钥加密器生成器
		KeyGenerator kgen = KeyGenerator.getInstance(AES);
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");// 需要自己手动设置
		random.setSeed(password.getBytes());
		kgen.init(128, random);

		// 创建加密器
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();

		SecretKeySpec key = new SecretKeySpec(enCodeFormat, AES);

		return key;
	}

	public static String AESEncode2ID64URLSafeString(String data, String password) throws Exception {
		Cipher cipher = Cipher.getInstance(AES);
		byte[] byteContent = data.getBytes(ENCODING_UTF8);

		cipher.init(Cipher.ENCRYPT_MODE, getKey(password));
		byte[] result = cipher.doFinal(byteContent);

		return Simple64.encodeToString(result);
	}

	public static String AESDecodeByID64URLSafeString(String data, String password) throws Exception {
		byte[] content = Simple64.decodeFromString(data);

		Cipher cipher = Cipher.getInstance(AES);
		cipher.init(Cipher.DECRYPT_MODE, getKey(password));

		byte[] result = cipher.doFinal(content);
		return new String(result, ENCODING_UTF8);
	}

	/**
	 * URLEncoding 编码
	 * 
	 * @param src
	 * @return 编码结果
	 * @throws EncoderException
	 * @throws Exception
	 */
	public static String urlEncode(String src) {
		try {
			return urlCodec.encode(src);
		} catch (EncoderException e) {
			log.error(e.getMessage(), e);
			return src;
		}
	}

	/**
	 * URLEncoding解码
	 * 
	 * @param src
	 * @return 解码结果
	 * @throws DecoderException
	 * @throws Exception
	 */
	public static String urlDecode(String src) {
		try {
			return urlCodec.decode(src);
		} catch (DecoderException e) {
			log.error(e.getMessage(), e);
			return src;
		}
	}

	/**
	 * 用于将由特殊分隔符拼接的编号字符串，分割为Long型列表
	 * 
	 * @param src
	 *            分割前的字符串
	 * @param spliter
	 *            分隔符，一般是英文逗号
	 * @return 分割好的编号（Long型）列表
	 */
	public static List<Long> splitString2LongId(String src, char spliter) {
		String[] strIds = StringUtils.split(src, spliter);
		List<Long> ids = new ArrayList<>();
		for (String strId : strIds) {
			ids.add(Long.parseLong(strId));
		}
		return ids;
	}

	/**
	 * 用于将字符串类型的列表，转换成Long型的id列表
	 * 
	 * @param src
	 *            转换前的源字符串列表
	 * @return 转换后的Long型列表
	 */
	public static List<Long> convertStringIds2LongIds(List<String> src) {
		List<Long> ids = new ArrayList<>();
		for (String id : src) {
			ids.add(Long.parseLong(id));
		}
		return ids;
	}

	/**
	 * 用于将Long型编号列表，转换成String型编号数组
	 * 
	 * @param src
	 *            转换前的Long型列表
	 * @return 转换后的String型数组
	 */
	public static String[] convertLongIds2StringIds(List<Long> src) {
		String[] ret = new String[src.size()];
		int i = 0;
		for (Long l : src) {
			ret[i] = Long.toString(l);
			i++;
		}
		return ret;
	}

	/**
	 * 将逗号分隔的字符串数组转换成JSONArray
	 */
	public static JSONArray convertCommaStringList2JSONArray(String str) {
		if (StringUtils.isNotBlank(str)) {
			str = StringUtils.replaceChars(StringUtils.trim(str), '，', ',');// 替换中文逗号
			String[] strs = StringUtils.split(str, ',');
			JSONArray ret = new JSONArray();
			if (strs != null && strs.length > 0) {
				for (int i = 0; i < strs.length; i++) {
					ret.add(StringUtils.trim(strs[i]));
				}
			}
			return ret;
		} else {
			return new JSONArray();
		}
	}

}
