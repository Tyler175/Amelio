<template>
  <div class="container">
    <div class="content" style="display: flex; justify-content:space-around">
      <div class="user-menu" style="width: 20%">
        <li>
          <router-link to="/profile">Мой профиль</router-link>
        </li>
        <li>
          <router-link v-if="currentUser" to="/user">Задачи</router-link>
        </li>
        <li v-if="showModeratorBoard">
          <router-link to="/mod" >Панель модератора</router-link>
        </li>
        <li v-if="showAdminBoard">
          <router-link to="/admin" @click.prevent>Панель администратора</router-link>
        </li>
      </div>

      <div style="width: 50%">
        <h3>{{content}}</h3>
      </div>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Admin',
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
    UserService.getAdminBoard().then(
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
