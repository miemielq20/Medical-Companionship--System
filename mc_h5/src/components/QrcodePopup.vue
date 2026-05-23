<template>
    <van-popup v-model:show="show" round position="center">
        <div class="qrcode-container">
            <div class="qrcode-header">
                <van-icon name="cross" class="close-btn" @click="handleClose" />
                <h3 class="title">{{ title }}</h3>
            </div>
            <img :src="qrcodeUrl" alt="支付二维码" class="qrcode-img">
            <p class="tip-text">{{ tipText }}</p>
        </div>
    </van-popup>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';
import Qrcode from 'qrcode';

interface Props {
    modelValue: boolean;
    codeUrl: string;
    title?: string;
    tipText?: string;
    width?: number;
    margin?: number;
}

const props = withDefaults(defineProps<Props>(), {
    title: '微信支付',
    tipText: '请使用本人微信扫描二维码',
    width: 300,
    margin: 2
});

const emit = defineEmits<{
    (e: 'update:modelValue', value: boolean): void;
    (e: 'close'): void;
}>();

const show = ref(props.modelValue);
const qrcodeUrl = ref('');

// 监听modelValue变化
watch(() => props.modelValue, (newVal) => {
    show.value = newVal;
    if (newVal && props.codeUrl) {
        generateQrcode();
    }
});

// 监听show变化同步给父组件
watch(show, (newVal) => {
    emit('update:modelValue', newVal);
});

// 监听codeUrl变化重新生成二维码
watch(() => props.codeUrl, (newUrl) => {
    if (newUrl && show.value) {
        generateQrcode();
    }
});

// 生成二维码
const generateQrcode = async () => {
    if (!props.codeUrl) return;
    
    try {
        const url = await Qrcode.toDataURL(props.codeUrl, {
            width: props.width,
            margin: props.margin,
            color: {
                dark: '#000000',
                light: '#ffffff'
            }
        });
        qrcodeUrl.value = url;
    } catch (error) {
        console.error('生成二维码失败:', error);
    }
};

// 关闭弹窗
const handleClose = () => {
    show.value = false;
    emit('close');
};

// 初始化时如果有codeUrl则生成二维码
if (props.codeUrl && props.modelValue) {
    generateQrcode();
}
</script>

<style scoped lang="less">
.qrcode-container {
    padding: 1.25rem;
    width: 20rem;
    background-color: #fff;
    border-radius: 0.75rem;

    .qrcode-header {
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 1rem;
        position: relative;

        .close-btn {
            position: absolute;
            left: 0;
            top: 50%;
            transform: translateY(-50%);
            font-size: 1rem;
            color: #999;
            cursor: pointer;
            line-height: 1;

            &:hover {
                color: #333;
            }
        }

        .title {
            font-size: 1rem;
            font-weight: bold;
            color: #333;
            margin: 0;
        }
    }

    .qrcode-img {
        display: block;
        width: 16rem;
        height: 16rem;
        margin: 0 auto;
    }

    .tip-text {
        text-align: center;
        font-size: 1rem;
        color: #666;
        margin-top: 1rem;
        margin-bottom: 0;
    }
}
</style>
