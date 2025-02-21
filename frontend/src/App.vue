<script setup lang="ts">
import {ref} from 'vue';
import { RouterLink } from 'vue-router';
import LoginView from './views/LoginView.vue';
import SignupView from './views/SignupView.vue';

const isLoggedIn = ref(false);

function handleLoginSuccess() {
  isLoggedIn.value = true;
}

function handleSignupSuccess() {
  isLoggedIn.value = true;
}

</script>

<template>
  <header>
    <h1>CO₂mpass</h1>
    <p>Understand your carbon footprint</p>
  </header>

  <!-- Show login or register component when not logged in -->
  <div v-if="!isLoggedIn">
    <LoginView @login-success="handleLoginSuccess"/>
    <SignupView @navigate-to-signup="handleSignupSuccess"/>
  </div>
  <div v-else>
    <div class="wrapper">
      <nav>
        <RouterLink to="/">Home</RouterLink>
        <RouterLink to="/tripestimator">Trip estimator</RouterLink>
      </nav>
    </div>
    <RouterView />
  </div>
</template>

<style scoped>
header {
  text-align: center;
  margin-top: 0.1rem;
}

nav {
  display: flex;
  justify-content: center;
  gap: 1rem;
  font-size: 12px;
  width: 100%;
  margin-top: 2rem
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
