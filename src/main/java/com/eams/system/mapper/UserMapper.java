package com.eams.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.system.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper 接口
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
