<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useCosmeticImages } from '@/composables/useCosmeticImages'
import { useCosmeticsStore } from '@/composables/useCosmeticsStore'
import { getUserIdFromLocalStorage } from '@/services/user'

const { getImagePath } = useCosmeticImages()
const { userPoints, fetchUserPoints, updatePoints } = useCosmeticsStore()
const errorMessage = ref('')
const showError = ref(false)
const ownedCosmetics = ref<string[]>([])

type cosmetics = {
  rows: {
    name: string
    price: number
    description: string
    image: string
    category: string
  }[]
}
const data = ref<cosmetics>({ rows: [] })
const loading = ref(false)

const canAfford = computed(() => (price: number) => {
  return userPoints.value >= price
})

const isOwned = computed(() => (name: string) => {
  return ownedCosmetics.value.includes(name)
})

async function fetchCosmetics(): Promise<void> {
  loading.value = true
  try {
    const response = await fetch('http://localhost:8080/cosmetics/shop')
    const cosmetics = await response.json()
    data.value = { rows: cosmetics }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function fetchInventory(): Promise<void> {
  try {
    const userId = getUserIdFromLocalStorage()
    const response = await fetch(`http://localhost:8080/cosmetics/inventory?userId=${userId}`, {
      credentials: 'include',
    })
    if (response.ok) {
      const inventory = await response.json()
      ownedCosmetics.value = inventory.map((item: any) => item.name)
    }
  } catch (error) {
    console.error(error)
  }
}

async function buyCosmetic(name: string, price: number): Promise<void> {
  if (userPoints.value < price) {
    errorMessage.value = `You don't have enough points to buy this item!`
    showError.value = true
    setTimeout(() => {
      showError.value = false
    }, 3000)
    return
  }

  try {
    const userId = getUserIdFromLocalStorage()
    const response = await fetch(
      `http://localhost:8080/cosmetics/purchaseCosmetics?userId=${userId}`,
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name }),
        credentials: 'include',
      },
    )
    if (response.ok) {
      // Update local points after successful purchase
      updatePoints(userPoints.value - price)
      await fetchCosmetics()
      await fetchInventory()
    } else {
      const error = await response.json()
      errorMessage.value = error.message || 'Failed to purchase cosmetic'
      showError.value = true
      setTimeout(() => {
        showError.value = false
      }, 3000)
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await fetchCosmetics()
  await fetchUserPoints()
  await fetchInventory()
})
</script>

<template>
  <h1>Shop</h1>

  <!-- Add error message display -->
  <div v-if="showError" class="error-message">
    {{ errorMessage }}
  </div>

  <div class="shopbox">
    <h2>Borders</h2>
    <div class="cosmetic-container">
      <div
        v-for="cosmetic in data.rows"
        :key="cosmetic.name"
        class="cosmetic-group"
        :class="{ unaffordable: !canAfford(cosmetic.price) }"
        v-show="cosmetic.category === 'BORDER' && !isOwned(cosmetic.name)"
      >
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{ cosmetic.name }}</label>
        <p class="cosmetic-description">{{ cosmetic.description }}</p>
        <p class="cosmetic-price" :class="{ 'price-unaffordable': !canAfford(cosmetic.price) }">
          {{ cosmetic.price }} points
        </p>
        <button
          type="button"
          @click="buyCosmetic(cosmetic.name, cosmetic.price)"
          :class="{ 'button-unaffordable': !canAfford(cosmetic.price) }"
        >
          {{ canAfford(cosmetic.price) ? 'Buy' : 'Not enough points' }}
        </button>
      </div>
    </div>
  </div>

  <!-- Update the Profile Icons section similarly -->
  <div class="shopbox">
    <h2>Profile Icons</h2>
    <div class="cosmetic-container">
      <div
        v-for="cosmetic in data.rows"
        :key="cosmetic.name"
        class="cosmetic-group"
        :class="{ unaffordable: !canAfford(cosmetic.price) }"
        v-show="cosmetic.category === 'PROFILE_PICTURE' && !isOwned(cosmetic.name)"
      >
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{ cosmetic.name }}</label>
        <p class="cosmetic-description">{{ cosmetic.description }}</p>
        <p class="cosmetic-price" :class="{ 'price-unaffordable': !canAfford(cosmetic.price) }">
          {{ cosmetic.price }} points
        </p>
        <button
          type="button"
          @click="buyCosmetic(cosmetic.name, cosmetic.price)"
          :class="{ 'button-unaffordable': !canAfford(cosmetic.price) }"
        >
          {{ canAfford(cosmetic.price) ? 'Buy' : 'Not enough points' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
h1 {
  color: var(--text-primary);
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
  font-size: var(--font-size-display);
  text-align: center;
}

h2 {
  color: var(--text-secondary);
  margin-left: 1rem;
  margin-bottom: 1.5rem;
  font-size: var(--font-size-xl);
  text-align: left;
}

.shopbox {
  background-color: var(--background-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  width: 1000px;
  margin: 2rem auto;
  border: 1px solid var(--border-color);
}

.cosmetic-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
  justify-content: center;
}

.cosmetic-group {
  background-color: var(--background-secondary);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  text-align: center;
  box-shadow: var(--shadow-sm);
  transition: transform var(--transition-fast);
  border: 1px solid var(--border-color);
}

.cosmetic-group:hover {
  transform: scale(1.05);
}

.cosmetic-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: var(--border-radius-md);
  margin-bottom: var(--spacing-sm);
}

label {
  display: block;
  color: var(--text-primary);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-sm);
}

.cosmetic-description {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  word-wrap: break-word;
  overflow-wrap: break-word;
  text-align: center;
  max-width: 180px;
  margin: 0 auto;
}

.cosmetic-price {
  color: var(--warning-color);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--spacing-sm);
}

button {
  width: 80%;
  padding: var(--spacing-sm);
  border: none;
  border-radius: var(--border-radius-sm);
  background-color: var(--primary-color);
  color: var(--text-light);
  font-size: var(--font-size-base);
  cursor: pointer;
  margin-top: var(--spacing-sm);
  transition: background-color var(--transition-fast);
}

button:hover {
  background-color: var(--primary-color-dark);
}

.error-message {
  background-color: var(--danger-color);
  color: var(--text-light);
  padding: 0.75rem;
  border-radius: var(--border-radius-sm);
  margin-bottom: var(--spacing-md);
  animation: fadeIn 0.3s;
  font-weight: var(--font-weight-bold);
  text-align: center;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.unaffordable {
  opacity: 0.7;
  position: relative;
}

.unaffordable::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.2);
  border-radius: var(--border-radius-md);
}

.price-unaffordable {
  color: var(--danger-color) !important;
}

.button-unaffordable {
  background-color: var(--medium-color) !important;
  cursor: not-allowed;
  font-size: var(--font-size-sm) !important;
}

.button-unaffordable:hover {
  background-color: var(--medium-color) !important;
}
</style>
