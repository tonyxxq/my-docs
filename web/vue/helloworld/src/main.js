// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
// import App from './App'
// import router from './router'
// import router from "./Router"
// import transition from "./transition"
// import rt from "./router-transition"
import store from "./store"
import vuex from "./vuex.vue"

new Vue({
  el: '#app',
  store,
  render: x => x(vuex)
})
