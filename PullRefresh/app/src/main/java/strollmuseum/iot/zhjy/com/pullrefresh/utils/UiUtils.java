package strollmuseum.iot.zhjy.com.pullrefresh.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import strollmuseum.iot.zhjy.com.pullrefresh.application.MobileOaApplication;


/**
 * ============================================================
 * 
 * 版 权 ： zhangyang(c) 2015
 * 
 * 作 者 : 张扬
 * 
 * 版 本 ： 1.0
 * 
 * 创建日期 ：2015-3-25上午2:55:31
 * 
 * 描 述 ： 进行创建UI的相关的工具方法
 * 
 * 
 * 
 * 逻辑：
 * 
 * 
 * 注意点:
 * 
 * 
 * 
 * 修订历史 ：
 * Handler
 * ============================================================
 **/
public class UiUtils {

	/**
	 * 通过传入tabName,进行得出字符串的数组
	 * 
	 * @param tabName
	 * @return
	 */
	public static String[] getStringArray(int tabName) {

		return getResources().getStringArray(tabName);
	}

	/**
	 * 进行获取资源类
	 * 
	 * @return
	 */
	private static Resources getResources() {

		return MobileOaApplication.application.getResources();
	}

	/**
	 * 从dip转向dp
	 * 
	 * @param dip
	 * @return
	 */
	public static int dip2dp(int dip) {

		// 进行获取到密度
		float density = getResources().//
				getDisplayMetrics().density;

		return (int) (dip * density + 0.5f);
	}

	/**
	 * 从dp转向dip
	 * 
	 * @param dip
	 * @return
	 */
	public static int dp2dip(int dp) {

		// 进行获取到密度
		float density = getResources().//
				getDisplayMetrics().density;

		return (int) (dp / density + 0.5f);
	}
	/**
	 * 进行实现runOnUiThread
	 * @param runnable
	 */
	public static void runOnUiThread(Runnable runnable){
		
		//首先进行判断是不是主线程
		if(android.os.Process.myTid()==//
				MobileOaApplication.mainId){
			//如果是主线程,进行运行runnable
			runnable.run();
		}else{
			//如果不是主线程,进行将方法进行提交都主线程
			MobileOaApplication.getHandler().post(runnable);
			
		}
	}
	
	
	/**
	 * 进行提供获取Context
	 * @return
	 */
	public static Context getContext(){
		
		return MobileOaApplication.application;
	}
	
	/**
	 * 进行填充view对象
	 * @param viewId
	 * @return
	 */
	public static View inflate(int viewId){
		
		return View.inflate(getContext(), viewId, null);
	}
	
	/**
	 * 进行获取图片的Drawable
	 * @param nothing
	 * @return
	 */
	public static Drawable getDrawable(int drawableId) {
		
		 return getResources().getDrawable(drawableId);
	}
	
	public static int getDimen(int id){
		return (int)(getResources().getDimension(id)+0.5f);
	}
	
	public static int dip2px(int id){
		
		return 0;
	}
	
	public static DisplayMetrics getDisplayMetrics(){
		WindowManager windowManager=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics metrics=new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}
	/**
	 * 进行获取Handler
	 */
	public static Handler getHandler() {
		return MobileOaApplication.getHandler();
	}

	/**
	 * 进行显示信息
	 * @param msg
	 */
	public static void showToast(final String msg){
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				
				Toast.makeText(getContext(),msg,0).show();
			}
		});
	}
	
	/**
	 * 进行获取屏幕的宽度
	 * @return
	 */
	public static int getDefaultWidth(){
		WindowManager windowManager=(WindowManager) UiUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getWidth();
	}
	
	/**
	 * 进行获取屏幕的高度
	 * @return
	 */
	public static int getDefaultHeight(){
		WindowManager windowManager=(WindowManager) UiUtils.getContext().getSystemService(Context.WINDOW_SERVICE);
		return windowManager.getDefaultDisplay().getHeight();
	}
	
	
	/**
	 * dp转换成像素
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2Px(Context context, float dp) {
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dp * scale + 0.5f);  
	}  

	/**
	 * 像素转换成dp
	 * @param context
	 * @param px
	 * @return
	 */
	public static int px2Dp(Context context, float px) {
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (px / scale + 0.5f);  
	}  
	
	/**
	 * 描述：强制关闭软键盘
	 * @param editText
	 */
	public static void closeKeyBoard(EditText editText) {
		@SuppressWarnings("static-access")
		InputMethodManager imm = (InputMethodManager) UiUtils.getContext()
				.getSystemService(UiUtils.getContext().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	
	
}
