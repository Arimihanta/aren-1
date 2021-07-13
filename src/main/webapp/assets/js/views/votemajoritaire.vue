<template>
  <div class="box-container">
    <base-layout id="votelayout">
      <template v-slot:title>
        <div class="vm-header">
          <h1 class="vm-header-title">Vote majoritaire</h1>
          <router-link
            v-if="$root.user.is('MODO')"
            to="/createVoteMajoritaire"
            class="waves-effect waves-light btn"
          >
            Créer vote majoritaires
          </router-link>
        </div>
      </template>
      <div class="space-top">
        <div
          class="empty-container"
          v-if="!voteMajoritaireList || !voteMajoritaireList.length"
        >
          <span>Aucun</span>
        </div>
        <div class="vm-container">
          <div class="row center-align" id="vmList">
            <div
              v-for="program in voteMajoritaireList"
              v-bind:key="program.id"
              class="col"
            >
              <router-link
                v-bind:to="'/votemajoritairedetails?id=' + program.id"
                class="card hoverable center-align"
              >
                <div class="card-image light-color valign-wrapper">
                  <img
                    alt="image"
                    class="category-picture"
                    src="assets/img/vmajoritaire.png"
                  />
                </div>
                <div>
                  <h2 v-bind:title="program.title">{{ program.title }}</h2>
                </div>
                <div class="card-footer light-color">
                  <span
                    >Fin de vote le
                    {{ new Date(program.expiracyDate).toLocaleString() }}</span
                  >
                </div>
              </router-link>
            </div>
          </div>
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
.vm-header {
  display: flex;
  flex-direction: row;
  align-items: center;
}
.vm-header-title {
  flex: 1;
}
.vm-container {
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
      voteMajoritaireList: false,
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
      try {
        const vms = await axios({
          method: "GET",
          url: `${ApiBaseUri}/vm/themes`,
        });
        this.voteMajoritaireList = vms.data;
      } catch (error) {
        alert(
          "ERREUR INTERNE\n\nToutes nos excuses, une erreur s'est produite sur nos serveurs.\nVeuillez réessayer ou contacter un administrateur si l'erreur persiste."
        );
        console.log(error);
      }
    },
  },
};
</script>
