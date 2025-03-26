<script lang="ts">
import { useRouter } from "vue-router";
import eye from "@/assets/eye.svg";
import eyeOff from "@/assets/eye-off.svg";

export default {
  data() {
    return {
      username: '',
      password: '',
      error: '',
      showPassword: false,
      eye,
      eyeOff,
    }
  },

  setup() {
    const router = useRouter();
    return { router };
  },

  methods: {
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword;
    },

    async handleSubmit(event: Event) {
      event.preventDefault();
      this.error = '';
      try {
        const response = await fetch('http://localhost:8080/auth/login', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: this.username,
            password: this.password,
          }),
        });

        const data = await response.json();

        if (!response.ok) {
          this.error = data.error;
          throw new Error(data.error);
        } else {
          // Redirect directly after successful login
          console.log("User id: ", data)
          localStorage.setItem("userId", data);
          await this.router.push({name: 'home'});
        }
        console.log('Login success:', data);
      } catch (error) {
        console.error('Error during login:', error);
      }
    },
    navigateToSignup() {
      this.router.push({ name: 'signup' });
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
        <label for="password">Password:</label>
        <div class="password-wrapper">
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="password"
            placeholder="Password"
            required
          />
          <button type="button" class="toggle-password" @click="togglePasswordVisibility">
            <img :src="showPassword ? eye : eyeOff" alt="Toggle password visibility" class="eye-icon" />
          </button>
        </div>
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
  margin: 2rem auto 0;
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

.register-button:hover {
  background-color: #47a365;
}

.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 40px;
}

.toggle-password {
  position: absolute;
  left: 200px;
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  width: 2rem;
  height: 2rem;
  padding: 0 10px;
  margin: 0;
}

.toggle-password:hover {
  background-color: transparent;
}

.eye-icon {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

</style>
