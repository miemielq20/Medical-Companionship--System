<template>
    <span class="countdown" :class="{ expired: isExpired }">
        {{ countdownText }}
    </span>
</template>

<script lang="ts" setup>
    import { computed, onUnmounted, ref, watch } from 'vue';

    interface Props {
        timeEnd?: number;
    }

    const props = withDefaults(defineProps<Props>(), {
        timeEnd: 0
    });

    const emit = defineEmits<{
        expired: [];
    }>();

    const remainingSeconds = ref(0);
    const expiredEmitted = ref(false);
    let timer: number | null = null;

    const isExpired = computed(() => remainingSeconds.value <= 0);

    const countdownText = computed(() => {
        if (isExpired.value) {
            return '00:00:00';
        }

        const hours = Math.floor(remainingSeconds.value / 3600);
        const minutes = Math.floor((remainingSeconds.value % 3600) / 60);
        const seconds = remainingSeconds.value % 60;

        return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;
    });

    const stopTimer = () => {
        if (timer !== null) {
            clearInterval(timer);
            timer = null;
        }
    };

    const emitExpired = () => {
        if (!expiredEmitted.value) {
            expiredEmitted.value = true;
            emit('expired');
        }
    };

    const calculateRemainingTime = () => {
        if (!props.timeEnd) {
            remainingSeconds.value = 0;
            stopTimer();
            return;
        }

        const diff = props.timeEnd - Date.now();
        if (diff <= 0) {
            remainingSeconds.value = 0;
            stopTimer();
            emitExpired();
            return;
        }

        remainingSeconds.value = Math.floor(diff / 1000);
    };

    const startTimer = () => {
        stopTimer();
        calculateRemainingTime();

        if (!isExpired.value) {
            timer = window.setInterval(calculateRemainingTime, 1000);
        }
    };

    watch(() => props.timeEnd, () => {
        expiredEmitted.value = false;
        startTimer();
    }, { immediate: true });

    onUnmounted(stopTimer);
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
