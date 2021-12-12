<template>
  <div class="box-container">
    <base-layout id="voting">
      <template v-slot:title>
        <h1>Sondage</h1>
        <div v-if="user.is('ADMIN') || user.is('SUPERADMIN')">
          <button
            title="Supprimer le sondage"
            class="delete-btn space-top"
            @click="
              () => {
                deleteCurrentSondage();
              }
            "
          >
            <img alt="edit" src="assets/img/delete.png" />
          </button>
        </div>
      </template>
      <div class="theme-container">
        <br />
        <span class="block theme-title">{{ theme.title }}</span>
        <span class="block">{{ theme.description }}</span>
        <br />
        <span class="block">
          Créé le {{ new Date(theme.createdAt).toLocaleString() }}</span
        >
        <span class="block">
          Sera archivé le
          {{ new Date(theme.expiracyDate).toLocaleString() }}</span
        >
      </div>
      <div class="space-top block choices-ct">
        <table class="table">
          <tr>
            <td
              v-for="subtheme in choices"
              :key="subtheme.id"
              class="centered-data"
            >
              <img :src="subtheme.img" alt="image" />
            </td>
          </tr>
          <tr>
            <td v-for="subtheme in choices" :key="subtheme.id">
              <span class="sub-theme-title">
                {{ subtheme.title }}
              </span>
            </td>
          </tr>
          <tr>
            <td v-for="subtheme in choices" :key="subtheme.id">
              <p class="sub-theme-description">{{ subtheme.description }}</p>
            </td>
          </tr>
          <tr>
            <td v-for="subtheme in choices" :key="subtheme.id">
              <span class="sub-theme-title">
                <a :href="subtheme.url" target="_blank">{{ subtheme.url }}</a>
              </span>
            </td>
          </tr>
          <tr v-if="user.authority != `GUEST`">
            <td v-for="subtheme in choices" :key="subtheme.id">
              <button
                class="btn-for btn-vote"
                @click="vote(subtheme, `FOR`)"
                v-if="checkVoteAvalaibility(subtheme.id)"
              >
                <span>Participation certaine | {{ subtheme.for }}</span>
              </button>
            </td>
          </tr>
          <tr v-if="user.authority != `GUEST`">
            <td v-for="subtheme in choices" :key="subtheme.id">
              <button
                class="btn-neutral btn-vote"
                @click="vote(subtheme, `NEUTRAL`)"
                v-if="checkVoteAvalaibility(subtheme.id)"
              >
                <span>Participation possible | {{ subtheme.neutral }}</span>
              </button>
            </td>
          </tr>
          <tr v-if="user.authority != `GUEST`">
            <td v-for="subtheme in choices" :key="subtheme.id">
              <button
                class="btn-against btn-vote"
                @click="vote(subtheme, `AGAINST`)"
                v-if="checkVoteAvalaibility(subtheme.id)"
              >
                <span>Simple observateur | {{ subtheme.against }}</span>
              </button>
            </td>
          </tr>
          <tr>
            <td
              v-for="subtheme in choices"
              :key="subtheme.id"
              class="text-centered"
            >
              <span class="nb-total-vote"
                >{{ subtheme.for }} participant(s)
              </span>
            </td>
          </tr>
        </table>
      </div>
    </base-layout>
  </div>
</template>
<style scoped>
.theme-title {
  font-weight: bold;
  font-size: larger;
}

.centered-data {
  text-align: center;
}

.box-container {
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

.choices-ct {
  display: flex;
  justify-content: center;
}

.choice-poster {
  max-width: 15em;
  max-height: 15em;
}

.delete-btn {
  background-color: #dd2034;
  padding: 1rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #dd2034;
  margin-bottom: 2rem;
  cursor: pointer;
  width: 50px;
  height: 50px;
}

.btn-vote {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: auto;
  width: 90%;
}

.btn-vote img {
  height: 1em;
  width: 1em;
  margin: 1em;
}

.btn-for {
  /* width: 100%; */
  color: white;
  height: 2rem;
  background-color: #1976d2;
  border-radius: 0.4em;
  border: 0;
  cursor: pointer;
}

.btn-neutral {
  /* width: 100%; */
  color: white;
  height: 2rem;
  background-color: #2196f3;
  border-radius: 0.4em;
  border: 0;
  cursor: pointer;
}

.btn-against {
  /* width: 100%; */
  color: white;
  height: 2rem;
  background-color: #64b5f6;
  border-radius: 0.4em;
  border: 0;
  cursor: pointer;
}

.sub-theme-title {
  display: block;
  font-weight: bold;
  font-size: larger;
  text-align: center;
  overflow: hidden;
}

.sub-theme-description {
  max-height: 6.5rem;
  overflow-y: auto;
  text-align: justify;
}

.nb-total-vote {
  font-weight: bold;
  font-size: larger;
  text-align: center;
}

.table {
  border-spacing: 1em;
  margin: auto;
}

.table tr {
  text-align: center;
}

.table tr td {
  max-width: 20rem;
  min-width: 20rem;
}

.text-centered {
  text-align: center;
}
</style>
<script>
const getUrl = window.location;
let baseUrl = getUrl.protocol + "//" + getUrl.host;
module.exports = {
  data() {
    return {
      user: false,
      theme: false,
      choices: [],
      votes: false,
    };
  },
  created() {
    if (!this.$root.user.is("USER")) {
      this.$router.push("/404");
      return;
    }
    this.getCurrentUser();
    this.fetchTheme();
    this.fetchVotes();
  },
  methods: {
    getCurrentUser() {
      ArenService.Users.getLoged({
        onSuccess: (logedUser) => {
          this.user = logedUser;
        },
      });
    },
    fetchTheme() {
      ArenService.Sondage.get({
        id: this.$route.query.id,
        onSuccess: (myTheme) => {
          this.theme = myTheme;
          this.choices = [...myTheme.choices];
        },
      });
    },
    vote(subtheme, opinion) {
      ArenService.Vote.create({
        data: {
          subThemeId: subtheme,
          authorId: this.user,
          opinion: opinion,
        },
        onSuccess: () => {
          location.reload();
        },
      });
    },
    checkVoteAvalaibility(_subthemeId) {
      const userId = this.user.id;
      if (this.votes) {
        const filteredVotes = this.votes?.filter(
          (el) => el.authorId?.id == userId
        );
        const filteredBySubTheme = filteredVotes.map(
          (el) => el.subThemeId?.id ?? el.subThemeId
        );
        const arr = filteredBySubTheme.filter((el) => el == _subthemeId);
        return arr.length <= 0;
      }
      return false;
    },
    fetchVotes() {
      ArenService.Vote.getAll({
        onSuccess: (f_votes) => (this.votes = f_votes),
      });
    },
    deleteCurrentSondage() {
      this.$confirm({
        title: "Êtes-vous sûr?",
        message: "Le sondage sera supprimé avec toutes ses informations",
        callback: (returnValue) => {
          if (returnValue) {
            ArenService.Sondage.removeCurrent({
              data: { id: this.$route.query.id, sondage: this.theme },
              onSuccess: () =>
                swal("Succès!", "Le sondage a été supprimé", "success").then(
                  (value) => {
                    this.$router.push("/");
                  }
                ),
            });
          }
        },
      });
    },
  },
};
</script>
