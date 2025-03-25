<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { onMounted } from 'vue'
import { getMe, type User } from './services/user'
import UserProfileView from "@/views/UserProfileView.vue";


const router = useRouter()
const route = useRoute()

const showLayout = computed(() => {
  return !route.meta.hideLayout
})

const user = ref<User>()

onMounted(() => {
  const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
  if (!isLoggedIn) {
    router.push('/login')
  }

  // TODO: things like admin role could be stored in a JWT
  // That way we do not have to make a request every time.
  getMe().then((data) => {
    user.value = data
  })
})
</script>

<template>
  <header>
    <h1>CO₂mpass</h1>
    <p>Understand your carbon footprint</p>
  </header>

  <nav v-if="showLayout">
    <RouterLink to="/">Home</RouterLink>
    <RouterLink to="/vehicles">Vehicles</RouterLink>
    <RouterLink to="/leaderboard">Leaderboard</RouterLink>
    <RouterLink to="/tripestimator">Trip estimator</RouterLink>
    <RouterLink to="/shop">Shop</RouterLink>
    <RouterLink to="/inventory">Inventory</RouterLink>
    <RouterLink to="/statistics">Statistics</RouterLink>
    <RouterLink v-if="user?.admin" to="/admin">Admin</RouterLink>

    <UserProfileView />
  </nav>

  <RouterView />
</template>

<style scoped>
header {
  text-align: center;
  margin-top: 0.1rem;
  font-size: large;
}

nav {
  display: flex;
  justify-content: center;
  gap: 1rem;
  font-size: 12px;
  width: 100%;
  margin-top: 2rem;
}

nav a.router-link-exact-active {
  color: var(--color-text);
}

nav a.router-link-exact-active:hover {
  background-color: transparent;
}

nav a {
  padding: 0 1rem;
  border-left: 1px solid var(--color-border);
}

nav a:first-of-type {
  border: 0;
}

@media (min-width: 1024px) {
  nav {
    font-size: 1rem;
    padding: 1rem 0;
    margin-top: 1rem;
    text-align: left;
    margin-left: 0;
  }
}

header {
  text-align: center;
}

.wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  padding: 0 1rem;
}
</style>
