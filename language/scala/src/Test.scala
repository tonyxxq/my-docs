/**
  * Created by Administrator on 2018/11/5 0005.
  */
object Test {
  def main(args: Array[String]): Unit = {

    // 定义列表
    val x = List(1, 2, 3, 4)

    // head 返回列表第一个元素
    println(x.head)

    // tail 返回一个列表，包含除了第一元素之外的其他元素
    println(x.tail)

    // isEmpty 在列表为空时返回true
    println(x.isEmpty)

    // 列表连接 ::: 或 List.concat(x, x2)
    val x2 = List(5, 6, 7)
    val x3 = x ::: x2

    // fill, 指定重复数量的元素列表
    val num = List.fill(10)(2)         // 重复元素 2, 10 次

    // tabulate, 通过给定的函数来创建列表， 第一个参数为元素的数量，可以二维，第二个为函数，初始值为 0
    val squares = List.tabulate(6)(n => n * n)
    println(squares)

    // reverse ，序列翻转

    // intersect 计算多个集合的交集
    // forall 检测所有元素是否满足条件 返回 boolean
    // distinct 去重

    // :+ 复制添加元素后列表, 因为列表是不能修改的， 列表
    val a = List(1)
    val b = a:+2
    print(b)
  }
}
