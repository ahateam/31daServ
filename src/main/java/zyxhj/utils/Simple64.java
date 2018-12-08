package zyxhj.utils;

import java.nio.charset.StandardCharsets;

/**
 * Simple64编码（改自Base64），用于数据库编号，也适用于Base64的使用场景。<br>
 * -64位编码相对10进制数字和16进制文本，编码更短 <br>
 * -按ASCII顺序排列字典顺序，理论上比Base64更有利于索引<br>
 * -字符集对url（urlencode）友好<br>
 * <br>
 * 参考：在MongoDB的ObjectId的基础上，使用64位编码替换16位编码，ID长度从24位缩短为16位。<br>
 * -ObjectId由time，machine，random三部分（整形），其字符串形式为24位的hex编码。<br>
 * -使用Simple64编码，编号短且具备顺序性。 <br>
 * 参考：从escape字符集选择了*+-三个字符与数字和字母组成65位字典<br>
 * -escape不编码字符有69个：*，+，-，.，/，@，_，0-9，a-z，A-Z<br>
 * -encodeURIComponent不编码字符有71个：!， '，(，)，*，-，.，_，~，0-9，a-z，A-Z<br>
 * -encodeURI不编码字符有82个：!，#，$，&，'，(，)，*，+，,，-，.，/，:，;，=，?，@，_，~，0-9，a-z，A-Z<br>
 * 
 */
public class Simple64 {

	/**
	 * Simple64编码
	 */
	public static byte[] encode(byte[] src) {
		int len = outLength(src.length);
		byte[] dst = new byte[len];
		int ret = encode0(src, 0, src.length, dst);
		if (ret != dst.length)
			return copyOf(dst, ret);
		return dst;
	}

	/**
	 * Simple64编码
	 */
	public static String encodeToString(byte[] src) {
		byte[] data = encode(src);
		return new String(data, StandardCharsets.ISO_8859_1);
	}

	/**
	 * Simple64解码
	 */
	public static byte[] decode(byte[] src) {
		byte[] dst = new byte[outLength(src, 0, src.length)];
		int ret = decode0(src, 0, src.length, dst);
		if (ret != dst.length) {
			dst = copyOf(dst, ret);
		}
		return dst;
	}

	/**
	 * Simple64解码
	 */
	public static byte[] decodeFromString(String src) {
		return decode(src.getBytes(StandardCharsets.ISO_8859_1));
	}

	private static final char[] toSimple64 = { '*', '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
			'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
			'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

	private static final char Simple64_PADDING = '*';

	private static final int[] fromSimple64 = new int[256];

	private Simple64() {
	}

	static {
		for (int i = 0, len = fromSimple64.length; i < len; i++)
			fromSimple64[i] = -1;
		for (int i = 0; i < toSimple64.length; i++)
			fromSimple64[toSimple64[i]] = i;
		fromSimple64[Simple64_PADDING] = -2;

	}

	private static int encode0(byte[] src, int off, int end, byte[] dst) {
		int sp = off;
		int slen = (end - off) / 3 * 3;
		int sl = off + slen;
		int dp = 0;
		while (sp < sl) {
			int sl0 = Math.min(sp + slen, sl);
			for (int sp0 = sp, dp0 = dp; sp0 < sl0;) {
				int bits = (src[sp0++] & 0xff) << 16 | (src[sp0++] & 0xff) << 8 | (src[sp0++] & 0xff);
				dst[dp0++] = (byte) toSimple64[(bits >>> 18) & 0x3f];
				dst[dp0++] = (byte) toSimple64[(bits >>> 12) & 0x3f];
				dst[dp0++] = (byte) toSimple64[(bits >>> 6) & 0x3f];
				dst[dp0++] = (byte) toSimple64[bits & 0x3f];
			}
			int dlen = (sl0 - sp) / 3 * 4;
			dp += dlen;
			sp = sl0;
		}
		if (sp < end) { // 1 or 2 leftover bytes
			int b0 = src[sp++] & 0xff;
			dst[dp++] = (byte) toSimple64[b0 >> 2];
			if (sp == end) {
				dst[dp++] = (byte) toSimple64[(b0 << 4) & 0x3f];
				dst[dp++] = Simple64_PADDING;
				dst[dp++] = Simple64_PADDING;
			} else {
				int b1 = src[sp++] & 0xff;
				dst[dp++] = (byte) toSimple64[(b0 << 4) & 0x3f | (b1 >> 4)];
				dst[dp++] = (byte) toSimple64[(b1 << 2) & 0x3f];
				dst[dp++] = Simple64_PADDING;
			}
		}
		return dp;
	}

	private static int outLength(byte[] src, int sp, int sl) {
		int paddings = 0;
		int len = sl - sp;
		if (len == 0)
			return 0;
		if (len < 2) {
			throw new IllegalArgumentException("Input byte[] should at least have 2 bytes for Simple64 bytes");
		}
		if (src[sl - 1] == Simple64_PADDING) {
			paddings++;
			if (src[sl - 2] == Simple64_PADDING)
				paddings++;
		}

		if (paddings == 0 && (len & 0x3) != 0)
			paddings = 4 - (len & 0x3);
		return 3 * ((len + 3) / 4) - paddings;
	}

	private static int decode0(byte[] src, int sp, int sl, byte[] dst) {
		int dp = 0;
		int bits = 0;
		int shiftto = 18; // pos of first byte of 4-byte atom
		while (sp < sl) {
			int b = src[sp++] & 0xff;
			if ((b = fromSimple64[b]) < 0) {
				if (b == -2) {
					if (shiftto == 6 && (sp == sl || src[sp++] != Simple64_PADDING) || shiftto == 18) {
						throw new IllegalArgumentException("Input byte array has wrong 4-byte ending unit");
					}
					break;
				}
				throw new IllegalArgumentException("Illegal Simple64 character " + Integer.toString(src[sp - 1], 16));
			}
			bits |= (b << shiftto);
			shiftto -= 6;
			if (shiftto < 0) {
				dst[dp++] = (byte) (bits >> 16);
				dst[dp++] = (byte) (bits >> 8);
				dst[dp++] = (byte) (bits);
				shiftto = 18;
				bits = 0;
			}
		}
		if (shiftto == 6) {
			dst[dp++] = (byte) (bits >> 16);
		} else if (shiftto == 0) {
			dst[dp++] = (byte) (bits >> 16);
			dst[dp++] = (byte) (bits >> 8);
		} else if (shiftto == 12) {
			// dangling single "x", incorrectly encoded.
			throw new IllegalArgumentException("Last unit does not have enough valid bits");
		}
		// anything left is invalid, if is not MIME.
		// if MIME, ignore all non-Simple64 character
		while (sp < sl) {
			throw new IllegalArgumentException("Input byte array has incorrect ending byte at " + sp);
		}
		return dp;
	}

	private static final int outLength(int srclen) {
		return 4 * ((srclen + 2) / 3);
	}

	private static byte[] copyOf(byte[] original, int newLength) {
		byte[] copy = new byte[newLength];
		System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
		return copy;
	}

}
