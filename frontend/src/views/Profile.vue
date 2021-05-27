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
        <li v-if="isUserManager && false">
          <router-link to="/mod">Панель модератора</router-link>
        </li>
        <li v-if="isUserAdmin">
          <router-link to="/admin">Панель администратора</router-link>
        </li>
      </div>
    <div class="content">
      <div>

          <div class="row inf" style="margin-bottom: 0"> Имя: {{currentUser.username}}</div>
        <div class="column" style="margin-bottom: 20px ">
                      <!--div class="row" style="margin: 0"><div style="margin: 0 30px 0 0" class="inf">Роли: </div><div class="inf" style="margin: 0 30px 0 0" v-for="(role,index) in currentUser.roles" :key="index">{{ roles(role)}}</div></div-->
          <div class=" row inf"> Контактные данные: {{currentUser.email}}</div>
          <textarea class="inform" v-model="about" v-on:input="changeAbout"></textarea>
          <div class=" row inf"> Роль: {{hasRole}}</div>
        </div>
        <div class="row">
        <div class="column" style="margin-right: 50px">
          <div class="fields" v-if="false">
            <div v-if="response" class="alert-error">{{response}}</div>
            <div class="row" style="margin: 5px 0">
              <div class="pas">Старый пароль: </div> <input type="password" v-model="oldPassword" style="height: 20px"/>
            </div>
            <div class="row" style="margin: 5px 0">
              <div class="pas">Новый пароль: </div> <input type="password" v-model="newPassword" style="height: 20px"/>
            </div>
            <div class="row" style="margin: 5px 0">
              <div class="pas">Повторите новый пароль: </div> <input type="password" v-model="checkedPassword" style="height: 20px"/>
            </div>
            <div v-if="notSamePasswords" class="alert-error">Пароли не совпадают</div>
              <div class="button-g" @click="changePassword">Сменить пароль</div>
            </div>
          </div>
        </div>
      </div>
      <a class="logout" href @click.prevent="logOut">
        Выйти
      </a>
        <!-- END -->

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
      about: '',

      oldPassword: '',
      newPassword: '',
      checkedPassword: '',
      response: ''
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    },
    isUserAdmin() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_ADMIN');
      }

      return false;
    },
    isUserManager() {
      if (this.currentUser && this.currentUser.roles) {
        return this.currentUser.roles.includes('ROLE_MANAGER');
      }

      return false;
    },
    hasRole(){
      if (this.currentUser.roles.includes('ROLE_ADMIN')) return 'Администратор';
      if (this.currentUser.roles.includes('ROLE_MANAGER')) return 'Менеджер';
      if (this.currentUser.roles.includes('ROLE_USER')) return 'Пользователь';
      return 0;

    },
    notSamePasswords () {
      if (this.newPassword !== '' && this.checkedPassword !== '') {
        return (this.newPassword !== this.checkedPassword)
      } else {
        return false
      }
    },
  },

  methods: {
    changePassword(){
      if (!this.notSamePasswords && this.newPassword !== '' && this.checkedPassword !== ''){
        UserService.isPasswordCorrect(this.oldPassword).then(
            response => {
              if (response.data) {
                UserService.changePassword(this.currentUser.id,this.newPassword);
              } else this.response = 'Неверный старый пароль'

            }
        )
      } else this.response = 'Заполните все поля'
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