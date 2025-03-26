<script setup lang="ts">
import { ref, onMounted } from 'vue';
import CanvasJS from '@canvasjs/charts';
import TransportService from '../services/transport'

const chartContainerDaily = ref(null);
const chartContainerMonthly = ref(null);
const chartContainerYearly = ref(null);


const optionsDaily = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "Daily CO2e Emission" },
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

const optionsMonthly = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "Monthly CO2e Emission" },
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

const optionsYearly = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "Yearly CO2e Emission" },
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


    const currentDate = new Date().getDate();
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();

    // Filter trips by current day
    const dailyTrips = trips.filter(trip => trip.createdAt && new Date(trip.createdAt).getDate() === currentDate && new Date(trip.createdAt).getMonth() === currentMonth && new Date(trip.createdAt).getFullYear() === currentYear);
    console.log("dailyTrips: ", dailyTrips);

    // Filter trips by current month
    const monthlyTrips = trips.filter(trip => trip.createdAt && new Date(trip.createdAt).getMonth() === currentMonth && new Date(trip.createdAt).getFullYear() === currentYear);
    console.log("monthlyTrips: ", monthlyTrips);

    // Filter trips by current year
    const yearlyTrips = trips.filter(trip => trip.createdAt && new Date(trip.createdAt).getFullYear() === currentYear);
    console.log("yearlyTrips: ", yearlyTrips);

    // Transform trips into chart data points
    optionsDaily.data[0].dataPoints = dailyTrips.map(trip => ({
      x: new Date(trip.createdAt),
      y: trip.totalEmissionsCO2eKg
    }));

     optionsMonthly.data[0].dataPoints = monthlyTrips.map(trip => ({
       x: new Date(trip.createdAt),
       y: trip.totalEmissionsCO2eKg
     }));

     optionsYearly.data[0].dataPoints = yearlyTrips.map(trip => ({
       x: new Date(trip.createdAt),
       y: trip.totalEmissionsCO2eKg
     }));

  const chartDaily = new CanvasJS.Chart(chartContainerDaily.value, optionsDaily);
  const chartMonthly = new CanvasJS.Chart(chartContainerMonthly.value, optionsMonthly);
  const chartYearly = new CanvasJS.Chart(chartContainerYearly.value, optionsYearly);

     chartDaily.render();
     chartMonthly.render();
     chartYearly.render();

  } catch (error) {
    console.error("Failed to load trip data ",  error);
  }

});
</script>

<template>
  <h1>Daily Emission</h1>
  <div ref="chartContainerDaily" style="width: 100%; height: 360px; margin-bottom: 20px; margin-top: 5px;"></div>
  <h1>Monthly Emission</h1>
  <div ref="chartContainerMonthly" style="width: 100%; height: 360px; margin-top: 5px; margin-bottom: 20px;"></div>
  <h1>Yearly Emission</h1>
  <div ref="chartContainerYearly" style="width: 100%; height: 360px; margin-top: 5px; margin-bottom: 20px;"></div>
</template>

<style scoped>
</style>
