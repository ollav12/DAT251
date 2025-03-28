<script lang="ts">
import { defineComponent } from 'vue'
import { useRouter } from 'vue-router'
import eye from '@/assets/eye.svg'
import eyeOff from '@/assets/eye-off.svg'

export default defineComponent({
  data() {
    return {
      firstName: '',
      lastName: '',
      username: '',
      email: '',
      password: '',
      errorMessage: '',
      errorFirstName: '',
      errorLastName: '',
      errorUsername: '',
      errorEmail: '',
      errorPassword: '',
      showPassword: false,
      eye,
      eyeOff,
    }
  },

  setup() {
    const router = useRouter()
    return { router }
  },

  methods: {
    togglePasswordVisibility() {
      this.showPassword = !this.showPassword
    },

    validateForm(): boolean {
      this.validateFirstName(this.firstName)
      this.validateLastName(this.lastName)
      this.validateUsername(this.username)
      this.validateEmail(this.email)
      this.validatePassword(this.password)

      return !(
        this.errorFirstName ||
        this.errorLastName ||
        this.errorUsername ||
        this.errorEmail ||
        this.errorPassword
      )
    },

    validateFirstName(val: string) {
      if (!/^[A-Z][a-zA-Z]{1,14}$/.test(val)) {
        this.errorFirstName = 'First name must be 2-15 letters, starting uppercase.'
      } else {
        this.errorFirstName = ''
      }
    },

    validateLastName(val: string) {
      if (!/^[A-Z][a-zA-Z]{1,14}$/.test(val)) {
        this.errorLastName = 'Last name must be 2-15 letters, starting uppercase.'
      } else {
        this.errorLastName = ''
      }
    },

    validateUsername(val: string) {
      if (!/^[a-zA-Z0-9]{2,15}$/.test(val)) {
        this.errorUsername = 'Username must be 2-15 alphanumeric characters.'
      } else {
        this.errorUsername = ''
      }
    },

    validateEmail(val: string) {
      if (!/^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/.test(val)) {
        this.errorEmail = 'Please provide a valid email address.'
      } else {
        this.errorEmail = ''
      }
    },

    validatePassword(val: string) {
      if (!/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).{8,}$/.test(val)) {
        this.errorPassword =
          'Password must be 8+ chars and include uppercase, lowercase, and a digit.'
      } else {
        this.errorPassword = ''
      }
    },

    async handleSubmit(e: Event) {
      e.preventDefault()
      if (!this.validateForm()) {
        return
      }
      try {
        const response = await fetch('http://localhost:8080/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            firstName: this.firstName,
            lastName: this.lastName,
            username: this.username,
            email: this.email,
            password: this.password,
          }),
        })
        const data = await response.json()
        if (!response.ok) {
          if (Array.isArray(data)) {
            this.errorMessage = data.join(' ')
          } else {
            this.errorMessage = data.message || 'An error occurred during signup.'
          }
          return
        }
        await this.router.push({ name: 'login' })
      } catch (error) {
        console.error('Error during signup:', error)
        this.errorMessage = 'An error occurred during signup'
      }
    },
  },

  watch: {
    firstName(newVal: string) {
      this.validateFirstName(newVal)
    },
    lastName(newVal: string) {
      this.validateLastName(newVal)
    },
    username(newVal: string) {
      this.validateUsername(newVal)
    },
    email(newVal: string) {
      this.validateEmail(newVal)
    },
    password(newVal: string) {
      this.validatePassword(newVal)
    },
  },
})
</script>

<template>
  <div class="login-box">
    <h1>Signup</h1>
    <form @submit="handleSubmit">
      <div class="input-group">
        <label for="firstName">First Name:</label>
        <input
          type="text"
          id="firstName"
          v-model="firstName"
          required
          placeholder="First Name"
          :class="{ 'input-valid': !errorFirstName && firstName, 'input-invalid': errorFirstName }"
        />
        <small v-if="errorFirstName" style="color: #dc3545">{{ errorFirstName }}</small>
      </div>
      <div class="input-group">
        <label for="lastName">Last Name:</label>
        <input
          type="text"
          id="lastName"
          v-model="lastName"
          required
          placeholder="Last Name"
          :class="{ 'input-valid': !errorLastName && lastName, 'input-invalid': errorLastName }"
        />
        <small v-if="errorLastName" style="color: #dc3545">{{ errorLastName }}</small>
      </div>
      <div class="input-group">
        <label for="username">Username:</label>
        <input
          type="text"
          id="username"
          v-model="username"
          required
          placeholder="Username"
          :class="{ 'input-valid': !errorUsername && username, 'input-invalid': errorUsername }"
        />
        <small v-if="errorUsername" style="color: #dc3545">{{ errorUsername }}</small>
      </div>
      <div class="input-group">
        <label for="email">Email:</label>
        <input
          type="email"
          id="email"
          v-model="email"
          required
          placeholder="Email"
          :class="{ 'input-valid': !errorEmail && email, 'input-invalid': errorEmail }"
        />
        <small v-if="errorEmail" style="color: #dc3545">{{ errorEmail }}</small>
      </div>
      <div class="input-group">
        <label for="password">Password:</label>
        <div class="password-wrapper">
          <input
            :type="showPassword ? 'text' : 'password'"
            id="password"
            v-model="password"
            required
            placeholder="Password"
            :class="{ 'input-valid': !errorPassword && password, 'input-invalid': errorPassword }"
          />
          <button type="button" class="toggle-password" @click="togglePasswordVisibility">
            <img
              :src="showPassword ? eye : eyeOff"
              alt="Toggle password visibility"
              class="eye-icon"
            />
          </button>
        </div>
        <small v-if="errorPassword" style="color: #dc3545">{{ errorPassword }}</small>
      </div>

      <button class="register-button" type="submit">Signup</button>
      <p v-if="errorMessage" style="color: #dc3545">{{ errorMessage }}</p>
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
  margin: 2rem auto 0;
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
  border: 2px solid #ccc;
  background-color: #ecf0f1;
  color: #1a1a1a;
  outline: none;
}

input {
  transition: border 0.3s ease-in-out;
}

small {
  font-weight: bold;
  display: block;
  margin-top: 0.3rem;
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

.input-valid {
  border: 2px solid #28a745;
}

.input-invalid {
  border: 2px solid #dc3545;
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
