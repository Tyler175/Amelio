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
          <router-link to="/projects" class="active" @click.prevent>Проекты</router-link>
        </li>
        <li>
          <router-link to="/profile">Мой профиль</router-link>
        </li>
        <li v-if="isUserManager && false">
          <router-link to="/mod">Панель модератора</router-link>
        </li>
        <li v-if="isUserAdmin">
          <router-link to="/admin">Панель администратора</router-link>
        </li>
      </div>
    <div class="content">
      <div class="action">
        <router-link to="/profile">
          <h2 style="margin: 0">{{ currentUser.username }}</h2>
        </router-link>
      </div>
      <div>

        <div class="row">
          <h1>Проекты</h1>
          <button v-if="isManager" class="button-p" @click="createProject">Новый проект</button>
        </div>
        <div v-if="invitations.length > 0" class="row"><h2>Приглашения</h2></div>
        <div v-for="invitation in invitations" :key="invitation.id" class="row">
          <div class="task" style="cursor: auto">{{invitation.name}}</div>
          <button class="button-g" @click="acceptInv(invitation)">Принять</button>
          <button class="button-b" @click="declineInv(invitation)">Отказать</button>
        </div>
        <div v-if="invitations.length > 0" class="row"><h2>Проекты</h2></div>
        <div v-if="projects.length <= 0" class="task" style="cursor: auto">Пока вы не участвуете в проектах</div>
        <div class="row" v-for="project in projects" :key="project.id">
          <div class="task"  @click="link(project.id)">{{ project.name }}</div>
          <button class="button-b" @click="leave(project.id)" style="width: auto">Покинуть проект</button>
          <button v-if="isManager" class="button-r" @click="del(project.id)" style="width: auto">Удалить</button>
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
      content: '',
      invitations: [],
      projects: []
    };
  },
  computed: {
    //handle projects duplicates
    /*otherProjects(){
      return this.projects.filter((item, index) => this.projects.indexOf(item) === index)
    },*/
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
    isManager(){
      return this.isUserManager || this.isUserAdmin;
    },
  },
  mounted() {
    UserService.getInvitations().then(
        response => {
          this.invitations = response.data;
        },
        error => {
          this.content =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
    UserService.getProjects().then(
      response => {
        this.projects = response.data;

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
  methods:{
    link(id){
      this.$router.push('/project/' + id);
    },
    createProject(){
      UserService.postProject().then(
          response => {
            this.$router.push('/project/' + response.data.id);
          },
          error => {
            this.content =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    acceptInv(inv){
      let project = this.invitations.splice(this.invitations.findIndex(item => item.id === inv.id), 1)[0];
      project.invitations.splice(project.invitations.findIndex(item => item.id === this.currentUser.id), 1);
      UserService.changeInvitation(project);
      UserService.getUser().then(
          response => {
            project.workers.push(response.data);
            UserService.changeUsers(project);
            this.projects.push(project);
          }
      )
    },
    declineInv(inv){
      let project = this.invitations.splice(this.invitations.findIndex(item => item.id === inv.id), 1)[0];
      project.invitations.splice(project.invitations.findIndex(item => item.id === this.currentUser.id), 1);
      UserService.changeInvitation(project);
    },
    del(id){
      this.projects.splice(this.projects.findIndex(item => item.id == id),1);
      UserService.delProject(id);
    },
    leave(id){
      let project = this.projects.splice(this.projects.findIndex(item => item.id == id),1)[0];
      UserService.getUser().then(
          response => {
            project.workers.splice(project.workers.findIndex(item => item.id == response.data.id),1);

            UserService.changeUsers(project);
          }
      )
    }

  }

};
</script>
