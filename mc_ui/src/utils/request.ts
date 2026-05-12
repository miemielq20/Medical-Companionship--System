import axios from 'axios';
import { ElMessage } from 'element-plus';


const http=axios.create({
  baseURL: 'https://v3pz.itndedu.com/v3pz',
  timeout: 10000,
});


// 添加请求拦截器
http.interceptors.request.use(function (config) {
    const token = localStorage.getItem('token');
    const whiteUrls=['/get/code','/user/authentication','/login']
    if(token&&!whiteUrls.includes(config.url as string)){
        config.headers['x-token']=token
    }
    return config;

  }, function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  });

// 添加响应拦截器
http.interceptors.response.use(function (response) {
    if(response.data.code==-1){
        ElMessage.error(response.data.msg)
    }
    //token过期
    else if(response.data.code==-2){
        ElMessage.error(response.data.msg)
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('RouterList')
        window.location.href='/'
    }
    return response;
  }, function (error) {
    // 超出 2xx 范围的状态码都会触发该函数。
    // 对响应错误做点什么
    return Promise.reject(error);
  });

  export default http;