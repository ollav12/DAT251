<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useCosmeticImages } from '@/composables/useCosmeticImages'
import { useCosmeticsStore } from '@/composables/useCosmeticsStore';

const { getImagePath } = useCosmeticImages();
const { updateEquippedBorder, updateEquippedProfilePicture } = useCosmeticsStore();

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
    const userId = localStorage.getItem('userId')
    const response = await fetch(`http://localhost:8080/cosmetics/inventory?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })

    if(!response.ok) {
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
    const userId = localStorage.getItem('userId')
    const response = await fetch(`http://localhost:8080/cosmetics/equip/${cosmeticId}?userId=${userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json'
      },
      credentials: 'include'
    })

    if(response.ok) {
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
      <div v-for="cosmetic in data.rows" :key="cosmetic.name" class="cosmetic-group" v-show="cosmetic.category === 'BORDER'">
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{cosmetic.name}}</label>
        <p class="cosmetic-description">{{cosmetic.description}}</p>
        <p class="cosmetic-category">Type: {{cosmetic.category.toLowerCase()}}</p>
        <button type="button" @click="equipCosmetic(cosmetic.id)">Equip</button>
      </div>
    </div>
  </div>
  <div class="inventorybox">
    <h2>Profile Icons</h2>
    <div class="inventory-container">
      <div v-for="cosmetic in data.rows" :key="cosmetic.name" class="cosmetic-group" v-show="cosmetic.category === 'PROFILE_PICTURE'">
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{cosmetic.name}}</label>
        <p class="cosmetic-description">{{cosmetic.description}}</p>
        <p class="cosmetic-category">Type: {{cosmetic.category.toLowerCase()}}</p>
        <button type="button" @click="equipCosmetic(cosmetic.id)">Equip</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
h1 {
  color: #ffffff;
  margin-top: 1.5rem;
  margin-bottom: 1.5rem;
  font-size: 2.5rem;
  text-align: center;
}

h2 {
  color: #b7b9bc;
  margin-left: 1rem;
  margin-bottom: 1.5rem;
  font-size: xx-large;
  text-align: left;
}

.inventorybox {
  background-color: #555555;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  width: 1000px;
  margin: 2rem auto;
}

.inventory-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  justify-content: center;
}

.cosmetic-group {
  background-color: #444;
  padding: 1rem;
  border-radius: 10px;
  text-align: center;
  box-shadow: 0px 2px 5px rgba(0, 0, 0, 0.2);
  transition: transform 0.2s;
}

.cosmetic-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 0.5rem;
}

label {
  display: block;
  color: #ecf0f1;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.cosmetic-description {
  color: #ddd;
  font-size: 0.9rem;
  word-wrap: break-word;
  overflow-wrap: break-word;
  text-align: center;
  max-width: 180px;
  margin: 0 auto;
}

button {
  width: 80%;
  padding: 0.5rem;
  border: none;
  border-radius: 5px;
  background-color: #1abc9c;
  color: white;
  font-size: 1rem;
  cursor: pointer;
  margin-top: 0.5rem;
  transition: background-color 0.2s;
}

button:hover {
  background-color: #16a085;
}
</style>
