import request from '@/utils/request'

// 获取模型列表
export function getModelList(params) {
  return request({
    url: '/api/models',
    method: 'get',
    params
  })
}

// 获取分页模型列表
export function getPagedModelList(params) {
  return request({
    url: '/api/models/paged',
    method: 'get',
    params
  })
}

// 创建模型
export function createModel(data) {
  const formData = new FormData()
  
  // 直接添加各个字段到FormData
  if (data.name) formData.append('name', data.name)
  if (data.type) formData.append('type', data.type)
  if (data.version) formData.append('version', data.version)
  if (data.description) formData.append('description', data.description)
  if (data.accuracy !== undefined && data.accuracy !== null) formData.append('accuracyValue', data.accuracy)
  if (data.precision !== undefined && data.precision !== null) formData.append('precisionValue', data.precision)
  if (data.recall !== undefined && data.recall !== null) formData.append('recallValue', data.recall)
  if (data.f1_score !== undefined && data.f1_score !== null) formData.append('f1ScoreValue', data.f1_score)
  if (data.file) formData.append('file', data.file)

  return request({
    url: '/api/models',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除模型
export function deleteModel(id) {
  return request({
    url: `/api/models/${id}`,
    method: 'delete'
  })
}

// 启用模型
export function activateModel(id, active = true) {
  return request({
    url: `/api/models/${id}/active`,
    method: 'put',
    params: { active }
  })
}

// 停用模型
export function deactivateModel(id) {
  return request({
    url: `/api/models/${id}/active`,
    method: 'put',
    params: { active: false }
  })
}

// 更新模型
export function updateModel(id, data) {
  const formData = new FormData()
  
  // 直接添加各个字段到FormData
  if (data.name) formData.append('name', data.name)
  if (data.type) formData.append('type', data.type)
  if (data.version) formData.append('version', data.version)
  if (data.description) formData.append('description', data.description)
  if (data.accuracy !== undefined && data.accuracy !== null) formData.append('accuracyValue', data.accuracy)
  if (data.precision !== undefined && data.precision !== null) formData.append('precisionValue', data.precision)
  if (data.recall !== undefined && data.recall !== null) formData.append('recallValue', data.recall)
  if (data.f1_score !== undefined && data.f1_score !== null) formData.append('f1ScoreValue', data.f1_score)
  if (data.file) formData.append('file', data.file)

  return request({
    url: `/api/models/${id}`,
    method: 'put',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 分析模型
export function analyzeModel(file) {
  const formData = new FormData()
  formData.append('file', file)

  return request({
    url: '/api/models/analyze',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
} 