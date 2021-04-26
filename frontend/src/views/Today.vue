<template>
  <div class="container">
    <v-taskEdit v-if='!isHidden' v-on:hide="isHidden = true"
                :task="task" :post="post" :put="put" :del="del"></v-taskEdit>

    <div v-if="currentUser" class="user-menu">
      <li>
        <router-link to="/today" class="active" @click.prevent>To Do</router-link>
      </li>
      <li>
        <router-link to="/tasks">Задачи</router-link>
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
        <h1>Текущая задача</h1>
        <div class="row">
          <div class="task">Выбранная задача</div>
          <div v-if="!current" class="task"> Выбранная задача </div>
          <div v-else> {{ current.task_name }} </div>
          <div class="timer"> 05:15 &nbsp;</div>
        </div>
        <button class="button-g">Перерыв</button>
        <button class="button-b" @click="complete">Готово</button>
        <div class="row">
          <h1>Задачи на сегодня</h1>
          <button class="button-p">Добавить задачу</button>
        </div>
        <v-todayRow v-for="task in tasks" :key="task.id" :task="task" :editMethod="openEdit"
                    :toStart="toStart" :complete="complete"></v-todayRow>
        <!-- END -->

      </div>
    </div>


  </div>
</template>

<script>
import UserService from '../services/user.service';
import TaskEdit from "@/views/components/TaskEdit.vue";
import TodayRow from "@/views/components/TodayRow";

export default {
  name: 'Today',
  data() {
    return {
      isHidden: true,
      tasks: [],
      content: '',
      task: {},
      current: {}
    };
  },
  components:{
    'v-taskEdit' : TaskEdit,
    'v-todayRow' : TodayRow
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
    UserService.getToday().then(
      response => {
        this.tasks = response.data;
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
    toStart(){

    },
    toStop(){

    },
    complete(task){
      if (task){
        task.taskComplete = true;
        UserService.putTask(task).then(
            response => {
              this.del(response.data);
              this.message = ''
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      } else if(this.current) {
        this.current.taskComplete = true;
        UserService.putTask(this.current).then(
            response => {
              this.del(response.data);
              this.current = {};
              this.message = ''
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      }

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
    }
  }
};
</script>
