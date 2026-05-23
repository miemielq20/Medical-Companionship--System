<template>
    <van-sticky :offset-top="0" z-index="999">
        <div class="header">
            <van-icon name="arrow-left" size="1.5rem" class="back-btn" @click="back" />
            <p class="title">我的订单</p>
        </div>
    </van-sticky>
    <statusBar :item="stateMap[data.trade_state as keyof typeof stateMap]" />

    <div class="tips">
        <div class="dzf" v-if="data.trade_state === '待支付'">
            <div class="text1">
                订单待支付
            </div>
            <div class="text2">
                请在
                <Countdown :time-end="data.time_end" />
                内支付,超时将自动取消订单
            </div>

            <div class="pay">
                <van-button type="success" class="pay-btn" @click="pay">立即支付({{ data.price }})元</van-button>
            </div>
        </div>

        <div class="dzf" v-if="data.trade_state === '待服务'">
            <div class="text1">
                正在为您安排服务专员...
            </div>
            <div class="text2">
                请保持手机通畅无阻,稍后将有专员与您联系
            </div>
        </div>

        <div class="dzf" v-if="data.trade_state === '已完成'">
            <div class="text1">
                服务已完成
            </div>
            <div class="text2">
                感谢您的使用,如有售后问题请联系客服
            </div>
        </div>

        <div class="dzf" v-if="data.trade_state === '已取消'">
            <div class="text1">
                订单已取消
            </div>
            <div class="text2">
                期待下次为您服务,如需帮助请联系客服
            </div>
        </div>
    </div>

    <van-cell-group class="detail-info" v-for="(group, groupIndex) in infoGroups" :key="groupIndex">
        <div class="header-text">{{ group.title }}</div>
        
        <van-cell 
            v-for="(item, itemIndex) in group.items" 
            :key="itemIndex"
            :title="item.label" 
            :value="formatValue(item.value)" 
        />
    </van-cell-group>

    <QrcodePopup 
        v-model="showCode" 
        :code-url="data.code_url || ''"
        title="微信支付"
        tip-text="请使用本人微信扫描二维码"
    />
</template>


<script lang="ts" setup>
    import statusBar from '@/components/bar/statusBar.vue';
    import { getCurrentInstance, ref, onMounted, reactive } from "vue";
    import { useRouter, useRoute } from 'vue-router';
    import { type ApiResponse, type orderDetailResponse, type CreateOrderResponse } from "@/types/response";
    import { type order } from "@/types/order";
    import Countdown from '@/components/Countdown.vue';

    import Qrcode from 'qrcode';

    //二维码弹出框
    const showCode = ref(false)
    //二维码
    const qrcode = ref('')

    const { proxy } = getCurrentInstance() as any;
    const router = useRouter();
    const route = useRoute();

    //订单状态
    const stateMap = {
        '待支付': "10",
        '待服务': "20",
        '已完成': "30",
        '已取消': "40",
    }
    const back = () => {
        router.go(-1);
    };

    const data = ref<order>({} as order)

    // 动态生成的信息分组
    const infoGroups = ref<Array<{
        title: string;
        items: Array<{ label: string; value: any }>;
    }>>([])
    

    onMounted(() => {
        proxy.$api.orderDetail({ oid: route.query.oid }).then((res: ApiResponse<orderDetailResponse>) => {
            data.value = res.data.data
            data.value.time_end = data.value.order_start_time + 7200000;

            // 动态生成信息分组
            generateInfoGroups()
        })
    })

    const close = () => {
        showCode.value = false;
    };

    const pay = () => {
        showCode.value = true;
    };

    // 格式化值显示
    const formatValue = (value: any) => {
        if (value === undefined || value === null) return '-';
        if (typeof value === 'number' && value > 1000000000000) {
            // 时间戳，需要格式化
          return new Date(value).toISOString().slice(0, 10);
        }
        if (typeof value === 'number' && value > 1000000000) {
            // 金额，保留两位小数
            return value.toFixed(2);
        }
        return String(value);
    };

    // 动态生成信息分组
    const generateInfoGroups = () => {
        const groups = [];

        // 预约信息
        const appointmentItems = [
            { label: '服务名称', value: data.value.service_name },
            { label: '医院名称', value: data.value.hospital_name },
            { label: '陪诊员', value: data.value.companion?.name || '待分配' },
            { label: '联系电话', value: data.value.tel },
            { label: '预约时间', value: data.value.starttime },
            { label: '就诊地址', value: data.value.receiveAddress },
            { label: '就诊需求', value: data.value.demand }
        ];
        groups.push({ title: '预约信息', items: appointmentItems });

        // 患者信息
        const patientItems = [
            { label: '姓名', value: data.value.client?.name },
            { label: '手机号', value: data.value.client?.mobile }
        ];
        groups.push({ title: '患者信息', items: patientItems });

        // 订单信息
        const orderItems = [
            { label: '订单编号', value: data.value.out_trade_no },
            { label: '交易流水号', value: data.value.transaction_id || '-' },
            { label: '订单金额', value: data.value.price },
            { label: '实付金额', value: data.value.paidPrice },
            { label: '创建时间', value: data.value.order_start_time }
        ];
        groups.push({ title: '订单信息', items: orderItems });

        infoGroups.value = groups;
        console.log(infoGroups.value)
    };

</script>

<style scoped lang="less">

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

    .dzf {
        padding: 2rem;

        .text1 {
            font-size: 1.5rem;
            color: #424242;
            font-weight: bold;
        }

        .pay {
            width: 100%;
            height: 3.5rem;
            display: flex;
            justify-content: center;
            margin: 1.5rem auto 0 auto;

            .pay-btn {
                width: 80%;
                font-weight: bold;
            }
        }

    }

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

    .detail-info {
        margin-top: 1rem;
        overflow: hidden;

        .header-text {
            position: relative;
            padding-left: 1rem;
            font-size: 1.2rem;
            font-weight: bold;
            color: #333;
            margin-bottom: 0.5rem;
        }

        .header-text::before {
            content: '';
            position: absolute;
            left: 0.5rem;
            top: 50%;
            transform: translateY(-50%);
            width: 0.25rem;
            height: 2rem;
            background-color: #ff4d4f;
        }
    }



</style>
