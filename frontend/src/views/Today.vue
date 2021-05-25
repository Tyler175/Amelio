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
        <router-link to="/statistics">Статистика</router-link>
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
          <div v-if="!current.taskName" class="task" style="cursor: auto"> Задача не выбрана </div>
          <div v-else class="task" style="cursor: auto"> {{ current.taskName }} </div>
          <div class="timer"> {{ currentTime }} &nbsp;</div>
        </div>
        <button class="button-b" v-if="current.taskName && timerId != null" @click="toStop">Перерыв</button>
        <button style="width: auto" class="button-g" v-if="current.taskName && timerId === null" @click="toStart(0)">Продолжить</button>
        <div class="row">
          <h1>Задачи на сегодня</h1>
          <button class="button-p" @click="openEdit(0)">Добавить задачу</button>
        </div>
        <div class="row" v-for="task in filterTasks" :key="task.id">
          <div class="task"  @click="openEdit(task)">{{task.taskName}}</div>
          <button class="button-g" @click="toStart(task)">Начать</button>
        </div>
      </div>
    </div>


  </div>
</template>

<script>
import UserService from '../services/user.service';
import TaskEdit from "@/views/components/TaskEdit.vue";

export default {
  name: 'Today',
  data() {
    return {
      isHidden: true,
      tasks: [],
      content: '',
      task: {},
      current: {},

      work: null,

      checkId: null,
      timerId: null,
      startDate: new Date(),
      currentTimer: 0,
      timer: 0,
      currentTime: '00:00:00'
    };
  },
  components:{
    'v-taskEdit' : TaskEdit
  },
  computed: {
    filterTasks(){
    return this.tasks.filter(item =>
          (new Date(item.task_start).getTime() <= new Date().getTime()) &&
          (new Date(item.task_end).getTime() >= new Date().getTime()) && !item.current
      );
    },
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
    if (!this.currentUser) {
      this.$router.push('/login');
    }
    UserService.getToday().then(
      response => {
        this.tasks = response.data;
        this.current = this.tasks.find(item => item.current);
        this.current = this.current ? this.current : {};
        if (this.current.id) UserService.getCurrentWork(this.currentUser.id, this.current.id).then(
            response => {
              this.work = response.data;
              if (!this.work) {
                this.work = {};
              } else{
                this.timerId = setTimeout(this.countdown, 1);
              }
              if (this.work.startDate) this.timer = new Date().getTime() - this.work.startDate.getTime();
              let seconds = Math.floor((this.timer / 1000) % 60);
              let minutes = Math.floor((this.timer / 1000 / 60) % 60);
              let hours = Math.floor((this.timer / (1000 * 60 * 60)) % 24);
              //let days = Math.floor(t / (1000 * 60 * 60 * 24));

              if (seconds < 10) seconds = '0' + seconds;
              if (minutes < 10) minutes = '0' + minutes;
              if (hours < 10) hours = '0' + hours;
              this.currentTime = hours+':'+minutes+':'+seconds;
            }
        ); else {
          UserService.saveTimer(this.currentUser.id, 0);
        }


      },
      error => {
        this.content =
          (error.response && error.response.data && error.response.data.message) ||
          error.message ||
          error.toString();
      }
    );


    UserService.getTimer(this.currentUser.id).then(
        response => {
          this.timer = response.data;
        }
    );
    //

    //

  },


  methods: {
    openEdit(task){
      if (task === 0) {this.task = {}}
      else {
        this.task = {}
        Object.assign(this.task, task);
      }
      this.isHidden = false;
    },
    toStart(task){
      if (task){ //if we choose task from list
        if (this.current.taskName) {
          this.toStop();
          this.current.current = false;
          UserService.putTask(this.current);
        }
        this.currentTime = '00:00:00';
        this.current = task;
        this.current.current = true;
        UserService.putTask(this.current);
        this.timer = 0;
        UserService.saveTimer(this.currentUser.id, this.timer);
      } //continue after break

      if (this.work === '') this.work = {} //i dont know how it became a string
      this.work.task = this.current;
      this.work.workStart = new Date();
      this.work.workEnd = null;

      if (this.checkId != null) clearTimeout(this.checkId);
      this.checkId = setTimeout(UserService.postWork, 1000, this.work);

      this.timerId = setTimeout(this.countdown, 1);
    },
    toStop(){
      if (this.currentTimer - this.timer > 1000)
      {

        this.work.workEnd = new Date();
        this.timer = this.currentTimer;

        UserService.saveTimer(this.currentUser.id, this.timer);
        UserService.setWorkEnd(this.work);

      }

      clearTimeout(this.timerId);
      this.timerId = null;
    },
    countdown() {
      this.currentTimer = new Date().getTime() - new Date(this.work.workStart).getTime();
      this.currentTimer += this.timer;
      let seconds = Math.floor((this.currentTimer / 1000) % 60);
      let minutes = Math.floor((this.currentTimer / 1000 / 60) % 60);
      let hours = Math.floor((this.currentTimer / (1000 * 60 * 60)) % 24);
      //let days = Math.floor(t / (1000 * 60 * 60 * 24));

      if (seconds < 10) seconds = '0' + seconds;
      if (minutes < 10) minutes = '0' + minutes;
      if (hours < 10) hours = '0' + hours;
      this.currentTime = hours+':'+minutes+':'+seconds;
      this.timerId = setTimeout(this.countdown, 1000);

    },
    post(task){
      this.tasks.push(task);
      Object.assign(this.task, task);
    },
    put(task){
      this.tasks.splice(this.tasks.findIndex(item => item.id == task.id), 1, task);

    },
    del(task){
      this.tasks.splice(this.tasks.findIndex(item => item.id === task.id),1);
    }
  }
};
</script>
