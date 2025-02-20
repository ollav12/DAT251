<script lang="ts">
export default {
  data() {
    return {
      username: '',
      password: '',
    }
  },
  methods: {
    async handleSubmit(event: Event) {
      event.preventDefault()
      try {
        const response = await fetch('http://localhost:8080/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: this.username, password: this.password })
        });
        if (!response.ok) { throw new Error('Login failed'); }
        const data = await response.json();
        console.log('Login success:', data);
      } catch (error) {
        console.error('Error during login:', error);
      }
    },
  },
}
</script>

<template>
  <h1>Login</h1>
  <form @submit.prevent="handleSubmit">
    <input type="username" v-model="username" placeholder="Username" required />
    <input type="password" v-model="password" placeholder="Password" required />
    <button type="submit">Login</button>
  </form>
</template>

<style scoped>
form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 300px;
}
</style>
