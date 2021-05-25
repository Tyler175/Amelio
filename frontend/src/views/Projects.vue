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
        <div v-if="invitations.length > 0" class="row"><h2>Приглашения</h2></div>
        <div v-for="invitation in invitations" :key="invitation.id" class="row">
          <div class="task" style="cursor: auto">{{invitation.name}}</div>
          <button class="button-g" @click="acceptInv(invitation)">Принять</button>
          <button class="button-b" @click="declineInv(invitation)">Отказать</button>
        </div>
        <div class="row"><h2>Мои проекты</h2></div>
        <div v-if="user_projects.length <= 0" class="task" style="cursor: auto">У вас пока нет проектов</div>
        <v-projectRow v-for="project in user_projects" :key="project.id" :project="project" :btn="'Удалить'" :action="del" ></v-projectRow>
        <div class="row"><h2>Проекты</h2></div>
        <div v-if="otherProjects.length <= 0" class="task" style="cursor: auto">Пока вы не участвуете в проектах</div>
        <v-projectRow v-for="project in otherProjects" :key="project.id" :project="project" :btn="'Покинуть проект'" :action="leave"></v-projectRow>
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
      invitations: [],
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
    UserService.getUserProjects().then(
        response => {
          this.user_projects = response.data;
          // eslint-disable-next-line no-console
          console.log(this.user_projects);
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
    acceptInv(inv){
      let project = this.invitations.splice(this.invitations.findIndex(item => item.id === inv.id), 1)[0];
      // eslint-disable-next-line no-console
      console.log(this.invitations, project);
      project.invitations.splice(project.invitations.findIndex(item => item.id === this.currentUser.id), 1);
      UserService.changeInvitation(project);
      UserService.getUser().then(
          response => {
            project.workers.push(response.data);
            UserService.changeUsers(project);
          }
      )
    },
    declineInv(inv){
      let project = this.invitations.splice(this.invitations.findIndex(item => item.id === inv.id), 1)[0];
      project.invitations.splice(project.invitations.findIndex(item => item.id === this.currentUser.id), 1);
      UserService.changeInvitation(project);
    },
    del(id){
      this.user_projects.splice(this.user_projects.findIndex(item => item.id == id),1);

    },
    leave(id){
      this.projects.splice(this.projects.findIndex(item => item.id == id),1);

    }

  }

};
</script>
