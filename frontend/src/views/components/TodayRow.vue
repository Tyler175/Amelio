<template>
  <div class="row">
    <div class="task" @click="edit">{{task.task_name}}</div>
    <button class="button-g" @click="select">Начать</button>
    <button class="button-b" @click="finish">Готово</button>
  </div>
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