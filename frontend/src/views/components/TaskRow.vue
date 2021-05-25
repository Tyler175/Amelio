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
  <div>
    <div class="row">
      <div class="task" @click="edit">
        {{ task.parent.taskName }} <i>{{formatDate(task.parent.task_start)}} - {{formatDate(task.parent.task_end)}}</i>
      </div>
      <span>
        <input type="button" value="X" @click="deleteTask"/>
      </span>
    </div>
    <ul>
      <v-taskSubRow v-for="child in task.children" :key="child.id" :child="child" :editMethod="editMethod" :del="del"></v-taskSubRow>
      <button class="button-b" @click="createTask" style="width: auto">Добавить подзадачу</button>
    </ul>

  </div>
</template>

<script>
import TaskSubRow from "@/views/components/TaskSubRow";
import UserService from "@/services/user.service";

export default {
  name: "TaskRow",
  props: ['task', 'editMethod', 'del'],
  data() {
    return {
      message: ''
    };
  },
  components: {
    'v-taskSubRow' : TaskSubRow
  },
  methods: {
    edit() {
      this.editMethod(this.task.parent);
    },
    createTask(){
      this.editMethod({parent: this.task.parent.id})
    },
    deleteTask(){
      UserService.delTask(this.task.parent.id).then(
          response => {
            this.del(this.task.parent);
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