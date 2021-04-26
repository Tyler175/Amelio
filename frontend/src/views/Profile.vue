<style scoped>
  .button-g{
    width: auto;
    height: auto;
    padding: 10px 15px;
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
          <router-link to="/user">Статистика</router-link>
        </li>
        <li>
          <router-link to="/projects">Проекты</router-link>
        </li>
        <li>
          <router-link to="/profile" class="active" @click.prevent>Мой профиль</router-link>
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
        <div class="row" style="margin-bottom: 20px ">
          <div class="photo" >Фото</div>
          <div class="ras">
            <div class="inf"> Имя: {{currentUser.username}}</div>
            <div class="inf"> Контактные данные: {{currentUser.email}}</div>
            <div class="inform">О себе</div>
          </div>
        </div>
        <div class="changepass">
          <div>
            <div class="pas">Пароль:</div>
            <div class="pas">Новый пароль:</div>
            <div class="pas">Повторите новый пароль:</div>
            <div class="button-g">Сменить пароль</div>
          </div>
          <div class="block">
            <div class="pas">Ваша почта: {{currentUser.email}}</div>
            <div class="button-g">Сменить адрес электронной почты</div>
          </div>
        </div>
        <strong>Authorities:</strong>
        <ul>
          <li v-for="(role,index) in currentUser.roles" :key="index">{{role}}</li>
        </ul>
        <a class="logout" href @click.prevent="logOut">
          Выйти
        </a>
        <!-- END -->

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