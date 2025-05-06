<script setup lang="ts">
import { onMounted, computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMe, logout, type User } from './services/user'
import UserProfileView from '@/views/UserProfileView.vue'

const router = useRouter()
const route = useRoute()

const showLayout = computed(() => {
  return !route.meta.hideLayout
})

const user = ref<User>()

onMounted(() => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'false'
  if (!isLoggedIn) {
    router.push('/login')
  }

  // TODO: things like admin role could be stored in a JWT
  // That way we do not have to make a request every time.
  getMe().then((data) => {
    user.value = data
  })
})

function performLogout() {
  logout()
  window.location.href = '/login'
}
</script>

<template>
  <div class="app-container">
    <header class="app-header bg-light">
      <div class="header-content">
        <h1 class="app-title">CO₂mpass</h1>
        <p class="app-tagline">Understand your carbon footprint</p>
        <div class="header-decoration"></div>
      </div>
    </header>

    <div class="app-body container" v-if="showLayout">
      <div class="top-bar">
        <button class="btn btn-outline btn-sm logout-btn" @click="performLogout">
          <i class="icon">↪</i> Log out
        </button>

        <div class="user-profile">
          <UserProfileView />
        </div>
      </div>

      <nav class="main-nav shadow">
        <RouterLink to="/" class="nav-link"> <i class="icon">🏠</i> Home </RouterLink>
        <RouterLink to="/vehicles" class="nav-link"> <i class="icon">🚗</i> Vehicles </RouterLink>
        <RouterLink to="/tripestimator" class="nav-link">
          <i class="icon">🗺️</i> Trip estimator
        </RouterLink>
        <RouterLink to="/challenges" class="nav-link">
          <i class="icon">🏆</i> Challenges
        </RouterLink>
        <RouterLink to="/shop" class="nav-link"> <i class="icon">🛒</i> Shop </RouterLink>
        <RouterLink to="/inventory" class="nav-link"> <i class="icon">📦</i> Inventory </RouterLink>
        <RouterLink to="/statistics" class="nav-link">
          <i class="icon">📊</i> Statistics
        </RouterLink>
        <RouterLink v-if="user?.admin" to="/admin" class="nav-link">
          <i class="icon">⚙️</i> Admin
        </RouterLink>
      </nav>
    </div>

    <main class="app-content">
      <RouterView v-slot="{ Component }">
        <transition name="section-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </RouterView>
    </main>
    
    <footer class="app-footer">
      <div class="container">
        <p>© 2023 CO₂mpass. All rights reserved.</p>
        <p class="text-muted">Making a greener future, one trip at a time.</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.app-header {
  padding: var(--spacing-lg) 0;
  background: var(--bg-gradient-light);
  border-bottom: var(--card-border);
  position: relative;
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  padding: 0 var(--spacing-md);
}

.app-title {
  font-size: calc(var(--font-size-display) * 1.2);
  font-weight: 800;
  letter-spacing: -0.5px;
  background: var(--primary-gradient);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: var(--spacing-xs);
  text-shadow: 0 1px 1px rgba(0,0,0,0.05);
}

.app-tagline {
  font-size: var(--font-size-md);
  color: var(--text-secondary);
  margin: 0;
  font-weight: var(--font-weight-medium);
}

.header-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--primary-gradient);
}

.app-body {
  position: relative;
  width: 100%;
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: var(--spacing-md) 0;
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  transition: all var(--animation-fast);
}

.logout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.user-profile {
  display: flex;
  align-items: center;
}

.main-nav {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background-color: var(--background-primary);
  border-radius: var(--card-border-radius);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--card-shadow);
  border: var(--card-border);
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  color: var(--text-secondary);
  padding: var(--spacing-sm) var(--spacing-md);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-base);
  text-decoration: none;
  font-weight: var(--font-weight-medium);
  position: relative;
  overflow: hidden;
}

.nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 2px;
  background: var(--primary-gradient);
  transition: all var(--transition-base);
  transform: translateX(-50%);
}

.nav-link:hover {
  color: var(--primary-color);
  transform: translateY(-2px);
}

.nav-link:hover::after {
  width: 80%;
}

.nav-link.router-link-exact-active {
  color: var(--text-light);
  background: var(--primary-gradient);
  box-shadow: 0 4px 10px rgba(26, 188, 156, 0.3);
}

.nav-link.router-link-exact-active:hover {
  background: var(--primary-gradient);
}

.nav-link.router-link-exact-active::after {
  width: 0;
}

.icon {
  font-style: normal;
}

.app-content {
  flex: 1;
  padding: 0 var(--spacing-md);
  max-width: 1280px;
  margin: 0 auto;
  width: 100%;
  margin-bottom: var(--spacing-xl);
}

.app-footer {
  margin-top: auto;
  padding: var(--spacing-lg) 0;
  background: var(--bg-gradient-light);
  border-top: var(--card-border);
  text-align: center;
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

@media (max-width: 768px) {
  .main-nav {
    flex-direction: column;
    align-items: stretch;
    gap: var(--spacing-xs);
  }

  .nav-link {
    padding: var(--spacing-md);
  }

  .top-bar {
    flex-direction: column-reverse;
    gap: var(--spacing-md);
  }

  .logout-btn {
    width: 100%;
  }
}
</style>
