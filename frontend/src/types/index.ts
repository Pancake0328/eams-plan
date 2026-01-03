/**
 * é€šç”¨å“åº”ç»“æœæ¥å£
 */
export interface Result<T = any> {
    code: number
    message: string
    data: T
    timestamp: number
}

/**
 * åˆ†é¡µå“åº”æ¥å£
 */
export interface PageResult<T> {
    records: T[]
    total: number
    size: number
    current: number
    pages: number
}

/**
 * ç”¨æˆ·ä¿¡æ¯æ¥å£
 */
export interface User {
    id: number
    username: string
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    status: number
    createTime: string
    updateTime: string
}

/**
 * åˆ›å»ºç”¨æˆ·è¯·æ±‚
 */
export interface UserCreateRequest {
    username: string
    password: string
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    status?: number
}

/**
 * æ›´æ–°ç”¨æˆ·è¯·æ±‚
 */
export interface UserUpdateRequest {
    nickname: string
    email?: string
    phone?: string
    avatar?: string
    status?: number
}

/**
 * é‡ç½®å¯†ç è¯·æ±‚
 */
export interface ResetPasswordRequest {
    newPassword: string
}

/**
 * ç”¨æˆ·åˆ†é¡µæŸ¥è¯¢å‚æ•°
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
 * ç™»å½•è¯·æ±‚
 */
export interface LoginRequest {
    username: string
    password: string
}

/**
 * ç™»å½•å“åº”
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
    }
}

/**
 * èµ„äº§åˆ†ç±»
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
 * èµ„äº§åˆ†ç±»æ ‘èŠ‚ç‚¹
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
 * åˆ›å»ºèµ„äº§åˆ†ç±»è¯·æ±‚
 */
export interface CategoryCreateRequest {
    parentId: number
    categoryName: string
    categoryCode?: string
    sortOrder?: number
    description?: string
}

/**
 * æ›´æ–°èµ„äº§åˆ†ç±»è¯·æ±‚
 */
export interface CategoryUpdateRequest {
    categoryName: string
    categoryCode?: string
    sortOrder?: number
    description?: string
}

/**
 * èµ„äº§ä¿¡æ¯
 */
export interface Asset {
    id: number
    assetNumber: string
    assetName: string
    categoryId: number
    categoryName?: string
    purchaseAmount?: number
    purchaseDate?: string
    department?: string
    custodian?: string
    assetStatus: number
    assetStatusText?: string
    specifications?: string
    manufacturer?: string
    remark?: string
    createTime: string
    updateTime: string
}

/**
 * åˆ›å»ºèµ„äº§è¯·æ±‚
 */
export interface AssetCreateRequest {
    assetName: string
    categoryId: number
    purchaseAmount?: number
    purchaseDate?: string
    department?: string
    custodian?: string
    assetStatus?: number
    specifications?: string
    manufacturer?: string
    remark?: string
}

/**
 * æ›´æ–°èµ„äº§è¯·æ±‚
 */
export interface AssetUpdateRequest {
    assetName: string
    categoryId: number
    purchaseAmount?: number
    purchaseDate?: string
    department?: string
    custodian?: string
    assetStatus?: number
    specifications?: string
    manufacturer?: string
    remark?: string
}

/**
 * èµ„äº§åˆ†é¡µæŸ¥è¯¢æ¡ä»¶
 */
export interface AssetPageQuery {
    current?: number
    size?: number
    assetNumber?: string
    assetName?: string
    categoryId?: number
    department?: string
    custodian?: string
    assetStatus?: number
}

/**
 * ×Ê²úÁ÷×ª¼ÇÂ¼
 */
export interface AssetRecord {
  id: number
  assetId: number
  assetNumber?: string
  assetName?: string
  recordType: number
  recordTypeText?: string
  fromDepartment?: string
  toDepartment?: string
  fromCustodian?: string
  toCustodian?: string
  oldStatus?: number
  oldStatusText?: string
  newStatus?: number
  newStatusText?: string
  remark?: string
  operator: string
  operateTime: string
  createTime: string
}

/**
 * ´´½¨Á÷×ª¼ÇÂ¼ÇëÇó
 */
export interface RecordCreateRequest {
  assetId: number
  toDepartment?: string
  toCustodian?: string
  remark?: string
}

/**
 * Á÷×ª¼ÇÂ¼·ÖÒ³²éÑ¯Ìõ¼ş
 */
export interface RecordPageQuery {
  current?: number
  size?: number
  assetId?: number
  recordType?: number
  operator?: string
}

// ================================================================
// ²¿ÃÅ¹ÜÀíÀàĞÍ
// ================================================================

/**
 * ²¿ÃÅĞÅÏ¢
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
 * ´´½¨²¿ÃÅÇëÇó
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
 * ¸üĞÂ²¿ÃÅÇëÇó
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
// Ô±¹¤¹ÜÀíÀàĞÍ
// ================================================================

/**
 * Ô±¹¤ĞÅÏ¢
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
 * ´´½¨Ô±¹¤ÇëÇó
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
 * ¸üĞÂÔ±¹¤ÇëÇó
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
 * Ô±¹¤·ÖÒ³²éÑ¯
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
// ×Ê²ú·ÖÅäÀàĞÍ
// ================================================================

/**
 * ×Ê²ú·ÖÅäÇëÇó
 */
export interface AssetAssignRequest {
  assetId: number
  toEmpId: number
  toDeptId?: number
  remark?: string
}

/**
 * ×Ê²ú·ÖÅä¼ÇÂ¼
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
 * ×Ê²ú·ÖÅä¼ÇÂ¼²éÑ¯
 */
export interface AssignRecordPageQuery {
  current?: number
  size?: number
  assetId?: number
  empId?: number
  assignType?: number
}

// ================================================================
// ÕËµ¥Óë×Ê½ğ¹ÜÀíÀàĞÍ
// ================================================================

/**
 * ²É¹º¼ÇÂ¼
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
 * ²É¹º¼ÇÂ¼´´½¨ÇëÇó
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
 * ÕÛ¾ÉĞÅÏ¢
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
 * ÕËµ¥
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
 * ×Ê½ğÍ³¼Æ
 */
export interface FinanceStatistics {
  dimension: string
  assetCount: number
  totalPurchaseAmount: number
  totalDepreciation: number
  totalNetValue: number
  averagePrice: number
}
