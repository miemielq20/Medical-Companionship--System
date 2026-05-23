import axios from 'axios';

const http = axios.create({
  baseURL: 'http://localhost:8080/',
  timeout: 10000,
  headers:{
    "terminal": "h5"
  }
})


// 添加请求拦截器
http.interceptors.request.use(function (config) {
    const token = localStorage.getItem('h5-token');
    const whiteUrls=['/login']
    if(token && !whiteUrls.includes(config.url as string)){
        config.headers [`terminal`]=`h5`
        config.headers ['h-token']=token
    }
    return config;

  }, function (error) {
    return Promise.reject(error);
  });

// 添加响应拦截器
http.interceptors.response.use(function (response) {
    if(response.data.code==-1){
        // ElMessage.error(response.data.msg)
    }
    //token过期
    else if(response.data.code==-2){
      localStorage.removeItem('h5_token')
      localStorage.removeItem('h5_userInfo')
      // 把过期跳转到登录页，避免跳转到根路径再被重定向回 /home
      window.location.href='/login'
    }
    return response;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });

  export default http;
