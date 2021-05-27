<style scoped>

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
          <router-link to="/profile">Мой профиль</router-link>
        </li>
        <li v-if="isUserManager && false">
          <router-link to="/mod">Панель модератора</router-link>
        </li>
        <li v-if="isUserAdmin">
          <router-link to="/admin" class="active" @click.prevent>Панель администратора</router-link>
        </li>
      </div>
    <div class="content">
      <div class="action">
        <router-link to="/profile">
          <h2 style="margin: 0">{{ currentUser.username }}</h2>
        </router-link>
      </div>
      <h1>Панель администратора</h1>
      <div v-if="content" class="row">
        Упс, {{content}}
      </div>
      <div class="row" style="align-items: flex-start">
        <div class="column" style="max-width: 400px">
          <input class="select" type="text" maxlength="50" v-autowidth="{maxWidth: '400px', minWidth: '300px', comfortZone: 0}"
                 v-model="findUser" v-on:input="findUsers" />
          <select class="select" v-model="selected" multiple v-bind:size="users.length > 0 ? users.length < 6 ? users.length : 5: 1" style="margin-top: -2px">
            <option v-for="user in users" :key="user.id">{{user.email}}</option>
          </select>
        </div>
        <div class="column" style="flex-grow: 1">
          <div class="task" style="cursor: auto; flex-grow: 1; width: auto" v-if="user.username">{{user.username}} - {{user.email}}</div>
          <div class="task" style="cursor: auto; flex-grow: 1; width: auto" v-else>Пользователь не выбран</div>

          <div class="row" v-if="user.username">
            <button v-bind:disabled="allowToEdit" @click="changeRole({id: 1})" v-bind:class="[hasRole === 1 ? 'button-g' : 'button-b']" style="width: auto">Исполнитель</button>
            <button v-bind:disabled="allowToEdit" @click="changeRole({id: 3})" v-bind:class="[hasRole === 3 ? 'button-g' : 'button-b']" style="width: auto">Менеджер</button>
            <button v-bind:disabled="allowToEdit" @click="changeRole({id: 2})" v-bind:class="[hasRole === 2 ? 'button-g' : 'button-b']" style="width: auto">Администратор</button>
          </div>
          <div class="row" style="margin-top: 0" v-if="user.username">
            <button v-bind:disabled="allowToEdit" @click="delUser"  class="button-r" style="width: auto">Удалить пользователя</button>
          </div>
        </div>
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
      content: '',
      message: '',

      users: [],
      selected: [],
      findUser: ''

    };
  },
  computed: {
    hasRole(){
      if (this.user.roles.find(item => item.id == 2)) return 2;
      if (this.user.roles.find(item => item.id == 3)) return 3;
      if (this.user.roles.find(item => item.id == 1)) return 1;
      return 0;

    },
    allowToEdit(){
      return this.user.roles.find(item => item.id == 2)
    },
    user(){
      if (this.selected.length > 0){
        return this.users.find(item => item.email == this.selected[0]);
      } else return {};
    },
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
    }
  },
  mounted() {
    if (!this.currentUser) {
      this.$router.push('/login');
    }
  },
  methods: {
    changeRole(role){
      this.user.roles.splice(0, this.user.roles.length, role);
      UserService.changeRoles(this.user);
    },
    delUser(){
      UserService.delUser(this.user).then(
          response => {
            response.data;
            this.message = 'Пользователь удален!';
            this.findUsers();
          },
          error => {//probably not needed
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    findUsers(){
      if (this.findUser){
        UserService.getUsersByEmail(this.findUser).then(
            response => {
              this.message = '';
              this.users = response.data
              this.selected = [];
            },
            error => {//probably not needed
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      } else{
        this.selected = [];
        this.users = [];
      }
    }
  }
};
</script>
