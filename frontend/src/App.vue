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
      >>>>>>> 54ec6ea (claude changes)

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
      <RouterView />
    </main>
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
  background-color: var(--background-tertiary);
  border-bottom: 1px solid var(--border-color);
}

.header-content {
  max-width: 1280px;
  margin: 0 auto;
  text-align: center;
  padding: 0 var(--spacing-md);
}

.app-title {
  font-size: var(--font-size-display);
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-weight-bold);
}

.app-tagline {
  font-size: var(--font-size-md);
  color: var(--text-secondary);
  margin: 0;
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
  border-radius: var(--border-radius-md);
  margin-bottom: var(--spacing-lg);
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
}

.nav-link:hover {
  background-color: var(--background-tertiary);
  color: var(--primary-color);
  transform: translateY(-2px);
}

.nav-link.router-link-exact-active {
  color: var(--text-light);
  background-color: var(--primary-color);
}

.nav-link.router-link-exact-active:hover {
  background-color: var(--primary-color-dark);
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
