package com.tanjinc.omvideoplayer_lib.http;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;

/**
 * 代理服务器类
 * @author hellogv
 *
 */
public class HttpGetProxy{
	final static public int SIZE =  (int) (1*1024 * 1024);
	final static public String TAG = "HttpGetProxy";
	/** 链接带的端口 */
	private int remotePort=-1;
	/** 远程服务器地址 */
	private String remoteHost;
	/** 代理服务器使用的端口 */
	private int localPort;
	/** 本地服务器地址 */
	private String localHost;
	private ServerSocket localServer = null;
	/** 收发Media Player请求的Socket */
	private Socket sckPlayer = null;
	/** 收发Media Server请求的Socket */
	private Socket sckServer = null;
	/**服务器的Address*/
	private SocketAddress serverAddress;
	
	/**下载线程*/
	private DownloadThread download = null;
	
	/**
	 * 初始化代理服务器
	 * @param localport 代理服务器监听的端口
	 */
	public HttpGetProxy(int localport) {
		try {
			localPort = localport;
			localHost = C.LOCAL_IP_ADDRESS;
			localServer = new ServerSocket(localport, 1, InetAddress.getByName(localHost));
		} catch (Exception e) {
//			System.exit(0);
			Log.e(TAG, "video HttpGetProxy: ", e);
		}
	}

	/**
	 * 把URL提前下载在SD卡，实现预加载
	 * @param urlString
	 * @return 返回预加载文件名
	 * @throws Exception
	 */
	public String prebuffer(String urlString,int size) throws Exception{
		if(download!=null && download.isDownloading())
			download.stopThread(true);
		
		URI tmpURI = new URI(urlString);
		String fileName = Utils.urlToFileName(tmpURI.getPath());
		String filePath = C.getBufferDir()+"/"+fileName;
		
		download = new DownloadThread(urlString,filePath,size);
		download.startThread();
		
		return filePath;
	}
	
	/**
	 * 把网络URL转为本地URL，127.0.0.1替换网络域名
	 * 
	 * @param url网络URL
	 * @return [0]:重定向后MP4真正URL，[1]:本地URL
	 */
	public String[] getLocalURL(String urlString) {
		
		// ----排除HTTP特殊----//
		String targetUrl = Utils.getRedirectUrl(urlString);
		// ----获取对应本地代理服务器的链接----//
		String localUrl = null;
		URI originalURI = URI.create(targetUrl);
		remoteHost = originalURI.getHost();
		if (originalURI.getPort() != -1) {// URL带Port
			serverAddress = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
			remotePort = originalURI.getPort();// 保存端口，中转时替换
			localUrl = targetUrl.replace(
					remoteHost + ":" + originalURI.getPort(), localHost + ":"
							+ localPort);
		} else {// URL不带Port
			serverAddress = new InetSocketAddress(remoteHost, C.HTTP_PORT);// 使用80端口
			remotePort = -1;
			localUrl = targetUrl.replace(remoteHost, localHost + ":"
					+ localPort);
		}
		
		String[] result= new String[]{targetUrl,localUrl};
		return result;
	}

	/**
	 * 异步启动代理服务器
	 * 
	 * @throws IOException
	 */
	public void asynStartProxy() {
		new Thread() {
			public void run() {
				startProxy();
			}
		}.start();
	}

	private void startProxy() {
		HttpParser httpParser =null;
		HttpGetProxyUtils utils=null;
		int bytes_read;
	
		byte[] local_request = new byte[1024];
		byte[] remote_reply = new byte[1024];

		while (true) {
			boolean sentResponseHeader = false;
			try {// 开始新的request之前关闭过去的Socket
				if (sckPlayer != null)
					sckPlayer.close();
				if (sckServer != null)
					sckServer.close();
			} catch (IOException e1) {}
			try {
				// --------------------------------------
				// 监听MediaPlayer的请求，MediaPlayer->代理服务器
				// --------------------------------------
				sckPlayer = localServer.accept();
				Log.e(TAG,"------------------------------------------------------------------");
				if(download!=null && download.isDownloading()) {
					download.stopThread(false);
				}
				
				httpParser= new HttpParser(remoteHost,remotePort,localHost,localPort);
				utils = new HttpGetProxyUtils(sckPlayer,sckServer,serverAddress);
				
				HttpParser.ProxyRequest request = null;
				while ((bytes_read = sckPlayer.getInputStream().read(local_request)) != -1) {
					byte[] buffer=httpParser.getRequestBody(local_request,bytes_read);
					if(buffer!=null){
						request=httpParser.getProxyRequest(buffer);
						break;
					}
				}
				
				boolean isExists=new File(request._prebufferFilePath).exists();
				if(isExists)
					Log.e(TAG,"prebuffer size:"+download.getDownloadedSize());
				
				sckServer = utils.sentToServer(request._body);
				// ------------------------------------------------------
				// 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
				// ------------------------------------------------------
				while ((bytes_read = sckServer.getInputStream().read(remote_reply)) != -1) {
					if(sentResponseHeader){
						try{//拖动进度条时，容易在此异常，断开重连
							utils.sendToMP(remote_reply,bytes_read);
						}catch (Exception e) {
							break;//发送异常直接退出while
						}
						continue;//退出本次while
					}

					List<byte[]> httpResponse = httpParser.getResponseBody(remote_reply, bytes_read);
					if (httpResponse.size() == 0)
						continue;//没Header则退出本次循环

					sentResponseHeader = true;
					String responseStr = new String(httpResponse.get(0));
					Log.e(TAG + "<---", responseStr);
					//send http header to mediaplayer
					utils.sendToMP(httpResponse.get(0));
					
					if (isExists) {//需要发送预加载到MediaPlayer
						isExists = false;
						int sentBufferSize = 0;
						try{
							sentBufferSize = utils.sendPrebufferToMP(
								request._prebufferFilePath,
								request._rangePosition);
						}catch(Exception ex){
							break;
						}
						if (sentBufferSize > 0) {// 成功发送预加载，重新发送请求到服务器
							int newRange=(int) (sentBufferSize + request._rangePosition);
							String newRequestStr = httpParser.modifyRequestRange(request._body,newRange);
							Log.e(TAG + "-pre->", newRequestStr);
							//修改Range后的Request发送给服务器
							sckServer = utils.sentToServer(newRequestStr);
							//把服务器的Response的Header去掉
							utils.removeResponseHeader(httpParser);
							continue;
						}
					}

					// 发送剩余数据
					if (httpResponse.size() == 2) {
						utils.sendToMP(httpResponse.get(1));
					}
				}

				Log.e(TAG, ".........over..........");

				// 关闭 2个SOCKET
				sckPlayer.close();
				sckServer.close();
			} catch (Exception e) {
				Log.e(TAG,e.toString());
				Log.e(TAG,Utils.getExceptionMessage(e));
			}
		}
	}
	
	
}