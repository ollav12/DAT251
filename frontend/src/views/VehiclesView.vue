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
  default: boolean
}

type AddVehicle = {
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

async function submitVehicle(e: Event) {
  try {
    const make = (e.target as HTMLFormElement).make.value
    const model = (e.target as HTMLFormElement).model.value
    const type = (e.target as HTMLFormElement).type.value
    const year = parseInt((e.target as HTMLFormElement).year.value)
    const emissionsCO2ePerKm = parseFloat((e.target as HTMLFormElement).emissionsCO2ePerKm.value)

    const vehicle: AddVehicle = {
      make,
      model,
      type,
      year,
      emissionsCO2ePerKm,
    }

    const response = await fetch('http://localhost:8080/transport/vehicles', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(vehicle),
    })
    const data = await response.json()
    console.log('Added vehicle', data)
    fetchVehicles()
  } catch (error) {
    console.error(error)
  }
}

async function setDefaultVehicle(vehicle: Vehicle) {
  try {
    const response = await fetch(`http://localhost:8080/transport/vehicles/${vehicle.id}/default`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    const data = await response.json()
    console.log('Set default vehicle', data)
    fetchVehicles()
  } catch (error) {
    console.error(error)
  }
}

async function deleteVehicle(vehicleId: number) {
  try {
    const response = await fetch(`http://localhost:8080/transport/vehicles/${vehicleId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    const data = await response.json()
    console.log('Deleted vehicle', data)
    fetchVehicles()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchVehicles()
})
</script>

<template>
  <main>
    <h2>Vehicles</h2>
    <ul v-if="vehicles.length > 0" class="vehicle-list">
      <li v-for="vehicle in vehicles" :key="vehicle.id" class="vehicle-item">
        <h4>{{ vehicle.make }} {{ vehicle.model }} ({{ vehicle.year }})</h4>

        <p v-if="vehicle.type == 'CAR'">Car</p>
        <p v-else-if="vehicle.type == 'ELECTRIC_CAR'">Electric Car</p>
        <p v-else-if="vehicle.type == 'BICYCLE'">Bicycle</p>
        <p v-else-if="vehicle.type == 'ELECTRIC_BIKE'">Electric Bike</p>
        <p v-else-if="vehicle.type == 'ELECTRIC_SCOOTER'">Electric Scooter</p>
        <p v-else-if="vehicle.type == 'MOTORCYCLE'">Motorcycle</p>
        <p v-else-if="vehicle.type == 'ELECTRIC_MOTORCYCLE'">Electric Motorcycle</p>
        <p v-else>{{ vehicle.type }}</p>

        <p>{{ vehicle.emissionsCO2ePerKm }} grams CO2e/km</p>

        <p v-if="vehicle.default">Default</p>
        <button v-else @click="setDefaultVehicle(vehicle)">Set default</button>
        <button v-if="!vehicle.default" @click="deleteVehicle(vehicle.id)">Delete</button>
      </li>
    </ul>
    <h3>Add Vehicle</h3>
    <form @submit.prevent="submitVehicle">
      <label for="make">Make:</label>
      <input type="text" id="make" name="make" required />
      <label for="model">Model:</label>
      <input type="text" id="model" name="model" required />
      <label for="year">Year:</label>
      <input type="number" id="year" name="year" required />
      <label for="type">Type:</label>
      <select name="type" required>
        <option value="BICYCLE">Bicycle</option>
        <option value="ELECTRIC_BIKE">Electric Bike</option>
        <option value="ELECTRIC_SCOOTER">Electric Scooter</option>
        <option value="MOTORCYCLE">Motorcycle</option>
        <option value="ELECTRIC_MOTORCYCLE">Electric Motorcycle</option>
        <option value="CAR">Car</option>
        <option value="ELECTRIC_CAR">Electric Car</option>
      </select>
      <label for="emissionsCO2ePerKm">Emissions grams CO2e/km:</label>
      <input
        type="number"
        id="emissionsCO2ePerKm"
        name="emissionsCO2ePerKm"
        min="0"
        max="10000"
        required
      />
      <button type="submit">Add Vehicle</button>
    </form>
  </main>
</template>

<style scoped>
main {
  display: grid;
  max-width: 480px;
  margin: 0 auto;
  gap: 16px;
}

ul.vehicle-list {
  display: grid;

  grid-template-columns: repeat(var(--columns, 3), minmax(var(--min-width, 100px), 1fr));
  grid-gap: var(--gap, 10px);

  list-style-type: none;
  padding: 0;
  margin: 0;
  flex-wrap: wrap;

  list-style-type: none;
  padding: 0;
  max-width: 100%;
}

li.vehicle-item {
  background-color: var(--color-background-soft);
  filter: brightness(1.3);
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  padding: 16px 20px;
  margin-bottom: 16px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}
</style>
