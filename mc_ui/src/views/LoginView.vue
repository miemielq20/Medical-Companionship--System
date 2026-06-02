<template>
  <el-row justify="center" :align="'middle'" class="container">
    <el-card class="login-card" shadow="hover">
      <template #header>
        <div class="login-title">
          <el-image :src="imgUrl" class="login-image" fit="cover" />
        </div>
      </template>
      <div class="jump-link">
        <el-link type="primary" @click="handChange()">{{ formType ? '返回登录' : '注册账号' }}</el-link>
      </div>
      <el-form :model="loginForm" class="userForm" :rules="rules" ref="ruleFormRef">
        <el-form-item prop="userName">
          <el-input v-model="loginForm.userName" placeholder="手机号">
            <template #prefix>
              <el-icon class="el-input__icon">
                <UserFilled />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="passWord">
          <el-input v-model="loginForm.passWord" type="password" show-password placeholder="密码">
            <template #prefix>
              <el-icon>
                <Lock />
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="validCode" v-if="formType">
          <el-input v-model="loginForm.validCode" placeholder="验证码">
            <template #prefix>
              <el-icon>
                <Lock />
              </el-icon>
            </template>
            <template #append><span @click="countdownChange">
                {{ countdown.text }}
              </span></template>
          </el-input>
        </el-form-item>
        <el-form-item >
          <el-button type="primary" class="login-btn" @click="submitForm(ruleFormRef)" >
            {{formType?"注册账号":"登录"  }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </el-row>

</template>


<style lang="less" scoped>
  .container {
    height: 100vh;
    width: 100%;
  }

  .login-card {
    max-width: 480px;
    width: 100%;

    // 移除header的padding和边框
    :deep(.el-card__header) {
      padding: 0 !important;
      margin: 0 !important;
    }

    // 移除body的padding
    :deep(.el-card__body) {
      padding: 0 !important;
      margin: 0 !important;
    }

    .login-image {
      width: 100% !important;
      height: 180px !important;
      display: block;
      margin: 0;
      padding: 0;
    }

    // 为跳转链接添加间距
    .jump-link {
      padding: 8px;
      text-align: right;
    }

    .userForm {
      padding: 10px;

      .login-btn {
        width: 100%;
      }

      .code-btn {
        width: 30%;
      }
    }

  }


</style>

<script setup lang="ts">

  import {reactive, ref ,computed} from 'vue'
  import type { FormRules,FormInstance } from 'element-plus'
  import {getCode,userAuthentication,login} from '@/api/index'
  import {useRouter} from 'vue-router'
  import getDynamicRouter from '@/utils/getList'
  import {useRouterStore} from '@/stores/router'

  const RouterStore = useRouterStore()
  const imgUrl = '/image/login-bg.jpeg'
  const formType = ref(0)
  const ruleFormRef = ref<FormInstance>()
  const router =useRouter()


  const  routerList = computed(
    () => {
      return RouterStore.routerList
    }
  )
  const handChange = () => {
    formType.value = formType.value ? 0 : 1
  }

  const loginForm = reactive({
    userName: '',
    passWord: '',
    validCode: ''
  })

  // 校验正则
  const phoneReg = /^1[3-9]\d{9}$/;
  const passwordReg = /^.{6,20}$/;
  const validateUsername = (rule: any, value: string, callback: any) => {
    if (!phoneReg.test(value)) {
      callback(new Error('请输入正确的手机号码'))
    } else {
      callback()
    }
  }

  const validatePassword = (rule: any, value: string, callback: any) => {
    if (!passwordReg.test(value)) {
      callback(new Error('密码不能少于6位'))
    } else {
      callback()
    }
  }

  //校验
  const rules = reactive<FormRules<typeof loginForm>>({
     userName: [{ validator: validateUsername, trigger: 'blur' }],
     passWord: [{ validator: validatePassword, trigger: 'blur' }],
     validCode: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})


  const countdown = reactive({
    text: '获取验证码',
    time: 60
  })

  const flag = ref(true)
  //获取验证码
  const countdownChange = () => {
    if (!flag.value) return

    if (!loginForm.userName || !phoneReg.test(loginForm.userName)) {
      return ElMessage({
        message: '请输入正确的手机号格式',
        type: 'warning',
      })
    }

    // 立即禁用并开始倒计时
    flag.value = false
    const time = setInterval(() => {
      if (countdown.time <= 0) {
        countdown.text = '获取验证码'
        countdown.time = 60
        flag.value = true
        clearInterval(time)
      } else {
        countdown.time--
        countdown.text = countdown.time + 's'
      }
    }, 1000)

    // 异步发送验证码
    getCode({
      phone: loginForm.userName
    }).then(res => {
      if (res.data.code === 10000) {
        ElMessage.success('验证码发送成功')
      } else {
        ElMessage.error(res.data.msg || '验证码发送失败')
        // 失败时重置
        clearInterval(time)
        countdown.text = '获取验证码'
        countdown.time = 60
        flag.value = true
      }
    }).catch(() => {
      clearInterval(time)
      countdown.text = '获取验证码'
      countdown.time = 60
      flag.value = true
    })
  }
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
        if(formType.value) {
          //注册
          userAuthentication(loginForm).then(res => {
            if(res.data.code === 10000) {
              ElMessage.success('注册成功')
              formType.value=0
            } else {
              ElMessage.error(res.data.msg )
            }
          })
        } else {
          //登录
         login(loginForm).then(async res => {
              if(res.data.code === 10000) {
                ElMessage.success('登录成功')
                window.localStorage.setItem('token',res.data.data.token)
                window.localStorage.setItem('userInfo',JSON.stringify(res.data.data.userInfo))
                try {
                  await getDynamicRouter()
                  router.replace('/')
                } catch (error) {
                  ElMessage.error('菜单加载失败，请重试')
                }
              } else {
                ElMessage.error(res.data.msg )
              }
            }).catch(() => {
              ElMessage.error('登录失败，请检查网络连接')
            })
        }
    } else {
        ElMessage.error('请输入正确的格式' )
    }
  })
}
</script>
