<template>

</template>

<script>

export default {
  name: "TaskRow",
  props: ['task', 'editMethod', 'toStart', 'complete'],
  methods: {
    edit() {
      this.editMethod(this.task);
    },
    finish(){
      this.complete(this.task);
    },
    select(){
      this.toStart(this.task);
    },
    formatDate(date) {
      let formDate = new Date(date);
      let dd = formDate.getDate();
      if (dd < 10) dd = '0' + dd;
      let mm = formDate.getMonth() + 1;
      if (mm < 10) mm = '0' + mm;
      let yy = formDate.getFullYear() % 100;
      if (yy < 10) yy = '0' + yy;
      return dd + '.' + mm + '.' + yy;
    }
  }
}
</script>