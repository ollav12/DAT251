<script setup lang="ts">
import { onMounted, ref } from 'vue'

type Leaderboard = {
  rows: {
    username: string
    firstName: string
    lastName: string
    totalEmissions: number
  }[]
}

const data = ref<Leaderboard>({ rows: [] })
const loading = ref(false)

async function fetchLeaderboard(): Promise<void> {
  loading.value = true
  try {
    const response = await fetch('http://localhost:8080/transport/leaderboard')
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
    <table style="text-align: left">
      <thead>
        <tr>
          <th>Rank</th>
          <th>Name</th>
          <th>Total Emissions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, i) in data.rows" :key="i">
          <td>{{ i + 1 }}</td>
          <td>{{ row.firstName }} {{ row.lastName }}</td>
          <td>{{ row.totalEmissions.toFixed(2) }} kg CO2e</td>
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
</style>
