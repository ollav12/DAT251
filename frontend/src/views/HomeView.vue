<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { debounce } from 'lodash-es'
import { formatDuration } from '../util/format'
import Transport from '../services/transport'
import type { Statistics, Vehicle, Trip } from '../services/transport'
import {getUserIdFromLocalStorage, logout, getMe} from "@/services/user.ts";
import Leaderboard from "@/views/Leaderboard.vue"

const statistics = ref<Statistics | null>(null)
const statisticsLoading = ref(false)
const statisticsError = ref<Error | null>(null)

const visibleTripsCount = ref(2)

const userChallenges = ref([])

async function fetchUserChallenges(): Promise<void> {
  try {
    const userId = localStorage.getItem("userId")
    if (!userId) {
      throw new Error("User ID not found")
    }
    const response = await fetch(`http://localhost:8080/users/${userId}/challenges`)
    userChallenges.value = await response.json()
  } catch (error) {
    console.error("Error fetching user challenges:", error)
  }
}
function showMoreTrips() {
  visibleTripsCount.value += 5
}

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
    await fetchStatistics()
  } catch (e) {
    error.value = e as Error
  }
}

async function fetchTrips() {
  try {
    loading.value = true

    const userId = getUserIdFromLocalStorage()
    if(!userId) {
      console.error("No user ID found in local storage")
      error.value = new Error("User ID not found")
      loading.value = false
      return
    }
    const userIdNumber = parseInt(userId, 10)
    const trips = await Transport.listUserTrips(userIdNumber)
    data.value = trips
    console.log(data.value)
  } catch (e) {
    console.error(e)
    error.value = e as Error
  } finally {
    loading.value = false
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
  fetchUserChallenges()
})

</script>

<template>
  <main v-if="loading">Loading...</main>
  <main v-else-if="error">Error: {{ error.message }}</main>
  <main>
    <div class="main-content">
      <section class="stats">
        <div class="stat-item">
          <p class="emissions-display">
            <span class="value">{{ statistics?.totalEmissionsCO2eKg?.toFixed(2) }}</span>
            <span class="unit"> kg CO2e</span>
          </p>
          <p>Total emitted</p>
        </div>
        <div class="stat-item">
          <p class="emissions-display">
            <span class="value">{{ statistics?.totalEmissionsSavingsCO2eKg?.toFixed(2) }}</span>
            <span class="unit"> kg CO2e</span>
          </p>
          <p>Total saved</p>
        </div>
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
        <p>Total trips: {{ data.length }}</p>
        <ul v-if="data" class="trip-list">
          <li v-for="trip in data.slice(0, visibleTripsCount)" :key="trip.id" class="trip-item">
            <div>
              <h4 v-if="trip.travelMode === 'driving'">Drive</h4>
              <h4 v-else-if="trip.travelMode === 'walking'">Walk</h4>
              <h4 v-else-if="trip.travelMode === 'bicycling'">Bike</h4>
              <h4 v-else-if="trip.travelMode === 'transit'">Transit</h4>
              <h4 v-else>Trip</h4>
              <p>{{ trip.origin }} to {{ trip.destination }}</p>
              <p>{{ trip.totalDistanceKm.toFixed(2) }} kilometers</p>
              <p>{{ formatDuration(trip.totalDurationSeconds) }}</p>
              <p v-if="trip.vehicle">
                {{ trip.vehicle.make }} {{ trip.vehicle.model }} ({{ trip.vehicle.year }})
              </p>
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

        <!-- Vis mer knapp -->
        <button
          v-if="data && visibleTripsCount < data.length"
          @click="showMoreTrips"
          class="show-more-btn"
        >
          Show more
        </button>
        <div v-if="data && data.length === 0">No trips found.</div>
      </section>
    </div>
    <div class="leaderboard-sidebar">
      <Leaderboard :limit="5" :show-view-more="true" />
    </div>
  </main>
</template>

<style scoped>
main {
  position: relative;
  max-width: 100%;
  margin: 0 auto;
  padding: 16px;
}

.main-content {
  width: 480px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.leaderboard-sidebar {
  position: absolute;
  width: 350px;
  top: 16px;
  right: 0;
}

@media (max-width: 768px) {
  main {
    position: static;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  .leaderboard-sidebar {
    position: static;
    width: 100%;
    max-width: 480px;
    margin-top: 20px;
  }
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
  flex-direction: row;
  justify-content: center;
  gap: 60px;
  margin-bottom: 20px;
  margin-top: 10px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
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

.show-more-btn {
  margin-top: 10px;
  background-color: #4caf50;
  color: white;
  border: none;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}
.show-more-btn:hover {
  background-color: #45a049;
}

</style>
