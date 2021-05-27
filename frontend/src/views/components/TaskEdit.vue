<template>
  <div class="cover">
    <div class = "editor">
      <input class="close" type="button" value="Х" v-on:click="$emit('hide')"/>
      <div class = "fields" style="">
        <div class="row"><h1>Название задачи</h1><input v-bind:disabled="allowToEdit" class="check-mark" type="checkbox" v-model="task.taskComplete"><h2>Готово</h2></div>
        <input v-bind:disabled="allowToEdit" type="text" name="название" v-model="task.taskName" v-validate="'required|max:100'"/>
        <div
            v-if="errors.has('название')"
            class="alert-error"
        >{{errors.first('название').replace('название ','')}}</div>
      </div>
      <div class = "fields" style="">
        <h3>Описание задачи</h3>
        <textarea v-bind:disabled="allowToEdit" v-model="task.task_description"></textarea>
      </div>
      <div class = "fields" style="max-width: 600px; flex-direction: row;">
        <div style="width: 100%; margin-right: 30px">
          <h3>Начало</h3>
          <input v-bind:disabled="allowToEdit" type="date" v-model="task_start" min="2015-01-01" max="2030-01-01" style="height: 40px"/>
        </div>
        <div style="width: 100%; margin-right: 30px">
          <h3>Конец</h3>
          <input v-bind:disabled="allowToEdit" type="date" v-model="task_end" min="2015-01-01" max="2030-01-01" style="height: 40px"/>
        </div>
      </div>
      <div v-if="dateCompare" class="alert-error">{{dateCompare}}</div>
      <div v-if="option === 'project'" class="row" style="align-items: flex-start">
        <div>
          <div class="row"><h2 style="margin:5px">Исполнители</h2></div>
          <div class="row">
            <div class="column">
              <div v-if="workers.length > 0" class="select">
                <div v-for="user in workers" :key="user.id" class="employes" >
                  <div>{{ user.username }} - {{ user.email }}</div>
                  <div v-if="!allowToEdit" class="cross2" @click="delWorker(user)">x</div>
                </div>
              </div>
              <input v-bind:disabled="allowToEdit" class="select" type="text" maxlength="50" v-autowidth="{maxWidth: '700px', minWidth: '505px', comfortZone: 0}"
                     v-model="findUser" />

              <select v-if="findUsers.length > 0" class="select" multiple v-bind:size="findUsers.length > 0 ? findUsers.length < 6 ? findUsers.length : 5: 1" style="margin-top: -2px">
                <option v-for="user in findUsers" :key="user.id" @click="addWorker(user)">{{ user.username }} - {{ user.email }}</option>
              </select>
            </div>
          </div>
        </div>
        <div>
          <div class="row"><h2 style="margin:5px">План</h2> (часов)</div>
          <div class="row">
            <div class="column">
              <div v-for="plan in plans" :key="plan.user.id" >
                <input v-bind:disabled="allowToEdit" v-on:input="changePlan(plan)" type="number" class="select employes" v-model="plan.plan" style="border:none; width: 60%"/>
              </div>
            </div>
          </div>
        </div>

      </div>
      <div class = "row" v-if="!allowToEdit">
        <div class="button-g" @click="save">Сохранить</div>
        <div class="button-b" v-on:click="$emit('hide')">Отменить</div>
        <div class="button-r"  @click="deleteTask" v-if="task.id">Удалить</div>
      </div>
      {{message}}

    </div>
  </div>
</template>

<script>

import UserService from '../../services/user.service';

export default {
  name: 'TaskEdit',
  props: ['task','post','put','del', 'option', 'employes'],
  data() {
    return {
      message: '',
      start: '',
      end: '',
      workers: [],
      plans: [],

      isManager: false,

      findUser: ''
    };
  },
  computed: {
    allowToEdit(){
      return !((this.isManager) && this.option!='view');
    },
    currentUser() {
      return this.$store.state.auth.user;
    },
    findUsers(){
      if (this.findUser){
        return this.employes.filter(i => (
            (i.email.toLowerCase().startsWith(this.findUser.toLowerCase()) || i.username.toLowerCase().startsWith(this.findUser.toLowerCase())) &&
            this.workers.findIndex(item => item.id === i.id)<0
        ));
      } else{
        return [];
      }
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
    task_start: {
      get: function () {
        return this.formatDate(this.start);
      },
      set: function (value) {
        this.start = value;
      }
    },
    task_end: {
      get() {
        return this.formatDate(this.end);
      },
      set(value) {
        this.end = value;
      }
    },
    dateCompare(){
      if (this.end && this.start) {
        if (this.end < this.start) return 'Задача не может начинаться раньше конца';
        if(this.task.parent){
          if (this.formatDate(this.task.parent.task_start) > this.formatDate(this.start)) return 'Подзадача не может начинаться раньше основной';
          if (this.formatDate(this.task.parent.task_end) < this.formatDate(this.end)) return 'Подзадача не может заканчиваться позже основной';
        } return '';
      } else return ''
    }
  },
  mounted() {
    if (this.task.id) UserService.getEditPermissions(this.task.id).then(
        response => {
          this.isManager = response.data;
        },
        error => {
          this.message =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        });
    else this.isManager = true;
    this.start = this.task.task_start ? this.task.task_start : this.formatDate(new Date());
    this.end = this.task.task_end ? this.task.task_end : this.formatDate(new Date());
    this.workers = this.task.workers ? this.task.workers : [];
    this.plans = this.task.plans ? this.task.plans : [];

  },
  methods: {
    changePlan(plan){
      UserService.putPlan(plan, this.task);
    },
    save(){
      this.$validator.validateAll().then(isValid => {
        if (isValid && !this.dateCompare) {
          this.task.task_start = this.start;
          this.task.task_end = this.end;
          this.task.workers = this.workers;
          this.task.plans = this.plans;
          /*not necessary?*/
          if (!this.task.task_start && !this.task.task_end) {
            this.task.task_end = this.task.task_start = this.formatDate(new Date());
          } else if (!this.task.task_start) {
            this.task.task_start = this.task.task_end
          } else if (!this.task.task_end) {
            this.task.task_end = this.task.task_start
          }

          if (this.task.id){
            if (this.option != 'project' && this.task.workers.length == 0)  this.task.workers = [this.currentUser];
            UserService.putTask(this.task).then(
                response => {
                  this.put(response.data);
                  this.message = 'Задача сохранена!'
                },
                error => {
                  this.message =
                      (error.response && error.response.data && error.response.data.message) ||
                      error.message ||
                      error.toString();
                }
            );
          } else{
            if (this.option != 'project') this.task.workers = [this.currentUser];
            UserService.postTask(this.task).then(
                response => {
                  this.task.id = response.data.id;
                  this.post(response.data);
                  this.message = 'Задача сохранена!'
                },
                error => {
                  this.message =
                      (error.response && error.response.data && error.response.data.message) ||
                      error.message ||
                      error.toString();
                }
            );
          }
        } else this.message = 'Не удалось сохранить задачу'
      });
    },
    delWorker(user){
      this.workers.splice(this.workers.findIndex(i => i.id === user.id),1);
      UserService.delPlan(this.plans.find(i => i.id === user.id));
      this.plans.splice(this.plans.findIndex(i => i.id === user.id),1);
    },
    addWorker(user){
      this.$set(this.workers, this.workers.length, user);
      this.plans.push({user: user, plan: ''});
      UserService.postPlan({user: user, task: this.task, plan: ''});
    },
    deleteTask(){
      if (this.task.id){
        UserService.delTask(this.task.id).then(
            response => {
              this.del(this.task);
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