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
          <router-link to="/projects" class="active" @click.prevent>Проекты</router-link>
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

        <div class="row">
          <h1>Проекты</h1>
          <button class="button-p">Создать проект</button>
        </div>
        <div class="row">
          <div class="task">Проект первый, мой</div>
          <button class="button-g">Изменить</button>
          <button class="button-b">Удалить</button>
        </div>
        <div class="row">
          <div class="task">Проект второй, чужой</div>
          <button class="button-s">Покинуть проект</button>
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
  name: 'Projects',
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
    UserService.getProjects().then(
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
