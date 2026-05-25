<template>
  <div class="common-layout">
    <el-container class="container">
      <Aside />
      <el-container>
        <el-header class="header">
          <Header />
        </el-header>
        <el-main>
                  <Suspense>
          <RouterView />
          <template #fallback>
            <div class="route-loading">
              <el-icon class="is-loading" :size="32"><Loading /></el-icon>
              <p>页面加载中...</p>
            </div>
          </template>
        </Suspense>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
  import Aside from '@/components/aside/Aside.vue';
  import Header from '@/components/header/Header.vue';
import { Loading } from '@element-plus/icons-vue';
</script>

<style lang="less">
  .common-layout {
    height: 100%;

    .container {
      height: 100%;
      .header{
        padding: 0;
      }
    }
  }
.route-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 60vh;
  color: #909399;
  gap: 12px;
}
</style>
