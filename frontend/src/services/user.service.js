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
  getTasks() {
    return axios.get(API_URL + 'tasks', { headers: authHeader() });
  }
  getTaskById(id){
    return axios.get(API_URL + 'tasks/' + id, { headers: authHeader() });
  }
  postTask(task){
    if (task.taskComplete === null) {task.taskComplete = false}
    return axios.post(API_URL + 'tasks', {
      task_name: task.task_name,
      task_description: task.task_description,
      username: task.username,
      task_start: task.task_start,
      task_end: task.task_end,
      task_work: task.task_work,
      parent: task.parent,
      work_start: task.work_start,
      taskComplete: task.taskComplete
    }, { headers: authHeader() })
  }
  putTask(task){
    return axios.put(API_URL + 'tasks/'+task.id, {
      task_name: task.task_name,
      task_description: task.task_description,
      username: task.username,
      task_start: task.task_start,
      task_end: task.task_end,
      task_work: task.task_work,
      parent: task.parent,
      work_start: task.work_start,
      taskComplete: task.taskComplete
    }, { headers: authHeader() })
  }
  delTask(id){
    return axios.delete(API_URL + 'tasks/' + id, { headers: authHeader() });
  }



  getUserBoard() {
    return axios.get(API_URL + 'user', { headers: authHeader() });
  }
  getProjects() {
    return axios.get(API_URL + 'projects', { headers: authHeader() });
  }
  getModeratorBoard() {
    return axios.get(API_URL + 'mod', { headers: authHeader() });
  }

  getAdminBoard() {
    return axios.get(API_URL + 'admin', { headers: authHeader() });
  }
}

export default new UserService();
