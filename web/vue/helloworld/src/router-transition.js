import Vue from "vue"
import VueRouter from "vue-router"

Vue.use(VueRouter)

const first = {
    template: `
        <div>
            <p>这是第一个菜单</p>
        </div>
    `
}

const second = {
    template: `
        <div>
            <p>这是第二个菜单</p>
        </div>
    `
}

const router = new VueRouter({
        model: "history",
        base: "__dirname",
        routes: [
            {path: "/first", component: first},
            {path: "/second", component: second}
        ]
    }
)

new Vue({
    router,
    template: `
        <div>
            <h2>这是菜单</h2>
            <ul>
                <li>
                    <router-link to="/first">菜单一</router-link>
                </li>
                <li>
                    <router-link to="/second">菜单二</router-link>
                </li>
            </ul>
            <transition name="fade" mode="out-in">
                <router-view></router-view>
            </transition>
        </div>
    `
}).$mount("#app")

