<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useCosmeticImages } from '@/composables/useCosmeticImages'
import { useCosmeticsStore } from '@/composables/useCosmeticsStore'
import { getUserIdFromLocalStorage } from '@/services/user'

const { getImagePath } = useCosmeticImages()
const { updateEquippedBorder, updateEquippedProfilePicture } = useCosmeticsStore()

type cosmetics = {
  rows: {
    id: number
    name: string
    price: number
    description: string
    image: string
    category: string
  }[]
}
const data = ref<cosmetics>({ rows: [] })
const loading = ref(false)

async function fetchPurchasedCosmetics(): Promise<void> {
  loading.value = true
  try {
    const userId = getUserIdFromLocalStorage()
    const response = await fetch(`http://localhost:8080/cosmetics/inventory?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
    })

    if (!response.ok) {
      console.error('Error:', response.status, response.statusText)
      return
    }

    const cosmetics = await response.json()
    data.value = { rows: cosmetics }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

async function equipCosmetic(cosmeticId: number): Promise<void> {
  try {
    const userId = getUserIdFromLocalStorage()
    const response = await fetch(
      `http://localhost:8080/cosmetics/equip/${cosmeticId}?userId=${userId}`,
      {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        credentials: 'include',
      },
    )

    if (response.ok) {
      const equippedCosmetic = await response.json()

      if (equippedCosmetic.category === 'BORDER') {
        updateEquippedBorder(equippedCosmetic.image)
      } else if (equippedCosmetic.category === 'PROFILE_PICTURE') {
        updateEquippedProfilePicture(equippedCosmetic.image)
      }

      await fetchPurchasedCosmetics()
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await fetchPurchasedCosmetics()
})
</script>

<template>
  <h1>Inventory</h1>
  <div class="inventorybox">
    <h2>Borders</h2>
    <div class="inventory-container">
      <div
        v-for="cosmetic in data.rows"
        :key="cosmetic.name"
        class="cosmetic-group"
        v-show="cosmetic.category === 'BORDER'"
      >
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{ cosmetic.name }}</label>
        <p class="cosmetic-description">{{ cosmetic.description }}</p>
        <p class="cosmetic-category">Type: {{ cosmetic.category.toLowerCase() }}</p>
        <button type="button" @click="equipCosmetic(cosmetic.id)">Equip</button>
      </div>
    </div>
  </div>
  <div class="inventorybox">
    <h2>Profile Icons</h2>
    <div class="inventory-container">
      <div
        v-for="cosmetic in data.rows"
        :key="cosmetic.name"
        class="cosmetic-group"
        v-show="cosmetic.category === 'PROFILE_PICTURE'"
      >
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{ cosmetic.name }}</label>
        <p class="cosmetic-description">{{ cosmetic.description }}</p>
        <p class="cosmetic-category">Type: {{ cosmetic.category.toLowerCase() }}</p>
        <button type="button" @click="equipCosmetic(cosmetic.id)">Equip</button>
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

.inventorybox {
  background-color: var(--background-primary);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  width: 1000px;
  margin: 2rem auto;
  border: 1px solid var(--border-color);
}

.inventory-container {
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
</style>
