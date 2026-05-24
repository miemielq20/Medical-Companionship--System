<template>
    <h1 class="title"> 用户登录</h1>
    <van-form @submit="onSubmit">
        <van-cell-group inset>
            <van-field v-model="form.userName" name="用户名" label="用户名" placeholder="用户名"
                :rules="[{ required: true, message: '请填写用户名' }]" />
            <van-field v-model="form.passWord" type="password" name="密码" label="密码" placeholder="密码"
                :rules="[{ required: true, message: '请填写密码' }]" />
        </van-cell-group>
        <div style="margin: 16px;">
            <van-button native-type="submit" round block type="primary">
                提交
            </van-button>
        </div>
    </van-form>
    <div class="register-link">
        没有账号？<router-link to="/register">去注册</router-link>
    </div>
</template>

<script setup lang="ts">
    import router from '@/router';
    import { reactive, getCurrentInstance } from 'vue';
    import {type LoginReponse,type ApiResponse} from '@/types/response';
    const instance = getCurrentInstance()
    const proxy = instance?.proxy as any 

    const form = reactive({
        userName: '',
        passWord: ''
    });
    const onSubmit =  () => {
         proxy.$api.login(form).then((res: ApiResponse<LoginReponse>) => {
          
            console.log(res.data.code);
            if (res.data.code === 10000) {
                localStorage.setItem('h5-token',res.data.data.token);
                localStorage.setItem('h5-userInfo', JSON.stringify(res.data.data.userInfo));
                router.push('/');
            }
        });
    };
</script>

<style scoped lang="less">
    .title {
        text-align: center;
        font-weight: bold;
        padding: 3rem;
    }

    .register-link {
        text-align: center;
        font-size: 0.9rem;
        color: #999;
        margin-top: 1rem;

        a {
            color: #1989fa;
        }
    }
</style>
