<template>
    <div class="main-container">
        <RouterView />
        <van-tabbar v-model="active">
            <van-tabbar-item v-for="item in data?.children" :key="item.path" :icon="item.meta?.icon" @click="router.push(item.path)">{{ (item.meta as any)?.name }}</van-tabbar-item>
        </van-tabbar>
    </div>
</template>

<script setup lang="ts">
    import { ref } from 'vue';
    import { useRoute,useRouter } from 'vue-router';

    const route = useRoute();
    const router = useRouter();

    const active = ref(0);
    const data = router.options.routes[0];
    if (data && data.children) {
        active.value = data.children.findIndex(item => '/' + item.path === route.path);
    }

</script>