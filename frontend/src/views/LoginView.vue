<script lang="ts">
import {useRouter} from "vue-router";

export default {
  data() {
    return {
      username: '',
      password: '',
      error: '',
    }
  },

  setup() {
    const router = useRouter();
    return { router };
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
            password: this.password,
          }),
        })

        const data = await response.json()

        if (!response.ok) {
          this.error = data.error
          throw new Error(data.error)
        } else {
          this.$emit('login-success', data.user)
        }
        console.log('Login success:', data.message)
      } catch (error) {
        console.error('Error during login:', error)
      }
    },
    navigateToSignup() {
      this.$router.push({ name: 'signup' })
    }
  },
}
</script>

<template>
  <div class="login-box">
    <h1>Login</h1>
    <form @submit.prevent="handleSubmit">
      <div v-if="error" style="color: red">{{ error }}</div>
      <div class="input-group">
        <label for="username">Username</label>
        <input type="username" v-model="username" placeholder="Username" required />
      </div>

      <div class="input-group">
        <label for="password">Password</label>
        <input type="password" v-model="password" placeholder="Password" required />
      </div>

      <button class="register-button" type="submit">Login</button>
      <button class="register-button" type="button" @click="navigateToSignup">Register</button>
    </form>
  </div>
</template>

<style scoped>
h1 {
  color: #c6cacc;
  margin-bottom: 1.5rem;
}

.login-box {
  background-color: #555555;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  width: 300px;
  margin: 0 auto;
  margin-top: 2rem;
}

.input-group {
  margin-bottom: 1rem;
}

label {
  display: block;
  color: #ecf0f1;
  margin-bottom: 0.5rem;
  text-align: left;
}

input {
  width: 100%;
  padding: 0.5rem;
  border-radius: 5px;
  border: none;
  background-color: #ecf0f1;
  color: #1a1a1a;
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

button:hover {
  background-color: #16a085;
}

.register-button:hover {
  background-color: #47a365;
}
</style>
