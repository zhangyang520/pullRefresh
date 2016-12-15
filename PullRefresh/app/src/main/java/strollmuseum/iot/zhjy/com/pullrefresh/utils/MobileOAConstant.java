package strollmuseum.iot.zhjy.com.pullrefresh.utils;


/**
 * 
 * @author zhangyang
 * 
 * @日期 2016年3月17日上午10:39:08
 * 
 * @描述  常量类
 */
public class MobileOAConstant{

	//新添加的常量 message中所用的常量
	public static final int REQUEST_FIRST_PAGE=101;//请求第一页的数据
	public static final int REFRESH_PAGE=102;//刷新第一页的数据
	public static final int LOAD_MORE=103;//加载下一页的数据
	public static final int ERROR_CODE=104;//请求错误的状态码!
	public static final String ACTION_CODE="action_code";
	
	//请求网络失败
	public static final int REQUEST_ERROR=104;//请求网络失败
		
	public static final int INTERNET_TIME_OUT=6000;//网络超时时间
	public static final int CLICK_GAP_TIME=500;//点击间隔时间
	
	public static final int POLICY_PAGER_COUNT=2;//行政收文的页面的个数
	public static final int FILETRANSACTION_PAGER_COUNT=4;//文件传阅的页面的个数
	public static final int RECEIVE_NOTIFICATION_COUNT=2;//公文通知的页面的个数
	
	public static final int NET_GAP_TIME=15000;//访问网络的间隔时间
	
	public static final String ERROR_CONTENT="content";//内容描述的键
	
	public static final int POLICY_PAGE_SIZE=10;//每页的个数
	
	public static final int CURR_REQUEST_EXPIRY=20000;//请求
	
	//http://192.168.80.148:8080/AroundServer/servlet/AroundServlet?hashCode=66667&count=9527
	public static final String CESHIURL="http://127.0.0.1:8090/policyReceDetail";
	public static final String CESHIURL_FILE_TRANS="http://127.0.0.1:8090/FileTransDetail";
	public static final String CESHIURL_POLICY_RECE="http://127.0.0.1:8090";
	
	public static final String WORKITEM_CONSTANT="finishActivity";//工作项的id常量--当无工作操作的时候:为该常量
	
	public static final String LOADING_URL_KEY="LOADING_URL_KEY";//加载项LoadingActivity的键
	public static final String DOWNLOAD_FILE_NAME="DOWNLOAD_FILE_NAME";//下载文件的名称
	public static final String LOAD_STATE="LOAD_STATE";//加载的状态键
	public static final String LOAD_FAILURE="LOAD_FAILURE";//加载的状态键的值:加载失败
	public static final String LOAD_SUCCESS="LOAD_SUCCESS";//加载的状态键的值:加载成功
	public static final int LOAD_RESULT_CODE=101;//加载界面LoadingActivity的结果值
	public static final String LOAD_CONTENT="LOAD_CONTENT";//加载的状态网络返回值的键
	public static final int LOAD_REQUEST_CODE=102;//请求加载界面的请求码
	
	public static final int BANLI_REQUEST_CODE=201;//办理的请求码
	
	public static final int FILE_TRANS_2_PUTONG_REQUEST_CODE=301;//文件传阅中转为普通的请求码
	public static final int FILE_TRANS_2_IMPORTANT_REQUEST_CODE=302;//文件传阅中转为重要的请求码
	public static final int FILE_TRANS_2_DELETE_REQUEST_CODE = 303;//文件传阅中删除的请求码
	public static final int SYNCHRONIZE_DATA_REQUEST_CODE=304;//同步用户数据的请求码
	public static final int FILE_TRANS_TRANSFILE_REQUEST_CODE=305;//文件传阅之下载文件
	public static final int MODIFY_PASSWORD_REQUEST_CODE=306;//修改密码
	
	public static final int NOTIFICATION_DAIYUE_REQUEST_CODE=307;//待阅详情的请求码！
	public static final int NOTIFICATION_DAIYUE_RESULT_CODE=308;//待阅详情的结束码！
	
	public static final int FILE_TRANSACTION_SELECT_FILE_REQUEST_CODE=500;//选择文件的请求码
	public static final String DOWNLOAD_DIR_NAME="download";//下载的目录名称
	public static final String ICON_DIR_NAME="ICON";//图片的目录
	public static final String ZIP_DIR_NAME="ZIP";//zip文件的目录
	public static final String DEBUG_INFO="DEBUG_INFO";//日志文件的目录
	
	public static final String ZIP_FILE_NAME="FILE_TRANS.zip";
	public static final String LOADING_TITLE_KEY="LOADING_TITLE_KEY";//进度条activity类中intent传递的url键名称
	public static final String LOADING_IS_PREVENT_BACK="LOADING_IS_PREVENT_BACK";//进度条activity类中intent传递的是否阻止back键键名称
	
	
	public static final int LOAD_DATA_ERROR=103;//请求数据异常
	public static final int LOAD_DATA_FAIL=104;//请求数据失败
	
	public static final String MODIFY_PASSWORD_KEY  = "MODIFY_PASSWORD_KEY";//修改密码key
	
	public static final int NO_NODE_ERROR_CODE=201;//不存在对应对 Node节点！
	public static final int NO_DATAS_ERROR_CODE=202;//不存在对应对 Node节点！
	
	public static final int WEIBAN_ACTIVITY_REQUEST_CODE=401;//未办loadingPager的请求码
	public static final int WEIBAN_ACTIVITY_RESULT_CODE=402;//未办的loadingPager的结束码
	
	public static final int FILR_SEND_REQUEST_CODE=501;//文件传阅上传文件的请求码
	
//	public static final String nameSpacing="http://www.primeton.com/serverInterface";
//	public static final String endPoint="http://192.168.81.107:8082/default/serverInterface?wsdl";
	
	//方法名
	public static final String CHECK_LOGIN="checkLogin";//检查登录
	public static final String SYNBASICRESOURCE="synBasicResource";//同步用户数据
	public static final String QUERYRECEIVEDOC = "queryReceivedDoc";//已收文件
	public static final String QUERYPUBLISHEDDOC = "queryPublishedDoc";//已发文件
	public static final String QUERYIMPORTENTDOC = "queryImportentDoc";//重要文件
	public static final String DELETEDOCUMENT = "deleteDocument";//删除文件
	public static final String SWITCH_DOC_TYPE="switchDocType";//改变文档类型
	public static final String UPDATE_USE_RPASSWORDS = "updateUserPasswords";//更新密码
	public static final String DEFAULT_DEPT_STRING="";//缺省的部门字符串
	public static final String POLICY_RECEIVE_YIBAN = "mobileAdvancedQueryDoneTasks";//行政收文已办
	public static final String POLICY_RECEIVE_WEIBAN = "mobileAdvancedQueryToDoTasks";//行政收文未办
	public static final String RECEIVE_NOTIFICATION_DATEIL = "MobileNotificationsQueryDoneInfo";//公文通知详情
	public static final String RECEIVE_NOTIFICATION_YIYUE = "mobileQueryNotificationsr";//公文通知已阅
	public static final String RECEIVE_NOTIFICATION_DAIYUE = "mobileQueryNotifications";//公文通知待阅
	
	public static final String POLICY_RECEIVE_YIBAN_DATEIL = "MobileAdvancedQueryDoneInfo";//行政收文已办详情
	public static final String POLICY_RECEIVE_WEIBAN_DATEIL = "MobileAdvancedQueryDoneInfo";//行政收文未办详情
	public static final String FILE_SEND_ADDDOC = "addDoc";//文件上传的参数方法名
	
	public static final String FILE_TRANS_TRANCODES_POND = "transpond";//文件传阅模块的文件转发按钮接口
	public static final String POLICY_RECEIVE_ACTION="commonSubmit";//办理操作接口
	
	public static final String FILE_TRANS_DETAIL="queryDoc";//文件传阅的条目的详情页
	
	public static final String OPERATION_PROCESS="mobileQueryWorkitems";//查看操作流程的方法名
	//----------------
	public static final int PAGE_SIZE=10;//分页请求的个数
	public static final String CONSTANT_NULL_SOAP_STRING="anyType{}";//soap中的空字符串
	public static final String CONSTANT_FILE_TRANS_NO_DESCRIPTION="暂无描述";//文件传阅中描述信息常量值——暂无
	public static final String CONSTANT_TYPE_CODE="emp";//员工角色
	
	public static final String CONSTANT_NODE_NAME_DELEGATE=",";
	public static final String CONSTANT_OPINION_SIGNNAME_DELEAGET="";//意见签名的分隔符
	public static final String CONSTANT_OPINION_BETWEEN_DELEAGET="";//意见之间的分隔符
	
	//下载文件的路径
//	public static final String DOWNLOAD_URL="http://192.168.100.44:8081/gzw_oa/mobileInterface/fileDownLoad/DownloadFilesServlet.jsp";
	public static final String DOWNLOAD_URL="http://218.249.38.236:8081/gzw_oa/mobileInterface/fileDownLoad/DownloadFilesServlet.jsp";
//	public static final String UPLOAD_URL="http://192.168.81.199:8089/default/mobileInterface/fileDownLoad/UpFilesServlet.jsp";
	
	public static final String UPLOAD_URL = "http://218.249.38.236:8081/gzw_oa/mobileInterface/fileDownLoad/UpFilesServlet.jsp";
}
