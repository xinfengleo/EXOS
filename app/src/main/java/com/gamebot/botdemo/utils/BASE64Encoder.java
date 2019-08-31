package com.gamebot.botdemo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class BASE64Encoder {
	private static final char[] pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	public BASE64Encoder() {
	}

	protected int bytesPerAtom() {
		return 3;
	}

	protected int bytesPerLine() {
		return 57;
	}

	protected PrintStream pStream;

	protected void encodeBufferPrefix(OutputStream aStream) throws IOException {
		this.pStream = new PrintStream(aStream);
	}

	protected void encodeLineSuffix(OutputStream aStream) throws IOException {
		this.pStream.println();
	}

	protected int readFully(InputStream in, byte[] buffer) throws IOException {
		for (int i = 0; i < buffer.length; ++i) {
			int q = in.read();
			if (q == -1) {
				return i;
			}
			buffer[i] = (byte) q;
		}
		return buffer.length;
	}

	protected void encode(InputStream inStream, OutputStream outStream) throws IOException {
		byte[] tmpbuffer = new byte[this.bytesPerLine()];
		this.encodeBufferPrefix(outStream);

		while (true) {
			int numBytes = this.readFully(inStream, tmpbuffer);
			if (numBytes == 0) {
				break;
			}

			for (int j = 0; j < numBytes; j += this.bytesPerAtom()) {
				if (j + this.bytesPerAtom() <= numBytes) {
					this.encodeAtom(outStream, tmpbuffer, j, this.bytesPerAtom());
				} else {
					this.encodeAtom(outStream, tmpbuffer, j, numBytes - j);
				}
			}

			if (numBytes < this.bytesPerLine()) {
				break;
			}

			this.encodeLineSuffix(outStream);
		}
	}

	public String encode(byte[] aBuffer) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayInputStream inStream = new ByteArrayInputStream(aBuffer);
		String retVal = null;

		try {
			this.encode((InputStream) inStream, outStream);
			retVal = outStream.toString("8859_1");
			return retVal;
		} catch (Exception var6) {
			throw new Error(".encode internal error");
		}
	}

	protected void encodeAtom(OutputStream outStream, byte[] data, int offset, int len) throws IOException {
		byte a;
		if (len == 1) {
			a = data[offset];
			byte b = 0;
			//boolean c = false;
			outStream.write(pem_array[a >>> 2 & 63]);
			outStream.write(pem_array[(a << 4 & 48) + (b >>> 4 & 15)]);
			outStream.write(61);
			outStream.write(61);
		} else {
			byte b1;
			if (len == 2) {
				a = data[offset];
				b1 = data[offset + 1];
				byte c1 = 0;
				outStream.write(pem_array[a >>> 2 & 63]);
				outStream.write(pem_array[(a << 4 & 48) + (b1 >>> 4 & 15)]);
				outStream.write(pem_array[(b1 << 2 & 60) + (c1 >>> 6 & 3)]);
				outStream.write(61);
			} else {
				a = data[offset];
				b1 = data[offset + 1];
				byte c2 = data[offset + 2];
				outStream.write(pem_array[a >>> 2 & 63]);
				outStream.write(pem_array[(a << 4 & 48) + (b1 >>> 4 & 15)]);
				outStream.write(pem_array[(b1 << 2 & 60) + (c2 >>> 6 & 3)]);
				outStream.write(pem_array[c2 & 63]);
			}
		}

	}
}
