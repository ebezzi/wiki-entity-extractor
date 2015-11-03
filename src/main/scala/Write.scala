import java.io._
import javax.xml.parsers._
import scala.collection.immutable
import eu.cdevreeze.yaidom._

import org.sweble.wikitext.parser._
import org.sweble.wikitext.parser.utils._

import scala.util._

import scala.xml.pull._
import scala.io.Source

import com.redis._

import com.scalawilliam.xs4s.elementprocessor.XmlStreamElementProcessor

object WikiParser {

  def enumerate(file: String) = {
    val splitter = XmlStreamElementProcessor.collectElements { _.last == "page" }

    import XmlStreamElementProcessor.IteratorCreator._
    splitter.processInputStream(new FileInputStream(file))

  }

}

object WriteWiktionary extends App {

  def groupRegex(lines: Seq[String], pat: String): List[(String, Seq[String])] = {
    if (lines.isEmpty) return Nil
    val header = lines.head
    val (group, rest) = lines.tail span (x => !(x matches pat) )
    (header -> group) +: groupRegex(rest, pat)
  }

  case class Word(plural: Option[String], cats: Seq[String])

  def parseText(xs: String) = {

    // val chapters = groupRegex(xs.lines.toSeq.tail, "==.*==")
    // val it = chapters.head._2.mkString("\n")

    val cats = """\{\{[Tt]erm\|(\w*)\|it\}\}""".r.findAllMatchIn(xs) map {_ group 1} toList

    val plural = """\{\{Linkp\|(\w*)\}\}""".r.findFirstMatchIn(xs) map {_ group 1}

    Word(plural, cats)

  }

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("words")

  def insert(word: String, categories: Seq[String]) = 
    coll.update(Doc("word" -> word), Doc("$addToSet" -> Doc("categories" -> categories)), true, false)

  WikiParser.enumerate("./data/wiktionary.xml") foreach { page =>

    val title = (page \\ "title").text
    val text = (page \\ "text").text

    Try(parseText(text)) match {
      case Success(Word(plural, cats)) if cats.nonEmpty => 
        insert(title, cats)
        plural.foreach { pl => insert(pl, cats) }

      case otherwise => // nothing
    }


  }

}


object WriteWikipedia extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("pages")

  coll.ensureIndex(Doc("title" -> 1), "title", true)

  WikiParser.enumerate("./data/wikipedia.xml") foreach { page => 

    val title = (page \\ "title").text
    // println(title)

    val text = (page \\ "text").text
      // println (text)

    if (!(title contains ":")){
      coll.insert(Doc("title" -> title, "text" -> text))
    }


  }

}

object WriteRedirects extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("redirects")

  coll.ensureIndex(Doc("from" -> 1), "from", true)
  coll.ensureIndex(Doc("fromLower" -> 1), "fromLower", false)

  WikiParser.enumerate("./data/wikipedia.xml") foreach { page => 

    val title = (page \\ "title").text
    // println(title)

    val text = (page \\ "text").text
      // println (text)

    val redirect = (page \\ "redirect").headOption flatMap { x => x attribute "title" } map { _.text }

    redirect foreach { to =>
      coll.insert(Doc("from" -> title, "fromLower" -> title.toLowerCase, "to" -> to))
    }


    // if (!(title contains ":")){
    //   coll.insert(Doc("title" -> title, "text" -> text))
    // }


  }

}

object WriteDisambiguation extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("redirects")

  // coll.ensureIndex(Doc("from" -> 1), "from", true)
  // coll.ensureIndex(Doc("fromLower" -> 1), "fromLower", false)

  WikiParser.enumerate("./data/wikipedia.xml") foreach { page => 

    val title = (page \\ "title").text
    // println(title)

    val text = (page \\ "text").text

    if (text startsWith "{{disambigua}}")
      println (page)


    // if (!(title contains ":")){
    //   coll.insert(Doc("title" -> title, "text" -> text))
    // }


  }

}

object WritePortals extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("entities")

  // coll.ensureIndex(Doc("from" -> 1), "from", true)
  // coll.ensureIndex(Doc("fromLower" -> 1), "fromLower", false)

  WikiParser.enumerate("./data/wikipedia.xml") foreach { page => 

    val title = (page \\ "title").text
    // println(title)

    val text = (page \\ "text").text

    val portals = """\{\{Portale\|([|\w]*)\}\}""".r.findAllMatchIn(text) map {_ group 1} toList match {
      case h :: t => h split '|' toList
      case other => Nil
    }

    if (portals.nonEmpty)
      coll.update(Doc("entity" -> title), Doc("$set" -> Doc("portals" -> portals)))


  }

}

// object ExtractCategoriesWiki extends App {

//   import com.mongodb.casbah.Imports._
//   import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
//   val client = MongoClient("localhost", 27017)
//   val db = client("wikipedia")
//   val dest = db("entities")

//   coll.ensureIndex(Doc("entity" -> 1), "entity", true)

//   WikiParser.enumerate("./data/wikipedia.xml") foreach { page => 

//     val title = (page \\ "title").text
//     val text = (page \\ "text").text

//     implicit class RichString(s: String) {
//       import scala.util.matching.Regex
//       def findMatches(pat: Regex) = pat.findAllMatchIn(s) map {_ group 1} toList
//     }

//     val cats = text findMatches """\[\[Categoria:(.*)\]\]""".r

//     coll.insert(Doc("entity" -> title, "categories" -> cats))

//   }

// }

object ExtractCategoriesWiki extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val source = db("pages")
  val dest = db("entities")

  dest.ensureIndex(Doc("entity" -> 1), "entity", true)

  source.find() foreach { page =>

    // val id = page.getAs[ObjectId]("_id").get

    val title = page.getAs[String]("title").get
    val text = page.getAs[String]("text").get

    implicit class RichString(s: String) {
      import scala.util.matching.Regex
      def findMatches(pat: Regex) = pat.findAllMatchIn(s) map {_ group 1} toList
    }

    val cats = text findMatches """\[\[Categoria:(.*)\]\]""".r

    dest.insert(Doc("entity" -> title, "categories" -> cats))

  }

}

object Bah extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}
  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val source = db("categories")
  val dest = db("entities")

  dest.ensureIndex(Doc("entity" -> 1), "entity", true)

  WikiParser.enumerate("./data/wikipedia.xml") foreach { page => 

    val title = (page \\ "title").text
    val text = (page \\ "text").text
    val id = (page \ "id").text.toInt

    // println (id)

    val cats = source.find(Doc("id" -> id)) collect Function.unlift( d => d.getAs[String]("category") ) toList

    if (cats.nonEmpty)
      dest.insert(Doc("id" -> id, "entity" -> title, "categories" -> cats))
    


    // if (title contains "David Gilmour"){
    //   println (page)
    // }


  }

}

object Normalize extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val dest = db("entities")

  dest.ensureIndex(Doc("entity" -> 1), "entity", true)
  dest.ensureIndex(Doc("entityLower" -> 1), "entityLower")

  dest.find() foreach { page =>

    val entity = page.getAs[String]("entity").get
    dest.update(Doc("entity" -> entity), Doc("$set" -> Doc("entityLower" -> entity.toLowerCase)))

  }


}