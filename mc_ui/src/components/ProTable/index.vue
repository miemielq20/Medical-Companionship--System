<template>
  <div class="pro-table">
    <el-table
      :data="data"
      style="width: 100%"
      table-layout="auto"
      v-loading="loading"
      v-bind="$attrs"
      @selection-change="onSelectionChange"
    >
      <slot />
    </el-table>
    <div class="pro-table-pagination" v-if="showPagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :page-sizes="pageSizes"
        :layout="paginationLayout"
        :total="total"
        @size-change="onSizeChange"
        @current-change="onPageChange"
      />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';

/**
 * ProTable 通用表格组件
 * 封装 el-table + el-pagination，统一处理 loading、分页、选中行
 * 通过 v-bind="$attrs" 透传所有 el-table 原生属性
 */
const props = withDefaults(defineProps<{
  data: any[];
  loading?: boolean;
  total?: number;
  pageNum?: number;
  pageSize?: number;
  pageSizes?: number[];
  showPagination?: boolean;
  paginationLayout?: string;
}>(), {
  loading: false,
  total: 0,
  pageNum: 1,
  pageSize: 10,
  pageSizes: () => [10, 20, 50, 100],
  showPagination: true,
  paginationLayout: 'total, sizes, prev, pager, next, jumper',
});

const emit = defineEmits<{
  'page-change': [pageNum: number, pageSize: number];
  'selection-change': [selection: any[]];
}>();

const currentPage = ref(props.pageNum);

watch(() => props.pageNum, (val) => { currentPage.value = val; });

const onPageChange = (page: number) => {
  currentPage.value = page;
  emit('page-change', page, props.pageSize);
};

const onSizeChange = (size: number) => {
  currentPage.value = 1;
  emit('page-change', 1, size);
};

const onSelectionChange = (selection: any[]) => {
  emit('selection-change', selection);
};
</script>

<style scoped>
.pro-table { width: 100%; }
.pro-table-pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>