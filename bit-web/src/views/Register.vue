<template>
  <div class="register">
    <section class="card">
      <h1 class="title">注册</h1>
      <form class="form" @submit.prevent="handleRegister">
        <div class="row">
          <label class="label">注册方式</label>
          <select v-model="mode" class="input">
            <option value="email">邮箱</option>
            <option value="phone">手机</option>
          </select>
        </div>
        <div class="row">
          <label class="label">用户名</label>
          <input v-model.trim="username" class="input" type="text" />
        </div>
        <div class="row" v-if="mode==='email'">
          <label class="label">邮箱</label>
          <input v-model.trim="email" class="input" type="email" />
        </div>
        <div class="row" v-else>
          <label class="label">手机号</label>
          <input v-model.trim="phone" class="input" type="tel" />
        </div>
        <div class="row">
          <label class="label">密码</label>
          <input v-model="password" class="input" type="password" />
        </div>
        <div class="row">
          <label class="label">验证码</label>
          <div class="inline">
            <input v-model.trim="captcha" class="input" type="text" />
            <button type="button" class="btn secondary" @click="handleGetCaptcha" :disabled="captchaLoading">{{ captchaLoading ? '发送中' : '获取验证码' }}</button>
          </div>
        </div>
        <button class="btn primary" type="submit" :disabled="submitDisabled">{{ loading ? '提交中' : '注册' }}</button>
      </form>
      <p v-if="message" class="msg">{{ message }}</p>
      <p v-if="error" class="err">{{ error }}</p>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getCaptcha, register } from '../api/auth'

const mode = ref<'email'|'phone'>('email')
const username = ref('')
const email = ref('')
const phone = ref('')
const password = ref('')
const captcha = ref('')
const loading = ref(false)
const captchaLoading = ref(false)
const message = ref('')
const error = ref('')
const router = useRouter()

const submitDisabled = computed(() => {
  const base = !!username.value && !!password.value && !!captcha.value
  if (mode.value === 'email') return base && !!email.value ? false : true
  return base && !!phone.value ? false : true
})

const handleGetCaptcha = async () => {
  error.value = ''
  message.value = ''
  const identifier = mode.value === 'email' ? email.value : phone.value
  const captchaMethod = mode.value === 'email' ? 'email_captcha' : 'phone_captcha'
  if (!identifier) {
    error.value = '请填写邮箱或手机号'
    return
  }
  captchaLoading.value = true
  try {
    const { data } = await getCaptcha(identifier, captchaMethod)
    if (data?.code === 200) {
      message.value = data?.msg || '验证码已发送'
    } else {
      error.value = data?.msg || '验证码发送失败'
    }
  } catch (e: any) {
    error.value = e?.message || '验证码发送失败'
  } finally {
    captchaLoading.value = false
  }
}

const handleRegister = async () => {
  error.value = ''
  message.value = ''
  loading.value = true
  try {
    const registerType = mode.value === 'email' ? 'email_code' : 'phone_code'
    const payload = {
      registerType,
      username: username.value,
      email: mode.value === 'email' ? email.value : '',
      phone: mode.value === 'phone' ? phone.value : '',
      captcha: captcha.value,
      password: password.value
    }
    const { data } = await register(payload)
    if (data?.code === 200) {
      message.value = data?.msg || '注册成功'
      setTimeout(() => router.push('/login'), 1000)
    } else {
      error.value = data?.msg || '注册失败'
    }
  } catch (e: any) {
    error.value = e?.message || '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #f6f7fb; padding: 24px; }
.card { width: 420px; background: #fff; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,.08); padding: 24px; }
.title { font-size: 20px; font-weight: 600; margin: 0 0 12px; }
.form { display: grid; gap: 14px; }
.row { display: grid; gap: 8px; }
.label { font-size: 13px; color: #666; }
.input { height: 38px; border: 1px solid #e5e7eb; border-radius: 8px; padding: 0 12px; font-size: 14px; outline: none; }
.inline { display: grid; grid-template-columns: 1fr auto; gap: 8px; }
.btn { height: 38px; border: none; border-radius: 8px; padding: 0 14px; font-size: 14px; cursor: pointer; }
.primary { background: #2563eb; color: #fff; }
.secondary { background: #f3f4f6; color: #333; }
.msg { margin-top: 8px; color: #2563eb; font-size: 13px; }
.err { margin-top: 8px; color: #dc2626; font-size: 13px; }
</style>