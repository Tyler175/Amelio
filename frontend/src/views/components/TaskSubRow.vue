<style scoped>
.task{
  padding: 0 10px;
  display: flex;
  justify-content: space-between;
}
i{
  margin-right: 15%;
}
</style>
<template>
  <div class="row">
    <div class="task" @click="edit">
      {{ child.task_name }} <i>{{formatDate(child.task_start)}} - {{formatDate(child.task_end)}}</i>
    </div>
    <span>
      <input type="button" value="X" @click="deleteTask"/>
    </span>
    {{message}}
  </div>
</template>

<script>
import UserService from "@/services/user.service";

export default {
  name: "TaskSubRow",
  props: ['child', 'editMethod', 'del'],
  data() {
    return {
      message: ''
    };
  },
  methods: {
    edit() {
      this.editMethod(this.child);
    },
    deleteTask(){
      UserService.delTask(this.child.id).then(
          response => {
            this.del(this.child);
            response.data;
            this.message = 'Задача удалена!'
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
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