<template>
  <base-layout id="debate">
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
          <div class="wrap">
            <documented v-bind:value="$t('documentation.debate_document')">
              <div id="mapContainer">
                <img
                  id="mapImg"
                  :src="debate?.document?.mapLink"
                  alt="map image"
                />
                <table id="myTable" v-if="debate">
                  <tr
                    v-for="(_, indexOfLine) in debate.document.meshLine"
                    :key="indexOfLine"
                  >
                    <td
                      v-for="(_, indexOfColumn) in debate.document.meshColumn"
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
          <comment-widget
            v-for="comment in filteredComments"
            v-show="displayableComments[comment.id]"
            v-bind:displayable-comments="displayableComments"
            v-bind:key="comment.id"
            v-bind:comment="comment"
            v-bind:search="search"
            v-bind:hide-on-scroll="true"
            @tag-edition="editTags($event)"
            @selection-end="selectionHandler($event)"
            @focus-me="
              selectComment($event);
              scrollToComment($event);
            "
            @select-me="selectComment($event)"
            @highlight-me="highlight($event)"
            @mounted="mountChild($event)"
            @report="reportComment($event)"
            @collapse="collapseComment($event)"
          >
          </comment-widget>
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

    <template v-slot:addons>
      <comment-modal
        id="commentModal"
        ref="commentModal"
        v-bind:comment="newComment"
      >
      </comment-modal>

      <tags-modal ref="tagModal"> </tags-modal>
    </template>
  </base-layout>
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
  height: 100%;
}
#mapContainer {
  position: relative;
  height: 50%;
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
    this.fetchData();
    ArenService.CommentListener.listen({
      id: this.$route.params.id,
      onMessage: (comment) => {
        Vue.set(this.displayableComments, comment.id, true);
      },
    });
  },
  computed: {
    filteredComments() {
      return this.debate.comments
        ? this.debate.comments.filter((comment) => !comment.parent)
        : [];
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
  watch: {
    $route() {
      this.fetchData();
      if (this.$route.query.comment) {
        this.scrollToComment(this.$route.query.comment);
      }
    },
    debate() {
      this.displayableComments = {};
      //This is to make all the properties reactive
      this.debate.comments.forEach((comment) =>
        Vue.set(this.displayableComments, comment.id, true)
      );
    },
    leftDisplay(val) {
      if (val === "scraps") {
        this.fetchScraps();
      }
      if (val !== "document") {
        this.updateSpaghettis();
      }
    },
    // search(val) {
    //     if (this.leftDisplay === 'scraps' || this.leftDisplay === 'theme') {
    //         this.updateSpaghettis();
    //     }
    //     this.updateDisplayableComments();
    // },
    sortByPosition(val) {
      if (val) {
        this.debate.deepSortComments((a, b) =>
          a.compareBoundaryPoints(Range.START_TO_START, b)
        );
      } else {
        this.debate.deepSortComments((a, b) =>
          a.created > b.created ? 1 : -1
        );
      }
      if (this.leftDisplay !== "document") {
        // Sort by disply position
        this.$nextTick(() => {
          this.spaghettiData.forEach((s) => {
            s.comments.sort((a, b) => {
              let divA = document.getElementById("comment_" + a.id);
              let divB = document.getElementById("comment_" + b.id);
              return divA.offsetTop - divB.offsetTop;
            });
          });
        });
        this.updateSpaghettis();
      }
    },
  },
  methods: {
    /******************* */
    fetchData() {
      ArenService.Debates.get({
        id: this.$route.params.id,
        onSuccess: (debate) => {
          this.debate = debate;
          let arr = [];
          let col = Array(debate.document.meshColumn).fill(false);
          for (let i = 0; i < debate.document.meshLine; i++) {
            arr.push(col);
          }
          this.arrayData = arr;
        },
        onError: () => {
          this.$router.push("/404");
        },
      });
    },
    fetchScraps() {
      ArenService.Debates.getScraps({
        id: this.debate.id,
        onSuccess: (result) => {
          if (result.length > 0) {
            let arrLength = result.map((r) => r.comments.length).sort();
            let minLength =
              arrLength[arrLength.length - this.scrapsMagicNumber];
            result = result.filter(
              (s) => s.comments.length > 1 && s.comments.length >= minLength
            );
            result.forEach((s, i) => {
              s.id = i;
              s.comments = s.comments.map((cid) =>
                ArenService.Store.get(cid, Comment)
              );
              let range = new Range();
              range.setPathStart(
                this.documentAsDom,
                s.startContainer,
                s.startOffset
              );
              range.setPathEnd(this.documentAsDom, s.endContainer, s.endOffset);
              s.selected = false;
              s.html = range.getHtml();
              s.spaghettis = [];
              s.comments.sort((a, b) => {
                let divA = document.getElementById("comment_" + a.id);
                let divB = document.getElementById("comment_" + b.id);
                return divA.offsetTop - divB.offsetTop;
              });
            });
            this.scraps = result;
            this.updateSpaghettis();
          }
        },
      });
    },
    fetchTheme() {
      if (this.themes.findIndex((t) => t.html === this.themeInput) === -1) {
        ArenService.Debates.getTheme({
          id: this.debate.id,
          query: { theme: this.themeInput },
          onSuccess: (result) => {
            if (result.comments.length > 0) {
              result.comments = result.comments
                .map((cid) => ArenService.Store.get(cid, Comment))
                .filter((c) => c !== -1);
              result.html = result.theme;
              result.spaghettis = [];
              result.selected = false;
              result.comments.sort((a, b) => {
                let divA = document.getElementById("comment_" + a.id);
                let divB = document.getElementById("comment_" + b.id);
                return divA.offsetTop - divB.offsetTop;
              });
              this.themes.push(result);
              this.updateSpaghettis();
              this.themeInput = "";
            } else {
              this.themeHelper = this.$t("helper.theme_not_found");
              setTimeout(() => (this.themeHelper = false), 3500);
            }
          },
        });
      } else {
        this.themeHelper = this.$t("helper.theme_already_set");
        setTimeout(() => (this.themeHelper = false), 3500);
      }
    },
    updateDisplayableComments() {
      let selectedScrapComments = false;
      this.debate.comments.forEach(
        (comment) =>
          (this.displayableComments[comment.id] =
            !selectedScrapComments || selectedScrapComments.includes(comment))
      );
    },
    mountChild(comment) {
      this.mountedChildren++;
      console.table("Mounted Child", this.mountedChildren);
      // If there is a commentId in the url, scroll to its position
      if (
        this.$route.query.comment &&
        this.mountedChildren >= this.debate.commentsCount
      ) {
        this.$nextTick(() => {
          this.scrollToComment(this.$route.query.comment);
        });
      }
      // When a new comment is created, scrool to its position
      if (this.newComment && comment.owner.id === this.$root.user.id) {
        this.$nextTick(() => {
          this.scrollToComment(comment);
          this.newComment = false;
        });
      }
    },
    hidePopup() {
      this.selectedRange = false;
    },
    clearSelection() {
      if (this.$route.query.comment) {
        this.$router.replace({ query: null });
      }
      clearSelection();
    },
    editTags(comment) {
      this.$refs.tagModal.comment = comment;
      let copy = [...comment.proposedTags];
      this.$refs.tagModal.open((returnComment) => {
        if (returnComment) {
          ArenService.Comments.edit({
            data: returnComment,
            loading: false,
          });
        } else {
          comment.proposedTags = copy;
        }
      });
    },
    mailleSelectionHandler(comment, text) {
      if (this.newComment === false) {
        this.newComment = new Comment();
      }
      if (!this.$refs.commentModal.isOpen()) {
        this.newComment.debate = this.debate;
        this.newComment.hypostases = [];
        this.newComment.tags = [];
        this.newComment.parent = comment;
        this.newComment.selection = text;
        this.newComment.startContainer = 0;
        this.newComment.endContainer = 0;
        this.newComment.startOffset = 0;
        this.newComment.endOffset = 0;
      } else {
        this.hidePopup();
      }
      this.createComment();
    },
    selectionHandler(comment) {
      let selection = getSelection();
      let position = selection.anchorNode.compareDocumentPosition(
        selection.focusNode
      );
      let backward = false;
      if (
        (!position && selection.anchorOffset > selection.focusOffset) ||
        position === Node.DOCUMENT_POSITION_PRECEDING
      )
        backward = true;
      if (!selection.isCollapsed) {
        this.selectedRange = selection.getRangeAt(0);
        this.selectedRange.affineToWord();
        if (this.newComment === false) {
          this.newComment = new Comment();
        }
        if (!this.$refs.commentModal.isOpen()) {
          this.newComment.debate = this.debate;
          this.newComment.hypostases = [];
          this.newComment.tags = [];
          this.newComment.parent = comment;
          this.newComment.selection = this.selectedRange.getHtml();
          let container = this.getCommentContainer(this.newComment);
          this.newComment.startContainer = container.getChildPathTo(
            this.selectedRange.startContainer
          );
          this.newComment.endContainer = container.getChildPathTo(
            this.selectedRange.endContainer
          );
          this.newComment.startOffset = this.selectedRange.startOffset;
          this.newComment.endOffset = this.selectedRange.endOffset;

          this.popupPositionFromRange(backward);
        } else {
          this.hidePopup();
        }
      }
    },
    popupPositionFromRange(backward) {
      if (this.selectedRange) {
        backward =
          backward === undefined
            ? this.$refs.arguePopup.classList.contains("top")
            : backward;
        let top = mainContainer.offsetTop + 28;
        let bottom = mainContainer.offsetHeight - window.scrollY + top - 58;
        let rects = this.selectedRange.getClientRects();
        let start = rects[0];
        let end = rects[rects.length - 1];
        if ((backward && start.y > top) || end.y + end.height > bottom) {
          this.popup.x = start.x + window.scrollX;
          this.popup.y = start.y + window.scrollY;
          this.$refs.arguePopup.classList.remove("bottom");
          this.$refs.arguePopup.classList.add("top");
        } else {
          this.popup.x = end.x + end.width + window.scrollX;
          this.popup.y = end.y + end.height + window.scrollY;
          this.$refs.arguePopup.classList.remove("top");
          this.$refs.arguePopup.classList.add("bottom");
        }
      }
    },
    getCommentContainer(comment) {
      return comment.parent
        ? document.querySelector(
            "#comment_" + comment.parent.id + " .argumentation"
          )
        : this.$refs.documentDisplay;
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
            ArenService.Debates.addComment({
              id: this.newComment.debate.id,
              data: this.newComment,
              loading: false,
            });
          }
        });
        this.hidePopup();
        // For the selected text to stay
        this.highlight(this.newComment);
      }
    },
    selectComment(comment) {
      let range = new Range();
      let container = comment.parent
        ? document.querySelector(
            "#comment_" + comment.parent.id + " .argumentation"
          )
        : this.$refs.documentDisplay;
      range.setPathStart(
        container,
        comment.startContainer,
        comment.startOffset
      );
      range.setPathEnd(container, comment.endContainer, comment.endOffset);
      clearSelection();
      getSelection().addRange(range);
      return range;
    },
    highlight(comment) {
      if (!comment.parent) {
        let range = this.selectComment(comment);
        if (!inScrollView(this.$refs.documentContainer, range)) {
          scrollCenter(this.$refs.documentContainer, range);
        }
      } else {
        // Select after scroll is done because of lazy loading
        this.scrollToComment(comment.parent, false, () => {
          this.selectComment(comment);
        });
      }
    },
    scrollToComment(comment, focus = true, callback = () => {}) {
      let commentDiv = document.getElementById(
        "comment_" + (comment.id ? comment.id : comment)
      );
      let previousScroll = this.$refs.commentsContainer.scrollTop;
      if (focus) {
        commentDiv.querySelector(".focuser").focus();
      }
      this.$refs.commentsContainer.scrollTop = previousScroll;
      if (!inScrollView(this.$refs.commentsContainer, commentDiv)) {
        scrollCenter(this.$refs.commentsContainer, commentDiv, callback);
      } else {
        callback();
      }
    },
    scrollToScrap(scrapId, callback) {
      let scrapDiv = document.getElementById("scrap_" + scrapId);
      if (!inScrollView(this.$refs.documentContainer, scrapDiv)) {
        scrollCenter(this.$refs.documentContainer, scrapDiv, callback);
      } else {
        callback();
      }
    },
    spaghetti(scrap, scrapIndex, comment, commentIndex) {
      let scrapId = "scrap_" + scrapIndex;
      let commentId = "comment_" + comment.id;
      let scrapDiv = document.getElementById(scrapId);
      let commentDiv = document.getElementById(commentId);
      // Check if a parent is collapsed, if so remove spaghettis
      let answersDiv = commentDiv.closest(".answers-container");
      while (answersDiv && commentDiv) {
        let commentParentDiv = answersDiv.previousElementSibling;
        if (
          commentDiv.offsetTop - commentParentDiv.offsetTop <
          commentParentDiv.offsetHeight / 2
        ) {
          commentDiv = false;
        } else {
          answersDiv = answersDiv.parentElement.closest(".answers-container");
        }
      }
      // Only if the div exists and is visible
      if (commentDiv && commentDiv.offsetParent !== null) {
        let offsetStart = this.$refs.documentContainer.scrollTop;
        let offsetEnd = this.$refs.commentsContainer.scrollTop;
        let heightStart = Math.min(
          scrapDiv.offsetHeight * 0.5,
          scrap.comments.length * 2
        );
        let deltaY = heightStart / scrap.comments.length;

        return d3
          .linkHorizontal()
          .x((d) => d.x)
          .y((d) => d.y)({
          source: {
            x: scrapDiv.offsetLeft + scrapDiv.offsetWidth,
            y:
              scrapDiv.offsetTop +
              commentIndex * deltaY +
              (scrapDiv.offsetHeight - heightStart) / 2 -
              offsetStart,
          },
          target: {
            x: commentDiv.offsetLeft,
            y: commentDiv.offsetTop + commentDiv.offsetHeight / 2 - offsetEnd,
          },
        });
      }
      return "";
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
    clickSpaghetti() {
      let arrIds = this.spaghettiOver.split("_");
      this.spaghettiCountDown = 2;
      this.scrollToScrap(arrIds[1], () => this.spaghettiCountDown--);
      this.scrollToComment(arrIds[2], true, () => this.spaghettiCountDown--);
    },
    collapseComment(duration) {
      let start = Date.now();
      duration = duration + 500;
      let interval = setInterval(() => {
        this.updateSpaghettis();
        if (Date.now() - start >= duration) {
          clearInterval(interval);
        }
      }, 1000 / 60);
    },
    reportComment(comment) {
      this.$confirm({
        title: this.$t("report"),
        message: this.$t("helper.report_details"),
      });
    },
    /********************* */
    select(l, c) {
      let row = this.arrayData[l].slice();
      row[c] = true;
      this.arrayData.splice(l, 1, row);
      this.mailleSelectionHandler(
        null,
        `Maille ligne ${l + 1}, colonne ${c + 1}`
      );
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
