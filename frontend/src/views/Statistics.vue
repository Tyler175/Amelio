<style scoped>
h3{
  margin: 0 20px 0 0;
}
</style>
<template>
  <div class="container">
    <v-taskEdit v-if='!isHidden' v-on:hide="isHidden = true"
                :task="task" :post="post" :put="put" :del="del" :option="'view'"></v-taskEdit>
    <v-workEdit v-if='!isWorkHidden' v-on:whide="isWorkHidden = true"
                :work="work" :post="workPost" :put="workPut" :del="workDel"></v-workEdit>
    <div v-if="currentUser" class="user-menu">
        <li>
          <router-link to="/today">To Do</router-link>
        </li>
        <li>
          <router-link to="/tasks">Задачи</router-link>
        </li>
        <li>
          <router-link to="/statistics" class="active" @click.prevent>Статистика</router-link>
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
      <h1>Статистика</h1>
      <div v-if="content" class="row">
        Упс, {{content}}
      </div>
      <div class="column">
        <div class="row" style="margin-bottom: 0" v-if="false"> <!--not needed yet-->
          <input type="checkbox" checked v-model="completed"><h3>Выполненные</h3>
          <input type="checkbox" checked v-model="overdue"><h3>Просроченные</h3>
          <input type="checkbox" checked v-model="inWork"><h3>В работе</h3>
        </div>
        <div class="row">
          <h3 style="margin-right: 10px">{{ interval_start ? 'Показать с:' : 'Показать за' }} </h3>
          <input class="date" type="date" v-model="interval_start" min="2015-01-01" max="2030-01-01"/>
          <h3 v-if="interval_start" style="margin-right: 10px">до: </h3>
          <input v-if="interval_start" class="date" type="date" v-model="interval_end" min="2015-01-01" max="2030-01-01"/>
          <div class="button-g" @click="interval_start=interval_end=''">Сбросить</div>
        </div>
      </div>
      <div v-if="interval_end">
        <div class="row" v-for="task in allTask" :key="task.id">
          <div class="task" style="cursor: auto" >{{task.taskName}} - {{formateQuantity(quantity(task))}}</div>
        </div>
      </div>
      <!--   WATERFALL-->
      <div v-else class="row tasks" ref="tasks" style="align-items: flex-start">
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
                  {{ task.taskName }} - {{formateQuantity(quantity(task))}}
                </div>
              </div>
              <div class="plus-btn" @click="openWorkEdit({task: task})" data-content="Добавить время работы"></div>

            </div>
            <div v-for="child_task in children_open(task) " :key="child_task.id" style="display: flex; align-items: center"
                 v-bind:class="[hover === child_task.id ? 'hover' : '']" @mouseover='hover = child_task.id' @mouseout="hover = 0">
              <div class="task-in-table subtask-in-table"
                   @click="openEdit(child_task)">
                {{child_task.taskName}} - {{formateQuantity(quantity(child_task))}}
              </div>
              <div class="plus-btn" @click="openWorkEdit({task: child_task})" data-content="Добавить время работы"></div>
            </div>

          </div>
          <div class="button-s" @click="openWorkEdit(0)"
               style="width: auto; margin-right: 0px; margin-left: -1px; border-radius: 0 0 0 5px;">
            Добавить время работы
          </div>
        </div>
        <horizontal-scroll class="horizontal-scroll" ref="table" v-bind:style="{height: tableHeight + 'px'}">
          <div class="table" v-bind:style="{width: 100*24 + 'px'}">
            <!--dates row-->
            <div class="main-table-row"><div class="table-row">
              <div class="dates" v-for="date in Array.from({length: 24}, (v, k) => k)" :key="date" style="width: 100px; flex-direction: row;">
                <div style="padding-left: 5px"> {{date &lt; 10 ? '0' + date : date}}:00 </div>
              </div>
            </div></div>
            <!--content rows-->
            <div class="main-table-row" v-for="task in parents" :key="task.id">
              <div class="table-row" v-bind:class="[hover === task.id ? 'hover' : '']" @mouseover='hover = task.id' @mouseout="hover = 0">
                <div class="table-task" style="height: 20px; padding: 5px 0"
                     v-for="work in taskWorks(task)" :key="work.id"
                     v-bind:style="workProps(work)" @click="openWorkEdit(work)"></div>
              </div>
              <div class="table-row table-subrow" style="height: 20px; padding: 5px 0"
                   v-for="child_task in children_open(task) " :key="child_task.id"
                   v-bind:class="[hover === child_task.id ? 'hover' : '']" @mouseover='hover = child_task.id' @mouseout="hover = 0">
                <div class="table-task" style="height: 20px"
                     v-for="child_work in taskWorks(child_task)" :key="child_work.id"
                     v-bind:style="workProps(child_work)" @click="openWorkEdit(child_work)"></div>
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
import WorkEdit from "@/views/components/WorkEdit";
import HorizontalScroll from 'vue-horizontal-scroll'
import 'vue-horizontal-scroll/dist/vue-horizontal-scroll.css'

export default {
  name: 'Statistics',
  data() {
    return {
      isHidden: true,
      isWorkHidden: true,
      content: '',
      hover: 0,

      findTask: '',
      interval_start: '',
      interval_end: '',

      works: [],
      work: {},

      openParents: [],
      tasks: [],
      task: {}
    };
  },
  components:{
    'v-taskEdit' : TaskEdit,
    'v-workEdit' : WorkEdit,
    'horizontal-scroll' : HorizontalScroll
  },
  computed: {
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
    taskWorks(){
      return task => this.works
          .filter(item => item.task.id === task.id)
          .filter(item => {
            if (!this.interval_end) return (new Date(this.formatDate(item.workStart)).getTime() === new Date(this.interval_start ? this.formatDate(this.interval_start): this.formatDate(new Date())).getTime());
            else return (new Date(this.formatDate(item.workStart)).getTime() >= new Date(this.formatDate(this.interval_start)).getTime() && new Date(this.formatDate(item.workStart)).getTime() <= new Date(this.formatDate(this.interval_end)).getTime());
          })

    },
    parents(){
      return this.tasks.filter(item => (!item.parent))
          .filter(item =>(this.taskWorks(item).length>0 || this.children(item).length > 0))
          .sort(function(f, s) {return f.task_start > s.task_start ? 1 : -1})
          .filter(item=>item.taskName.toLowerCase().includes(this.findTask.toLowerCase()));
      //can combine filters
    },
    children_task(){
      return this.tasks.filter(item => (item.parent))
          .filter(item =>(this.taskWorks(item).length>0))
          .sort(function(f, s) {return f.task_start > s.task_start ? 1 : -1});
    },
    tableHeight(){
      return  50*(1+this.parents.length)+ this.openParents.reduce(function (p, v) {
        return ( v.isOpen && this.parents.find(item => item.id === v.id) ? p+=(this.children_task.filter(item => (item.parent.id === v.id)).length) : p );
      }.bind(this), 0)*30+17;
    },

    workProps(){
      return work => ({
        width: ((100/3600*(new Date(work.workEnd) - new Date(work.workStart))/1000) >= 5 ? (100/3600*(new Date(work.workEnd) - new Date(work.workStart))/1000) : 0) + 'px',
        left: (new Date(work.workStart) - new Date (work.workStart).setHours(0,0,0,0))/36/1000 + 'px',
        background: '#6c7bfe',
        position: 'absolute'
      });
    },

    quantity(){
      return task => this.taskWorks(task).reduce(function (p, v) {
        return ( p += (new Date(v.workEnd).getTime() - new Date(v.workStart).getTime())/1000/60 );
      }, 0);
    },
    formateQuantity(){
      return quant => {
        let mm = quant%60;
        if (mm < 10) mm = '0' + mm;
        let hh = quant/60>>0;
        if (hh < 10) hh = '0' + hh;
        return  hh.slice(0,2) + ':' + mm.slice(0,2);
      }
    },
    allTask(){
      return this.parents.filter(item =>(this.taskWorks(item).length>0)).concat(this.children_task);
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
        },
        error => {
          this.content =
              (error.response && error.response.data && error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
    UserService.getWorksByUser(this.currentUser.id).then(
        response => {
          this.works = response.data;
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
      // eslint-disable-next-line no-console
      console.log();



      return this.children_task.filter(item => (item.parent.id === parent.id))
    },
    children_open(parent){
      if (this.openParents.find(item => item.id === parent.id) &&
          this.openParents.find(item => item.id === parent.id).isOpen){
        return this.children(parent);
      }
      else return [];
    },
    openWorkEdit(work){
      if (work === 0) {this.work = {}}
      else {
        this.work = {}
        Object.assign(this.work, work);
      }
      this.isWorkHidden = false;
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
    },
    put(task){
      this.tasks.splice(this.tasks.findIndex(item => item.id == task.id), 1, task);

    },
    del(task){
      this.tasks.splice(this.tasks.findIndex(item => item.id === task.id),1);
    },
    workPost(work){
      this.works.push(work);
      Object.assign(this.work, work);
    },
    workPut(work){
      this.works.splice(this.works.findIndex(item => item.id == work.id), 1, work);

    },
    workDel(work){
      this.works.splice(this.works.findIndex(item => item.id === work.id),1);
    },
    test(){

    }
  }
};
</script>