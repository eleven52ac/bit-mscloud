<template>
  <!-- 外层容器：用于居中卡片并提供背景 -->
  <div class="login">
    <div class="login-background">
      <span class="login-background__flow" />
      <span class="login-background__blob login-background__blob--one" />
      <span class="login-background__blob login-background__blob--two" />
      <span class="login-background__blob login-background__blob--three" />
      <span class="login-background__wave login-background__wave--one" />
      <span class="login-background__wave login-background__wave--two" />
    </div>
    <section class="login-card">
      <!-- 标题区域：提供欢迎语和说明 -->
      <header class="login-card__header">
        <h1 class="login-card__title">欢迎登录</h1>
        <p class="login-card__subtitle">请输入您的企业账号信息以继续访问系统</p>
      </header>

      <!-- 登录表单主体 -->
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="login-form__item" :class="{ 'login-form__item--error': fieldErrors.username }">
          <!-- 账号输入 -->
          <label class="login-form__label" for="username">账号</label>
          <input
            id="username"
            v-model.trim="form.username"
            class="login-form__input"
            type="text"
            placeholder="请输入登录账号"
            autocomplete="username"
            :disabled="loading"
            @blur="validateField('username')"
          />
          <p
            class="login-form__error"
            :class="{ 'login-form__error--visible': fieldErrors.username }"
          >
            {{ fieldErrors.username || '　' }}
          </p>
        </div>

        <div class="login-form__item" :class="{ 'login-form__item--error': fieldErrors.password }">
          <!-- 密码输入及显隐按钮 -->
          <label class="login-form__label" for="password">密码</label>
          <div class="login-form__password-wrapper">
            <input
              id="password"
              v-model="form.password"
              class="login-form__input"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              autocomplete="current-password"
              :disabled="loading"
              @blur="validateField('password')"
            />
            <button
              type="button"
              class="login-form__toggle"
              :aria-label="showPassword ? '隐藏密码' : '显示密码'"
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? '隐藏' : '显示' }}
            </button>
          </div>
          <p
            class="login-form__error"
            :class="{ 'login-form__error--visible': fieldErrors.password }"
          >
            {{ fieldErrors.password || '　' }}
          </p>
        </div>

        <!-- 表单辅助操作 -->
        <div class="login-form__actions">
          <label class="login-form__remember">
            <input v-model="form.rememberMe" type="checkbox" :disabled="loading" />
            记住账号
          </label>
          <button type="button" class="login-form__link" @click="handleForgotPassword">
            忘记密码？
          </button>
        </div>

        <!-- 提交按钮 -->
        <button class="login-form__submit" type="submit" :disabled="submitDisabled">
          <span v-if="loading" class="login-form__spinner" aria-hidden="true" />
          <span>{{ loading ? '正在登录...' : '登录' }}</span>
        </button>
      </form>

      <!-- 反馈信息 -->
      <p v-if="generalError" class="login-form__alert" role="alert">{{ generalError }}</p>
      <p v-if="successMessage" class="login-form__success" role="status">{{ successMessage }}</p>
    </section>

    <transition name="alert-fade">
      <div
        v-if="alert.visible"
        class="alert-overlay"
        role="alertdialog"
        aria-modal="true"
        :aria-label="alert.title || '通知'"
      >
        <section class="alert-card" :class="`alert-card--${alert.type}`">
          <header class="alert-card__header">
            <h2 class="alert-card__title">{{ alert.title }}</h2>
            <button
              type="button"
              class="alert-card__close"
              aria-label="关闭提示"
              @click="hideAlert"
            >
              ×
            </button>
          </header>
          <p class="alert-card__message">{{ alert.message }}</p>
        </section>
      </div>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/auth'

// 登录接口响应结构（根据后端返回拓展）
interface LoginResponse {
  status?: number
  message?: string
  data?: unknown
  token?: string
}

// 登录表单字段声明
interface LoginForm {
  username: string
  password: string
  rememberMe: boolean
}

type Field = keyof Pick<LoginForm, 'username' | 'password'>

// 表单数据
const form = reactive<LoginForm>({
  username: '',
  password: '',
  rememberMe: false
})

// 各字段的错误信息
const fieldErrors = reactive<Record<Field, string>>({
  username: '',
  password: ''
})

// 页面提示信息
const generalError = ref('')
const successMessage = ref('')
const loading = ref(false)
const showPassword = ref(false)
const router = useRouter()
type AlertType = 'info' | 'success' | 'error'

const alert = reactive({
  visible: false,
  title: '',
  message: '',
  type: 'info' as AlertType
})

let alertTimer: number | undefined

const hideAlert = () => {
  alert.visible = false
  if (alertTimer) {
    window.clearTimeout(alertTimer)
    alertTimer = undefined
  }
}

const showAlert = (
  message: string,
  options?: {
    title?: string
    type?: AlertType
    duration?: number
  }
) => {
  hideAlert()
  alert.message = message
  alert.title =
    options?.title ||
    (options?.type === 'error'
      ? '操作失败'
      : options?.type === 'success'
        ? '操作成功'
        : '提示')
  alert.type = options?.type ?? 'info'
  alert.visible = true

  const duration = options?.duration ?? 3000
  if (duration > 0) {
    alertTimer = window.setTimeout(() => {
      alert.visible = false
      alertTimer = undefined
    }, duration)
  }
}

// 提交按钮禁用逻辑
const submitDisabled = computed(() => {
  return loading.value || !form.username || !form.password
})

// 单字段校验逻辑
const validateField = (field: Field) => {
  fieldErrors[field] = ''

  if (field === 'username') {
    if (!form.username) {
      fieldErrors.username = '请输入账号'
    } else if (form.username.length < 3) {
      fieldErrors.username = '账号长度至少为 3 位'
    }
  }

  if (field === 'password') {
    if (!form.password) {
      fieldErrors.password = '请输入密码'
    } else if (form.password.length < 6) {
      fieldErrors.password = '密码长度至少为 6 位'
    }
  }
}

// 校验整张表单
const validateForm = () => {
  validateField('username')
  validateField('password')
  return !fieldErrors.username && !fieldErrors.password
}

// “忘记密码”按钮响应
const handleForgotPassword = () => {
  generalError.value = ''
  successMessage.value = ''
  showAlert('请联系管理员重置密码。', { title: '忘记密码', type: 'info' })
}

// 登录提交
const handleLogin = async () => {
  generalError.value = ''
  successMessage.value = ''

  if (!validateForm()) {
    return
  }

  loading.value = true
  try {
    const { data } = await login({
      username: form.username,
      password: form.password,
      loginType: 'username_password'
    })
    const payload = data as LoginResponse

    if (form.rememberMe) {
      localStorage.setItem('rememberedUsername', form.username)
    } else {
      localStorage.removeItem('rememberedUsername')
    }

    if (payload?.status !== 200) {
      const message =
        payload?.message ||
        (payload?.status ? `登录失败（状态码：${payload.status}）` : '') ||
        '登录失败，请稍后重试。'
      console.warn('登录失败：', payload)
      generalError.value = ''
      showAlert(message, { title: '登录失败', type: 'error', duration: 0 })
      return
    }

    let token: string | undefined

    if (typeof payload?.data === 'string') {
      token = payload.data
    } else if (payload?.data && typeof payload.data === 'object') {
      const dataObject = payload.data as { token?: string }
      token = dataObject?.token
    } else if (payload?.token) {
      token = payload.token
    }

    if (token) {
      localStorage.setItem('authToken', token)
    }

    successMessage.value = '登录成功，即将为您跳转...'
    console.log('登录成功：', payload)

    setTimeout(() => {
      router.push('/dashboard')
    }, 1000)
  } catch (error: any) {
    const message =
      error?.response?.data?.message ||
      error?.message ||
      '登录失败，请稍后重试。'
    generalError.value = ''
    showAlert(message, { title: '登录失败', type: 'error', duration: 0 })
  } finally {
    loading.value = false
  }
}

// 初始化时自动填充“记住邮箱”内容
onMounted(() => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    form.username = rememberedUsername
    form.rememberMe = true
  }
})

onBeforeUnmount(() => {
  hideAlert()
})
</script>

<style scoped>
/* 页面背景及整体布局 */
.login {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #e6f2ff 0%, #f9f7ff 50%, #ffffff 100%);
  background-size: 220% 220%;
  animation: gradientShift 22s ease-in-out infinite alternate;
  padding: 24px;
  box-sizing: border-box;
}

.login-background {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
  z-index: 0;
}

.login-background__blob {
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 45% 55% 60% 40%/55% 45% 55% 45%;
  filter: blur(50px);
  opacity: 0.75;
  transform-origin: center;
  animation: blobMorph 26s ease-in-out infinite alternate;
  mix-blend-mode: screen;
  backdrop-filter: blur(12px);
  box-shadow: 0 0 120px rgba(59, 130, 246, 0.3);
}

.login-background__flow {
  position: absolute;
  inset: -20%;
  background: conic-gradient(
    from 90deg,
    rgba(76, 106, 255, 0.08),
    rgba(56, 180, 255, 0.16),
    rgba(50, 220, 190, 0.12),
    rgba(147, 91, 255, 0.16),
    rgba(76, 106, 255, 0.08)
  );
  animation: flowRotate 32s linear infinite;
  filter: blur(60px);
  transform-origin: center;
  opacity: 0.9;
  mix-blend-mode: screen;
}

.login-background__blob--one {
  top: -160px;
  left: -180px;
  background: radial-gradient(circle at 30% 30%, rgba(37, 99, 235, 0.8), rgba(37, 99, 235, 0));
  animation-delay: 0s;
}

.login-background__blob--two {
  bottom: -220px;
  right: -200px;
  background: radial-gradient(circle at 70% 70%, rgba(79, 70, 229, 0.8), rgba(79, 70, 229, 0));
  animation-delay: 8s;
}

.login-background__blob--three {
  top: 40%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 460px;
  height: 460px;
  background: radial-gradient(circle at 50% 50%, rgba(16, 185, 129, 0.7), rgba(16, 185, 129, 0));
  animation-delay: 12s;
  box-shadow: 0 0 120px rgba(16, 185, 129, 0.35);
}

.login-background__wave {
  position: absolute;
  left: -20%;
  width: 140%;
  height: 180px;
  background: linear-gradient(
    90deg,
    rgba(255, 255, 255, 0) 0%,
    rgba(148, 163, 255, 0.28) 45%,
    rgba(236, 254, 255, 0.45) 55%,
    rgba(255, 255, 255, 0) 100%
  );
  opacity: 0.85;
  transform: skewY(-6deg);
  animation: waveSlide 18s ease-in-out infinite alternate;
  mix-blend-mode: screen;
}

.login-background__wave--one {
  top: 15%;
}

.login-background__wave--two {
  bottom: 12%;
  animation-delay: 6s;
  opacity: 0.35;
}

.login::before,
.login::after {
  content: '';
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 50%;
  filter: blur(120px);
  opacity: 0.15;
  z-index: 0;
  animation: floatParticle 16s ease-in-out infinite;
  mix-blend-mode: screen;
}

.login::before {
  background: rgba(59, 130, 246, 0.4);
  top: -160px;
  left: -140px;
}

.login::after {
  background: rgba(126, 214, 255, 0.4);
  bottom: -180px;
  right: -160px;
  animation-delay: 6s;
}

/* 登录卡片主体样式 */
.login-card {
  width: 100%;
  max-width: 420px;
  padding: 48px 40px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
  box-shadow: 0 25px 65px rgba(15, 76, 129, 0.22);
  backdrop-filter: blur(14px);
  box-sizing: border-box;
  position: relative;
  z-index: 2;
}

/* 卡片头部 */
.login-card__header {
  text-align: center;
  margin-bottom: 32px;
}

.login-card__title {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #0f4c81;
}

.login-card__subtitle {
  margin: 8px 0 0;
  color: #6b7280;
  font-size: 14px;
}

/* 表单项样式 */
.login-form__item {
  margin-bottom: 18px;
}

.login-form__label {
  display: block;
  margin-bottom: 6px;
  font-size: 14px;
  color: #1f2937;
  font-weight: 500;
}

.login-form__input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #d1d5db;
  border-radius: 10px;
  font-size: 14px;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(6px);
}

/* 获取焦点 */
.login-form__input:focus {
  border-color: #0f4c81;
  outline: none;
  box-shadow: 0 0 0 4px rgba(15, 76, 129, 0.1);
}

/* 密码输入区域 */
.login-form__password-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.login-form__password-wrapper .login-form__input {
  padding-right: 72px;
}

.login-form__toggle {
  position: absolute;
  right: 12px;
  border: none;
  background: none;
  color: #0f4c81;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px;
  transition: color 0.2s;
}

/* 显示/隐藏密码按钮状态 */
.login-form__toggle:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.login-form__toggle:hover {
  color: #0c3a63;
}

/* 辅助操作（记住我等） */
.login-form__actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.login-form__remember {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #4b5563;
  font-size: 13px;
}

.login-form__link {
  border: none;
  background: none;
  color: #0f4c81;
  font-size: 13px;
  cursor: pointer;
  padding: 0;
}

/* 提交按钮 */
.login-form__submit {
  width: 100%;
  padding: 12px 14px;
  border: none;
  border-radius: 10px;
  background: linear-gradient(135deg, #0f4c81, #1d9bf0);
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
  position: relative;
  overflow: hidden;
}

/* 提交按钮禁用态 */
.login-form__submit:disabled {
  cursor: not-allowed;
  background: #94a3b8;
  box-shadow: none;
}

.login-form__submit:not(:disabled):hover {
  transform: translateY(-2px) scale(1.01);
  box-shadow: 0 18px 32px rgba(15, 76, 129, 0.25);
}

/* 按钮加载动画 */
.login-form__spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.6);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* 错误提示 */
.login-form__error {
  min-height: 18px;
  margin-top: 6px;
  font-size: 12px;
  color: #dc2626;
  visibility: hidden;
  transition: visibility 0.2s, opacity 0.2s;
  opacity: 0;
}

.login-form__error--visible {
  visibility: visible;
  opacity: 1;
}

.login-form__item--error .login-form__input {
  border-color: #dc2626;
  animation: pulseBorder 0.4s ease-in-out;
}

/* 提示信息样式 */
.login-form__alert {
  margin-top: 18px;
  padding: 12px;
  border-radius: 10px;
  background-color: #fee2e2;
  color: #b91c1c;
  font-size: 13px;
  text-align: center;
}

.login-form__success {
  margin-top: 18px;
  padding: 12px;
  border-radius: 10px;
  background-color: #ecfdf5;
  color: #047857;
  font-size: 13px;
  text-align: center;
}

.alert-overlay {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(15, 23, 42, 0.45);
  backdrop-filter: blur(4px);
  padding: 24px;
  z-index: 999;
}

.alert-card {
  width: 100%;
  max-width: 360px;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 18px 45px rgba(15, 76, 129, 0.25);
  padding: 28px 24px 24px;
  box-sizing: border-box;
}

.alert-card--error {
  border-top: 4px solid #ef4444;
}

.alert-card--info {
  border-top: 4px solid #0f4c81;
}

.alert-card--success {
  border-top: 4px solid #10b981;
}

.alert-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.alert-card__title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.alert-card__close {
  border: none;
  background: none;
  font-size: 22px;
  line-height: 1;
  cursor: pointer;
  color: #64748b;
  padding: 4px;
}

.alert-card__close:hover {
  color: #1f2937;
}

.alert-card__message {
  margin: 0;
  color: #475569;
  font-size: 14px;
  line-height: 1.6;
  animation: textReveal 0.6s ease forwards;
}

.alert-fade-enter-active,
.alert-fade-leave-active {
  transition: opacity 0.2s ease;
}

.alert-fade-enter-from,
.alert-fade-leave-to {
  opacity: 0;
}

.login,
.alert-overlay {
  perspective: 1400px;
}

/* 加载动画关键帧 */
@keyframes spin {
  from {
    transform: rotate(0);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes floatParticle {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1);
  }
  50% {
    transform: translate3d(18px, -14px, 0) scale(1.015);
  }
}

@keyframes blobMorph {
  0%,
  100% {
    transform: translate3d(0, 0, 0) scale(1) rotate(0deg);
    border-radius: 45% 55% 60% 40%/55% 45% 55% 45%;
  }
  33% {
    transform: translate3d(8px, -12px, 0) scale(1.03) rotate(6deg);
    border-radius: 50% 50% 55% 45%/50% 55% 45% 50%;
  }
  66% {
    transform: translate3d(-10px, 14px, 0) scale(0.985) rotate(-5deg);
    border-radius: 48% 52% 58% 42%/58% 42% 48% 52%;
  }
}

@keyframes waveSlide {
  0% {
    transform: translateX(-12%) skewY(-6deg);
    opacity: 0.16;
  }
  25% {
    opacity: 0.3;
  }
  50% {
    transform: translateX(8%) skewY(-6deg);
    opacity: 0.24;
  }
  75% {
    opacity: 0.3;
  }
  100% {
    transform: translateX(20%) skewY(-6deg);
    opacity: 0.16;
  }
}

@keyframes flowRotate {
  0% {
    transform: rotate(0deg) scale(1);
  }
  50% {
    transform: rotate(180deg) scale(1.02);
  }
  100% {
    transform: rotate(360deg) scale(1);
  }
}

@keyframes pulseBorder {
  0%,
  100% {
    box-shadow: 0 0 0 0 rgba(220, 38, 38, 0);
  }
  50% {
    box-shadow: 0 0 0 6px rgba(220, 38, 38, 0.15);
  }
}

@keyframes textReveal {
  from {
    transform: translateY(6px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 小屏优化 */
@media (max-width: 480px) {
  .login-card {
    padding: 32px 24px;
  }

  .login-card__title {
    font-size: 24px;
  }
}
</style>
