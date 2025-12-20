/// <reference types="vite/client" />

declare namespace API {
  /**
   * 用户登录请求参数
   * 功能: 描述登录所需的账号与密码
   * @property userAccount 账号，用户的登录名
   * @property userPassword 密码，至少 8 位
   */
  interface UserLoginRequest {
    userAccount: string
    userPassword: string
  }

  /**
   * 登录用户信息
   * 功能: 描述当前已登录用户的基本信息
   * @property id 用户 id，可选
   * @property userName 用户名，可选
   */
  interface loginUserVO {
    id?: number
    userName?: string
  }
}

// 兼容类型检查环境缺少 WebWorker 类型的情况
// 功能: 提供 Worker 的最简类型声明以通过类型检查
declare global {
  interface Worker {}
}
