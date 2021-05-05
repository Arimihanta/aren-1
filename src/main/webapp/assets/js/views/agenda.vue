<template>
  <div class="box-container">
    <base-layout id="testfunc">
      <template v-slot:title>
        <h1>Agenda</h1>
        <div v-if="user.is('ADMIN') || user.is('SUPERADMIN')">
          <button
            class="edit-btn space-top"
            v-if="!isEditMode"
            @click="
              () => {
                isEditMode = true;
                copyAgendaInfo();
              }
            "
            title="Modifier l'agenda"
          >
            <img alt="edit" src="assets/img/edit.png" />
          </button>
          <button
            class="edit-btn space-top"
            v-if="isEditMode"
            @click="
              () => {
                isEditMode = false;
              }
            "
            title="Annuler la modification"
          >
            <img alt="edit" src="assets/img/cancel.png" />
          </button>
          <button
            title="Supprimer l'agenda"
            class="delete-btn space-top"
            v-if="!isEditMode"
            @click="
              () => {
                deleteCurrentAgenda();
              }
            "
          >
            <img alt="edit" src="assets/img/delete.png" />
          </button>
        </div>
      </template>
      <div class="space-top block">
        <label>
          <span>Titre</span>
        </label>
        <span v-if="!isEditMode" class="block">{{ program.title }}</span>
        <input
          v-if="isEditMode"
          class="block"
          type="text"
          v-model="editionProgram.title"
        />

        <label class="block space-top">
          <span>Description</span>
        </label>
        <span v-if="!isEditMode" class="block">{{ program.description }}</span>
        <textarea
          v-if="isEditMode"
          class="block"
          v-model="editionProgram.description"
        ></textarea>

        <label class="block space-top">
          <span class="block">Date</span>
          <v-date-picker
            v-if="isEditMode"
            v-model="editionProgram.date"
            mode="dateTime"
            :timezone="timezone"
            :min-date="new Date()"
            color="red"
          />
        </label>
        <span v-if="!isEditMode" class="block">{{
          new Date(program.date).toLocaleString()
        }}</span>
        <span v-if="isEditMode" class="block">{{
          editionProgram.date.toISOString()
        }}</span>

        <label class="block space-top">
          <span>URL</span>
        </label>
        <span v-if="!isEditMode" class="block">
          <a :href="program.url" target="_blank">{{ program.url }}</a>
        </span>
        <input v-if="isEditMode" type="text" v-model="editionProgram.url" />
      </div>
      <div class="space-top"></div>
      <div v-if="isEditMode" class="space-top">
        <button
          id="modifySondage"
          @click="
            () => {
              modifyAgenda();
            }
          "
        >
          Modifier l' agenda
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

#modifySondage {
  background-color: #b84000;
  padding: 1rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #b84000;
  margin-bottom: 2rem;
  cursor: pointer;
}
.edit-btn {
  background-color: #808080;
  padding: 1rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #808080;
  margin-bottom: 2rem;
  cursor: pointer;
  width: 50px;
  height: 50px;
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
.cancel-btn {
  background-color: #b84000;
  padding: 1rem;
  border-radius: 0.5rem;
  color: white;
  border-color: transparent;
  box-shadow: 0.1rem 0.1rem 1rem #b84000;
  margin-bottom: 2rem;
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
      isEditMode: false,
      program: false,
      editionProgram: false,
      user: false,
    };
  },
  created() {
    this.fetchProgram();
    this.getCurrentUser();
  },
  methods: {
    fetchProgram: async function () {
      try {
        let program = await axios.get(
          `${baseUrl}/ws/agenda/calendars/${this.$route.query.id}`
        );
        this.program = program.data;
        this.editionProgram = { ...program.data };
      } catch (expetion) {
        console.error(expetion);
      }
    },
    copyAgendaInfo() {
      this.editionProgram = {
        ...this.program,
        date: new Date(this.program.date),
      };
    },
    getCurrentUser: async function () {
      ArenService.Users.getLoged({
        onSuccess: (logedUser) => {
          this.user = logedUser;

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
    modifyAgenda: function () {
      new BrowserNotification("En cours de construction ....", {
        body: "Modification de l'agenda",
      });
    },
    deleteCurrentAgenda: function () {
      try {
        swal({
          title: "Êtes-vous sûr?",
          text:
            "L'agenda sera supprimé",
          icon: "warning",
          buttons: ["Annuler", true],
          dangerMode: true,
        }).then( async (willDelete) => {
          if (willDelete) {
            let _ = await axios.delete(`${baseUrl}/ws/agenda/calendars/${this.$route.query.id}`);
            swal("Succès!", "L'agenda a été supprimé", "success").then((value) => {
              location.href = baseUrl;
            });
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
