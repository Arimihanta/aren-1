<template>
  <div class="box-container">
    <base-layout id="majoritaire">
      <p></p>
      <template v-slot:title>
        <h1>Création vote majoritaire</h1>
        <div class="space-top">
          <span>Selectionnez un groupe</span>
          <select class="browser-default" v-model="themeInfo.team">
            <option v-for="team in teams" :key="team.id" :value="team">
              {{ team.name }}
            </option>
          </select>

          <label>
            <span>Titre</span>
            <input type="text" v-model="themeInfo.title" />
          </label>

          <label>
            <span>Description</span>
          </label>
          <div class="choice-container">
            <tooltiped tag="div" v-bind:value="editionMode">
              <wysiwyg-editor
                v-model="themeInfo.description"
                v-bind:enabled="editionMode"
              >
              </wysiwyg-editor>
            </tooltiped>
          </div>

          <label class="block space-top">
            <span class="block">Date de fin de vote</span>
            <v-date-picker
              v-model="themeInfo.expiracyDate"
              mode="dateTime"
              :timezone="timezone"
              :min-date="new Date()"
              color="red"
            />
          </label>
        </div>

        <div class="space-top">
          <div v-for="(choice, k) in choices" :key="k" class="sous-theme-block">
            <label>
              <span>{{ choice.choice_title_label }}</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choice.choice_title"
                />
              </div>
            </label>
            <label>
              <span>Description</span>
            </label>
            <div class="choice-container">
              <tooltiped tag="div" v-bind:value="editionMode">
                <wysiwyg-editor
                  v-model="choice.choice_description"
                  v-bind:enabled="editionMode"
                >
                </wysiwyg-editor>
              </tooltiped>
            </div>
            <label>
              <span>Image</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choice.choice_image"
                />
              </div>
            </label>
            <label>
              <span>URL</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choice.choice_url"
                />
              </div>
            </label>
          </div>
          <div class="box-footer">
            <button
              @click="addMoreChoice()"
              class="add-choice-btn"
              title="Ajouter un nouveau choix"
            >
              <img
                height="50"
                width="50"
                alt="ajouter"
                src="assets/img/add.png"
              />
            </button>
            <button
              v-if="choices.length > 2"
              @click="removeChoice()"
              class="rem-choice-btn"
              title="Supprimer le dernier choix"
            >
              <img
                height="40"
                width="40"
                alt="supprimer"
                src="assets/img/delete.png"
              />
            </button>
            <button @click="saveVoteMajoritaire()" class="create-vote-btn">
              <span>Créer la vote majoritaire</span>
            </button>
          </div>
        </div>
      </template>
    </base-layout>
  </div>
</template>
<style scoped>
.block {
  display: block;
}
.space-top {
  margin-top: 4em;
}
.box-container {
  border: 1px solid #a1a3a1;
  border-radius: 10px;
  padding-top: 2em;
  width: 50%;
  margin: 2em auto;
}
.box-footer {
  display: flex;
  flex-direction: row;
}
.choice-container {
  display: flex;
  flex-direction: column;
}
.choice-text {
  flex: 1;
  margin-right: 1rem !important;
}
.sous-theme-block {
  border: 1px solid;
  padding: 1.5em;
  margin: 1em 0;
}
.add-choice-btn {
  background-color: #3d82bb;
  padding: 0.3rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #3d82bb;
  margin: 2rem 1rem;
  cursor: pointer;
}
.rem-choice-btn {
  background-color: #e06d6d;
  padding: 0.6rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #e06d6d;
  margin: 2rem 1rem;
  cursor: pointer;
}
.create-vote-btn {
  background-color: #6de09d;
  padding: 0.3rem;
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
      timezone: "",
      themeInfo: {
        author: false,
        team: false,
        title: "",
        description: "",
        expiracyDate: new Date(),
      },
      document: { content: "" },
      teams: false,
      editionMode: true,
      choices: [
        {
          choice_title_label: "Choix numéro 1",
          choice_title: "",
          choice_description: "",
          choice_image: "",
          choice_url: "",
        },
        {
          choice_title_label: "Choix numéro 2",
          choice_title: "",
          choice_description: "",
          choice_image: "",
          choice_url: "",
        },
      ],
    };
  },
  created() {
    if (!this.$root.user.is("MODO")) {
      this.$router.push("/404");
    }
    this.fetchData();
  },
  methods: {
    async fetchData() {
      try {
        let user = await axios.get(`${ApiBaseUri}/users/me`);
        this.themeInfo.author = user.data;

        let allteams = await axios.get(`${ApiBaseUri}/teams`);
        this.teams = allteams.data;
      } catch (error) {
        alert(
          "ERREUR INTERNE\n\nToutes nos excuses, une erreur s'est produite sur nos serveurs.\nVeuillez réessayer ou contacter un administrateur si l'erreur persiste."
        );
        console.error(error);
      }
    },
    addMoreChoice() {
      this.choices.push({
        choice_title_label: `Choix numéro ${this.choices.length + 1}`,
        choice_title: "",
        choice_description: "",
        choice_image: "",
        choice_url: "",
      });
    },
    removeChoice() {
      this.choices.pop();
    },
    async saveVoteMajoritaire() {
      let data = { ...this.themeInfo };

      try {
        let newVoteMajoritaire = await axios({
          method: "POST",
          url: `${ApiBaseUri}/vm/themes`,
          data,
        });

        for (const choice of this.choices) {
          const choice_data = {
            themeId: newVoteMajoritaire.data,
            title: choice.choice_title,
            description: choice.choice_description,
            img: choice.choice_image,
            url: choice.choice_url,
          };
          const newChoice = await axios({
            method: "POST",
            url: `${ApiBaseUri}/vm/choices`,
            data: choice_data,
          });
        }

        swal("Succès!", "L a été créé", "success").then((value) => {
          this.$router.push("/");
        });
      } catch (error) {
        swal("Erreur!", `Erreur, Veuillez réésayer plus tard`, "error");
        console.error(error);
      }
    },
  },
  components: {
    "wysiwyg-editor": vueLoader("components/widgets/wysiwygEditor"),
  },
};
</script>
