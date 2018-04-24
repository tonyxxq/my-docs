<template>
    <div>
        <button v-on:click="show = !show ">显示/隐藏</button>
       <!--要控制动画的放入transition里边-->
        <transition name="fade">
            <p v-if="show">
                Hello World!
            </p>
        </transition>
    </div>
</template>

<script>
export default {
    name:"demo",
    data(){
        return  {
            show:true
        }
    }
}
</script>
<style scoped>
    .fade-enter-active, .fade-leave-active{
        transition:opacity 2s
    }

    .fade-enter, .fade-leave-active{
        opacity:0
    }
</style>

