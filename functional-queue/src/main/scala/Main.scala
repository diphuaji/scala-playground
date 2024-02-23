import scala.annotation.tailrec
trait Queue[+T] {
    def mirror: Queue[T]

    def head: Option[T]

    def tail: Queue[T]

    def hasHead: Boolean

    def enqueue[U>:T](x: U): Queue[U]
}

object Queue{

    private class QueueImpl[+T] (val leading: List[T], val tailing: List[T]) extends Queue[T] {
        def mirror: QueueImpl[T] = {
            if (leading.isEmpty)
                new QueueImpl(tailing.reverse, Nil)
            else
                this
        }

        def head: Option[T] = mirror.leading.headOption

        def tail: Queue[T] = {
            val q = mirror
            new QueueImpl(q.leading.tail, q.tailing) 
        }

        def hasHead: Boolean = !mirror.leading.isEmpty

        def enqueue[U>:T](x: U): Queue[U] = new QueueImpl(leading, x::tailing)
    }

    def apply[T](initialValues: T*): Queue[T] = new QueueImpl(initialValues.toList, Nil)
}

class Fruit

class NonSweetFruit extends Fruit

class SweetFruit extends Fruit

class FruitWithSeed extends Fruit

class Orange extends FruitWithSeed

class Mandarin extends Orange

class Apple extends SweetFruit

object Main extends App{    
    @tailrec
    def printQueue[T](queue: Queue[T], printHeader: Boolean=false): Unit = {
        if (printHeader) println("-----start-----")
        if (queue.hasHead){
            println(queue.head)
            printQueue(queue.tail)
        }
    }

    val queue = Queue(new Orange())

    val queu2 = queue.enqueue(new FruitWithSeed())

    val queue3 = queue.enqueue(new Apple())

    val queue4 = queue.enqueue(new Mandarin())

    printQueue(queue, true)
    printQueue(queue3, true)
    printQueue(queue4, true)

    println((new Orange()).asInstanceOf[AnyRef])
}