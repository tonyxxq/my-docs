

/**
  * Created by Administrator on 2018/10/31 0031.
  */
object HelloWorld {
  /* 这是我的第一个 Scala 程序
   * 以下程序将输出'Hello World!'
   */

  def main(args: Array[String]) {
    var itb = Iterator(20, 40, 2, 50, 69, 90)
      println(itb.mkString)
  }

  trait Equal1 {
    val xx= "stttt";
    def isEqual(x: Any): Boolean = true
    def isNotEqual(x: Any): Boolean = !isEqual(x)
  }

  class Point(xc: Int, yc: Int) extends  Equal1 {
    var x: Int = xc
    var y: Int = yc
    override def isNotEqual(x: Any): Boolean = !isEqual(x)
  }
}
