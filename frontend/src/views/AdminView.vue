<script setup lang="ts">
import { onMounted, ref } from 'vue'
import AdminService from '../services/admin'
import type { Statistics } from '../services/admin'

const statistics = ref<Statistics>()

async function fetchStatistics() {
  const data = await AdminService.getStatistics()
  statistics.value = data
}

onMounted(async () => {
  fetchStatistics()
})
</script>
<template>
  <h1>Admin</h1>
  <div>
    <h2>Statistics</h2>
    <ul>
      <li>Total users: {{ statistics?.totalUsers }}</li>
      <li>Total trips: {{ statistics?.totalTrips }}</li>
      <li>Total trip distance: {{ statistics?.totalTripDistance.toFixed(2) }} km</li>
      <li>Total emissions: {{ statistics?.totalEmissionsCO2eKg.toFixed(2) }} CO2e kg</li>
    </ul>
  </div>
</template>
