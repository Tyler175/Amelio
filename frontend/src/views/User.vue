<style scoped>
.table:first-child .label {
  padding-top: 10px;
}
.table:first-child .row{
  align-items: flex-start;
}
</style>
<template>
  <div class="container">
      <div v-if="currentUser" class="user-menu">
        <li>
          <router-link to="/today">To Do</router-link>
        </li>
        <li>
          <router-link to="/tasks">Задачи</router-link>
        </li>
        <li>
          <router-link to="/user" class="active" @click.prevent>Статистика</router-link>
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
        <div class="row" style="margin-bottom: 20px ">
          <div class="label"> Дата:</div>
          <input class="tasker" type="date" min="2015-01-01" max="2030-01-01"/>
          <div class="label"> Общее время работы:</div>
          <div class="number">06:23</div>
        </div>
        <div class= "row">
          <div class="tab" v-bind:class="{ active: !isActive }" v-bind:disabled="!isActive" @click="changeActive">Выполнено</div>
          <div class="tab" v-bind:class="{ active: isActive }" v-bind:disabled="isActive" @click="changeActive" style="margin-left: -2px ">Не выполнено</div>
        </div>
        <div class="table" v-bind:class="{ hidden: isActive }">
          <div class="row" style="justify-content: flex-end;margin-top: 0" >
            <div class="label"> Время работы</div>
            <div class="label" style="margin-left: 25px"> Часы</div>
          </div>
          <div class="row">
            <div class="task">Задача один</div>
            <ul class="time-list">
              <li>10:00 - 13:23</li>
              <li>11:00 - 13:23</li>
              <li>11:00 - 13:23</li>
            </ul>
            <div class="label"> 03:43 </div>
          </div>
          <div class="row">
            <div class="task">Задача один</div>
            <ul class="time-list">
              <li>Не указана</li>
            </ul>
            <div class="label"> 00:00 </div>
          </div>
        </div>
        <div class="table" v-bind:class="{ hidden: !isActive }">
          <div class="row" style="justify-content: flex-end;margin-top: 0; margin-right: 210px" >
            <div class="label"> Дедлайн</div>
          </div>
          <div class="row">
            <div class="task">Первая задача</div>
            <div class="label" style="margin-left:-40px; margin-right:20px">14.04.21</div>
            <button class="button-b">Готово</button>
          </div>
        </div>
        <!-- END -->
        <h3>{{content}}</h3>
      </div>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'User',
  data() {
    return {
      content: '',
      isActive: true,
    };
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
    UserService.getUserBoard().then(
      response => {
        this.content = response.data;
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
    changeActive(){
      this.isActive=!this.isActive;
    }
  }
};
</script>