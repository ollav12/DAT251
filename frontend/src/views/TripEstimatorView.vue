<script setup lang="ts">
import { ref } from 'vue'

type Alternative = {
  emissionsCO2eKg: number
  duration: string
  distance: number
}

const origin = ref('')
const destination = ref('')
const alternatives = ref<Record<string, Alternative>>({})

async function estimateTrip() {
  try {
    console.log('making request')
    const response = await fetch(
      `http://localhost:8080/transport/tripestimate?origin=${encodeURIComponent(origin.value)}&destination=${encodeURIComponent(destination.value)}`,
    )
    const data = await response.json()
    alternatives.value = data.alternatives
  } catch (error) {
    console.error(error)
  }
}
</script>

<!-- {
    "estimates": {
        "driving": {
            "duration": "PT4H9M25S",
            "distance": 267172,
            "emissionsCO2eKg": 30.679999999999996
        }
    }
} -->

<template>
  <h1>Trip Estimator</h1>
  <p>Estimate the carbon footprint of your trip for different modes of travel</p>
  <form @submit.prevent="estimateTrip">
    <label for="origin">Origin:</label><br />
    <input type="text" id="origin" v-model="origin" required /><br />
    <label for="destination">Destination:</label><br />
    <input type="text" id="destination" v-model="destination" required /><br />
    <button type="submit">Estimate</button>
  </form>
  <div v-if="Object.keys(alternatives).length > 0">
    <h2>Alternatives</h2>
    <ul>
      <li v-for="(alternative, index) in alternatives" :key="index">
        <p>Mode: {{ index }}</p>
        <p>Distance: {{ alternative.distance }} km</p>
        <p>Duration: {{ alternative.duration }}</p>
        <p>Carbon Footprint: {{ alternative.emissionsCO2eKg }} kg CO2</p>
      </li>
    </ul>
  </div>
</template>
