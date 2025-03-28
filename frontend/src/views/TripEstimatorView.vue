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
    console.log(data)
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
  <main>
    <h1>Trip Estimator</h1>
    <p>Estimate the carbon footprint of your trip for different modes of travel</p>
    <form @submit.prevent="estimateTrip">
      <fieldset>
        <legend>Trip Details</legend>
        <label for="origin">Origin:</label><br />
        <input type="text" id="origin" v-model="origin" required /><br />
        <label for="destination">Destination:</label><br />
        <input type="text" id="destination" v-model="destination" required /><br />
        <button type="submit">Estimate</button>
      </fieldset>
    </form>
  </main>
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

<style scoped>
main {
  display: grid;
  max-width: 480px;
  margin: 0 auto;
  gap: 16px;
  padding: 16px;
}

h1 {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  margin: 0;
  text-align: left;
}

p {
  font-size: 16px;
  color: #ddd;
  margin: 0;
  text-align: left;
}


form {
  display: grid;
  gap: 16px;
  padding: 0px;
}

fieldset {
  border: 1px solid darkgrey;
  border-radius: 8px;
  padding: 16px;
}

legend {
  font-weight: bold;
  padding: 0 8px;
}

label {
  font-weight: bold;
}

input[type="text"] {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid darkgrey;
  border-radius: 8px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

input[type="text"]:hover {
  background-color: #ddd;
}

button[type="submit"] {
  padding: 8px 12px;
  border: 1px solid darkgrey;
  border-radius: 16px;
  background-color: #2b5797;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 10px;
}

button[type="submit"]:hover {
  background-color: #ddd;
}

button[type="submit"]:active {
  background-color: #2b5797;
  color: white;
}

</style>
