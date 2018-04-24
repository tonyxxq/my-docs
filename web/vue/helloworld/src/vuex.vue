<template>
    <div id="app">
        <h1>Hello Vuex</h1>
        <p>{{ count }}</p>
        <p>
            <button @click="jiaplus({a:10})">+plus</button>
            <button @click="jianplus">-plus</button>
        </p>
    </div>
</template>
<script>
    import {mapState, mapMutations, mapGetters, mapActions} from 'vuex'

    export default {
        name: 'app',
        computed: {
            ...mapState([
                "count"
            ]),
            ...mapGetters([
                "count"
            ])
        },
        methods: {
            ...mapMutations([
                "jia",
                "jian"
             ]),
            ...mapActions([
                "jiaplus",
                "jianplus"
            ])
        }
    }
</script>