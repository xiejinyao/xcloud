package com.xjinyao.xcloud.core.rule.constant;

/**
 * 规则常量
 */
public class RuleConstant {

    public static final String LOCALHOST = "localhost";
    public static final String LOCALHOST_IP = "127.0.0.1";

    public static final String ALL = "all";
    public static final String BLACKLIST_OPEN = "1";
    public static final String BLACKLIST_CLOSE = "0";

    public static final String COLLECTIONS_DEFAULT_SEPARATOR = ",";

    private static final String BLACKLIST_CACHE_BLACKLIST_KEY_PREFIX = "xcloud:rule:blacklist:";
    private static final String CACHE_ROUTER_KEY_PREFIX = "xcloud:rule:routerStatusList:";
    private static final String CACHE_API_KEY_PREFIX = "xcloud:rule:apiStatusList:";

    public static String getBlackListCacheKey(String ip) {
        if (LOCALHOST.equalsIgnoreCase(ip)) {
            ip = LOCALHOST_IP;
        }
        return String.format("%s%s", BLACKLIST_CACHE_BLACKLIST_KEY_PREFIX, ip);
    }

    public static String getBlackListCacheKey() {
        return String.format("%s" + ALL, BLACKLIST_CACHE_BLACKLIST_KEY_PREFIX);
    }

    public static String getRouterCacheKey() {
        return String.format("%s" + ALL, CACHE_ROUTER_KEY_PREFIX);
    }

    public static String getApiCacheKey() {
        return String.format("%s" + ALL, CACHE_API_KEY_PREFIX);
    }

    public static String getApiCacheKey(String serviceId) {
        return String.format("%s" + serviceId, CACHE_API_KEY_PREFIX);
    }

}
