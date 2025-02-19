<script lang="ts">
export default {
  data() {
    return {
      origin: '',
      destination: '',
    }
  },
}
</script>

<template>
  <h1>Trip Estimator</h1>
  <p>Estimate the carbon footprint of your trip for different modes of travel</p>
  <form>
    <label for="origin">Origin:</label><br />
    <input type="text" id="origin" v-model="origin" required /><br />
    <label for="destination">Destination:</label><br />
    <input type="text" id="destination" v-model="destination" required /><br />
    <button type="submit">Estimate</button>
  </form>
</template>
