import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter)

const  home = {template:"<div>Home内容</div>"}
const  first = {template:"<div>First内容</div>"}
const  second = {template:"<div>Second内容</div>"}

const router = new VueRouter({
    model:"history",
    base:__dirname,
    routes:[
        {path:"/",component:home},
        {path:"/first/:aa/:bb",component:first},
        {path:"/second/:cc/:dd",component:second}
    ]
})

new Vue({
    router,
    template:
      `
      <div>
          <ol>
              <li><router-link to="/">/</router-link></li>
              <li><router-link to="/first/xxx/xx">first</router-link></li>
              <li><router-link to="/second/sss/ss">second</router-link></li>
          </ol>
          <router-view></router-view>
          {{ $route.params }}
      </div>
      `
}).$mount("#app")
