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
        <router-link to="/user">Статистика</router-link>
      </li>
      <li>
        <router-link to="/projects">Проекты</router-link>
      </li>
      <li>
        <router-link to="/profile" @click.prevent>Мой профиль</router-link>
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
          <div class="timer"> 05:15 &nbsp;</div>
        </div>
        <button class="button-g">Перерыв</button>
        <button class="button-b">Готово</button>
        <div class="row">
          <h1>Задачи на сегодня</h1>
          <button class="button-p">Добавить задачу</button>
        </div>
        <div class="row">
          <div class="task">Задача один</div>
          <button class="button-g">Начать</button>
          <button class="button-b">Готово</button>
        </div>
        <div class="row">
          <div class="task">Задача два</div>
          <button class="button-g">Начать</button>
          <button class="button-b">Готово</button>
        </div>
        <!-- END -->

      </div>
    </div>


  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Today',
  data() {
    return {
      content: ''
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
    UserService.getToday().then(
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
  }
};
</script>
