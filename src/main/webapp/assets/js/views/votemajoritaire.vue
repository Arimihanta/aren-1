<template>
  <div class="box-container">
    <base-layout id="votelayout">
      <template v-slot:title>
        <h1>Vote majoritaire</h1>
      </template>
      <div class="space-top">
        <div id="chart">
          <apexchart
            type="bar"
            height="350"
            :options="chartOptions"
            :series="series"
          ></apexchart>
        </div>
      </div>
    </base-layout>
  </div>
</template>

<style lang="css" scoped>
.box-container {
  border: 1px solid #a1a3a1;
  border-radius: 10px;
  padding-top: 2em;
  width: 50%;
  margin: 2em auto;
}
.space-top {
  margin-top: 4em;
}
.block {
  display: block;
}
</style>

<script>
module.exports = {
  data() {
    return {
      series: [
        {
          name: "Marine Sprite",
          data: [44, 55, 41, 37, 22, 43, 21],
        },
        {
          name: "Striking Calf",
          data: [53, 32, 33, 52, 13, 43, 32],
        },
        {
          name: "Tank Picture",
          data: [12, 17, 11, 9, 15, 11, 20],
        },
        {
          name: "Bucket Slope",
          data: [9, 7, 5, 8, 6, 9, 4],
        },
        {
          name: "Reborn Kid",
          data: [25, 12, 19, 32, 25, 24, 10],
        },
      ],
      chartOptions: {
        chart: {
          type: "bar",
          height: 350,
          stacked: true,
        },
        plotOptions: {
          bar: {
            horizontal: true,
          },
        },
        stroke: {
          width: 1,
          colors: ["#fff"],
        },
        title: {
          text: "Fiction Books Sales",
        },
        xaxis: {
          categories: [2008, 2009, 2010, 2011, 2012, 2013, 2014],
          labels: {
            formatter: function (val) {
              return val + "K";
            },
          },
        },
        yaxis: {
          title: {
            text: undefined,
          },
        },
        tooltip: {
          y: {
            formatter: function (val) {
              return val + "K";
            },
          },
        },
        fill: {
          opacity: 1,
        },
        legend: {
          position: "top",
          horizontalAlign: "left",
          offsetX: 40,
        },
      },
    };
  },
  methods: {},
};
</script>
