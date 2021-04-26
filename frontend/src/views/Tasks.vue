<style scoped>

</style>
<template>
  <div class="container">
    <v-taskEdit v-if='!isHidden' v-on:hide="isHidden = true"
                :task="task" :post="post" :put="put" :del="del"></v-taskEdit>

    <div v-if="currentUser" class="user-menu">
        <li>
          <router-link to="/today">To Do</router-link>
        </li>
        <li>
          <router-link to="/tasks" class="active" @click.prevent>Задачи</router-link>
        </li>
        <li>
          <router-link to="/user">Статистика</router-link>
        </li>
        <li>
          <router-link to="/projects">Проекты</router-link>
        </li>
        <li>
          <router-link to="/profile">Мой профиль</router-link>
        </li>
        <li v-if="showModeratorBoard">
          <router-link to="/mod">Панель модератора</router-link>
        </li>
        <li v-if="showAdminBoard">
          <router-link to="/admin">Панель администратора</router-link>
        </li>
      </div>
    <div class="content">
      <div>
        <ul>
            <v-taskRow v-for="task in tasks" :key="task.id" :task="task" :editMethod="openEdit" :del="del"></v-taskRow>
        </ul>
        <button class="button-p" @click="openEdit">Добавить задачу</button>

        {{content}}
      </div>
    </div>
  </div>
</template>

<script>
import UserService from '../services/user.service';
import TaskEdit from "@/views/components/TaskEdit.vue";
import TaskRow from "@/views/components/TaskRow.vue";

export default {
  name: 'Tasks',
  data() {
    return {
      isHidden: true,
      tasks: [],
      content: '',
      task: {}
    };
  },
  components:{
    'v-taskEdit' : TaskEdit,
    'v-taskRow' : TaskRow
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    showAdminBoard() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ADMIN');
      }

      return false;
    },
    showModeratorBoard() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_MODERATOR');
      }

      return false;
    }
  },
  mounted() {
    UserService.getTasks().then(
      response => {
        this.tasks = response.data;
        // eslint-disable-next-line no-console
        //console.log(this.tasks);
      },
      error => {
        this.content =
          (error.response && error.response.data && error.response.data.message) ||
          error.message ||
          error.toString();
      }
    );
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  },
  methods: {
    openEdit(task){
      this.task = task;
      this.isHidden = false;
    },
    post(task){
      if(task.parent){
        let i = this.tasks.findIndex(item => item.parent.id == task.parent);
        this.tasks[i].children.push(task);
      }else{
        this.tasks.push({parent: task, children: []});
      }
    },
    put(task){
      if(task.parent){
        let i = this.tasks.findIndex(item => item.parent.id == task.parent);
        this.tasks[i].children[this.tasks[i].children.findIndex(item => item.id == task.id)] = task;
      }else{
        this.tasks[this.tasks.findIndex(item => item.parent.id == task.id)].parent = task;
      }
    },
    del(task){
      if(task.parent){
        let i = this.tasks.findIndex(item => item.parent.id == task.parent);
        this.tasks[i].children.splice(this.tasks[i].children.findIndex(item => item.id == task.id),1);
      }else{

        this.tasks.splice(this.tasks.findIndex(item => item.parent.id == task.id),1);
      }
    },
    test(){
      // eslint-disable-next-line no-console
      console.log(this.tasks);
    }
  }
};
</script>
