- “对象“的属性方法简写

  ```javascript
  const lukeSkywalker = 'Luke Skywalker';

  const obj = {
    lukeSkywalker,
  };

  const atom = {
    value: 1,
    addValue(value) {
      return atom.value + value;
    },
  };
  ```

- 数组辅助方法，这样就不必写冗余的for循环

  ```javascript
  var colors = ['red', 'green', 'blue']

  // forEach，遍历每个元素，不修改原值
  colors.forEach(x => console.log(x));

  // map，遍历每个元素，修改原值，等号左边必须有变量接收
  colors = colors.map(x => {return x.toUpperCase()});

  // filter，过滤掉不满足条件的值
  colors = colors.filter(x => {return x !=="blue" });

  let people = [
  	  {name: 'Jack', age: 50},
  	  {name: 'Michael', age: 9}, 
  	  {name: 'John', age: 40}, 
  	  {name: 'Ann', age: 19}, 
  	  {name: 'Elisabeth', age: 16}
  	]

  // find，找到通过传入的函数测试的第一个元素
  person = people.find(x => {return x.age>10 && x.age < 50 });

  // every，判断数组中是否每个元素都满足条件
  person = people.every(x => {return x.age>10 && x.age < 50 });

  // some，判断数组中是否至少有一个满足条件，和every相对
  person = people.some(x => {return x.age>10 && x.age < 50 });

  // reduce，数组中的每个值（从左到右）开始缩减，最终为一个值。 reduce第一个参数为函数，第二个值为
  // 结果初始值
  let array = [1, 2, 3, 4]
  function sum(acc, value) {
    return acc + value
  }
  val = array.reduce(sum, 0);
  ```

- 使用拓展运算符 ...复制数组。

  ```javascript
  const items = [1, 2, 3];
  const itemsCopy = [...items];
  ```

- 使用解构存取和使用多属性对象。

  ```javascript
  function getFullName(obj) {
    const { firstName, lastName } = obj;
    return `${firstName} ${lastName}`;
  }
  // 或
  function getFullName({ firstName, lastName }) {
    return `${firstName} ${lastName}`;
  }
  ```

- 使用模板字符串代替字符串连接。

  ```python
  function sayHi(name) {
    return `How are you, ${name}?`;
  }
  ```

- 给函数设置默认值，不要使用一个变化的函数参数。

  ```javascript
  function handleThings(opts = {}) {
  }
  ```

- 箭头函数

  ```javascript
  [1, 2, 3].map((x) => {
    return x;
  });
  // 如果一个函数适合用一行写出并且只有一个参数，那就把花括号、圆括号和 return 都省略掉。
  [1, 2, 3].map(x => x + x);
  ```

- 优先使用 `===` 和 `!==` 而不是 `==` 和 `!=`.

- if条件判断时使用简写

  ```javascript
  if (name) {
  }

  if (collection.length) {
  }
  ```

- 在块末和新语句前插入空行。（为了是代码规范）

  ```python
  const obj = {
    foo() {
    },

    bar() {
    },
  };

  return obj;
  ```

- 逗号不要放到行首。（为了使代码规范）

  ```python 
  const hero = {
    firstName: 'Ada',
    lastName: 'Lovelace',
    birthYear: 1815,
    superPower: 'computers',
  };
  ```

- 增加结尾的逗号: **需要**。

  > 这会让 git diffs 更干净。另外，像 babel 这样的转译器会移除结尾多余的逗号，也就是说你不必担心老旧浏览器的[尾逗号问题](https://github.com/yuche/javascript/blob/master/es5/README.md#commas)。

  ```javascript
  const hero = {
    firstName: 'Dana',
    lastName: 'Scully',
  };
  ```

- 类型转换

  ```javascript
  // 字符串
  const totalScore = String(this.reviewScore);
  // 数字 
  const val = Number(inputValue);
  const val = parseInt(inputValue, 10);
  // 布尔
  const hasAge = Boolean(age);
  const hasAge = !!age;
  ```

-  使用 `$` 作为存储 jQuery 对象的变量名前缀。

  ```
  const $sidebar = $('.sidebar');
  ```

- 缓存 jQuery 查询。

  ```javascript
  function setSidebar() {
    const $sidebar = $('.sidebar');
    $sidebar.hide();

    $sidebar.css({
      'background-color': 'pink'
    });
  }
  ```

- Promiss，你将会得到延期或长期运行任务的未来结果。使代码异步执行。

  ```javascript
  function asyncFunc() {
      return new Promise((resolve, reject) => {
          setTimeout(() => {
            const result = Math.random();
            result > 0.5 ? resolve(result) : reject('Oppps....I cannot calculate')
          }, 1)
      });
  }

  for (let i=0; i<10; i++) {
      asyncFunc()
          .then(result => console.log('Result is: ' + result))
          .catch(result => console.log('Error: ' + result))
  }
  ```

  ​