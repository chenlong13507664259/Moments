### 接口文档

### (1)用户注册接口
请求地址: http://localhost:8080

请求路径：/user/register

请求方式：POST

请求参数：
- phone: 手机号
- nickname: 昵称
- password: 密码


响应示例：（注：密码不能暴露，已做安全处理，显示为null）
{
"redirectPageType": 0,
"result": 1,
"message": "注册成功",
"mdata": {
"user": {
"id": 6,
"phone": "13800138006",
"nickname": "小华",
"avatar": "",
"password": null,
"createTime": "2025-10-31 21:57:44",
"updateTime": "2025-10-31 21:57:44"
}
}
}




### (2)用户登录接口
请求地址: http://localhost:8080

请求路径：/user/login

请求方式：POST

请求参数：
- phone: 手机号
- password: 密码

响应示例：（注：密码不能暴露，已做安全处理，显示为null）
{
"redirectPageType": 0,
"result": 1,
"message": "登录成功",
"mdata": {
"user": {
"id": 1,
"phone": "13800138001",
"nickname": "小明",
"avatar": "https://example.com/avatar1.jpg",
"password": null,
"createTime": "2025-10-29 18:30:00",
"updateTime": "2025-10-29 18:30:00"
},
"token": "1_466002d7-9cd0-44ec-b4ff-342319b8400b"
}
}



### (3)动态发布接口
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


### (4)朋友圈动态查询接口
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


