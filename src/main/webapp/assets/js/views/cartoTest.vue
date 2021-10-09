<template>
  <div>
    <base-layout id="cartodebates">
      <template v-slot:title>
        <h1>Debat sur carte</h1>
      </template>

      <!-- ************************************************* -->

      <div id="mainContainer">
        <div id="documentContainer">
          <div
            class="scroll-area"
            @scroll="
              hidePopup();
              updateSpaghettis();
            "
            ref="documentContainer"
          >
            <div v-if="leftDisplay === 'document'" class="wrap">
              <documented v-bind:value="$t('documentation.debate_document')">
                <div id="mapContainer">
                  <img
                    id="mapImg"
                    src="assets/img/mapimg.png"
                    alt="map image"
                  />
                  <table id="myTable">
                    <tr
                      v-for="(_, indexOfLine) in nbOfLines"
                      :key="indexOfLine"
                    >
                      <td
                        v-for="(_, indexOfColumn) in nbOfColumns"
                        :key="indexOfColumn"
                        @click="select(indexOfLine, indexOfColumn)"
                        :class="{
                          selected: arrayData[indexOfLine][indexOfColumn],
                        }"
                      ></td>
                    </tr>
                  </table>
                </div>
              </documented>

              <!-- <documented v-bind:value="$t('documentation.debate_bulets')">
                <bullets-container
                  @over-bullet="selectComment($event)"
                  @click-bullet="
                    selectComment($event);
                    scrollToComment($event);
                  "
                  v-bind:displayable-comments="displayableComments"
                  v-bind:comments="filteredComments"
                >
                </bullets-container>
              </documented> -->
            </div>
          </div>
        </div>

        <div
          id="commentsContainer"
          v-bind:class="{ loading: mountedChildren < debate.commentsCount }"
        >
          <div
            class="scroll-area"
            @scroll="
              hidePopup();
              updateSpaghettis();
            "
            ref="commentsContainer"
          >
            <comment-widget v-for="comment in filteredComments" v-bind:key="comment.id" v-bind:comment="comment"> </comment-widget>
          </div>
        </div>
      </div>

      <div
        v-show="selectedRange"
        class="selection_popup z-depth-2"
        ref="arguePopup"
        v-bind:style="'top: ' + popup.y + 'px; left: ' + popup.x + 'px;'"
        @click="createComment()"
        @mousedown.stop=""
        @mouseup.stop=""
      >
        {{ $t("argue").toLowerCase() }}
      </div>

      <!-- ************************************************** -->

      <template v-slot:addons>
        <comment-modal
          id="commentModalMap"
          ref="commentModal"
          v-bind:comment="newComment"
        >
        </comment-modal>

        <tags-modal ref="tagModal"> </tags-modal>
      </template>
    </base-layout>
  </div>
</template>
<style scoped>
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
.selected {
  background-color: rgb(0 0 0 / 26%);
}
</style>
<script>
module.exports = {
  data() {
    return {
      nbOfLines: 5,
      nbOfColumns: 9,
      arrayData: false,
      // ********************************

      debate: false,
      newComment: false,
      sortByPosition: false,
      popup: { x: -9999, y: -9999 },
      displayableComments: {},
      selectedRange: false,
      search: "",
      mountedChildren: 0,
      leftDisplay: "document",
      scrapsMagicNumber: 2,
      spaghettiOver: "",
      spaghettiCountDown: 0,
      scraps: [],
      themes: [],
      themeInput: "",
      themeHelper: false,

      // ****************************************
    };
  },

  created() {
    this.initDataArray();
  },
  computed: {
    filteredComments() {
      return this.debate.comments ? this.debate.comments.filter((comment) => !comment.parent) : []
    },
    documentAsDom() {
      let div = document.createElement("div");
      div.innerHTML = this.debate.document.content;
      return div;
    },
    spaghettiData() {
      if (this.leftDisplay === "theme") {
        return this.themes;
      } else {
        return this.scraps;
      }
    },
  },
  methods: {
    initDataArray() {
      let arr = [];
      let col = Array(this.nbOfColumns).fill(false);
      for (let i = 0; i < this.nbOfLines; i++) {
        arr.push(col);
      }
      this.arrayData = arr;
    },
    select(l, c) {
      let row = this.arrayData[l].slice();
      row[c] = true;
      this.arrayData.splice(l, 1, row);
      this.createComment();
    },
    createComment() {
      if (!this.$root.user.is("USER")) {
        this.$confirm({
          title: this.$t("not_connected"),
          message: this.$t("helper.not_connected"),
          isInfo: true,
        });
      } else {
        this.$refs.commentModal.open((returnValue) => {
          if (returnValue) {
            // ArenService.Debates.addComment({
            //   id: this.newComment.debate.id,
            //   data: this.newComment,
            //   loading: false,
            // });
          }
        });
        // this.hidePopup();
      }
    },
    updateSpaghettis() {
      if (this.leftDisplay !== "document") {
        this.$nextTick(() => {
          this.spaghettiData.forEach((s, scrapIndex) => {
            s.spaghettis = s.comments.map((c, comIndex) =>
              this.spaghetti(s, scrapIndex, c, comIndex)
            );
            s.color = d3.interpolateRainbow(
              scrapIndex / this.spaghettiData.length
            );
          });
        });
      }
    },
    hidePopup() {
      this.selectedRange = false;
    },
  },

  components: {
    "tags-modal": vueLoader("components/modals/tagsModal"),
    "comment-modal": vueLoader("components/modals/commentModal"),

    "comment-widget": vueLoader("components/widgets/comment"),
    "bullets-container": vueLoader("components/widgets/bulletsContainer"),
  },
};
</script>
