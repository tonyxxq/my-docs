package groovycode

/**
 * Created by Administrator on 2018/1/9 0009.
 */
// 可以按照java
/*class GroovyCode {
    public static void main(String[] args) {
       System.out.print("hello Groovy")
    }
}*/
// groovy
/*println "Hello Groovy"*/

// 一切变量皆对象（def定义），但是复制的时候可以改变类型
/*def var = 1
println(var.class)
var = "hello groovy"
println(var.class)
var = 1l
print(var.class)*/

// 循环
/*def var = """
             这是第一行
             这是第二行
          """

def repate(val ,repateNum=2) {
    for (i in 0..repateNum) {
        println("line num ${i} : val ${val}")
    }
}

repate(var,3)*/

/*print((1..2).class)*/

/*// 增加元素
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
println(collect[-1])*/

/*// 增加元素
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
println(map.height)*/

/*// 闭包用于迭代
def map = ['name': 'john', 'age': 14, 'sex': 'boy']
map.each({ key, value ->    // key,value两个参数用于接受每个元素的键/值
    println "$key:$value"
})
map.each ({ println it })     //it是一个关键字，代表map集合的每个元素
map.each({ println it.getKey() + "-->" + it.getValue() })*/

/*
// 闭包用于定义方法
def say = { word ->
    println "Hi,$word!"
}
// 调用：
say('groovy')
say.call('groovy&grails')*/
/*

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
println(p2)*/

/*def msg = "Hello!"
println msg.metaClass
String.metaClass.up = { delegate.toUpperCase() }
println msg.up()
// 通过元类，我们还可以检索对象所拥有的方法和属性（就象反射）：
msg.metaClass.methods.each({ println it.name })
msg.metaClass.properties.each({ println it.name })*/
