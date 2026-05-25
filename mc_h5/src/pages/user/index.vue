<template>
    <div class="user-center">
        <div class="user-info" >
            <van-image
                round
                :src="userInfo.avatar || 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg'"
                width="80"
                height="80"
            />
            <div class="user-name">
                <div>{{ userInfo.name || 'admin' }}</div>
    
            </div>
        
        </div>

       
        <van-cell-group inset class="order-section">
            <div class="section-header">
                <span class="title">我的订单</span>
                <span class="more" @click="goToAllOrders">全部 ></span>
            </div>
            
            <div class="order-types">
                <div class="order-type-item" @click="goToOrderList('待支付')">
                    <van-image src="/images/od_10.png" width="32" height="32" />
                    <span>待支付</span>
                </div>
                <div class="order-type-item" @click="goToOrderList('待服务')">
                    <van-image src="/images/od_20.png" width="32" height="32" />
                    <span>待服务</span>
                </div>
                <div class="order-type-item" @click="goToOrderList('已完成')">
                    <van-image src="/images/od_30.png" width="32" height="32" />
                    <span>已完成</span>
                </div>
                <div class="order-type-item" @click="goToOrderList('已取消')">
                    <van-image src="/images/od_40.png" width="32" height="32" />
                    <span>已取消</span>
                </div>
            </div>
        </van-cell-group>

      
        <van-cell-group inset class="function-list">
            <van-cell title="服务对象管理" is-link @click="goToServiceManage">
                <template #icon>
                    <van-image src="/images/ic_clients.png" width="20" height="20" />
                </template>
            </van-cell>
            <van-cell title="分享转发" is-link @click="handleShare">
                <template #icon>
                    <van-image src="/images/ic_share.png" width="20" height="20" />
                </template>
            </van-cell>
        </van-cell-group>

     
        <div class="logout-section">
            <van-button type="danger" block size="large" @click="handleLogout">
                退出登录
            </van-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
/**
 * H5端个人中心页面
 * 展示用户头像昵称、订单快捷入口、功能菜单、退出登录
 */
import { ref, onMounted, getCurrentInstance } from 'vue';
import { useRouter } from 'vue-router';
import { showConfirmDialog, showToast } from 'vant';

const router = useRouter();

const instance = getCurrentInstance();
const proxy = instance?.proxy as any;

// 用户信息
const userInfo = ref<{ name: string; avatar?: string }>({
    name: '',
    avatar: ''
});

// 加载用户信息
onMounted(() => {
    const storedUserInfo = localStorage.getItem('h5-userInfo');
    if (storedUserInfo) {
        try {
            userInfo.value = JSON.parse(storedUserInfo);
        } catch (e) {
            console.error('解析用户信息失败', e);
        }
    }
});

// 跳转到全部订单
/** 跳转到全部订单列表 */
const goToAllOrders = () => {
    router.push('/order');
};

// 跳转到指定状态的订单列表
/** 跳转到指定状态的订单列表 */
const goToOrderList = (state: string) => {
    // 根据状态映射到对应的路由参数（与订单页 tab name 保持一致）
    const stateMap: Record<string, string> = {
        '待支付': '1',
        '待服务': '2',
        '已完成': '3',
        '已取消': '4'
    };

    const queryState = stateMap[state] || '';
    router.push({
        path: '/order',
        query: { state: queryState }
    });
};

// 跳转到服务对象管理
const goToServiceManage = () => {
    showToast('服务对象管理功能开发中...');
    // router.push('/service-manage');
};



// 处理分享
const handleShare = () => {
    showToast('分享功能开发中...');
};

// 退出登录
/** 退出登录：清除token和用户信息，跳转到登录页 */
const handleLogout = async () => {
    try {
        await showConfirmDialog({
            title: '提示',
            message: '确定要退出登录吗？'
        });

        // 清除本地存储的token和用户信息
        localStorage.removeItem('h5-token');
        localStorage.removeItem('h5-userInfo');

        // 跳转到登录页
        router.push('/login');
    } catch (error) {
        // 用户取消操作
        console.log('用户取消退出');
    }
};
</script>

<style scoped lang="less">
.user-center {
    min-height: calc(100dvh - 50px);
    box-sizing: border-box;
    background-color: #f7f8fa;
    padding-bottom: 1rem;

    .user-info {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        padding: 2rem 1rem;
        background-color: #fff;
        margin-bottom: 1rem;

        .user-name {
            margin-top: 1rem;
            font-size: 1.2rem;
            font-weight: bold;
            color: #333;
        }
    }

    .order-section {
        margin-bottom: 1rem;

        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            border-bottom: 1px solid #f0f0f0;

            .title {
                font-size: 1rem;
                font-weight: bold;
                color: #333;
            }

            .more {
                font-size: 0.9rem;
                color: #999;
                cursor: pointer;
            }
        }

        .order-types {
            display: flex;
            justify-content: space-around;
            padding: 1.5rem 0;

            .order-type-item {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 0.5rem;
                cursor: pointer;

                span {
                    font-size: 0.85rem;
                    color: #666;
                }

                &:active {
                    opacity: 0.7;
                }
            }
        }
    }

    .function-list {
        margin-bottom: 1rem;

        :deep(.van-cell__left-icon) {
            margin-right: 0.5rem;
        }
    }

    .logout-section {
        padding: 0 1rem;
        margin-top: 2rem;
    }
}
</style>
