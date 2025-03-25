<script setup lang="ts">
import { ref, onMounted } from 'vue';
import CanvasJS from '@canvasjs/charts';
import { listUserTrips } from '../services/transport';
const chartContainer = ref(null);

const options = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "CO2e Emission" },
  axisY: { title: "CO2e Emission in kg", suffix: " kg" },
  data: [{
    type: "line",
    xValueFormatString: "MMM DD, YYYY",
    yValueFormatString: "$#,###.00",
    markerSize: 0,
    dataPoints: [
      { x: new Date("2020-01-01"), y: 8163.692383 },
      { x: new Date("2020-01-08"), y: 8827.764648 },
      { x: new Date("2020-01-15"), y: 8745.894531 },
      { x: new Date("2020-01-22"), y: 9358.589844 },
      { x: new Date("2020-01-29"), y: 9180.962891 },
      { x: new Date("2020-02-05"), y: 10208.23633 },
      { x: new Date("2020-02-12"), y: 10141.99609 },
      { x: new Date("2020-02-19"), y: 9341.705078 },
      { x: new Date("2020-02-26"), y: 8787.786133 },
      { x: new Date("2020-03-04"), y: 7909.729492 },
      { x: new Date("2020-03-11"), y: 5225.629395 },
      { x: new Date("2020-03-18"), y: 6734.803711 },
      { x: new Date("2020-03-25"), y: 6438.644531 },
      { x: new Date("2020-04-01"), y: 7176.414551 },
      { x: new Date("2020-04-08"), y: 6842.427734 },
      { x: new Date("2020-04-15"), y: 6880.323242 },
      { x: new Date("2020-04-22"), y: 7807.058594 },
      { x: new Date("2020-04-29"), y: 9003.070313 },
      { x: new Date("2020-05-06"), y: 8804.477539 },
      { x: new Date("2020-05-13"), y: 9729.038086 },
      { x: new Date("2020-05-20"), y: 8835.052734 },
      { x: new Date("2020-05-27"), y: 9529.803711 },
      { x: new Date("2020-06-03"), y: 9795.700195 },
      { x: new Date("2020-06-10"), y: 9538.024414 },
      { x: new Date("2020-06-17"), y: 9629.658203 },
      { x: new Date("2020-06-24"), y: 9137.993164 },
      { x: new Date("2020-07-01"), y: 9252.277344 },
      { x: new Date("2020-07-08"), y: 9243.213867 },
      { x: new Date("2020-07-15"), y: 9374.887695 },
      { x: new Date("2020-07-22"), y: 10912.82324 },
      { x: new Date("2020-07-29"), y: 11205.89258 },
      { x: new Date("2020-08-05"), y: 11410.52539 },
      { x: new Date("2020-08-12"), y: 11991.2334 },
      { x: new Date("2020-08-19"), y: 11366.13477 },
      { x: new Date("2020-08-26"), y: 11970.47852 },
      { x: new Date("2020-09-02"), y: 10131.5166 },
      { x: new Date("2020-09-09"), y: 10796.95117 },
      { x: new Date("2020-09-16"), y: 10538.45996 },
      { x: new Date("2020-09-23"), y: 10844.64063 },
      { x: new Date("2020-09-30"), y: 10604.40625 },
      { x: new Date("2020-10-07"), y: 11425.89941 },
      { x: new Date("2020-10-14"), y: 11916.33496 },
      { x: new Date("2020-10-21"), y: 13654.21875 },
      { x: new Date("2020-10-28"), y: 13950.30078 },
      { x: new Date("2020-11-04"), y: 15290.90234 },
      { x: new Date("2020-11-11"), y: 17645.40625 },
      { x: new Date("2020-11-18"), y: 19107.46484 },
      { x: new Date("2020-11-25"), y: 18802.99805 },
      { x: new Date("2020-12-02"), y: 18321.14453 },
      { x: new Date("2020-12-09"), y: 19417.07617 },
      { x: new Date("2020-12-16"), y: 23783.0293 },
      { x: new Date("2020-12-23"), y: 27362.4375 },
      { x: new Date("2020-12-30"), y: 29001.7207 }
    ]
  }]
};

onMounted(async () => {
   try {


  //TODO replace with actual user ID or get from auth state
  //TODO console log the trips to see if they are being fetched correctly


    const userId = localStorage.getItem("userId");
    console.log("userId: ", userId);
    const trips = await transportService.listUserTrips(userId);
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
