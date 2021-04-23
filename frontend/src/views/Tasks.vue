<template>
  <div class="container">
    <v-taskEdit v-if='!isHidden' v-on:hide="isHidden = true" :task="task"></v-taskEdit>
    <div class="content" style="display: flex; justify-content:space-around">
      <div v-if="currentUser" class="user-menu" style="width: 20%">
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
      <div style="width: 50%">
        <select v-model="selected">
          <option v-for="task in tasks" :key="task.id" @click="openEdit(selected.id)" v-bind:value="task.parent.id">
            <i>({{ task.parent.id }})</i> {{ task.parent.task_name }}
            <span>
              <input type="button" value="Готово"/>
              <input type="button" value="X"/>
            </span>
              <select v-model="selected">
              <option  v-for="child in task.children" :key="child.id" @click="openEdit(selected.id)" v-bind:value="child.id">
                <i>({{ child.id }})</i> {{ child.task_name }}
                <span>
                  <input type="button" value="Готово"/>
                  <input type="button" value="X"/>
                </span>
            </option>
              </select>
          </option>
        </select>

        {{content}}
      </div>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';
import TaskEdit from "@/views/components/TaskEdit.vue";

export default {
  name: 'Tasks',
  data() {
    return {
      isHidden: true,
      tasks: [],
      content: '',
      task_id: 0,
      selected: null,
      task: {}
    };
  },
  components:{
    'v-taskEdit' : TaskEdit
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
    UserService.getTasks().then(
      response => {
        this.tasks = response.data;
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
  methods: {
    openEdit(id){
      UserService.getTaskById(id).then(
          response => {
            this.task = response.data;
          },
          error => {
            this.content =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
      this.isHidden = false;

    }
  }
};
</script>
