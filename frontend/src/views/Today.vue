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
          <div v-if="!current" class="task" style="cursor: auto"> Задача не выбрана </div>
          <div v-else class="task" style="cursor: auto"> {{ current.task_name }} </div>
          <div class="timer"> {{ currentTime }} &nbsp;</div>
        </div>
        <button class="button-b" v-if="current && timerStop" @click="toStop">Перерыв</button>
        <button style="width: auto" class="button-g" v-if="current && !timerStop" @click="toStart">Продолжить</button>
        <div class="row">
          <h1>Задачи на сегодня</h1>
          <button class="button-p" @click="openEdit(0)">Добавить задачу</button>
        </div>
        <div class="row" v-for="task in filterTasks" :key="task.id">
          <div class="task"  @click="openEdit(task)">{{task.task_name}}</div>
          <button class="button-g" @click="toStart(task)">Начать</button>
          <button class="button-b" v-if="task.workers.includes(currentUser)" @click="complete(task)">Готово</button>
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

      work: {},

      timerStop: false,
      startDate: new Date(),
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
    this.current = this.tasks.find(item => item.current);
    this.current = this.current ? this.current : {};
    if (!this.currentUser) {
      this.$router.push('/login');
    }

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
      if (task){
        // eslint-disable-next-line no-console
        console.log(!this.current);
        if (this.current) {this.toStop(); this.current.current = false;}
        this.currentTime = '00:00:00';
        this.current = task;
        task.current = true;
        this.timer = 0;
      }
      this.work.start = new Date();
      this.work.end = null;

      this.timerStop = true;
      setTimeout(this.countdown, 1);
    },
    toStop(){

      this.work.end=new Date();
      this.work.user = this.currentUser;
      this.work.task = this.current;
      UserService.saveWork(this.work);
      this.timerStop = false;
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
    countdown() {
      let t = new Date().getTime() - this.work.start.getTime();
      t+=this.timer;
      let seconds = Math.floor((t / 1000) % 60);
      let minutes = Math.floor((t / 1000 / 60) % 60);
      let hours = Math.floor((t / (1000 * 60 * 60)) % 24);
      //let days = Math.floor(t / (1000 * 60 * 60 * 24));
      if (this.timerStop) {
        if (seconds < 10) seconds = '0' + seconds;
        if (minutes < 10) minutes = '0' + minutes;
        if (hours < 10) hours = '0' + hours;
        this.currentTime = hours+':'+minutes+':'+seconds;
        setTimeout(this.countdown, 1000);
      } else {
        this.timer = t;
      }
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
