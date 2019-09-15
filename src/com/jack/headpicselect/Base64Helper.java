package com.jack.headpicselect;

import java.io.IOException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Base64Helper {

	public static String encode(byte[] byteArray) {
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(byteArray);
	}

	public static byte[] decode(String base64EncodedString) {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		try {
			return base64Decoder.decodeBuffer(base64EncodedString);
		} catch (IOException e) {
			return null;
		}
	}
}