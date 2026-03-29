<template>
  <div class="error-page">
    <h1>403</h1>
    <h2>抱歉，您没有访问权限</h2>
    <el-button type="primary" @click="goBack">返回</el-button>
  </div>
</template>

<script>
export default {
  name: '403',
  methods: {
    goBack() {
      this.$router.go(-1)
    }
  }
}
</script>

<style scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style> 