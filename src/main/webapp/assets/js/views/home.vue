.<template>
  <div class="scroll-content" id="home">
    <div id="welcomeCallout" class="row light-color valign-wrapper">
      <img class="col" alt="image" src="assets/img/imageleves.png" />
      <p class="col" v-html="$t('helper.welcome')"></p>
    </div>

    <div class="container">
      <h1>Catégories</h1>

      <div class="row center-align" id="categoriesList">
        <div
          v-for="category in categories"
          v-bind:key="category.id"
          class="col"
        >
          <router-link
            v-bind:to="'/debates?category=' + category.id"
            class="card hoverable center-align"
          >
            <div class="card-image light-color valign-wrapper">
              <img
                alt="image"
                class="category-picture"
                v-bind:src="category.picture"
              />
            </div>
            <div>
              <h2 v-bind:title="category.name">{{ category.name }}</h2>
              <p>{{ $tc("nb_debates", category.debatesCount) }}</p>
            </div>
            <div class="card-footer light-color">
              <span>{{
                category.lastCommentDate !== 0
                  ? $t("last_post_the") +
                    " " +
                    $d(category.lastCommentDate, "short")
                  : $t("no_post")
              }}</span>
            </div>
          </router-link>
        </div>
      </div>
    </div>

    <!-- Pour les agenda -->
    <div class="container">
      <h1>Agenda</h1>
      <div class="row center-align" id="sondagesList">
        <div v-for="program in agenda" v-bind:key="program.id" class="col">
          <router-link
            v-bind:to="'/agenda?id=' + program.id"
            class="card hoverable center-align"
          >
            <div class="card-image light-color valign-wrapper">
              <img
                alt="image"
                class="category-picture"
                src="assets/img/calendar.png"
              />
            </div>
            <div>
              <h2 v-bind:title="program.title">{{ program.title }}</h2>
            </div>
            <div class="card-footer light-color">
              <span>Le {{ new Date(program.date).toLocaleString() }}</span>
            </div>
          </router-link>
        </div>
      </div>
    </div>

    <!-- Pour les sondages -->
    <div class="container">
      <h1>Sondages</h1>
      <div class="row center-align" id="sondagesList">
        <div v-for="sondage in sondages" v-bind:key="sondage.id" class="col">
          <router-link
            v-bind:to="'/sondage?id=' + sondage.id"
            class="card hoverable center-align"
          >
            <div class="card-image light-color valign-wrapper">
              <img
                alt="image"
                class="category-picture"
                src="assets/img/sondage.png"
              />
            </div>
            <div>
              <h2 v-bind:title="sondage.title">{{ sondage.title }}</h2>
            </div>
            <div class="card-footer light-color">
              <span
                >Créé le
                {{ new Date(sondage.createdAt).toLocaleString() }}</span
              >
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const getUrl = window.location;
// let baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split("/")[1];
// if (baseUrl.endsWith("/")) {
//   baseUrl = baseUrl.slice(0, -1);
// }
let baseUrl = getUrl.protocol + "//" + getUrl.host
module.exports = {
  data() {
    return {
      categories: false,
      agenda: [],
      sondages: [],
    };
  },
  created() {
    this.fetchData();
    this.fetchAgenda();
    this.fetchSondages();
  },
  methods: {
    fetchData() {
      ArenService.Categories.getAll({
        query: { overview: true },
        onSuccess: (categories) => (this.categories = categories),
      });
    },

    //Fetch agendas
    async fetchAgenda() {
      try {
        let agenda = await axios.get(`${baseUrl}/ws/agenda/calendars`);
        this.agenda.push(...agenda.data);
      } catch (expetion) {
        console.error(expetion);
      }
    },

    //Fetch sondages
    async fetchSondages() {
      try {
        let agenda = await axios.get(`${baseUrl}/ws/themes`);
        this.sondages.push(...agenda.data);
      } catch (expetion) {
        console.error(expetion);
      }
    },
  },
};
</script>
