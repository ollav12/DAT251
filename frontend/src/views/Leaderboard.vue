<!-- eslint-disable vue/multi-word-component-names -->
<script setup lang="ts">
import { onMounted, ref, watch, inject, computed } from 'vue'
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

const props = defineProps<{
  limit?: number
  showViewMore?: boolean
}>()

const selectedMetric = ref<LeaderboardMetric>(LeaderboardMetric.TOTAL_EMISSIONS)
const selectedPeriod = ref<LeaderboardPeriod>(LeaderboardPeriod.LIFETIME)
const data = ref<Leaderboard>({ rows: [] })
const loading = ref(false)

const user = inject<{ username: string }>('user')

async function fetchLeaderboard(): Promise<void> {
  loading.value = true
  try {
    const response = await fetch(
        `http://localhost:8080/transport/leaderboard?metric=${selectedMetric.value}&period=${selectedPeriod.value}`
    )
    const leaderboard = await response.json()
    data.value = leaderboard
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

watch(selectedMetric, fetchLeaderboard)
watch(selectedPeriod, fetchLeaderboard)
onMounted(fetchLeaderboard)

const metricOptions = [
  { value: LeaderboardMetric.TOTAL_EMISSIONS, label: 'Total emissions' },
  { value: LeaderboardMetric.TOTAL_SAVED_EMISSIONS, label: 'Total saved emissions' },
  { value: LeaderboardMetric.AVERAGE_CO2_PER_KILOMETER, label: 'Average CO₂ per km' },
  { value: LeaderboardMetric.TOTAL_DURATION_SECONDS, label: 'Total duration' },
  { value: LeaderboardMetric.TOTAL_DISTANCE_KILOMETERS, label: 'Total distance' },
]

const periodOptions = [
  { value: LeaderboardPeriod.LIFETIME, label: 'Lifetime' },
  { value: LeaderboardPeriod.PAST_YEAR, label: 'Last year' },
  { value: LeaderboardPeriod.PAST_MONTH, label: 'Last month' },
  { value: LeaderboardPeriod.PAST_WEEK, label: 'Last week' },
]

function formatValue(value: number, metric: LeaderboardMetric): string {
  switch (metric) {
    case LeaderboardMetric.TOTAL_EMISSIONS:
      return `${value.toFixed(2)} kg CO2e`
    case LeaderboardMetric.TOTAL_SAVED_EMISSIONS:
      return `${value.toFixed(2)} kg CO2e`
    case LeaderboardMetric.AVERAGE_CO2_PER_KILOMETER:
      return `${value.toFixed(2)} CO2e/km`
    case LeaderboardMetric.TOTAL_DURATION_SECONDS:
      return formatDuration(value)
    case LeaderboardMetric.TOTAL_DISTANCE_KILOMETERS:
      return `${value.toFixed(2)} km`
    default:
      return value.toFixed(2)
  }
}

function getMetricLabel(metric: LeaderboardMetric): string {
  return metricOptions.find((opt) => opt.value === metric)?.label || 'Value'
}

const displayedRows = computed(() =>
    props.limit ? data.value.rows.slice(0, props.limit) : data.value.rows
)
</script>

<template>
  <div class="leaderboard-card">
    <h3>Leaderboard</h3>
    <div class="leaderboard-controls">
      <div class="select-group">
        <label for="metric">Metric</label>
        <select id="metric" v-model="selectedMetric">
          <option v-for="option in metricOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>
      <div class="select-group">
        <label for="period">Period</label>
        <select id="period" v-model="selectedPeriod">
          <option v-for="option in periodOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>
      </div>
    </div>
    <table v-if="!loading">
      <thead>
      <tr>
        <th>Rank</th>
        <th>Name</th>
        <th>{{ getMetricLabel(selectedMetric) }}</th>
      </tr>
      </thead>
      <tbody>
      <tr
          v-for="(row, i) in displayedRows"
          :key="i"
          :class="{ 'current-user': user && row.username === user.username }"
      >
        <td>{{ i + 1 }}</td>
        <td>{{ row.firstName }} {{ row.lastName }}</td>
        <td>{{ formatValue(row.value, selectedMetric) }}</td>
      </tr>
      </tbody>
    </table>
    <p v-else>Loading...</p>
    <div v-if="props.showViewMore && typeof props.limit === 'number' && data.rows.length > props.limit" class="view-more">
      <RouterLink to="/leaderboard">View more</RouterLink>
    </div>
  </div>
</template>

<style scoped>
.leaderboard-card {
  background-color: #1e2525;
  border: 1px solid #3d3d3d;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  color: white;
}

h3 {
  margin: 0 0 12px 0;
  font-size: 1.5em;
}

.leaderboard-controls {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
}

.select-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 48%;
}

.select-group label {
  font-size: 0.9em;
  color: #b0b0b0;
}

select {
  background-color: #3d3d3d;
  color: white;
  border: 1px solid #555;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 1em;
  cursor: pointer;
  transition: border-color 0.2s ease;
  appearance: none;
  background-image: url('data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSIyNCIgaGVpZ2h0PSIyNCIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJub25lIiBzdHJva2U9IndoaXRlIiBzdHJva2Utd2lkdGg9IjIiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIgc3Ryb2tlLWxpbmVqb2luPSJyb3VuZCI+PHBvbHlsaW5lIHBvaW50cz0iNiA5IDEyIDE1IDE4IDkiPjwvcG9seWxpbmU+PC9zdmc+');
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 16px;
}

select:hover,
select:focus {
  border-color: #2b5797;
  outline: none;
}

select option {
  background-color: #3d3d3d;
  color: white;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 10px 12px;
  text-align: left;
  font-size: 0.95em;
}

th {
  background-color: #2a2a2a;
  color: #b0b0b0;
  font-weight: bold;
  text-transform: uppercase;
  font-size: 0.85em;
}

tr:nth-child(even) {
  background-color: #252b2b;
}

tr.current-user {
  background-color: #1e3a5f;
}

.view-more {
  margin-top: 10px;
  text-align: right;
}

.view-more a {
  color: #007bff;
  text-decoration: none;
}

.view-more a:hover {
  text-decoration: underline;
}
</style>
