<template>
  <div>
    <nav class="header">

      <div class="header-menu">
        <a href class="brand" @click.prevent>Amelio</a>
        <li class="nav-item">
          <router-link to="/home" class="nav-link">
            Главная
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/home" class="nav-link">
            Пункт меню
          </router-link>
        </li>
        <li class="nav-item">
          <router-link to="/home" class="nav-link">
            Пункт меню
          </router-link>
        </li>

      </div>

      <div v-if="!currentUser" class="action">

        <li class="nav-item" >
          <router-link to="/login" class="nav-link">
            Войти
          </router-link>
        </li>
      </div>

      <div v-if="currentUser" class="action">
        <li class="nav-item">
          <router-link to="/profile" class="nav-link">

            {{ currentUser.username }}
          </router-link>
        </li>
        <li class="nav-item">
          <a class="nav-link" href @click.prevent="logOut">
            Выйти
          </a>
        </li>
      </div>
    </nav>
    <div class="content">
        <h3>{{content}}</h3>
    </div>
  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Home',
  data() {
    return {
      content: ''
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },

  },
  methods: {
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  },
  mounted() {
    UserService.getPublicContent().then(
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
  }
};
</script>
