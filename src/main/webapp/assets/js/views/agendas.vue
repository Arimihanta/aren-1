<template>
  <div class="box-container">
    <base-layout id="testfunc">
      <template v-slot:title>
        <h1>Utilitaire de création d'Agenda</h1>
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

        <label class="block space-top">
          <span class="block">Date</span>
          <v-date-picker
            v-model="selectedDate"
            mode="dateTime"
            :timezone="timezone"
            :min-date="new Date()"
            color="red"
          />
        </label>

        <span class="block">{{ selectedDate.toISOString() }}</span>

        <label class="block space-top">
          <span>image</span>
          <input type="text" v-model="img" />
        </label>

        <label class="block space-top">
          <span>URL (si nécessaire)</span>
          <input type="text" v-model="url" />
        </label>

        <div class="space-top">
          <button
            id="createSondage"
            @click="postAgenda(name, text, url, selectedDate)"
          >
            Créer l' agenda
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
</style>


<script>
const getUrl = window.location;
const baseUrl =
  getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split("/")[1];
module.exports = {
  data() {
    return {
      name: "",
      text: "",
      expirationDate: "",
      url: "",
      img: "",
      selectedDate: new Date(),
      timezone: "",
      isInserted: false,
      isNotInserted: false,
    };
  },
  methods: {
    exitSuccess: () => {
      this.isInserted = false;
    },
    async postAgenda(name, text, url, selectedDate) {
      try {
        if (name == "" || text == "") {
          this.isNotInserted = true;
          return;
        }
        let resp = await axios({
          method: "post",
          url: `${baseUrl}/ws/agenda/calendars`,
          data: {
            title: name,
            description: text,
            url: url,
            // img: img,
            date: selectedDate,
          },
        });
        console.log(resp)

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