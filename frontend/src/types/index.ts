/**
 * 閫氱敤鍝嶅簲缁撴灉鎺ュ彛
 */
export interface Result<T = any> {
  code: number
  message: string
  data: T
  timestamp: number
}

/**
 * 鍒嗛〉鍝嶅簲鎺ュ彛
 */
export interface PageResult<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

/**
 * 鐢ㄦ埛淇℃伅鎺ュ彛
 */
export interface User {
  id: number
  username: string
  nickname: string
  email?: string
  phone?: string
  avatar?: string
  departmentId?: number
  departmentName?: string
  status: number
  createTime: string
  updateTime: string
}

/**
 * 鍒涘缓鐢ㄦ埛璇锋眰
 */
export interface UserCreateRequest {
  username: string
  password: string
  nickname: string
  email?: string
  phone?: string
  avatar?: string
  departmentId: number
  status?: number
}

/**
 * 鏇存柊鐢ㄦ埛璇锋眰
 */
export interface UserUpdateRequest {
  nickname: string
  email?: string
  phone?: string
  avatar?: string
  departmentId?: number
  status?: number
}

/**
 * 閲嶇疆瀵嗙爜璇锋眰
 */
export interface ResetPasswordRequest {
  newPassword: string
}

/**
 * 鐢ㄦ埛鍒嗛〉鏌ヨ�鍙傛暟
 */
export interface UserPageQuery {
  current?: number
  size?: number
  username?: string
  nickname?: string
  phone?: string
  status?: number
}

/**
 * 鐧诲綍璇锋眰
 */
export interface LoginRequest {
  username: string
  password: string
}

/**
 * 鐧诲綍鍝嶅簲
 */
export interface LoginResponse {
  token: string
  tokenType: string
  expiresIn: number
  userInfo: {
    id: number
    username: string
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    departmentId?: number
  }
}

/**
 * 璧勪骇鍒嗙被
 */
export interface AssetCategory {
  id: number
  parentId: number
  categoryName: string
  categoryCode?: string
  sortOrder?: number
  description?: string
  createTime: string
  updateTime: string
}

/**
 * 璧勪骇鍒嗙被鏍戣妭鐐�
 */
export interface CategoryTreeNode {
  id: number
  parentId: number
  categoryName: string
  categoryCode?: string
  sortOrder?: number
  description?: string
  children?: CategoryTreeNode[]
}

/**
 * 鍒涘缓璧勪骇鍒嗙被璇锋眰
 */
export interface CategoryCreateRequest {
  parentId: number
  categoryName: string
  categoryCode?: string
  sortOrder?: number
  description?: string
}

/**
 * 鏇存柊璧勪骇鍒嗙被璇锋眰
 */
export interface CategoryUpdateRequest {
  categoryName: string
  categoryCode?: string
  sortOrder?: number
  description?: string
}

/**
 * 璧勪骇淇℃伅
 */
export interface Asset {
  id: number
  assetNumber: string
  assetName: string
  categoryId: number
  categoryName?: string
  purchaseAmount?: number
  purchaseDate?: string
  departmentId?: number
  departmentName?: string
  custodian?: string
  custodianName?: string
  assetStatus: number
  assetStatusText?: string
  specifications?: string
  manufacturer?: string
  remark?: string
  createTime: string
  updateTime: string
}

/**
 * 鍒涘缓璧勪骇璇锋眰
 */
export interface AssetCreateRequest {
  assetName: string
  categoryId: number
  purchaseAmount?: number
  purchaseDate?: string
  departmentId?: number
  assetStatus?: number
  specifications?: string
  manufacturer?: string
  remark?: string
}

/**
 * 鏇存柊璧勪骇璇锋眰
 */
export interface AssetUpdateRequest {
  assetName: string
  categoryId: number
  purchaseAmount?: number
  purchaseDate?: string
  departmentId?: number
  assetStatus?: number
  specifications?: string
  manufacturer?: string
  remark?: string
}

/**
 * 璧勪骇鍒嗛〉鏌ヨ�鏉′欢
 */
export interface AssetPageQuery {
  current?: number
  size?: number
  assetNumber?: string
  assetName?: string
  categoryId?: number
  departmentId?: number
  custodian?: string
  assetStatus?: number
}

/**
 * 资产流转记录
 */
export interface AssetRecord {
  id: number
  assetId: number
  assetNumber?: string
  assetName?: string
  recordType: number
  recordTypeText?: string
  fromDepartmentId?: number
  fromDepartment?: string
  toDepartmentId?: number
  toDepartment?: string
  fromCustodian?: string
  fromCustodianName?: string
  toCustodian?: string
  toCustodianName?: string
  oldStatus?: number
  oldStatusText?: string
  newStatus?: number
  newStatusText?: string
  remark?: string
  operator: string
  operatorName?: string
  operateTime: string
  createTime: string
}

/**
 * 创建流转记录请求
 */
export interface RecordCreateRequest {
  assetId: number
  toDepartmentId?: number
  toCustodian?: string
  remark?: string
}

/**
 * 流转记录分页查询条件
 */
export interface RecordPageQuery {
  current?: number
  size?: number
  assetId?: number
  recordType?: number
  operator?: string
}

// ================================================================
// 部门管理类型
// ================================================================

/**
 * 部门信息
 */
export interface Department {
  id: number
  parentId: number
  deptName: string
  deptCode?: string
  leader?: string
  phone?: string
  email?: string
  sortOrder?: number
  status: number
  remark?: string
  createTime: string
  children?: Department[]
}

/**
 * 创建部门请求
 */
export interface DepartmentCreateRequest {
  parentId: number
  deptName: string
  deptCode?: string
  leader?: string
  phone?: string
  email?: string
  sortOrder?: number
  status?: number
  remark?: string
}

/**
 * 更新部门请求
 */
export interface DepartmentUpdateRequest {
  deptName?: string
  deptCode?: string
  leader?: string
  phone?: string
  email?: string
  sortOrder?: number
  status?: number
  remark?: string
}

// ================================================================
// 员工管理类型
// ================================================================

/**
 * 员工信息
 */
export interface Employee {
  id: number
  empNo: string
  empName: string
  deptId?: number
  deptName?: string
  position?: string
  phone?: string
  email?: string
  gender?: number
  genderText?: string
  entryDate?: string
  status: number
  statusText?: string
  remark?: string
  createTime: string
  updateTime: string
}

/**
 * 创建员工请求
 */
export interface EmployeeCreateRequest {
  empNo: string
  empName: string
  deptId: number
  position?: string
  phone?: string
  email?: string
  gender?: number
  entryDate?: string
  status?: number
  remark?: string
}

/**
 * 更新员工请求
 */
export interface EmployeeUpdateRequest {
  empName?: string
  deptId?: number
  position?: string
  phone?: string
  email?: string
  gender?: number
  entryDate?: string
  status?: number
  remark?: string
}

/**
 * 员工分页查询
 */
export interface EmployeePageQuery {
  current?: number
  size?: number
  empNo?: string
  empName?: string
  deptId?: number
  status?: number
}

// ================================================================
// 资产分配类型
// ================================================================

/**
 * 资产分配请求
 */
export interface AssetAssignRequest {
  assetId: number
  toEmpId: number
  toDeptId?: number
  remark?: string
}

/**
 * 资产分配记录
 */
export interface AssetAssignRecord {
  id: number
  assetId: number
  assetNumber?: string
  assetName?: string
  assignType: number
  assignTypeText?: string
  fromEmpId?: number
  fromEmpName?: string
  toEmpId?: number
  toEmpName?: string
  fromDeptId?: number
  fromDeptName?: string
  toDeptId?: number
  toDeptName?: string
  assignDate: string
  returnDate?: string
  remark?: string
  operator: string
  operateTime: string
  createTime: string
}

/**
 * 资产分配记录查询
 */
export interface AssignRecordPageQuery {
  current?: number
  size?: number
  assetId?: number
  empId?: number
  assignType?: number
}

// ================================================================
// 账单与资金管理类型
// ================================================================

/**
 * 采购记录
 */
export interface Purchase {
  id: number
  assetId: number
  assetNumber?: string
  assetName?: string
  purchaseAmount: number
  supplierName?: string
  supplierContact?: string
  invoiceNumber?: string
  purchaseDate: string
  paymentMethod?: string
  warrantyPeriod?: number
  remark?: string
  purchaser?: string
  approvalStatus: number
  approvalStatusText?: string
  approver?: string
  approvalTime?: string
  createTime: string
}

/**
 * 采购记录创建请求
 */
export interface PurchaseCreateRequest {
  assetId: number
  purchaseAmount: number
  supplierName?: string
  supplierContact?: string
  invoiceNumber?: string
  purchaseDate: string
  paymentMethod?: string
  warrantyPeriod?: number
  remark?: string
}

/**
 * 折旧信息
 */
export interface DepreciationInfo {
  assetId: number
  assetNumber: string
  assetName: string
  purchaseAmount: number
  residualValue: number
  monthlyDepreciation: number
  accumulatedDepreciation: number
  netValue: number
  depreciationRate: number
  usefulLife: number
  usedMonths: number
  remainingMonths: number
}

/**
 * 账单
 */
export interface Bill {
  id: number
  billNumber: string
  billType: number
  billTypeText?: string
  billYear: number
  billMonth?: number
  totalPurchaseAmount: number
  totalDepreciationAmount: number
  totalAssetValue: number
  totalNetValue: number
  billStatus: number
  billStatusText?: string
  generateTime: string
  confirmTime?: string
  remark?: string
  createTime: string
}

/**
 * 资金统计
 */
export interface FinanceStatistics {
  dimension: string
  assetCount: number
  totalPurchaseAmount: number
  totalDepreciation: number
  totalNetValue: number
  averagePrice: number
}
// ==================== 仪表盘统计 ====================
export interface DashboardStatistics {
  totalAssets: number
  totalAmount: number
  inUseAssets: number
  idleAssets: number
  repairingAssets: number
  scrapAssets: number
  idleRate: number
  scrapRate: number
  usageRate: number
  currentMonthNewAssets: number
  pendingRepairs: number
  ongoingInventories: number
}

export interface AssetDistribution {
  name: string
  count: number
  amount: number
  percentage: number
}

export interface TimeTrendStatistics {
  period: string
  newAssets: number
  scrapAssets: number
  totalAssets: number
}

// ================================================================
// 生命周期、盘点与报修类型
// ================================================================

export interface Lifecycle {
  id: number
  assetId: number
  assetNumber?: string
  assetName?: string
  fromDepartmentId?: number
  fromDepartment?: string
  toDepartmentId?: number
  toDepartment?: string
  fromCustodian?: string
  fromCustodianName?: string
  toCustodian?: string
  toCustodianName?: string
  stage: number
  stageText?: string
  previousStage?: number
  previousStageText?: string
  stageDate: string
  reason?: string
  operator: string
  operatorName?: string
  remark?: string
  createTime: string
}

export interface LifecycleCreateRequest {
  assetId: number
  stage: number
  stageDate: string
  reason: string
  operator: string
  remark?: string
}

export interface Inventory {
  id: number
  inventoryNumber: string
  inventoryName: string
  inventoryType: number
  inventoryTypeText?: string
  categoryId?: number
  categoryName?: string
  planStartDate: string
  planEndDate: string
  actualStartDate?: string
  actualEndDate?: string
  inventoryStatus: number
  inventoryStatusText?: string
  totalCount: number
  actualCount: number
  normalCount: number
  abnormalCount: number
  completionRate?: number
  creator: string
  remark?: string
  createTime: string
  details?: InventoryDetail[]
}

export interface InventoryDetail {
  id: number
  inventoryId: number
  assetId: number
  assetNumber?: string
  assetName?: string
  expectedLocation?: string
  actualLocation?: string
  inventoryResult: number
  inventoryResultText?: string
  inventoryPerson?: string
  inventoryTime?: string
  remark?: string
}

export interface InventoryCreateRequest {
  inventoryName: string
  inventoryType: number
  planStartDate: string
  planEndDate: string
  creator: string
  categoryId?: number
  remark?: string
}

export interface InventoryExecuteRequest {
  detailId: number
  actualLocation?: string
  inventoryResult: number
  inventoryPerson: string
  remark?: string
}

export interface Repair {
  id: number
  repairNumber: string
  assetId: number
  assetNumber?: string
  assetName?: string
  faultDescription: string
  repairType: number
  repairTypeText?: string
  repairPriority: number
  repairPriorityText?: string
  reporter: string
  reporterName?: string
  reportTime: string
  repairStatus: number
  repairStatusText?: string
  approver?: string
  approverName?: string
  approvalTime?: string
  repairPerson?: string
  repairPersonName?: string
  repairStartTime?: string
  repairEndTime?: string
  repairCost?: number
  repairResult?: string
  remark?: string
  createTime: string
}

export interface RepairCreateRequest {
  assetId: number
  faultDescription: string
  repairType: number
  repairPriority: number
  reporter: string
  remark?: string
}
