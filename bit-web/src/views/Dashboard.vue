<template>
  <div class="dashboard">
    <section class="dashboard-card">
      <h1>登录成功</h1>
      <p>欢迎回来，祝您工作顺利！</p>
      <div class="dashboard-actions">
        <button type="button" @click="handleGoBack">返回登录</button>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const handleGoBack = () => {
  router.push('/login')
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #eef2ff 0%, #ffffff 100%);
  padding: 24px;
  box-sizing: border-box;
}

.dashboard-card {
  width: 100%;
  max-width: 460px;
  padding: 48px 40px;
  border-radius: 16px;
  background-color: #fff;
  box-shadow: 0 18px 45px rgba(79, 70, 229, 0.15);
  text-align: center;
  box-sizing: border-box;
}

.dashboard-card h1 {
  margin: 0;
  font-size: 30px;
  font-weight: 600;
  color: #4338ca;
}

.dashboard-card p {
  margin-top: 16px;
  color: #4b5563;
  font-size: 16px;
}

.dashboard-actions {
  margin-top: 32px;
}

.dashboard-actions button {
  padding: 12px 24px;
  border: none;
  border-radius: 9999px;
  background: linear-gradient(135deg, #4338ca, #6366f1);
  color: #fff;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.dashboard-actions button:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(79, 70, 229, 0.2);
}
</style>

