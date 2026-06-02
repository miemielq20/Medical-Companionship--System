<template>
    <div class="create-order">
        <div class="header">
            <van-icon name="arrow-left" size="1.5rem" class="header-left" @click="back" />
            <p class="title">请填服务订单</p>
        </div>
        <statusBar item="0" />
        <van-cell-group inset class="cells">
            <van-cell>
                <template #title>
                    <div class="title-cell">
                        <van-image :src="data.service.serviceImg" width="25" height="25" />
                        <span class="custom-title">{{ data.service.serviceName }}</span>
                    </div>
                </template>
                <template #default>
                    <div class="text"><van-icon name="info-o" style="margin-right: 0.8rem;" size="1rem" />服务内容</div>
                </template>
            </van-cell>
        </van-cell-group>

        <van-cell-group inset class="cells">
            <van-cell is-link @click="showHospital = true">
                <template #title>
                    <div class="title-cell">
                        <span class="custom-title">就诊医院</span>
                    </div>
                </template>
                <template #default>
                    <div class="text">{{ form.hospital_name || '请选择就诊医院' }}</div>
                </template>
            </van-cell>

            <van-cell is-link @click="showStarTime = true">
                <template #title>
                    <div class="title-cell">
                        <span class="custom-title">就诊时间</span>
                    </div>
                </template>
                <template #default>
                    <div class="text">{{ currentDate || '请选择就诊时间' }}</div>
                </template>
            </van-cell>

            <van-cell is-link @click="showCompanion = true">
                <template #title>
                    <div class="title-cell">
                        <span class="custom-title">陪护师</span>
                    </div>
                </template>
                <template #default>
                    <div class="text">{{ compoinonName || '请选择陪护师' }}</div>
                </template>
            </van-cell>

            <van-cell class="cell-item">
                <template #title>
                    <div class="title-cell">
                        <span class="custom-title">接送地址</span>
                    </div>
                </template>
                <template #default>
                    <van-field v-model="form.receiveAddress" placeholder="请填写接送地址" class="field-item"
                        input-align="right" />
                </template>
            </van-cell>
            <van-cell class="cell-item">
                <template #title>
                    <div class="title-cell">
                        <span class="custom-title">联系电话</span>
                    </div>
                </template>
                <template #default>
                    <van-field v-model="form.tel" type="tel" placeholder="请填写联系电话" input-align="right" 
                        :error-message="telError" @input="validateTel" />
                </template>
            </van-cell>
        </van-cell-group>

        <van-cell-group inset title="服务需求">
            <van-field v-model="form.demand" placeholder="请简单描述您要就诊的科室..." input-align="left" style="height: 6rem;" />
        </van-cell-group>

        <van-button type="primary" size="large" @click="onSubmit" class="submit">提交</van-button>

        <van-popup v-model:show="showHospital" round position="bottom" :style="{ height: '30%' }">
            <van-picker :columns="showHospColumns" @confirm="showConfirm" @cancel="showHospital = false" />
        </van-popup>

        <van-popup v-model:show="showStarTime" round position="bottom" :style="{ height: '30%' }">
            <van-date-picker title="选择日期" :min-date="minDate" @confirm="showStarTimeConfirm"
                @cancel="showStarTime = false" />
        </van-popup>

        <van-popup v-model:show="showCompanion" round position="bottom" :style="{ height: '30%' }">
            <van-picker :columns="showCompanionColumns" @confirm="showCompanionConfirm"
                @cancel="showCompanion = false" />
        </van-popup>

        <!-- 使用封装的二维码组件 -->
        <QrcodePopup 
            v-model="showCode" 
            :code-url="wxCodeUrl"
            title="微信支付"
            tip-text="请使用本人微信扫描二维码"
            @close="handleQrcodeClose"
        />
    </div>
</template>


<style lang="less" scoped>
    :deep(.van-field__error-message) {
        text-align: right !important;
    }

    .create-order {
        .header {
            display: flex;
            align-items: center;
            padding: 10px;
            background-color: #fff;
            font-size: 18px;

            .header-left {
                width: 6%;
            }

            .title {
                width: 90%;
                text-align: center;
                font-weight: bold;
                color: rgb(61, 58, 58);
            }
        }

        .cells {
            margin-top: 1rem;

            .cell-item {
                display: flex;
                align-items: center;
            }

            .title-cell {
                display: flex;
                align-items: center;

                .custom-title {
                    margin-left: 10px;
                    font-size: 16px;
                    white-space: nowrap;

                }
            }
        }

        .submit {
            position: absolute;
            bottom: 0;
        }
    }

    // 二维码弹窗样式
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


<script lang="ts" setup>

    import statusBar from '@/components/bar/statusBar.vue';
    import { onMounted, reactive, getCurrentInstance, ref, computed } from 'vue';
    import { useRouter, useRoute } from 'vue-router';

    import { type h5CompanionResponse, type ApiResponse, type CreateOrderResponse } from '@/types/response';
    import { type CreateOrderForm } from '@/types/order'
    import { type h5Companion } from '@/types/h5_companion';
    import { showNotify } from 'vant';
    import QrcodePopup from '@/components/QrcodePopup.vue';


    const instance = getCurrentInstance()
    const proxy = instance?.proxy as any
    const router = useRouter();
    const route = useRoute();

    // 选择医院弹出框
    const showHospital = ref(false)
    // 选择时间弹出框
    const showStarTime = ref(false)
    // 选择陪护员弹出框
    const showCompanion = ref(false)
    //二维码弹出框
    const showCode=ref(false)
    // 当前时间
    const currentDate = ref()
    // 陪护员名字
    const compoinonName = ref()
    //最小就诊时间
    const minDate = ref(new Date())
    // 手机号错误提示
    const telError = ref('')
    // 二维码URL（用于QrcodePopup组件）
    const wxCodeUrl = ref('')

    //医院数据
    const showHospColumns = computed(() => {
        return data.hospitals.map(item => {
            return {
                text: item.name,
                value: item.id
            }
        })
    })

    //陪护师数据
    const showCompanionColumns = computed(() => {
        return data.companion.map(item => {
            return {
                text: item.name,
                value: item.id
            }
        })
    })


    //陪护师数据
    const data = reactive<h5Companion>({
        companion: [],
        hospitals: [],
        service: {
            serviceName: '',
            serviceImg: ''
        }
    })

    //订单表单数据
    const form = reactive<CreateOrderForm>({
        hospital_id: 0,
        hospital_name: '',
        starttime: 0,
        companion_id: 0,
        receiveAddress: '',
        tel: '',
        demand: ''
    })

    //选择医院
    const showConfirm = (item: any) => {
        form.hospital_id = item.selectedOptions[0].value
        form.hospital_name = item.selectedOptions[0].text
        showHospital.value = false
    }

    //选择陪护师
    const showCompanionConfirm = (item: any) => {
        form.companion_id = item.selectedOptions[0].value
        compoinonName.value = item.selectedOptions[0].text
        showCompanion.value = false
    }

    //选择时间
    const showStarTimeConfirm = (item: any) => {
        const dateStr = item.selectedValues.join('-');
        form.starttime = new Date(dateStr).getTime()
        showStarTime.value = false
        currentDate.value = dateStr
     
    };

    // 验证手机号格式
    const validateTel = () => {
        const telRegex = /^1[3-9]\d{9}$/;
        if (!form.tel) {
            telError.value = '';
        } else if (!telRegex.test(form.tel)) {
            telError.value = '请输入正确的手机号';
        } else {
            telError.value = '';
        }
    };

    const close=() => { 
        showCode.value=false;
        router.push('/order');
    };

    // 处理二维码弹窗关闭
    const handleQrcodeClose = () => {
        router.push('/order');
    };

    const onSubmit = () => {
        // 验证手机号格式
        validateTel();
        if (telError.value) {
            showNotify({ message: telError.value, className: 'custom-notify' });
            return;
        }

        const params:(keyof CreateOrderForm) []= [
            "hospital_id",
            "hospital_name",
            "demand",
            "companion_id",
            "receiveAddress",
            "tel",
            "starttime",
        ]

        // 表单验证
        for (const i of params){
          if(!form[i]){ 
            showNotify({ message: '请把每一项都填完',className: 'custom-notify' });
            return;
          }
        }

        // 创建订单
        proxy.$api.createOrder(form).then((res:ApiResponse<CreateOrderResponse>) =>{ 
              const {wx_code} = res.data.data;
              wxCodeUrl.value = wx_code;
              showCode.value = true;
            })
    };

    //获取医院和陪护师数据，自动匹配首页传入的医院id
    onMounted(() => {
        proxy.$api.h5Companion().then((res: ApiResponse<h5CompanionResponse>) => {
            Object.assign(data, res.data.data)
            // 首页点击医院跳转时自动选中
            const hospitalId = route.query.id
            if (hospitalId) {
                const hospital = data.hospitals.find((h: any) => String(h.id) === String(hospitalId))
                if (hospital) {
                    form.hospital_id = hospital.id
                    form.hospital_name = hospital.name
                }
            }
        })
    })

    const back = () => {
        router.go(-1)
    }
</script>
