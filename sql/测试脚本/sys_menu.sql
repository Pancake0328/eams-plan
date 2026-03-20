-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 192.168.5.175    Database: eams-test
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

--SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '7896c5ec-09b4-11f1-b795-6a5883946176:1-957';

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
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,0,'仪表盘','MENU','dashboard:view','/dashboard','Dashboard','DataLine',1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(2,0,'系统管理','DIR',NULL,'/system',NULL,'Setting',2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(3,0,'资产管理','DIR',NULL,'/asset',NULL,'Grid',3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(4,0,'采购管理','MENU','purchase:list','/purchase','PurchaseManagement','ShoppingCart',4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(5,0,'人员管理','DIR',NULL,'/personnel',NULL,'User',5,0,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(6,0,'生命周期与盘点','DIR',NULL,'/lifecycle',NULL,'Refresh',6,0,0,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:12'),(8,2,'角色管理','MENU','system:role:list','/role','RoleManagement','UserFilled',2,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:11'),(9,2,'用户管理','MENU','system:user:list','/user','UserManagement','User',3,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-11 14:09:05'),(10,2,'部门管理','MENU','system:department:list','/departments','DepartmentManagement','OfficeBuilding',1,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:11'),(11,2,'菜单管理','MENU','system:permission:list','/permissions','PermissionManagement','Key',4,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:11'),(13,3,'全部资产','MENU','asset:info:list','/assets','AssetManagement','List',1,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:10'),(14,3,'资产分类','MENU','asset:category:list','/categories','CategoryManagement','Files',2,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:10'),(15,3,'流转记录','MENU','asset:record:list','/records','RecordManagement','Document',3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(16,0,'资产生命周期','MENU','lifecycle:list','/lifecycle','LifecycleManagement','Clock',7,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:12'),(17,0,'盘点管理','MENU','inventory:list','/inventory','InventoryManagement','Checked',6,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:12'),(18,0,'全部报修','DIR',NULL,'/repair-management',NULL,'Tools',5,1,1,NULL,0,'2026-02-08 21:46:36','2026-03-20 12:20:12'),(20,8,'新增角色','BUTTON','system:role:add',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(21,8,'编辑角色','BUTTON','system:role:edit',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(22,8,'删除角色','BUTTON','system:role:delete',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(23,8,'分配权限','BUTTON','system:role:permission',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(24,8,'查看角色','BUTTON','system:role:view',NULL,NULL,NULL,5,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(25,8,'更新状态','BUTTON','system:role:status',NULL,NULL,NULL,6,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(26,9,'新增用户','BUTTON','system:user:add',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(27,9,'编辑用户','BUTTON','system:user:edit',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(28,9,'删除用户','BUTTON','system:user:delete',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(29,9,'分配角色','BUTTON','system:user:assign',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(30,9,'查看用户','BUTTON','system:user:view',NULL,NULL,NULL,5,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(31,9,'更新状态','BUTTON','system:user:status',NULL,NULL,NULL,6,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(32,9,'重置密码','BUTTON','system:user:reset',NULL,NULL,NULL,7,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(33,10,'新增部门','BUTTON','system:department:add',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(34,10,'编辑部门','BUTTON','system:department:edit',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(35,10,'删除部门','BUTTON','system:department:delete',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(36,10,'查看部门','BUTTON','system:department:view',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(37,11,'新增权限','BUTTON','system:permission:add',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(38,11,'编辑权限','BUTTON','system:permission:edit',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(39,11,'删除权限','BUTTON','system:permission:delete',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(47,13,'编辑资产','BUTTON','asset:info:edit',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(49,13,'查看资产','BUTTON','asset:info:view',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(51,13,'资产入库','BUTTON','asset:record:in',NULL,NULL,NULL,6,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(52,13,'分配资产','BUTTON','asset:record:allocate',NULL,NULL,NULL,7,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(53,13,'调拨资产','BUTTON','asset:record:transfer',NULL,NULL,NULL,8,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(54,13,'归还资产','BUTTON','asset:record:return',NULL,NULL,NULL,9,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(55,13,'报废资产','BUTTON','asset:record:scrap',NULL,NULL,NULL,10,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(56,13,'送修资产','BUTTON','asset:record:repair',NULL,NULL,NULL,11,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(58,13,'查看流转历史','BUTTON','asset:record:history',NULL,NULL,NULL,13,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(59,14,'新增分类','BUTTON','asset:category:add',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(60,14,'编辑分类','BUTTON','asset:category:edit',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(61,14,'删除分类','BUTTON','asset:category:delete',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(62,14,'查看分类','BUTTON','asset:category:view',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(63,16,'创建生命周期','BUTTON','lifecycle:create',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(64,16,'查询历史','BUTTON','lifecycle:history',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(65,16,'当前阶段','BUTTON','lifecycle:current',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(67,17,'创建盘点','BUTTON','inventory:create',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(68,17,'开始盘点','BUTTON','inventory:start',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(69,17,'执行盘点','BUTTON','inventory:execute',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(70,17,'完成盘点','BUTTON','inventory:complete',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(71,17,'取消盘点','BUTTON','inventory:cancel',NULL,NULL,NULL,5,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(72,17,'查看盘点','BUTTON','inventory:view',NULL,NULL,NULL,6,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(73,18,'创建报修','BUTTON','repair:create',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(74,18,'审批报修','BUTTON','repair:approve',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(75,18,'开始维修','BUTTON','repair:start',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(76,18,'完成维修','BUTTON','repair:complete',NULL,NULL,NULL,4,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(77,18,'查看报修','BUTTON','repair:view',NULL,NULL,NULL,5,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(78,4,'新建采购','BUTTON','purchase:create',NULL,NULL,NULL,1,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(79,4,'查看采购','BUTTON','purchase:view',NULL,NULL,NULL,2,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(80,4,'取消采购','BUTTON','purchase:cancel',NULL,NULL,NULL,3,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(89,4,'账单列表','BUTTON','finance:bill:list',NULL,NULL,NULL,6,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(90,4,'生成账单','BUTTON','finance:bill:generate',NULL,NULL,NULL,7,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(91,4,'确认账单','BUTTON','finance:bill:confirm',NULL,NULL,NULL,8,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(92,4,'删除账单','BUTTON','finance:bill:delete',NULL,NULL,NULL,9,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(93,4,'查看账单','BUTTON','finance:bill:view',NULL,NULL,NULL,10,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(94,4,'资金统计','BUTTON','finance:statistics:view',NULL,NULL,NULL,11,1,1,NULL,0,'2026-02-08 21:46:36','2026-02-08 21:46:36'),(95,3,'持有资产','MENU','asset:info:my:list','/my-assets','MyAssetManagement','List',2,1,1,'资产管理-持有资产',0,'2026-03-11 14:09:05','2026-03-20 12:20:13'),(96,18,'我的报修','MENU','repair:own:list','/my-repairs','RepairManagement','Tools',2,1,1,'报修管理-我的报修',0,'2026-03-11 14:30:28','2026-03-20 12:20:12'),(97,18,'全部报修','MENU','repair:list','/repair','RepairManagement','Tools',1,1,1,'报修管理-全部报修',0,'2026-03-20 19:37:55','2026-03-20 12:20:12');
INSERT INTO `sys_menu` VALUES
(98,3,'申请审核管理','MENU','asset:usage:list','/usage-applications','UsageApplicationManagement','List',4,1,1,'资产管理-申请审核管理',0,'2026-03-20 21:30:00','2026-03-20 21:30:00'),
(99,13,'申请使用','BUTTON','asset:usage:apply',NULL,NULL,NULL,12,1,1,'全部资产-申请使用',0,'2026-03-20 21:30:00','2026-03-20 21:30:00'),
(100,98,'审核申请','BUTTON','asset:usage:audit',NULL,NULL,NULL,1,1,1,'申请审核管理-审核申请',0,'2026-03-20 21:30:00','2026-03-20 21:30:00');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-20 20:54:15
