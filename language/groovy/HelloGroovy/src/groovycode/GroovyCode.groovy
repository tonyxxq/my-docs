package groovycode

/**
 * Created by Administrator on 2018/1/9 0009.
 */
// ���԰���java
/*class GroovyCode {
    public static void main(String[] args) {
       System.out.print("hello Groovy")
    }
}*/
// groovy
/*println "Hello Groovy"*/

// һ�б����Զ���def���壩�����Ǹ��Ƶ�ʱ����Ըı�����
/*def var = 1
println(var.class)
var = "hello groovy"
println(var.class)
var = 1l
print(var.class)*/

// ѭ��
/*def var = """
             ���ǵ�һ��
             ���ǵڶ���
          """

def repate(val ,repateNum=2) {
    for (i in 0..repateNum) {
        println("line num ${i} : val ${val}")
    }
}

repate(var,3)*/

/*print((1..2).class)*/

/*// ����Ԫ��
def collect = ["a", "b", "c"]
collect.add("d")
collect[4] = 'e'
collect << 'f'
collect = collect + 'g'
println(collect)

// ɾ��Ԫ��
collect - 'g'
collect.remove("a")
collect = collect - collect[0..2]
println(collect)

// �޸�Ԫ��
collect[0] = "z"
println(collect)

// ��ѯ,֧�ָ�����������python
println(collect[1])
println(collect[-1])*/

/*// ����Ԫ��
def map = ['name': 'john', 'age': 14, 'sex': 'boy']
map.put("height", 180)
map = map + ['level': 10]
map.father='Keller'
println(map)

// ɾ��Ԫ��
map.remove("father")
println(map)

// �޸�Ԫ��
map.height=167
println(map)

// ��ѯԪ��
println(map.height)*/

/*// �հ����ڵ���
def map = ['name': 'john', 'age': 14, 'sex': 'boy']
map.each({ key, value ->    // key,value�����������ڽ���ÿ��Ԫ�صļ�/ֵ
    println "$key:$value"
})
map.each ({ println it })     //it��һ���ؼ��֣�����map���ϵ�ÿ��Ԫ��
map.each({ println it.getKey() + "-->" + it.getValue() })*/

/*
// �հ����ڶ��巽��
def say = { word ->
    println "Hi,$word!"
}
// ���ã�
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
// ͨ��Ԫ�࣬���ǻ����Լ���������ӵ�еķ��������ԣ������䣩��
msg.metaClass.methods.each({ println it.name })
msg.metaClass.properties.each({ println it.name })*/
