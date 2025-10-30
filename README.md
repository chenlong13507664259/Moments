# 朋友圈动态功能

## 项目简介

本项目是一个基于Java开发的社交平台朋友圈动态功能模块，实现了用户发布动态和查看好友动态的核心功能。


## 功能特性

### 核心功能

1. **动态发布**
   - 支持发布动态
   - 文字内容限制在500字符以内
   - 最多支持9张图片
   - 支持地理位置信息
   - 媒体资源已通过对象存储处理完成

2. **朋友圈动态查看**
   - 支持获取朋友圈动态
   - 显示用户及其好友的最新动态
   - 按发布时间倒序排列，保持最新动态在上
   - 默认每页10条记录

3. **安全控制**
   - 仅允许指定的API访问
   - 其他未授权访问均返回"非法访问！"

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

### 接口文档

### （1）动态发布接口
请求地址: http://localhost:8080

请求路径：/content/publish

请求方式：POST
Content-Type: application/json

请求参数：如
{
"userId": 1,
"content": "这是一条测试动态",
"images": "[\"http://example.com/test1.jpg\", \"http://example.com/test2.jpg\"]",
"location": "测试位置"
}

响应参数：如
{
"redirectPageType": 0,
"result": 1,
"message": "动态发布成功",
"mdata": {
"details": "您的动态已成功发布至朋友圈"
}


### （2）朋友圈动态查询接口
请求地址：http://localhost:8080

请求路径: /content/list

请求方式：GET

请求参数：如
http://localhost:8080/content/list?userId=1&pageNo=1&pageSize=10
或
http://localhost:8080/content/list?userId=1 (默认参数pageNo=1,pageSize=10)

响应参数：如
{
"redirectPageType": 0,
"result": 1,
"message": "查询朋友圈动态成功",
"mdata": {
"moments": [
{
"id": 11,
"userId": 2,
"content": "这是一条测试动态4",
"images": "[\"http://example.com/test1.jpg\", \"http://example.com/test2.jpg\"]",
"location": "测试位置4",
"createTime": "2025-10-29 20:47:31",
"updateTime": "2025-10-29 20:47:31"
},
{
"id": 10,
"userId": 2,
"content": "这是一条测试动态3",
"images": "[\"http://example.com/test1.jpg\", \"http://example.com/test2.jpg\"]",
"location": "测试位置3",
"createTime": "2025-10-29 20:40:26",
"updateTime": "2025-10-29 20:40:26"
},
{
"id": 9,
"userId": 2,
"content": "这是一条测试动态2",
"images": "[\"http://example.com/test1.jpg\", \"http://example.com/test2.jpg\"]",
"location": "测试位置2",
"createTime": "2025-10-29 20:36:01",
"updateTime": "2025-10-29 20:36:01"
},
{
"id": 8,
"userId": 1,
"content": "这是一条测试动态",
"images": "[\"http://example.com/test1.jpg\", \"http://example.com/test2.jpg\"]",
"location": "测试位置",
"createTime": "2025-10-29 19:48:42",
"updateTime": "2025-10-29 19:48:42"
},
{
"id": 4,
"userId": 3,
"content": "努力学习，天天向上！",
"images": null,
"location": "图书馆",
"createTime": "2025-10-29 18:35:00",
"updateTime": "2025-10-29 18:30:00"
},
{
"id": 1,
"userId": 1,
"content": "今天天气真好！",
"images": "[\"https://example.com/image1.jpg\", \"https://example.com/image2.jpg\"]",
"location": "北京市朝阳区",
"createTime": "2025-10-29 18:30:00",
"updateTime": "2025-10-29 18:35:00"
},
{
"id": 2,
"userId": 2,
"content": "周末去爬山了，风景美不胜收",
"images": "[\"https://example.com/image3.jpg\"]",
"location": "香山公园",
"createTime": "2025-10-29 18:30:00",
"updateTime": "2025-10-29 18:30:00"
},
{
"id": 3,
"userId": 1,
"content": "分享一首好听的歌",
"images": null,
"location": null,
"createTime": "2025-10-29 18:30:00",
"updateTime": "2025-10-29 18:30:00"
},
{
"id": 7,
"userId": 2,
"content": "新买的书到了，开心！",
"images": null,
"location": null,
"createTime": "2025-10-29 18:30:00",
"updateTime": "2025-10-29 18:30:00"
}
],
"pageNo": 1,
"pageSize": 10,
"details": "已获取最新的朋友圈动态"
}


## 部署说明

1. 确保安装并运行MySQL和Redis服务
2. 根据ddl.sql文件创建数据库表结构
3. 修改`demo/src/main/resources/dev/application.yaml`中的数据库和Redis连接配置
4. 编译项目: `mvn clean package`
5. 运行项目: `java -jar demo/target/demo.jar --spring.profiles.active=dev`



## 后续计划

本项目作为社交平台的模块，未来将逐步拓展以下功能：
- 用户登录认证
- 好友管理（添加/删除好友）
- 动态点赞和评论
- 用户个人信息管理
- 更丰富的内容类型支持


## 注意事项

1. 所有未明确允许的请求都将返回"非法访问！"
2. 更多功能将后续添加......






