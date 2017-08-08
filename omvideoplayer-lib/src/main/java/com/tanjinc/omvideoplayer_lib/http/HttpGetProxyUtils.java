package com.tanjinc.omvideoplayer_lib.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

import android.util.Log;
/**
 * 代理服务器工具类
 * @author hellogv
 *
 */
public class HttpGetProxyUtils {
	final static public String TAG = "HttpGetProxy";
	
	/** 收发Media Player请求的Socket */
	private Socket mSckPlayer = null;
	/** 收发Media Server请求的Socket */
	private Socket mSckServer = null;
	/**服务器的Address*/
	private SocketAddress mServerAddress;
	
	public HttpGetProxyUtils(Socket sckPlayer,Socket sckServer,SocketAddress address){
		mSckPlayer=sckPlayer;
		mSckServer=sckServer;
		mServerAddress=address;
	}
	
	/**
	 * 发送预加载至服务器
	 * @param fileName 预加载文件
	 * @param range skip的大小
	 * @return 已发送的大小，不含skip的大小
	 * @throws Exception
	 */
	public int sendPrebufferToMP(String fileName,long range) throws Exception {
		int fileBufferSize=0;
		byte[] file_buffer = new byte[1024];
		int bytes_read = 0;
		long startTimeMills=System.currentTimeMillis();
		
		File file = new File(fileName);
		if(range > (file.length()))//可用的预缓存太小，没必要
				return 0;

		FileInputStream fInputStream = new FileInputStream(file);
		if (range > 0) {
			byte[] tmp=new byte[(int) range];
			long skipByteCount = fInputStream.read(tmp);
			Log.e(TAG, ">>>skip:" + skipByteCount);
		}
		try {
			while ((bytes_read = fInputStream.read(file_buffer)) != -1) {
				fileBufferSize += bytes_read;
				mSckPlayer.getOutputStream().write(file_buffer, 0, bytes_read);
			}
			mSckPlayer.getOutputStream().flush();
			fInputStream.close();
		} catch (Exception ex) {
			fInputStream.close();
			throw ex;
		}
		
		long costTime=(System.currentTimeMillis() - startTimeMills);
		Log.e(TAG,">>>读取预加载耗时:"+costTime);
		Log.e(TAG,">>>读取完毕...下载:"+file.length()+",读取:"+fileBufferSize);
		
		return fileBufferSize;
	}
	

	/**
	 * 把服务器的Response的Header去掉
	 * @throws IOException 
	 */
	public void removeResponseHeader(HttpParser httpParser) throws IOException{
		int bytes_read;
		byte[] tmp_buffer = new byte[1024];
		while ((bytes_read = mSckServer.getInputStream().read(tmp_buffer)) != -1) {
			List<byte[]> tmp = httpParser.getResponseBody(tmp_buffer, bytes_read);
			
			//接收到Response的Header
			if (tmp.size() > 0) {
				Log.e(TAG + " ~~~~", new String(tmp.get(0)));
				if (tmp.size() == 2) {// 发送剩余数据
					sendToMP(tmp.get(1));
				}
				return;
			}
		}
	}
	
	public void sendToMP(byte[] bytes, int length) throws IOException {
		mSckPlayer.getOutputStream().write(bytes, 0, length);
		mSckPlayer.getOutputStream().flush();
	}

	public void sendToMP(byte[] bytes) throws IOException{
		if(bytes.length==0)
			return;
		
		mSckPlayer.getOutputStream().write(bytes);
		mSckPlayer.getOutputStream().flush();	
	}
	
	public Socket sentToServer(String requestStr) throws IOException{
		try {
			if(mSckServer!=null)
				mSckServer.close();
		} catch (Exception ex) {}
		mSckServer = new Socket();
		mSckServer.connect(mServerAddress);
		mSckServer.getOutputStream().write(requestStr.getBytes());// 发送MediaPlayer的请求
		mSckServer.getOutputStream().flush();
		return mSckServer;
	}
}
