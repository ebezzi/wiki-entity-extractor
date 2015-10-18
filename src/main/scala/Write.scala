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

object Write {

	def groupRegex(lines: Seq[String], pat: String): List[(String, Seq[String])] = {
		if (lines.isEmpty) return Nil
		val header = lines.head
		val (group, rest) = lines.tail span (x => !(x matches pat) )
		(header -> group) +: groupRegex(rest, pat)		
	}

	def parseText(xs: String) = {

		val chapters = groupRegex(xs.lines.toSeq.tail, "==.*==")
		val it = chapters.head._2.mkString("\n")

		val pat = """\{\{[Tt]erm\|(\w*)\|it\}\}""".r
		pat.findAllMatchIn(xs) map {_ group 1} toList

		// val pars = groupRegex(it, """\{\{.*\}\}""")
		// println(pars.head._2)


		// val it = xs.split("==.*==")(1).trim
		// val (tpe :: meta :: defs) = it.lines.toList 
		// Word(
		// 	tpe = tpe,
		// 	meta = meta,
		// 	defs = defs takeWhile ( _.startsWith("#") )
		// )
	}

	val file = "/opt/scala-wiki/wiktionary.xml"

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

		if (title contains "evasione") println(text)

		// Try(parseText(text)) match {
		// 	case Success(c) if c.nonEmpty => c foreach { e => redis.lpush(title, e) }
		// 	case otherwise => // nothing
		// }


	}

	// import scala.pickling.Defaults._
	// import scala.pickling.binary._

	// val m = pages collect Function.unlift({ page =>
	// 	val title = (page \\ "title").text
	// 	// println(title)

	// 	val text = (page \\ "text").text
	// 		// println (text)

	// 	Try(parseText(text)) match {
	// 		case Success(c) if c.nonEmpty => Some(title -> c)
	// 		case otherwise => None
	// 	}


	// }) toList

	// println (m.getClass)

	// import java.nio.file._
	// Files.write(Paths.get("words.data"), m.pickle.value)



	// pages foreach { page =>
	// 	val title = (page \\ "title").text
	// 	// println(title)
	// 	if (title == "ricetta"){

	// 		val text = (page \\ "text").text
	// 		println (text)

	// 		println (parseText(text))

	// 	}
	// }

	
}