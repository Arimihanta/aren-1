<template>
  <modal-layout>
    <template v-slot:title>{{ choice.title }}</template>

    <div class="card-image light-color valign-wrapper">
      <img
        v-if="choice.img"
        class="category-picture"
        v-bind:src="choice.img"
        alt="image"
      />
    </div>
    <div>
      <div>
        <div v-html="choice.description"></div>
      </div>
      <div>
        <a :href="choice.url" target="_blank">{{ choice.url }}</a>
      </div>
    </div>

    <template v-slot:footer>
      <button @click="close()" class="waves-effect waves-green btn-flat">
        ok
      </button>
    </template>
  </modal-layout>
</template>

<script>
module.exports = {
  mixins: [VueModal],
  data() {
    return {
      choice: false,
    };
  },
};
</script>
