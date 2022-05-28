import slick.jdbc.MySQLProfile.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}

object Main extends App {
  class People(tag: Tag) extends Table[(Int, String)](tag, "people") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column

    def name = column[String]("name")

    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name)
  }

  val people = TableQuery[People]

  class Hobby(tag: Tag) extends Table[(Int, Int, String)](tag, "hobby") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc) // This is the primary key column

    def peopleId = column[Int]("people_id")

    def name = column[String]("name")

    // Every table needs a * projection with the same type as the table's type parameter
    def people = foreignKey("PEOPLE-FK", peopleId, TableQuery[People])(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    def * = (id, peopleId, name)
  }

  val db = Database.forConfig("mysql_config");

  val hobby = TableQuery[Hobby]

  //  db.run(hobby.schema.create)

  val q = for {
    inserted <- people.map(p => (p.name)) += ("Tianpeng Xia")
    peopleResult <- people.filter(_.name === "James Brown").result
    insertCount <- {
      if (inserted != 0) throw new Exception("Break here!")
      val person = peopleResult.head
      hobby.map(h => (h.peopleId, h.name)) += (person._1, "smash bros")
    }
  }
  yield insertCount


  db.run(q.transactionally).onComplete {
    case Success(result) => println(s"${result} inserted!")
    case Failure(t) => println(s"Something wrong: ${t.getLocalizedMessage}")
  }
}