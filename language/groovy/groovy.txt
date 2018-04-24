groovy作为下一代的java语言，但是和java不同的是它是动态语言，其语法类似python


如果文件内没有class，默认生成一个class，名称和文件名相同且在class内部生成一个main方法和一个run方法，run内部存放代码，main调用run方法
如果只有一个class，和java一样
如果有多个class，生成读个class文件，且在第一个class内必须有main


代码见：HelloGroovy

首先在idea中引入的时候，报了这个错：
Groovy distribution in specified path is broken,Cannot determine versioin
解决办法是降低groovy版本，或升级idea版本


参考文章： http://blog.csdn.net/kmyhy/article/details/4200563

java的一切语法特性几乎groovy都支持
-------------------新特性---------------------------
1.没有类型，统一都是对象，继承object，统一使用def定义类型，且类型根据赋值对象会发生变化
def var = 1
println(var.class)
var = "hello groovy"
println(var.class)
var = 1l
print(var.class)

输出：
class java.lang.Integer
class java.lang.String
class java.lang.Long

2.不需要写public(默认)

3.不需要语句结束符“;”

4.字符串连接符,可连接多行 """
                          1。。。
			  2。。。
                          """
同时兼容旧版本

5.循环（i 可以不用定义类型）
def var = """
             这是第一行
             这是第二行
          """

def repate(val) {
    for (i in 0..2) {
        println(val)
    }
}

repate(var)

输出：
这是第一行
这是第二行


这是第一行
这是第二行


这是第一行
这是第二行

6.Gstring，可以在String中直接使用出现过的变量
例如将上面的方法改为： println("line num ${i} : val ${val}")

7.默认参数值和python相同

8.集合
常用的两种：
java.util.Collection和java.util.Map。
(1)：java.util.Collection
// 增加元素
def collect = ["a", "b", "c"]
collect.add("d")
collect[4] = 'e'
collect << 'f'
collect = collect + 'g'
println(collect)

// 删除元素
collect - 'g'
collect.remove("a")
collect = collect - collect[0..2]
println(collect)

// 修改元素
collect[0] = "z"
println(collect)

// 查询,支持负索引，类似python
println(collect[1])
println(collect[-1])

// 遍历元素且获取下标
collect.eachWithIndex{ it,i -> print("${i},") }

(2)：map,注意：初始化的使用用的[],不是{}
// 增加元素
def map = ['name': 'john', 'age': 14, 'sex': 'boy']
map.put("height", 180)
map = map + ['level': 10]
map.father='Keller'
println(map)

// 删除元素
map.remove("father")
println(map)

// 修改元素
map.height=167
println(map)

// 查询元素
println(map.height)


9、闭包（Closure）,"代码块",类似‘匿名类’或内联函数的概念,或者python中的lambda,闭包可以作为参数传递
// 闭包用于迭代,相当于python中的map
def map = ['name': 'john', 'age': 14, 'sex': 'boy']
map.each({ key, value ->    // key,value两个参数用于接受每个元素的键/值
    println "$key:$value"
})
map.each({ println it })     // it是一个关键字，代表map集合的每个元素
map.each({ println it.getKey() + "-->" + it.getValue() })

// 闭包用于定义方法
def say = { word ->
    println "Hi,$word!"
}

// 调用：
say('groovy')
say.call('groovy&grails')

10.类
(1)不需要public修饰符
(2)实例化对象时，不需要类型说明
(3)不需要getter/setter方法
(4)不需要构造函数
(5)不需要return，默认返回最后一行
(6)不需要()号，Groovy中方法调用可以省略()号（构造函数除外）
class Person {
    def name
    def age

    String toString() {
        "$name,$age"
    }
}

def p1 = new Person();
p1.name = "xxq"
p1.age = "18"
println(p1)
def p2 = new Person([name:'zxl',age:12])
println(p2)

输出：
xxq,18
zxl,12

11.动态性
Groovy所有的对象都有一个元类metaClass，我们可以通过metaClass属性访问该元类。通过元类，可以为这个对象增加方法（在java中不可想象,反射）

def msg = "Hello!"
println msg.metaClass
String.metaClass.up = { delegate.toUpperCase() }
println msg.up()
// 通过元类，我们还可以检索对象所拥有的方法和属性（就象反射）：
msg.metaClass.methods.each({ println it.name })
msg.metaClass.properties.each({ println it.name })

