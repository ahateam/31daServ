package zyxhj.utils.api;

/**
 * 基本ResultCode
 */
public interface BaseRC {

	// SERVICE级别的ResultCode
	static final RC SUCCESS = new RC("succ", "success");// 成功
	static final RC FAILURE = new RC("fail", "failure");// 失败
	static final RC NOT_FINISHED = new RC("fail", "not finished");// 失败

	static final RC REDIRECT = new RC("000003", "redirect 302");// 302重定向

	static final RC EMPTY_REQUEST = new RC("000004", "empty request");// 空请求
	static final RC AUTH_ERROR = new RC("000005", "auth error");// 验证错误
	static final RC SERVER_ERROR = new RC("000006", "server error");// 未知的系统错误
	static final RC METHOD_NOT_FOUND = new RC("000007", "method not found");// 未找到对应的方法

	static final RC REQUEST_CONTENT_EMPTY = new RC("000008", "request content empty");// 请求内容为空
	static final RC REQUEST_CONTENT_NOT_JSON = new RC("000009", "request content not json");// 请求内容不是JSON格式
	static final RC REQUEST_PARAMS_EMPTY = new RC("000010", "request params empty");// 请求参数为空

	static final RC REQUEST_PARSE_ERROR = new RC("000011", "request parse error");// 请求解析错误
	static final RC REQUEST_PARAMS_ERROR = new RC("000012", "request params error");// 请求参数解析错误

	static final RC SERVER_DEFAULT_ERROR = new RC("000013", "server default error");// 默认错误

	static final RC SERVER_OBJECT_NULL = new RC("000014", "object is null");// 对象空了

	// 常用Repository层的错误

	static final RC REPOSITORY_SQL_PREPARE_ERROR = new RC("repo-0001", "sql prepare error");// SQL指令错误
	static final RC REPOSITORY_SQL_EXECUTE_ERROR = new RC("repo-0002", "sql execute error");// SQL指令执行错误
	static final RC REPOSITORY_COUNT_OFFSET_ERROR = new RC("repo-0003", "count or offset parameter error");// 分页参数错误
	static final RC REPOSITORY_INSERT_ERROR = new RC("repo-0004", "insert error");// 插入错误
	static final RC REPOSITORY_UPDATE_ERROR = new RC("repo-0005", "update error");// 更新错误
	static final RC REPOSITORY_DELETE_ERROR = new RC("repo-0006", "delete error");// 插入错误
	static final RC REPOSITORY_GET_ERROR = new RC("repo-0007", "get error");// 获取错误
	static final RC REPOSITORY_PUT_ERROR = new RC("repo-0008", "put error");// PUT错误
	static final RC REPOSITORY_SET_ERROR = new RC("repo-0009", "set error");// SET错误
	static final RC REPOSITORY_CONNECTION_ERROR = new RC("repo-0010", "connection error");// 插入错误
	static final RC REPOSITORY_QUERY_EMPTY = new RC("repo-0011", "query empty");// 什么都没查到

	// 开放搜索错误
	static final RC OPENSEARCH_APPNAME_BLANK = new RC("os-0001", "opensearch appname blank");// 开放搜索appneme错误
	static final RC OPENSEARCH_SEARCH_ERROR = new RC("os-0002", "opensearch search error");// 开放搜索错误

	// 标签系统错误
	static final RC TAG_NAME_EXIST_IN_APP_GROUP = new RC("tag-0010", "tag name exist in this app and group");// 标签已存在

	// 专栏系统错误
	static final RC CHANNEL_TITLE_EXIST_IN_APP = new RC("channel-0010", "channel title exist in this app");// 专栏已存在

	// 应用模块错误
	static final RC APP_NOT_EXIST = new RC("app-0010", "app not exist");// 应用不存在
	static final RC APP_AUTH_PENDING = new RC("app-0011", "app is pending");// 应用正在审核
	static final RC APP_AUTH_STOPED = new RC("app-0012", "app is stoped");// 应用停止使用
	static final RC APP_AUTH_DELETED = new RC("app-0013", "app is deleted");// 应用已经删除
	static final RC APP_AUTH_UNKNOWN_STATUS = new RC("app-0014", "unknown app status");// 未知应用状态

	// 用户系统错误
	static final RC USER_EXIST = new RC("user-0010", "user exist");// 用户已存在
	static final RC USER_NOT_EXIST = new RC("user-0011", "user not exist");// 用户不存在
	static final RC USER_PWD_ERROR = new RC("user-0012", "user password error");// 用户密码错误
	static final RC USER_LEVEL_ANONYMOUS = new RC("user-0013", "user level anonymous");// 用户是匿名权限
	static final RC USER_NOT_LOGIN = new RC("user-0014", "user not login");// 用户是匿名权限
	static final RC USER_AUTH_PENDING = new RC("user-0015", "user is pending");// 用户正在审核
	static final RC USER_AUTH_STOPED = new RC("user-0016", "user is stoped");// 用户停止使用
	static final RC USER_AUTH_DELETED = new RC("user-0017", "user is deleted");// 用户已经删除
	static final RC USER_AUTH_UNKNOWN_STATUS = new RC("user-0018", "unknown user status");// 未知用户状态
	static final RC USER_AUTH_NOT_ADMIN = new RC("user-0019", "must be an admin user");// 不是管理员账号

	// CMS内容管理系统错误
	static final RC CMS_CONTENT_NOT_EXISET = new RC("cms-0010", "Content不存在");
	static final RC CMS_CONTENTSET_NOT_EXISET = new RC("cms-0011", "ContentSet不存在");
	static final RC CMS_CONTENTSET_STATUS_NOT_USE = new RC("cms-0012", "ContentSet状态不可用");
	static final RC CMS_CONTENTSET_EXIST = new RC("cms-0013", "ContentSet已经存在");

	// Media媒体采集系统错误
	static final RC MEDIA_TUNNEL_NOT_EXISET = new RC("media-0010", "Tunnel不存在");

	// 订单系统错误
	static final RC ORDER_NOT_EXIST = new RC("orde-0010", "order not exist");// 订单不存在
	static final RC NOT_COLLECTION = new RC("book-0001", "not collect this object");// 没有收藏该对象

	/**
	 * 支付交易通道链接构造错误
	 */
	static final RC TRADE_WAY_URL_BUILD_ERROR = new RC("300006", "支付交易通道链接构造错误");
	static final RC STAR_NOT_EXISET = new RC("300007", "STAR不存在");
	static final RC STARAVATOR_NOT_EXISET = new RC("300008", "STARVATOR不存在");
	static final RC POST_NOT_EXISET = new RC("300009", "post不存在");
	static final RC STARGROUP_NOT_EXISET = new RC("300010", "StarGroup不存在");

	/**
	 * 集体经济项目错误
	 * 
	 */
	static final RC ECM_ORG_EXIST = new RC("ecm-0010", "组织已存在");
	static final RC ECM_TICKET_EXIST = new RC("ecm-0011", "已经投过票");
	static final RC ECM_VOIT_PROJECT_NOTEXIST = new RC("ecm-0012", "投票项目不存在");

	static final RC ECM_VOIT_PROJECT_STARTED = new RC("ecm-0013", "投票项目已经开始，无法再更改");
	static final RC ECM_VOIT_PROJECT_NOTSTARTED = new RC("ecm-0014", "投票项目未开始");
	static final RC ECM_VOIT_PROJECT_FINISHED = new RC("ecm-0015", "投票项目已结束");
	static final RC ECM_VOTE_ORGROLE_ERROR = new RC("ecm-0016", "该用户与组织信息不符");
	static final RC ECM_VOTE_NO_PROMISS = new RC("ecm-0017", "该用户没有权限");

	static final RC ECM_VOIT_NOTEXIST = new RC("ecm-0018", "投票不存在");
	static final RC ECM_VOIT_STATUS_ERROR = new RC("ecm-0019", "投票状态不允许该操作");
	static final RC ECM_ORG_USER_EXIST = new RC("ecm-0020", "组织的用户已存在");

}
