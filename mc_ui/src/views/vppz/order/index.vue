<template>
    <panel-head :route="route" />

    <div class="bts-into">
        <el-tabs v-model="activeTab" @tab-change="onTabChange">
            <el-tab-pane label="全部" name="" />
            <el-tab-pane label="待支付" name="1" />
            <el-tab-pane label="待服务" name="2" />
            <el-tab-pane label="已完成" name="3" />
            <el-tab-pane label="已取消" name="4" />
        </el-tabs>
    </div>

    <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="out_trade_no" label="订单号" min-width="200" show-overflow-tooltip />
        <el-table-column prop="hospital_name" label="就诊医院" width="140" show-overflow-tooltip />
        <el-table-column prop="service_name" label="服务名称" width="140" />
        <el-table-column label="陪护师" width="160">
            <template #default="scope">
                <div class="companion-info">
                    <el-avatar :src="scope.row.companion?.avatar" :size="32" />
                    <span>{{ scope.row.companion?.name || "-" }}</span>
                </div>
            </template>
        </el-table-column>
        <el-table-column label="陪护师状态" width="100">
            <template #default="scope">
                <el-tag :type="scope.row.companion?.active ? 'success' : 'info'" size="small">
                    {{ scope.row.companion?.active ? '正常' : '禁用' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
            <template #default="scope">
                <span style="color: #f56c6c; font-weight: bold">{{ scope.row.price }} 元</span>
            </template>
        </el-table-column>
        <el-table-column prop="trade_state" label="状态" width="100">
            <template #default="scope">
                <el-tag :type="statusTagType(scope.row.trade_state)">{{ scope.row.trade_state }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="创建时间" width="170">
            <template #default="scope">
                <div class="time-info">
                    <el-icon><Clock /></el-icon>
                    <span>{{ formatTime(scope.row.order_start_time || scope.row.starttime) }}</span>
                </div>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
                <el-button type="primary" size="small" @click="showDetail(scope.row)">详情</el-button>
                <el-button type="success" size="small" :disabled="scope.row.trade_state !== '待服务'" :loading="scope.row._completing" @click="handleComplete(scope.row)">
                    {{ scope.row.trade_state === '待服务' ? '服务完成' : '暂无服务' }}
                </el-button>
            </template>
        </el-table-column>
    </el-table>

    <el-dialog v-model="detailVisible" title="订单详情" width="600px">
        <el-descriptions :column="2" border v-if="currentOrder">
            <el-descriptions-item label="订单号">{{ currentOrder.out_trade_no }}</el-descriptions-item>
            <el-descriptions-item label="状态">
                <el-tag :type="statusTagType(currentOrder.trade_state)">{{ currentOrder.trade_state }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="就诊医院">{{ currentOrder.hospital_name }}</el-descriptions-item>
            <el-descriptions-item label="服务名称">{{ currentOrder.service_name }}</el-descriptions-item>
            <el-descriptions-item label="陪护师">{{ currentOrder.companion?.name || "-" }}</el-descriptions-item>
            <el-descriptions-item label="价格">{{ currentOrder.price }} 元</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ currentOrder.tel }}</el-descriptions-item>
            <el-descriptions-item label="接送地址">{{ currentOrder.receiveAddress }}</el-descriptions-item>
            <el-descriptions-item label="就诊时间">{{ formatTime(currentOrder.starttime) }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.order_start_time) }}</el-descriptions-item>
            <el-descriptions-item label="服务需求" :span="2">{{ currentOrder.demand || "-" }}</el-descriptions-item>
        </el-descriptions>
    </el-dialog>
</template>

<script lang="ts" setup>
/**
 * 订单管理页面
 * 支持按状态Tab切换筛选、订单详情弹窗、服务完成操作
 */
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { Clock } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { orderList, completeOrder } from '@/api';
import type { order } from '@/types/order';
import type { ApiResponse, orderListResponse } from '@/types/response';
import PanelHead from '@/components/panel/PanelHead.vue';

const route = useRoute();
const activeTab = ref("");
const tableData = ref<order[]>([]);
const loading = ref(false);
const detailVisible = ref(false);
const currentOrder = ref<order | null>(null);

/** 订单状态对应的Element Plus标签颜色 */
const statusTagType = (state: string) => {
    const map: Record<string, string> = {
        '待支付': 'warning',
        '待服务': 'primary',
        '已完成': 'success',
        '已取消': 'info',
    };
    return map[state] || 'info';
};

/** 格式化时间戳 */
const formatTime = (value: number | string | undefined) => {
    if (!value) return '-';
    const ts = Number(value);
    if (!Number.isFinite(ts)) return String(value);
    return new Date(ts).toLocaleString('zh-CN');
};

/** 请求订单列表，可传入state筛选 */
const fetchOrders = async (state?: string) => {
    loading.value = true;
    try {
        const res = await orderList(state ? { state } : undefined);
        if (res.data.code === 10000) {
            tableData.value = (res.data.data || []).map(o => ({ ...o, _completing: false }));
        }
    } finally {
        loading.value = false;
    }
};

/** Tab切换时重新加载对应状态的订单 */
const onTabChange = (name: string | number) => {
    const s = String(name);
    fetchOrders(s || undefined);
};

/** 显示订单详情弹窗 */
const showDetail = (row: order) => {
    currentOrder.value = row;
    detailVisible.value = true;
};

/** 完成订单：将待服务状态改为已完成 */
const handleComplete = async (row: any) => {
    row._completing = true;
    try {
        const res = await completeOrder(row.out_trade_no);
        if (res.data.code === 10000) {
            row.trade_state = '已完成';
            ElMessage.success('订单已完成');
        } else {
            ElMessage.error(res.data.msg || '操作失败');
        }
    } catch {
        ElMessage.error('操作失败，请稍后重试');
    }
    row._completing = false;
};

onMounted(() => {
    fetchOrders();
});
</script>

<style scoped>
.bts-into { padding: 10px 0; }
.time-info { display: flex; align-items: center; gap: 4px; }
.companion-info { display: flex; align-items: center; gap: 8px; }
</style>
