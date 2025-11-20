import http from './http'

export interface LoginPayload {
  loginType?: string
  registerType?: string
  username: string
  email?: string
  phone?: string
  captcha?: string
  password: string
}

export const login = (payload: LoginPayload) => {
  return http.post('/auth/token/login', {
    loginType: 'username_password',
    registerType: '',
    email: '',
    phone: '',
    captcha: '',
    ...payload
  })
}

export interface RegisterPayload {
  registerType: string
  username: string
  email?: string
  phone?: string
  captcha: string
  password: string
}

export const register = (payload: RegisterPayload) => {
  return http.post('/auth/token/register', payload)
}

export const getCaptcha = (identifier: string, captchaMethod: string) => {
  return http.get('/auth/token/captcha', {
    params: { identifier, captchaMethod }
  })
}
