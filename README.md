# AndroidSmoothMove
# 服务器从MySQL获取经纬度传给Android studio在高德地图上画轨迹——客户端

# 一、高德API官方demo学习
平滑移动demo：https://github.com/amap-demo/android-smooth-move

# 二、在demo基础上添加
## 服务器将从数据库中提取的经纬度传给Android客户端
• 用json传给android
• 使用http方法

## 客户端部分
• avtivity_point.xml文件设有两个按钮
  • GETPOINT为获取服务器传来的json数据并解析成double型，为生成地图做准备
  • TRANS为跳转到绘制轨迹的地图界面按钮
  
• PointActivity与服务器交互，并为跳转到地图界面绘制轨迹做准备

• 官方demo中的activity_main.xml，也即绘制轨迹界面

• 在高德地图上画轨迹MainActivity
  官方demo中经纬度数组double[] coords是已经初始化好的，本案例的是从PointActivity传递过来的
