<template>
  <div class="cover">
    <div class = "editor">
      <input class="close" type="button" value="Х" v-on:click="$emit('whide')"/>
      <div class="row"><h2 style="margin:5px">{{ task.taskName ? task.taskName : 'Задача не выбрана' }}</h2></div>
      <div class="row">
        <div class="column">
          <input v-bind:disabled="allowToEdit" class="select" type="text" maxlength="50" v-autowidth="{maxWidth: '700px', minWidth: '505px', comfortZone: 0}"
                 v-model="findTask" v-on:input="findTasks"/>

          <select v-if="tasks.length > 0" class="select" multiple v-bind:size="tasks.length > 0 ? tasks.length < 6 ? tasks.length : 5: 1" style="margin-top: -2px">
            <option v-for="task in tasks" :key="task.id" @click="setTask(task)">{{ task.taskName }}</option>
          </select>
        </div>
      </div>
      <div class = "fields" style="max-width: 600px; flex-direction: row;">
        <div style="width: 100%; margin-right: 30px">
          <h3>Дата</h3>
          <input v-bind:disabled="allowToEdit" type="date" v-model="workDate" min="2015-01-01" max="2030-01-01" style="height: 50px"/>
        </div>
        <div style="width: 100%; margin-right: 30px">
          <h3>Начало</h3>
          <input v-bind:disabled="allowToEdit" v-mask="timeMask" v-model="start"/>
        </div>
        <div style="width: 100%; margin-right: 30px">
          <h3>Конец</h3>
          <input v-bind:disabled="allowToEdit" v-mask="timeMask" v-model="end"/>
        </div>
      </div>

      <div v-if="dateValidate" class="alert-error">{{dateValidate}}</div>

      <div v-if="this.option === 'project'" class = "fields" style="max-width: 600px; flex-direction: row;">
        <div style="width: 100%; margin-right: 30px">
          <h3>Исполнитель</h3>
          <div class="task" style="width: auto; margin-right: 0px; cursor: auto">{{this.work.user.username}} - {{this.work.user.email}}</div>
        </div>
      </div>

      <div class = "row" v-if="!allowToEdit">
        <div class="button-g" @click="save">Сохранить</div>
        <div class="button-b" v-on:click="$emit('whide')">Отменить</div>
        <div class="button-r"  @click="deleteTask" v-if="work.id">Удалить</div>
      </div>
      {{message}}

    </div>
  </div>
</template>

<script>

import UserService from '../../services/user.service';

export default {
  name: 'TaskEdit',
  props: ['work','post','put','del', 'option'],
  data() {
    return {
      message: '',
      start: '',
      end: '',
      workDate: '',
      task: {},
      tasks: [],


      findTask: ''
    };
  },
  computed: {
    allowToEdit(){
      return (this.option === 'project');
    },
    currentUser() {
      return this.$store.state.auth.user;
    },
    formatDate() {
      return date => {
        let formDate = new Date(date);
        let dd = formDate.getDate();
        if (dd < 10) dd = '0' + dd;
        let mm = formDate.getMonth() + 1;
        if (mm < 10) mm = '0' + mm;
        let yy = formDate.getFullYear();
        if (yy < 10) yy = '0' + yy;
        return  yy + '-' + mm + '-' + dd;
      }
    },
    dateValidate(){
      if (this.end.length === 5 && this.start.length === 5) {
        return '';
      } else return 'Пожалуйста, заполните время работы'
    }
  },
  mounted() {
    this.start = this.work.workStart ? this.formatTime(this.work.workStart): this.formatTime(new Date());

    this.end = this.work.workEnd ? this.formatTime(this.work.workEnd): this.formatTime(new Date().setHours(new Date().getHours()+1));

    this.workDate = this.work.workStart ? this.formatDate(this.work.workStart): this.formatDate(new Date());

    this.task = this.work.task ? this.work.task : {};

  },
  methods: {
    formatTime(time){
      let formDate = new Date(time);
      let mm = formDate.getMinutes();
      if (mm < 10) mm = '0' + mm;
      let hh = formDate.getHours();
      if (hh < 10) hh = '0' + hh;
      return hh+':'+mm;
    },
    timeMask(value) {
      const hours = [
        /[0-2]/,
        value.charAt(0) === '2' ? /[0-3]/ : /[0-9]/,
      ];
      const minutes = [/[0-5]/, /[0-9]/];
      return value.length > 2
          ? [...hours, ':', ...minutes]
          : hours;
    },

    findTasks(){
      if (this.findTask){
        UserService.getTaskByUserAndName(this.currentUser.id, this.findTask).then(
            response => {
              this.tasks = response.data;
            }
        )
      } else {
        this.tasks = [];
      }
    },
    save(){
      this.$validator.validateAll().then(isValid => {
        if (isValid && !this.dateValidate && this.task.taskName) {
          this.work.task = this.task;
          this.work.workStart = new Date(this.workDate +' '+ this.start);
          this.work.workEnd = new Date(this.workDate +' '+ this.end);

          if (this.work.id){
            UserService.putWork(this.work).then(
                response => {
                  if (!response.data) this.message = 'В это время вы делали другую задачу!';
                  else{
                    this.put(response.data);
                    this.message = 'Время работы обновлено!'
                  }
                },
                error => {
                  this.message =
                      (error.response && error.response.data && error.response.data.message) ||
                      error.message ||
                      error.toString();
                }
            );
          } else{
            UserService.postWork(this.work).then(
                response => {
                  if (!response.data){
                    this.message = 'В это время вы делали другую задачу!';
                  }
                  else{
                    this.work.id = response.data.id;
                    this.post(response.data);
                    this.message = 'Время работы добавлено!'
                  }

                },
                error => {
                  this.message =
                      (error.response && error.response.data && error.response.data.message) ||
                      error.message ||
                      error.toString();
                }
            );
          }
        } else this.message = 'Пожалуйста, измените данные'
      });
    },
    setTask(task){
      if (this.task.taskName) this.tasks.push(this.task)
      this.tasks.splice(this.tasks.findIndex(i => i.id === task.id), 1);
      this.task = task;
    },
    deleteTask(){
      if (this.work.id){
        UserService.delWork(this.work.id).then(
            response => {
              this.del(this.work);
              response.data;
              this.message = 'Задача удалена!'
              this.$emit('hide');
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );}
    }
  }
};
</script>