<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { formatDuration } from '../util/format'

enum LeaderboardMetric {
  TOTAL_EMISSIONS = 'TOTAL_EMISSIONS',
  TOTAL_SAVED_EMISSIONS = 'TOTAL_SAVED_EMISSIONS',
  AVERAGE_CO2_PER_KILOMETER = 'AVERAGE_CO2E_PER_KILOMETER',
  TOTAL_DURATION_SECONDS = 'TOTAL_DURATION_SECONDS',
  TOTAL_DISTANCE_KILOMETERS = 'TOTAL_DISTANCE_KILOMETERS',
}

enum LeaderboardPeriod {
  LIFETIME = 'LIFETIME',
  PAST_YEAR = 'PAST_YEAR',
  PAST_MONTH = 'PAST_MONTH',
  PAST_WEEK = 'PAST_WEEK',
}

type Leaderboard = {
  metric?: LeaderboardMetric
  period?: LeaderboardPeriod
  rows: {
    username: string
    firstName: string
    lastName: string
    value: number
  }[]
}

const selectedMetric = ref<LeaderboardMetric>(LeaderboardMetric.TOTAL_EMISSIONS)
const selectedPeriod = ref<LeaderboardPeriod>(LeaderboardPeriod.LIFETIME)
const data = ref<Leaderboard>({ rows: [] })
const loading = ref(false)
watch(selectedMetric, () => {
  fetchLeaderboard()
})
watch(selectedPeriod, () => {
  fetchLeaderboard()
})

async function fetchLeaderboard(): Promise<void> {
  loading.value = true
  try {
    const response = await fetch(
      `http://localhost:8080/transport/leaderboard?metric=${selectedMetric.value}&period=${selectedPeriod.value}`,
    )
    const leaderboard = await response.json()
    data.value = leaderboard
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchLeaderboard()
})
</script>
<template>
  <main>
    <h1>Leaderboard</h1>
    <p>Compare your emissions with others, and see how you rank.</p>
    <div class="radio-group">
      <fieldset>
        <legend>Metric</legend>
        <div class="radio-options">
          <label class="radio-label">
            <input
              type="radio"
              name="metric"
              :value="LeaderboardMetric.TOTAL_EMISSIONS"
              checked
              v-model="selectedMetric"
            />
            <span class="radio-text">Total emissions</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="metric"
              :value="LeaderboardMetric.TOTAL_SAVED_EMISSIONS"
              v-model="selectedMetric"
            />
            <span class="radio-text">Total saved emissions</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="metric"
              :value="LeaderboardMetric.AVERAGE_CO2_PER_KILOMETER"
              v-model="selectedMetric"
            />
            <span class="radio-text">Average CO₂ per kilometer</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="metric"
              :value="LeaderboardMetric.TOTAL_DURATION_SECONDS"
              v-model="selectedMetric"
            />
            <span class="radio-text">Total duration</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="metric"
              :value="LeaderboardMetric.TOTAL_DISTANCE_KILOMETERS"
              v-model="selectedMetric"
            />
            <span class="radio-text">Total distance</span>
          </label>
        </div>
      </fieldset>
    </div>
    <div class="radio-group">
      <fieldset>
        <legend>Period</legend>
        <div class="radio-options">
          <label class="radio-label">
            <input
              type="radio"
              name="period"
              :value="LeaderboardPeriod.LIFETIME"
              v-model="selectedPeriod"
            />
            <span class="radio-text">Lifetime</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="period"
              :value="LeaderboardPeriod.PAST_YEAR"
              v-model="selectedPeriod"
            />
            <span class="radio-text">Last year</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="period"
              :value="LeaderboardPeriod.PAST_MONTH"
              v-model="selectedPeriod"
            />
            <span class="radio-text">Last month</span>
          </label>
          <label class="radio-label">
            <input
              type="radio"
              name="period"
              :value="LeaderboardPeriod.PAST_WEEK"
              v-model="selectedPeriod"
            />
            <span class="radio-text">Last week</span>
          </label>
        </div>
      </fieldset>
    </div>
    <table style="text-align: left">
      <thead>
        <tr>
          <th>Rank</th>
          <th>Name</th>
          <th v-if="selectedMetric === LeaderboardMetric.TOTAL_EMISSIONS">Total emissions</th>
          <th v-else-if="selectedMetric === LeaderboardMetric.AVERAGE_CO2_PER_KILOMETER">
            Average emissions
          </th>
          <th v-else-if="selectedMetric === LeaderboardMetric.TOTAL_DURATION_SECONDS">
            Total duration
          </th>
          <th v-else-if="selectedMetric === LeaderboardMetric.TOTAL_DISTANCE_KILOMETERS">
            Total distance
          </th>
          <th v-else-if="selectedMetric === LeaderboardMetric.TOTAL_SAVED_EMISSIONS">
            Total saved emissions
          </th>
          <th v-else>Value</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, i) in data.rows" :key="i">
          <td>{{ i + 1 }}</td>
          <td>{{ row.firstName }} {{ row.lastName }}</td>
          <td v-if="selectedMetric === LeaderboardMetric.TOTAL_EMISSIONS">
            {{ row.value.toFixed(2) }} kg CO2e
          </td>
          <td v-else-if="selectedMetric === LeaderboardMetric.AVERAGE_CO2_PER_KILOMETER">
            {{ row.value.toFixed(2) }} average CO2e/km
          </td>
          <td v-else-if="selectedMetric === LeaderboardMetric.TOTAL_DURATION_SECONDS">
            {{ formatDuration(row.value) }}
          </td>
          <td v-else-if="selectedMetric === LeaderboardMetric.TOTAL_DISTANCE_KILOMETERS">
            {{ row.value.toFixed(2) }} km
          </td>
          <td v-else-if="selectedMetric === LeaderboardMetric.TOTAL_SAVED_EMISSIONS">
            {{ row.value.toFixed(2) }} kg CO2e
          </td>
          <td v-else>{{ row.value.toFixed(2) }}</td>
        </tr>
      </tbody>
    </table>
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

.radio-group fieldset {
  border: 1px solid darkgrey;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.radio-group legend {
  font-weight: bold;
  padding: 0 8px;
  margin-bottom: 8px;
}

.radio-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.radio-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 16px;
  background-color: var(--color-background-soft);
  transition: all 0.2s ease;
}

.radio-label:hover {
  background-color: var(--color-background-mute);
}

.radio-label input[type='radio'] {
  margin-right: 8px;
}

.radio-label input[type='radio']:checked + .radio-text {
  font-weight: bold;
  color: #2b5797;
}
</style>
