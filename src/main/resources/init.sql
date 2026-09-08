-- =============================================
-- 数据库建表脚本（优化版）
-- 字符集：utf8mb4（支持 Emoji 和中文）
-- 存储引擎：InnoDB
-- =============================================
create database if not exists nuoPao;
use nuoPao;
-- 1. 用户表
create table user
(
    id           bigint                              comment 'id（雪花算法生成）' primary key,
    username     varchar(64)                         null comment '用户昵称',
    userAccount  varchar(64)      not null           comment '账号（登录用）',
    avatarUrl    varchar(1024)                       null comment '用户头像',
    gender       tinyint                             null comment '性别 0-女 1-男 2-未知',
    userPassword varchar(512)      not null           comment '密码（加密存储）',
    phone        varchar(20)                         null comment '电话',
    email        varchar(128)                        null comment '邮箱',
    userStatus   int      default 0                 not null comment '状态 0-正常',
    createTime   datetime default current_timestamp null comment '创建时间',
    updateTime   datetime default current_timestamp null on update current_timestamp comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除 0-未删 1-已删',
    userRole     int      default 0                 not null comment '0-普通用户 1-管理员',
    planetCode   bigint   default 0                 not null comment '星球编号（雪花ID，对外转Base62）',
    unique key uniIndex_userAccount (userAccount),
    unique key uniIndex_planetCode (planetCode)
) comment '用户' engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;


-- 2. 标签表
create table tag
(
    id         bigint                               comment 'id（雪花算法生成）' primary key,
    tagName    varchar(64)      not null            comment '标签名',
    userId     bigint                              null comment '上传标签的用户ID',
    parentId   bigint                              null comment '父标签id（null表示一级标签）',
    createTime datetime default current_timestamp  null comment '创建时间',
    updateTime datetime default current_timestamp  null on update current_timestamp comment '更新时间',
    isDelete   tinyint  default 0                  not null comment '是否删除 0-未删 1-已删',
    key idx_parentId (parentId)
) comment '标签' engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;


-- 3. 用户标签关系表（联合主键，无独立id）
create table user_tag
(
    userId     bigint not null comment '用户id',
    tagId      bigint not null comment '标签id',
    createTime datetime default current_timestamp null comment '创建时间',
    primary key (userId, tagId),
    key idx_tagId (tagId)
) comment '用户标签关系' engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;


-- 4. 队伍表
create table team
(
    id          bigint                               comment 'id（雪花算法生成）' primary key,
    name        varchar(64)      not null            comment '队伍名称',
    description varchar(1024)                       null comment '描述',
    maxNum      int      default 1                  not null comment '最大人数',
    expireTime  datetime                            null comment '过期时间',
    userId      bigint                              null comment '创建人id',
    status      int      default 0                  not null comment '0-公开 1-私有 2-加密',
    password    varchar(64)                         null comment '密码（加密存储，仅status=2时有值）',
    createTime  datetime default current_timestamp  null comment '创建时间',
    updateTime  datetime default current_timestamp  null on update current_timestamp comment '更新时间',
    isDelete    tinyint  default 0                  not null comment '是否删除 0-未删 1-已删'
) comment '队伍' engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;


-- 5. 用户队伍关系表
create table user_team
(
    id         bigint                               comment 'id（雪花算法生成）' primary key,
    userId     bigint                              null comment '用户id',
    teamId     bigint                              null comment '队伍id',
    joinTime   datetime                            null comment '加入时间',
    createTime datetime default current_timestamp  null comment '创建时间',
    updateTime datetime default current_timestamp  null on update current_timestamp comment '更新时间',
    isDelete   tinyint  default 0                  not null comment '是否删除 0-未删 1-已删',
    unique key uniIndex_userId_teamId (userId, teamId),
    key idx_teamId (teamId)
) comment '用户队伍关系' engine = InnoDB default charset = utf8mb4 collate = utf8mb4_unicode_ci;