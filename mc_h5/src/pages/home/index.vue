<template>
    <div class="home-container">
        <van-sticky>
        <div class="header">
            <div class="header-left" @click="showAreaPopup = true">
                <span class="header-left-text">{{ selectedProvinceDisplay }}</span>
                <van-icon name="arrow-down" size="12" />
            </div>
            <div class="header-right">
                <van-search v-model="searchText" placeholder="请输入搜索关键词" shape="round" />
            </div>
        </div>
        </van-sticky>

        <van-popup v-model:show="showAreaPopup" round position="bottom">
            <van-area
                :area-list="areaList"
                :columns-num="1"
                :value="selectedProvinceCode"
                title="选择省份"
                @confirm="onAreaConfirm"
                @cancel="showAreaPopup = false"
            />
        </van-popup>

        <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
            <van-swipe-item v-for="item in data.slides" :key="item.id">
                <img :src="item.pic_image_url" alt="" style="width: 100%; height: 200px; object-fit: cover;" />
            </van-swipe-item>
        </van-swipe>

        <van-row justify="space-around">
            <van-col v-for="(value,index) in data.nav2s" :key="value.id" span="11" @click="goOrderTwo(index)">
                <van-image :src="value.pic_image_url" />
            </van-col>
        </van-row>

        <van-row justify="space-around" v-for="value in data.hospitals" :key="value.id" class="hospital-item"
            @click="goOrder(value)">
            <van-col span="6">
                <van-image width="100" height="90" :src="value.avatar_url" />
            </van-col>
            <van-col span="15">
                <div class="hospital-info">
                    <h2 class="hospital-name">{{ value.name }}</h2>
                    <p class="hospital-rank">{{ value.rank }} {{ value.label }}</p>
                    <p class="hospital-intro">{{ value.intro }}</p>
                </div>
            </van-col>
        </van-row>
    </div>
</template>

<script setup lang="ts">
/**
 * H5端首页
 * 展示轮播图、导航入口、推荐医院列表，顶部搜索栏吸顶固定，支持省份切换
 */
import { reactive, ref, computed, onMounted, getCurrentInstance } from 'vue';
import { type HomeHospitals, type HomeIndex } from '@/types/h5_index';
import { type h5IndexResponse, type ApiResponse } from '@/types/response';
import { areaList } from '@vant/area-data';
import { useRouter } from 'vue-router';

const router = useRouter();
const instance = getCurrentInstance();
const proxy = instance?.proxy as any;

// 默认选中江西省
const selectedProvinceCode = ref('360000');
const selectedProvince = ref('江西');
const selectedProvinceDisplay = computed(() => selectedProvince.value.replace(/省|市|自治区|特别行政区$/, ''));
const showAreaPopup = ref(false);
const searchText = ref('');

// 首页数据
const data = reactive<HomeIndex>({
    now: '',
    hospitals: [],
    nav2s: [],
    navs: [],
    slides: []
});

// 加载首页数据
const loadData = () => {
    const province = selectedProvince.value.replace(/省|市|自治区|特别行政区$/g, '');
            proxy.$api.homeIndex({ province })
        .then((res: ApiResponse<h5IndexResponse>) => {
            Object.assign(data, res.data.data);
            console.log(data);
        });
};

onMounted(() => {
    loadData();
});

// 省份选择确认
const onAreaConfirm = ({ selectedOptions }: any) => {
    selectedProvince.value = selectedOptions[0].text;
    selectedProvinceCode.value = selectedOptions[0].code;
    showAreaPopup.value = false;
    loadData();
};

/** 点击导航入口跳转到下单页 */
const goOrderTwo = (index: number) => {
    router.push(`/createOrder`);
};

/** 点击医院卡片跳转到下单页 */
const goOrder = (data: HomeHospitals) => {
    router.push(`/createOrder?id=${data.id}`);
};
</script>

<style lang="less" scoped>
.home-container {
    padding-bottom: 4rem;
}

.header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 10px;
    height: 50px;
    background-color: #fff;
    top: 0;
    z-index: 999;

    .header-left {
        display: flex;
        align-items: center;
        width: 40%;
        cursor: pointer;

        .header-left-text {
            font-size: 1.2rem;
            font-weight: bold;
            margin-right: 5px;
        }
    }

    .header-right {
        display: flex;
        justify-content: flex-end;
        width: 60%;
    }
}

.hospital-item {
    display: flex;
    flex-direction: row;
    overflow: hidden;
    border-radius: 0.5rem;
    border: 1px solid #eee;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 0.1rem;
    margin-top: 1.4rem;

    .hospital-info {
        .hospital-name {
            font-size: 1.2rem;
            font-weight: bold;
        }

        .hospital-rank {
            font-size: 1rem;
            font-weight: bold;
            color: rgb(18, 140, 126);
        }

        .hospital-intro {
            margin: .5rem 0;
            font-size: .8rem;
            color: rgb(102, 102, 102);
        }
    }
}
</style>