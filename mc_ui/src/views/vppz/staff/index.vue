<template>
    <panel-head :route="route" />
    <div class="bts-into">
        <el-button type="primary" @click="open" :icon="Plus">添加 </el-button>

        <el-popover :visible="visible" placement="bottom" :width="80" :icon="InfoFilled">
            <p>
                <el-icon color="blue">
                    <InfoFilled />
                </el-icon>
                你是否要删除
            </p>
            <div style="text-align: right; margin: 0">
                <el-button size="small" text @click="visible = false">否</el-button>
                <el-button size="small" type="primary" @click="deleteItem">
                    是
                </el-button>
            </div>
            <template #reference>
                <el-button @click="visible = true" :icon="Delete" type="danger">Delete</el-button>
            </template>
        </el-popover>
    </div>
    <el-table :data="tableData.list" style="width: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" :selectable="selectable" width="55" />
        <el-table-column prop="id" label="id" />
        <el-table-column prop="name" label="昵称" />
        <el-table-column label="头像" width="180" prop="avatar">
            <template #default="scope">
                <el-image :src="scope.row.avatar" style="width: 50px; height: 50px;" />
            </template>
        </el-table-column>
        <el-table-column prop="sex" label="性别">
            <template #default="scope">
                {{ scope.row.sex === '1' ? '男' : '女' }}
            </template>
        </el-table-column>
        <el-table-column prop="mobile" label="手机号" />
        <el-table-column prop="active" label="状态">
            <template #default="scope">
                <el-tag :type="scope.row.active ? 'success' : 'danger'">{{ scope.row.active ? '正常' : '异常' }}</el-tag>
            </template>
        </el-table-column>
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
        <h3>陪护师</h3>
        <el-form ref="formRef" :model="form" :rules="rulers">
            <el-form-item v-show="false" prop="id" />
            <el-form-item label="昵称" prop="name">
                <el-input v-model="form.name" placeholder="请输入昵称" />
            </el-form-item>
            <el-form-item prop="avatar" label="头像">
                <el-button v-if="!form.avatar" type="primary" @click="dialogAvatarVisible = true">点击上传</el-button>
                <el-image v-else :src="form.avatar" style="width: 100px; height: 100px;" />
            </el-form-item>
            <el-form-item label="性别" prop="sex">
                <el-select v-model="form.sex" placeholder="请选择性别">
                    <el-option label="男" value="1" />
                    <el-option label="女" value="2" />
                </el-select>
            </el-form-item>
            <el-form-item label="年龄" prop="age">
                <el-input-number v-model="form.age" :min="18" :max="40" />
            </el-form-item>
            <el-form-item label="手机号" prop="mobile">
                <el-input v-model="form.mobile" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="是否生效" prop="active">
                <el-radio-group v-model="form.active">
                    <el-radio :value="0" size="large">失效</el-radio>
                    <el-radio :value="1" size="large">生效</el-radio>
                </el-radio-group>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button type="primary" @click="confirm(formRef)">确定</el-button>
        </template>
    </el-dialog>
    <el-dialog v-model="dialogAvatarVisible" :before-close="close" width="655px">
        <h3>新增陪护师照片</h3>
        <div class="img-list">
            <div v-for="(item, index) in photoListData" :key="index" class="img-item" @click="selectIndex = index">
                <el-image style="width: 148px; height: 148px" :src="item.url" />
                <div v-if="selectIndex === index">
                    <el-icon>
                        <Check />
                    </el-icon>
                </div>
            </div>
        </div>
        <template #footer>
            <el-button type="primary" @click="avatorConfirm()">确定</el-button>
        </template>
    </el-dialog>
</template>

<style lang="less" scoped>
    .time-into {
        display: flex;
        align-items: center;
        gap: 5px;

        .el-icon {
            margin-top: 2px;
        }
    }

    .pagination-into {
        width: 100%;
        background-color: white;
        display: flex;
        justify-content: flex-end;
    }

    .img-list {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-top: 20px;

        .img-item {
            position: relative;

            .el-icon {
                width: 30px;
                height: 30px;
                background: rgb(61, 203, 36);
                color: rgb(255, 255, 255);
                position: absolute;
                top: 0px;
                left: 0px;
            }
        }

    }
</style>


<script lang="ts" setup>
    import { reactive, ref, nextTick } from 'vue'
    import { useRoute } from 'vue-router'
    import { photoList, companion, companionList, deleteCompanion } from '@/api/index'
    import { onMounted } from 'vue'
    import { InfoFilled, Plus, Delete } from '@element-plus/icons-vue'

    import { type FormInstance } from 'element-plus'
    import { type PhotoList } from '@/types/companion'
    import { type CompanionRequest,type DeleteCompanionRequest } from '@/types/api'

    import PanelHead from '@/components/panel/PanelHead.vue'
    import dayjs from 'dayjs'

    const route = useRoute()
    // 弹窗
    const dialogTableVisible = ref(false)
    // 头像
    const dialogAvatarVisible = ref(false)
    // 头像列表
    const photoListData = ref(<PhotoList[]>([]))
    // 选择的图片索引
    const selectIndex = ref(0)

    const selectData = ref(<CompanionRequest[]>([]))
    const deleteList = ref<DeleteCompanionRequest>({ id: [] })

    const visible = ref(false)

    onMounted(() => {
        // 获取头像列表
        getList()
        photoList().then((res) => {
            photoListData.value = res.data.data
        })
    })

    //分页数据
    const paginnationData = {
        pageNum: 1,
        pageSize: 10
    }


    //陪护师列表数据
    const tableData = reactive({
        list: <CompanionRequest[]>([]),
        total: 0
    })

    const selectable = (row: CompanionRequest) => tableData.list.includes(row)
    //请求列表数据
    const getList = () => {
        companionList(paginnationData).then(res => {
            // 处理数据类型
            const { list, total } = res.data.data as { list: CompanionRequest[], total: number }
            list.forEach(item => {
                item.create_time = dayjs(item.create_time).format('YYYY-MM-DD')
                const data = JSON.parse(localStorage.userInfo);
                data.name = item.name;
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

    //选中数据
    const handleSelectionChange = (val: CompanionRequest[]) => {
        selectData.value = val
        deleteList.value.id = val.map(item => ({ id: item.id }))
    }

    // 删除数据
    const deleteItem = () => {
        if (!selectData.value.length) {
            ElMessage.warning('请选择至少一项要删除的数据')
        } else {
            deleteCompanion(deleteList.value).then((res) => {
                if (res.data.code === 10000) {
                    getList()
                    visible.value = false
                    ElMessage.success('删除成功')
                } else {
                    ElMessage.error(res.data.msg)
                }
})        }
    }

    //创建陪护师数据
    const form = reactive<CompanionRequest>({
        id: 0,
        name: '',
        mobile: '',
        age: 0,
        sex: '',
        avatar: '',
        active: 1
    })

    //手机号校验
    const phoneReg = /^1[3-9]\d{9}$/;
    const validatePhone = (rule: any, value: string, callback: any) => {
        if (!phoneReg.test(value)) {
            callback(new Error('请输入正确的手机号码'))
        } else {
            callback()
        }
    }
    //新增陪护师校验规则
    const rulers = {
        name: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        avatar: [{ required: true, message: '请上传头像', },],
        sex: { required: true, message: '请选择性别' },
        mobile: [{ required: true, validator: validatePhone, trigger: 'blur' }],
    }

    // 关闭弹窗
    const beforeClose = () => {
        dialogTableVisible.value = false
        formRef.value.resetFields()
    }
    // 关闭头像弹窗
    const close = () => {
        dialogAvatarVisible.value = false
        selectIndex.value = 0
    }
    //打开创建陪护师窗口
    const open = (rowData?: { name: string; id: number; mobile: number, age: number; sex: string, avatar: string, active: number }) => {
        dialogTableVisible.value = true
        nextTick(() => {
            if (rowData) {
                Object.assign(form, rowData)
            }
        })
    }

    // 陪护师创建窗口数据结构
    const formRef = ref()
    //权限提交
    const confirm = async (formEl: FormInstance | undefined) => {
        if (!formEl) return
        // 校验
        await formEl.validate(async (valid, fields) => {
            if (valid) {
                companion(form).then((res) => {
                    if (res.data.code === 10000) {
                        getList()
                        ElMessage.success('创建成功')
                    } else {
                        ElMessage.error(res.data.msg)
                    }
                    beforeClose()
                })
            } else {
                console.log('error submit!', fields)
            }
        })
    }

    // 添加头像
    const avatorConfirm = () => {
        const selectedPhoto = photoListData.value[selectIndex.value];
        form.avatar = selectedPhoto?.url ?? '';
        close()
    }



</script>