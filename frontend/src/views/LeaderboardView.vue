<script setup lang="ts">
import Leaderboard from '@/views/Leaderboard.vue' // Adjust path as needed
</script>

<template>
  <main>
    <h1>Leaderboard</h1>
    <p>Compare your emissions with others, and see how you rank.</p>
    <Leaderboard />
  </main>
</template>

<style scoped>
main {
  max-width: 480px;
  margin: 0 auto;
  padding: 16px;
}
</style>
