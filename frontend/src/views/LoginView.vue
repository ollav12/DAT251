<script lang="ts">
import { useRouter } from "vue-router";
import eye from "@/assets/eye.svg";
import eyeOff from "@/assets/eye-off.svg";
import { useCosmeticsStore } from '@/composables/useCosmeticsStore';

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
    const { updateEquippedBorder, updateEquippedProfilePicture } = useCosmeticsStore();
    return { router, updateEquippedBorder, updateEquippedProfilePicture };
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
          localStorage.setItem('isLoggedIn', 'true');
          localStorage.setItem('userId', data.userId);

          // Save equipped cosmetics
          if (data.equippedBorder) {
            this.updateEquippedBorder(data.equippedBorder);
          }

          if (data.equippedProfilePicture) {
            this.updateEquippedProfilePicture(data.equippedProfilePicture);
          }
          // Redirect directly after successful login
          console.log("User id: ", data)
          localStorage.setItem("userId", data.userId);
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
  <div class="auth-container">
    <div class="login-card card">
      <div class="card-header text-center">
        <h1>Login</h1>
        <p class="text-muted">Sign in to your account</p>
      </div>
      
      <form @submit.prevent="handleSubmit" class="login-form">
        <div v-if="error" class="alert alert-danger">{{ error }}</div>
        
        <div class="form-group">
          <label for="username" class="form-label">Username</label>
          <input 
            type="username" 
            id="username"
            class="form-control" 
            v-model="username" 
            placeholder="Enter your username" 
            required 
          />
        </div>

        <div class="form-group">
          <label for="password" class="form-label">Password</label>
          <div class="password-wrapper">
            <input
              :type="showPassword ? 'text' : 'password'"
              id="password"
              class="form-control"
              v-model="password"
              placeholder="Enter your password"
              required
            />
            <button type="button" class="toggle-password" @click="togglePasswordVisibility">
              <img :src="showPassword ? eye : eyeOff" alt="Toggle password visibility" class="eye-icon" />
            </button>
          </div>
        </div>
        
        <div class="form-buttons">
          <button class="btn btn-primary btn-block" type="submit">Login</button>
          <button class="btn btn-outline btn-block" type="button" @click="navigateToSignup">Create Account</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: var(--spacing-lg);
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: var(--spacing-xl);
  background-color: var(--background-primary);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-lg);
}

.card-header {
  margin-bottom: var(--spacing-lg);
}

.card-header h1 {
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-weight-bold);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.form-buttons {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
  margin-top: var(--spacing-md);
}

.alert-danger {
  background-color: var(--accent-color-light);
  color: var(--danger-color);
  border-radius: var(--border-radius-sm);
  padding: var(--spacing-sm) var(--spacing-md);
  margin-bottom: var(--spacing-md);
  font-size: var(--font-size-sm);
}

.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 40px;
}

.toggle-password {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  margin: 0;
  width: auto;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
}

.toggle-password:hover {
  background-color: transparent;
  color: var(--primary-color);
}

.eye-icon {
  width: 20px;
  height: 20px;
  cursor: pointer;
  opacity: 0.7;
  transition: opacity var(--transition-base);
}

.eye-icon:hover {
  opacity: 1;
}

@media (max-width: 480px) {
  .login-card {
    padding: var(--spacing-lg);
  }
}
</style>
