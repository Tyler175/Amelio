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
        <li v-if="showModeratorBoard">
          <router-link to="/mod">Панель модератора</router-link>
        </li>
        <li v-if="showAdminBoard">
          <router-link to="/admin">Панель администратора</router-link>
        </li>
      </div>
    <div class="content">
      <div>
        <h3>
          Привет, <strong>{{currentUser.username}}</strong>
        </h3>
        <p>
          <strong>Id:</strong>
          {{currentUser.id}}
        </p>
        <p>
          <strong>Email:</strong>
          {{currentUser.email}}
        </p>
        <strong>Authorities:</strong>
        <ul>
          <li v-for="(role,index) in currentUser.roles" :key="index">{{role}}</li>
        </ul>
        <a class="logout" href @click.prevent="logOut">
          Выйти
        </a>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'Profile',
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
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  }
};
</script>