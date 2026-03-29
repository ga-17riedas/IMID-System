<template>
  <div class="patients-management">
    <!-- 搜索和过滤区域 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.search"
        placeholder="搜索患者姓名/ID"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select 
        v-model="listQuery.gender" 
        placeholder="性别" 
        clearable 
        class="filter-item"
        style="width: 120px"
      >
        <el-option label="男" value="MALE" />
        <el-option label="女" value="FEMALE" />
      </el-select>
      <el-button 
        type="primary" 
        class="filter-item" 
        @click="handleFilter"
      >
        搜索
      </el-button>
    </div>

    <!-- 患者列表 -->
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
        prop="gender"
        label="性别"
        width="80"
      >
        <template slot-scope="{row}">
          {{ row.gender === 'MALE' ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column
        prop="age"
        label="年龄"
        width="80"
      />
      <el-table-column
        prop="phone"
        label="联系电话"
        width="120"
      />
      <el-table-column
        prop="email"
        label="邮箱"
        width="180"
      />
      <el-table-column
        prop="emergencyContact"
        label="紧急联系人"
        width="120"
      />
      <el-table-column
        prop="emergencyPhone"
        label="紧急联系电话"
        width="120"
      />
      <el-table-column
        fixed="right"
        label="操作"
        width="250"
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
            type="danger"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
          <el-button 
            size="mini" 
            type="warning" 
            @click="handleResetPassword(row)">
            重置密码
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
      :title="dialogType === 'create' ? '添加患者' : '编辑患者'"
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
        <el-form-item label="姓名" prop="fullName">
          <el-input v-model="form.fullName" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="form.age" :min="0" :max="150" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact" />
        </el-form-item>
        <el-form-item label="紧急电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone" />
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
  name: 'PatientsManagement',
  
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
      listQuery: {
        page: 1,
        size: 10,
        search: '',
        gender: ''
      },
      dialogVisible: false,
      dialogType: 'create',
      form: {
        username: '',
        fullName: '',
        gender: '',
        age: 0,
        phone: '',
        email: '',
        emergencyContact: '',
        emergencyPhone: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        fullName: [
          { required: true, message: '请输入姓名', trigger: 'blur' }
        ],
        gender: [
          { required: true, message: '请选择性别', trigger: 'change' }
        ],
        age: [
          { required: true, message: '请输入年龄', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
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
      console.log('Fetching patients list with query:', {
        page: this.listQuery.page - 1,
        size: this.listQuery.size,
        search: this.listQuery.search,
        gender: this.listQuery.gender
      })
      
      this.loading = true
      this.$http.get('/api/admin/patients', { 
        params: {
          page: this.listQuery.page - 1,
          size: this.listQuery.size,
          search: this.listQuery.search || undefined,
          gender: this.listQuery.gender || undefined
        }
      })
      .then(response => {
        console.log('Patients response:', response.data)
        this.list = response.data.content
        this.total = response.data.totalElements
      })
      .catch(error => {
        console.error('Error fetching patients:', error)
        if (error.response && error.response.data) {
          if (error.response.data.message && error.response.data.message.includes('gender')) {
            this.$message.error('性别参数格式错误，请检查并重试')
          } else {
            this.$message.error(error.response.data.message || '获取患者列表失败')
          }
        } else {
          this.$message.error('获取患者列表失败')
        }
        
        if (this.listQuery.gender) {
          this.listQuery.gender = ''
          this.$http.get('/api/admin/patients', { 
            params: {
              page: this.listQuery.page - 1,
              size: this.listQuery.size,
              search: this.listQuery.search || undefined
            }
          })
          .then(response => {
            console.log('Retry patients response:', response.data)
            this.list = response.data.content
            this.total = response.data.totalElements
          })
          .catch(() => {
            this.list = []
            this.total = 0
          })
        } else {
          this.list = []
          this.total = 0
        }
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
      this.$confirm('确认删除该患者?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http.delete(`/api/admin/patients/${row.id}`)
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
          console.log('Submitting form:', this.form);
          console.log('Dialog type:', this.dialogType);
          
          const action = this.dialogType === 'create' ? 
            this.$http.post('/api/admin/patients', this.form) :
            this.$http.put(`/api/admin/patients/${this.form.id}`, {
              fullName: this.form.fullName,
              gender: this.form.gender,
              age: this.form.age,
              phone: this.form.phone,
              email: this.form.email,
              emergencyContact: this.form.emergencyContact,
              emergencyPhone: this.form.emergencyPhone
            });

          action.then(response => {
            console.log('Submit response:', response.data);
            this.$message.success(`${this.dialogType === 'create' ? '添加' : '编辑'}成功`);
            this.dialogVisible = false;
            this.getList();
          }).catch(error => {
            console.error(`${this.dialogType === 'create' ? '添加' : '编辑'}失败:`, error);
            if (error.response) {
              console.error('Error response:', error.response.data);
              this.$message.error(error.response.data.message || `${this.dialogType === 'create' ? '添加' : '编辑'}失败`);
            } else {
              this.$message.error(`${this.dialogType === 'create' ? '添加' : '编辑'}失败`);
            }
          });
        }
      });
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
            await this.$http.post(`/api/admin/patients/${this.resetPasswordForm.id}/reset-password`, {
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
.patients-management {
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