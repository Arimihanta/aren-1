<template>
  <div class="box-container">
    <base-layout id="voting">
      <template v-slot:title>
        <h1>Sondage</h1>
        <div v-if="loggedUser.is('ADMIN') || loggedUser.is('SUPERADMIN')">
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
// let baseUrl = getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split("/")[1];
// if (baseUrl.endsWith("/")) {
//   baseUrl = baseUrl.slice(0, -1);
// }
let baseUrl = getUrl.protocol + "//" + getUrl.host;
module.exports = {
  data() {
    return {
      user: false,
      theme: false,
      choices: [],
      votes: [],
      loggedUser: false,
    };
  },
  created() {
    this.fetchTheme();
    this.fetchUser();
    this.fetchVotes();
    this.getCurrentUser();
  },
  methods: {
    getCurrentUser: function () {
      ArenService.Users.getLoged({
        onSuccess: (logedUser) => {
          this.loggedUser = logedUser;
          console.log(logedUser)
          ArenService.NotificationListener.listen({
            onMessage: (notif) => {
              this.user.notifications.push(notif);
              let message = this.$t(
                "notification." + notif.content.message,
                notif.content.details
              );
              this.$toast(message);
              new BrowserNotification("AREN", {
                body: message,
              });
            },
          });
        },
        onError: (e) => {
          this.logout();
        },
      });
    },
    fetchUser: async function () {
      const _user = await axios.get(`${baseUrl}/ws/users/me`);
      this.user = _user.data;
      console.log(this.user);
    },
    fetchTheme: async function () {
      try {
        const myTheme = await axios.get(
          `${baseUrl}/ws/themes/${this.$route.query.id}`
        );
        this.theme = myTheme.data;
        this.choices = [...myTheme.data.choices];
      } catch (error) {
        console.error(error);
      }
    },
    vote: async function (subtheme, opinion) {
      try {
        const makeVote = await axios({
          method: "post",
          url: `${baseUrl}/ws/votes`,
          data: {
            subThemeId: subtheme,
            authorId: this.user,
            opinion: opinion,
          },
        });
        // console.log(makeVote);
        location.reload();
        this.fetchTheme();
      } catch (error) {
        console.error(error);
      }
    },
    fetchVotes: async function () {
      const _votes = await axios.get(`${baseUrl}/ws/votes`);
      this.votes = [..._votes.data];
      // console.log(this.votes.filter(el => el.authorId == this.user.id));
    },
    checkVoteAvalaibility: function (_subthemeId) {
      const userId = this.user.id;
      const filteredVotes = this.votes.filter((el) => el.authorId == userId);

      const filteredBySubTheme = filteredVotes.map(
        (el) => el.subThemeId.id || el.subThemeId
      );

      const arr = filteredBySubTheme.filter((el) => el == _subthemeId);

      return arr.length <= 0;
    },
    deleteCurrentSondage: function () {
      try {
        swal({
          title: "Êtes-vous sûr?",
          text: "Le sondage sera supprimé avec toutes ses informations",
          icon: "warning",
          buttons: ["Annuler", true],
          dangerMode: true,
        }).then(async (willDelete) => {
          if (willDelete) {
            let _ = await axios.delete(
              `${baseUrl}/ws/themes/delete/${this.$route.query.id}`
            );
            swal("Succès!", "Le sondage a été supprimé", "success").then(
              (value) => {
                location.href = baseUrl;
              }
            );
          }
        });
      } catch (error) {
        swal("Erreur!", `${error}`, "error");
        console.log(error);
      }
    },
  },
};
</script>
