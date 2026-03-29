<template>
  <div class="admins-management">
    <!-- 搜索和过滤区域 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.search"
        placeholder="搜索管理员用户名/邮箱"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select 
        v-model="listQuery.status" 
        placeholder="状态" 
        clearable 
        class="filter-item"
        @change="handleFilter"
      >
        <el-option label="正常" value="ACTIVE" />
        <el-option label="禁用" value="DISABLED" />
      </el-select>
    </div>

    <!-- 管理员列表 -->
    <el-table
      v-loading="loading"
      :data="adminList"
      border
      style="width: 100%"
    >
      <el-table-column label="用户名" prop="username" />
      <el-table-column label="邮箱" prop="email" />
      <el-table-column label="状态">
        <template slot-scope="{row}">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
            {{ row.status === 'ACTIVE' ? '正常' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createdAt">
        <template slot-scope="{row}">
          {{ formatDateTime(row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="最后登录时间" prop="lastLogin">
        <template slot-scope="{row}">
          {{ formatDateTime(row.lastLogin) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="{row}">
          <el-button 
            size="mini" 
            :type="row.status === 'ACTIVE' ? 'danger' : 'success'"
            @click="handleStatusChange(row)"
          >
            {{ row.status === 'ACTIVE' ? '禁用' : '启用' }}
          </el-button>
          <el-button 
            size="mini"
            type="warning"
            @click="handleResetPassword(row)"
          >
            重置密码
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 新增管理员对话框 -->
    <el-dialog :visible.sync="dialogVisible" title="新增管理员">
      <el-form 
        ref="adminForm" 
        :model="adminForm" 
        :rules="rules" 
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input v-model="adminForm.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="adminForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="adminForm.password" type="password" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAdmins, createAdmin, updateAdminStatus, resetAdminPassword } from '@/api/admin'
import Pagination from '@/components/Pagination'
import { formatDateTime } from '@/utils/format'

export default {
  name: 'AdminsManagement',
  components: { Pagination },
  data() {
    return {
      loading: false,
      adminList: [],
      total: 0,
      listQuery: {
        page: 1,
        limit: 10,
        search: '',
        status: ''
      },
      dialogVisible: false,
      adminForm: {
        username: '',
        email: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    formatDateTime,
    async getList() {
      try {
        this.loading = true
        const { data } = await getAdmins({
          page: this.listQuery.page - 1,
          size: this.listQuery.limit,
          search: this.listQuery.search,
          status: this.listQuery.status
        })
        this.adminList = data.content
        this.total = data.totalElements
      } catch (error) {
        console.error('获取管理员列表失败:', error)
        this.$message.error('获取管理员列表失败')
      } finally {
        this.loading = false
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleCreate() {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.adminForm.resetFields()
      })
    },
    async submitForm() {
      try {
        await this.$refs.adminForm.validate()
        await createAdmin(this.adminForm)
        this.$message.success('创建管理员成功')
        this.dialogVisible = false
        this.getList()
      } catch (error) {
        console.error('创建管理员失败:', error)
        this.$message.error('创建管理员失败')
      }
    },
    async handleStatusChange(row) {
      try {
        const newStatus = row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
        await updateAdminStatus(row.id, { status: newStatus })
        this.$message.success('更新状态成功')
        this.getList()
      } catch (error) {
        console.error('更新状态失败:', error)
        this.$message.error('更新状态失败')
      }
    },
    async handleResetPassword(row) {
      try {
        const { data } = await resetAdminPassword(row.id)
        this.$message.success(`密码重置成功,新密码: ${data.password}`)
      } catch (error) {
        console.error('重置密码失败:', error)
        this.$message.error('重置密码失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.admins-management {
  padding: 20px;

  .filter-container {
    padding-bottom: 20px;
    .filter-item {
      margin-right: 10px;
    }
  }

  .pagination-container {
    padding: 20px 0;
  }
}
</style> 