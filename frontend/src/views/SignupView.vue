<script lang="ts">
export default {
  data() {
    return {
      username: '',
      email: '',
      password: '',
    }
  },
  methods: {
    async handleSubmit(e: Event) {
      e.preventDefault()
      throw new Error('Not implemented')
      // try {
      //   const response = await fetch('/api/signup', {
      //     method: 'POST',
      //     headers: {
      //       'Content-Type': 'application/json',
      //     },
      //     body: JSON.stringify({
      //       username: this.username,
      //       email: this.email,
      //       password: this.password,
      //     }),
      //   })
      //   if (!response.ok) {
      //     throw new Error('Signup failed')
      //   }
      //   const data = await response.json()
      //   console.log('Signup successful:', data)
      // } catch (error) {
      //   console.error('Error during signup:', error)
      // }
    },
  },
}
</script>
<template>
  <h1>Signup</h1>
  <form @submit="handleSubmit">
    <label for="username">Username:</label>
    <input type="text" id="username" v-model="username" required />
    <label for="email">Email:</label>
    <input type="email" id="email" v-model="email" required />
    <label for="password">Password:</label>
    <input type="password" id="password" v-model="password" required />
    <button type="submit">Signup</button>
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
