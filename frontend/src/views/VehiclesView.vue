<script setup lang="ts">
import { onMounted, ref } from 'vue'

type Vehicle = {
  id: number
  name: string
  make: string
  model: string
  type: string
  year: number
  emissionsCO2ePerKm: number
}

const vehicles = ref<Vehicle[]>([])

async function fetchVehicles() {
  try {
    const response = await fetch('http://localhost:8080/transport/vehicles')
    const data = await response.json()
    console.log('Fetched vehicles', data)
    vehicles.value = data
  } catch (error) {
    console.error(error)
  }
}

// async function submitVehicle(e: Event) {
//   try {
//     const response = await fetch('http://localhost:8080/transport/vehicles', {
//       method: 'POST',
//       headers: {
//         'Content-Type': 'application/json',
//       },
//       body: JSON.stringify(vehicle),
//     })
//     const data = await response.json()
//     console.log('Added vehicle', data)
//     vehicles.value.push(data)
//   } catch (error) {
//     console.error(error)
//   }
// }

onMounted(() => {
  fetchVehicles()
  console.log('VehiclesView mounted')
})
</script>

<template>
  <main>
    <h2>Vehicles</h2>
    <ul v-if="vehicles.length > 0">
      <li v-for="vehicle in vehicles" :key="vehicle.id">
        <p>
          {{ vehicle.make }} {{ vehicle.model }} ({{ vehicle.year }}) - {{ vehicle.type }} -
          {{ vehicle.emissionsCO2ePerKm }} CO2e/km
        </p>
      </li>
    </ul>
    <!-- <h3>Add Vehicle</h3>
    <form @submit.prevent="submitVehicle">
      <label for="make">Make:</label>
      <input type="text" id="make" v-model="vehicle.make" required />
      <label for="model">Model:</label>
      <input type="text" id="model" v-model="vehicle.model" required />
      <label for="year">Year:</label>
      <input type="number" id="year" v-model="vehicle.year" required />
      <label for="type">Type:</label>
      <input type="text" id="type" v-model="vehicle.type" required />
      <label for="emissionsCO2ePerKm">Emissions CO2e/km:</label>
      <input type="number" id="emissionsCO2ePerKm" v-model="vehicle.emissionsCO2ePerKm" required />
      <button type="submit">Add Vehicle</button>
    </form> -->
  </main>
</template>
