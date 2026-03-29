<template>
  <div class="image-management">
    <!-- 搜索和过滤区域 -->
    <div class="filter-container">
      <el-input
        v-model="listQuery.search"
        placeholder="搜索患者姓名"
        style="width: 200px;"
        class="filter-item"
        @keyup.enter.native="handleFilter"
      />
      <el-select
        v-model="listQuery.imageType"
        placeholder="影像类型"
        clearable
        class="filter-item"
        @change="handleFilter"
      >
        <el-option label="脑部MRI" value="BRAIN_MRI" />
        <el-option label="胸部CT" value="CHEST_CT" />
      </el-select>
      <el-button
        type="primary"
        icon="el-icon-search"
        class="filter-item"
        @click="handleFilter"
      >
        搜索
      </el-button>
    </div>

    <!-- 数据表格 -->
    <el-table
      v-loading="loading"
      :data="list"
      border
      style="width: 100%"
    >
      <el-table-column label="ID" prop="id" width="80" align="center" />
      <el-table-column label="患者姓名" prop="patientName" width="120" />
      <el-table-column label="影像类型" prop="imageType" width="120">
        <template slot-scope="{row}">
          <el-tag>{{ row.imageType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="AI诊断" prop="aiDiagnosis" />
      <el-table-column label="置信度" prop="confidenceScore" width="100">
        <template slot-scope="{row}">
          {{ row.confidenceScore ? (row.confidenceScore * 100).toFixed(2) + '%' : '-' }}
        </template>
      </el-table-column>
      <el-table-column label="医生诊断" prop="doctorDiagnosis" />
      <el-table-column label="上传时间" prop="createdAt" width="180" />
      <el-table-column label="操作" width="150" align="center">
        <template slot-scope="{row}">
          <el-button
            size="mini"
            type="primary"
            @click="handleView(row)"
          >
            查看
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

    <!-- 分页器 -->
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="listQuery.page"
      :limit.sync="listQuery.limit"
      @pagination="getList"
    />

    <!-- 查看影像对话框 -->
    <el-dialog
      title="影像详情"
      :visible.sync="dialogVisible"
      width="70%"
    >
      <image-viewer
        v-if="dialogVisible"
        :image-info="currentImage"
      />
    </el-dialog>
  </div>
</template>

<script>
import { getModelList, deleteImage } from '@/api/medical'
import Pagination from '@/components/Pagination'
import ImageViewer from './components/ImageViewer'

export default {
  name: 'ImageManagement',
  components: {
    Pagination,
    ImageViewer
  },
  data() {
    return {
      list: [],
      total: 0,
      loading: false,
      dialogVisible: false,
      currentImage: null,
      listQuery: {
        page: 1,
        limit: 10,
        search: '',
        imageType: ''
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      try {
        this.loading = true
        const { data } = await getModelList({
          page: this.listQuery.page - 1,
          size: this.listQuery.limit,
          search: this.listQuery.search,
          imageType: this.listQuery.imageType
        })
        this.list = data.content
        this.total = data.totalElements
      } catch (error) {
        console.error('获取影像列表失败:', error)
        this.$message.error('获取影像列表失败')
      } finally {
        this.loading = false
      }
    },
    handleFilter() {
      this.listQuery.page = 1
      this.getList()
    },
    handleView(row) {
      this.currentImage = row
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该影像记录?', '提示', {
          type: 'warning'
        })
        await deleteImage(row.id)
        this.$message.success('删除成功')
        this.getList()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除影像失败:', error)
          this.$message.error('删除影像失败')
        }
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.image-management {
  padding: 20px;

  .filter-container {
    margin-bottom: 20px;
    .filter-item {
      margin-right: 10px;
      &:last-child {
        margin-right: 0;
      }
    }
  }

  .el-select {
    width: 150px;
  }
}
</style> 