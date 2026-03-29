const path = require('path');

module.exports = {
  devServer: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  },
  css: {
    loaderOptions: {
      less: {
        lessOptions: {
          javascriptEnabled: true,
          modifyVars: {
            'primary-color': '#1890ff',
            'link-color': '#1890ff',
            'border-radius-base': '4px'
          }
        }
      }
    }
  },
  configureWebpack: {
    resolve: {
      alias: {
        '@': require('path').join(__dirname, 'src/imid_user')
      }
    }
  }
} 