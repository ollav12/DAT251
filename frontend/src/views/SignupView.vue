<script lang="ts">
import { useRouter } from 'vue-router';

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

  setup() {
    const router = useRouter();
    return { router };
  },

  methods: {
    async handleSubmit(e: Event) {
      e.preventDefault();
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
          // Redirect directly after successful signup
          await this.router.push({name: 'login'});
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
  <div class="login-box">
    <h1>Signup</h1>
    <form @submit="handleSubmit">
      <div class="input-group">
        <label for="firstName">First Name:</label>
        <input type="text" id="firstName" v-model="firstName" required />
      </div>
      <div class="input-group">
        <label for="lastName">Last Name:</label>
        <input type="text" id="lastName" v-model="lastName" required />
      </div>
      <div class="input-group">
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div class="input-group">
        <label for="email">Email:</label>
        <input type="email" id="email" v-model="email" required />
      </div>
      <div class="input-group">
        <label for="password">Password:</label>
        <input type="password" id="password" v-model="password" required />
      </div>
      <button class="register-button" type="submit">Signup</button>
    </form>
  </div>
</template>

<style scoped>
.login-box {
  background-color: #555555;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  width: 300px;
  margin: 0 auto;
  margin-top: 2rem;
}

h1 {
  color: #c6cacc;
  margin-bottom: 1.5rem;
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
