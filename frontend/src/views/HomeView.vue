<script setup lang="ts">
import { onMounted, ref } from 'vue'

type Trip = {
  id: string
  origin: string
  destination: string
  distance: number
  travelMode: string
  totalDistanceKm: number
  totalDurationSeconds: number
  totalEmissionsCO2eKg: number
  savedEmissionsCO2eKg: number
}

const data = ref<Trip[]>([])
const loading = ref(true)
const error = ref<Error | null>(null)

async function submitTrip(e: Event) {
  const origin = (e.target as HTMLFormElement).origin.value
  const destination = (e.target as HTMLFormElement).destination.value
  const mode = (e.target as HTMLFormElement).mode.value
  try {
    const response = await fetch('http://localhost:8080/trips?userId=1', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ origin, destination, mode }),
    })
    console.log(response)
    fetchTrips()
  } catch (e) {
    error.value = e as Error
  }
}

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

  <form @submit.prevent="submitTrip" style="display: flex; flex-direction: column; gap: 10px">
    <input name="origin" placeholder="Origin" required />
    <input name="destination" placeholder="Destination" required />
    <select name="mode" id="mode">
      <option value="walking" selected>Walking</option>
      <option value="bicycling">Bicycling</option>
      <option value="transit">Transit</option>
      <option value="driving">Driving</option>
    </select>
    <button type="submit">Add trip</button>
  </form>

  <div v-if="loading">Loading...</div>
  <div v-else-if="error">Error: {{ error.message }}</div>
  <div v-else>
    <h3>Trips</h3>
    <ul>
      <li v-for="trip in data" :key="trip.id">
        <p>From {{ trip.origin }}</p>
        <p>To {{ trip.destination }}</p>
        <p>Travel mode: {{ trip.travelMode }}</p>
        <p>Distance {{ trip.totalDistanceKm }} meters</p>
        <p>Duration {{ trip.totalDurationSeconds }} seconds</p>
        <p>Emissions {{ trip.totalEmissionsCO2eKg }} kg CO2e</p>
        <p>Saved emissions {{ trip.savedEmissionsCO2eKg }} kg CO2e</p>

        <pre>All data: {{ JSON.stringify(trip) }}</pre>
      </li>
    </ul>
  </div>
</template>
