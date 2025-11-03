# 朋友圈动态功能

## 项目简介

本项目是一个基于Java开发的社交平台朋友圈动态功能模块，实现了用户注册登录、发布动态和查看好友动态等核心功能。


## 功能特性

### 核心功能

1. **用户注册登录**
   - 支持手机号注册用户
   - 支持用户登录并生成访问令牌
   - 密码安全存储和验证

2. **动态发布**
   - 支持发布动态
   - 文字内容限制在500字符以内
   - 最多支持9张图片
   - 支持地理位置信息
   - 媒体资源通过对象存储处理

3. **朋友圈动态查看**
   - 支持获取朋友圈动态
   - 显示用户及其好友的最新动态
   - 按发布时间倒序排列，保持最新动态在上
   - 默认每页10条记录

4. **好友管理**
   - 支持添加好友
   - 支持删除好友
   - 支持查看好友列表
   - 防止重复添加和自添加

5. **用户个人信息管理**
   - 支持查看个人信息（昵称、头像、手机号等）
   - 支持更新昵称和头像
   - 支持更换用户头像
   - 完善的参数验证和错误处理

6. **安全控制**
   - 用户需要登录获取token后才能访问特定接口
   - 仅允许指定请求的API访问
   - 其他未授权访问均返回"非法访问！"


## 后续计划

本项目作为社交平台的模块，未来将逐步拓展以下功能：
- 动态点赞和评论
- ~~用户个人信息管理~~（✅已完成）
- 好友申请和审核机制
- 更丰富的内容类型支持
- 更多功能将后续添加...... 


## 技术栈

- Java 8+
- Spring Boot
- MyBatis
- MySQL
- Redis
- Maven

## 数据库设计

### 用户表 (user)
- id: 用户ID
- phone: 手机号
- nickname: 昵称
- avatar: 头像URL
- password: 密码
- create_time: 创建时间
- update_time: 更新时间

### 好友关系表 (friend)
- id: 主键ID
- user_id: 用户ID
- friend_id: 好友ID
- create_time: 创建时间

### 动态表 (moment)
- id: 动态ID
- user_id: 发布者ID
- content: 文字内容
- images: 图片URL列表(JSON格式)
- location: 定位信息
- create_time: 创建时间
- update_time: 更新时间



## 核心接口

### 用户注册接口
见apiDoc.md


### 用户登录接口
见apiDoc.md


### 动态发布接口
见apiDoc.md


### 朋友圈动态查询接口
见apiDoc.md


### 添加好友接口
见apiDoc.md


### 删除好友接口
见apiDoc.md


### 获取好友列表接口
见apiDoc.md


### 获取用户个人信息接口
见apiDoc.md


### 更新用户个人信息接口
见apiDoc.md


### 更新用户头像接口
见apiDoc.md


## 部署说明

1. 确保安装并运行MySQL和Redis服务
2. 根据ddl.sql文件创建数据库表结构
3. 修改`demo/src/main/resources/dev/application.yaml`中的数据库和Redis连接配置
4. 编译项目: `mvn clean package`
5. 运行项目: `java -jar demo/target/demo.jar --spring.profiles.active=dev`


## 注意事项

1. 所有未明确允许的请求都将返回"非法访问！"
2. 用户需要先注册并登录获取token，才能访问动态相关接口
3. 更多功能将后续添加......
