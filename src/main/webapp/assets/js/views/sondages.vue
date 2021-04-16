<template>
  <div class="box-container">
    <base-layout id="testfunc">
      <template v-slot:title>
        <h1>Utilitaire de création de sondage</h1>
      </template>
      <div class="space-top">
        <label>
          <span>Titre</span>
          <input type="text" v-model="name" />
        </label>

        <label>
          <span>Description</span>
          <textarea v-model="text"></textarea>
        </label>

        <div class="space-top">
          <div class="sous-theme-block">
            <label>
              <span>Sous-thème 1</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.first.title"
                />
              </div>
            </label>
            <label>
              <span>Description</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.first.description"
                />
              </div>
            </label>
            <label>
              <span>Image</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.first.img"
                />
              </div>
            </label>
            <label>
              <span>URL</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.first.url"
                />
              </div>
            </label>
          </div>

          <div class="sous-theme-block">
            <label>
              <span>Sous-thème 2</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.second.title"
                />
              </div>
            </label>
            <label>
              <span>Description</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.second.description"
                />
              </div>
            </label>
            <label>
              <span>Image</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.second.img"
                />
              </div>
            </label>
            <label>
              <span>URL</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.second.url"
                />
              </div>
            </label>
          </div>

          <div class="sous-theme-block">
            <label>
              <span>Sous-thème 3</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.third.title"
                />
              </div>
            </label>
            <label>
              <span>Description</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.third.description"
                />
              </div>
            </label>
            <label>
              <span>Image</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.third.img"
                />
              </div>
            </label>
            <label>
              <span>URL</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.third.url"
                />
              </div>
            </label>
          </div>

          <div class="sous-theme-block">
            <label>
              <span>Sous-thème 4</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.fourth.title"
                />
              </div>
            </label>
            <label>
              <span>Description</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.fourth.description"
                />
              </div>
            </label>
            <label>
              <span>Image</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.fourth.img"
                />
              </div>
            </label>
            <label>
              <span>URL</span>
              <div class="choice-container">
                <input
                  class="choice-text"
                  type="text"
                  v-model="choices.fourth.url"
                />
              </div>
            </label>
          </div>
        </div>

        <label class="block space-top">
          <span class="block">Date d'archivage </span>
          <v-date-picker
            v-model="selectedDate"
            mode="dateTime"
            :timezone="timezone"
            :min-date="new Date()"
            color="red"
          />
        </label>

        <span class="block">{{ selectedDate.toISOString() }}</span>

        <div class="space-top">
          <button
            id="createSondage"
            @click="postTheme(name, text, selectedDate)"
          >
            Créer le sondage
          </button>
        </div>
      </div>
      <div v-if="isInserted" class="success-mess">
        <p class="mess-text">Programme ajoutée avec succès</p>
        <button
          class="exit-btn"
          @click="
            () => {
              isInserted = false;
            }
          "
        >
          fermer
        </button>
      </div>
      <div v-if="isNotInserted" class="error-mess">
        <p class="mess-text">
          Une érreur est survenue (veuillez remplir tous les champs
          indispensables)
        </p>
        <button
          class="exit-btn"
          @click="
            () => {
              isNotInserted = false;
            }
          "
        >
          fermer
        </button>
      </div>
    </base-layout>

  </div>
</template>


<style scoped>
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
.choice-container {
  display: flex;
}
.choice-text {
  flex: 1;
  margin-right: 1rem !important;
}
#createSondage {
  background-color: #b84000;
  padding: 1rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #b84000;
  margin-bottom: 2rem;
  cursor: pointer;
}
.success-mess {
  background-color: #5fb668;
  padding: 0 1em;
  margin-bottom: 1em;
  color: white;
  border-radius: 0.5em;
  display: flex;
}

.error-mess {
  background-color: #dd2034;
  padding: 0 1em;
  margin-bottom: 1em;
  color: white;
  border-radius: 0.5em;
  display: flex;
}

.mess-text {
  flex: 1;
}
.exit-btn {
  color: white;
  border: none;
  text-decoration: underline;
  background-color: transparent;
  padding: 0;
  cursor: pointer;
}
.sous-theme-block {
  border: 1px solid;
  padding: 1.5em;
  margin: 1em 0;
}
</style>


<script>
const getUrl = window.location;
const baseUrl =
  getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split("/")[1];
Vue.component("modal", {
  template: "#modal-template",
});
module.exports = {
  data() {
    return {
      showModal: false,
      name: "",
      text: "",
      expirationDate: "",
      // url: "",
      selectedDate: new Date(),
      timezone: "",
      isInserted: false,
      isNotInserted: false,
      choices: {
        first: {
          title: "",
          description: "",
          url: "",
          img: "",
        },
        second: {
          title: "",
          description: "",
          url: "",
          img: "",
        },
        third: {
          title: "",
          description: "",
          url: "",
          img: "",
        },
        fourth: {
          title: "",
          description: "",
          url: "",
          img: "",
        },
      },
    };
  },
  methods: {
    async postTheme(title, description, expiracyDate) {
      try {
        if (title == "" || description == "") {
          this.isNotInserted = true;
          return;
        }

        let user = await axios.get(`${baseUrl}/ws/users/me`);
        //console.log(user)

        let theme = await axios({
          method: "post",
          url: `${baseUrl}/ws/themes`,
          data: {
            title: title,
            author: user.data,
            description: description,
            expiracyDate: expiracyDate,
          },
        });

        // console.log(theme)

        if (
          this.choices.first.title != "" &&
          this.choices.first.description != ""
        ) {
          let sousTheme1 = await axios({
            method: "post",
            url: `${baseUrl}/ws/choices`,
            data: {
              themeId: theme.data,
              img: this.choices.first.img,
              url: this.choices.first.url,
              title: this.choices.first.title,
              description: this.choices.first.description,
            },
          });
        }

        if (
          this.choices.second.title != "" &&
          this.choices.second.description != ""
        ) {
          let sousTheme2 = await axios({
            method: "post",
            url: `${baseUrl}/ws/choices`,
            data: {
              themeId: theme.data,
              img: this.choices.second.img,
              url: this.choices.second.url,
              title: this.choices.second.title,
              description: this.choices.second.description,
            },
          });
        }

        if (
          this.choices.third.title != "" &&
          this.choices.third.description != ""
        ) {
          let sousTheme3 = await axios({
            method: "post",
            url: `${baseUrl}/ws/choices`,
            data: {
              themeId: theme.data,
              img: this.choices.third.img,
              url: this.choices.third.url,
              title: this.choices.third.title,
              description: this.choices.third.description,
            },
          });
        }

        if (
          this.choices.fourth.title != "" &&
          this.choices.fourth.description != ""
        ) {
          let sousTheme4 = await axios({
            method: "post",
            url: `${baseUrl}/ws/choices`,
            data: {
              themeId: theme.data,
              img: this.choices.fourth.img,
              url: this.choices.fourth.url,
              title: this.choices.fourth.title,
              description: this.choices.fourth.description,
            },
          });
        }

        this.name = "";
        this.text = "";
        this.expirationDate = "";
        this.url = "";
        this.selectedDate = new Date();

        this.isNotInserted = false;
        this.isInserted = true;
      } catch (error) {
        this.isNotInserted = true;
        this.isInserted = false;
        console.error(error);
      }
    },
  },
};
</script>