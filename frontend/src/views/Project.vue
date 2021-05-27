<style scoped>
h3{
  margin: 0 20px 0 0;
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
      <div class="form" style="margin-top: 50px" v-if="areAllowed"> <!--areAllowed if workers are allowed to-->
        <h2>Проект не найден</h2>
      </div>
      <div v-else>
        <v-taskEdit v-if='!isHidden' v-on:hide="isHidden = true"
                    :task="task" :post="post_task" :put="put_task" :del="del_task" :option="'project'" :employes="this.project.workers"></v-taskEdit>
        <div class="row">
          <input v-bind:disabled="!isManager" type="text" maxlength="30" v-autowidth="{maxWidth: '960px', minWidth: '20px', comfortZone: 0}"
                 class="h1" style="margin-right: 30px; margin-bottom: 0"
                 v-model="project.name" v-on:input="changeName" />

          <div v-if="isManager" @click="active = 0" v-bind:class="[active === 0 ? 'button-g' : 'button-b']">Задачи</div>
          <div v-if="isManager" @click="active = 1" v-bind:class="[active === 1 ? 'button-g' : 'button-b']">Статистика</div>
          <div v-if="isManager" @click="active = 2" v-bind:class="[active === 2 ? 'button-g' : 'button-b']">Команда</div>
        </div>
        <div v-if="content" class="row">
          <h3>Упс, {{content}}</h3>
        </div>
        <!--Задачи-->
        <div v-if="active === 0">
          <div class="column">
            <div class="row" style="margin-bottom: 0">
              <input type="checkbox" checked v-model="completed"><h3>Выполненные</h3>
              <input type="checkbox" checked v-model="overdue"><h3>Просроченные</h3>
              <input type="checkbox" checked v-model="inWork"><h3>В работе</h3>
            </div>
            <div class="row">
              <h3 style="margin-right: 10px">Показать с: </h3>
              <input class="date" type="date" v-model="interval_start" min="2015-01-01" max="2030-01-01"/>
              <h3 style="margin-right: 10px">до: </h3>
              <input class="date" type="date" v-model="interval_end" min="2015-01-01" max="2030-01-01"/>
              <div class="button-g" @click="interval_start=interval_end=''">Сбросить</div>
            </div>
          </div>


          <div class="row tasks" ref="tasks" style="align-items: flex-start">
            <div class="task-table">
              <div class="row-in-table"><div class="task-in-table"><h3>Поиск: </h3>
                <input type="text" maxlength="30" v-autowidth="{maxWidth: '200px', minWidth: '100px', comfortZone: 0}" v-model="findTask" style="font-size:16px"/></div>
              </div>
              <div class="row-in-table" v-for="task in parents" :key="task.id">
                <div style="display: flex; justify-content: space-between; align-items: center;"
                     v-bind:class="[hover === task.id ? 'hover' : '']" @mouseover='hover = task.id' @mouseout="hover = 0">
                  <div class="arrow-btn" v-bind:class="[children(task).length === 0 ? 'hidden' : '','arrow-btn']"
                       v-bind:style="{transform: children_open(task).length === 0 ? 'rotate(0.0turn)' : 'rotate(0.25turn)'}"
                       @click="open(task)"></div>
                    <div class="task-in-table" @click="openEdit(task)">
                      <div>
                        {{ task.taskName }}
                      </div>
                    </div>
                    <div v-if="isManager" class="plus-btn" @click="openEdit({parent: task})" data-content="Добавить подзадачу"></div>

                </div>
                <div class="task-in-table subtask-in-table" v-for="child_task in children_open(task) " :key="child_task.id"
                     v-bind:class="[hover === child_task.id ? 'hover' : '']" @mouseover='hover = child_task.id' @mouseout="hover = 0"
                     @click="openEdit(child_task)">
                  {{child_task.taskName}}
                </div>
              </div>
              <div v-if="isManager" class="button-s" @click="openEdit(0)"
                   style="width: auto; margin-right: 0px; margin-left: -1px; border-radius: 0 0 0 5px;">
                Добавить задачу
              </div>
            </div>
            <horizontal-scroll class="horizontal-scroll" ref="table" v-bind:style="{height: tableHeight + 'px'}">
              <div class="table" v-bind:style="{width: tableWidth + 'px'}">

                <div class="main-table-row"><div class="table-row">
                  <div v-bind:class="[date.title == 'Задач не найдено' ? 'row' : 'dates']" v-for="date in datesList" :key="date.id">
                    {{ date.title }}
                    <div>
                      <div style="width: 40px" v-for="day in date.days" :key="day">{{day}}</div>
                    </div>
                  </div>
                </div></div>
                <div class="main-table-row" v-for="task in parents" :key="task.id">
                  <div class="table-row" v-bind:class="[hover === task.id ? 'hover' : '']" @mouseover='hover = task.id' @mouseout="hover = 0">
                    <div class="table-task" v-bind:style="taskProps(task)" @click="openEdit(task)">{{ task.taskName }}</div>
                  </div>
                  <div class="table-row table-subrow" v-for="child_task in children_open(task) " :key="child_task.id"
                       v-bind:class="[hover === child_task.id ? 'hover' : '']" @mouseover='hover = child_task.id' @mouseout="hover = 0">
                    <div class="table-task" v-bind:style="taskProps(child_task)" @click="openEdit(child_task)">{{ child_task.taskName }}</div>
                  </div>
                </div>
              </div>
            </horizontal-scroll>
          </div>
        </div>
        <!--Статистика-->
        <div v-else-if="active === 1">
          <v-stat :project="project"></v-stat>
        </div>
        <!--Команда-->
        <div v-else>
          <div v-if="isManager">
            <div class="row"><h2 style="margin-bottom: 5px">Приглашения</h2></div>
            <v-invRow v-for="user in project.invitations" :key="user.id" :user="user" :action="delInv"></v-invRow>

            <div class="row">
              <div class="column">
                <input class="select" type="text" maxlength="50" v-autowidth="{maxWidth: '400px', minWidth: '300px', comfortZone: 0}"
                       v-model="findUser" v-on:input="findUsers" />
                <select class="select" v-model="selected" multiple v-bind:size="users.length > 0 ? users.length < 6 ? users.length : 5: 1" style="margin-top: -2px">
                  <option v-for="user in users" :key="user.id">{{user.username}} - {{user.email}}</option>
                </select>
              </div>
              <div class="button-b" style="width: auto" @click="addInv">Пригласить {{selected[0]}}</div>
            </div>
          </div>
          <h2>Сотрудники</h2>
          <input class="select" type="text" maxlength="50" v-autowidth="{maxWidth: '400px', minWidth: '300px', comfortZone: 0}" v-model="sortM"/>
          <div class="row" v-for="worker in workers" :key="worker.id">
            <div class="task" style="cursor: auto">{{worker.username}} - {{worker.email}}</div>
            <div class="button-r" @click="delWorker(worker)">Удалить</div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
import UserService from '../services/user.service';
import InvRow from "@/views/components/InvRow";
import TaskEdit from "@/views/components/TaskEdit";
import Stat from "@/views/components/Stat";
import HorizontalScroll from 'vue-horizontal-scroll'
import 'vue-horizontal-scroll/dist/vue-horizontal-scroll.css'


export default {
  name: 'Projects',
  data() {
    return {
      content: '',
      active: 0,
      hover: 0,

      completed: true,
      overdue: true,
      inWork: true,
      findTask: '',
      interval_start: '',
      interval_end: '',

      tasks:[],
      task:{},
      openParents: [],
      isHidden: true,


      selected: [],
      findUser: '',
      users: [],

      project: {
        owner: {},
        managers: [],
        workers: [],
        invitations: [],
      },

      sortW: '',
      sortM: ''
    };
  },
  components:{
    'v-invRow' : InvRow,
    'horizontal-scroll' : HorizontalScroll,
    'v-taskEdit' : TaskEdit,
    'v-stat' : Stat
  },
  computed: {
    parents(){
      if (!this.project.tasks) return [];
      return this.project.tasks.filter(item => (!item.parent))
          .filter(item =>
              (!this.interval_start || new Date(item.task_start).getTime() >= new Date(this.interval_start).getTime()) &&
              (!this.interval_end || new Date(item.task_end).getTime() <= new Date(this.interval_end).getTime())
          )
          .sort(function(f, s) {return f.task_start > s.task_start ? 1 : -1})
          .filter(item=>item.taskName.toLowerCase().includes(this.findTask.toLowerCase()))
          .filter(item => ((this.completed || !item.taskComplete) && (this.overdue || item.taskComplete || new Date(item.task_end).getTime() > new Date().getTime()) && (this.inWork || item.taskComplete || new Date(item.task_end).getTime() < new Date().getTime())));
    },
    children_task(){
      return this.project.tasks.filter(item => (item.parent))
          .filter(item => ((this.completed || !item.taskComplete) && (this.overdue || item.taskComplete || new Date(item.task_end).getTime() > new Date().getTime()) && (this.inWork || item.taskComplete || new Date(item.task_end).getTime() < new Date().getTime())))
          .sort(function(f, s) {return f.task_start > s.task_start ? 1 : -1});
    },
    /*
    items () {
      return this.project.workers.map(function (item) {
        return {user: item, list: 1}
      }).concat(this.project.managers.map(function (item) {
        return {user: item, list: 2}
      }))
    },
    listOne () {
      return this.items.filter(item => (item.list === 1 && (item.user.username.toLowerCase().includes(this.sortW.toLowerCase()) || item.user.email.toLowerCase().includes(this.sortW.toLowerCase()) )))
    },
    listTwo () {
      return this.items.filter(item => (item.list === 2 && (item.user.username.toLowerCase().includes(this.sortM.toLowerCase()) || item.user.email.toLowerCase().includes(this.sortM.toLowerCase()) )))
    },
    */
    workers(){
      return this.project.workers.filter(item => (item.username.toLowerCase().includes(this.sortM.toLowerCase()) || item.email.toLowerCase().includes(this.sortM.toLowerCase()) ));
    },
    minDate(){
      return this.parents.length > 0 ? (new Date(this.parents.reduce(function (p, v) {
        return ( p < v.task_start ? p : v.task_start );
      }, this.parents[0].task_start))) : 0;
    },
    maxDate(){
      let res = this.parents.length > 0 ? (new Date(this.parents.reduce(function (p, v) {
        return ( p > v.task_end ? p : v.task_end );
      }, this.parents[0].task_end))) : 0;

      let buf = 0;
      if (this.$refs.tasks && res){
        buf = this.$refs.tasks.clientWidth * 0.7;
        buf = (buf - buf % 40) / 40;
        buf -= (res.getTime() - this.minDate.getTime())/86400000;
        if (buf>0) res.setTime(res.getTime()+buf*86400000);
      }

      return res;
    },
    tableHeight(){
      return  50*(1+this.parents.length)+ this.openParents.reduce(function (p, v) {
        return ( v.isOpen && this.parents.find(item => item.id === v.id) ? p+=(this.children_task.filter(item => (item.parent.id === v.id)).length) : p );
      }.bind(this), 0)*30+17;
    },
    tableWidth(){
      return (((this.maxDate- this.minDate)/86400000+1)*40);
    },
    taskProps(){
      return task => ({
        width: ((new Date(task.task_end) - new Date(task.task_start))/86400000+1)*40-30 + 'px',
        left: ((new Date(task.task_start) - this.minDate)/86400000)*40 + 'px',
        background: task.taskComplete ? '#66cdaa' : (new Date(task.task_end) < new Date() ? '#FF7373' : '#6c7bfe')
      });
    },
    datesList(){
      if (this.minDate === 0) return [{id: 1, title: 'Задач не найдено', days: []}]
      let res=[]
      let id = 1;
      let i = new Date(this.minDate);
      for (i; i <= this.maxDate; i.setDate(i.getDate() + 1)) {
        let obj = {id: 1, title: '', days: []}
        obj.title = i.toLocaleString('ru', {month: 'short'});
        if (new Date().getFullYear() != i.getFullYear()) obj.title+=i.getFullYear()
        while (i <= this.maxDate){
          obj.days.push(i.getDate());
          if (i.getDay() === 0) break;
          i.setDate(i.getDate() + 1);
        }
        obj.id = id;
        id+=1;
        res.push(obj);
        //res.push(Object.assign({}, obj));
      }

      return res;
    },

    currentUser() {
      return this.$store.state.auth.user;
    },
    isWorker(){
      return this.project.workers.findIndex(item => item.id === this.currentUser.id)>=0
    },
    isManager(){
      return this.isUserManager || this.isUserAdmin;
    },
    areAllowed(){
      return !(this.isManager || this.isWorker);
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
    UserService.getProject(this.$route.params.id).then(
      response => {
        this.project = response.data;
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
    /*
    startDrag(evt, item) {
      if (!this.isManager) return;
      evt.dataTransfer.dropEffect = 'move'
      evt.dataTransfer.effectAllowed = 'move'
      evt.dataTransfer.setData('itemID', item.user.id)

    },
    onDrop (evt, list) {
      if (!this.isManager) return;
      const itemID = evt.dataTransfer.getData('itemID')
      const item = this.items.find(item => item.user.id == itemID)
      if (list === 3){
        if (this.project.workers.find(i => i.id == itemID)){
          this.project.workers.splice(this.project.workers.findIndex(i => i.id == itemID),1)
        } else {
          this.project.managers.splice(this.project.managers.findIndex(i => i.id == itemID),1)
        }
        UserService.changeUsers(this.project).then(
            response => {
              response.data;//not used yet
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      } else if(item.list != list) {
        if (this.project.workers.find(i => i.id == itemID)){
          this.project.managers.push(this.project.workers.splice(this.project.workers.findIndex(i => i.id == itemID),1)[0])
        } else{
          this.project.workers.push(this.project.managers.splice(this.project.managers.findIndex(i => i.id == itemID),1)[0])
        }
        UserService.changeUsers(this.project).then(
            response => {
              response.data;//not used yet
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      }
    },
    */
    delWorker(worker){
      if (!this.isManager) return;
      this.project.workers.splice(this.project.workers.findIndex(i => i.id == worker.id),1)
      UserService.changeUsers(this.project).then(
          response => {
            response.data;//not used yet
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    open(task){
      let parent = this.openParents.find(item => item.id === task.id);
      if (parent) {
        parent.isOpen = !parent.isOpen;
        this.openParents.splice(this.openParents.findIndex(item => item.id === parent.id), 1, parent);
      } else {
        this.openParents.push({id: task.id, isOpen: true})
      }
    },
    children(parent){
      return this.children_task.filter(item => (item.parent.id === parent.id))
    },
    children_open(parent){
      if (this.openParents.find(item => item.id === parent.id) &&
          this.openParents.find(item => item.id === parent.id).isOpen){
        return this.children(parent);
      }
      else return [];
    },

    changeName(){
      if (!this.isManager) return;
      UserService.changeProjectName(this.project).then(
          response => {
            response.data;//not used yet
          },
          error => {
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
              //if (response.data.findIndex(item => item.email === this.currentUser.email) >= 0){response.data.splice(response.data.findIndex(item => item.email === this.currentUser.email),1)}
              this.users = response.data.filter( function( el ) {
                return (this.project.workers.findIndex(item => item.id === el.id) < 0  &&  this.project.invitations.findIndex(item => item.id === el.id) < 0);
              }, this);
              this.selected = [];
            },
            error => {//probably not needed
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        ).catch(e => {
          // eslint-disable-next-line no-console
          console.log(e);
        });
      } else{
        this.users = [];
      }
    },
    addInv(){
      if (!this.isManager) return;
      if (this.selected[0]){
        // eslint-disable-next-line no-console
        console.log(this.selected[0]);
        this.project.invitations.push(this.users[this.users.findIndex(item => item.email === this.selected[0].split(' - ')[1])]);
        this.findUsers();
        UserService.changeInvitation(this.project).then(
            response => {
              response.data;//not used yet
              this.message = 'Приглашение добавлено!'
            },
            error => {
              this.message =
                  (error.response && error.response.data && error.response.data.message) ||
                  error.message ||
                  error.toString();
            }
        );
      }
    },
    delInv(user){
      if (!this.isManager) return;
      this.project.invitations.splice(this.project.invitations.findIndex(item => item.id === user.id),1);
      UserService.changeInvitation(this.project).then(
          response => {
            response.data;//not used yet
            this.message = 'Приглашение удалено!'
          },
          error => {
            this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
          }
      );
    },
    //                        task update
    //                        task update
    //                        task update
    openEdit(task){
      if (task === 0) {this.task = {}}
      else {
        this.task = {}
        Object.assign(this.task, task);
      }
      this.isHidden = false;

    },
    post_task(task){
      this.project.tasks.push(task);
      Object.assign(this.task, task);
      this.task_update('Задача добавлена');
    },
    put_task(task){
      this.project.tasks.splice(this.project.tasks.findIndex(item => item.id == task.id), 1, task); //because otherwise computed property doesnt recomputed
      this.task_update('Задача добавлена');
    },
    del_task(task){
      this.project.tasks.splice(this.project.tasks.findIndex(item => item.id === task.id),1);
    },
    task_update(message){
      UserService.changeTasks(this.project).then(
          response => {
            response.data;//not used yet
            this.message = message;
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
};
</script>

