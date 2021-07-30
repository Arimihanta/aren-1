<template>
  <div class="box-container">
    <base-layout id="votelayout">
      <template v-slot:title>
        <div class="vm-header">
          <h1 class="vm-header-title">{{ currentTheme.title }}</h1>
          <router-link
            v-if="$root.user.is('MODO')"
            to="/createVoteMajoritaire"
            class="waves-effect waves-light btn"
          >
            Créer vote majoritaires
          </router-link>
        </div>
        <span class="creaeted-at">
          Créé le {{ new Date(currentTheme.createdAt).toLocaleString() }}
        </span>
        <div v-html="currentTheme.description"></div>
      </template>
      <div v-if="showVote" class="space-top tableau-vote">
        <h1 class="vm-header-title">Vote</h1>
        <table id="vote-majoritaire-table">
          <thead>
            <tr>
              <th class="tab-head-cell" id="vm-vote-header">Vote</th>
              <th class="tab-head-cell" id="vm-vote-reject">A rejeter</th>
              <th class="tab-head-cell" id="vm-vote-insuff">Insuffisant</th>
              <th class="tab-head-cell" id="vm-vote-passable">Passable</th>
              <th class="tab-head-cell" id="vm-vote-gooden">Assez-bien</th>
              <th class="tab-head-cell" id="vm-vote-good">Bien</th>
              <th class="tab-head-cell" id="vm-vote-vergood">Très-bien</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(choice, k) in currentTheme.choices" :key="k">
              <td>
                <span class="choice" @click="OpenDetail(choice)">{{ choice.title }}</span>
              </td>
              <td class="chek-cell-container">
                <input
                  type="checkbox"
                  v-model="choicesValue[k].opinion"
                  true-value="REJECTED"
                  false-value="REJECTED"
                  class="vote-check"
                  disabled
                />
              </td>

              <td class="chek-cell-container">
                <input
                  type="checkbox"
                  v-model="choicesValue[k].opinion"
                  true-value="INSUFFICIENT"
                  false-value="REJECTED"
                  class="vote-check"
                />
              </td>
              <td class="chek-cell-container">
                <input
                  type="checkbox"
                  v-model="choicesValue[k].opinion"
                  true-value="PASS"
                  false-value="REJECTED"
                  class="vote-check"
                />
              </td>
              <td class="chek-cell-container">
                <input
                  type="checkbox"
                  v-model="choicesValue[k].opinion"
                  true-value="ACCEPTABLE"
                  false-value="REJECTED"
                  class="vote-check"
                />
              </td>
              <td class="chek-cell-container">
                <input
                  type="checkbox"
                  v-model="choicesValue[k].opinion"
                  true-value="GOOD"
                  false-value="REJECTED"
                  class="vote-check"
                />
              </td>
              <td class="chek-cell-container">
                <input
                  type="checkbox"
                  v-model="choicesValue[k].opinion"
                  true-value="VERY_GOOD"
                  false-value="REJECTED"
                  class="vote-check"
                />
              </td>
            </tr>
          </tbody>
        </table>
        <button @click="saveVoteMajoritaire()" class="create-vote-btn">
          <span>Valider mon choix</span>
        </button>
      </div>
      <div v-if="!showVote" class="space-top">
        <h1 class="vm-header-title">Résultat</h1>
        <div v-if="typeof currentTheme === `object`" id="chart">
          <apexchart
            type="bar"
            height="350"
            :options="chartOptions"
            :series="series"
          ></apexchart>
        </div>
      </div>
      <template v-slot:addons>
        <choice-detail-modal ref="ChoiceDetailsModal"> </choice-detail-modal>
      </template>
    </base-layout>
  </div>
</template>

<style lang="css" scoped>
.choice {
  cursor: pointer;
}
.box-container {
  border: 1px solid #a1a3a1;
  border-radius: 10px;
  padding-top: 2em;
  width: 50%;
  margin: 2em auto;
}
.vm-header {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.vm-header-title {
  flex: 1;
}
.space-top {
  margin-top: 4em;
}
.block {
  display: block;
}
.creaeted-at {
  color: gray;
  font-size: 10px;
  opacity: 0.7;
}
.vote-check {
  opacity: 1;
  pointer-events: auto;
  position: unset;
}

#vote-majoritaire-table {
  table-layout: fixed;
}

#vm-vote-reject {
  background-color: #e3493c;
}

#vm-vote-insuff {
  background-color: #f07c0a;
}

#vm-vote-passable {
  background-color: #fff209;
}

#vm-vote-gooden {
  background-color: #c9e537;
}

#vm-vote-good {
  background-color: #92d050;
}

#vm-vote-vergood {
  background-color: #00b050;
}

.chek-cell-container {
  text-align: center;
}

.tab-head-cell {
  text-align: center;
}

.create-vote-btn {
  background-color: #6de09d;
  padding: 1.3rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #6de09d;
  margin: 2rem 1rem;
  cursor: pointer;
}
</style>

<script>
module.exports = {
  data() {
    return {
      currentTheme: false,
      choicesValue: false,
      showVote: false,
      series: [],
      chartOptions: {
        colors: [
          "#E3493C",
          "#F07C0A",
          "#FFF209",
          "#C9E537",
          "#92D050",
          "#00B050",
        ],
        chart: {
          type: "bar",
          height: 200,
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
          text: `Résultat du vote : `,
        },
        xaxis: {
          categories: false,
          labels: {
            show: false,
          },
          lines: {
            show: false,
          },
          axisBorder: {
            show: false,
          },
          axisTicks: {
            show: false,
          },
        },
        yaxis: {
          title: {
            text: undefined,
          },
          lines: {
            show: false,
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
  created() {
    if (!this.$root.user.is("USER")) {
      this.$router.push("/404");
    }
    this.fetchData();
  },
  methods: {
    async fetchData() {
      this.series = [];
      try {
        const user = await axios.get(`${ApiBaseUri}/users/me`);

        const theme = await axios({
          method: "GET",
          url: `${ApiBaseUri}/vm/themes/${this.$route.query.id}`,
        });

        this.currentTheme = theme.data;

        this.choicesValue = theme.data.choices.map((e) => ({
          subThemeId: e,
          authorId: user.data,
          opinion: "REJECTED",
        }));

        this.series.push(
          {
            name: "A rejeter",
            data: this.currentTheme.choices.map((e) => e.rejected),
          },
          {
            name: "Insuffisant",
            data: this.currentTheme.choices.map((e) => e.insufficient),
          },
          {
            name: "Passable",
            data: this.currentTheme.choices.map((e) => e.pass),
          },
          {
            name: "Assez-bien",
            data: this.currentTheme.choices.map((e) => e.acceptable),
          },
          {
            name: "Bien",
            data: this.currentTheme.choices.map((e) => e.good),
          },
          {
            name: "Très-bien",
            data: this.currentTheme.choices.map((e) => e.veryGood),
          }
        );

        this.chartOptions.xaxis.categories = this.currentTheme.choices.map(
          (e) => e.title
        );

        for (const choice of theme.data.choices) {
          for (const vote of choice.votes) {
            const authId = vote.authorId;

            if (typeof authId === "number") {
              if (authId === user.data.id) {
                this.showVote = false;
                return;
              }
            }

            if (typeof authId === "object") {
              if (authId.id === user.data.id) {
                this.showVote = false;
                return;
              }
            }
          }
          this.showVote = true;
        }
      } catch (error) {
        alert(
          "ERREUR INTERNE\n\nToutes nos excuses, une erreur s'est produite sur nos serveurs.\nVeuillez réessayer ou contacter un administrateur si l'erreur persiste."
        );
        console.log(error);
      }
    },
    async saveVoteMajoritaire() {
      try {
        for (const choice of this.choicesValue) {
          const newChoice = await axios({
            method: "POST",
            url: `${ApiBaseUri}/vm/votes`,
            data: choice,
          });
          console.log(choice);
        }

        swal("Succès!", "Vote enregistré", "success").then((value) => {
          this.fetchData();
        });
      } catch (error) {
        swal("Erreur!", `Erreur, Veuillez réésayer plus tard`, "error");
        console.error(error);
      }
    },
    OpenDetail(choice = {}) {
      this.$refs.ChoiceDetailsModal.choice = choice;
      this.$refs.ChoiceDetailsModal.open();
    },
  },
  components: {
    apexchart: VueApexCharts,
    "choice-detail-modal": vueLoader("components/modals/ChoiceDetailsModal"),
  },
};
</script>
