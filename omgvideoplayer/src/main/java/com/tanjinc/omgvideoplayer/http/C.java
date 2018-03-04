package com.tanjinc.omgvideoplayer.http;

import android.os.Environment;

/**
 * Config
 * @author hellogv
 *
 */
public class C{

	final static public String LOCAL_IP_ADDRESS = "127.0.0.1";
	final static public int HTTP_PORT = 80;
	final static public String HTTP_BODY_END = "\r\n\r\n";
	final static public String HTTP_RESPONSE_BEGIN = "HTTP/1.1 ";
	final static public String HTTP_REQUEST_BEGIN = "GET ";
	final static public String HTTP_REQUEST_LINE1_END = " HTTP/";
	
	static public String getBufferDir(){
		String bufferDir = Environment.getExternalStorageDirectory()
		.getAbsolutePath() + "/";
		return bufferDir;
	}
}