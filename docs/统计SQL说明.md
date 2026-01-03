# 报表与仪表盘模块 - 统计SQL说明

## 一、仪表盘概览统计

### 1.1 资产总数和总金额
```sql
-- 资产总数
SELECT COUNT(*) AS total_assets FROM asset_info WHERE deleted = 0;

-- 资产总金额
SELECT SUM(purchase_amount) AS total_amount 
FROM asset_info 
WHERE deleted = 0;
```

### 1.2 按状态统计资产数量
```sql
SELECT 
    asset_status,
    COUNT(*) AS count,
    CASE asset_status
        WHEN 1 THEN '闲置'
        WHEN 2 THEN '使用中'
        WHEN 3 THEN '维修中'
        WHEN 4 THEN '报废'
        ELSE '未知'
    END AS status_text
FROM asset_info 
WHERE deleted = 0
GROUP BY asset_status;
```

**性能优化**：
- 在`asset_status`字段上建立索引
- 使用COUNT(*)而非COUNT(id)

### 1.3 计算闲置率、报废率、使用率
```sql
-- 一次性查询所有比率
SELECT 
    COUNT(*) AS total,
    SUM(CASE WHEN asset_status = 1 THEN 1 ELSE 0 END) AS idle_count,
    SUM(CASE WHEN asset_status = 2 THEN 1 ELSE 0 END) AS in_use_count,
    SUM(CASE WHEN asset_status = 4 THEN 1 ELSE 0 END) AS scrap_count,
    ROUND(SUM(CASE WHEN asset_status = 1 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS idle_rate,
    ROUND(SUM(CASE WHEN asset_status = 4 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS scrap_rate,
    ROUND(SUM(CASE WHEN asset_status = 2 THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS usage_rate
FROM asset_info 
WHERE deleted = 0;
```

**优化要点**：
- 单次聚合查询，避免多次扫描表
- CASE WHEN条件索引无法使用，但单次扫描已是最优

### 1.4 本月新增资产
```sql
SELECT COUNT(*) AS current_month_new
FROM asset_info 
WHERE deleted = 0
  AND purchase_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
  AND purchase_date < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH);
```

**优化建议**：
- 在`purchase_date`字段上建立索引
- 使用范围查询而非函数，便于索引利用

### 1.5 待审批报修和进行中盘点
```sql
-- 待审批报修
SELECT COUNT(*) FROM asset_repair WHERE repair_status = 1;

-- 进行中盘点
SELECT COUNT(*) FROM asset_inventory WHERE inventory_status = 2;
```

**索引优化**：
```sql
CREATE INDEX idx_repair_status ON asset_repair(repair_status);
CREATE INDEX idx_inventory_status ON asset_inventory(inventory_status);
```

---

## 二、资产分布统计

### 2.1 部门资产分布
```sql
SELECT 
    COALESCE(department, '未分配') AS department_name,
    COUNT(*) AS asset_count,
    SUM(purchase_amount) AS total_amount,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM asset_info WHERE deleted = 0), 2) AS percentage
FROM asset_info 
WHERE deleted = 0
GROUP BY department
ORDER BY asset_count DESC;
```

**性能优化**：
- 在`department`字段上建立索引
- 子查询会被缓存，避免重复计算

### 2.2 资产分类分布
```sql
SELECT 
    c.category_name,
    COUNT(a.id) AS asset_count,
    SUM(a.purchase_amount) AS total_amount,
    ROUND(COUNT(a.id) * 100.0 / (SELECT COUNT(*) FROM asset_info WHERE deleted = 0), 2) AS percentage
FROM asset_info a
INNER JOIN asset_category c ON a.category_id = c.id
WHERE a.deleted = 0
GROUP BY c.id, c.category_name
ORDER BY asset_count DESC;
```

**优化建议**：
- 在`asset_info.category_id`上建立索引（外键通常已有）
- 使用INNER JOIN过滤掉未分类资产

### 2.3 资产状态分布（含金额）
```sql
SELECT 
    CASE asset_status
        WHEN 1 THEN '闲置'
        WHEN 2 THEN '使用中'
        WHEN 3 THEN '维修中'
        WHEN 4 THEN '报废'
        ELSE '未知'
    END AS status_name,
    COUNT(*) AS asset_count,
    SUM(purchase_amount) AS total_amount,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM asset_info WHERE deleted = 0), 2) AS percentage
FROM asset_info 
WHERE deleted = 0
GROUP BY asset_status
ORDER BY asset_count DESC;
```

---

## 三、时间趋势统计

### 3.1 按月统计新增资产
```sql
SELECT 
    DATE_FORMAT(purchase_date, '%Y-%m') AS period,
    COUNT(*) AS new_assets
FROM asset_info
WHERE deleted = 0
  AND purchase_date BETWEEN ? AND ?
GROUP BY DATE_FORMAT(purchase_date, '%Y-%m')
ORDER BY period;
```

### 3.2 按月统计报废资产（基于生命周期表）
```sql
SELECT 
    DATE_FORMAT(stage_date, '%Y-%m') AS period,
    COUNT(*) AS scrap_assets
FROM asset_lifecycle
WHERE stage = 5  -- 报废状态
  AND stage_date BETWEEN ? AND ?
GROUP BY DATE_FORMAT(stage_date, '%Y-%m')
ORDER BY period;
```

**复合索引优化**：
```sql
-- 生命周期表复合索引
CREATE INDEX idx_lifecycle_stage_date ON asset_lifecycle(stage, stage_date);

-- 资产表购买日期索引
CREATE INDEX idx_asset_purchase_date ON asset_info(purchase_date);
```

### 3.3 完整时间趋势（包含期末资产总数）
```sql
WITH RECURSIVE months AS (
    SELECT DATE_FORMAT(?, '%Y-%m-01') AS month_start
    UNION ALL
    SELECT DATE_ADD(month_start, INTERVAL 1 MONTH)
    FROM months
    WHERE month_start < DATE_FORMAT(?, '%Y-%m-01')
)
SELECT 
    DATE_FORMAT(m.month_start, '%Y-%m') AS period,
    COALESCE(new.new_count, 0) AS new_assets,
    COALESCE(scrap.scrap_count, 0) AS scrap_assets,
    (SELECT COUNT(*) FROM asset_info 
     WHERE deleted = 0 
       AND purchase_date <= LAST_DAY(m.month_start)) AS total_assets
FROM months m
LEFT JOIN (
    SELECT DATE_FORMAT(purchase_date, '%Y-%m') AS month, COUNT(*) AS new_count
    FROM asset_info
    WHERE deleted = 0 AND purchase_date BETWEEN ? AND ?
    GROUP BY month
) new ON DATE_FORMAT(m.month_start, '%Y-%m') = new.month
LEFT JOIN (
    SELECT DATE_FORMAT(stage_date, '%Y-%m') AS month, COUNT(*) AS scrap_count
    FROM asset_lifecycle
    WHERE stage = 5 AND stage_date BETWEEN ? AND ?
    GROUP BY month
) scrap ON DATE_FORMAT(m.month_start, '%Y-%m') = scrap.month
ORDER BY period;
```

---

## 四、性能优化建议

### 4.1 必要索引

```sql
-- 资产信息表
CREATE INDEX idx_asset_status ON asset_info(asset_status);
CREATE INDEX idx_asset_department ON asset_info(department);
CREATE INDEX idx_asset_category ON asset_info(category_id);
CREATE INDEX idx_asset_purchase_date ON asset_info(purchase_date);

-- 生命周期表
CREATE INDEX idx_lifecycle_stage_date ON asset_lifecycle(stage, stage_date);
CREATE INDEX idx_lifecycle_asset ON asset_lifecycle(asset_id, stage_date);

-- 报修表
CREATE INDEX idx_repair_status ON asset_repair(repair_status);
CREATE INDEX idx_repair_asset ON asset_repair(asset_id, repair_status);

-- 盘点表
CREATE INDEX idx_inventory_status ON asset_inventory(inventory_status);
```

### 4.2 查询优化策略

1. **使用聚合查询代替多次查询**
   - 一次性计算所有统计指标
   - 减少数据库往返次数

2. **避免在WHERE中使用函数**
   ```sql
   -- 不推荐
   WHERE YEAR(purchase_date) = 2024
   
   -- 推荐
   WHERE purchase_date >= '2024-01-01' AND purchase_date < '2025-01-01'
   ```

3. **使用CASE WHEN进行条件聚合**
   ```sql
   SELECT 
       SUM(CASE WHEN asset_status = 1 THEN 1 ELSE 0 END) AS idle_count,
       SUM(CASE WHEN asset_status = 2 THEN 1 ELSE 0 END) AS in_use_count
   FROM asset_info;
   ```

4. **分页查询优化**
   ```sql
   -- 使用主键偏移
   SELECT * FROM asset_info 
   WHERE id > last_id 
   ORDER BY id 
   LIMIT 20;
   ```

### 4.3 缓存策略

1. **仪表盘数据缓存**
   - 统计数据可缓存5-15分钟
   - 使用Redis缓存热点数据

2. **定时预计算**
   - 每日凌晨生成统计报表
   - 存储到专门的统计表

3. **读写分离**
   - 统计查询走从库
   - 降低主库压力

---

## 五、数据一致性保证

### 5.1 软删除处理
所有查询必须加上`deleted = 0`条件，避免统计已删除数据。

### 5.2 NULL值处理
使用`COALESCE`或`IFNULL`处理NULL值：
```sql
SELECT COALESCE(department, '未分配') AS department FROM asset_info;
```

### 5.3 事务一致性
统计查询建议使用`READ COMMITTED`隔离级别，避免长事务。

---

## 六、监控与告警

### 6.1 慢查询监控
```sql
-- 开启慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2; -- 超过2秒记录

-- 分析慢查询
SELECT * FROM mysql.slow_log WHERE query_time > 2;
```

### 6.2 关键指标
- 仪表盘API响应时间 < 500ms
- Excel导出时间 < 5s（1000条记录）
- 数据库查询时间 < 200ms

---

## 七、实现说明

### 7.1 当前实现方式
- 使用Java Stream API进行内存聚合
- 适合中小规模数据（< 10万条）
- 简化代码逻辑，减少SQL复杂度

### 7.2 大数据场景优化
如果资产数量超过10万条，建议：
1. 将聚合逻辑下推到数据库
2. 使用MyBatis自定义SQL
3. 建立统计视图或物化表
4. 引入缓存层（Redis）

### 7.3 扩展方向
- 支持自定义时间维度（周、季度、年）
- 增加更多维度（地区、供应商等）
- 导出图表可视化（ECharts）
- 数据对比分析（同比、环比）
