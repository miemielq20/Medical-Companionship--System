<template>
  <div class="common-layout">
    <el-container class="container">
      <Aside />
      <el-container>
        <el-header class="header">
          <Header />
        </el-header>
        <el-main>
          <RouterView />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script lang="ts" setup>
  import Aside from '@/components/aside/Aside.vue';
  import Header from '@/components/header/Header.vue';
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
</style>
