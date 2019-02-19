package zyxhj.utils;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import zyxhj.core.domain.UserRole;
import zyxhj.core.domain.UserSession;

/**
 * 缓存中心，项目中所有缓存集中在这里
 *
 */
public class CacheCenter {

	/**
	 * session 缓存</br>
	 * 为了避免反复请求Session存储，增加了此缓存</br>
	 * 有效期30分钟，最大缓存对象数量10000个（内存缓存1万用户，足够大了）</br>
	 * 缓存有效期1小时，OTS存储有效期2天
	 */
	public static Cache<Long, UserSession> SESSION_CACHE = CacheBuilder.newBuilder()//
			.expireAfterAccess(2, TimeUnit.DAYS)// 缓存对象有效时间，2天
			.maximumSize(100000)// 最大缓存对象数量，十万
			.build();

	public static Cache<Long, UserRole> USER_ROLE_CACHE = CacheBuilder.newBuilder()//
			.expireAfterAccess(5, TimeUnit.MINUTES)// 缓存对象有效时间，2天
			.maximumSize(100)// 最大缓存对象数量
			.build();

}
