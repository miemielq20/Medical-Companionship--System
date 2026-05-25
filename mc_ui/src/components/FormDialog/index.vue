<template>
  <el-dialog
    :model-value="visible"
    :title="title"
    :width="width"
    :before-close="onClose"
    :close-on-click-modal="closeOnClickModal"
    v-bind="$attrs"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
    >
      <slot :form="formData" />
    </el-form>
    <template #footer>
      <el-button @click="onClose">{{ cancelText }}</el-button>
      <el-button type="primary" :loading="confirmLoading" @click="onConfirm">
        {{ confirmText }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';

/**
 * FormDialog 通用表单弹窗组件
 * 封装 el-dialog + el-form，统一处理弹窗显隐、表单校验、loading状态
 * 通过默认插槽暴露 form 数据，子组件直接 v-model="form.xxx"
 */
const props = withDefaults(defineProps<{
  visible: boolean;
  title?: string;
  formData: Record<string, any>;
  rules?: FormRules;
  width?: string;
  labelWidth?: string;
  confirmText?: string;
  cancelText?: string;
  confirmLoading?: boolean;
  closeOnClickModal?: boolean;
}>(), {
  title: '表单',
  width: '500px',
  labelWidth: '80px',
  confirmText: '确定',
  cancelText: '取消',
  confirmLoading: false,
  closeOnClickModal: false,
});

const emit = defineEmits<{
  'update:visible': [value: boolean];
  'confirm': [formEl: FormInstance | undefined];
  'cancel': [];
}>();

const formRef = ref<FormInstance>();

const onClose = () => {
  emit('update:visible', false);
  formRef.value?.resetFields();
  emit('cancel');
};

const onConfirm = async () => {
  if (!formRef.value) return;
  await formRef.value.validate((valid) => {
    if (valid) {
      emit('confirm', formRef.value);
    }
  });
};

/** 暴露表单实例，父组件可通过 ref 调用 validate/resetFields 等 */
defineExpose({ formRef });
</script>

<style scoped>
</style>