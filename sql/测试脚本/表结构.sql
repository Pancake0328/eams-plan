-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 192.168.5.175    Database: eams_plan
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- GTID state at the beginning of the backup 
--

--SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '7896c5ec-09b4-11f1-b795-6a5883946176:1-947';

--
-- Table structure for table `asset_category`
--

DROP TABLE IF EXISTS `asset_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父分类ID，0表示顶级分类',
  `category_name` varchar(50) NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) DEFAULT NULL COMMENT '分类编码',
  `sort_order` int DEFAULT '0' COMMENT '排序顺序',
  `description` varchar(200) DEFAULT NULL COMMENT '分类描述',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_info`
--

DROP TABLE IF EXISTS `asset_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '资产ID',
  `asset_number` varchar(50) NOT NULL COMMENT '资产编号（唯一）',
  `asset_name` varchar(100) NOT NULL COMMENT '资产名称',
  `category_id` bigint NOT NULL COMMENT '资产分类ID',
  `purchase_detail_id` bigint DEFAULT NULL COMMENT '采购明细ID',
  `purchase_amount` decimal(15,2) DEFAULT NULL COMMENT '采购金额',
  `purchase_date` date DEFAULT NULL COMMENT '采购日期',
  `department_id` bigint DEFAULT NULL COMMENT '使用部门ID',
  `custodian` varchar(50) DEFAULT NULL COMMENT '责任人',
  `asset_status` tinyint NOT NULL DEFAULT '1' COMMENT '资产状态：0-采购，1-闲置，2-使用中，3-维修中，4-报废，6-取消采购',
  `specifications` text COMMENT '规格型号',
  `manufacturer` varchar(100) DEFAULT NULL COMMENT '生产厂商',
  `remark` text COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_asset_number` (`asset_number`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_asset_status` (`asset_status`),
  KEY `idx_department_id` (`department_id`)
) ENGINE=InnoDB AUTO_INCREMENT=551 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_inventory`
--

DROP TABLE IF EXISTS `asset_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inventory_number` varchar(50) NOT NULL COMMENT '盘点编号',
  `inventory_name` varchar(100) NOT NULL COMMENT '盘点名称',
  `inventory_type` int NOT NULL COMMENT '盘点类型：1-全面盘点 2-抽样盘点 3-专项盘点',
  `sample_count` int DEFAULT NULL COMMENT '抽样数量',
  `category_id` bigint DEFAULT NULL COMMENT '专项盘点分类ID',
  `plan_start_date` date NOT NULL COMMENT '计划开始日期',
  `plan_end_date` date NOT NULL COMMENT '计划结束日期',
  `actual_start_date` date DEFAULT NULL COMMENT '实际开始日期',
  `actual_end_date` date DEFAULT NULL COMMENT '实际结束日期',
  `inventory_status` int NOT NULL DEFAULT '1' COMMENT '盘点状态：1-计划中 2-进行中 3-已完成 4-已取消',
  `total_count` int DEFAULT '0' COMMENT '应盘资产数量',
  `actual_count` int DEFAULT '0' COMMENT '实盘资产数量',
  `normal_count` int DEFAULT '0' COMMENT '正常资产数量',
  `abnormal_count` int DEFAULT '0' COMMENT '异常资产数量',
  `creator` varchar(50) NOT NULL COMMENT '创建人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_inventory_number` (`inventory_number`),
  KEY `idx_inventory_status` (`inventory_status`),
  KEY `idx_plan_date` (`plan_start_date`,`plan_end_date`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产盘点计划表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_inventory_detail`
--

DROP TABLE IF EXISTS `asset_inventory_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_inventory_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `inventory_id` bigint NOT NULL COMMENT '盘点计划ID',
  `asset_id` bigint NOT NULL COMMENT '资产ID',
  `asset_number` varchar(50) NOT NULL COMMENT '资产编号',
  `asset_name` varchar(100) NOT NULL COMMENT '资产名称',
  `expected_location` varchar(100) DEFAULT NULL COMMENT '预期位置',
  `actual_location` varchar(100) DEFAULT NULL COMMENT '实际位置',
  `inventory_result` int NOT NULL DEFAULT '1' COMMENT '盘点结果：1-未盘点 2-正常 3-位置异常 4-状态异常 5-丢失',
  `inventory_person` varchar(50) DEFAULT NULL COMMENT '盘点人',
  `inventory_time` datetime DEFAULT NULL COMMENT '盘点时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_inventory_id` (`inventory_id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_inventory_result` (`inventory_result`)
) ENGINE=InnoDB AUTO_INCREMENT=929 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产盘点明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_lifecycle`
--

DROP TABLE IF EXISTS `asset_lifecycle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_lifecycle` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `asset_id` bigint NOT NULL COMMENT '资产ID',
  `from_department_id` bigint DEFAULT NULL COMMENT '变更前部门ID',
  `from_department` varchar(100) DEFAULT NULL COMMENT '变更前部门',
  `to_department_id` bigint DEFAULT NULL COMMENT '变更后部门ID',
  `to_department` varchar(100) DEFAULT NULL COMMENT '变更后部门',
  `from_custodian` varchar(50) DEFAULT NULL COMMENT '变更前责任人',
  `to_custodian` varchar(50) DEFAULT NULL COMMENT '变更后责任人',
  `stage` int NOT NULL COMMENT '生命周期阶段：1-购入 2-使用中 3-维修中 4-闲置 5-报废 6-取消采购',
  `previous_stage` int DEFAULT NULL COMMENT '上一阶段',
  `stage_date` date NOT NULL COMMENT '阶段变更日期',
  `reason` varchar(500) DEFAULT NULL COMMENT '变更原因',
  `operator` varchar(50) NOT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_stage` (`stage`),
  KEY `idx_stage_date` (`stage_date`)
) ENGINE=InnoDB AUTO_INCREMENT=1375 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产生命周期记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_number_sequence`
--

DROP TABLE IF EXISTS `asset_number_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_number_sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '序列ID',
  `prefix` varchar(20) NOT NULL COMMENT '编号前缀',
  `current_number` int NOT NULL DEFAULT '0' COMMENT '当前序号',
  `date_part` varchar(10) DEFAULT NULL COMMENT '日期部分（可选）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prefix_date` (`prefix`,`date_part`)
) ENGINE=InnoDB AUTO_INCREMENT=352 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产编号序列表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_purchase_detail`
--

DROP TABLE IF EXISTS `asset_purchase_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_purchase_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `purchase_id` bigint NOT NULL COMMENT '采购单ID',
  `asset_name` varchar(200) NOT NULL COMMENT '资产名称',
  `category_id` bigint DEFAULT NULL COMMENT '分类ID',
  `specifications` varchar(500) DEFAULT NULL COMMENT '规格型号',
  `manufacturer` varchar(200) DEFAULT NULL COMMENT '生产厂商',
  `unit_price` decimal(15,2) DEFAULT NULL COMMENT '单价',
  `quantity` int DEFAULT '1' COMMENT '采购数量',
  `inbound_quantity` int DEFAULT '0' COMMENT '已入库数量',
  `total_amount` decimal(15,2) DEFAULT NULL COMMENT '小计金额',
  `expected_life` int DEFAULT NULL COMMENT '预计使用年限',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `detail_status` int DEFAULT '1' COMMENT '明细状态：1-待入库，2-已入库',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_purchase_id` (`purchase_id`),
  KEY `idx_detail_status` (`detail_status`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产采购明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_purchase_order`
--

DROP TABLE IF EXISTS `asset_purchase_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_purchase_order` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '采购单ID',
  `purchase_number` varchar(50) NOT NULL COMMENT '采购单号',
  `purchase_date` date NOT NULL COMMENT '采购日期',
  `supplier` varchar(200) DEFAULT NULL COMMENT '供应商',
  `total_amount` decimal(15,2) DEFAULT '0.00' COMMENT '采购总金额',
  `purchase_status` int DEFAULT '1' COMMENT '采购状态：1-待入库，2-部分入库，3-已入库，4-已取消',
  `applicant_id` bigint DEFAULT NULL COMMENT '申请人用户ID',
  `approver` varchar(100) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `remark` text COMMENT '备注',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `purchase_number` (`purchase_number`),
  KEY `idx_purchase_number` (`purchase_number`),
  KEY `idx_purchase_status` (`purchase_status`),
  KEY `idx_purchase_date` (`purchase_date`),
  KEY `idx_purchase_applicant` (`applicant_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产采购单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_record`
--

DROP TABLE IF EXISTS `asset_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `asset_id` bigint NOT NULL COMMENT '资产ID',
  `record_type` tinyint NOT NULL COMMENT '记录类型：1-入库，2-分配，3-调拨，4-归还，5-报废，6-送修，7-维修完成，8-报修拒绝',
  `from_department_id` bigint DEFAULT NULL COMMENT '原部门ID',
  `from_department` varchar(100) DEFAULT NULL COMMENT '原部门名称',
  `to_department_id` bigint DEFAULT NULL COMMENT '目标部门ID',
  `to_department` varchar(100) DEFAULT NULL COMMENT '目标部门名称',
  `from_custodian` varchar(50) DEFAULT NULL COMMENT '原责任人',
  `to_custodian` varchar(50) DEFAULT NULL COMMENT '目标责任人',
  `old_status` tinyint DEFAULT NULL COMMENT '原状态',
  `new_status` tinyint DEFAULT NULL COMMENT '新状态',
  `remark` text COMMENT '备注',
  `operator` varchar(50) NOT NULL COMMENT '操作人',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_record_type` (`record_type`),
  KEY `idx_operate_time` (`operate_time`)
) ENGINE=InnoDB AUTO_INCREMENT=271 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产流转记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `asset_repair`
--

DROP TABLE IF EXISTS `asset_repair`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asset_repair` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `repair_number` varchar(50) NOT NULL COMMENT '报修编号',
  `asset_id` bigint NOT NULL COMMENT '资产ID',
  `asset_number` varchar(50) NOT NULL COMMENT '资产编号',
  `asset_name` varchar(100) NOT NULL COMMENT '资产名称',
  `fault_description` varchar(1000) NOT NULL COMMENT '故障描述',
  `repair_type` int NOT NULL COMMENT '报修类型：1-日常维修 2-故障维修 3-预防性维修',
  `repair_priority` int NOT NULL DEFAULT '2' COMMENT '优先级：1-紧急 2-普通 3-低',
  `reporter` varchar(50) NOT NULL COMMENT '报修人',
  `report_time` datetime NOT NULL COMMENT '报修时间',
  `repair_status` int NOT NULL DEFAULT '1' COMMENT '报修状态：1-待审批 2-已审批 3-维修中 4-已完成 5-已拒绝',
  `original_status` tinyint DEFAULT NULL COMMENT '报修前资产状态',
  `approver` varchar(50) DEFAULT NULL COMMENT '审批人',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `repair_person` varchar(50) DEFAULT NULL COMMENT '维修人',
  `repair_start_time` datetime DEFAULT NULL COMMENT '维修开始时间',
  `repair_end_time` datetime DEFAULT NULL COMMENT '维修完成时间',
  `repair_cost` decimal(10,2) DEFAULT '0.00' COMMENT '维修费用',
  `repair_result` varchar(500) DEFAULT NULL COMMENT '维修结果',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_repair_number` (`repair_number`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_repair_status` (`repair_status`),
  KEY `idx_report_time` (`report_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资产报修记录表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门ID，0表示顶级部门',
  `dept_name` varchar(50) NOT NULL COMMENT '部门名称',
  `dept_code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `sort_order` int DEFAULT '0' COMMENT '显示顺序',
  `status` tinyint DEFAULT '1' COMMENT '状态：1-正常，0-停用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_dept_code` (`dept_code`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID，0表示顶级菜单',
  `menu_name` varchar(100) NOT NULL COMMENT '菜单名称',
  `menu_type` varchar(20) NOT NULL COMMENT '菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮',
  `permission_code` varchar(200) DEFAULT NULL COMMENT '权限标识（如：system:user:add）',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
  `sort_order` int DEFAULT '0' COMMENT '排序号',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `visible` tinyint(1) DEFAULT '1' COMMENT '是否可见：1-可见，0-隐藏',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_menu_type` (`menu_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码（唯一）',
  `role_name` varchar(100) NOT NULL COMMENT '角色名称',
  `status` tinyint(1) DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_code` (`role_code`),
  KEY `idx_role_code` (`role_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=304 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '工号',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `department_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-正常',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记：0-未删除，1-已删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_department_id` (`department_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-20 20:44:35
