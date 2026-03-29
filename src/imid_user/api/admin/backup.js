import request from '@/utils/request'

// 获取备份列表
export function getBackupList(params) {
  return request({
    url: '/api/admin/backups',
    method: 'get',
    params
  })
}

// 创建新备份
export function createBackup(data) {
  return request({
    url: '/api/admin/backups',
    method: 'post',
    data
  })
}

// 获取备份详情
export function getBackupDetail(backupId) {
  return request({
    url: `/api/admin/backups/${backupId}`,
    method: 'get'
  })
}

// 删除备份
export function deleteBackup(backupId) {
  return request({
    url: `/api/admin/backups/${backupId}`,
    method: 'delete'
  })
}

// 恢复备份
export function restoreBackup(backupId) {
  return request({
    url: `/api/admin/backups/${backupId}/restore`,
    method: 'post'
  })
}

// 验证备份
export function verifyBackup(backupId) {
  return request({
    url: `/api/admin/backups/${backupId}/verify`,
    method: 'post'
  })
}

// 获取备份任务列表
export function getBackupTasks() {
  return request({
    url: '/api/admin/backups/tasks',
    method: 'get'
  })
}

// 创建定时备份任务
export function scheduleBackup(data) {
  return request({
    url: '/api/admin/backups/schedule',
    method: 'post',
    data
  })
}

// 取消定时备份任务
export function cancelBackupTask(taskId) {
  return request({
    url: `/api/admin/backups/tasks/${taskId}`,
    method: 'delete'
  })
}

// 获取备份统计信息
export function getBackupStats() {
  return request({
    url: '/api/admin/backups/stats',
    method: 'get'
  })
}