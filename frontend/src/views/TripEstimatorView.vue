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
  <div v-if="Object.keys(alternatives).length > 0" class="results-container">
    <h2>Trip Alternatives</h2>
    <div class="alternatives-grid">
      <div 
        v-for="(alternative, index) in alternatives" 
        :key="index"
        class="alternative-card"
      >
        <div class="alternative-header">
          <h3>{{ index.charAt(0).toUpperCase() + index.slice(1) }}</h3>
        </div>
        <div class="alternative-content">
          <div class="alternative-metric">
            <span class="metric-label">Distance:</span>
            <span class="metric-value">{{ (alternative.distance / 1000).toFixed(1) }} km</span>
          </div>
          <div class="alternative-metric">
            <span class="metric-label">Duration:</span>
            <span class="metric-value">{{ alternative.duration.replace('PT', '').replace('H', 'h ').replace('M', 'm ').replace('S', 's') }}</span>
          </div>
          <div class="alternative-metric">
            <span class="metric-label">Carbon Footprint:</span>
            <span class="metric-value">{{ alternative.emissionsCO2eKg.toFixed(2) }} kg CO₂</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
main {
  display: grid;
  max-width: 480px;
  margin: 0 auto;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
}

h1 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
  margin: 0;
  text-align: left;
}

p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
  text-align: left;
}


form {
  display: grid;
  gap: var(--spacing-md);
  padding: 0px;
}

fieldset {
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-md);
  background-color: var(--background-primary);
  box-shadow: var(--shadow-sm);
}

legend {
  font-weight: var(--font-weight-bold);
  padding: 0 var(--spacing-sm);
  color: var(--text-primary);
}

label {
  font-weight: var(--font-weight-medium);
  color: var(--text-secondary);
}

input[type="text"] {
  width: 100%;
  padding: var(--spacing-sm) var(--spacing-md);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-base);
  box-sizing: border-box;
  background-color: var(--background-secondary);
  color: var(--text-primary);
}

input[type="text"]:hover {
  background-color: var(--background-tertiary);
  border-color: var(--border-color-dark);
}

input[type="text"]:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(26, 188, 156, 0.25);
}

button[type="submit"] {
  padding: var(--spacing-sm) var(--spacing-md);
  border: none;
  border-radius: var(--border-radius-md);
  background-color: var(--primary-color);
  color: var(--text-light);
  font-weight: var(--font-weight-bold);
  cursor: pointer;
  transition: background-color var(--transition-base);
  margin-top: var(--spacing-sm);
}

button[type="submit"]:hover {
  background-color: var(--primary-color-dark);
}

button[type="submit"]:active {
  background-color: var(--primary-color-dark);
  transform: translateY(1px);
}

/* Results styling */
.results-container {
  max-width: 800px;
  margin: 2rem auto;
  padding: var(--spacing-md);
}

.results-container h2 {
  font-size: var(--font-size-xl);
  color: var(--text-primary);
  margin-bottom: var(--spacing-md);
  text-align: center;
}

.alternatives-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-md);
}

.alternative-card {
  background-color: var(--background-primary);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  transition: transform var(--transition-fast), box-shadow var(--transition-fast);
}

.alternative-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.alternative-header {
  background-color: var(--primary-color);
  color: var(--text-light);
  padding: var(--spacing-sm) var(--spacing-md);
  text-align: center;
}

.alternative-header h3 {
  margin: 0;
  font-size: var(--font-size-md);
}

.alternative-content {
  padding: var(--spacing-md);
}

.alternative-metric {
  display: flex;
  justify-content: space-between;
  margin-bottom: var(--spacing-sm);
  padding-bottom: var(--spacing-sm);
  border-bottom: 1px solid var(--border-color);
}

.alternative-metric:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.metric-label {
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
}

.metric-value {
  color: var(--text-primary);
  font-weight: var(--font-weight-bold);
}

@media (max-width: 600px) {
  .alternatives-grid {
    grid-template-columns: 1fr;
  }
}
</style>
