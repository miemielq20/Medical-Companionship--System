<template>
    <panel-head :route="route"/>
    <div class="bts-into">
        <el-button type="primary" @click="open" :icon="Plus">添加 </el-button>
    </div>

    <el-table :data="tableData.list" style="width: 100%">
        <el-table-column prop="id" label="id" />
        <el-table-column prop="name" label="昵称" />
        <el-table-column prop="permissionName" label="权限管理" />
        <el-table-column label="操作" width="180">
            <template #default="scope">
                <el-button type="primary" @click="open(scope.row)">编辑</el-button>
            </template>
        </el-table-column>>
    </el-table>

    <div class="pagination-into">
        <el-pagination v-model:current-page="paginnationData.pageNum" :page-size="paginnationData.pageSize"
            layout="total, prev, pager, next" @size-change="handleSizeChange" @current-change="handleCurrentChange"
            :total="tableData.total" />
    </div>

    <el-dialog v-model="dialogTableVisible" :before-close="beforeClose" width="30%">
        <h3>添加权限</h3>
        <el-form ref="formRef" :model="form" label-width="80px">
            <el-form-item v-show="false" prop="id" />
            <el-form-item label="权限名称" prop="name" :rules="[
                { required: true, message: '名字不能为空', trigger: 'blur' },
            ]">
                <el-input v-model="form.name" pleaceholder="请输入权限名称" />
            </el-form-item>
            <el-form-item label="权限" prop="permissions" max-width="600px">
                <el-tree style="max-width: 600px" :data="permissionData" show-checkbox node-key="id"
                    :default-expanded-keys="[2, 3]" :default-checked-keys="defalutChecked" ref="treeRef" />
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button type="primary" @click="confirm(formRef)">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
    import { reactive, ref, nextTick } from 'vue'
    import { useRoute } from 'vue-router'
    import { userGetMenu, userSetmenu, menuList, } from '@/api/index'
    import { onMounted } from 'vue'
    import { Plus } from '@element-plus/icons-vue'

    import { type FormInstance } from 'element-plus'
    import { type MenuGroup,type MenuList } from '@/types/menu'

    import PanelHead from '@/components/panel/PanelHead.vue'

    const route = useRoute()
    // 弹窗
    const dialogTableVisible = ref(false)

    //权限菜单数据
    const form = reactive({
        name: '',
        permissions: '',
        id: 0
    })

    // 权限窗口菜单结构
    const permissionData =  ref<MenuGroup[]>([]);

    const treeRef = ref()
    const formRef = ref()

    // 默认选中
    const defalutChecked = [4, 5]

    // 获取权限数据
    onMounted(() => {
        userGetMenu().then(res => {
            permissionData.value = res.data.data
        })
        getList()
    })

    // 列表参数
    const paginnationData = {
        pageNum: 1,
        pageSize: 10
    }

    // 列表数据
    const tableData = reactive({
        list: <MenuList[]>([]),
        total: 0
    })

    //请求列表数据
    const getList = () => {
        menuList(paginnationData).then(res => {
            const { list, total } = res.data.data
            tableData.list = list
            tableData.total = total
        })
    }

    // 页数改变
    const handleSizeChange = (val: number) => {
        paginnationData.pageSize = val
        getList()
    }

    // 分页
    const handleCurrentChange = (val: number) => {
        paginnationData.pageNum = val
        getList()
    }

    // 打开弹窗
    const open = (rowData?: { id: number; name: string; permission: string }) => {
        dialogTableVisible.value = true
        // 编辑
        nextTick(() => {
            if (rowData && rowData.id) {
                Object.assign(form, {
                    name: rowData.name,
                    permissions: rowData.permission,
                    id: rowData.id
                })
                treeRef.value.setCheckedKeys(rowData.permission)
            }
        })

    }
    // 关闭弹窗
    const beforeClose = () => {
        // 重置表单
        formRef.value.resetFields()
        // 取消选中
        treeRef.value.setCheckedKeys(defalutChecked)
        dialogTableVisible.value = false
    }

    //权限提交
    const confirm = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        // 校验
        await formEl.validate(async (valid, fields) => {
            if (valid) {
                const checkedKeys = JSON.stringify(treeRef.value.getCheckedKeys())
                await userSetmenu({ id: form.id, name: form.name, permissions: checkedKeys })
                await getList()
                beforeClose()
            } else {
                console.log('error submit!', fields)
            }
        })
    }
</script>

<style lang="less">
    .pagination-into {
        width: 100%;
        background-color: white;
        display: flex;
        justify-content: flex-end;
    }

    .bts-into {
        width: 100%;
        background-color: white;
        display: flex;
        justify-content: flex-start;
        padding: 10px;
    }

</style>