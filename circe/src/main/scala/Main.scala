import io.circe.generic.auto._, io.circe.syntax._
import io.circe.parser.decode
import io.circe.Json
import io.circe.parser._
import io.circe.Decoder
import io.circe.HCursor

case class ContentStackResponse(data: Data)

case class Data(all_recommended_product: AllRecommendedProduct)

case class AllRecommendedProduct(items: Seq[Item])

case class Item(bands: Seq[BandWrapper])

case class BandWrapper(band: Band)

case class Band(description: String, label: String, label_summary: String, risk_score_floor: Int, risk_score_ceiling: Int)

object Main extends App {
  def testBandScores = {
    val jsonValue =
      """
        |{
        |    "data": {
        |        "all_recommended_product": {
        |            "items": [
        |                {
        |                    "bands": [
        |                        {
        |                            "band": {
        |                                "description": "<h5>You prefer less uncertainty. Lower risk bets can help&nbsp;ensure smooth sailing.</h5>",
        |                                "label": "Conservative",
        |                                "label_summary": "",
        |                                "risk_score_floor": 0,
        |                                "risk_score_ceiling": 20,
        |                                "target_allocations": "0 / 0 / 100"
        |                            }
        |                        },
        |                        {
        |                            "band": {
        |                                "description": "<h5>You tread cautiously, but accept some fluctuation in returns. Slow and steady may win the race.</h5>",
        |                                "label": "Moderately Conservative",
        |                                "label_summary": "",
        |                                "risk_score_floor": 21,
        |                                "risk_score_ceiling": 40,
        |                                "target_allocations": "25 / 75 / 0"
        |                            }
        |                        },
        |                        {
        |                            "band": {
        |                                "description": "<h5>You prefer a balanced approach and accept a modest degree of fluctuation in returns. An even keel keeps you moving forward.</h5>",
        |                                "label": "Moderate",
        |                                "label_summary": "",
        |                                "risk_score_floor": 41,
        |                                "risk_score_ceiling": 60,
        |                                "target_allocations": "50 / 50 / 0"
        |                            }
        |                        },
        |                        {
        |                            "band": {
        |                                "description": "<h5>You are more comfortable with uncertainty and fluctuation in returns. Higher risk may mean high rewards.</h5>",
        |                                "label": "Moderately Aggressive",
        |                                "label_summary": "",
        |                                "risk_score_floor": 61,
        |                                "risk_score_ceiling": 80,
        |                                "target_allocations": "75 / 25 / 0"
        |                            }
        |                        },
        |                        {
        |                            "band": {
        |                                "description": "<h5>You are comfortable knowing that returns can fluctuate significantly. When markets fall, you see opportunity!</h5>",
        |                                "label": "Aggressive",
        |                                "label_summary": "",
        |                                "risk_score_floor": 81,
        |                                "risk_score_ceiling": 100,
        |                                "target_allocations": "90 / 10 / 0",
        |                                "bb": "haha"
        |                            }
        |                        }
        |                    ]
        |                }
        |            ]
        |        }
        |    }
        |}
        |
        |""".stripMargin

    val thing = decode[ContentStackResponse](jsonValue).map{ response =>
      println(response.data.all_recommended_product.items)
    }
    println(thing)
  }

  class K
  class V

  def customMapParse = {
    val json: String = """
      {
        "id": "c730433b-082c-4984-9d66-855c243266f0",
        "name": "Foo",
        "counts": [1, 2, 3],
        "values": {
          "bar": true,
          "baz": 100.001,
          "qux": ["a", "b"]
        }
      }
    """
    val doc = parse(json)
    // implicit val fooKeyDecoder: ValueDecoder[Foo] = new KeyDecoder[Foo] {
    //   override def apply(key: String): Option[Foo] = Some(Foo(key))
    // }
    implicit val objDecoder: Decoder[String] = {
      case x: HCursor => Right(x.value.asString.getOrElse(x.value.toString()))
    }


    doc.flatMap(_.as[Map[String, String]]).getOrElse(Map.empty[String, String])
  }

  def parseNullJson = {
    Json.Null.as[Json]
  }

  testBandScores

  // println(customMapParse)

  // println(s"parse null: ${parseNullJson}, class: ${parseNullJson.getClass()}")
}
