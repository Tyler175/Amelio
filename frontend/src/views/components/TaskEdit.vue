<template>
  <div class="cover">
    <div class = "editor">
      <input class="close" type="button" value="Х" v-on:click="$emit('hide')"/>
      <div class = "fields" style="">
        <h1>Название задачи</h1>
        <input type="text" v-model="task.task_name" />
      </div>
      <div class = "fields" style="">
        <h3>Описание задачи</h3>
        <textarea v-model="task.task_description"></textarea>
      </div>
      <div class = "fields" style="max-width: 600px; flex-direction: row;">
        <div style="width: 100%; margin-right: 30px">
        <h3>Начало</h3>
        <input type="date" v-model="task.task_start" min="2015-01-01" max="2030-01-01"/>
        </div>
        <div style="width: 100%; margin-right: 30px">
        <h3>Конец</h3>
        <input type="date" v-model="task.task_end" min="2015-01-01" max="2030-01-01"/>
        </div>
      </div>
      <div class = "fields" style="max-width: 600px; flex-direction: row;">
        <button type="button" @click="save"><span>Сохранить</span></button>
        <button type="button" v-on:click="$emit('hide')"><span>Отменить</span></button>
      </div>
      {{message}}

    </div>
  </div>
</template>

<script>

import UserService from '../../services/user.service';

export default {
  name: 'TaskEdit',
  props: ['task'],
  data() {
    return {
      message: ''
    };
  },
  computed: {
    currentUser() {
      return this.$store.state.auth.user;
    }
  },
  methods: {
    save(){
      if (!this.task.task_start) {this.task.task_start = this.task.task_end}
      if (!this.task.task_end) {this.task.task_end = this.task.task_start}
      if (this.task.id){
        UserService.putTask(this.task).then(
            response => {
              this.message = response.data;
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      } else{
        this.task.username = this.currentUser().username;
        UserService.postTask(this.task).then(
            response => {
              this.message = response.data;
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
};
</script>