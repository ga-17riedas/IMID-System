<template>
  <div class="doctors-management">
    <!-- 搜索和过滤区域 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.search"
        placeholder="搜索医生姓名/工号"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select 
        v-model="listQuery.department" 
        placeholder="科室" 
        clearable 
        class="filter-item"
        style="width: 120px"
      >
        <el-option 
          v-for="dept in departments"
          :key="dept"
          :label="dept"
          :value="dept"
        />
      </el-select>
      <el-button 
        type="primary" 
        class="filter-item" 
        @click="handleFilter"
      >
        搜索
      </el-button>
    </div>

    <!-- 医生列表 -->
    <el-table
      v-loading="loading"
      :data="list"
      border
      style="width: 100%"
    >
      <el-table-column
        prop="username"
        label="用户名"
        width="120"
      />
      <el-table-column
        prop="fullName"
        label="姓名"
        width="120"
      />
      <el-table-column
        prop="department"
        label="科室"
        width="120"
      />
      <el-table-column
        prop="professionalTitle"
        label="职称"
        width="120"
      />
      <el-table-column
        prop="hospital"
        label="医院"
      />
      <el-table-column
        prop="licenseNumber"
        label="执业证号"
        width="150"
      />
      <el-table-column
        label="操作"
        width="250"
        fixed="right"
      >
        <template slot-scope="{row}">
          <el-button 
            size="mini"
            type="primary"
            @click="handleUpdate(row)"
          >
            编辑
          </el-button>
          <el-button
            size="mini"
            type="warning"
            @click="handleResetPassword(row)"
          >
            重置密码
          </el-button>
          <el-button
            size="mini"
            type="danger"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        :current-page.sync="listQuery.page"
        :page-sizes="[10, 20, 30, 50]"
        :page-size.sync="listQuery.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog
      :title="dialogType === 'create' ? '添加医生' : '编辑医生'"
      :visible.sync="dialogVisible"
    >
      <el-form
        ref="form"
        :model="form"
        :rules="rules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username" v-if="dialogType === 'create'">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'create'">
          <el-input v-model="form.password" type="password" />
        </el-form-item>
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="form.fullName" />
        </el-form-item>
        <el-form-item label="科室" prop="department">
          <el-select v-model="form.department" placeholder="请选择科室">
            <el-option 
              v-for="dept in departments"
              :key="dept"
              :label="dept"
              :value="dept"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="职称" prop="professionalTitle">
          <el-input v-model="form.professionalTitle" />
        </el-form-item>
        <el-form-item label="医院" prop="hospital">
          <el-input v-model="form.hospital" />
        </el-form-item>
        <el-form-item label="执业证号" prop="licenseNumber">
          <el-input v-model="form.licenseNumber" />
        </el-form-item>
        <el-form-item label="专长" prop="specialty">
          <el-input v-model="form.specialty" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>

    <!-- 添加密码重置对话框 -->
    <el-dialog
      title="重置密码"
      :visible.sync="resetPasswordDialogVisible"
      width="30%">
      <el-form :model="resetPasswordForm" :rules="resetPasswordRules" ref="resetPasswordForm">
        <el-form-item label="新密码" prop="password">
          <el-input 
            v-model="resetPasswordForm.password" 
            type="password"
            show-password
            placeholder="请输入新密码">
          </el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="resetPasswordForm.confirmPassword" 
            type="password"
            show-password
            placeholder="请再次输入密码">
          </el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="resetPasswordDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmResetPassword">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'DoctorsManagement',
  
  data() {
    // 密码验证规则
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else if (value.length < 6) {
        callback(new Error('密码长度不能小于6位'))
      } else {
        if (this.resetPasswordForm.confirmPassword !== '') {
          this.$refs.resetPasswordForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.resetPasswordForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      list: [],
      total: 0,
      loading: false,
      dialogVisible: false,
      dialogType: 'create',
      departments: ['内科', '外科', '儿科', '妇产科', '眼科', '骨科', '神经科', '心胸外科'],
      
      listQuery: {
        page: 1,
        size: 10,
        search: '',
        department: ''
      },
      
      form: {
        username: '',
        password: '',
        fullName: '',
        department: '',
        professionalTitle: '',
        hospital: '',
        licenseNumber: '',
        specialty: ''
      },
      
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        department: [
          { required: true, message: '请选择科室', trigger: 'change' }
        ],
        licenseNumber: [
          { required: true, message: '请输入执业证号', trigger: 'blur' }
        ]
      },
      resetPasswordDialogVisible: false,
      resetPasswordForm: {
        id: null,
        password: '',
        confirmPassword: ''
      },
      resetPasswordRules: {
        password: [
          { validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { validator: validatePass2, trigger: 'blur' }
        ]
      }
    }
  },

  created() {
    this.getList()
  },

  methods: {
    getList() {
      console.log('Fetching doctors list with query:', {
        page: this.listQuery.page - 1, // Spring Data JPA 分页从0开始
        size: this.listQuery.size,
        search: this.listQuery.search,
        department: this.listQuery.department
      })
      
      this.loading = true
      this.$http.get('/api/admin/doctors', { 
        params: {
          page: this.listQuery.page - 1, // 转换页码
          size: this.listQuery.size,
          search: this.listQuery.search || undefined, // 如果为空则不传
          department: this.listQuery.department || undefined
        }
      })
      .then(response => {
        console.log('Doctors response:', response.data)
        this.list = response.data.content
        this.total = response.data.totalElements
      })
      .catch(error => {
        console.error('Error fetching doctors:', error)
        this.$message.error('获取医生列表失败')
      })
      .finally(() => {
        this.loading = false
      })
    },

    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },

    handleSizeChange(val) {
      this.listQuery.size = val
      this.getList()
    },

    handleCurrentChange(val) {
      this.listQuery.page = val
      this.getList()
    },

    handleCreate() {
      this.dialogType = 'create'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.form.resetFields()
      })
    },

    handleUpdate(row) {
      this.dialogType = 'update'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.form = Object.assign({}, row)
      })
    },

    handleDelete(row) {
      this.$confirm('确认删除该医生?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/admin/doctors/${row.id}`)
          .then(() => {
            this.$message.success('删除成功')
            this.getList()
          })
          .catch(error => {
            console.error('删除失败:', error)
            this.$message.error('删除失败')
          })
      })
    },

    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          const action = this.dialogType === 'create' ? 
            this.$http.post('/api/admin/doctors', this.form) :
            this.$http.put(`/api/admin/doctors/${this.form.id}`, this.form)

          action.then(() => {
            this.$message.success(`${this.dialogType === 'create' ? '添加' : '编辑'}成功`)
            this.dialogVisible = false
            this.getList()
          }).catch(error => {
            console.error(`${this.dialogType === 'create' ? '添加' : '编辑'}失败:`, error)
            this.$message.error(`${this.dialogType === 'create' ? '添加' : '编辑'}失败`)
          })
        }
      })
    },

    handleResetPassword(row) {
      this.resetPasswordForm.id = row.id
      this.resetPasswordForm.password = ''
      this.resetPasswordForm.confirmPassword = ''
      this.resetPasswordDialogVisible = true
    },

    confirmResetPassword() {
      this.$refs.resetPasswordForm.validate(async (valid) => {
        if (valid) {
          try {
            await this.$http.post(`/api/admin/doctors/${this.resetPasswordForm.id}/reset-password`, {
              password: this.resetPasswordForm.password
            })
            this.$message.success('密码重置成功')
            this.resetPasswordDialogVisible = false
          } catch (error) {
            this.$message.error('密码重置失败')
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.doctors-management {
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

  .el-button {
    margin-right: 8px;
    &:last-child {
      margin-right: 0;
    }
  }
}
</style> 