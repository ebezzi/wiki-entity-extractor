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

object Write extends App {

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

	val file = "/opt/scala-wiki/data/wiktionary.xml"

	val splitter = XmlStreamElementProcessor.collectElements { _.last == "page" }

	import XmlStreamElementProcessor.IteratorCreator._
  val pages = splitter.processInputStream(new FileInputStream(file))

  val patata = Source.fromFile("/opt/scala-wiki/patata.txt").mkString

	println (parseText(patata))


	val parser = new WikitextParser(new SimpleParserConfig)

	val redis = new RedisClient("localhost", 6379)

	pages foreach { page =>

		val title = (page \\ "title").text
		// println(title)

		val text = (page \\ "text").text
			// println (text)

		Try(parseText(text)) match {
			case Success(Word(plural, cats)) if cats.nonEmpty => 
				cats foreach { e => redis.lpush(title, e) }
				plural.foreach { pl =>
					cats foreach { e => redis.lpush(pl, e) }
				}

			case otherwise => // nothing
		}


	}

}