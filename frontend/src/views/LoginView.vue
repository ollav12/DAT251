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
      throw new Error('Not implemented')
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
