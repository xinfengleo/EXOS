package com.gamebot.botdemo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;

public class BASE64Decoder {
	private static final char[] pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1',
			'2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
	private static final byte[] pem_convert_array = new byte[256];
	byte[] decode_buffer = new byte[4];

	static {
		int i;
		for (i = 0; i < 255; ++i) {
			pem_convert_array[i] = -1;
		}

		for (i = 0; i < pem_array.length; ++i) {
			pem_convert_array[pem_array[i]] = (byte) i;
		}
	}

	public BASE64Decoder() {
	}

	protected int decodeLinePrefix(PushbackInputStream aStream, OutputStream bStream) throws IOException {
		return this.bytesPerLine();
	}

	protected int readFully(InputStream in, byte[] buffer, int offset, int len) throws IOException {
		for (int i = 0; i < len; ++i) {
			int q = in.read();
			if (q == -1) {
				return i == 0 ? -1 : i;
			}
			buffer[i + offset] = (byte) q;
		}

		return len;
	}

	private void decodeBuffer(InputStream aStream, OutputStream bStream) throws IOException {
		// int totalBytes = 0;
		PushbackInputStream ps = new PushbackInputStream(aStream);

		while (true) {
			try {
				int length = this.decodeLinePrefix(ps, bStream);

				int i;
				for (i = 0; i + this.bytesPerAtom() < length; i += this.bytesPerAtom()) {
					this.decodeAtom(ps, bStream, this.bytesPerAtom());
					// totalBytes += this.bytesPerAtom();
				}

				if (i + this.bytesPerAtom() == length) {
					this.decodeAtom(ps, bStream, this.bytesPerAtom());
					// totalBytes += this.bytesPerAtom();
				} else {
					this.decodeAtom(ps, bStream, length - i);
					// totalBytes += length - i;
				}

				// this.decodeLineSuffix(ps, bStream);
			} catch (IOException var8) {
				// this.decodeBufferSuffix(ps, bStream);
				return;
			}
		}
	}

	public byte[] decodeBuffer(String inputString) throws IOException {
		byte[] inputBuffer = new byte[inputString.length()];
		// inputString.getBytes(0, inputString.length(), inputBuffer, 0);
		inputBuffer = inputString.getBytes();
		ByteArrayInputStream inStream = new ByteArrayInputStream(inputBuffer);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		this.decodeBuffer(inStream, outStream);
		return outStream.toByteArray();
	}

	protected int bytesPerAtom() {
		return 4;
	}

	protected int bytesPerLine() {
		return 72;
	}

	protected void decodeAtom(PushbackInputStream inStream, OutputStream outStream, int rem) throws IOException {
		byte a = -1;
		byte b = -1;
		byte c = -1;
		byte d = -1;
		if (rem < 2) {
			throw new IOException("BASE64Decoder: Not enough bytes for an atom.");
		} else {
			int i;
			do {
				i = inStream.read();
				if (i == -1) {
					throw new IOException();
				}
			} while (i == 10 || i == 13);

			this.decode_buffer[0] = (byte) i;
			i = this.readFully(inStream, this.decode_buffer, 1, rem - 1);
			if (i == -1) {
				throw new IOException();
			} else {
				if (rem > 3 && this.decode_buffer[3] == 61) {
					rem = 3;
				}

				if (rem > 2 && this.decode_buffer[2] == 61) {
					rem = 2;
				}

				switch (rem) {
				case 4:
					d = pem_convert_array[this.decode_buffer[3] & 255];
				case 3:
					c = pem_convert_array[this.decode_buffer[2] & 255];
				case 2:
					b = pem_convert_array[this.decode_buffer[1] & 255];
					a = pem_convert_array[this.decode_buffer[0] & 255];
				default:
					switch (rem) {
					case 2:
						outStream.write((byte) (a << 2 & 252 | b >>> 4 & 3));
						break;
					case 3:
						outStream.write((byte) (a << 2 & 252 | b >>> 4 & 3));
						outStream.write((byte) (b << 4 & 240 | c >>> 2 & 15));
						break;
					case 4:
						outStream.write((byte) (a << 2 & 252 | b >>> 4 & 3));
						outStream.write((byte) (b << 4 & 240 | c >>> 2 & 15));
						outStream.write((byte) (c << 6 & 192 | d & 63));
					}
				}
			}
		}
	}
}
