<template>
    <div>
        <template v-for="item in AsideMenu" :key="item.name" >
            <el-menu-item v-if="!item.children || item.children.length == 0" :index="`${Aindex}-${item.meta?.id}`"
            @click="handleClick(item,`${Aindex}-${item.meta?.id}`)">
                <el-icon v-if="item.meta?.icon" size="24">
                    <component :is="item.meta?.icon" />
                </el-icon>
                <span>{{ item.meta?.name }}</span>
            </el-menu-item>
            <el-sub-menu v-else :index="`${Aindex}-${item.meta?.id}`">
                <template #title>
                    <el-icon>
                       <component :is="item.meta?.icon" />
                    </el-icon>
                     <span>{{ item.meta?.name }}</span>
                </template>
             <tree-menu :AsideMenu="item.children" :Aindex="`${Aindex}-${item.meta?.id}`"></tree-menu>
            </el-sub-menu>
        </template>
    </div>
</template>

<script setup lang="ts">
     import { useRouter } from 'vue-router'
     import {type MenuItem} from "@/types/router"
     import { useAsideStore } from '@/stores/aside'
    

     const asideStore = useAsideStore();
     const { addMenu,updateMenuActive }=asideStore
     const router = useRouter()
  
    const handleClick = (item: MenuItem,index:string) => {
  
     //添加侧边栏菜单信息
     addMenu(item)
     updateMenuActive(index)
       //侧边栏路由跳转
     if (item.meta?.path) {
       router.push(item.meta.path)
     }
    }
    
    defineProps<{
        AsideMenu: MenuItem[]
        Aindex:string
    }>()
</script>