/**
 * Axios实例配置
 * - baseURL指向后端8080端口
 * - 请求拦截器：自动附带x-token（白名单除外）
 * - 响应拦截器：token过期(-2)自动跳转登录页
 */
import axios from 'axios';
import { ElMessage } from 'element-plus';


// 创建Axios实例
const http = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
})


// 添加请求拦截器
// 请求拦截器：自动附带token
http.interceptors.request.use(function (config) {
  const token = localStorage.getItem('token');
  const whiteUrls = ['/get/code', '/user/authentication', '/login']
  if (token && !whiteUrls.includes(config.url as string)) {
    config.headers['x-token'] = token
  }
  return config;

}, function (error) {
  // 对请求错误做些什么
  if (error.response?.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('userInfo'); localStorage.removeItem('RouterList'); ElMessage.error('登录已过期，请重新登录'); window.location.href = '/'; } else if (error.response) { ElMessage.error(error.response.data?.message || '请求失败'); } else { ElMessage.error('网络连接失败，请检查网络'); } return Promise.reject(error);
});

// 添加响应拦截器
// 响应拦截器：处理token过期等异常
http.interceptors.response.use(function (response) {
  if (response.data.code == -1) {
    ElMessage.error(response.data.msg)
  }
  //token过期
  else if (response.data.code == -2) {
    ElMessage.error(response.data.msg)
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('RouterList')
    window.location.href = '/'
  }
  return response;
}, function (error) {
  // 超出 2xx 范围的状态码都会触发该函数。
  // 对响应错误做点什么
  if (error.response?.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('userInfo'); localStorage.removeItem('RouterList'); ElMessage.error('登录已过期，请重新登录'); window.location.href = '/'; } else if (error.response) { ElMessage.error(error.response.data?.message || '请求失败'); } else { ElMessage.error('网络连接失败，请检查网络'); } return Promise.reject(error);
});

export default http;