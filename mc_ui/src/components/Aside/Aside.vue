<template>
  <el-row class="tac">
    <el-col :span="24">
      <el-menu :style="{ width: isCollapse ? '50px' : '200px' }" active-text-color="#ffd04b" background-color="#545c64"
        class="el-menu-vertical-demo" :default-active="menuActive" text-color="#fff"   :collapse="isCollapse" >
        <p class="title">{{ isCollapse?'DIDI' :'DIDI陪诊' }}</p>
        <TreeMenu :AsideMenu="AsideMenu" :Aindex="'1'"/>
      </el-menu>
    </el-col> 
  </el-row>
</template>


<script lang="ts" setup>

  import TreeMenu from '@/components/aside/TreeMenu.vue'
  import { useAsideStore } from '@/stores/aside'
  import { useRouterStore } from '@/stores/router'


  const AsideStore = useAsideStore()
  const RouterStore = useRouterStore()

  //侧边栏状态
  const isCollapse = computed(() => {
    return AsideStore.asideCollapse
  })

  const menuActive = computed(() => {
    return AsideStore.menuActive
  })

  import { computed } from 'vue'
  
  // 侧边栏菜单 - 从store中获取动态路由
  const AsideMenu = computed(() => RouterStore.routerList)

</script>
<style lang="less">
  .tac {
    height: 100%;
    overflow: hidden;

    .el-menu-vertical-demo {
      height: 100%;
      transition: none!important;

      .title {
        text-align: center;
        color: white;
        font-size: 24px;
      }
    }

  }
</style>
