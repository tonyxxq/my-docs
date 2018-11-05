- Scala 有方法与函数，二者在语义上的区别很小。Scala 方法是类的一部分，而函数是一个对象可以赋值给一个变量。换句话来说在类中定义的函数即是方法。

  Scala 中的方法跟 Java 的类似，方法是组成类的一部分。

  Scala 中的函数则是一个完整的对象，Scala 中的函数其实就是继承了 Trait 的类的对象。

  Scala 中使用 val 语句可以定义函数，def 语句定义方法。

  > 定义方法，如果方法没有返回值，可以返回为 **Unit**，这个类似于 Java 的 **void**, 实例如下：

  ```scala
  def main(args: Array[String]) {
      println(addInt(5, 7));
  }
  
  def addInt( a:Int, b:Int ) : Int = {
      var sum:Int = 0;
      sum = a + b;
  
      return sum
  }
  ```

  > 函数定义

  ```scala
  val f = (x: Int) => x + 3
  ```

  > 函数可作为一个参数传入到方法中，而方法不行。

  ```scala
  def main(args: Array[String]) {
     println(m2(f));
  }
  
  // 定义方法
  def m2(f: (Int, Int) => Int): Int = f(2, 6);
  
  // 定义函数
  val f = (a: Int, b: Int) => a + b;
  ```

  > 在Scala中无法直接操作方法，如果要操作方法，必须先将其转换成函数。有两种方法可以将方法转换成函数

  ```scala
  // 在方法名称m后面紧跟一个空格和下划线告诉编译器将方法 m 转换成函数，而不是要调用这个方法。
  val f1 = m _
  
  // 显示地告诉编译器需要将方法转换成函数
  val f1: (Int) => Int = m
  
  // 如果直接使用方法名称作为参数也表示方法转为函数
  ttt(m0)
  ```

- 方法的点和括号

  > 如果方法有<font color="red">0或者1个参数</font>，括号可以丢掉

  ```scala
  // 下面两个输出是一样的
  println(1.to(3))
  println(1 to 3)
  
  // 当只有两个元素的时候， 交换元素
  val x = (1, 2)
  println(x.swap)
  ```

- 多行注释，和 python 一致

  ```
  ​```
  这是注释
  ​```
  ```

- 元组

  ```scala
  // 定义
  val x = (1, 2,3)
  
  // 访问， 下划线加数字，数字是元素的序号
  println(x._1)
  
  // Tuple.productIterator()方法遍历元组的所有元素
  val x = (1, 2, 3)
  x.productIterator.foreach(i => println(i))
  ```

- 闭包

  > 函数可以使用外部的变量

  ```scala
  def main(args: Array[String]) {  
        println( "muliplier(1) value = " +  multiplier(1) )  
        println( "muliplier(2) value = " +  multiplier(2) )  
  }
  var factor = 3
  val multiplier = (i:Int) => i * factor  
  ```

- 数组， 使用数组前需要引入包  import Array._

  > 一维数组

  ```scala
  // 声明数组
  var z:Array[String] = new Array[String](3)
  或
  var z = new Array[String](3)
  或
  var z = Array("Runoob", "Baidu", "Google")
  
  // 给元素复制，最后一个是计算 4/2=2
  z(0) = "Runoob"; z(1) = "Baidu"; z(4/2) = "Google"
  
  // 获取元素
  z(0)
  
  // 输出所有数组元素
  for ( x <- myList ) {
      println( x )
  }
  
  // 计算数组所有元素的总和
  var total = 0.0;
  for ( i <- 0 to (myList.length - 1)) {
      total += myList(i);
  }
  ```

  > 多维数组

  ```scala
  def main(args: Array[String]) {
      var myMatrix = ofDim[Int](4, 4)
      val random = new Random()
      
      // 定义二维数组
      for (i <- 0 to 3) {
        for (j <- 0 to 3) {
          myMatrix(i)(j) = random.nextInt(10);
        }
      }
  
      // 打印输出
      for (i <- 0 to 3) {
        for (j <- 0 to 3) {
          print(myMatrix(i)(j) + "  ");
          if (j == 3) {
            println();
          }
        }
      }
    }
  ```

  > 合并数组

  ```scala
   def main(args: Array[String]) {
       var myList1 = Array(1.9, 2.9, 3.4, 3.5)
       var myList2 = Array(8.9, 7.9, 0.4, 1.5)
  
       var myList3 =  concat( myList1, myList2)
  
       // 输出所有数组元素
       for ( x <- myList3 ) {
           println( x )
       }
  }
  ```

  > range 函数 生成区间，和 python 类似

  ```scala
  // 指定开始、结束位置、步长
  var myList1 = range(10, 20, 2)
  ```

  > 其他操作

  ```
  // empty
  ```

- to  和 untile

  > 结果都是 Range 类型

  ```scala
  // 结果 Range(1, 2, 3, 4, 5, 6, 7, 8, 9)
  println(1 until 10)
  
  // 结果 Range(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  println(1 to 10)
  ```

- 集合

  - Map

    ```scala
    // 定义 Map
    var x:Map[String,Int] = Map("one" -> 1, "two" -> 2, "three" -> 3)
    
    // 向 map 添加元素
    x += ("four" -> 4)
    x += ("five" -> 5)
    
    // 覆盖元素
    x += ("five" -> 5)
    
    // 删除指定 key 的元素, 多个用 , 分割, 和 remove 一样
    x -= ("five", "one")
    
    // 返回迭代器， 下面每个元素的结果是元组类型
    var list =  x.iterator.toList
    println(list)
    
    // addString，将 Map 中的所有元素附加到 StringBuilder，可加入分隔符，keys,values也可以使用 addString
    var sb1 = new StringBuilder;
    x.addString(sb1)
    print(sb1)
    
    var sb2 = new StringBuilder;
    x.addString(sb2, "||")
    print(sb2)
    
    // 返回指定 key 的值
    println(x.apply("two"))
    
    // clear 清空 map
    // clone 克隆
    
    // count 返回满足条件的元素数量, e 是一个元组，第一个值为key， 第二个值为 value
    x.count(e => e._2 > 0)
    
    // contains判断是否存在指定的 key
    x.contains("two")
    
    // drop：丟掉前几个元素, dropRight：丢掉后面几个元素
    x = x drop 2
    
    // empty 返回相同类型的空 Map, 之前的 map 不被影响
    var x2 = x.empty
    
    // equals 判断两个 map 是否相等
    
    // exists, filter, filterKeys, find, foreach, isEmpty, keys, last, max, min, 	    mkString, product, size, sum
    
    // init 返回所有元素， 除了最后一个; tail 返回所有元素， 除了第一个
    // take，返回前 n 个; takeRight, 返回后 n 个元素
    // toArray, toList, toSeq, toSet
    ```

  - List

    > 注意：列表是不能改变的值一旦被定义就不能改变， 其他没有列出的方法和 map 一致

    ```scala
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
    ```

  - Set

    > 默认是 set 不可变的
    >
    > 如果想使用可变集合，需要引用 **scala.collection.mutable.Set** 包

    ```scala
    // 定义 Set
    val x = Set(1,3,5,7)
    
    // ++ 连接两个集合, 重复的会去重
    val site1 = Set("Runoob", "Google", "Baidu")
    val site2 = Set("Faceboook", "Taobao")
    var site = site1 ++ site2
    
    // 计算交集 intersect
    
    // 其他的方法和 map 和 list 一致
    ```

  - Option

    ```scala
    // 定义 Option
    val x:Option[Int] = Some(5)
    ```

  - 元组

    ```scala
    // 创建两个不同类型元素的元组
    val x = (10, "Runoob")
    ```

  **注意：Scala 使用 Option、Some、None，避免使用 Null， 当返回的option有可能为None时使用getOrElse设置默认值。**

- Iterator 迭代器，迭代器 it 的两个基本操作是 **next** 和 **hasNext**。

  ```scala
  def main(args: Array[String]) {
      // next 和 hasNext  
      val it = Iterator("Baidu", "Google", "Runoob", "Taobao")
      while (it.hasNext){
           println(it.next())
      }
  	
      // 获取最大值， 最小值, 注意：取了一遍之后迭代器就是空的了，不能再进行其他操作
      val ita = Iterator(20,40,2,50,69, 90)
      val itb = Iterator(20,40,2,50,69, 90)
      println("最大元素是：" + ita.max )
      println("最小元素是：" + itb.min )
      
      // 获取长度
      val ita = Iterator(20,40,2,50,69, 90)
      val itb = Iterator(20,40,2,50,69, 90)  
      println("ita.size 的值: " + ita.size )
      println("itb.length 的值: " + itb.length )
      
      // 判断迭代器是否有指定的元素
      val itb = Iterator(20,40,2,50,69, 90)
      println(itb.contains(20))
      
      // 返回迭代器中满足条件的值的总数
      val itb = Iterator(20,40,2,50,69, 90)
      println(itb.count(a=>a>10))
      
      // 丢弃迭代器中前
      var itb = Iterator(20,40,2,50,69, 90)
      itb = itb.drop(2)
      println(itb.size)
      
      // 判断是否有满足条件的元素
      val itb = Iterator(20,40,2,50,69, 90)
      println(itb.exists(a=>a>40))
      
      // filter 过滤, 同理 filterNot相反 
      var itb = Iterator(20,40,2,50,69, 90)
      itb = itb.filter(a=>a>40)
      println(itb.size)
      
      // find， 找打第一个满足条件的值，注意：如果找到满足条件的元素，迭代器会被置于该元素之后；如果	   // 没有找到，会被置于终点。 找到返回元素, 没找到返回 None
      var itb = Iterator(20,40,2,50,69, 90)
      println(itb.find(a=>a>40).get)
      
      // forall，判断所有元素是否都满足条件
      var itb = Iterator(20,40,2,50,69, 90)
      println(itb.forall((a)=>a>10))
      
      // foreach,对每个元素执行操作，返回空
      var itb = Iterator(20, 40, 2, 50, 69, 90)
      itb.foreach((a) => {
        println(a)
      })
      
      // isEmpty
      // indexOf 返回指定Index的元素
      // indexWhere 返回下标满足条件的元素
      
      // mkString 将迭代器的所有元素转换为字符串
      def main(args: Array[String]) {
      var itb = Iterator(20, 40, 2, 50, 69, 90)
        println(itb.mkString)
    	}
      
      // product 计算迭代器所有元素的积 
      
      // def slice(from: Int, until: Int): Iterator[A] 返回迭代器片段
      // sum 求和
      // toArray 转为数组
      // toList
      // toMap
      // toSeq
      // toString
      
      // 返回指定的二元组序列， 第一个迭代器元素，第二个是taht, 和 python 中的 zip 类似
      def zip[B](that: Iterator[B]): Iterator[(A, B)
  }
  ```

- 类和对象

  > 类可以定义参数， 子类只可以继承一个父类。

  ```scala
  import java.io._
  
  class Point(val xc: Int, val yc: Int) {
     var x: Int = xc
     var y: Int = yc
     def move(dx: Int, dy: Int) {
        x = x + dx
        y = y + dy
        println ("x 的坐标点 : " + x);
        println ("y 的坐标点 : " + y);
     }
  }
  
  class Location(override val xc: Int, override val yc: Int,
     val zc :Int) extends Point(xc, yc){
     var z: Int = zc
  
     def move(dx: Int, dy: Int, dz: Int) {
        x = x + dx
        y = y + dy
        z = z + dz
        println ("x 的坐标点 : " + x);
        println ("y 的坐标点 : " + y);
        println ("z 的坐标点 : " + z);
     }
  }
  
  object Test {
     def main(args: Array[String]) {
        val loc = new Location(10, 20, 15);
  
        // 移到一个新的位置
        loc.move(10, 10, 5);
     }
  }
  ```

- Trait(特征)

  > 类似于java接口，但是比java接口更加强大，允许定义属性和方法的实现且可以多继承。
  >
  > 抽象方法需要在子类实现，非抽象方法在子类中需要使用 override 进行覆盖
  >
  > 多继承例如：  class Point(xc: Int, yc: Int) extends  Equal1 with Equal2 with Equal3{}

  ```scala
  trait Equal {
    def isEqual(x: Any): Boolean
    def isNotEqual(x: Any): Boolean = !isEqual(x)
  }
  
  class Point(xc: Int, yc: Int) extends Equal {
    var x: Int = xc
    var y: Int = yc
    def isEqual(obj: Any) = obj.isInstanceOf[Point] && obj.asInstanceOf[Point].x == x
    override def isNotEqual(x: Any): Boolean = !isEqual(x)
  }
  
  object Test {
     def main(args: Array[String]) {
        val p1 = new Point(2, 3)
        val p2 = new Point(2, 4)
        val p3 = new Point(3, 3)
  
        println(p1.isNotEqual(p2))
        println(p1.isNotEqual(p3))
        println(p1.isNotEqual(2))
     }
  }
  ```

- 模式匹配, 相当于 java 中的 switch

  ```scala
  def main(args: Array[String]) {
    println(matchTest("two"))
    println(matchTest("test"))
    println(matchTest(1))
    println(matchTest(6))
  }
  
  def matchTest(x: Any): Any = x match {
    case 1 => "one"
    case "two" => 2
    case y: Int => "scala.Int" // 类型匹配 相当于 isInstanceOf
    case _ => "many" // _ 相当于 default
  }
  ```

- 样例类

  - 构造器的每个参数都成为 val，除非显式被声明为var，但是并不推荐这么做；
  - 在伴生对象中提供了apply方法，所以可以不使用new关键字就可构建对象；
  - 提供unapply方法使模式匹配可以工作（常用）；
  - 生成toString、equals、hashCode和copy方法，除非显示给出这些方法的定义。

  ```scala
  def main(args: Array[String]) {
      val alice = Person("Alice", 25)
      val bob = Person("Bob", 32)
      val charlie = Person("Charlie", 32)
  
      for (person <- List(alice, bob, charlie)) {
          person match {
              case Person("Alice", 25) => println("Hi Alice!")
              case Person("Bob", 32) => println("Hi Bob!")
              case Person(name, age) =>
              println("Age: " + age + " year, name: " + name + "?")
          }
      }
  }
  
  // 样例类
  case class Person(name: String, age: Int)
  ```

- 异常处理和 java 中一致

- 提取器(Extractor)

  ```scala
  object Test {
     def main(args: Array[String]) {
        println ("Apply 方法 : " + apply("Zara", "gmail.com"));
        println ("Unapply 方法 : " + unapply("Zara@gmail.com"));
        println ("Unapply 方法 : " + unapply("Zara Ali"));
     }
     
      // 注入方法 (可选), 类似 java 中的构造函数, 传入参数生成一个对象，且不适用 new
      // 使用 Test("Zara", "gmail.com") 生成对象
     def apply(user: String, domain: String) = {
        user +"@"+ domain
     }
  
     // 提取方法（必选）, 从对象中提取参数
     def unapply(str: String): Option[(String, String)] = {
        val parts = str split "@"
        if (parts.length == 2) {
           Some(parts(0), parts(1)) 
        } else {
           None
        }
     }
  }
  ```

- 文件 I/O

  ```scala
  // 写入文件，读取根目录下文件 test.txt
  val printWiter = new PrintWriter(new File("test.txt"));
  printWiter.write("Hello World");
  printWiter.close();
  
  // 读取文件, 会在项目根目录下创建 test.txt
  Source.fromFile("test.txt").foreach(println)
  
  // 获取屏幕上的输入
  val line = StdIn.readLine()
  println("谢谢，你输入的是: " + line)
  ```

- for 循环

  ```scala
  // 普通 for 循环
  for (a <- 1 to 10) {
     println("Value of a: " + a);
  }
  
  // 嵌套 for 循环
  var a = 0;
  var b = 0;
  for( a <- 1 to 3; b <- 1 to 3){
      println( "Value of a: " + a );
      println( "Value of b: " + b );
  }
  
  // for 循环过滤, 多个条件使用 ; 分割
  var a = 0;
  val numList = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  for (a <- numList
       if a != 3; if a < 8; if a!=7) {
      println("Value of a: " + a);
  }
  
  // yield, 结果为 List(1, 2, 4, 5, 6, 7)
  val a = 0
  val numList = List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
  var retVal = for {a <- numList
                    if a != 3; if a < 8
                   } yield a
  println(retVal)
  ```

- 传递可变参数

  ```scala
  
  // 离散的实参, * 可以有任意多个 Int 类型
  def sum(values:Int*)=values.foldLeft(10){_+_}
  println(sum(2,3,5))// 输出20
   
  // 数组做实参(:_*将数组展开成离散值， 类似 ES6 中的[...numbers])
  val numbers = Array(2,3,5)
  println(sum(numbers:_*))// 输出20
  ```



  ```
  
  ```
