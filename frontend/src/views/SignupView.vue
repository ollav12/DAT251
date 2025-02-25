<script lang="ts">
export default {
  data() {
    return {
      firstName: '',
      lastName: '',
      username: '',
      email: '',
      password: ''
    }
  },
  methods: {
    async handleSubmit(e: Event) {
      e.preventDefault()
      try {
        const response = await fetch('http://localhost:8080/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            firstName: this.firstName,
            lastName: this.lastName,
            username: this.username,
            email: this.email,
            password: this.password
          })
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        } else {
        this.$emit('navigate-to-signup', data.user)
        }
        console.log('Signup success:', data.message);
      } catch (error) {
        console.error('Error during signup:', error);
      }
    },
  },
}
</script>

<template>
  <h1>Signup</h1>
  <form @submit="handleSubmit">
    <label for="firstName">First Name:</label>
    <input type="text" id="firstName" v-model="firstName" required />
    <label for="lastName">Last Name:</label>
    <input type="text" id="lastName" v-model="lastName" required />
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
  text-align: center;
  margin: 0 auto;
}

h1{
  text-align: center;
  margin-top: 2rem;
}
</style>
