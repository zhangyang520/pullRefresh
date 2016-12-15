package strollmuseum.iot.zhjy.com.pullrefresh.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

/**
 * 
 * @author zhangyang
 * 
 * @日期 2016年3月17日上午10:10:51
 * 
 * @描述 application类
 */
public class MobileOaApplication extends Application{

	public static final String TAG = "com.zhjy.iot.mobile.oa.application.MobileOaApplication";
	public static final String FILE_PATH = "0A-GZW";//系统文件存储路径
	public static SharedPreferences sp ;//文件存储引用
	public static final String APP_CHARSET = "utf-8";//系统默认编码方式
	public static final int SECRET_FLAG = 8;
	public static final int TIME_OUT = 5*1000;//设置请求超时时间

	public static String MOBILESERVER = "";//服务器路径
	public static int MAX_WIDTH = 0;
	private static Handler handler;
	public static int mainId;
	public static MobileOaApplication application;
	@Override
	public void onCreate() {
		super.onCreate();
		
		application=this;
		handler=new Handler();
		mainId=android.os.Process.myTid();

		//参数一 包名_preferences
	    sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());//获取本应用的偏好

	}

	public static int getMainId() {
		return mainId;
	}

	public static void setMainId(int mainId) {
		MobileOaApplication.mainId = mainId;
	}

	public static Handler getHandler() {
		return handler;
	}

	public static void setHandler(Handler handler) {
		MobileOaApplication.handler = handler;
	}

}
