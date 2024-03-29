import axios from 'axios';
import authHeader from './auth-header';

const API_URL = 'http://localhost:8080/api/';

class UserService {
  getPublicContent() {//not used
    return axios.get(API_URL + 'all');
  }


  getToday() {
    return axios.get(API_URL + 'today', { headers: authHeader() });
  }
  getCurrentWork(user_id, task_id){
    return axios.get(API_URL + 'works/' + user_id + '/' + task_id, { headers: authHeader() });
  }
  postWork(work) {
    return axios.post(API_URL + 'works', {
      workStart: work.workStart,
      workEnd: work.workEnd,
      task: work.task,
      user: work.user
    }, { headers: authHeader() })
  }
  putWork(work) {
    return axios.put(API_URL + 'works/'+work.id, {
      workStart: work.workStart,
      workEnd: work.workEnd,
      task: work.task,
      user: work.user
    }, { headers: authHeader() })
  }
  setWorkEnd(work) {
    return axios.put(API_URL + 'works', {
      workStart: work.workStart,
      workEnd: work.workEnd,
      task: work.task,
      user: work.user
    }, { headers: authHeader() })
  }
  getTimer(id){
    return axios.get(API_URL + 'today/timer/' + id, { headers: authHeader() })
  }
  saveTimer(id, timer){
    return axios.put(API_URL + 'today/timer/' + id, {timer: timer}, { headers: authHeader() })
  }

  getWorksByUser(user_id){
    return axios.get(API_URL + 'works', { headers: authHeader(), params:{user_id: user_id} });
  }
  getWorksByProject(project_id){
    return axios.get(API_URL + 'works', { headers: authHeader(), params:{project_id: project_id} });
  }
  delWork(id){
    return axios.delete(API_URL + 'works/' + id, { headers: authHeader() });
  }

  getTasks() {
    return axios.get(API_URL + 'tasks', { headers: authHeader() });
  }
  getTaskById(id){
    return axios.get(API_URL + 'tasks/' + id, { headers: authHeader() });
  }
  getTaskByUserAndName(user_id, task_name){
    return axios.get(API_URL + 'tasks', { headers: authHeader(), params:{user_id: user_id, task_name: task_name} });
  }
  postTask(task){
    if (task.taskComplete === null) {task.taskComplete = false}
    return axios.post(API_URL + 'tasks', {
      taskName: task.taskName,
      task_description: task.task_description,
      parent: task.parent,
      task_start: task.task_start,
      task_end: task.task_end,
      taskComplete: task.taskComplete,
      taskDelete: task.taskDelete,
      current: task.current,
      workers: task.workers,
      plans: task.plans
    }, { headers: authHeader() })
  }
  putTask(task){
    return axios.put(API_URL + 'tasks/'+task.id, {
      taskName: task.taskName,
      task_description: task.task_description,
      parent: task.parent,
      task_start: task.task_start,
      task_end: task.task_end,
      taskComplete: task.taskComplete,
      taskDelete: task.taskDelete,
      current: task.current,
      workers: task.workers,
      plans: task.plans
    }, { headers: authHeader() })
  }
  delTask(id){
    return axios.delete(API_URL + 'tasks/' + id, { headers: authHeader() });
  }
  postPlan(plan){return axios.post(API_URL + 'tasks/plan', {task: plan.task, user: plan.user, plan: plan.plan}, { headers: authHeader() })}
  putPlan(plan, task){return axios.put(API_URL + 'tasks/plan', {task: task, user: plan.user, plan: plan.plan}, { headers: authHeader() })}
  delPlan(plan){
    return axios.delete(API_URL + 'tasks/plan', { headers: authHeader(), params:{plan: plan} });
  }

  getUsersByEmail(email){
    return axios.get(API_URL + 'users', { headers: authHeader(), params:{email: email}});
  }


  isPasswordCorrect(password){
    return axios.get(API_URL + 'users', { headers: authHeader(), params:{password: password}});
  }
  changeRoles(user){
    {return axios.put(API_URL + 'users/'+user.id, {roles: user.roles}, { headers: authHeader() })}
  }

  getUser() {
    return axios.get(API_URL + 'user', { headers: authHeader() });
  }
  delUser(user){
    return axios.delete(API_URL + 'users/' + user.id, { headers: authHeader() });
  }
  getEditPermissions(id){
    return axios.get(API_URL + 'tasks/permissions/' + id, { headers: authHeader() });
  }

  getInvitations(){
    return axios.get(API_URL + 'projects/invitations', { headers: authHeader() });
  }
  getProjects() {
    return axios.get(API_URL + 'projects', { headers: authHeader() });
  }
  getProject(id) {
    return axios.get(API_URL + 'projects/' + id, { headers: authHeader() });
  }
  postProject(){
    return axios.post(API_URL + 'projects', {name: "Новый проект"}, { headers: authHeader() })
  }
  changeProjectName(project){
    return axios.put(API_URL + 'projects/' + project.id, {name: project.name}, { headers: authHeader() });
  }
  changeInvitation(project){
    return axios.put(API_URL + 'projects/invitation/' + project.id, {invitations: project.invitations}, { headers: authHeader() });
  }
  changeUsers(project){
    return axios.put(API_URL + 'projects/workers/' + project.id, {workers: project.workers}, { headers: authHeader() });
  }
  changeTasks(project){
    return axios.put(API_URL + 'projects/tasks/' + project.id, {tasks: project.tasks}, { headers: authHeader() });
  }
  delProject(id){
    return axios.delete(API_URL + 'projects/' + id, { headers: authHeader() });
  }
  changePassword(id, oldPassword, newPassword){
    return axios.put(API_URL + 'users/' + id + '/' + oldPassword, {password: newPassword}, { headers: authHeader() });
  }
  changeAbout(id, about){
    return axios.put(API_URL + 'profile/' + id, {description: about}, { headers: authHeader() });
  }

  getModeratorBoard() {
    return axios.get(API_URL + 'mod', { headers: authHeader() });
  }
  getAdminBoard() {
    return axios.get(API_URL + 'admin', { headers: authHeader() });
  }
}

export default new UserService();
