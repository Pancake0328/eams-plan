package com.eams.common.constant;

/**
 * 通用常量
 *
 * @author Pancake
 * @since 2026-01-03
 */
public interface CommonConstant {

    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    Integer FAIL = 500;

    /**
     * 删除标记 - 已删除
     */
    Integer DELETED = 1;

    /**
     * 删除标记 - 未删除
     */
    Integer NOT_DELETED = 0;

    /**
     * 状态 - 正常
     */
    Integer STATUS_NORMAL = 1;

    /**
     * 状态 - 禁用
     */
    Integer STATUS_DISABLED = 0;

    /**
     * 是
     */
    String YES = "Y";

    /**
     * 否
     */
    String NO = "N";

    /**
     * 默认页码
     */
    Long DEFAULT_PAGE = 1L;

    /**
     * 默认每页数量
     */
    Long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 最大每页数量
     */
    Long MAX_PAGE_SIZE = 100L;
}
