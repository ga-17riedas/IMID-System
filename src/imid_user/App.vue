<template>
  <!-- 应用根容器 -->
  <div id="app">
    <!-- 路由视图，根据当前路由显示对应组件 -->
    <router-view></router-view>
  </div>
</template>

<script>
/**
 * 应用根组件
 * 作为整个应用的容器，包含路由视图
 */
export default {
  name: 'App'
};
</script>

<style>
/**
 * 全局样式
 * 设置基础样式和全局规则
 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
}

#app {
  font-family: Arial, sans-serif;
  -webkit-font-smoothing: antialiased;  /* 字体平滑渲染 */
  -moz-osx-font-smoothing: grayscale;   /* Firefox字体渲染 */
  height: 100%;
}
</style> 