gradle的优势：
一个像 Ant 一样的非常灵活的通用构建工具
一种可切换的, 像 maven 一样的基于合约构建的框架
支持强大的多工程构建
支持强大的依赖管理(基于 ApacheIvy )
支持已有的 maven 和 ivy 仓库
支持传递性依赖管理, 而不需要远程仓库或者 pom.xml 或者 ivy 配置文件
优先支持 Ant 式的任务和构建
基于 groovy 的构建脚本
有丰富的领域模型来描述你的构建


Gradle里的任何东西都是基于这两个基础概念:
projects(项目)
tasks(任务)

// 定义一个task，第二种方式和第一种方式一样，但更简洁
task hello {
    doLast {
        println 'Hello world!'
    }
}

task hello << {
    println 'Hello world!'
}

// 声明依赖，可先依赖后声明，依赖的task先执行
task intro(dependsOn: hello) << {
    println "I'm Gradle"
}

task hello << {
    println 'Hello world!'
}

// 通过task的api来操作任务，doLast和doList可以设置多次，按顺序执行
task hello1 << {
    println "hello world1"
}

task hello2 << {
    println "hello world2"
}

hello1.dependsOn hello2

hello1.doFirst  {
   println "hello0"
}

// task添加自定义属性
task hello1  {
    ext.myProperty="xxq"
}

task hello2 << {
    println hello1.myProperty
}

// 定义默认任务
defaultTasks 'clean', 'run'
执行gradle当没有指定任务，则默认任务将执行

// 根据选择的任务产生不同的输出
task distribution << {
    println "We build the zip with version=$version"
}

task release(dependsOn: 'distribution') << {
    println 'We release now'
}

gradle.taskGraph.whenReady {taskGraph ->
    if (taskGraph.hasTask(release)) {
        version = '1.0'
    } else {
        version = '1.0-SNAPSHOT'
    }
}

//使用插件，插件其实就是添加了一些默认任务,plugin:java作为map的一个键值对
apply plugin: 'java'


// 列出项目有哪些任务(build只是其中的一个任务，打出具体的包根据使用的插件而定)
gradle tasks

// 外部的依赖
1.maven
repositories {
    mavenCentral()
}
2.添加依赖
dependencies {
    compile group: 'commons-collections', name: 'commons-collections', version: '3.2'
    testCompile group: 'junit', name: 'junit', version: '4.+'
}

// java插件已经有足够多的property,可以使用下面的命令显示属性
gradle properties

// 可以重写任务在任务里边加入代码，不用加task，用的比较多
task distribution << {
    println "We build the zip with version=$version"
    println sourceCompatibility 
}

distribution{
    println "overload"
}

// 发布 JAR 文件，默认在项目的build\libs目录下，可以修改到其他目录：例如：repos目录下
uploadArchives {
    repositories {
       flatDir {
           dirs 'repos'
       }
    }
    ivy {
	   credentials {
	        username "username"
		password "pw"
	    }
	    url "http://repo.mycompany.com"
    }
    mavenDeployer {
            repository(url: "file://localhost/tmp/myRepo/")
    }
}

// 创建eclipse项目
apply plugin: 'eclipse'

// 定义一个多项目构建，多项目构建必须包含settings.gradle，则从更目录打包的时候会把根目录下边的项目也一起打包
include "shared", "api", "services:webservice", "services:shared"

// 定义一个通用的配置在根目录，下面的子项目都会按照这个通用配置打包，子项目可以修改或重写
subprojects {
    apply plugin: 'java'
    apply plugin: 'eclipse-wtp'

    repositories {
       mavenCentral()
    }
    
    dependencies {
        testCompile 'junit:junit:4.11'
    }
    
    version = '1.0'
    
    jar {
        manifest.attributes provider: 'gradle'
    }
}

// 项目之间的依赖,则当前项目会在shared打包完成之后再进行打包
dependencies {
    compile project(':shared')
}

// 外部依赖
dependencies {
    compile group: 'org.hibernate', name: 'hibernate-core', version: '3.6.7.Final'
}
// 简写"group:name:version"
dependencies {
    compile 'org.hibernate:hibernate-core:3.6.7.Final'
}
// 配置仓库，可以配置本地的和远程的
repositories {
    mavenCentral()
    maven {
        url "http://repo.mycompany.com/maven2"
    }
    ivy {
        url "http://repo.mycompany.com/repo"
    }
    ivy {
        // URL can refer to a local directory
        url "../local-repo"
    }
}

Gradle 提供了两个插件用来支持网页应用: War插件 和 Jetty插件. 
War插件是在 Java 插件的基础上扩充的用来构建 WAR 文件. 
Jetty插件是在 War 插件的基础上扩充的, 允许用户将网页应用发布到一个介入的 Jetty 容器里.

apply plugin: 'war' // 引入war插件
apply plugin: 'jetty' // 引入jtty文件，gradle jettyRun，会自动把war包部署到jetty容器



---------------------------命令行---------------------------
// 多任务调度，这些任务只会被调用一次, 无论它们是否被包含在脚本中
gradle compile test

// 排除某些任务 -x
gradle dist -x test

// 调用任务失败后继续执行，--continue，依赖的出错，则调用的任务也不执行
gradle dist --continue

// 选择执行构建,选择具体的目录进行构建，此时settings.gradle将失效
-b 参数用以指定脚本具体所在位置, 格式为 dirpwd/build.gradle.
-p 参数用以指定脚本目录即可.

// 查看子项目
gradle projects

// 在项目中给项目添加描述信息
description = 'The shared API for the application'

// 获取任务的详细信息，包含了任务的路径、类型以及描述信息等.
gradle help --task someTask

// 列出项目的所有依赖，以树形结构显示出来
gradle dependencies

// 查看某一个项目的依赖
gradle lms:dependencies

// 过滤依赖，指定任务
gradle -q api:dependencies --configuration testCompile

// 过滤依赖，指定具体的包名，指定任务
gradle -q webapp:dependencyInsight --dependency groovy --configuration compile

// 获取属性列表
gradle -q api:properties

// 构建日志
--profile 参数可以收集一些构建期间的信息并保存到 build/reports/profile 目录下

----------------------------------图形界面------------------------------------
 // 启动图形界面
gradle --gui 命令行处于封锁状态
gradle --gui& 后台执行



---------------------------------编写构建脚本----------------------------------
// 对于构建脚本中每个项目，Gradle 都创建了一个 Project 类型的project对象
// 构建脚本的方法和属性都委托给该对象
println name
println project.name
// 所以以上的输出是一致的

Project 对象提供了一些标准的属性，您可以在构建脚本中很方便的使用他们. 下面列出了常用的属性:

Name	Type	Default Value
project	Project	Project 实例对象
name	String	项目目录的名称
path	String	项目的绝对路径
description	String	项目描述
projectDir	File	包含构建脚本的目录
build	File	projectDir/build
group	Object	未具体说明
version	Object	未具体说明
ant	AntBuilder	Ant实例对象

// 变量声明：
1.局部属性，不是project对象的属性
def var = 1
2.扩展属性ext，是project对象的属性;对其子项目也是可见的;就是说子项目会继承父项目
ext{
   var=2               // 可定义多个
}
或
ext.var=2


// 指定类型的任务
task myCopy(type: Copy)

myCopy {
   from 'resource'
   into 'target'
   include('**/*.txt', '**/*.xml', '**/*.properties')
}

// 从不同的项目添加依赖
project('projectA') {
    task taskX(dependsOn: ':projectB:taskY') << {
        println 'taskX'
    }
}

project('projectB') {
    task taskY << {
        println 'taskY'
    }
}

// 替换任务
task copy(type: Copy)

task copy(overwrite: true) << {
    println('I am the new one.')
}

//  任务的顺序
taskY.mustRunAfter taskX

// 跳过task
task hello << {
    println 'hello world'
}
hello.onlyIf { !project.hasProperty('skipHello') }

------------------------文件操作--------------------------------------
// 使用一个相对路径获取文件对象
File configFile = file('src/config.xml')

// 使用一个绝对路径
configFile = file(configFile.absolutePath)

// 使用一个项目路径的文件对象 
configFile = file(new File('src/config.xml'))

-------------------------远程部署--------------------------------------------
buildscript {
	repositories {
		maven {
			url "https://plugins.gradle.org/m2/"
		}
		dependencies {
			classpath "org.hidetake:gradle-ssh-plugin:2.9.0"
		}
	}
}

apply plugin: "org.hidetake.ssh"

remotes {
       server {
	        host = configuration['server.exam_edge.ip']
	        user = configuration['server.exam_edge.user']
	        password = configuration['server.exam_edge.password']
	        // identity = file("${System.properties['user.home']}/.ssh/id_rsa")
       }
}

ssh.settings {
    knownHosts = allowAnyHosts
}

// 重启tomcat服务器
task restart() {
    def tomcatDir = configuration['tomcat_dir.exam_edge']
    doLast {
        ssh.run {
            session(remotes.server) {
                execute """
                    rm -f ${tomcatDir}/catalina.pid;
                    ${tomcatDir}/bin/shutdown.sh;
                    ${tomcatDir}/bin/startup.sh;
                """
            }
         }
     }
}

// 拷贝文件到服务器并解压
task upload(dependsOn: build) {
    def targetDir = configuration['doc_base.exam_edge']
    doLast {
        ssh.run {
            session(remotes.server) {
                put from: "${buildDir}/libs/${war.archiveName}", into: "${targetDir}"
                execute """
                    source /root/.bash_profile;
                    unzip -o ${targetDir}/${war.archiveName} -d ${targetDir} > /dev/null;
                    rm -rf ${targetDir}/${war.archiveName};
                """
            }
        }
    }
}

-----------------------------------在 Gradle中使用Ant-----------------------------------------------



