github地址：[https://github.com/vuejs/router](https://github.com/vuejs/router)

- 安装 

```
npm install vue-router --save-dev
```
- vue过渡动画

  > 四种动画时间点

  ![1](F:\我的\web\vue\router\imgs\1.jpg)

  ```vue
  <template>
      <div>
          <button v-on:click="show = !show ">显示/隐藏</button>
         <!--要控制动画的放入transition里边, fade可以随便指定，需和样式对应-->
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
  ```

  ​