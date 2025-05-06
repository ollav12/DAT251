<script lang="ts">
import { useRouter } from 'vue-router'
import eye from '@/assets/eye.svg'
import eyeOff from '@/assets/eye-off.svg'
import { useCosmeticsStore } from '@/composables/useCosmeticsStore'

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
    const router = useRouter()
    const { updateEquippedBorder, updateEquippedProfilePicture } = useCosmeticsStore()
    return { router, updateEquippedBorder, updateEquippedProfilePicture }
  },

  methods: {
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword
    },

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
        }
        // Login successful
        localStorage.setItem('isLoggedIn', 'true')
        localStorage.setItem('userId', data.userId)

        // Save equipped cosmetics
        if (data.equippedBorder) {
          this.updateEquippedBorder(data.equippedBorder)
        }

        if (data.equippedProfilePicture) {
          this.updateEquippedProfilePicture(data.equippedProfilePicture)
        }
        // Redirect directly after successful login
        console.log('User id: ', data)
        localStorage.setItem('userId', data.userId)
        await this.router.push({ name: 'home' })
        console.log('Login success:', data)
      } catch (error) {
        console.error('Error during login:', error)
      }
    },
    navigateToSignup() {
      this.router.push({ name: 'signup' })
    },
  },
}
</script>

<template>
  <div class="auth-container">
    <div class="login-card card">
      <div class="card-header text-center">
        <h1 class="card-title">Login</h1>
        <p class="text-muted">Welcome to CO₂mpass</p>
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
              <img
                :src="showPassword ? eye : eyeOff"
                alt="Toggle password visibility"
                class="eye-icon"
              />
            </button>
          </div>
        </div>

        <div class="form-buttons">
          <button class="btn btn-primary btn-block" type="submit">
            Login
          </button>
          <button class="btn btn-outline btn-block" type="button" @click="navigateToSignup">
            Create Account
          </button>
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
  padding: 2rem;
  background: var(--background-secondary);
}

.login-card {
  width: 100%;
  max-width: 400px;
  padding: 2rem;
  background-color: var(--background-primary);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-medium);
  border: var(--border-width) solid var(--border-color);
  transition: all var(--transition-base);
}

.login-card:hover {
  box-shadow: var(--shadow-large);
}

.card-header {
  margin-bottom: 1.5rem;
  padding-bottom: 1rem;
  border-bottom: var(--border-width) solid var(--border-color);
}

.card-title {
  color: var(--primary-color);
  font-size: 1.75rem;
  margin-bottom: 0.5rem;
  font-weight: var(--font-weight-bold);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  position: relative;
  margin-bottom: 1rem;
}

.form-control {
  transition: all var(--transition-fast);
  border: var(--border-width) solid var(--border-color);
  padding: 0.75rem;
}

.form-control:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(43, 138, 116, 0.1);
}

.form-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

.alert-danger {
  background-color: #f8d7da;
  color: #842029;
  border-radius: var(--border-radius);
  padding: 0.75rem;
  margin-bottom: 1rem;
  font-size: 0.875rem;
  border-left: 3px solid #842029;
}

.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 44px;
}

.toggle-password {
  position: absolute;
  right: var(--spacing-md);
  top: 50%;
  transform: translateY(-50%);
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 4px;
  margin: 0;
  width: auto;
  height: auto;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-secondary);
  border-radius: 50%;
  transition: all var(--animation-fast);
}

.toggle-password:hover {
  background-color: var(--background-tertiary);
  color: var(--primary-color);
}

.eye-icon {
  width: 20px;
  height: 20px;
  cursor: pointer;
  opacity: 0.7;
  transition: all var(--animation-fast);
}

.eye-icon:hover {
  opacity: 1;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem;
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-fast);
  border-radius: var(--border-radius);
  cursor: pointer;
}

.btn-primary {
  background-color: var(--primary-color);
  color: white;
  border: none;
}

.btn-primary:hover {
  background-color: var(--primary-color-dark);
}

.btn-outline {
  border: var(--border-width) solid var(--primary-color);
  color: var(--primary-color);
  background-color: transparent;
}

.btn-outline:hover {
  background-color: var(--primary-color);
  color: white;
}

@media (max-width: 480px) {
  .login-card {
    padding: 1.5rem;
    max-width: 90%;
  }

  .card-title {
    font-size: 1.5rem;
  }
}
</style>
