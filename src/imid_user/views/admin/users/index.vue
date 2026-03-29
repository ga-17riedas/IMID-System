<template>
  <div class="user-management">
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <span>用户管理</span>
      </div>
      
      <el-tabs v-model="activeTab" type="border-card" @tab-click="handleTabClick">
        <el-tab-pane label="医生管理" name="doctors">
          <router-view v-if="activeTab === 'doctors'"></router-view>
        </el-tab-pane>
        <el-tab-pane label="患者管理" name="patients">
          <router-view v-if="activeTab === 'patients'"></router-view>
        </el-tab-pane>
        <el-tab-pane label="管理员管理" name="admins">
          <router-view v-if="activeTab === 'admins'"></router-view>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'UserManagement',
  
  data() {
    return {
      activeTab: 'doctors'
    }
  },

  watch: {
    // 监听路由变化
    '$route': {
      immediate: true,
      handler(to) {
        // 根据路由路径设置当前激活的标签
        const path = to.path
        if (path.includes('/doctors')) {
          this.activeTab = 'doctors'
        } else if (path.includes('/patients')) {
          this.activeTab = 'patients'
        } else if (path.includes('/admins')) {
          this.activeTab = 'admins'
        }
      }
    }
  },

  methods: {
    handleTabClick(tab) {
      // 根据选中的标签切换路由
      const name = tab.name
      this.$router.push(`/admin/users/${name}`)
    }
  }
}
</script>

<style lang="scss" scoped>
.user-management {
  padding: 20px;
  
  .box-card {
    margin-bottom: 20px;
  }
}
</style> 