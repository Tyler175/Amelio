<template>
  <div class="login-page">
    <div class="form">

      <form class="registration-form" name="form" @submit.prevent="handleRegister">
        <div v-if="!successful">
          <h1>Регистрация</h1>
          <div class="form-group">
            <input
              v-model="user.username"
              v-validate="'required|min:3|max:20'"
              type="text"
              class="form-control"
              placeholder="Имя"
              name="username"
            />
            <div
              v-if="submitted && errors.has('username')"
              class="alert-error"
            >Введите логин</div>
          </div>
          <div class="form-group">
            <input
              v-model="user.email"
              v-validate="'required|email|max:50'"
              type="email"
              class="form-control"
              placeholder="Почта"
              name="email"
            />
            <div
              v-if="submitted && errors.has('email')"
              class="alert-error"
            >Введите почту</div>
          </div>
          <div class="form-group">
            <input
              v-model="user.password"
              v-validate="'required|min:6|max:40'"
              type="password"
              class="form-control"
              placeholder="Пароль"
              name="password"
            />
            <div
              v-if="submitted && errors.has('password')"
              class="alert-error"
            >Введите пароль</div>
          </div>
          <div class="form-group">
            <button class="btn">Зарегистрироваться</button>
          </div>
        </div>
        <p class="registr">Уже есть аккаунт?
          <router-link to="/login">
            Войти
          </router-link>

        </p>
      </form>

      <div
        v-if="message"
        class="alert"
        :class="successful ? 'alert-success' : 'alert-error'"
      >{{message}}</div>
    </div>
  </div>
</template>

<script>
import User from '../models/user';

export default {
  name: 'Register',
  data() {
    return {
      user: new User('', '', ''),
      submitted: false,
      successful: false,
      message: ''
    };
  },
  computed: {
    loggedIn() {
      return this.$store.state.auth.status.loggedIn;
    }
  },
  mounted() {
    if (this.loggedIn) {
      this.$router.push('/profile');
    }
  },
  methods: {
    handleRegister() {
      this.message = '';
      this.submitted = true;
      this.$validator.validate().then(isValid => {
        if (isValid) {
          this.$store.dispatch('auth/register', this.user).then(
            data => {
              this.message = data.message;
              this.successful = true;
            },
            error => {
              this.message =
                (error.response && error.response.data && error.response.data.message) ||
                error.message ||
                error.toString();
              this.successful = false;
            }
          );
        }
      });
    }
  }
};
</script>