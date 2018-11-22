package zyxhj.org.cn.utils;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

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
	public static LoadingCache<Long, UserSession> SESSION_CACHE = CacheBuilder.newBuilder()
			.expireAfterAccess(1, TimeUnit.HOURS)// 缓存对象有效时间，30分钟
			.maximumSize(10000).// 最大缓存对象数量，
			build(new CacheLoader<Long, UserSession>() {

				@Override
				/** 当本地缓存命没有中时，调用load方法获取结果并将结果缓存 **/
				public UserSession load(Long id) {
					// 内存中没有，先从session库中查询，如果session中没有则返回空
					// TODO 目前没有做session库，默认为空
					return null;
				}
			});

}
