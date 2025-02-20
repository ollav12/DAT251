<script lang="ts">
export default {
  data() {
    return {
      username: '',
      password: '',
      error: ''
    }
  },
  methods: {
    async handleSubmit(event: Event) {
      event.preventDefault()
      this.error = ''
      try {
        const response = await fetch('http://localhost:8080/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: this.username,
            password: this.password
          })
        });

        const data = await response.json();

        if (!response.ok) {
          this.error = data.error;
          throw new Error(data.error);
        }
        console.log('Login success:', data.message);
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
    <div v-if="error" style="color: red">{{ error }}</div>
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
