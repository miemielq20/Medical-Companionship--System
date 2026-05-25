<template>
    <panel-head :route="route" />

    <div class="bts-into">
        <el-button type="primary" @click="open()" :icon="Plus">添加</el-button>

        <el-popover :visible="visible" placement="bottom" :width="80">
            <p>
                <el-icon color="blue"><InfoFilled /></el-icon>
                确定要删除选中的陪护师吗？
            </p>
            <div style="text-align: right; margin: 0">
                <el-button size="small" text @click="visible = false">取消</el-button>
                <el-button size="small" type="primary" @click="deleteItem">确定</el-button>
            </div>
            <template #reference>
                <el-button @click="visible = true" :icon="Delete" type="danger" :disabled="!selectData.length">
                    批量删除
                </el-button>
            </template>
        </el-popover>
    </div>

    <ProTable
        :data="tableData.list"
        :loading="tableLoading"
        :total="tableData.total"
        :page-num="paginnationData.pageNum"
        :page-size="paginnationData.pageSize"
        @page-change="onPageChange"
        @selection-change="handleSelectionChange"
    >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="昵称" min-width="120" show-overflow-tooltip />
        <el-table-column label="头像" width="80">
            <template #default="scope">
                <el-image :src="scope.row.avatar" style="width: 50px; height: 50px; border-radius: 4px;" />
            </template>
        </el-table-column>
        <el-table-column prop="sex" label="性别" width="70">
            <template #default="scope">
                {{ scope.row.sex === '1' ? '男' : '女' }}
            </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号" min-width="130" />
        <el-table-column prop="active" label="状态" width="80">
            <template #default="scope">
                <el-tag :type="scope.row.active ? 'success' : 'danger'" size="small">
                    {{ scope.row.active ? '正常' : '禁用' }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="create_time" label="创建时间" min-width="120" />
        <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
                <el-button type="primary" size="small" @click="open(scope.row)">编辑</el-button>
            </template>
        </el-table-column>
    </ProTable>

    <FormDialog
        v-model:visible="dialogTableVisible"
        title="陪护师信息"
        :form-data="form"
        :rules="rulers"
        width="460px"
        label-width="80px"
        :confirm-loading="submitLoading"
        @confirm="onFormConfirm"
        @cancel="onFormCancel"
    >
        <template #default="{ form: f }">
            <el-form-item label="昵称" prop="name">
                <el-input v-model="f.name" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item label="头像" prop="avatar">
                <template v-if="!f.avatar">
                    <el-button type="primary" @click="openAvatarDialog">点击上传</el-button>
                </template>
                <div v-else class="avatar-preview">
                    <el-image :src="f.avatar" style="width: 80px; height: 80px; border-radius: 4px; cursor: pointer;" @dblclick="openAvatarDialog" />
                </div>
            </el-form-item>
            <el-form-item label="性别" prop="sex">
                <el-select v-model="f.sex" placeholder="请选择">
                    <el-option label="男" value="1" />
                    <el-option label="女" value="2" />
                </el-select>
            </el-form-item>
            <el-form-item label="年龄" prop="age">
                <el-input-number v-model="f.age" :min="18" :max="60" />
            </el-form-item>
            <el-form-item label="手机号" prop="mobile">
                <el-input v-model="f.mobile" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="状态" prop="active">
                <el-radio-group v-model="f.active">
                    <el-radio :value="1">启用</el-radio>
                    <el-radio :value="0">禁用</el-radio>
                </el-radio-group>
            </el-form-item>
        </template>
    </FormDialog>

    <el-dialog v-model="dialogAvatarVisible" :before-close="closeAvatar" width="655px" title="选择头像">
        <div class="img-list">
            <div v-for="(item, index) in photoListData" :key="index" class="img-item" @click="selectIndex = index">
                <el-image style="width: 148px; height: 148px" :src="item.url" />
                <div v-if="selectIndex === index">
                    <el-icon color="#fff" size="30"><Check /></el-icon>
                </div>
            </div>
        </div>
        <template #footer>
            <el-button type="primary" @click="avatorConfirm">确定</el-button>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { photoList, companion, companionList, deleteCompanion } from '@/api/index'
import { onMounted } from 'vue'
import { InfoFilled, Plus, Delete, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { PhotoList } from '@/types/companion'
import type { CompanionRequest, DeleteCompanionRequest } from '@/types/api.request'
import PanelHead from '@/components/panel/PanelHead.vue'
import ProTable from '@/components/ProTable/index.vue'
import FormDialog from '@/components/FormDialog/index.vue'
import dayjs from 'dayjs'

const route = useRoute()

const dialogTableVisible = ref(false)
const dialogAvatarVisible = ref(false)
const visible = ref(false)
const submitLoading = ref(false)

const photoListData = ref<PhotoList[]>([])
const selectIndex = ref(0)

const tableLoading = ref(false)
const tableData = reactive({
    list: <CompanionRequest[]>([]),
    total: 0
})

const paginnationData = reactive({ pageNum: 1, pageSize: 10 })
const selectData = ref<CompanionRequest[]>([])
const deleteList = ref<DeleteCompanionRequest>({ id: [] })

const form = reactive<CompanionRequest>({
    id: 0, name: '', mobile: '', age: 0, sex: '', avatar: '', active: 1
})

const phoneReg = /^1[3-9]\d{9}$/
const validatePhone = (_rule: any, value: string, callback: any) => {
    if (!phoneReg.test(value)) {
        callback(new Error('请输入正确的手机号码'))
    } else {
        callback()
    }
}

const rulers = {
    name: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
    avatar: [{ required: true, message: '请上传头像' }],
    sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
    mobile: [{ required: true, validator: validatePhone, trigger: 'blur' }],
}

onMounted(() => {
    getList()
    photoList().then((res) => { photoListData.value = res.data.data })
})

const getList = () => {
    tableLoading.value = true
    companionList(paginnationData).then(res => {
        const { list, total } = res.data.data as { list: CompanionRequest[], total: number }
        list.forEach(item => {
            item.create_time = dayjs(item.create_time).format('YYYY-MM-DD')
        })
        tableData.list = list
        tableData.total = total
    }).finally(() => { tableLoading.value = false })
}

const onPageChange = (pageNum: number, pageSize: number) => {
    paginnationData.pageNum = pageNum
    paginnationData.pageSize = pageSize
    getList()
}

const handleSelectionChange = (val: CompanionRequest[]) => {
    selectData.value = val
    deleteList.value.id = val.map(item => ({ id: item.id }))
}

const deleteItem = () => {
    if (!selectData.value.length) {
        ElMessage.warning('请选择至少一项要删除的数据')
        return
    }
    deleteCompanion(deleteList.value).then((res) => {
        if (res.data.code === 10000) {
            getList()
            visible.value = false
            ElMessage.success('删除成功')
        } else {
            ElMessage.error(res.data.msg)
        }
    })
}

const open = (rowData?: CompanionRequest) => {
    if (rowData) {
        Object.assign(form, rowData)
    } else {
        Object.assign(form, { id: 0, name: '', mobile: '', age: 0, sex: '', avatar: '', active: 1 })
    }
    dialogTableVisible.value = true
}

const onFormConfirm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    submitLoading.value = true
    try {
        const res = await companion(form)
        if (res.data.code === 10000) {
            getList()
            ElMessage.success(form.id > 0 ? '更新成功' : '创建成功')
            dialogTableVisible.value = false
        } else {
            ElMessage.error(res.data.msg)
        }
    } finally {
        submitLoading.value = false
    }
}

const onFormCancel = () => {
    dialogTableVisible.value = false
}

const openAvatarDialog = () => {
    dialogAvatarVisible.value = true
}

const closeAvatar = () => {
    dialogAvatarVisible.value = false
    selectIndex.value = 0
}

const avatorConfirm = () => {
    const selectedPhoto = photoListData.value[selectIndex.value]
    form.avatar = selectedPhoto?.url ?? ''
    closeAvatar()
}
</script>

<style lang="less">
.bts-into {
    margin-bottom: 12px;
    display: flex;
    gap: 8px;
}
.avatar-preview {
    display: flex;
    align-items: center;
    gap: 12px;
}
.img-list {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 20px;
}
.img-item {
    position: relative;
    width: 148px;
    height: 148px;
    cursor: pointer;
}
.img-item > div {
    position: absolute;
    top: 5px;
    right: 5px;
}
</style>