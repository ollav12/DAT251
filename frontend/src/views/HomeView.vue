<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { debounce } from 'lodash-es'
import Transport from '../services/transport'
import type { Statistics, Vehicle, Trip } from '../services/transport'

const statistics = ref<Statistics | null>(null)
const statisticsLoading = ref(false)
const statisticsError = ref<Error | null>(null)

async function fetchStatistics() {
  try {
    statisticsLoading.value = true
    const data = await Transport.getStatistics()
    console.log('Fetched statistics:', data)
    statistics.value = data
  } catch (e) {
    console.error(e)
    statisticsError.value = e as Error
  } finally {
    statisticsLoading.value = false
  }
}

const hideAddTrip = ref(true)

function toggleAddTrip() {
  hideAddTrip.value = !hideAddTrip.value
}

const data = ref<Trip[]>([])
const loading = ref(true)
const error = ref<Error | null>(null)

const useVehicle = ref(false)

const vehicles = ref<Vehicle[]>([])
const selectedVehicle = ref<Vehicle['id'] | null>(null)

async function fetchVehicles() {
  const data = await Transport.listVehicles()
  for (const vehicle of data) {
    if (vehicle.default) {
      selectedVehicle.value = vehicle.id
    }
  }
  vehicles.value = data
}

async function submitTrip(e: Event) {
  const origin = (e.target as HTMLFormElement).origin.value
  const destination = (e.target as HTMLFormElement).destination.value
  const mode = (e.target as HTMLFormElement).mode?.value
  const vehicleId = (e.target as HTMLFormElement).vehicleId?.value
  try {
    const data = await Transport.createTrip({
      origin,
      destination,
      mode,
      vehicleId,
    })
    console.log(data)
    await fetchTrips()
  } catch (e) {
    error.value = e as Error
  }
}

async function fetchTrips() {
  try {
    loading.value = true
    const trips = await Transport.listTrips()
    data.value = trips
    console.log(data.value)
  } catch (e) {
    console.error(e)
    error.value = e as Error
  } finally {
    loading.value = false
  }
}

function formatDuration(seconds: number) {
  // Handle edge cases
  if (seconds === 0) return '0 seconds'
  if (isNaN(seconds) || seconds < 0) return 'Invalid duration'

  // Less than a minute
  if (seconds < 60) {
    return `${Math.round(seconds)} ${seconds === 1 ? 'second' : 'seconds'}`
  }

  // Less than an hour
  if (seconds < 3600) {
    const minutes = seconds / 60
    if (minutes === Math.floor(minutes)) {
      // Whole number of minutes
      return `${minutes} ${minutes === 1 ? 'minute' : 'minutes'}`
    } else {
      // Decimal minutes (rounded to 1 decimal place)
      return `${minutes.toFixed(1)} minutes`
    }
  }

  // Hours
  const hours = seconds / 3600
  if (hours === Math.floor(hours)) {
    // Whole number of hours
    return `${hours} ${hours === 1 ? 'hour' : 'hours'}`
  } else {
    // Decimal hours (rounded to 2 decimal places)
    return `${hours.toFixed(2)} hours`
  }
}

function generateUUID(): string {
  if (crypto && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID()
  }
  throw new Error('UUID generation not supported')
}

// Used for address completion API
const sessionToken = generateUUID()

const originQuery = ref<string>('')
const originOptions = ref<string[]>([])
async function fetchOriginSuggestions() {
  const data = await Transport.getAddressCompletion(originQuery.value, sessionToken)
  console.log(data)
  originOptions.value = data.map((location) => location)
}
const debouncedFetchOriginSuggestions = debounce(fetchOriginSuggestions, 300)

const destinationQuery = ref<string>('')
const destinationOptions = ref<string[]>([])
async function fetchDestinationSuggestions() {
  const data = await Transport.getAddressCompletion(destinationQuery.value, sessionToken)
  destinationOptions.value = data.map((location) => location)
}
const debouncedFetchDestinationSuggestions = debounce(fetchDestinationSuggestions, 300)

onMounted(() => {
  fetchStatistics()
  fetchTrips()
  fetchVehicles()
})
</script>

<template>
  <main v-if="loading">Loading...</main>
  <main v-else-if="error">Error: {{ error.message }}</main>
  <main>
    <section class="stats">
      <p class="emissions-display">
        <span class="value">{{ statistics?.totalEmissionsCO2eKg?.toFixed(2) }}</span
        >{{ ' ' }}
        <span class="unit"> kg CO2e</span>
      </p>
      <p>Total emitted</p>
      <!-- money saved -->
      <!-- emissions saved -->
      <!-- trend -->
    </section>

    <section v-if="hideAddTrip">
      <button @click="toggleAddTrip">Add new trip</button>
    </section>
    <section v-else class="add-trip">
      <h3>Add new trip</h3>
      <form @submit.prevent="submitTrip" style="display: flex; flex-direction: column; gap: 10px">
        <label for="origin">Origin</label>
        <input
          name="origin"
          placeholder="Origin"
          list="origin-autocomplete"
          v-model="originQuery"
          @input="debouncedFetchOriginSuggestions"
          required
        />
        <datalist id="origin-autocomplete">
          <option
            v-for="suggestion in originOptions"
            :key="suggestion"
            :value="suggestion"
          ></option>
        </datalist>
        <label for="destination">Destination</label>
        <input
          name="destination"
          placeholder="Destination"
          list="destination-autocomplete"
          v-model="destinationQuery"
          @input="debouncedFetchDestinationSuggestions"
          required
        />
        <datalist id="destination-autocomplete">
          <option
            v-for="suggestion in destinationOptions"
            :key="suggestion"
            :value="suggestion"
          ></option>
        </datalist>

        <div style="display: flex; align-items: center; gap: 10px">
          <input type="checkbox" id="useVehicle" v-model="useVehicle" />
          <label for="useVehicle">Use personal vehicle</label>
        </div>
        <select v-if="!useVehicle" name="mode" id="mode">
          <option value="walking" selected>Walking</option>
          <option value="bicycling">Bicycling</option>
          <option value="transit">Transit</option>
          <option value="driving">Driving</option>
        </select>
        <div v-else>
          <a href="/vehicles">Manage Vehicles</a>
          <select name="vehicleId" id="vehicleId" v-model="selectedVehicle">
            <option value="" disabled>Select a vehicle</option>
            <option v-for="vehicle in vehicles" :key="vehicle.id" :value="vehicle.id">
              {{ vehicle.make }} {{ vehicle.model }} ({{ vehicle.year }})
            </option>
          </select>
        </div>

        <button type="submit">Add trip</button>
        <button @click="toggleAddTrip" class="secondary">Cancel</button>
      </form>
      <!-- TODO: make it possible to estimate before adding -->
    </section>

    <section class="recent-trips">
      <h3>Recent trips</h3>
      <p>Total trips: {{ statistics?.totalTrips || 0 }}</p>
      <ul v-if="data" class="trip-list">
        <li v-for="trip in data" :key="trip.id" class="trip-item">
          <div>
            <h4 v-if="trip.travelMode === 'driving'">Drive</h4>
            <h4 v-else-if="trip.travelMode === 'walking'">Walk</h4>
            <h4 v-else-if="trip.travelMode === 'bicycling'">Bike</h4>
            <h4 v-else-if="trip.travelMode === 'transit'">Transit</h4>
            <h4 v-else>Trip</h4>
            <p>{{ trip.origin }} to {{ trip.destination }}</p>
            <p>{{ trip.totalDistanceKm.toFixed(2) }} kilometers</p>
            <p>{{ formatDuration(trip.totalDurationSeconds) }}</p>
          </div>
          <div>
            <p>
              <span class="value">{{ trip.totalEmissionsCO2eKg.toFixed(2) }}</span>
              <span class="unit">kg CO2e</span>
            </p>
            <p>Saved {{ trip.savedEmissionsCO2eKg.toFixed(2) }} kg CO2e</p>
          </div>
        </li>
      </ul>
      <div v-else>No trips found.</div>
    </section>
  </main>
</template>

<style scoped>
main {
  display: grid;
  /* grid-template-rows: auto; */
  max-width: 480px;
  margin: 0 auto;
  gap: 16px;
}

form {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

button {
  padding: 10px 20px;
  font-size: 1em;
  border: none;
  border-radius: 5px;
  background-color: #007bff;
  color: #fff;
  cursor: pointer;
}
button.secondary {
  background-color: #6c757d;
  color: #fff;
}

button:hover {
  background-color: #0056b3;
}

select {
  padding: 10px 20px;
  font-size: 1em;
  border: none;
  border-radius: 2px;
  background-color: #fff;
  color: #000;
  cursor: pointer;
}

select:hover {
  background-color: #f0f0f0;
}

section {
  display: flex;
  flex-direction: column;

  gap: 16px;
}

.stats {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.stats .value {
  font-size: 3em;
}

.stats .unit {
  font-size: 1em;
  font-weight: bold;
}

ul.trip-list {
  list-style-type: none;
  padding: 0;
  max-width: 100%;
}

li.trip-item {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: start;
  flex-wrap: nowrap;
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

li.trip-item :nth-child(1) {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: start;
}

li.trip-item :nth-child(2) {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: end;
}

li.trip-item .value {
  font-size: 2em;
}

li.trip-item .unit {
  font-size: 1em;
  font-weight: bold;
}
</style>
