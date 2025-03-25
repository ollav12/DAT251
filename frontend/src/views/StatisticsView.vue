<script setup lang="ts">
import { ref, onMounted } from 'vue';
import CanvasJS from '@canvasjs/charts';
import TransportService from '../services/transport'

const chartContainer = ref(null);

const options = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "CO2e Emission" },
  axisY: { title: "CO2e Emission in kgCO2e", suffix: " kgCO2e" },
  data: [{
    type: "line",
    xValueFormatString: "MMM DD, YYYY",
    yValueFormatString: "#,###.00kgCO2e",
    markerSize: 0,
    dataPoints: [
     //Default data?
    ]
  }]
};

onMounted(async () => {
   try {


  //TODO replace with actual user ID or get from auth state
  //TODO console log the trips to see if they are being fetched correctly


    const userId = localStorage.getItem("userId");
    console.log("userId: ", userId);
    const trips = await TransportService.listUserTrips(userId);
    console.log("trips: ", trips);

    // Transform trips into chart data points
    options.data[0].dataPoints = trips.map(trip => ({
      x: new Date(trip.createdAt), // Assuming id can be parsed as date, modify if needed
      y: trip.totalEmissionsCO2eKg
    }));

  const chart = new CanvasJS.Chart(chartContainer.value, options);
  chart.render();

  } catch (error) {
    console.error("Failed to load trip data ",  error);
  }

});
</script>

<template>
  <h1>Statistics</h1>
  <div ref="chartContainer" style="width: 100%; height: 360px;"></div>
</template>

<style scoped>
</style>
