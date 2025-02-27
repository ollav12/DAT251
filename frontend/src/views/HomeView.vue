<script setup lang="ts">
import { onMounted, ref } from 'vue'

type Statistics = {
  totalTrips: number
  totalDistanceKm: number
  totalDurationSeconds: number

  totalEmissionsCO2eKg: number
  totalEmissionsSavingsCO2eKg: number

  totalCostNOK: number
  totalSavingsNOK: number
}

const statistics = ref<Statistics | null>(null)
const statisticsLoading = ref(false)
const statisticsError = ref<Error | null>(null)

async function fetchStatistics() {
  try {
    statisticsLoading.value = true
    const response = await fetch('http://localhost:8080/transport/statistics?userId=1')
    const data = await response.json()
    console.log('Fetched statistics:', data)
    statistics.value = data
  } catch (e) {
    console.error(e)
    statisticsError.value = e as Error
  } finally {
    statisticsLoading.value = false
  }
}

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
    console.log(data.value)
  } catch (e) {
    console.error(e)
    error.value = e as Error
  } finally {
    loading.value = false
  }
}
//

onMounted(() => {
  fetchStatistics()
  fetchTrips()
})
</script>

<template>
  <main v-if="loading">Loading...</main>
  <main v-else-if="error">Error: {{ error.message }}</main>
  <main>
    <section class="stats">
      <p class="emissions-display">
        <span class="value">{{ statistics?.totalEmissionsCO2eKg?.toLocaleString('se-SE') }}</span
        >{{ ' ' }}
        <span class="unit"> kg CO2e</span>
      </p>
      <p>Total emitted</p>
      <!-- money saved -->
      <!-- emissions saved -->
      <!-- trend -->
    </section>

    <section class="add-trip">
      <h2>Add new trip</h2>
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
      <!-- TODO: make it possible to estimate before adding -->
    </section>

    <section class="recent-trips">
      <h3>Recent trips</h3>
      <p>Total trips: {{ statistics?.totalTrips || 0 }}</p>
      <ul v-if="data" class="trip-list">
        <li v-for="trip in data" :key="trip.id" class="trip-item">
          <p>{{ trip.origin }} to {{ trip.destination }}</p>
          <p>Travel mode: {{ trip.travelMode }}</p>
          <p>Distance {{ trip.totalDistanceKm }} meters</p>
          <p>Duration {{ trip.totalDurationSeconds }} seconds</p>
          <p>Emissions {{ trip.totalEmissionsCO2eKg }} kg CO2e</p>
          <p>Saved emissions {{ trip.savedEmissionsCO2eKg }} kg CO2e</p>

          <!-- <pre class="code">All data: {{ JSON.stringify(trip) }}</pre> -->
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
  max-width: 100%;
}

form {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
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

.trip-list {
  list-style-type: none;
  padding: 0;
  max-width: 100%;
}

.trip-item {
  border: 1px solid #ccc;
  padding: 10px;
  margin-bottom: 10px;
}

.code {
  background-color: #333;
  padding: 10px;
  border-radius: 5px;
  overflow: auto;
  max-width: 100%;
}
</style>
