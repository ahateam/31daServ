package zyxhj.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

/**
 * 输出流工具列，实现了
 * OutputStream接口。本类使用Java的源代码，结合了BufferedOutputStream，DataOutputStream等， 组成了一个输
 * 出流的工具类。
 * 
 * 使用前请认真阅读writeUTF8，writeBytes，writeInputStream等方法的注释（
 * 没有注释的方法与DataOutputStream相同 ）
 */
public class Output extends OutputStream {

	private static final int DEFAULT_BUFFER_SIZE = 4096;
	private byte[] buf;
	private int count;
	private OutputStream out;

	public Output(OutputStream out) {
		this(out, DEFAULT_BUFFER_SIZE);
	}

	public Output(OutputStream out, int bufferSize) {
		this.out = out;
		buf = new byte[bufferSize];
	}

	private void flushBuffer() throws IOException {
		if (count > 0) {
			out.write(buf, 0, count);
			count = 0;
		}
	}

	public void flush() throws IOException {
		flushBuffer();
		out.flush();
	}

	public void close() throws IOException {
		flush();
		buf = null;
		out.close();
	}

	public void write(int b) throws IOException {
		if (count >= buf.length) {
			flushBuffer();
		}
		buf[count++] = (byte) b;
	}

	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte[] b, int off, int len) throws IOException {
		if (len >= buf.length) {
			flushBuffer();
			out.write(b, off, len);
			return;
		} else if (len > buf.length - count) {
			flushBuffer();
		}
		System.arraycopy(b, off, buf, count, len);
		count += len;
	}

	public void writeBoolean(boolean v) throws IOException {
		write(v ? 1 : 0);
	}

	public void writeShort(int v) throws IOException {
		write((v >>> 8) & 0xFF);
		write(v & 0xFF);
	}

	public void writeChar(int v) throws IOException {
		write((v >>> 8) & 0xFF);
		write(v & 0xFF);
	}

	public void writeInt(int v) throws IOException {
		write((v >>> 24) & 0xFF);
		write((v >>> 16) & 0xFF);
		write((v >>> 8) & 0xFF);
		write(v & 0xFF);
	}

	public void writeLong(long v) throws IOException {
		write((int) (v >>> 56) & 0xFF);
		write((int) (v >>> 48) & 0xFF);
		write((int) (v >>> 40) & 0xFF);
		write((int) (v >>> 32) & 0xFF);
		write((int) (v >>> 24) & 0xFF);
		write((int) (v >>> 16) & 0xFF);
		write((int) (v >>> 8) & 0xFF);
		write((int) v & 0xFF);
	}

	public void writeFloat(float v) throws IOException {
		writeInt(Float.floatToIntBits(v));
	}

	public void writeDouble(double v) throws IOException {
		writeLong(Double.doubleToLongBits(v));
	}

	/**
	 * 写入byte数组，这是一个扩展方法。与Input中的readByte方法配套使用。
	 * 当输出流中想要写入多个byte数组的时候，由于流无法判断byte数组的起止位置
	 * ，因此借鉴修改版UTF-8的做法，在byte数组前面加上数组长度，方便读取结构化的字节数组。
	 * 
	 * @param bytes
	 * @throws IOException
	 */
	public void writeBytes(byte[] bytes) throws IOException {
		writeInt(bytes.length);
		write(bytes);
	}

	/**
	 * 写入修改版UTF-8字符串，类似DataInputStream中的readUTF方法。
	 * 修改版UTF-8是在普通UTF-8字符串之前，加上两个字节，用来表示字符串的长度。 因此，该方法不能用于直接读取UTF-8的字符流。
	 * 
	 * @throws IOException
	 */
	public void writeUTF8(String str) throws IOException {
		if (str == null) {
			str = "";
		}
		int strlen = str.length();
		int utflen = 0;
		char[] charr = new char[strlen];
		int c, count = 0;

		str.getChars(0, strlen, charr, 0);

		for (int i = 0; i < strlen; i++) {
			c = charr[i];
			if ((c >= 0x0001) && (c <= 0x007F)) {
				utflen++;
			} else if (c > 0x07FF) {
				utflen += 3;
			} else {
				utflen += 2;
			}
		}

		if (utflen > 65535) {
			throw new UTFDataFormatException();
		}

		byte[] bytearr = new byte[utflen + 2];
		bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
		bytearr[count++] = (byte) ((utflen >>> 0) & 0xFF);
		for (int i = 0; i < strlen; i++) {
			c = charr[i];
			if ((c >= 0x0001) && (c <= 0x007F)) {
				bytearr[count++] = (byte) c;
			} else if (c > 0x07FF) {
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			} else {
				bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >> 0) & 0x3F));
			}
		}
		write(bytearr);
	}

	/**
	 * 写入输入流，这个方法用户输入输出流对考
	 * 
	 * @param is
	 * @throws IOException
	 */
	public void writeInputStream(InputStream is) throws IOException {
		flush();
		int len = 0;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		is.close();
		flush();
	}
}
