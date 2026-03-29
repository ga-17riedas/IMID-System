<template>
  <div class="reports-container">
    <el-card>
      <div slot="header">
        <span>诊断记录</span>
        <el-input
          v-model="searchQuery"
          placeholder="搜索报告"
          prefix-icon="el-icon-search"
          style="width: 200px; float: right"
          @input="handleSearch"
        ></el-input>
      </div>

      <el-table
        :data="reports"
        style="width: 100%"
        v-loading="loading"
      >
        <el-table-column
          prop="createdAt"
          label="日期"
          width="180"
          :formatter="formatDate"
        ></el-table-column>
        <el-table-column
          prop="doctor.fullName"
          label="医生"
          width="120"
        ></el-table-column>
        <el-table-column
          prop="diagnosis"
          label="诊断结果"
          show-overflow-tooltip
        ></el-table-column>
        <el-table-column
          label="状态"
          width="100"
        >
          <template slot-scope="scope">
            <el-tag :type="scope.row.readStatus ? 'success' : 'warning'" size="small">
              {{ scope.row.readStatus ? '已读' : '未读' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="有效期至"
          width="180"
          :formatter="formatExpireDate"
        ></el-table-column>
        <el-table-column
          label="操作"
          width="120"
        >
          <template slot-scope="scope">
            <el-button
              type="text"
              size="small"
              @click="viewReport(scope.row)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next"
          :total="total"
        >
        </el-pagination>
      </div>
    </el-card>

    <el-dialog
      title="报告详情"
      :visible.sync="dialogVisible"
      width="70%"
    >
      <report-detail 
        v-if="currentReport"
        :report="currentReport"
      />
    </el-dialog>
  </div>
</template>

<script>
import { formatDate } from '@/utils/date'
import ReportDetail from '@/components/ReportDetail'
import { getDiagnosisRecords, getDiagnosisDetail } from '@/api/diagnosis'
import _ from 'lodash'

export default {
  name: 'PatientReports',
  components: {
    ReportDetail
  },
  data() {
    return {
      reports: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      searchQuery: '',
      dialogVisible: false,
      currentReport: null
    }
  },
  created() {
    this.fetchReports()
  },
  methods: {
    formatDate(row, column) {
      const date = row[column.property]
      return date ? formatDate(new Date(date)) : ''
    },
    formatExpireDate(row) {
      return row.expireDate ? formatDate(new Date(row.expireDate)) : '永久有效'
    },
    async fetchReports() {
      this.loading = true
      try {
        const response = await this.$http.get('/api/patient/reports', {
          params: {
            page: this.currentPage - 1,
            size: this.pageSize,
            search: this.searchQuery
          }
        })
        this.reports = response.data.content
        this.total = response.data.totalElements
      } catch (error) {
        console.error('获取报告列表失败:', error)
        this.$message.error('获取报告列表失败')
      } finally {
        this.loading = false
      }
    },
    handlePageChange(page) {
      this.currentPage = page
      this.fetchReports()
    },
    handleSearch: _.debounce(function() {
      this.currentPage = 1
      this.fetchReports()
    }, 300),
    async viewReport(report) {
      try {
        this.dialogVisible = true
        const { data } = await getDiagnosisDetail(report.id)
        console.log('获取到的报告详情:', data)
        this.currentReport = data
      } catch (error) {
        console.error('获取报告详情失败:', error)
        this.$message.error('获取报告详情失败')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.reports-container {
  padding: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style> 