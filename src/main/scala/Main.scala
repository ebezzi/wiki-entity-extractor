import com.redis._

import eu.picoweb.crawler.extractor._
import eu.picoweb.crawler.extractor.Extractor._
import akka.actor._

class EntityExtractor {

  val stop = Set("a", "abbastanza", "accidenti", "ad", "adesso", "affinche", "agli", "ahime", "ahimÃ¨", "ai", "al", "alcuna", "alcuni", "alcuno", "all", "alla", "alle", "allo", "altri", "altrimenti", "altro", "altrui", "anche", "ancora", "anni", "anno", "ansa", "assai", "attesa", "avanti", "avendo", "avente", "aver", "avere", "avete", "aveva", "avuta", "avute", "avuti", "avuto", "basta", "bene", "benissimo", "berlusconi", "brava", "bravo", "c", "casa", "caso", "cento", "certa", "certe", "certi", "certo", "che", "chi", "chicchessia", "chiunque", "ci", "ciascuna", "ciascuno", "cima", "cio", "ciÃ²", "cioe", "cioÃ¨", "circa", "citta", "cittÃ ", "codesta", "codesti", "codesto", "cogli", "coi", "col", "colei", "coll", "coloro", "colui", "come", "con", "concernente", "consiglio", "contro", "cortesia", "cos", "cosa", "cosi", "cosÃ¬", "cui", "d", "da", "dagli", "dai", "dal", "dall", "dalla", "dalle", "dallo", "davanti", "degli", "dei", "del", "dell", "della", "delle", "dello", "dentro", "detto", "deve", "di", "dice", "dietro", "dire", "dirimpetto", "dopo", "dove", "dovra", "dovrÃ ", "due", "dunque", "durante", "e", "Ã¨", "ecco", "ed", "egli", "ella", "eppure", "era", "erano", "esse", "essendo", "esser", "essere", "essi", "ex", "fa", "fare", "fatto", "favore", "fin", "finalmente", "finche", "fine", "fino", "forse", "fra", "fuori", "gia", "giÃ ", "giacche", "giorni", "giorno", "gli", "gliela", "gliele", "glieli", "glielo", "gliene", "governo", "grande", "grazie", "gruppo", "ha", "hai", "hanno", "ho", "i", "ieri", "il", "improvviso", "in", "infatti", "insieme", "intanto", "intorno", "invece", "io", "l", "la", "lÃ ", "lavoro", "le", "lei", "li", "lo", "lontano", "loro", "lui", "lungo", "ma", "macche", "magari", "mai", "male", "malgrado", "malissimo", "me", "medesimo", "mediante", "meglio", "meno", "mentre", "mesi", "mezzo", "mi", "mia", "mie", "miei", "mila", "miliardi", "milioni", "ministro", "mio", "moltissimo", "molto", "mondo", "nazionale", "ne", "negli", "nei", "nel", "nell", "nella", "nelle", "nello", "nemmeno", "neppure", "nessuna", "nessuno", "niente", "no", "noi", "non", "nondimeno", "nostra", "nostre", "nostri", "nostro", "nulla", "nuovo", "o", "od", "oggi", "ogni", "ognuna", "ognuno", "oltre", "oppure", "ora", "ore", "osi", "ossia", "paese", "parecchi", "parecchie", "parecchio", "parte", "partendo", "peccato", "peggio", "per", "perche", "perchÃ¨", "percio", "perciÃ²", "perfino", "pero", "perÃ²", "persone", "piedi", "pieno", "piglia", "piu", "piÃ¹", "po", "pochissimo", "poco", "poi", "poiche", "press", "prima", "primo", "proprio", "puo", "puÃ²", "pure", "purtroppo", "qualche", "qualcuna", "qualcuno", "quale", "quali", "qualunque", "quando", "quanta", "quante", "quanti", "quanto", "quantunque", "quasi", "quattro", "quel", "quella", "quelli", "quello", "quest", "questa", "queste", "questi", "questo", "qui", "quindi", "riecco", "salvo", "sara", "sarÃ ", "sarebbe", "scopo", "scorso", "se", "secondo", "seguente", "sei", "sempre", "senza", "si", "sia", "siamo", "siete", "solito", "solo", "sono", "sopra", "sotto", "sta", "staranno", "stata", "state", "stati", "stato", "stesso", "su", "sua", "successivo", "sue", "sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "tale", "talvolta", "tanto", "te", "tempo", "ti", "torino", "tra", "tranne", "tre", "troppo", "tu", "tua", "tue", "tuo", "tuoi", "tutta", "tuttavia", "tutte", "tutti", "tutto", "uguali", "un", "una", "uno", "uomo", "va", "vale", "varia", "varie", "vario", "verso", "vi", "via", "vicino", "visto", "vita", "voi", "volta", "vostra", "vostre", "vostri", "vostro", "a", "adesso", "ai", "al", "alla", "allo", "allora", "altre", "altri", "altro", "anche", "ancora", "avere", "aveva", "avevano", "ben", "buono", "che", "chi", "cinque", "comprare", "con", "consecutivi", "consecutivo", "cosa", "cui", "da", "del", "della", "dello", "dentro", "deve", "devo", "di", "doppio", "due", "e", "ecco", "fare", "fine", "fino", "fra", "gente", "giu", "ha", "hai", "hanno", "ho", "il", "indietro  invece", "io", "la", "lavoro", "le", "lei", "lo", "loro", "lui", "lungo", "ma", "me", "meglio", "molta", "molti", "molto", "nei", "nella", "no", "noi", "nome", "nostro", "nove", "nuovi", "nuovo", "o", "oltre", "ora", "otto", "peggio", "pero", "persone", "piu", "poco", "primo", "promesso", "qua", "quarto", "quasi", "quattro", "quello", "questo", "qui", "quindi", "quinto", "rispetto", "sara", "secondo", "sei", "sembra  sembrava", "senza", "sette", "sia", "siamo", "siete", "solo", "sono", "sopra", "soprattutto", "sotto", "stati", "stato", "stesso", "su", "subito", "sul", "sulla", "tanto", "te", "tempo", "terzo", "tra", "tre", "triplo", "ultimo", "un", "una", "uno", "va", "vai", "voi", "volte", "vostro")

  val redis = new RedisClient("localhost", 6379)

  def getWord(w: String) = redis.lrange(w, 0, -1) match {
    case Some(xs) => xs.flatten
    case None => Seq()
  }
  // 1-grams (words)
  def tokenize(text: String) = 
    text.split("""[\s\p{Punct}]+""") filterNot (stop contains _.toLowerCase)

  // 2-grams
  // def tokenize(text: String) = 
  //   text.split("""[\s\p{Punct}]+""") sliding 3 map { _ mkString " " } toList

  def extract(text: String) = {
    val m = tokenize(text.toLowerCase)

    // m foreach { w => 
    //   val gw = getWord(w)
    //   println(s"$w -> $gw")
    // }

    val words = m map getWord flatten

    words
      .groupBy(identity)
      .mapValues(_.length)
      .filterKeys(k => !(Seq("araldica") contains k))
      .toList
      .sortBy{ case (k,v) => -v }
  }

}

class EntityExtractorWikipedia {

  val stop = Set("a", "abbastanza", "accidenti", "ad", "adesso", "affinche", "agli", "ahime", "ahimÃ¨", "ai", "al", "alcuna", "alcuni", "alcuno", "all", "alla", "alle", "allo", "altri", "altrimenti", "altro", "altrui", "anche", "ancora", "anni", "anno", "ansa", "assai", "attesa", "avanti", "avendo", "avente", "aver", "avere", "avete", "aveva", "avuta", "avute", "avuti", "avuto", "basta", "bene", "benissimo", "berlusconi", "brava", "bravo", "c", "casa", "caso", "cento", "certa", "certe", "certi", "certo", "che", "chi", "chicchessia", "chiunque", "ci", "ciascuna", "ciascuno", "cima", "cio", "ciÃ²", "cioe", "cioÃ¨", "circa", "citta", "cittÃ ", "codesta", "codesti", "codesto", "cogli", "coi", "col", "colei", "coll", "coloro", "colui", "come", "con", "concernente", "consiglio", "contro", "cortesia", "cos", "cosa", "cosi", "cosÃ¬", "cui", "d", "da", "dagli", "dai", "dal", "dall", "dalla", "dalle", "dallo", "davanti", "degli", "dei", "del", "dell", "della", "delle", "dello", "dentro", "detto", "deve", "di", "dice", "dietro", "dire", "dirimpetto", "dopo", "dove", "dovra", "dovrÃ ", "due", "dunque", "durante", "e", "Ã¨", "ecco", "ed", "egli", "ella", "eppure", "era", "erano", "esse", "essendo", "esser", "essere", "essi", "ex", "fa", "fare", "fatto", "favore", "fin", "finalmente", "finche", "fine", "fino", "forse", "fra", "fuori", "gia", "giÃ ", "giacche", "giorni", "giorno", "gli", "gliela", "gliele", "glieli", "glielo", "gliene", "governo", "grande", "grazie", "gruppo", "ha", "hai", "hanno", "ho", "i", "ieri", "il", "improvviso", "in", "infatti", "insieme", "intanto", "intorno", "invece", "io", "l", "la", "lÃ ", "lavoro", "le", "lei", "li", "lo", "lontano", "loro", "lui", "lungo", "ma", "macche", "magari", "mai", "male", "malgrado", "malissimo", "me", "medesimo", "mediante", "meglio", "meno", "mentre", "mesi", "mezzo", "mi", "mia", "mie", "miei", "mila", "miliardi", "milioni", "ministro", "mio", "moltissimo", "molto", "mondo", "nazionale", "ne", "negli", "nei", "nel", "nell", "nella", "nelle", "nello", "nemmeno", "neppure", "nessuna", "nessuno", "niente", "no", "noi", "non", "nondimeno", "nostra", "nostre", "nostri", "nostro", "nulla", "nuovo", "o", "od", "oggi", "ogni", "ognuna", "ognuno", "oltre", "oppure", "ora", "ore", "osi", "ossia", "paese", "parecchi", "parecchie", "parecchio", "parte", "partendo", "peccato", "peggio", "per", "perche", "perchÃ¨", "percio", "perciÃ²", "perfino", "pero", "perÃ²", "persone", "piedi", "pieno", "piglia", "piu", "piÃ¹", "po", "pochissimo", "poco", "poi", "poiche", "press", "prima", "primo", "proprio", "puo", "puÃ²", "pure", "purtroppo", "qualche", "qualcuna", "qualcuno", "quale", "quali", "qualunque", "quando", "quanta", "quante", "quanti", "quanto", "quantunque", "quasi", "quattro", "quel", "quella", "quelli", "quello", "quest", "questa", "queste", "questi", "questo", "qui", "quindi", "riecco", "salvo", "sara", "sarÃ ", "sarebbe", "scopo", "scorso", "se", "secondo", "seguente", "sei", "sempre", "senza", "si", "sia", "siamo", "siete", "solito", "solo", "sono", "sopra", "sotto", "sta", "staranno", "stata", "state", "stati", "stato", "stesso", "su", "sua", "successivo", "sue", "sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "tale", "talvolta", "tanto", "te", "tempo", "ti", "torino", "tra", "tranne", "tre", "troppo", "tu", "tua", "tue", "tuo", "tuoi", "tutta", "tuttavia", "tutte", "tutti", "tutto", "uguali", "un", "una", "uno", "uomo", "va", "vale", "varia", "varie", "vario", "verso", "vi", "via", "vicino", "visto", "vita", "voi", "volta", "vostra", "vostre", "vostri", "vostro", "a", "adesso", "ai", "al", "alla", "allo", "allora", "altre", "altri", "altro", "anche", "ancora", "avere", "aveva", "avevano", "ben", "buono", "che", "chi", "cinque", "comprare", "con", "consecutivi", "consecutivo", "cosa", "cui", "da", "del", "della", "dello", "dentro", "deve", "devo", "di", "doppio", "due", "e", "ecco", "fare", "fine", "fino", "fra", "gente", "giu", "ha", "hai", "hanno", "ho", "il", "indietro  invece", "io", "la", "lavoro", "le", "lei", "lo", "loro", "lui", "lungo", "ma", "me", "meglio", "molta", "molti", "molto", "nei", "nella", "no", "noi", "nome", "nostro", "nove", "nuovi", "nuovo", "o", "oltre", "ora", "otto", "peggio", "pero", "persone", "piu", "poco", "primo", "promesso", "qua", "quarto", "quasi", "quattro", "quello", "questo", "qui", "quindi", "quinto", "rispetto", "sara", "secondo", "sei", "sembra  sembrava", "senza", "sette", "sia", "siamo", "siete", "solo", "sono", "sopra", "soprattutto", "sotto", "stati", "stato", "stesso", "su", "subito", "sul", "sulla", "tanto", "te", "tempo", "terzo", "tra", "tre", "triplo", "ultimo", "un", "una", "uno", "va", "vai", "voi", "volte", "vostro")

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("pages")

  // 1-grams (words)
  // def tokenize(text: String) = 
    // text.split("""[\s\p{Punct}]+""") filterNot (stop contains _.toLowerCase)

  // 2-grams
  def tokenize(text: String) = 
    text.split("""[\s\p{Punct}]+""") sliding 2 map { _ mkString " " } toList

  def getWord(w: String) = coll.findOne(Doc("title" -> w)) match {
    case Some(xs) => true
    case None => false
  }

  // 2-grams
  // def tokenize(text: String) = 
  //   text.split("""[\s\p{Punct}]+""") sliding 3 map { _ mkString " " } toList

  def extract(text: String) = {
    val m = tokenize(text)

    m foreach { w => 
      val gw = getWord(w)
      if (gw) println(s"$w -> $gw")
    }

    Seq()

  }

}

object Main extends App {


  implicit val system = ActorSystem("wiki")
  import system.dispatcher

  val ee = new EntityExtractorWikipedia


  Extractor extract args.head foreach { case Article(title, Some(text), _, _, _, _, _, _) =>
    ee extract text foreach println
  }



}

object AssignEntities extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val ee = new EntityExtractor

  val client = MongoClient("cannobio", 27017)
  val db = client("news")
  val coll = db("articles")

  coll.find(Doc("type" -> "web", "_id" -> Doc("$gte" -> new ObjectId("562567e00000000000000000")))) foreach { article =>

    val id = article.getAs[ObjectId]("_id").get

    val title = article.getAsOrElse[String]("title", "")
    val text = article.getAsOrElse[String]("text", "")

    val categories = ee.extract(s"$title $text")

    // println (s"$title :: ${categories take 3}")

    val tags = categories take 3 map { case (k,v) => Doc("id" -> k, "name" -> s"$k ($v)") }

    // println (tags)

    coll.update(Doc("_id" -> id), Doc("$set" -> Doc("status.tags" -> tags)))
  }

}