<template>
  <div class="row">
    <div class="task" @click="link">{{ project.name }}</div>
    <button class="button-s" @click="deleteProject">{{btn}}</button>
  </div>
</template>

<script>
import UserService from "@/services/user.service";

export default {
  name: "ProjectRow",
  props: ['project','btn', 'action'],
  data() {
    return {
      message: ''
    };
  },
  methods: {
    link(){
      this.$router.push('/project/' + this.project.id);
    },
    deleteProject(){
      UserService.delProject(this.project.id).then(
          response => {
            this.action(this.project.id);
            response.data;
            this.message = 'Проект удален!'
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    }
  }
}
</script>