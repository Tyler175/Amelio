<style scoped>
h3{
  margin: 0 20px 0 0;
}
</style>
<template>
  <div class="container">
    <v-taskEdit v-if='!isHidden' v-on:hide="isHidden = true"
                :task="task" :post="post" :put="put" :del="del"></v-taskEdit>

    <div v-if="currentUser" class="user-menu">
        <li>
          <router-link to="/today">To Do</router-link>
        </li>
        <li>
          <router-link to="/tasks" class="active" @click.prevent>Задачи</router-link>
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
          <router-link to="/admin">Панель администратора</router-link>
        </li>
      </div>
    <div class="content">
      <div class="action">
        <router-link to="/profile">
          <h2 style="margin: 0">{{ currentUser.username }}</h2>
        </router-link>
      </div>
      <h1>Задачи</h1>
      <div v-if="content" class="row">
        Упс, {{content}}
      </div>
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

      <!--   WATERFALL-->
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
              <div v-if="editPerms.includes(task.id)" class="plus-btn" @click="openEdit({parent: task})" data-content="Добавить подзадачу"></div>

            </div>
            <div class="task-in-table subtask-in-table" v-for="child_task in children_open(task) " :key="child_task.id"
                 v-bind:class="[hover === child_task.id ? 'hover' : '']" @mouseover='hover = child_task.id' @mouseout="hover = 0"
                 @click="openEdit(child_task)">
              {{child_task.taskName}}
            </div>
          </div>
          <div class="button-s" @click="openEdit(0)"
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
  </div>
</template>

<script>
import UserService from '../services/user.service';
import TaskEdit from "@/views/components/TaskEdit.vue";
import HorizontalScroll from 'vue-horizontal-scroll'
import 'vue-horizontal-scroll/dist/vue-horizontal-scroll.css'

export default {
  name: 'Tasks',
  data() {
    return {
      isHidden: true,
      content: '',
      hover: 0,

      completed: true,
      overdue: true,
      inWork: true,
      findTask: '',
      interval_start: '',
      interval_end: '',

      editPerms:[],

      openParents: [],
      tasks: [],
      task: {}
    };
  },
  components:{
    'v-taskEdit' : TaskEdit,
    'horizontal-scroll' : HorizontalScroll
  },
  computed: {
    parents(){
      return this.tasks.filter(item => (!item.parent))
          .filter(item =>
              (!this.interval_start || new Date(item.task_start).getTime() >= new Date(this.interval_start).getTime()) &&
              (!this.interval_end || new Date(item.task_end).getTime() <= new Date(this.interval_end).getTime())
          )
          .sort(function(f, s) {return f.task_start > s.task_start ? 1 : -1})
          .filter(item=>item.taskName.toLowerCase().includes(this.findTask.toLowerCase()))
          .filter(item => ((this.completed || !item.taskComplete) && (this.overdue || item.taskComplete || new Date(item.task_end).getTime() > new Date().getTime()) && (this.inWork || item.taskComplete || new Date(item.task_end).getTime() < new Date().getTime())));
      //can combine filters
    },
    children_task(){
      return this.tasks.filter(item => (item.parent))
          .filter(item => ((this.completed || !item.taskComplete) && (this.overdue || item.taskComplete || new Date(item.task_end).getTime() > new Date().getTime()) && (this.inWork || item.taskComplete || new Date(item.task_end).getTime() < new Date().getTime())))
          .sort(function(f, s) {return f.task_start > s.task_start ? 1 : -1});
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
    UserService.getTasks().then(
      response => {
        this.tasks = response.data;
        this.tasks.forEach(task =>
            UserService.getEditPermissions(task.id).then(
                response => {
                  if (response.data[0] || response.data[1]) this.editPerms.push(task.id);
                }
            )
        )
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
    test(){
      // eslint-disable-next-line no-console
      console.log(this.editPerms);
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
    openEdit(task){
      if (task === 0) {this.task = {}}
      else {
        this.task = {}
        Object.assign(this.task, task);
      }
      this.isHidden = false;

    },
    post(task){
      this.tasks.push(task);
      Object.assign(this.task, task);
      this.editPerms.push(task.id);
    },
    put(task){
      this.tasks.splice(this.tasks.findIndex(item => item.id == task.id), 1, task);

    },
    del(task){
      this.tasks.splice(this.tasks.findIndex(item => item.id === task.id),1);
      this.editPerms.splice(this.editPerms.findIndex(item => item.id === task.id),1);
    }
  }
};
</script>
