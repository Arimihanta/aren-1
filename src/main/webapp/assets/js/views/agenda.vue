<template>
  <div class="box-container">
    <base-layout id="testfunc">
      <template v-slot:title>
        <h1>Agenda</h1>
      </template>
      <div class="space-top block">
        <label>
          <span>Titre</span>
        </label>
        <span class="block">{{ program.title }}</span>

        <label class="block space-top">
          <span>Description</span>
        </label>
        <span class="block">{{ program.description }}</span>

        <label class="block space-top">
          <span class="block">Date</span>
        </label>
        <span class="block">{{
          new Date(program.date).toLocaleString()
        }}</span>

        <label class="block space-top">
          <span>URL</span>
        </label>
        <span class="block"
          ><a :href="program.url" target="_blank">{{ program.url }}</a></span
        >
      </div>
      <div class="space-top"></div>
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
</style>

<script>
const getUrl = window.location;
const baseUrl =
  getUrl.protocol + "//" + getUrl.host + "/" + getUrl.pathname.split("/")[1];
module.exports = {
  data() {
    return {
      program: false,
    };
  },
  created() {
    this.fetchProgram();
  },
  methods: {
    fetchProgram: async function () {
      try {
        let program = await axios.get(
          `${baseUrl}/ws/agenda/calendars/${this.$route.query.id}`
        );
        this.program = program.data;
      } catch (expetion) {
        console.error(expetion);
      }
    },
  },
};
</script>