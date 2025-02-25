<script setup lang="ts">
import { onMounted, ref } from 'vue'

type Trip = {
  id: string
  origin: string
  destination: string
  distance: number
}

const data = ref<Trip[]>([])
const loading = ref(true)
const error = ref<Error | null>(null)

async function fetchTrips() {
  try {
    loading.value = true
    const response = await fetch('http://localhost:8080/trips')
    data.value = await response.json()
  } catch (e) {
    error.value = e as Error
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchTrips()
})
</script>

<template>
  <h2>3 458 kg CO2e emitted</h2>

  <button>Add trip (TODO)</button>

  <div v-if="loading">Loading...</div>
  <div v-else-if="error">Error: {{ error.message }}</div>
  <div v-else>
    <h3>Trips</h3>
    <ul>
      <li v-for="trip in data" :key="trip.id">
        <p>From {{ trip.origin }}</p>
        <p>To {{ trip.destination }}</p>
        <p>Distance {{ trip.distance }} km</p>
        <pre>All data: {{ JSON.stringify(data) }}</pre>
      </li>
    </ul>
  </div>
</template>
