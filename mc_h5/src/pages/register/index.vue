<template>
    <div class="register-page">
        <h1 class="title">用户注册</h1>
        <van-form @submit="onSubmit">
            <van-cell-group inset>
                <van-field
                    v-model="form.phone"
                    name="手机号"
                    label="手机号"
                    placeholder="请输入手机号"
                    type="tel"
                    maxlength="11"
                    :rules="[{ required: true, message: '请填写手机号' }]"
                />
                <van-field
                    v-model="form.code"
                    center
                    clearable
                    label="验证码"
                    placeholder="请输入验证码"
                    :rules="[{ required: true, message: '请填写验证码' }]"
                >
                    <template #button>
                        <van-button
                            size="small"
                            type="primary"
                            native-type="button"
                            :disabled="codeDisabled"
                            @click="sendCode"
                        >
                            {{ codeText }}
                        </van-button>
                    </template>
                </van-field>
                <van-field
                    v-model="form.password"
                    type="password"
                    name="密码"
                    label="密码"
                    placeholder="请输入密码"
                    :rules="[{ required: true, message: '请填写密码' }, { validator: checkPwdLen, message: '密码至少6位' }]"
                />
                <van-field
                    v-model="form.confirmPassword"
                    type="password"
                    name="确认密码"
                    label="确认密码"
                    placeholder="请再次输入密码"
                    :rules="[{ required: true, message: '请确认密码' }]"
                />
            </van-cell-group>
            <div style="margin: 16px;">
                <van-button native-type="submit" round block type="primary">
                    注册
                </van-button>
            </div>
        </van-form>
        <div class="login-link">
            已有账号？<router-link to="/login">去登录</router-link>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, getCurrentInstance } from 'vue';
import { showToast } from 'vant';
import { useRouter } from 'vue-router';
import type { ApiResponse } from '@/types/response';

const router = useRouter();
const instance = getCurrentInstance();
const proxy = instance?.proxy as any;

const form = reactive({
    phone: '',
    code: '',
    password: '',
    confirmPassword: '',
});

const codeText = ref('获取验证码');
const codeDisabled = ref(false);
let countdown = 0;

const checkPwdLen = (val: string) => val.length >= 6;

const sendCode = async () => {
    const phone = form.phone;
    if (phone.length !== 11) {
        showToast('请输入正确的手机号');
        return;
    }
    try {
        const res = await proxy.$api.getCode({ phone });
        if (res.data.code === 10000) {
            showToast('验证码已发送');
            codeDisabled.value = true;
            countdown = 60;
            codeText.value = `${countdown}s`;
            const timer = setInterval(() => {
                countdown--;
                if (countdown <= 0) {
                    clearInterval(timer);
                    codeDisabled.value = false;
                    codeText.value = '获取验证码';
                } else {
                    codeText.value = `${countdown}s`;
                }
            }, 1000);
        } else {
            showToast(res.data.msg || '发送失败');
        }
    } catch {
        showToast('发送验证码失败，请稍后重试');
    }
};

const onSubmit = async () => {
    if (form.password !== form.confirmPassword) {
        showToast('两次密码不一致');
        return;
    }
    if (form.password.length < 6) {
        showToast('密码至少6位');
        return;
    }
    try {
        const res = await proxy.$api.userAuthentication({
            userName: form.phone,
            passWord: form.password,
            validCode: form.code,
        });
        if (res.data.code === 10000) {
            showToast('注册成功');
            router.push('/login');
        } else {
            showToast(res.data.msg || '注册失败');
        }
    } catch {
        showToast('注册失败，请稍后重试');
    }
};
</script>

<style scoped lang="less">
.register-page {
    min-height: 100dvh;
    background-color: #f7f8fa;

    .title {
        text-align: center;
        font-weight: bold;
        padding: 3rem;
    }

    .login-link {
        text-align: center;
        font-size: 0.9rem;
        color: #999;
        margin-top: 1rem;

        a {
            color: #1989fa;
        }
    }
}
</style>
