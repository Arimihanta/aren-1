<template>
  <base-layout id="document">
    <template v-slot:title>
      <h1>{{ $t("document") }} -</h1>
      <h1>
        <text-input
          class="inline"
          v-bind:disabled="!editionMode"
          ref="name"
          type="textarea"
          v-model="document.name"
          v-bind:placeholder="$t('title')"
        ></text-input>
      </h1>
      <label>{{ $t("by").toLowerCase() }}</label>
      <text-input
        class="author inline"
        v-bind:disabled="!editionMode"
        ref="author"
        type="textarea"
        v-model="document.author"
        v-bind:placeholder="$t('author')"
        maxlength="30"
      ></text-input>
    </template>

    <template v-slot:right>
      <span
        :disabled="document.mapLink.length<=0"
        v-if="editionMode"
        class="waves-effect waves-light btn"
        @click="saveDocument()"
      >
        {{ $t("save") }}
      </span>
      <span
        v-if="editionMode"
        class="waves-effect waves-light btn"
        @click="cancelEdition()"
      >
        {{ $t("cancel") }}
      </span>

      <span
        v-if="!editionMode"
        class="waves-effect waves-light btn"
        @click="
          fetchData();
          editionMode = true;"
      >
        {{ $t("modify") }}
      </span>
    </template>
    <div id="cartoDetails" v-if="editionMode">
      <div>
        <label for="loadCarto" class="btn">Charger la carte</label>
        <input
          id="loadCarto"
          style="visibility: hidden;"
          type="file"
          @change="onCartoSelected"
        />
      </div>
      <div class="cartoInformation-bloc">
        <div class="mailleInfo">
          <label>Nombre de ligne </label>
          <input type="text" maxlength="2" v-model.number="document.meshLine" />
        </div>
        <div class="mailleInfo">
          <label>Nombre de colonne </label>
          <input type="text" maxlength="2" v-model.number="document.meshColumn" />
        </div>
      </div>
    </div>
    
    <div id="mapContainer" v-if="imgUrl">
      <img id="mapImg" :src="imgUrl" alt="map image" />
      <table>
        <tr v-for="(_, indexOfLine) in document.meshLine" :key="indexOfLine">
          <td
            v-for="(_, indexOfColumn) in document.meshColumn"
            :key="indexOfColumn"
          ></td>
        </tr>
      </table>
    </div>
    <!-- <div id="documentDisplay" v-bind:class="[ 'container', { 'in-edition': editionMode === true } ]">
            
            <tooltiped tag="div"
                       v-bind:value="(editionMode && document.debatesCount > 0) ? $t('helper.cannot_edit_document') : '' ">
                <wysiwyg-editor v-model="document.content"
                                v-bind:enabled="editionMode && !document.debatesCount">
                </wysiwyg-editor>
            </tooltiped>
        </div> -->
  </base-layout>
</template>

<style scoped>
#loadCarto {
  padding: 10px;
  margin: 15px;
}
#cartoDetails {
  display: inline;
}
.mailleInfo {
  margin: 10px;
  width: 110px;
}
.cartoInformation-bloc {
  display: flex;
}
/**********************/
table,
td,
th {
  border: 1px solid black;
}
table {
  border-collapse: collapse;
  position: absolute;
  top: 0;
  width: 100%;
  height: 100%;
}
#mapContainer {
  position: relative;
}
#mapImg {
  width: 100%;
  height: 100%;
}
</style>

<script>
module.exports = {
  data() {
    return {
      document: false,
      unmodifiedDocument: new Document(),
      editionMode: false,
      selectedCarto: null,
      imgUrl: null,
      // nbOfLines: 0,
      // nbOfColumns: 0,
    };
  },
  created() {
    if (!this.$root.user.is("MODO")) {
      this.$router.push("/404");
      return;
    }
  },
  mounted() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      if (this.$route.params.id !== "new") {
        ArenService.Documents.get({
          id: this.$route.params.id,
          onSuccess: (document) => {
            this.document = document;
            this.copyDocument(this.document, this.unmodifiedDocument);
            this.imgUrl = document.mapLink;
          },
        });
      } else {
        this.document = new Document();
        this.document.type = "CARTO";
        this.document.mapLink="";
        this.document.category = new Category();
        this.document.category.id = this.$route.query.category;
        this.document.category.type = "CARTO";
        this.editionMode = true;
      }
    },
    cancelEdition() {
      this.copyDocument(this.unmodifiedDocument, this.document);
      this.editionMode = false;
      if (this.$route.params.id === "new") {
        this.$router.push("/documents-debats-sur-cartes");
      }
    },
    saveDocument() {
      this.editionMode = false;
      //this.document.mapLink = this.getBase64Image();
      ArenService.Documents.createOrUpdate({
        data: this.document,
        onSuccess: (document) => {
          if (this.$route.params.id === "new") {
            this.$router.push("/documents-debats-sur-cartes/" + document.id);
          }
        },
      });
    },
    copyDocument(from, to) {
      to.name = from.name;
      to.author = from.author;
      to.content = from.content;
    },
    onCartoSelected(event) {
      const file = event.target.files[0];
      this.selectedCarto = file;
      this.imgUrl = URL.createObjectURL(file);
      this.document.mapLink = this.getBase64Image();
    },
    getBase64Image() {
      // Create an empty canvas element
      let img = document.getElementById("mapImg");
      var canvas = document.createElement("canvas");
      canvas.width = img.width;
      canvas.height = img.height;

      // Copy the image contents to the canvas
      var ctx = canvas.getContext("2d");
      ctx.drawImage(img, 0, 0);

      // Get the data-URL formatted image
      // Firefox supports PNG and JPEG. You could check img.src to
      // guess the original format, but be aware the using "image/jpg"
      // will re-encode the image.
      var dataURL = canvas.toDataURL("image/png");
      //let dt = dataURL.replace(/^data:image\/(png|jpg);base64,/, "");
      console.log(dataURL);
      return dataURL;
  }
  },
  components: {
    "wysiwyg-editor": vueLoader("components/widgets/wysiwygEditor"),
  },
};
</script>
