<template>
    <panel-head :route="route"/>

    <el-table :data="tableData.list" style="width: 100%">
        <el-table-column prop="id" label="id" />
        <el-table-column prop="name" label="昵称" />
        <el-table-column prop="permissions_id" label="所属组别">
            <template #default="scope">
                {{ permissionName(scope.row.permissions_id) }}
            </template>
        </el-table-column>
        <el-table-column prop="active" label="状态">
            <template #default="scope">
                <el-tag :type="scope.row.active ? 'success' : 'danger'">{{ scope.row.active ? '正常' : '异常' }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号" />
        <el-table-column prop="create_time" label="创建时间">
            <template #default="scope">
                <div class="time-into">
                    <el-icon>
                        <Clock />
                    </el-icon>
                    <span>{{ scope.row.create_time }}</span>
                </div>
            </template>
        </el-table-column>>
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
        <h3>编辑权限</h3>
        <el-form ref="formRef" :model="form" label-width="80px" :rules="rulers">
            <el-form-item label="手机号" prop="mobile">
                <el-input v-model="form.mobile" disabled />
            </el-form-item>
            <el-form-item label="昵称" prop="name">
                <el-input v-model="form.name" />
            </el-form-item>
            <el-form-item label="菜单权限" prop="permissions_id">
                <el-select v-model="form.permissions_id" placeholder="请选择菜单权限" style="width: 240px">
                    <el-option v-for="item in options" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button type="primary" @click="confirm(formRef)">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>

    import { authAdmin, menuSelectlist,updateUser } from '@/api/index'
    import { ref, reactive, onMounted } from 'vue'
    import { useRoute } from 'vue-router'
    import { useRouterStore } from '@/stores/router'
    import { useAsideStore } from '@/stores/aside'

    import { type FormInstance } from 'element-plus'
    import { type PermissionGroup, type Permissions } from '@/types/permisson'
    import dayjs from 'dayjs'

    const routerStore = useRouterStore()
     const asideStore = useAsideStore()
    const route = useRoute()
    // 弹窗
    const dialogTableVisible = ref(false)

    //权限菜单数据
    const form = reactive({
        name: '',
        permissions_id: 0,
        mobile: 0,
    })

    const formRef = ref()

    // 列表参数
    const paginnationData = {
        pageNum: 1,
        pageSize: 10
    }

    // 列表数据
    const tableData = reactive({
        list: <PermissionGroup[]>([]),
        total: 0
    })

    // 权限名称
    const options = ref<PermissionGroup[]>([])

    // 获取权限数据
    onMounted(() => {
        getList()
        menuSelectlist().then(res => {
            options.value = res.data.data
        })
    })
    // 权限名称
    const permissionName = (id: number) => {
        const data = options.value.find(item => item.id === id)
        return data ? data.name : '超级管理员'
    }
    //请求列表数据
    const getList = () => {
        authAdmin(paginnationData).then(res => {
            // 处理数据类型
            const { list, total } = res.data.data as { list: Permissions[], total: number}
            list.forEach(item => {
                item.create_time = dayjs(item.create_time).format('YYYY-MM-DD')
                    const data = JSON.parse(localStorage.userInfo );
                    data.name =item.name;
                    localStorage.userInfo = JSON.stringify(data);
                    })
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
    const open = (rowData?: { name: string; permissions_id: number; mobile: number }) => {
        dialogTableVisible.value = true
        if (rowData) {
            Object.assign(form, {
                name: rowData.name,
                permissions_id: rowData.permissions_id, 
                mobile: rowData.mobile
            })
        }
    }
    // 关闭弹窗
    const beforeClose = () => {
        dialogTableVisible.value = false
    }

    const rulers = { 
        name:[{required: true, message: '请输入昵称', trigger: 'blur'}],
        permissions_id:[{required: true, message: '请选择权限', trigger: 'blur'}] 
    }

     //权限提交
    const confirm = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        // 校验
        await formEl.validate( (valid, fields) => {
            if (valid) {
                const {name,permissions_id}=form
                updateUser({
                    name,permissions_id
                }).then(res=>{
                    if(res.data.code===10000){
                        getList()
                        beforeClose()
                    }
                })
            } else {
                console.log('error submit!', fields)
            }
        })
    }

</script>

<style scoped>
    .time-into {
        display: flex;
        align-items: center;
        gap: 5px;
    }

    .pagination-into {
        width: 100%;
        background-color: white;
        display: flex;
        justify-content: flex-end;
    }

</style>