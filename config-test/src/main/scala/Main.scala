 import com.typesafe.config.ConfigFactory


object Main extends App {
    val conf = ConfigFactory.load().getConfig("com.test");
    println(conf.root.render())
}