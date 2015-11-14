import com.redis._

import eu.picoweb.crawler.extractor._
import eu.picoweb.crawler.extractor.Extractor._
import akka.actor._

import eu.picoweb.commons.ScalaSoup

import scalaz._
import Scalaz._

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

    val words = m flatMap getWord 

    words
      .groupBy(identity)
      .mapValues(_.length)
      .filterKeys(k => !(Seq("araldica") contains k))
      .toList
      .sortBy{ case (k,v) => -v }
  }

}

/*
* Ideas: Accent folding
*/
class EntityExtractorWikipedia {

  val stop = Set("a", "abbastanza", "accidenti", "ad", "adesso", "affinche", "agli", "ahime", "ahimÃ¨", "ai", "al", "alcuna", "alcuni", "alcuno", "all", "alla", "alle", "allo", "altri", "altrimenti", "altro", "altrui", "anche", "ancora", "anni", "anno", "ansa", "assai", "attesa", "avanti", "avendo", "avente", "aver", "avere", "avete", "aveva", "avuta", "avute", "avuti", "avuto", "basta", "bene", "benissimo", "berlusconi", "brava", "bravo", "c", "casa", "caso", "cento", "certa", "certe", "certi", "certo", "che", "chi", "chicchessia", "chiunque", "ci", "ciascuna", "ciascuno", "cima", "cio", "ciÃ²", "cioe", "cioÃ¨", "circa", "citta", "cittÃ ", "codesta", "codesti", "codesto", "cogli", "coi", "col", "colei", "coll", "coloro", "colui", "come", "con", "concernente", "consiglio", "contro", "cortesia", "cos", "cosa", "cosi", "cosÃ¬", "cui", "d", "da", "dagli", "dai", "dal", "dall", "dalla", "dalle", "dallo", "davanti", "degli", "dei", "del", "dell", "della", "delle", "dello", "dentro", "detto", "deve", "di", "dice", "dietro", "dire", "dirimpetto", "dopo", "dove", "dovra", "dovrÃ ", "due", "dunque", "durante", "e", "Ã¨", "ecco", "ed", "egli", "ella", "eppure", "era", "erano", "esse", "essendo", "esser", "essere", "essi", "ex", "fa", "fare", "fatto", "favore", "fin", "finalmente", "finche", "fine", "fino", "forse", "fra", "fuori", "gia", "giÃ ", "giacche", "giorni", "giorno", "gli", "gliela", "gliele", "glieli", "glielo", "gliene", "governo", "grande", "grazie", "gruppo", "ha", "hai", "hanno", "ho", "i", "ieri", "il", "improvviso", "in", "infatti", "insieme", "intanto", "intorno", "invece", "io", "l", "la", "lÃ ", "lavoro", "le", "lei", "li", "lo", "lontano", "loro", "lui", "lungo", "ma", "macche", "magari", "mai", "male", "malgrado", "malissimo", "me", "medesimo", "mediante", "meglio", "meno", "mentre", "mesi", "mezzo", "mi", "mia", "mie", "miei", "mila", "miliardi", "milioni", "ministro", "mio", "moltissimo", "molto", "mondo", "nazionale", "ne", "negli", "nei", "nel", "nell", "nella", "nelle", "nello", "nemmeno", "neppure", "nessuna", "nessuno", "niente", "no", "noi", "non", "nondimeno", "nostra", "nostre", "nostri", "nostro", "nulla", "nuovo", "o", "od", "oggi", "ogni", "ognuna", "ognuno", "oltre", "oppure", "ora", "ore", "osi", "ossia", "paese", "parecchi", "parecchie", "parecchio", "parte", "partendo", "peccato", "peggio", "per", "perche", "perchÃ¨", "percio", "perciÃ²", "perfino", "pero", "perÃ²", "persone", "piedi", "pieno", "piglia", "piu", "piÃ¹", "po", "pochissimo", "poco", "poi", "poiche", "press", "prima", "primo", "proprio", "puo", "puÃ²", "pure", "purtroppo", "qualche", "qualcuna", "qualcuno", "quale", "quali", "qualunque", "quando", "quanta", "quante", "quanti", "quanto", "quantunque", "quasi", "quattro", "quel", "quella", "quelli", "quello", "quest", "questa", "queste", "questi", "questo", "qui", "quindi", "riecco", "salvo", "sara", "sarÃ ", "sarebbe", "scopo", "scorso", "se", "secondo", "seguente", "sei", "sempre", "senza", "si", "sia", "siamo", "siete", "solito", "solo", "sono", "sopra", "sotto", "sta", "staranno", "stata", "state", "stati", "stato", "stesso", "su", "sua", "successivo", "sue", "sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "tale", "talvolta", "tanto", "te", "tempo", "ti", "torino", "tra", "tranne", "tre", "troppo", "tu", "tua", "tue", "tuo", "tuoi", "tutta", "tuttavia", "tutte", "tutti", "tutto", "uguali", "un", "una", "uno", "uomo", "va", "vale", "varia", "varie", "vario", "verso", "vi", "via", "vicino", "visto", "vita", "voi", "volta", "vostra", "vostre", "vostri", "vostro", "a", "adesso", "ai", "al", "alla", "allo", "allora", "altre", "altri", "altro", "anche", "ancora", "avere", "aveva", "avevano", "ben", "buono", "che", "chi", "cinque", "comprare", "con", "consecutivi", "consecutivo", "cosa", "cui", "da", "del", "della", "dello", "dentro", "deve", "devo", "di", "doppio", "due", "e", "ecco", "fare", "fine", "fino", "fra", "gente", "giu", "ha", "hai", "hanno", "ho", "il", "indietro  invece", "io", "la", "lavoro", "le", "lei", "lo", "loro", "lui", "lungo", "ma", "me", "meglio", "molta", "molti", "molto", "nei", "nella", "no", "noi", "nome", "nostro", "nove", "nuovi", "nuovo", "o", "oltre", "ora", "otto", "peggio", "pero", "persone", "piu", "poco", "primo", "promesso", "qua", "quarto", "quasi", "quattro", "quello", "questo", "qui", "quindi", "quinto", "rispetto", "sara", "secondo", "sei", "sembra  sembrava", "senza", "sette", "sia", "siamo", "siete", "solo", "sono", "sopra", "soprattutto", "sotto", "stati", "stato", "stesso", "su", "subito", "sul", "sulla", "tanto", "te", "tempo", "terzo", "tra", "tre", "triplo", "ultimo", "un", "una", "uno", "va", "vai", "voi", "volte", "vostro")

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("entities")

  // 1-grams (words)
  // def tokenize(text: String) = 
    // text.split("""[\s\p{Punct}]+""") filterNot (stop contains _.toLowerCase)

  // 2-grams
  def tokenize(text: String) = 
    text.toLowerCase.split("""[\s\p{Punct}]+""") sliding 2 map { _ mkString " " } toList

  def getWord(w: String) = coll.findOne(Doc("entityLower" -> w)) match {
    case Some(xs) => xs.getAsOrElse[List[String]]("categories", Nil)
    case None => Nil
  }

  // 2-grams
  // def tokenize(text: String) = 
  //   text.split("""[\s\p{Punct}]+""") sliding 3 map { _ mkString " " } toList

  def extract(text: String) = {
    val m = tokenize(text)

    m foreach { w => 
      val gw = getWord(w)
      if (gw.nonEmpty) println(s"$w -> $gw")
    }

    Seq()

  }

}

class AnalyticEntityExtractor {

  val stop = Set("a", "abbastanza", "accidenti", "ad", "adesso", "affinche", "agli", "ahime", "ahimÃ¨", "ai", "al", "alcuna", "alcuni", "alcuno", "all", "alla", "alle", "allo", "altri", "altrimenti", "altro", "altrui", "anche", "ancora", "anni", "anno", "ansa", "assai", "attesa", "avanti", "avendo", "avente", "aver", "avere", "avete", "aveva", "avuta", "avute", "avuti", "avuto", "basta", "bene", "benissimo", "berlusconi", "brava", "bravo", "c", "casa", "caso", "cento", "certa", "certe", "certi", "certo", "che", "chi", "chicchessia", "chiunque", "ci", "ciascuna", "ciascuno", "cima", "cio", "ciÃ²", "cioe", "cioÃ¨", "circa", "citta", "cittÃ ", "codesta", "codesti", "codesto", "cogli", "coi", "col", "colei", "coll", "coloro", "colui", "come", "con", "concernente", "consiglio", "contro", "cortesia", "cos", "cosa", "cosi", "cosÃ¬", "cui", "d", "da", "dagli", "dai", "dal", "dall", "dalla", "dalle", "dallo", "davanti", "degli", "dei", "del", "dell", "della", "delle", "dello", "dentro", "detto", "deve", "di", "dice", "dietro", "dire", "dirimpetto", "dopo", "dove", "dovra", "dovrÃ ", "due", "dunque", "durante", "e", "Ã¨", "ecco", "ed", "egli", "ella", "eppure", "era", "erano", "esse", "essendo", "esser", "essere", "essi", "ex", "fa", "fare", "fatto", "favore", "fin", "finalmente", "finche", "fine", "fino", "forse", "fra", "fuori", "gia", "giÃ ", "giacche", "giorni", "giorno", "gli", "gliela", "gliele", "glieli", "glielo", "gliene", "governo", "grande", "grazie", "gruppo", "ha", "hai", "hanno", "ho", "i", "ieri", "il", "improvviso", "in", "infatti", "insieme", "intanto", "intorno", "invece", "io", "l", "la", "lÃ ", "lavoro", "le", "lei", "li", "lo", "lontano", "loro", "lui", "lungo", "ma", "macche", "magari", "mai", "male", "malgrado", "malissimo", "me", "medesimo", "mediante", "meglio", "meno", "mentre", "mesi", "mezzo", "mi", "mia", "mie", "miei", "mila", "miliardi", "milioni", "ministro", "mio", "moltissimo", "molto", "mondo", "nazionale", "ne", "negli", "nei", "nel", "nell", "nella", "nelle", "nello", "nemmeno", "neppure", "nessuna", "nessuno", "niente", "no", "noi", "non", "nondimeno", "nostra", "nostre", "nostri", "nostro", "nulla", "nuovo", "o", "od", "oggi", "ogni", "ognuna", "ognuno", "oltre", "oppure", "ora", "ore", "osi", "ossia", "paese", "parecchi", "parecchie", "parecchio", "parte", "partendo", "peccato", "peggio", "per", "perche", "perchÃ¨", "percio", "perciÃ²", "perfino", "pero", "perÃ²", "persone", "piedi", "pieno", "piglia", "piu", "piÃ¹", "po", "pochissimo", "poco", "poi", "poiche", "press", "prima", "primo", "proprio", "puo", "puÃ²", "pure", "purtroppo", "qualche", "qualcuna", "qualcuno", "quale", "quali", "qualunque", "quando", "quanta", "quante", "quanti", "quanto", "quantunque", "quasi", "quattro", "quel", "quella", "quelli", "quello", "quest", "questa", "queste", "questi", "questo", "qui", "quindi", "riecco", "salvo", "sara", "sarÃ ", "sarebbe", "scopo", "scorso", "se", "secondo", "seguente", "sei", "sempre", "senza", "si", "sia", "siamo", "siete", "solito", "solo", "sono", "sopra", "sotto", "sta", "staranno", "stata", "state", "stati", "stato", "stesso", "su", "sua", "successivo", "sue", "sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "tale", "talvolta", "tanto", "te", "tempo", "ti", "torino", "tra", "tranne", "tre", "troppo", "tu", "tua", "tue", "tuo", "tuoi", "tutta", "tuttavia", "tutte", "tutti", "tutto", "uguali", "un", "una", "uno", "uomo", "va", "vale", "varia", "varie", "vario", "verso", "vi", "via", "vicino", "visto", "vita", "voi", "volta", "vostra", "vostre", "vostri", "vostro", "a", "adesso", "ai", "al", "alla", "allo", "allora", "altre", "altri", "altro", "anche", "ancora", "avere", "aveva", "avevano", "ben", "buono", "che", "chi", "cinque", "comprare", "con", "consecutivi", "consecutivo", "cosa", "cui", "da", "del", "della", "dello", "dentro", "deve", "devo", "di", "doppio", "due", "e", "ecco", "fare", "fine", "fino", "fra", "gente", "giu", "ha", "hai", "hanno", "ho", "il", "indietro  invece", "io", "la", "lavoro", "le", "lei", "lo", "loro", "lui", "lungo", "ma", "me", "meglio", "molta", "molti", "molto", "nei", "nella", "no", "noi", "nome", "nostro", "nove", "nuovi", "nuovo", "o", "oltre", "ora", "otto", "peggio", "pero", "persone", "piu", "poco", "primo", "promesso", "qua", "quarto", "quasi", "quattro", "quello", "questo", "qui", "quindi", "quinto", "rispetto", "sara", "secondo", "sei", "sembra  sembrava", "senza", "sette", "sia", "siamo", "siete", "solo", "sono", "sopra", "soprattutto", "sotto", "stati", "stato", "stesso", "su", "subito", "sul", "sulla", "tanto", "te", "tempo", "terzo", "tra", "tre", "triplo", "ultimo", "un", "una", "uno", "va", "vai", "voi", "volte", "vostro")
  val articles = Set("il", "lo", "la", "i", "gli", "le", "un", "uno", "una") ++ Set("quel", "quello", "quelle", "quelli", "quella") ++ Set("quest", "questo", "queste", "questi", "questa") ++ Set("del", "della", "delle", "degli")


  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val client = MongoClient("cannobio", 27017)
  val db = client("wikipedia")
  val coll = db("entities")
  val rdr = db("redirects")
  val ptls = db("portals")

  // 1-grams (words)
  // def tokenize(text: String) = 
    // text.split("""[\s\p{Punct}]+""") filterNot (stop contains _.toLowerCase)

  // 2-grams

  case class NGram(tokens: Seq[String]) {
    def show = tokens mkString " "
    override def toString = show

    def tokensLower = tokens map { _.toLowerCase }

    def containsStop = tokensLower exists stop.contains
    def startsWithStop = stop contains tokensLower.head 
    def endsWithStop = stop contains tokensLower.last

    def startsWithArticle = articles contains tokensLower.head

    def contains(that: NGram) = this.tokens containsSlice that.tokens
    def contained(that: NGram) = that contains this

  }

  implicit class RichDoc(doc: Doc) {
    def str(key: String) = doc.getAs[String](key).get
    def strs(key: String) = doc.getAsOrElse[List[String]](key, nil)
  }

  implicit class RichBaseDoc(doc: BasicDBObject) {
    def str(key: String) = doc.getAs[String](key).get
    def strs(key: String) = doc.getAsOrElse[List[String]](key, nil)
  }

  case class Match(ng: NGram, categories: Seq[String], portals: Seq[String])

  def mkCategories(xs: Seq[String]) = xs
    .filterNot { _ startsWith "Voci" }
    .filterNot { _ startsWith "Collegamento" }
    .filterNot { _ startsWith "Aggiungere" }
    .filterNot { _ contains "_-_" } // usually crap
    .filterNot { _ contains "Wiki" } // meta categories
    .filterNot { _ contains "Aggiornare" } // meta categories
    .filterNot { _ == "Informazioni_senza_fonte" }
    .filterNot { _ == "BioBot" }

  def blacklistCategories(xs: Seq[String]) = 
    (xs exists { c => c startsWith "EP"}) || 
    (xs exists { c => c startsWith "Brani_Musicali"}) ||
    (xs exists { c => c startsWith "Singoli_"}) ||
    (xs exists { c => c startsWith "Album_"}) ||
    (xs exists { c => c startsWith "Serie_televisive_"}) ||
    (xs exists { c => c startsWith "Arcani_"}) // divinazione, controllare se ci sono altri casi che inquinano

  case object Match {
    def fromDoc(ng: NGram, doc: Doc) = {
      val categories = mkCategories(doc strs "categories")

      if (blacklistCategories(categories))
        None
      else
        Some(Match(ng, categories, doc strs "portals"))
    }
  }

  import java.util.regex.Pattern
  val pat = Pattern.compile("\\W+", Pattern.UNICODE_CHARACTER_CLASS)

  def tokenize(text: String, n: Int) : Seq[NGram] = n match {
    case 1 => pat split text filterNot (stop contains _.toLowerCase) filterNot {_ forall(_.isDigit)} map {x => NGram(Seq(x)) } toList
    case _ => pat split text sliding n map {x => NGram(x)} toList
  }
    
  def getEntityDoc(k: String, lower: Boolean = false) = {

    val key = lower ? "entityLower" | "entity"
    val word = lower ? k.toLowerCase | k

    if (lower) 
      coll.findOne(Doc("$or" -> Seq(Doc("entity" -> word), Doc("entityLower" -> word))))
    else 
      coll.findOne(Doc(key -> word))
  } 

  // def getEntityCI(ng: NGram) = getEntity(ng, true)

  def getEntity(ng: NGram) : Option[Match] = 
    getEntity(ng, false)

  def getEntity(ng: NGram, lower: Boolean) : Option[Match] = {

    val keyRdr = lower ? "fromLower" | "from"
    val word = lower ? ng.show.toLowerCase | ng.show 

    getEntityDoc(word, lower) match {
      case Some(doc) => 
        Match fromDoc (ng, doc)
      case None =>
        rdr.findOne(Doc(keyRdr -> word)) flatMap { wd => getEntityDoc(wd.getAs[String]("to").get) } flatMap { doc => Match fromDoc (ng, doc) }
    }

  }

  def getTop(xs: Seq[String]) = xs
    .map { _.toLowerCase }
    .groupBy(identity)
    .mapValues(_.length)
    .toList
    .sortBy{ case (k,v) => -v }

  def extractPortals(text: String) = {

    import eu.picoweb.commons.ScalaSoup
    import eu.picoweb.commons.ScalaSoup._
    // val firstPar = ScalaSoup clean ScalaSoup.parse(text).select("p").head.toString

    val threeGrams = tokenize(text, 3) collect Function.unlift(getEntity) 
    val bigrams = tokenize(text, 2) collect Function.unlift(getEntity) filterNot { m => threeGrams map {_.ng} exists m.ng.contained }
    val onegrams = tokenize(text, 1) collect Function.unlift(getEntity) filterNot { m => threeGrams map {_.ng} exists m.ng.contained } filterNot { m => bigrams map {_.ng} exists m.ng.contained }

    val ngrams = (threeGrams ++ bigrams ++ onegrams)
      // .filterNot { _.ng.isNumber }

    // Uncomment for debug!
    // ngrams foreach { m => println(s"\t$m") }

    getTop (ngrams flatMap { _.portals })

  }

  def analyze(text: String) = {

    // println (tokenize(text, 1))

    val threeGrams = tokenize(text, 3) collect Function.unlift(getEntity) 
    val bigrams = tokenize(text, 2) collect Function.unlift(getEntity) filterNot { m => threeGrams map {_.ng} exists m.ng.contained }
    val onegrams = tokenize(text, 1) collect Function.unlift(getEntity) filterNot { m => threeGrams map {_.ng} exists m.ng.contained } filterNot { m => bigrams map {_.ng} exists m.ng.contained }

    (threeGrams ++ bigrams ++ onegrams) foreach { m => println(s"\t$m") }

  }

  def extract(title: String, description: Option[String], text: String) = {

    val portalsTitle = extractPortals(title)
    println (s"Title: $portalsTitle")

    println()

    val portalsText = extractPortals(text)
    println (s"Text: $portalsText")

    println()

    if (description.isDefined) {
      val portalsDescription = extractPortals(description.get)
      println (s"Description: $portalsDescription")
    }

  }

  def extract2(title: String, text: String) = {

    val total = s"$title $text"

    // println (total)

    val threeGrams = tokenize(total, 3) collect Function.unlift(getEntity) 
    val bigrams = tokenize(total, 2) collect Function.unlift(getEntity) filterNot { m => threeGrams map {_.ng} exists m.ng.contained }
    val onegrams = tokenize(total, 1) collect Function.unlift(getEntity) filterNot { m => threeGrams map {_.ng} exists m.ng.contained } filterNot { m => bigrams map {_.ng} exists m.ng.contained }

    val ngrams = (threeGrams ++ bigrams ++ onegrams) //map { m => (m.ng, m) } toMap

    def spot(ng: NGram) = 
      ngrams filter { m => m.ng contains ng } headOption

    def spotExact(ng: NGram) = 
      ngrams filter { m => m.ng == ng } headOption

    val titleM = (tokenize(title, 3) ++ tokenize(title, 2) ++ tokenize(title, 1)) collect Function.unlift(spot) 

    val textM = (tokenize(text, 3) ++ tokenize(text, 2) ++ tokenize(text, 1)) collect Function.unlift(spotExact) 

    // Uncomment for tokens
    // tokenize(title, 3) foreach println

    println(s"Matches for title:")
    titleM foreach println

    println()

    println(s"Matches for text:")
    textM foreach println

    println()

    println(s"lowercase 3-grams:")
    tokenize(text, 3) filterNot { _.startsWithArticle} collect Function.unlift(x => getEntity(x, true)) foreach println

    println()

    println(s"lowercase 2-grams:")
    tokenize(text, 2) filterNot { _.startsWithArticle} collect Function.unlift(x => getEntity(x, true)) foreach println

    println()

    println(s"lowercase 1-grams:")
    tokenize(text, 1) collect Function.unlift(x => getEntity(x, true)) foreach println

    println()

    getTop(mkPortals(textM flatMap { _.portals }) ++ mkPortals(titleM flatMap { _.portals }))

  }

  // def cleanText(text: String) =
  //   ScalaSoup clean text

  def mkPortals(xs: Seq[String]) = xs
    .filterNot { _.toLowerCase == "biografie" }

  val noPortals = Seq("Biografie")

  case class Portal(key: String, repr: String, tree: Seq[String])

  def getPortal(portal: String) = 
    if (noPortals contains portal) None 
    else ptls.findOne(Doc("key" -> portal)) map { doc => 
      Portal(portal, doc.getAs[String]("repr").get, doc.getAs[Seq[String]]("tree").get)
    }


}

// http://www.repubblica.it/salute/medicina/2015/11/06/news/leucemia_terapia_genica_cura_bimba_di_un_anno_malata_terminale-126735265/
// 

object Main extends App {


  implicit val system = ActorSystem("wiki")
  import system.dispatcher

  val ee = new AnalyticEntityExtractor

  Extractor extract args.head foreach { case Article(Some(title), Some(text), description, _, _, _, _, _) =>

    val cleaned = ScalaSoup clean text

    println (s"Title: $title")
    println (s"Text: $cleaned")
    // println ()
    // ee analyze (title)
    // println()
    // ee analyze (cleaned)
    // println ()
    // ee extract2 (title, cleaned)

    // println ("Definitive")

    val portals = ee extract2 (title, cleaned)
    println (portals)

  }



}

object AssignEntities extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val ee = new AnalyticEntityExtractor

  val client = MongoClient("cannobio", 27017)
  val db = client("news")
  val coll = db("articles")

  val e = Doc("$exists" -> true)
  val ne = Doc("$exists" -> false)

  def assign() {

    coll.find(Doc("type" -> "web", "status.tags" -> ne, "cluster" -> e)).sort(Doc("score" -> -1)) foreach { article =>

      val id = article.getAs[ObjectId]("_id").get

      val title = article.getAsOrElse[String]("title", "")
      val text = article.getAsOrElse[String]("text", "")

      val portals = ee.extract2(title, ScalaSoup clean text)

      val tags = portals take 3 map { case (k,v) => Doc("id" -> k, "name" -> s"$k ($v)") }

      val cats = portals take 3 map { case (k,v) => k } collect Function.unlift(ee.getPortal) map { c => Doc("repr" -> c.repr, "tree" -> c.tree) }

      coll.update(Doc("_id" -> id), Doc("$set" -> Doc("status.tags" -> tags, "categories" -> cats)))

      println (s"Updated: $id")
    }

  }

  while (true){
    assign()
    println ("Done, sleeping for 10s.")
    Thread sleep 10000
  }


}

object ExpandEntities extends App {

  import com.mongodb.casbah.Imports._
  import com.mongodb.casbah.Imports.{MongoDBObject => Doc}

  val ee = new AnalyticEntityExtractor

  val client = MongoClient("cannobio", 27017)
  val db = client("news")
  val coll = db("articles")

  val e = Doc("$exists" -> true)
  val ne = Doc("$exists" -> false)

  // coll.find(Doc("type" -> "web", "_id" -> Doc("$gte" -> new ObjectId("562567e00000000000000000")))) foreach { article =>
  coll.find(Doc("type" -> "web", "status.tags" -> e)) foreach { article =>

    val id = article.getAs[ObjectId]("_id").get
    val status = article.getAs[BasicDBObject]("status").get
    val tags = status.getAs[Seq[BasicDBObject]]("tags").get

    val m = tags map { t => t.getAs[String]("id").get } collect Function.unlift(ee.getPortal)

    val toSave = m map { c => Doc("repr" -> c.repr, "tree" -> c.tree) }


    // println (tags)

    coll.update(Doc("_id" -> id), Doc("$set" -> Doc("categories" -> toSave)))

    println (s"Updated: $id")
  }
}