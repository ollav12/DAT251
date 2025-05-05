<script setup lang="ts">
import {onMounted, computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {getMe, logout, type User} from './services/user'
import UserProfileView from "@/views/UserProfileView.vue";


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
  <header>
    <h1>CO₂mpass</h1>
    <p>Understand your carbon footprint</p>
  </header>

  <nav v-if="showLayout">
    <button class="logoutButton" @click="performLogout">Log out</button>
    <RouterLink to="/">Home</RouterLink>
    <RouterLink to="/vehicles">Vehicles</RouterLink>
    <RouterLink to="/tripestimator">Trip estimator</RouterLink>
    <RouterLink to="/leaderboard">Leaderboard</RouterLink>
    <RouterLink to="/challenges">Challenges</RouterLink>
    <RouterLink to="/shop">Shop</RouterLink>
    <RouterLink to="/inventory">Inventory</RouterLink>
    <RouterLink to="/statistics">Statistics</RouterLink>
    <RouterLink v-if="user?.admin" to="/admin">Admin</RouterLink>

  </nav>

    <section class="profile-container" v-if="showLayout">
      <UserProfileView />
    </section>

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

.profile-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 1rem;
}

button {
  width: 100%;
  padding: 0.75rem;
  border: none;
  border-radius: 5px;
  background-color: #1abc9c;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 1rem;
}

.logoutButton {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 100;
  width: auto;
  padding: 0.5rem 1rem;
  border-radius: 5px;
  background-color: #1abc9c;
  color: white;
  font-size: 0.9rem;
  cursor: pointer;
  margin-top: 0;
}

.logoutButton:hover {
  background-color: #47a365;
}

</style>
