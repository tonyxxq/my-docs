import Vue from "vue"
import Vuex from "vuex"

Vue.use(Vuex)

const state = {
    count: 0
}

const mutations = {
    jia(state, n) {
        state.count += n.a
    },
    jian(state) {
        state.count--
    }
}

/**
 * 对数据过滤
 * @type {{count(*): *}}
 */
const getters = {
    count(state) {
        return state.count += 0
    }
}

const actions = {
    jiaplus({commit}) {
        commit("jia", {a: 10})
    },
    jianplus({commit}) {
        commit("jian")
    }
}

export default new Vuex.Store({
    state,
    mutations,
    getters,
    actions
})

