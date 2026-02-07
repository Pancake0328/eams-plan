create table asset_assign_record
(
    id           bigint auto_increment comment '记录ID'
        primary key,
    asset_id     bigint                             not null comment '资产ID',
    assign_type  tinyint                            not null comment '分配类型：1-分配给员工，2-回收，3-部门内调拨',
    from_emp_id  bigint                             null comment '原员工ID',
    to_emp_id    bigint                             null comment '目标员工ID',
    from_dept_id bigint                             null comment '原部门ID',
    to_dept_id   bigint                             null comment '目标部门ID',
    assign_date  datetime                           not null comment '分配日期',
    return_date  datetime                           null comment '归还日期',
    remark       varchar(500)                       null comment '备注',
    operator     varchar(50)                        not null comment '操作人',
    operate_time datetime default CURRENT_TIMESTAMP null comment '操作时间',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '资产分配记录表' charset = utf8mb4;

create index idx_asset_id
    on asset_assign_record (asset_id);

create index idx_assign_type
    on asset_assign_record (assign_type);

create index idx_from_emp_id
    on asset_assign_record (from_emp_id);

create index idx_to_emp_id
    on asset_assign_record (to_emp_id);

create table asset_bill
(
    id                        bigint auto_increment comment '账单ID'
        primary key,
    bill_number               varchar(50)                              not null comment '账单编号',
    bill_type                 tinyint                                  not null comment '账单类型：1-月度账单，2-年度账单',
    bill_year                 int                                      not null comment '账单年份',
    bill_month                int                                      null comment '账单月份',
    total_purchase_amount     decimal(15, 2) default 0.00              null comment '采购总金额',
    total_depreciation_amount decimal(15, 2) default 0.00              null comment '折旧总金额',
    total_asset_value         decimal(15, 2) default 0.00              null comment '资产总价值',
    total_net_value           decimal(15, 2) default 0.00              null comment '资产净值总额',
    bill_status               tinyint        default 0                 null comment '账单状态：0-草稿，1-已生成，2-已确认',
    generate_time             datetime                                 null comment '生成时间',
    confirm_time              datetime                                 null comment '确认时间',
    remark                    varchar(500)                             null comment '备注',
    create_time               datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time               datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted                   tinyint        default 0                 null comment '逻辑删除：0-未删除，1-已删除',
    constraint uk_bill_number
        unique (bill_number)
)
    comment '资产账单表' charset = utf8mb4;

create index idx_bill_year_month
    on asset_bill (bill_year, bill_month);

create table asset_bill_detail
(
    id                       bigint auto_increment comment '明细ID'
        primary key,
    bill_id                  bigint                                   not null comment '账单ID',
    asset_id                 bigint                                   not null comment '资产ID',
    asset_number             varchar(50)                              null comment '资产编号',
    asset_name               varchar(200)                             null comment '资产名称',
    category_name            varchar(100)                             null comment '分类名称',
    department               varchar(100)                             null comment '使用部门名称',
    purchase_amount          decimal(15, 2) default 0.00              null comment '采购金额',
    accumulated_depreciation decimal(15, 2) default 0.00              null comment '累计折旧',
    current_depreciation     decimal(15, 2) default 0.00              null comment '本期折旧',
    net_value                decimal(15, 2) default 0.00              null comment '净值',
    depreciation_rate        decimal(5, 4)                            null comment '折旧率',
    useful_life              int                                      null comment '使用年限（月）',
    used_months              int            default 0                 null comment '已使用月数',
    asset_status             tinyint                                  null comment '资产状态：0-采购，1-闲置，2-使用中，3-维修中，4-报废，6-取消采购',
    create_time              datetime       default CURRENT_TIMESTAMP null comment '创建时间'
)
    comment '资产账单明细表' charset = utf8mb4;

create index idx_asset_id
    on asset_bill_detail (asset_id);

create index idx_bill_id
    on asset_bill_detail (bill_id);

create table asset_category
(
    id            bigint auto_increment comment '分类ID'
        primary key,
    parent_id     bigint   default 0                 null comment '父分类ID，0表示顶级分类',
    category_name varchar(50)                        not null comment '分类名称',
    category_code varchar(50)                        null comment '分类编码',
    sort_order    int      default 0                 null comment '排序顺序',
    description   varchar(200)                       null comment '分类描述',
    deleted       tinyint  default 0                 not null comment '删除标记：0-未删除，1-已删除',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_asset_category
        unique (category_code)
)
    comment '资产分类表' charset = utf8mb4;

create index idx_parent_id
    on asset_category (parent_id);

create table asset_info
(
    id              bigint auto_increment comment '资产ID'
        primary key,
    asset_number    varchar(50)                        not null comment '资产编号（唯一）',
    asset_name      varchar(100)                       not null comment '资产名称',
    category_id     bigint                             not null comment '资产分类ID',
    purchase_detail_id bigint                          null comment '采购明细ID',
    purchase_amount decimal(15, 2)                     null comment '采购金额',
    purchase_date   date                               null comment '采购日期',
    department_id   bigint                             null comment '使用部门ID',
    custodian       varchar(50)                        null comment '责任人',
    asset_status    tinyint  default 1                 not null comment '资产状态：0-采购，1-闲置，2-使用中，3-维修中，4-报废，6-取消采购',
    specifications  text                               null comment '规格型号',
    manufacturer    varchar(100)                       null comment '生产厂商',
    remark          text                               null comment '备注',
    deleted         tinyint  default 0                 not null comment '删除标记：0-未删除，1-已删除',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_asset_number
        unique (asset_number)
)
    comment '资产信息表' charset = utf8mb4;

create index idx_asset_status
    on asset_info (asset_status);

create index idx_category_id
    on asset_info (category_id);

create index idx_department_id
    on asset_info (department_id);

create table asset_inventory
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    inventory_number  varchar(50)                        not null comment '盘点编号',
    inventory_name    varchar(100)                       not null comment '盘点名称',
    inventory_type    int                                not null comment '盘点类型：1-全面盘点 2-抽样盘点 3-专项盘点',
    sample_count      int                                null comment '抽样数量',
    category_id       bigint                             null comment '专项盘点分类ID',
    plan_start_date   date                               not null comment '计划开始日期',
    plan_end_date     date                               not null comment '计划结束日期',
    actual_start_date date                               null comment '实际开始日期',
    actual_end_date   date                               null comment '实际结束日期',
    inventory_status  int      default 1                 not null comment '盘点状态：1-计划中 2-进行中 3-已完成 4-已取消',
    total_count       int      default 0                 null comment '应盘资产数量',
    actual_count      int      default 0                 null comment '实盘资产数量',
    normal_count      int      default 0                 null comment '正常资产数量',
    abnormal_count    int      default 0                 null comment '异常资产数量',
    creator           varchar(50)                        not null comment '创建人',
    remark            varchar(500)                       null comment '备注',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted           tinyint  default 0                 not null comment '逻辑删除：0-未删除 1-已删除',
    constraint uk_inventory_number
        unique (inventory_number)
)
    comment '资产盘点计划表' charset = utf8mb4;

create index idx_inventory_status
    on asset_inventory (inventory_status);

create index idx_plan_date
    on asset_inventory (plan_start_date, plan_end_date);

create table asset_inventory_detail
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    inventory_id      bigint                             not null comment '盘点计划ID',
    asset_id          bigint                             not null comment '资产ID',
    asset_number      varchar(50)                        not null comment '资产编号',
    asset_name        varchar(100)                       not null comment '资产名称',
    expected_location varchar(100)                       null comment '预期位置',
    actual_location   varchar(100)                       null comment '实际位置',
    inventory_result  int      default 1                 not null comment '盘点结果：1-未盘点 2-正常 3-位置异常 4-状态异常 5-丢失',
    inventory_person  varchar(50)                        null comment '盘点人',
    inventory_time    datetime                           null comment '盘点时间',
    remark            varchar(500)                       null comment '备注',
    create_time       datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted           tinyint  default 0                 not null comment '逻辑删除：0-未删除 1-已删除'
)
    comment '资产盘点明细表' charset = utf8mb4;

create index idx_asset_id
    on asset_inventory_detail (asset_id);

create index idx_inventory_id
    on asset_inventory_detail (inventory_id);

create index idx_inventory_result
    on asset_inventory_detail (inventory_result);

create table asset_lifecycle
(
    id             bigint auto_increment comment '主键ID'
        primary key,
    asset_id            bigint                             not null comment '资产ID',
    from_department_id  bigint                             null comment '变更前部门ID',
        from_department     varchar(100)                       null comment '变更前部门',
    to_department_id    bigint                             null comment '变更后部门ID',
    to_department       varchar(100)                       null comment '变更后部门',
    from_custodian      varchar(50)                        null comment '变更前责任人',
    to_custodian        varchar(50)                        null comment '变更后责任人',
    stage               int                                not null comment '生命周期阶段：1-购入 2-使用中 3-维修中 4-闲置 5-报废 6-取消采购',
    previous_stage      int                                null comment '上一阶段',
    stage_date          date                               not null comment '阶段变更日期',
    reason              varchar(500)                       null comment '变更原因',
    operator            varchar(50)                        not null comment '操作人',
    remark              varchar(500)                       null comment '备注',
    create_time         datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time         datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted             tinyint  default 0                 not null comment '逻辑删除：0-未删除 1-已删除'
)
    comment '资产生命周期记录表' charset = utf8mb4;

create index idx_asset_id
    on asset_lifecycle (asset_id);

create index idx_stage
    on asset_lifecycle (stage);

create index idx_stage_date
    on asset_lifecycle (stage_date);

create table asset_number_sequence
(
    id             bigint auto_increment comment '序列ID'
        primary key,
    prefix         varchar(20)                        not null comment '编号前缀',
    current_number int      default 0                 not null comment '当前序号',
    date_part      varchar(10)                        null comment '日期部分（可选）',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_prefix_date
        unique (prefix, date_part)
)
    comment '资产编号序列表' charset = utf8mb4;

insert into asset_number_sequence (prefix, current_number, date_part)
values ('AST', 0, null),
       ('PUR', 0, null);

create table asset_purchase
(
    id              bigint auto_increment comment '采购单ID'
        primary key,
    purchase_number varchar(50)                              not null comment '采购单号',
    purchase_date   date                                     not null comment '采购日期',
    supplier        varchar(200)                             null comment '供应商',
    total_amount    decimal(15, 2) default 0.00              null comment '采购总金额',
    purchase_status int            default 1                 null comment '采购状态：1-待入库，2-部分入库，3-已入库，4-已取消',
    applicant       varchar(100)                             null comment '申请人',
    approver        varchar(100)                             null comment '审批人',
    approve_time    datetime                                 null comment '审批时间',
    remark          text                                     null comment '备注',
    deleted         tinyint(1)     default 0                 null comment '删除标记',
    create_time     datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint purchase_number
        unique (purchase_number)
)
    comment '资产采购单表' charset = utf8mb4;

create index idx_purchase_date
    on asset_purchase (purchase_date);

create index idx_purchase_number
    on asset_purchase (purchase_number);

create index idx_purchase_status
    on asset_purchase (purchase_status);

create table asset_purchase_order
(
    id              bigint auto_increment comment '采购单ID'
        primary key,
    purchase_number varchar(50)                              not null comment '采购单号',
    purchase_date   date                                     not null comment '采购日期',
    supplier        varchar(200)                             null comment '供应商',
    total_amount    decimal(15, 2) default 0.00              null comment '采购总金额',
    purchase_status int            default 1                 null comment '采购状态：1-待入库，2-部分入库，3-已入库，4-已取消',
    applicant       varchar(100)                             null comment '申请人',
    approver        varchar(100)                             null comment '审批人',
    approve_time    datetime                                 null comment '审批时间',
    remark          text                                     null comment '备注',
    deleted         tinyint(1)     default 0                 null comment '删除标记',
    create_time     datetime       default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime       default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    applicant_id    bigint                                   null comment '申请人用户ID',
    constraint purchase_number
        unique (purchase_number)
)
    comment '资产采购单表' charset = utf8mb4;

create table asset_purchase_detail
(
    id               bigint auto_increment comment '明细ID'
        primary key,
    purchase_id      bigint                             not null comment '采购单ID',
    asset_name       varchar(200)                       not null comment '资产名称',
    category_id      bigint                             null comment '分类ID',
    specifications   varchar(500)                       null comment '规格型号',
    manufacturer     varchar(200)                       null comment '生产厂商',
    unit_price       decimal(15, 2)                     null comment '单价',
    quantity         int      default 1                 null comment '采购数量',
    inbound_quantity int      default 0                 null comment '已入库数量',
    total_amount     decimal(15, 2)                     null comment '小计金额',
    expected_life    int                                null comment '预计使用年限',
    remark           varchar(500)                       null comment '备注',
    detail_status    int      default 1                 null comment '明细状态：1-待入库，2-已入库',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint asset_purchase_detail_ibfk_1
        foreign key (purchase_id) references asset_purchase_order (id)
            on delete cascade
)
    comment '资产采购明细表' charset = utf8mb4;

create index idx_detail_status
    on asset_purchase_detail (detail_status);

create index idx_purchase_id
    on asset_purchase_detail (purchase_id);

create index idx_purchase_applicant
    on asset_purchase_order (applicant_id);

create index idx_purchase_date
    on asset_purchase_order (purchase_date);

create index idx_purchase_number
    on asset_purchase_order (purchase_number);

create index idx_purchase_status
    on asset_purchase_order (purchase_status);

create table asset_record
(
    id              bigint auto_increment comment '记录ID'
        primary key,
    asset_id        bigint                             not null comment '资产ID',
    record_type     tinyint                            not null comment '记录类型：1-入库，2-分配，3-调拨，4-归还，5-报废，6-送修，7-维修完成，8-报修拒绝',
    from_department_id bigint                          null comment '原部门ID',
    from_department varchar(100)                       null comment '原部门名称',
    to_department_id   bigint                          null comment '目标部门ID',
    to_department   varchar(100)                       null comment '目标部门名称',
    from_custodian  varchar(50)                        null comment '原责任人',
    to_custodian    varchar(50)                        null comment '目标责任人',
    old_status      tinyint                            null comment '原状态',
    new_status      tinyint                            null comment '新状态',
    remark          text                               null comment '备注',
    operator        varchar(50)                        not null comment '操作人',
    operate_time    datetime default CURRENT_TIMESTAMP not null comment '操作时间',
    deleted         tinyint  default 0                 not null comment '删除标记：0-未删除，1-已删除',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '资产流转记录表' charset = utf8mb4;

create index idx_asset_id
    on asset_record (asset_id);

create index idx_operate_time
    on asset_record (operate_time);

create index idx_record_type
    on asset_record (record_type);

create table asset_repair
(
    id                bigint auto_increment comment '主键ID'
        primary key,
    repair_number     varchar(50)                              not null comment '报修编号',
    asset_id          bigint                                   not null comment '资产ID',
    asset_number      varchar(50)                              not null comment '资产编号',
    asset_name        varchar(100)                             not null comment '资产名称',
    fault_description varchar(1000)                            not null comment '故障描述',
    repair_type       int                                      not null comment '报修类型：1-日常维修 2-故障维修 3-预防性维修',
    repair_priority   int            default 2                 not null comment '优先级：1-紧急 2-普通 3-低',
    reporter          varchar(50)                              not null comment '报修人',
    report_time       datetime                                 not null comment '报修时间',
    repair_status     int            default 1                 not null comment '报修状态：1-待审批 2-已审批 3-维修中 4-已完成 5-已拒绝',
    original_status   tinyint                                  null comment '报修前资产状态',
    approver          varchar(50)                              null comment '审批人',
    approval_time     datetime                                 null comment '审批时间',
    repair_person     varchar(50)                              null comment '维修人',
    repair_start_time datetime                                 null comment '维修开始时间',
    repair_end_time   datetime                                 null comment '维修完成时间',
    repair_cost       decimal(10, 2) default 0.00              null comment '维修费用',
    repair_result     varchar(500)                             null comment '维修结果',
    remark            varchar(500)                             null comment '备注',
    create_time       datetime       default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time       datetime       default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted           tinyint        default 0                 not null comment '逻辑删除：0-未删除 1-已删除',
    constraint uk_repair_number
        unique (repair_number)
)
    comment '资产报修记录表' charset = utf8mb4;

create index idx_asset_id
    on asset_repair (asset_id);

create index idx_repair_status
    on asset_repair (repair_status);

create index idx_report_time
    on asset_repair (report_time);

create table sys_dept
(
    id          bigint auto_increment comment '部门ID'
        primary key,
    parent_id   bigint   default 0                 not null comment '父部门ID，0表示顶级部门',
    dept_name   varchar(100)                       not null comment '部门名称',
    dept_code   varchar(50)                        null comment '部门编码',
    leader      varchar(50)                        null comment '部门负责人',
    phone       varchar(20)                        null comment '联系电话',
    email       varchar(100)                       null comment '邮箱',
    sort_order  int      default 0                 null comment '显示顺序',
    status      tinyint  default 1                 null comment '状态：1-正常，0-停用',
    remark      varchar(500)                       null comment '备注',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 null comment '删除标志：0-未删除，1-已删除'
)
    comment '部门表' charset = utf8mb4;

create index idx_dept_code
    on sys_dept (dept_code);

create index idx_parent_id
    on sys_dept (parent_id);

create table sys_employee
(
    id          bigint auto_increment comment '员工ID'
        primary key,
    emp_no      varchar(50)                        not null comment '员工工号',
    emp_name    varchar(100)                       not null comment '员工姓名',
    dept_id     bigint                             null comment '部门ID',
    position    varchar(100)                       null comment '职位',
    phone       varchar(20)                        null comment '手机号',
    email       varchar(100)                       null comment '邮箱',
    gender      tinyint                            null comment '性别：1-男，2-女',
    entry_date  date                               null comment '入职日期',
    status      tinyint  default 1                 null comment '状态：1-在职，0-离职',
    remark      varchar(500)                       null comment '备注',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    deleted     tinyint  default 0                 null comment '删除标志：0-未删除，1-已删除',
    constraint uk_emp_no
        unique (emp_no)
)
    comment '员工表' charset = utf8mb4;

create index idx_dept_id
    on sys_employee (dept_id);

create index idx_status
    on sys_employee (status);

create table sys_menu
(
    id              bigint auto_increment comment '菜单ID'
        primary key,
    parent_id       bigint     default 0                 null comment '父菜单ID，0表示顶级菜单',
    menu_name       varchar(100)                         not null comment '菜单名称',
    menu_type       varchar(20)                          not null comment '菜单类型：DIR-目录，MENU-菜单，BUTTON-按钮',
    permission_code varchar(200)                         null comment '权限标识（如：system:user:add）',
    path            varchar(200)                         null comment '路由路径',
    component       varchar(200)                         null comment '组件路径',
    icon            varchar(100)                         null comment '菜单图标',
    sort_order      int        default 0                 null comment '排序号',
    status          tinyint(1) default 1                 null comment '状态：1-启用，0-禁用',
    visible         tinyint(1) default 1                 null comment '是否可见：1-可见，0-隐藏',
    remark          varchar(500)                         null comment '备注',
    deleted         tinyint(1) default 0                 null comment '删除标记：0-未删除，1-已删除',
    create_time     datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '菜单权限表' charset = utf8mb4;

create index idx_menu_type
    on sys_menu (menu_type);

create index idx_parent_id
    on sys_menu (parent_id);

create index idx_status
    on sys_menu (status);

create table sys_notify
(
    id            bigint auto_increment comment '通知ID'
        primary key,
    notify_type   int                                  not null comment '通知类型：1-系统通知，2-到期提醒，3-报修提醒，4-盘点提醒',
    notify_level  int        default 1                 not null comment '通知级别：1-普通，2-重要，3-紧急',
    title         varchar(200)                         not null comment '通知标题',
    content       text                                 null comment '通知内容',
    receiver      varchar(100)                         null comment '接收人（用户名或角色）',
    receiver_type int        default 1                 null comment '接收人类型：1-用户，2-角色，3-全体',
    is_read       tinyint(1) default 0                 null comment '是否已读：0-未读，1-已读',
    read_time     datetime                             null comment '已读时间',
    related_id    bigint                               null comment '关联业务ID',
    related_type  varchar(50)                          null comment '关联业务类型：asset-资产，repair-报修，inventory-盘点',
    send_time     datetime                             null comment '发送时间',
    expire_time   datetime                             null comment '过期时间',
    deleted       tinyint(1) default 0                 null comment '删除标记：0-未删除，1-已删除',
    create_time   datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time   datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '系统通知表' charset = utf8mb4;

create index idx_is_read
    on sys_notify (is_read);

create index idx_notify_type
    on sys_notify (notify_type);

create index idx_receiver
    on sys_notify (receiver);

create index idx_send_time
    on sys_notify (send_time);

create table sys_role
(
    id          bigint auto_increment comment '角色ID'
        primary key,
    role_code   varchar(50)                          not null comment '角色编码（唯一）',
    role_name   varchar(100)                         not null comment '角色名称',
    status      tinyint(1) default 1                 null comment '状态：1-启用，0-禁用',
    remark      varchar(500)                         null comment '备注说明',
    deleted     tinyint(1) default 0                 null comment '删除标记：0-未删除，1-已删除',
    create_time datetime   default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime   default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint role_code
        unique (role_code)
)
    comment '系统角色表' charset = utf8mb4;

create index idx_role_code
    on sys_role (role_code);

create index idx_status
    on sys_role (status);

create table sys_role_menu
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    role_id     bigint                             not null comment '角色ID',
    menu_id     bigint                             not null comment '菜单ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_role_menu
        unique (role_id, menu_id)
)
    comment '角色菜单关联表' charset = utf8mb4;

create index idx_menu_id
    on sys_role_menu (menu_id);

create index idx_role_id
    on sys_role_menu (role_id);

create table sys_user
(
    id            bigint auto_increment comment '用户ID'
        primary key,
    username      varchar(50)                        not null comment '用户名',
    password      varchar(100)                       not null comment '密码（BCrypt加密）',
    nickname      varchar(50)                        null comment '昵称',
    email         varchar(100)                       null comment '邮箱',
    phone         varchar(20)                        null comment '手机号',
    avatar        varchar(255)                       null comment '头像URL',
    status        tinyint  default 1                 not null comment '状态：0-禁用，1-正常',
    deleted       tinyint  default 0                 not null comment '删除标记：0-未删除，1-已删除',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    department_id bigint                             null comment '所属部门ID',
    constraint uk_username
        unique (username)
)
    comment '系统用户表' charset = utf8mb4;

create index idx_user_department
    on sys_user (department_id);

create table sys_user_role
(
    id          bigint auto_increment comment '主键ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    role_id     bigint                             not null comment '角色ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    constraint uk_user_role
        unique (user_id, role_id)
)
    comment '用户角色关联表' charset = utf8mb4;

create index idx_role_id
    on sys_user_role (role_id);

create index idx_user_id
    on sys_user_role (user_id);

