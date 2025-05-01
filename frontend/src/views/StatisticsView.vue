<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue';
import CanvasJS from '@canvasjs/charts';
import TransportService, { type Trip } from '../services/transport'

const chartContainerDaily = ref(null);
const chartContainerMonthly = ref(null);
const chartContainerYearly = ref(null);

const trips = ref<Trip[]>([]);

type CanvasOptions = {
  theme: string;
  animationEnabled: boolean;
  animationDuration: number;
  title: { text: string };
  axisY: { title: string; suffix: string; stripLines: {
    value: number;
    label: string;
    }[] };
  data: {
    type: string;
    xValueFormatString: string;
    yValueFormatString: string;
    markerSize: number;
    dataPoints: {
      x: Date;
      y: number;
    }[];
  }[];
};

const optionsDaily: CanvasOptions = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "Daily CO2e Emission" },
  axisY: { title: "CO2e Emission in kgCO2e", suffix: " kgCO2e", stripLines: []

  },
  data: [{
    type: "line",
    xValueFormatString: "MMM DD, YYYY",
    yValueFormatString: "#,###.00kgCO2e",
    markerSize: 0,
    dataPoints: [
     //Default data?
    ],
  }]
};

const optionsMonthly: CanvasOptions = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "Monthly CO2e Emission" },
  axisY: { title: "CO2e Emission in kgCO2e", suffix: " kgCO2e", stripLines:[] },
  data: [{
    type: "line",
    xValueFormatString: "MMM DD, YYYY",
    yValueFormatString: "#,###.00kgCO2e",
    markerSize: 0,
    dataPoints: [
      //Default data?
    ],
  }]
};

const optionsYearly: CanvasOptions = {
  theme: "light2",
  animationEnabled: true,
  animationDuration: 3000,
  title: { text: "Yearly CO2e Emission" },
  axisY: { title: "CO2e Emission in kgCO2e", suffix: " kgCO2e", stripLines:[] },
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
    trips.value = await TransportService.listUserTrips(parseInt(userId || ""));
    console.log("trips: ", trips);


    const currentDate = new Date().getDate();
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();

    // Filter trips by current day
    const dailyTrips = trips.value.filter(trip => trip.createdAt && new Date(trip.createdAt).getDate() === currentDate && new Date(trip.createdAt).getMonth() === currentMonth && new Date(trip.createdAt).getFullYear() === currentYear);
    console.log("dailyTrips: ", dailyTrips);

    // Filter trips by current month
    const monthlyTrips = trips.value.filter(trip => trip.createdAt && new Date(trip.createdAt).getMonth() === currentMonth && new Date(trip.createdAt).getFullYear() === currentYear);
    console.log("monthlyTrips: ", monthlyTrips);

    // Filter trips by current year
    const yearlyTrips = trips.value.filter(trip => trip.createdAt && new Date(trip.createdAt).getFullYear() === currentYear);
    console.log("yearlyTrips: ", yearlyTrips);


     const calculateAverage = (trips: Trip[]) => {
       const totalEmissionsCO2eKg = trips.reduce((acc, trip) => acc + trip.totalEmissionsCO2eKg, 0);
       return totalEmissionsCO2eKg / trips.length;
     };

     const averageEmissionDaily = calculateAverage(dailyTrips);
     const averageEmissionMonthly = calculateAverage(monthlyTrips);
     const averageEmissionYearly = calculateAverage(yearlyTrips);


    // Transform trips into chart data points
    optionsDaily.data[0].dataPoints = dailyTrips.map(trip => ({
      x: new Date(trip.createdAt),
      y: trip.totalEmissionsCO2eKg
    }));


    optionsDaily.axisY.stripLines = [{
      value: averageEmissionDaily,
      label: "Average",
    }];

     optionsMonthly.data[0].dataPoints = monthlyTrips.map(trip => ({
       x: new Date(trip.createdAt),
       y: trip.totalEmissionsCO2eKg
     }));

      optionsMonthly.axisY.stripLines = [{
        value: averageEmissionMonthly,
        label: "Average",
      }];

     optionsYearly.data[0].dataPoints = yearlyTrips.map(trip => ({
       x: new Date(trip.createdAt),
       y: trip.totalEmissionsCO2eKg
     }));

      optionsYearly.axisY.stripLines = [{
        value: averageEmissionYearly,
        label: "Average",
      }];


      await nextTick(() => {
        renderCharts();
      });



  } catch (error) {
    console.error("Failed to load trip data ",  error);
  }

});

const renderCharts = () => {
  if (trips.value.length > 0) {

    if(chartContainerDaily.value) {
      const chartDaily = new CanvasJS.Chart(chartContainerDaily.value, optionsDaily);
      chartDaily.render();
    }

    if(chartContainerMonthly.value) {
      const chartMonthly = new CanvasJS.Chart(chartContainerMonthly.value, optionsMonthly);
      chartMonthly.render();

    }

    if(chartContainerYearly.value) {
      const chartYearly = new CanvasJS.Chart(chartContainerYearly.value, optionsYearly);
      chartYearly.render();
    }
  }
}

//TODO Don't display graphs if trips are empty

</script>

<template>
  <div class="container" >
  <div class="message" v-if="!trips || trips.length < 2">
    <h1>Please add at least two trips to see statistics</h1>
  </div>
  <div v-else>
    <div ref="chartContainerDaily" class="chart"></div>
    <div ref="chartContainerMonthly" class="chart"></div>
    <div ref="chartContainerYearly" class="chart"></div>
  </div>
  </div>
</template>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;


}
.chart {
  width: 80%;
  height: 70vh;
  margin: 40px auto;
}

.message {
  margin-top: 20px;
  text-align: center;
}


</style>
