/**
 * H5端Axios实例配置
 * - 请求头自动带terminal=h5标识
 * - 请求拦截器：自动附带h-token（白名单除外）
 * - 响应拦截器：code=-2或401时清除登录态并跳转登录页
 */
import axios from 'axios';

// 创建Axios实例
const http = axios.create({
  baseURL: 'http://localhost:8080/',
  timeout: 10000,
  headers: {
    terminal: 'h5'
  }
})

// 请求拦截器：添加h5终端标识和token
http.interceptors.request.use(function (config) {
  const token = localStorage.getItem('h5-token')
  const whiteUrls = ['/login', '/register', '/get/code', '/user/authentication', '/Index/index', '/h5/companion']

  config.headers.terminal = 'h5'
  if (token && !whiteUrls.includes(config.url as string)) {
    config.headers['h-token'] = token
  }

  return config
}, function (error) {
  return Promise.reject(error)
})

// 响应拦截器：处理token过期和未授权
http.interceptors.response.use(function (response) {
  if (response.data.code === -2) {
    localStorage.removeItem('h5-token')
    localStorage.removeItem('h5-userInfo')
    window.location.href = '/login'
  }
  return response
}, function (error) {
  if (error.response?.status === 401) {
    localStorage.removeItem('h5-token')
    localStorage.removeItem('h5-userInfo')
    window.location.href = '/login'
  }
  return Promise.reject(error)
})

export default http
