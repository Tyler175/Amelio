<template>
  <div class="login-page">
    <div class="form">

      <form class="login-form" name="form" @submit.prevent="handleLogin">
        <h1>Вход</h1>
        <div class="form-group">
          <input
            v-model="user.username"
            v-validate="'required|min:3|max:20'"
            type="text"
            class="form-control"
            placeholder="Логин"
            name="логин"
          />
          <div
              v-if="errors.has('логин')"
              class="alert-error"
          >{{errors.first('логин')}}</div>
        </div>
        <div class="form-group">

          <input
            v-model="user.password"
            v-validate="'required|min:6|max:40'"
            type="password"
            class="form-control"
            placeholder="Пароль"
            name="пароль"
          />
          <div
              v-if="errors.has('пароль')"
              class="alert-error"
          >{{errors.first('пароль')}}</div>
        </div>
        <div class="form-group">
          <button class="btn" :disabled="loading">
            <span v-show="loading" class="spinner-border spinner-border-sm"></span><!--Not used yet-->
            <span>Войти</span>
          </button>
        </div>
        <div class="form-group">
          <div v-if="message" class="alert alert-error" role="alert">
            <div v-if="message === 'Bad credentials'">Неверный логин или пароль</div>
            <div v-else>{{message}}</div>
          </div>
        </div>
        <p class="registr">Нет аккаунта?
          <router-link to="/register">
          Зарегистрироваться
          </router-link>

        </p>
      </form>
    </div>
  </div>
</template>

<script>
import User from '../models/user';

export default {
  name: 'Login',
  data() {
    return {
      user: new User('', ''),
      loading: false,
      message: ''
    };
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    }
  },
  created() {
    if (this.loggedIn) {
      this.$router.push('/profile');
    }
  },
  methods: {
    handleLogin() {
      this.loading = true;
      this.$validator.validateAll().then(isValid => {
        if (!isValid) {
          this.loading = false;
          return;
        }

        if (this.user.username && this.user.password) {
          this.$store.dispatch('auth/login', this.user).then(
            () => {
              this.$router.push('/profile');
            },
            error => {
              this.loading = false;
              this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
            }
          );
        }
      });
    }
  }
};
</script>

