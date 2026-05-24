import axios from 'axios';

const http = axios.create({
  baseURL: 'http://localhost:8080/',
  timeout: 10000,
  headers: {
    terminal: 'h5'
  }
})

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
