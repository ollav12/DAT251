<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { debounce } from 'lodash-es'
import { formatDuration } from '../util/format'
import Transport from '../services/transport'
import type { Statistics, Vehicle, Trip } from '../services/transport'
import {getUserIdFromLocalStorage} from "@/services/user.ts";
import Leaderboard from "@/views/Leaderboard.vue"

const statistics = ref<Statistics | null>(null)
const statisticsLoading = ref(false)
const statisticsError = ref<Error | null>(null)

const visibleTripsCount = ref(10)
  
const userChallenges = ref<{id: string; icon: string; name: string; progress: number}[]>([])

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
    const userIdNumber = Number.parseInt(userId, 10)
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
  <main v-if="loading" class="loading-container">
    <div class="loader" aria-label="Loading content"></div>
    <p class="text-center text-muted mt-3">Loading your dashboard...</p>
  </main>
  <main v-else-if="error" class="error-container">
    <div class="alert alert-danger">
      <strong>Error:</strong> {{ error.message }}
    </div>
  </main>
  <main v-else>
    <div class="dashboard-container">
      <section class="stats-section">
        <div class="stat-card">
          <div class="stat-header">
            <span class="stat-title">Total CO₂ Emitted</span>
          </div>
          <div class="stat-value">
            {{ statistics?.totalEmissionsCO2eKg?.toFixed(2) || '0.00' }}
            <span class="stat-unit">kg CO₂e</span>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-header">
            <span class="stat-title">Total CO₂ Saved</span>
          </div>
          <div class="stat-value">
            {{ statistics?.totalEmissionsSavingsCO2eKg?.toFixed(2) || '0.00' }}
            <span class="stat-unit">kg CO₂e</span>
          </div>
        </div>
      </section>

      <section v-if="hideAddTrip" class="action-section">
        <button class="btn btn-primary" @click="toggleAddTrip">
          Add new trip
        </button>
        
        <div v-if="userChallenges && userChallenges.length > 0" class="active-challenges mt-4">
          <h3 class="section-title">Active Challenges</h3>
          <div class="challenges-container">
            <div v-for="challenge in userChallenges.slice(0, 2)" :key="challenge.id" class="challenge-card">
              <div class="challenge-details">
                <h4 class="challenge-title">{{ challenge.name }}</h4>
                <div class="challenge-progress">
                  <div class="progress-bar">
                    <div class="progress-fill" :style="{width: `${challenge.progress || 0}%`}"></div>
                  </div>
                  <span class="progress-text">{{ challenge.progress || 0 }}%</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
      <section v-else class="add-trip-section card">
        <div class="card-header">
          <h3>Add new trip</h3>
        </div>
        <form @submit.prevent="submitTrip" class="trip-form">
          <div class="form-group">
            <label for="origin" class="form-label">Origin</label>
            <input
              class="form-control"
              name="origin"
              placeholder="Enter starting point"
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
          </div>
          
          <div class="form-group">
            <label for="destination" class="form-label">Destination</label>
            <input
              class="form-control"
              name="destination"
              placeholder="Enter destination"
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
          </div>

          <div class="form-group checkbox-group">
            <div class="checkbox-wrapper">
              <input type="checkbox" id="useVehicle" v-model="useVehicle" class="checkbox-input" />
              <label for="useVehicle" class="checkbox-label">Use personal vehicle</label>
            </div>
          </div>
          
          <div class="form-group" v-if="!useVehicle">
            <label for="mode" class="form-label">Transport Mode</label>
            <select name="mode" id="mode" class="form-control">
              <option value="walking" selected>Walking</option>
              <option value="bicycling">Bicycling</option>
              <option value="transit">Transit</option>
              <option value="driving">Driving</option>
            </select>
          </div>
          
          <div class="form-group" v-else>
            <div class="d-flex justify-content-between align-items-center mb-2">
              <label for="vehicleId" class="form-label mb-0">Select Vehicle</label>
              <a href="/vehicles" class="btn-link">Manage Vehicles</a>
            </div>
            <select name="vehicleId" id="vehicleId" v-model="selectedVehicle" class="form-control">
              <option value="" disabled>Select a vehicle</option>
              <option v-for="vehicle in vehicles" :key="vehicle.id" :value="vehicle.id">
                {{ vehicle.make }} {{ vehicle.model }} ({{ vehicle.year }})
              </option>
            </select>
          </div>

          <div class="form-buttons">
            <button type="submit" class="btn btn-primary">
              Add trip
            </button>
            <button @click="toggleAddTrip" type="button" class="btn btn-secondary">
              Cancel
            </button>
          </div>
        </form>
      </section>

      <section class="trips-section">
        <div class="section-header">
          <h3>Recent Trips</h3>
          <span class="badge badge-primary">{{ data.length }} Total</span>
        </div>
        
        <div v-if="data && data.length > 0" class="trips-container">
          <div v-for="trip in data.slice(0, visibleTripsCount)" :key="trip.id" class="trip-card">
            <div class="trip-header">
              <div class="trip-mode">
                <span class="mode-icon" v-if="trip.travelMode === 'driving'">🚗</span>
                <span class="mode-icon" v-else-if="trip.travelMode === 'walking'">🚶</span>
                <span class="mode-icon" v-else-if="trip.travelMode === 'bicycling'">🚲</span>
                <span class="mode-icon" v-else-if="trip.travelMode === 'transit'">🚌</span>
                <span class="mode-icon" v-else>🧭</span>
                
                <h4 v-if="trip.travelMode === 'driving'" class="mode-title">Drive</h4>
                <h4 v-else-if="trip.travelMode === 'walking'" class="mode-title">Walk</h4>
                <h4 v-else-if="trip.travelMode === 'bicycling'" class="mode-title">Bike</h4>
                <h4 v-else-if="trip.travelMode === 'transit'" class="mode-title">Transit</h4>
                <h4 v-else class="mode-title">Trip</h4>
              </div>
              
              <div class="trip-emissions">
                <div class="trip-co2">
                  {{ trip.totalEmissionsCO2eKg.toFixed(2) }}
                  <span class="trip-unit">kg CO₂e</span>
                </div>
                <div class="trip-savings text-success">
                  <span class="savings-icon">↓</span> {{ trip.savedEmissionsCO2eKg.toFixed(2) }} kg saved
                </div>
              </div>
            </div>
            
            <div class="trip-details">
              <div class="trip-route">
                <div class="origin-dest">{{ trip.origin }} → {{ trip.destination }}</div>
              </div>
              
              <div class="trip-stats">
                <div class="stat-item">
                  <span class="stat-icon">📏</span>
                  <span class="stat-value">{{ trip.totalDistanceKm.toFixed(2) }} km</span>
                </div>
                <div class="stat-item">
                  <span class="stat-icon">⏱️</span>
                  <span class="stat-value">{{ formatDuration(trip.totalDurationSeconds) }}</span>
                </div>
              </div>
              
              <div v-if="trip.vehicle" class="trip-vehicle">
                <span class="vehicle-icon">🚘</span>
                {{ trip.vehicle.make }} {{ trip.vehicle.model }} ({{ trip.vehicle.year }})
              </div>
            </div>
          </div>
          
          <button
            v-if="data && visibleTripsCount < data.length"
            @click="showMoreTrips"
            class="btn btn-outline load-more-btn"
          >
            <i class="icon">⤵️</i> Show more trips
          </button>
        </div>
        
        <div v-else-if="data && data.length === 0" class="empty-state">
          <div class="empty-icon">🔎</div>
          <p>No trips found. Add your first trip to start tracking your carbon footprint.</p>
        </div>
      </section>
    </div>
    
    <aside class="leaderboard-sidebar">
      <Leaderboard :limit="5" :show-view-more="true" />
    </aside>
  </main>
</template>

<style scoped>
main {
  position: relative;
  max-width: 100%;
  margin: 0 auto;
  padding: var(--spacing-md);
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-lg);
}

.dashboard-container {
  flex: 1;
  min-width: 480px;
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.loading-container,
.error-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
  width: 100%;
}

.loader {
  font-size: var(--font-size-lg);
  color: var(--primary-color);
  position: relative;
}

.loader::after {
  content: '';
  animation: loading 1.5s infinite linear;
  display: inline-block;
}

@keyframes loading {
  0% { content: '.'; }
  33% { content: '..'; }
  66% { content: '...'; }
}

.alert {
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  width: 100%;
}

.alert-danger {
  background-color: var(--accent-color-light);
  color: var(--danger-color);
  border: 1px solid var(--danger-color);
}

/* Stats Section */
.stats-section {
  display: flex;
  justify-content: space-between;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.stat-card {
  background-color: var(--background-primary);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-medium);
  padding: var(--spacing-lg);
  display: flex;
  flex-direction: column;
  transition: all var(--transition-base);
  position: relative;
  overflow: hidden;
  border: var(--border-width) solid var(--border-color);
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-large);
}

.stat-card {
  border-left: 3px solid var(--primary-color);
}

.stat-info {
  margin-top: var(--spacing-md);
  font-size: var(--font-size-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.stat-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);
}

.stat-icon {
  font-size: var(--font-size-lg);
}

.stat-title {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--primary-color);
  text-align: center;
}

.trip-co2 {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--primary-color);
}

.stat-unit {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
  margin-left: var(--spacing-xs);
}

/* Add Trip Section */
.action-section {
  display: flex;
  justify-content: center;
  margin: var(--spacing-md) 0;
}

.add-trip-section {
  margin-bottom: var(--spacing-lg);
  grid-column: 1 / -1;
}

.card-header {
  margin-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  padding-bottom: var(--spacing-md);
}

.trip-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.checkbox-input {
  width: auto;
  margin-right: var(--spacing-xs);
}

.checkbox-label {
  margin: 0;
}

.form-buttons {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-xs);
}

/* Trips Section */
.trips-section {
  margin-bottom: var(--spacing-lg);
  grid-column: 1 / -1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 1rem;
  border-bottom: var(--border-width) solid var(--border-color);
}

.trips-container {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.trip-card {
  background-color: var(--background-primary);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow-medium);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  transition: transform var(--transition-base);
  border: var(--border-width) solid var(--border-color);
}

.trip-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-large);
}

.trip-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  border-bottom: var(--border-width) solid var(--border-color);
  padding-bottom: 1rem;
}

.trip-mode {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.mode-icon {
  font-size: 1.25rem;
  color: var(--primary-color);
}

.mode-title {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
}

.trip-emissions {
  text-align: right;
}

.trip-co2 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.trip-unit {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.trip-savings {
  font-size: var(--font-size-sm);
  color: var(--success-color);
}

.savings-icon {
  font-size: var(--font-size-sm);
}

.trip-details {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.trip-route {
  margin-bottom: var(--spacing-sm);
}

.origin-dest {
  font-size: var(--font-size-base);
  color: var(--text-primary);
}

.trip-stats {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-sm);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.stat-icon {
  font-size: var(--font-size-base);
}

.trip-vehicle {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin-top: var(--spacing-xs);
}

.vehicle-icon {
  font-size: var(--font-size-base);
}

.load-more-btn {
  align-self: center;
  margin-top: var(--spacing-md);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl);
  background-color: var(--background-tertiary);
  border-radius: var(--border-radius-md);
  text-align: center;
}

.empty-icon {
  font-size: var(--font-size-display);
  margin-bottom: var(--spacing-md);
  color: var(--text-secondary);
}

/* Leaderboard sidebar */
.leaderboard-sidebar {
  width: 350px;
}

@media (max-width: 1200px) {
  .dashboard-container {
    max-width: 100%;
  }
  
  .leaderboard-sidebar {
    width: 100%;
    margin-top: var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  main {
    flex-direction: column;
  }
  
  .dashboard-container {
    min-width: 100%;
  }
  
  .stats-section {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  
  .trip-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }
  
  .trip-emissions {
    text-align: left;
    width: 100%;
  }
  
  .form-buttons {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .trip-stats {
    flex-direction: column;
    gap: var(--spacing-xs);
  }
}
</style>
