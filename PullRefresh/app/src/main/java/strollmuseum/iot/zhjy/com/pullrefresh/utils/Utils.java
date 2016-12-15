package strollmuseum.iot.zhjy.com.pullrefresh.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.widget.Toast;

import strollmuseum.iot.zhjy.com.pullrefresh.application.MobileOaApplication;


//#define DEBUG

/**
 * 工具类
 * */
public class Utils {
	
	//解读并存储assets文件夹下的Config.txt文件信息
	private static Map<String,String> configHm_;
	public static String ServiceURL = "";
	
	private static final String UTF_8 = "UTF-8";//编码方式
	
    private static CharsetEncoder encoder = Charset.forName(UTF_8).newEncoder();
    private static CharsetDecoder decoder = Charset.forName(UTF_8).newDecoder();
    
    /**
     * 编码 
     * @param in
     * @return
     * @throws CharacterCodingException
     */
    public static ByteBuffer encode(CharBuffer in) throws CharacterCodingException{
        return encoder.encode(in);
    }
    /**
     * 解码
     * @param in
     * @return
     * @throws CharacterCodingException
     */
    public static CharBuffer decode(ByteBuffer in) throws CharacterCodingException{
        return decoder.decode(in);
    }
    /**
     * 解码
     * @param in
     * @return
     */
    public static String decodeByteBuffer(ByteBuffer in){
    	byte[] temp = new byte[in.position()];
    	for(int i=0;i<in.limit();i++){
			byte t;
			if(i<temp.length){
				t = in.get(i);
				temp[i] = t;
			}
		}
    	try {
			return new String(temp, UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return "";
    }
    
	/**
	 * 显示提示信息
	 * 
	 * @param activity
	 * @param content
	 */
	public static void showToast(final Activity activity , final String content){
		
		if(activity != null && !TextUtils.isEmpty(content)){
			if (Thread.currentThread().getId() == 1) {
				Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
			} else {
				activity.runOnUiThread(new Runnable(){

					@Override
					public void run() {
						Toast.makeText(activity, content, Toast.LENGTH_SHORT).show();
					}
				 });
			}
		}
	}
	
	/**
	 * 显示提示信息
	 * 
	 * @param activity
	 * @param content
	 */
	public static void showToastLong(final Activity activity , final String content){
		if(activity != null && !TextUtils.isEmpty(content)){
			if (Thread.currentThread().getId() == 1) {
				Toast.makeText(activity, content, Toast.LENGTH_LONG).show();
			} else {
				activity.runOnUiThread(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(activity, content, Toast.LENGTH_LONG)
						.show();
					}
					 
				 });
			}
		}
	}
	
	
	/*创建Dialog
	 * */
	public static ProgressDialog createProgressDialog(Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		//设置为圆形
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle("提示");
		progressDialog.setMessage("正在处理,请等待...");
		progressDialog.setIcon(android.R.drawable.ic_dialog_alert);
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.setMax(100);
		return progressDialog;
	}
	
	/**
	 * 将字节数组进行转换成bitmap对象
	 * @param buffer
	 * @return
	 */
	public final static Bitmap getBitmap(byte[] buffer) {

		if (buffer != null && buffer.length > 0) {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;

			Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0,
					buffer.length, opt);
			return bitmap;
		}
		return null;
	}
	
    
   
    /**
     * 将raw原始的资源转换成bitmap
     * @param rdoing
     * @param bv
     * @return
     */
    public final static Bitmap getBitmap(int rdoing, Activity bv) {
        // TODO Auto-generated method stub
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = bv.getResources().openRawResource(rdoing);

        Bitmap img = BitmapFactory.decodeStream(is, null, opt);
        try {
            is.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
           e.printStackTrace();
        }
        return img;
    }

    
	public static String compressImg(String path){
		
		String rePath = "";//压缩后文件存储路径
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeFile(path, opt);
			//计算缩放比例
			int be = (int) (opt.outHeight / (float)480);
			int ys = opt.outHeight % 480;
			float fe = ys / (float)480;
			if(fe >= 0.5){
				be++;
			}
			if(be <= 0){
				be = 1;
			}
			opt.inSampleSize = be;
			opt.inJustDecodeBounds = false;
			
			bmp = BitmapFactory.decodeFile(path, opt);
			if(null == bmp){
				return rePath;
			}
			String picName =  DateUtil.getDateLong()+".jpg";
			rePath = SDCardUtils.getInstance().getIconDir()+picName;
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(rePath));
			bmp.compress(Bitmap.CompressFormat.JPEG, 20, bos);
			bos.flush();
			bos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return rePath;
	}
	
	private static DisplayMetrics dm;
	/**
	 * 屏幕属性
	 * @return
	 */
	public static DisplayMetrics getDisplayMetrics(Activity context){
		if(dm == null){
			dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);
		}
		return dm;
	}
	/**
	 * 检查字符串数组是否包含某个元素
	 * @param values
	 * @param value
	 * @return
	 */
	public static boolean checkArrayContainsValue(String[] values,String value){
		for (String string : values) {
			if(string.equalsIgnoreCase(value)){
				return true;
			}
		}
		return false;
	}
	/**
	 * 将set集合转换为字符串数组
	 * @param set
	 * @return
	 */
	public static String[] parseCollection2Array(Collection<String>set){
		String[] x = new String[set.size()+1];
		x[0] = "请选择";
		int i = 1;
		for (String string : set) {
			x[i++] = string;
		}
		return x;
	}
	
	public static String[] parseOrignalCollection2Array(Collection<String>set){
		String[] x = new String[set.size()];
		int i = 0;
		for (String string : set) {
			x[i++] = string;
		}
		return x;
	}
	
	/**
	 * 从字符串数组获取指定字符串的索引获取
	 * @param array
	 * @param key
	 * @return
	 */
	public static int getIndexFromArray(String[] array,String key){
		for (int i = 0; i < array.length; i++) {
			if(key.equalsIgnoreCase(array[i])){
				return i;
			}
		}
		return -1;
		
	}
	/**
	 * 简单加密处理
	 * @param pwd
	 * @return
	 */
	public static String encodePassword(String pwd){
		String t = Base64.encodeToString(pwd.getBytes(), MobileOaApplication.SECRET_FLAG);
		int index = t.lastIndexOf("//n");
		return t.substring(0,index-1);
	}
	/**
	 * 获取本机ip地址
	 * @return
	 */
	public static String getLocalIP(){
		String ip = "";
		try {
			Enumeration<NetworkInterface> netsEnumeration = NetworkInterface.getNetworkInterfaces();
			while (netsEnumeration.hasMoreElements()) {
				NetworkInterface networkInterface = (NetworkInterface) netsEnumeration.nextElement();
				Enumeration<InetAddress> ips = networkInterface.getInetAddresses();
				while (ips.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) ips.nextElement();
					if(!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()){//如果不是回环地址
						ip = inetAddress.getHostAddress().toString();
						return ip;
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return ip;
	}
}
