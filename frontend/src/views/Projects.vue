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
          <button class="button-p" @click="createProject">Новый проект</button>
        </div>
        <div class="row"><h2>Мои проекты</h2></div>
        <v-projectRow v-for="project in user_projects" :key="project.id" :project="project" :btn="'Удалить'" :action="del"></v-projectRow>
        <div class="row"><h2>Проекты</h2></div>
        <v-projectRow v-for="project in otherProjects" :key="project.id" :project="project" :btn="'Покинуть проект'" :action="del"></v-projectRow>
        <!-- END -->
        <h3>{{content}}</h3>
      </div>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';
import ProjectRow from "@/views/components/ProjectRow";

export default {
  name: 'Projects',
  data() {
    return {
      content: '',
      user_projects: [],
      projects: []
    };
  },
  components:{
    'v-projectRow' : ProjectRow
  },
  computed: {
    otherProjects(){
      return this.projects.filter((item, index) => this.projects.indexOf(item) === index)
    },
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
        this.projects = response.data;
      },
      error => {
        this.content =
          (error.response && error.response.data && error.response.data.message) ||
          error.message ||
          error.toString();
      }
    );
    UserService.getUserProjects().then(
        response => {
          this.user_projects = response.data;
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
    del(project){
      let i = this.projects.findIndex(item => item.id == project.id);
      if (i < 0) {
        i = this.user_projects.findIndex(item => item.id == project.id);
        this.projects.splice(i,1);
      } else{
        this.projects.splice(i,1);
      }

    }
  }
};
</script>
