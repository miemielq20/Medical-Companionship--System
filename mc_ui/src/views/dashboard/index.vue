<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总订单</p>
              <p class="stat-value">{{ dashboardData.totalOrders ?? 0 }}</p>
            </div>
            <div class="stat-icon order-icon">
              <el-icon :size="48"><Document /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总用户</p>
              <p class="stat-value">{{ dashboardData.totalUsers ?? 0 }}</p>
            </div>
            <div class="stat-icon user-icon">
              <el-icon :size="48"><User /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">总流水</p>
              <p class="stat-value">¥{{ formatPrice(dashboardData.totalRevenue ?? 0) }}</p>
            </div>
            <div class="stat-icon revenue-icon">
              <el-icon :size="48"><Money /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 今日订单 + 系统信息 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card shadow="hover" class="table-card">
          <template #header>
            <span class="card-title">今日订单</span>
          </template>
          <el-table :data="dashboardData.todayOrders ?? []" style="width: 100%" v-loading="loading">
            <el-table-column label="陪护师" min-width="140">
              <template #default="scope">
                <div class="companion-cell">
                  <el-avatar :src="scope.row.companionAvatar" :size="32" />
                  <span>{{ scope.row.companionName || "-" }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="userName" label="用户" min-width="100" />
            <el-table-column label="收益" width="120">
              <template #default="scope">
                <span style="color: #f56c6c; font-weight: bold">¥{{ scope.row.revenue ?? 0 }}</span>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="170">
              <template #default="scope">
                <span>{{ formatTime(scope.row.time) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="scope">
                <el-tag :type="tradeStateType(scope.row.tradeState)" size="small">
                  {{ scope.row.tradeState || "-" }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="sysinfo-card">
          <template #header>
            <span class="card-title">系统信息</span>
          </template>
          <div class="sysinfo-list" v-if="dashboardData.systemInfo">
            <div class="sysinfo-item">
              <span class="sysinfo-label">系统名称</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.systemName }}</span>
            </div>
            <div class="sysinfo-item">
              <span class="sysinfo-label">系统版本</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.systemVersion }}</span>
            </div>
            <div class="sysinfo-item">
              <span class="sysinfo-label">操作系统</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.os }}</span>
            </div>
            <div class="sysinfo-item">
              <span class="sysinfo-label">Java版本</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.javaVersion }}</span>
            </div>
            <div class="sysinfo-item">
              <span class="sysinfo-label">当前时间</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.currentTime }}</span>
            </div>
            <div class="sysinfo-item">
              <span class="sysinfo-label">运行环境</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.runtimeEnv }}</span>
            </div>
            <div class="sysinfo-item">
              <span class="sysinfo-label">服务器IP</span>
              <span class="sysinfo-value">{{ dashboardData.systemInfo.serverIp }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 订单统计图表 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <span class="card-title">订单统计</span>
          </template>
          <v-chart :option="chartOption" style="height: 360px" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts" setup>
/**
 * 控制台仪表盘页面
 * 展示总订单/总用户/总流水统计卡片、今日订单列表、系统运行信息、近7天订单折线图
 */
import { ref, onMounted, computed } from 'vue';
import { Document, User, Money } from '@element-plus/icons-vue';
import { dashboard } from '@/api';
import VChart from 'vue-echarts';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart } from 'echarts/charts';
import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components';
import type { DashboardData } from '@/types/response';

const loading = ref(false);
const dashboardData = ref<DashboardData>({
  totalOrders: 0,
  totalUsers: 0,
  totalRevenue: 0,
  todayOrders: [],
  orderChart: [],
  systemInfo: {
    systemVersion: "",
    os: "",
    javaVersion: "",
    currentTime: "",
    runtimeEnv: "",
    serverIp: "",
    systemName: "",
  },
});

/** 格式化金额，千分位+两位小数 */
const formatPrice = (val: number) => {
  return Number(val).toLocaleString("zh-CN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
};

/** 格式化时间戳为本地时间字符串 */
const formatTime = (value: number | string | undefined) => {
  if (!value) return "-";
  const ts = Number(value);
  if (!Number.isFinite(ts)) return String(value);
  return new Date(ts).toLocaleString("zh-CN");
};

/** 根据订单状态返回Element Plus标签类型 */
const tradeStateType = (state: string) => {
  const map: Record<string, string> = {
    '待支付': 'warning',
    '待服务': 'primary',
    '已完成': 'success',
    '已取消': 'info',
  };
  return map[state] || "info";
};

/** 请求仪表盘数据 */
const fetchDashboard = async () => {
  loading.value = true;
  try {
    const res = await dashboard();
    if (res.data.code === 10000) {
      dashboardData.value = res.data.data;
    }
  } finally {
    loading.value = false;
  }
};

// 注册ECharts所需组件
use([CanvasRenderer, LineChart, GridComponent, TooltipComponent, LegendComponent]);

/** 订单统计折线图配置：双Y轴（订单数+金额） */
const chartOption = computed(() => {
  const data = dashboardData.value.orderChart ?? [];
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['订单数', '金额(元)'],
      top: 5
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map((d) => d.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        minInterval: 1
      },
      {
        type: 'value',
        name: '金额(元)'
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: data.map((d) => d.count),
        lineStyle: { color: '#409eff', width: 2 },
        itemStyle: { color: '#409eff' }
      },
      {
        name: '金额(元)',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: data.map((d) => d.amount),
        lineStyle: { color: '#67c23a', width: 2 },
        itemStyle: { color: '#67c23a' }
      }
    ]
  };
});

onMounted(() => {
  fetchDashboard();
});
</script>

<style scoped>
.dashboard { padding: 20px; }
.stat-row { margin-bottom: 20px; }
.stat-card { cursor: pointer; transition: transform 0.2s; }
.stat-card:hover { transform: translateY(-2px); }
.stat-content { display: flex; justify-content: space-between; align-items: center; }
.stat-label { font-size: 14px; color: #909399; margin: 0 0 8px 0; }
.stat-value { font-size: 28px; font-weight: bold; color: #303133; margin: 0; }
.stat-icon { width: 72px; height: 72px; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.order-icon { background: #ecf5ff; color: #409eff; }
.user-icon { background: #f0f9eb; color: #67c23a; }
.revenue-icon { background: #fdf6ec; color: #e6a23c; }
.content-row { margin-bottom: 20px; }
.card-title { font-size: 16px; font-weight: bold; }
.table-card { height: 100%; }
.sysinfo-card { height: 100%; }
.companion-cell { display: flex; align-items: center; gap: 8px; }
.sysinfo-list { display: flex; flex-direction: column; gap: 12px; }
.sysinfo-item { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #f0f0f0; }
.sysinfo-item:last-child { border-bottom: none; }
.sysinfo-label { font-size: 13px; color: #909399; }
.sysinfo-value { font-size: 13px; color: #303133; font-weight: 500; text-align: right; max-width: 60%; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.chart-row { margin-top: 20px; }
</style>