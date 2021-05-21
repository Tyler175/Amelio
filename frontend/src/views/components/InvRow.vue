<style scoped>
  .task{
    cursor: auto;
    padding: 5px 30px;
    font-weight: normal;
  }
</style>
<template>
  <div class="row">
    <div class="task">{{ user.username }} - {{ user.email }}</div>
    <button class="cross" @click="del">X</button>
  </div>
</template>

<script>

export default {
  name: "InvRow",
  props: ['user', 'action'],
  data() {
    return {
      message: ''
    };
  },
  methods: {
    del(){
      this.action(this.user);
    }
  }
}
</script>