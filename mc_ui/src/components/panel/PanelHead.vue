<template>
    <div class="header">
        <h3>
            {{ route?.meta.name }}
        </h3>
        <p>
            {{ route?.meta.describe }}
        </p>
    </div>

</template>

<style scoped>
    
    .header {
        width: 100%;
        height: 70px;
        display: block;
        background-color: #f5f5f5;
        padding: 10px !important;
    }
</style>

<script lang="ts" setup>
   defineProps(['route'])
   

</script>