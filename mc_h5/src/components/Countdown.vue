<template>
    <span class="countdown" :class="{ 'expired': isExpired }">
        {{ countdownText }}
    </span>
</template>

<script lang="ts" setup>
    import { ref, computed, onMounted, onUnmounted, watch } from 'vue';

    // 定义props
    interface Props {
        timeEnd?: number;  // 结束时间戳（毫秒），可选
    }

    const props = withDefaults(defineProps<Props>(), {
        timeEnd: 0
    });

    // 剩余秒数
    const remainingSeconds = ref(0);
    
    // 定时器
    let timer: number | null = null;

    // 是否已过期
    const isExpired = computed(() => remainingSeconds.value <= 0);

    // 倒计时文本
    const countdownText = computed(() => {
        if (isExpired.value) {
            return ;
        }

        const hours = Math.floor(remainingSeconds.value / 3600);
        const minutes = Math.floor((remainingSeconds.value % 3600) / 60);
        const seconds = remainingSeconds.value % 60;

        return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    });

    // 计算剩余秒数
    const calculateRemainingTime = () => {
        if (!props.timeEnd || props.timeEnd === 0) {
            remainingSeconds.value = 0;
            return;
        }

        // timeEnd已经是时间戳（毫秒），直接使用
        const endTime = props.timeEnd;
        const now = Date.now();
        const diff = endTime - now;

        if (diff <= 0) {
            remainingSeconds.value = 0;
            stopTimer();
        } else {
            remainingSeconds.value = Math.floor(diff / 1000);
        }
    };

    // 启动定时器
    const startTimer = () => {
        stopTimer(); // 先清除已有的定时器
        calculateRemainingTime();
        
        timer = window.setInterval(() => {
            calculateRemainingTime();
            
            // 如果已过期，停止定时器
            if (remainingSeconds.value <= 0) {
                stopTimer();
            }
        }, 1000);
    };

    // 停止定时器
    const stopTimer = () => {
        if (timer !== null) {
            clearInterval(timer);
            timer = null;
        }
    };

    // 监听timeEnd变化
    watch(() => props.timeEnd, (newVal) => {
        if (newVal) {
            startTimer();
        } else {
            stopTimer();
            remainingSeconds.value = 0;
        }
    }, { immediate: true });

    // 组件挂载时启动定时器
    onMounted(() => {
        if (props.timeEnd) {
            startTimer();
        }
    });

    // 组件卸载时清除定时器
    onUnmounted(() => {
        stopTimer();
    });
</script>

<style lang="less" scoped>
    .countdown {
        margin-top: 0.3rem;
        font-size: 0.85rem;
        color: #ff6b35;
        font-weight: bold;

        &.expired {
            color: #999;
        }
    }
</style>
