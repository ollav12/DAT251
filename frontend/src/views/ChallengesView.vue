<script setup lang="ts">
import { onMounted, ref } from 'vue'

type Challenges = {
  rows: {
    challengeId: string
    challengeTitle: string
    description: string
    duration: number
    rewardPoints: number,
  }[]
}

const data = ref<Challenges>({ rows: [] })
const loading = ref(false)

async function fetchChallenges(): Promise<void> {
  loading.value = true
  try {
    const responseChallenge = await fetch('http://localhost:8080/challenges')
    const challenges = await responseChallenge.json()

    //const userId = localStorage.getItem("userId")
    //const responseChallengeStatus = await fetch(`http://localhost:8080/users/${userId}`)
    //const status = await responseChallengeStatus.json()
    
    data.value = { rows: challenges }

  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  await fetchChallenges()
})
</script>
<template>
  <main>
    <h1>Challenges</h1>
    <p>Complete challenges to recieve points that you can use to unlock cosmetics.</p>
    <table style="text-align: left">
      <thead>
        <tr>
          <th>Nr</th>
          <th>Title</th>
          <th>Description</th>
          <th>Points</th>
          <th>Status</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, i) in data.rows" :key="i">
          <td>{{ i + 1 }}</td>
          <td>{{ row.challengeId }} {{ row.challengeTitle }}</td>
          <td>{{ row.description }}</td>
          <td>{{ row.rewardPoints }} points</td>
          <td> {{  }}</td>
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
