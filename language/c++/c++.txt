1.vector<int> intvariable;
  intvariable.assign(10,16);
  //或者
  //vector<int> intvariable(10,16);
  声明一个vector，10个元素且每一个元素都是16
  intvariable.size();
2.vector的长度是可变的arrays的长度是固定的
3.vector<float> vector1(array1, array1 + sizeof(array1) / sizeof(array1[0]));//从array中复制元素到vector
4.intvariable.push_back(25);//在vector最后加一个元素
5.vector < vector <int> > twodvector;// 定义二维vector,注意之间的空格
6.cin >> integertwo;// 从控制台输入值赋给integertwo


它的作用主要是利用它在程序的编译阶段对调用函数的合法性进行全面检查。


之前一直没有弄明白的地方：声明语句中进行初始化指针，表示的是初始化的指针的地址，而不是值。

指针加1表示指向下一个元素的地址

不传参数创建对象，不用圆括号，用圆括号表示声明的一个方法;


void show() const;// 可以声明类的成员函数不能修改调用对象
作为一种良好的编程风格，在声明一个成员函数时，若该成员函数并不对数据成员进行修改操作，应尽可能将该成员函数声明为const 成员函数。

构造函数可以设置默认值（聲明）则传递的时候可以选择性传值。。


引用合解引用 & *
int num = 2;
int *p = &num;//声明p为指针类型，p为地址
count << *p;*p为p地址所在的值
count << p;为地址

不要返回指向局部变量或临时变量的引用，函数执行完之后所指向的对象将消失，返回值可以作为引用参数传入再返回

c++运算符重载 例如：operator+等

友元

函数调用的时候指针指向值不变，但是指针地址变化了，函数的参数是复制过去的

创建副本的3种情况：
  1，以值的方式给函数传参；（被调用函数对值的修改不会影响原值，包括对象，所以需要传引用）
  2，类型转换；
  3，函数需要返回一个对象时；


注意指针和引用的区别：
  指针存取的是原对象的地址
  引用只是对原对象的取的一个别名

浅复制：当有成员变量为应用变量时，特别注意可能成员变量没有设置指针的地址，默认为浅复制
深复制：用new给成员变量设置地址

this在成员函数开始调用前构造

&：在等号左边的表示引用，在等号右边的表示地址
*：在等号左边的表示指针，在等号右边的表示解引用（值）
可以通过指针来访问对象的方法->
或则解引用，常规访问对象的方法


函数返回对象会重新创建一个临时对象，并拷贝该临时对象为外部变量返回，如果返回引用不会

类的内部成员使用了new则必须写析构函数释放。
只要你写了析构函数，就必须要写 赋值构造函数 和 赋值运算符，这就是著名的 三法则

通常声明基类的方法为虚构函数，virtual，这样在通过指针或引用调用得时候默认调用得都是基类得方法，而不是指针或引用类型获类得方法
通常基类得方法都声明为virtual,且包含析构函数

通过作用域可以调用基类得方法

为什么要给基类添加虚析构函数：当指针指向类的基类得时候，使用delete语句将调用基类的析构函数，而如果是virtual，则将调用实际对象得析构函数

抽象基类必须包含一个纯虚构的接口（virtual,=0结尾），不能实例化，派生的类必须实现其虚拟的接口，在实际中为了强调一个类是抽象类，可将该类的构造函数说明为保护的访问控制权限。

vector(相比数组可以动态分配元素的个数):
vector<float>::iterator t;c++中的迭代器，可用该迭代器进行遍历向量，*t可获取指定位置的元素（获取指定迭代器地址的值）
c.size()     元素的个数
c.resize()   重新分配元素的个数，大于当前vector元素个数，在末尾添加默认值，小于vector，去掉末尾值。
c.capacity() vector的当前容量
c.reserve()  vector重新分配容量
c.clear()  移除容器中所有数据。
c.empty()  判断容器是否为空。
c.assign() 重新初始化向量，之前的数据不存在了
// c++中获取指定位置的元素用迭代器，或则使用数组的方式
c.erase(pos)   删除pos位置的数据
c.erase(beg,end) 删除[beg,end)区间的数据
c.front()      传回第一个数据。
c.insert(pos,elem)  在pos位置插入一个elem拷贝
c.pop_back()   删除最后一个数据。
c.push_back(elem) 在尾部加入一个数据，如果容量不够会自动扩容。
c.begin()      返回指向容器第一个元素的迭代器
c.end()        返回指向容器最后一个元素的迭代器
c.at()         返回指定位置的元素,接受参数为无符号整数或浮点数
// 可以把数组的内容复制给，给出开始地址和结束地址
int arr[] = { 1, 2, 3, 4 };
vector<float> new_grid(arr, arr + 4);
vector<float> new_grid(&arr[1], &arr[2]);
// 把另外一个向量的指定区间的值复制给另外一个向量，使用迭代器
vector<float> a(10, 9);
vector<float> b(a.begin() + 8, a.end());
// 交换两个向量所有元素的位置
vector<float> a(10, 9);
vector<float> b(10, 1);
a.swap(b);


// 文件读写
ifstream file("1.txt");
string line;
// 逐词读取
while (file >> line) {
    cout << line << endl;
}
file.close();
// 按行读取
ifstream file("1.txt");
string line;
while (getline(file, line)) {
	cout << line << endl;
}
file.close();
// 按行读取，读取的数据放入数组
ifstream file("1.txt");
string line;
while (getline(file, line)) {
	cout << line << endl;
}
file.close();




