<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useCosmeticImages} from '@/composables/useCosmeticImages'


const { getImagePath } = useCosmeticImages();

type cosmetics = {
  rows: {
    name: string
    price: number
    description: string
    image: string
    category: string
  }[]
}
const data = ref<cosmetics>({rows: [] })
const loading = ref(false)

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

async function buyCosmetic(name: string): Promise<void> {
  try {
    const userId = localStorage.getItem('userId')
    const response = await fetch(`http://localhost:8080/cosmetics/purchaseCosmetics?userId=${userId}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ name }),
      credentials: 'include'
    })
    if (response.ok) {
      await fetchCosmetics()
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await fetchCosmetics()
})
</script>

<template>
  <h1>Shop</h1>
  <div class="shopbox">
    <h2>Borders</h2>
    <div class="cosmetic-container">
      <div v-for="cosmetic in data.rows" :key="cosmetic.name"  class="cosmetic-group" v-show="cosmetic.category === 'BORDER'">
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{cosmetic.name}}</label>
        <p class="cosmetic-description">{{cosmetic.description}}</p>
        <p class="cosmetic-price">{{cosmetic.price}} points</p>
        <button type="button" @click="buyCosmetic(cosmetic.name)">Buy</button>
      </div>
    </div>
  </div>
  <div class="shopbox">
    <h2>Profile Icons</h2>
    <div class="cosmetic-container">
      <div v-for="cosmetic in data.rows" :key="cosmetic.name"  class="cosmetic-group" v-show="cosmetic.category === 'PROFILE_PICTURE'">
        <img :src="getImagePath(cosmetic.image)" alt="cosmetic" class="cosmetic-image" />
        <label>{{cosmetic.name}}</label>
        <p class="cosmetic-description">{{cosmetic.description}}</p>
        <p class="cosmetic-price">{{cosmetic.price}} points</p>
        <button type="button" @click="buyCosmetic(cosmetic.name)">Buy</button>
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

.shopbox {
  background-color: #555555;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
  width: 1000px;
  margin: 2rem auto;
}

.cosmetic-container {
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

.cosmetic-group:hover {
  transform: scale(1.05);
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

.cosmetic-price {
  color: #ffcc00;
  font-weight: bold;
  margin-bottom: 0.5rem;
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
