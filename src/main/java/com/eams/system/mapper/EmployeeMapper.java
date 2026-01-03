package com.eams.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eams.system.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工Mapper
 *
 * @author EAMS Team
 * @since 2026-01-03
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
