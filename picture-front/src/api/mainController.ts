// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 检查检查接口 GET /api/health */
export async function healthUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/health', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 检查检查接口 PUT /api/health */
export async function healthUsingPut(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/health', {
    method: 'PUT',
    ...(options || {}),
  })
}

/** 检查检查接口 POST /api/health */
export async function healthUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/health', {
    method: 'POST',
    ...(options || {}),
  })
}

/** 检查检查接口 DELETE /api/health */
export async function healthUsingDelete(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/health', {
    method: 'DELETE',
    ...(options || {}),
  })
}

/** 检查检查接口 PATCH /api/health */
export async function healthUsingPatch(options?: { [key: string]: any }) {
  return request<API.BaseResponseString_>('/api/health', {
    method: 'PATCH',
    ...(options || {}),
  })
}
