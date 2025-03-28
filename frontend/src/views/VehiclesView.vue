<script setup lang="ts">
import { onMounted, ref } from 'vue'
import Transport from '../services/transport'
import type { Vehicle } from '../services/transport'

const vehicles = ref<Vehicle[]>([])

async function fetchVehicles() {
  const data = await Transport.listVehicles()
  vehicles.value = data
}

async function submitVehicle(e: Event) {
  await Transport.createVehicle({
    make: (e.target as HTMLFormElement).make.value,
    model: (e.target as HTMLFormElement).model.value,
    type: (e.target as HTMLFormElement).type.value,
    year: parseInt((e.target as HTMLFormElement).year.value),
    emissionsCO2ePerKm: parseFloat((e.target as HTMLFormElement).emissionsCO2ePerKm.value),
  })
  await fetchVehicles()
}

async function setDefaultVehicle(vehicle: Vehicle) {
  await Transport.setDefaultVehicle(vehicle.id)
  await fetchVehicles()
}

async function deleteVehicle(vehicleId: number) {
  await Transport.deleteVehicle(vehicleId)
  await fetchVehicles()
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
    <fieldset>
    <legend>Add Vehicle</legend>
    <form @submit.prevent="submitVehicle" style="display: flex; flex-direction: column; gap: 10px">
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
    </fieldset>
  </main>
</template>

<style scoped>
main {
  display: grid;
  max-width: 480px;
  margin: 0 auto;
  gap: 16px;
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
  color: white;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
}

input[type="text"]:hover {
  background-color: #ddd;
}

input[type="text"]:focus {
  border-color: #2b5797;
  outline: none;
  background-color: #4e4e4e;
}

input[type="number"] {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid darkgrey;
  border-radius: 8px;
  color: white;
  transition: border-color 0.2s ease;
  box-sizing: border-box;
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

select {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid darkgrey;
  border-radius: 8px;
  color: white;
  background-color: #3d3d3d;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

select:hover {
  background-color: #4e4e4e;
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
