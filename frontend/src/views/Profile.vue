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
          <router-link to="/statistics">Статистика</router-link>
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

          <div class="row inf"> Имя: {{currentUser.username}}</div>
        <div class="row" style="margin-bottom: 20px ">
          <div class="photo" >Фото</div>
          <div class="ras">
            <div class="row" style="margin: 0"><div style="margin: 0 30px 0 0" class="inf">Роли: </div><div class="inf" style="margin: 0 30px 0 0" v-for="(role,index) in currentUser.roles" :key="index">{{ roles(role)}}</div></div>
            <div class="inf"> Контактные данные: {{currentUser.email}}</div>
            <textarea class="inform" v-model="about" v-on:input="changeAbout"></textarea>
          </div>
        </div>
        <div class="row" style="align-items: flex-start">
        <div class="column" style="margin-right: 50px">
          <div class="fields">
            <div class="row" style="margin: 5px 0">
              <div class="pas">Старый пароль: </div> <input type="text" v-model="oldPassword"/>
            </div>
            <div class="row" style="margin: 5px 0">
              <div class="pas">Новый пароль: </div> <input type="text" v-model="newPassword" />
            </div>
            <div class="row" style="margin: 5px 0">
              <div class="pas">Повторите новый пароль: </div> <input type="text" v-model="chekedPassword" />
            </div>
              <div class="button-g">Сменить пароль</div>
            </div>
          </div>
          <div class="column">
            <div class="pas">Ваша почта: {{currentUser.email}}</div>
            <div class="button-g">Сменить адрес электронной почты</div>
          </div>
        </div>
      </div>
      <a class="logout" href @click.prevent="logOut">
        Выйти
      </a>
        <!-- END -->

      </div>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';

export default {
  name: 'Profile',
  data(){
    return{
      message: '',
      about: ''
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

  methods: {
    roles(role){
      if (role == 'ROLE_USER') return 'Пользователь'
      else return 'Администратор'
    },
    changeAbout(){
      UserService.changeAbout(this.currentUser.id, this.about).then(
          response => {
            response.data;
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    logOut() {
      this.$store.dispatch('auth/logout');
      this.$router.push('/login');
    }
  },
  mounted() {
    UserService.getUser().then(
        response => {
          // eslint-disable-next-line no-console
          //console.log(response.data);
          this.about = response.data.description;
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