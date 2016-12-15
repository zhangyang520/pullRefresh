package strollmuseum.iot.zhjy.com.pullrefresh.utils;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

import strollmuseum.iot.zhjy.com.pullrefresh.application.MobileOaApplication;

/**
 * 
 * @author zhangyang
 * 
 * @日期 2016-3-30下午2:30:17
 * 
 * @描述 sdcard的工具类
 */
public class SDCardUtils {

	//下载文件所存储的位置
	private String downLoadFileDir;
	//所有对应的图片路径
	private String iconDir;
	private String zipDir;//压缩文件目录
	private static SDCardUtils sdCardUtils=new SDCardUtils();
	private String debugInfo;
	
	private SDCardUtils()  
    {  
		initScardUtils();
    }  
    
	/**
	 * 进行返回实例
	 * @return
	 */
	public static SDCardUtils getInstance(){
		return sdCardUtils;
	}
	
	/**
	 * 进行初始化sdcardUtils
	 */
	public void initScardUtils(){
		//进行设置下载的路径
		downLoadFileDir=getRootFilePath()+MobileOAConstant.DOWNLOAD_DIR_NAME+File.separator;
		iconDir=getRootFilePath()+MobileOAConstant.ICON_DIR_NAME+File.separator;
		zipDir=getRootFilePath()+MobileOAConstant.ZIP_DIR_NAME+File.separator;
		debugInfo=getRootFilePath()+MobileOAConstant.DEBUG_INFO+File.separator;
	}
    /** 
     * 判断SDCard是否可用 
     *  
     * @return 
     */  
    public  boolean isSDCardEnable()  
    {  
        return Environment.getExternalStorageState().equals(  
                Environment.MEDIA_MOUNTED);
    }  
  
    /** 
     * 获取SD卡路径 
     *  
     * @return 
     */  
    private  String getSDCardPath()  
    {  
        return Environment.getExternalStorageDirectory().getAbsolutePath()  
                + File.separator;  
    }  
  
    /** 
     * 获取SD卡的剩余容量 单位byte 
     *  
     * @return 
     */  
    public  long getSDCardAllSize()  
    {  
        if (isSDCardEnable())  
        {  
            StatFs stat = new StatFs(getSDCardPath());  
            // 获取空闲的数据块的数量  
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;  
            // 获取单个数据块的大小（byte）  
            long freeBlocks = stat.getBlockSize();  
            return freeBlocks * availableBlocks;  
        }  
        return 0;  
    }  
  
    /** 
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte 
     *  
     * @param filePath 
     * @return 容量字节 SDCard可用空间，内部存储可用空间 
     */  
    public  long getFreeBytes(String filePath)  
    {  
        // 如果是sd卡的下的路径，则获取sd卡可用容量  
        if (filePath.startsWith(getSDCardPath()))  
        {  
            filePath = getSDCardPath();  
        } else  
        {// 如果是内部存储的路径，则获取内存存储的可用容量  
            filePath = Environment.getDataDirectory().getAbsolutePath();  
        }  
        StatFs stat = new StatFs(filePath);  
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;  
        return stat.getBlockSize() * availableBlocks;  
    }  
  
    /** 
     * 获取系统存储路径 
     *  
     * @return 
     */  
    private  String getRootDirectoryPath()  
    {  
        return Environment.getRootDirectory().getAbsolutePath();  
    }
    /**
     * 获取系统默认存储目录
     * @return
     */
    public  String getRootFilePath()  
    {  
    	String filepath = "";
    	if(isSDCardEnable())
    		filepath = getSDCardPath()+File.separator+MobileOaApplication.FILE_PATH+File.separator;
    	else {
			filepath = getRootDirectoryPath()+File.separator+ MobileOaApplication.FILE_PATH+File.separator;
		}
    	File f = new File(filepath);
    	if(!f.exists() || !f.isDirectory()){
    		f.mkdirs();
    	}
        return filepath;  
    }
    /**
     * 清除本地文件
     * @param file
     */
    public  void deleteFile(File file){
    	if(!file.exists())
    		return;
    	if(file.isDirectory()){
    		File[] fs = file.listFiles();
    		for(File f:fs){
    			deleteFile(f);
    		}
    	}else{
    		file.deleteOnExit();
    	}
    	
    	return;
    }

	public String getDownLoadFileDir() {
		return downLoadFileDir;
	}

	public void setDownLoadFileDir(String downLoadFileDir) {
		this.downLoadFileDir = downLoadFileDir;
	}

	public String getIconDir() {
		return iconDir;
	}

	public void setIconDir(String iconDir) {
		this.iconDir = iconDir;
	}

	public String getZipDir() {
		File file=new File(zipDir);
		if(!file.exists()){
			file.mkdirs();
		}
		return zipDir;
	}

	public void setZipDir(String zipDir) {
		this.zipDir = zipDir;
	}

	public String getDebugInfo() {
		return debugInfo;
	}

	public void setDebugInfo(String debugInfo) {
		this.debugInfo = debugInfo;
	}
	
	
}
