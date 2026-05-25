<template>
    <div class="order-list">
        <van-sticky :offset-top="0" z-index="999">
            <div class="header">
                <van-icon name="arrow-left" size="1.5rem" class="back-btn" @click="back" />
                <p class="title">我的订单</p>
            </div>
        </van-sticky>

        <van-tabs v-model:active="active" @click-tab="onClickTab" sticky>
            <van-tab title="全部" name="" />
            <van-tab title="待支付" name="1" />
            <van-tab title="待服务" name="2" />
            <van-tab title="已完成" name="3" />
            <van-tab title="已取消" name="4" />
        </van-tabs>

        <div class="order-content">
            <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
                <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="onLoad">
                    <div v-for="(item, index) in data" :key="index" class="order-card" @click="goOrderDetail(item)">
                        <div class="card-header">
                            <van-image :src="item.serviceImg || item.companion?.avatar" width="5rem" height="5rem" fit="cover" />
                            <div class="card-info">
                                <div class="service-name">{{ item.service_name }}</div>
                                <div class="hospital-name">{{ item.hospital_name }}</div>
                                <div class="appointment-time">预约时间: {{ formatTime(item.starttime) }}</div>
                            </div>
                            <div class="status-section">
                                <div :class="getStatusClass(item.trade_state)">
                                    {{ item.trade_state }}
                                </div>
                                <Countdown
                                    v-if="item.trade_state === '待支付' && item.time_end"
                                    :time-end="item.time_end"
                                    @expired="handleOrderExpired(item)"
                                />
                            </div>
                        </div>
                    </div>
                </van-list>
            </van-pull-refresh>
        </div>
    </div>
</template>

<script lang="ts" setup>
    /**
 * H5端订单列表页
 * 支持按状态Tab切换、下拉刷新、倒计时显示、超时自动取消
 */
import { getCurrentInstance, onMounted, ref } from 'vue';
    import { useRoute, useRouter } from 'vue-router';
    import { type order } from '@/types/order';
    import { type ApiResponse, type orderListResponse } from '@/types/response';
    import { showNotify } from 'vant';
    import Countdown from '@/components/Countdown.vue';

    const instance = getCurrentInstance();
    const proxy = instance?.proxy as any;
    const router = useRouter();
    const route = useRoute();

    const active = ref('');
    const data = ref<order[]>([]);
    const loading = ref(false);
    const finished = ref(false);
    const refreshing = ref(false);

    /** 根据订单状态返回对应的CSS类名 */
const getStatusClass = (state: string) => {
        const classMap: Record<string, string> = {
            '待支付': 'status-wait-pay',
            '待服务': 'status-wait-service',
            '已完成': 'status-completed',
            '已取消': 'status-cancelled'
        };
        return classMap[state] || '';
    };

    /** 格式化时间戳为日期字符串 */
const formatTime = (value: number | string) => {
        const timestamp = Number(value);
        if (!Number.isFinite(timestamp)) {
            return String(value || '');
        }
        return new Date(timestamp).toLocaleDateString();
    };

    /** 获取当前选中的Tab状态值 */
const currentState = () => active.value === '' ? undefined : String(active.value);

    /** 请求订单列表 */
const getOrderList = (state?: string) => {
        loading.value = true;
        proxy.$api.orderList({ state }).then((res: ApiResponse<orderListResponse>) => {
            if (res.data.code === 10000) {
                data.value = (res.data.data || []).map((item: order) => ({
                    ...item,
                    time_end: item.time_end || item.order_start_time + 7200000
                }));
            } else {
                showNotify({ message: res.data.msg || '获取订单失败', type: 'danger' });
            }
        }).catch(() => {
            showNotify({ message: '请求失败', type: 'danger' });
        }).finally(() => {
            loading.value = false;
            refreshing.value = false;
            finished.value = true;
        });
    };

    /** 下拉刷新 */
const onRefresh = () => {
        finished.value = false;
        getOrderList(currentState());
    };

    const onLoad = () => {
        finished.value = true;
    };

    /** Tab切换：重置数据并重新加载 */
const onClickTab = (item: any) => {
        active.value = item.name;
        data.value = [];
        finished.value = false;
        getOrderList(currentState());
    };

    /** 订单超时处理：本地标记为已取消并刷新列表 */
const handleOrderExpired = (item: order) => {
        item.trade_state = '已取消';
        item.service_state = '已取消';
        getOrderList(currentState());
    };

    /** 跳转到订单详情页 */
const goOrderDetail = (item: order) => {
        router.push(`/detail/?oid=${item.out_trade_no}`);
    };

    const back = () => {
        router.go(-1);
    };

    onMounted(() => {
        const stateQuery = (route.query.state as string) || '';
        active.value = stateQuery;
        getOrderList(currentState());
    });
</script>

<style lang="less" scoped>
    .order-list {
        min-height: 100vh;
        background-color: #f5f5f5;

        .header {
            display: flex;
            align-items: center;
            padding: 0.8rem;
            background-color: #fff;
            position: relative;

            .back-btn {
                position: absolute;
                left: 0.8rem;
                top: 50%;
                transform: translateY(-50%);
            }

            .title {
                width: 100%;
                text-align: center;
                font-size: 1.2rem;
                font-weight: bold;
                color: #333;
                margin: 0;
            }
        }

        :deep(.van-tabs) {
            background-color: #fff;
        }

        .order-content {
            padding: 0.8rem;

            .order-card {
                margin-bottom: 0.8rem;
                background-color: #fff;
                border-radius: 0.8rem;
                padding: 1rem;

                .card-header {
                    display: flex;
                    align-items: flex-start;

                    .card-info {
                        flex: 1;
                        margin-left: 0.8rem;

                        .service-name {
                            font-size: 1.1rem;
                            font-weight: bold;
                            color: #333;
                            margin-bottom: 0.4rem;
                        }

                        .hospital-name {
                            font-size: 0.9rem;
                            color: #666;
                            margin-bottom: 0.3rem;
                        }

                        .appointment-time {
                            font-size: 0.85rem;
                            color: #999;
                        }
                    }

                    .status-section {
                        text-align: right;
                        min-width: 5rem;

                        .status-wait-pay {
                            color: #ff6b35;
                            font-size: 0.95rem;
                            font-weight: bold;
                        }

                        .status-wait-service {
                            color: #1989fa;
                            font-size: 0.95rem;
                            font-weight: bold;
                        }

                        .status-completed {
                            color: #07c160;
                            font-size: 0.95rem;
                            font-weight: bold;
                        }

                        .status-cancelled {
                            color: #999;
                            font-size: 0.95rem;
                        }
                    }
                }
            }
        }
    }
</style>
