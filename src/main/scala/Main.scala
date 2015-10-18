import com.redis._

object Main extends App {

	val redis = new RedisClient("localhost", 6379)

	val stop = Set("a", "abbastanza", "accidenti", "ad", "adesso", "affinche", "agli", "ahime", "ahimÃ¨", "ai", "al", "alcuna", "alcuni", "alcuno", "all", "alla", "alle", "allo", "altri", "altrimenti", "altro", "altrui", "anche", "ancora", "anni", "anno", "ansa", "assai", "attesa", "avanti", "avendo", "avente", "aver", "avere", "avete", "aveva", "avuta", "avute", "avuti", "avuto", "basta", "bene", "benissimo", "berlusconi", "brava", "bravo", "c", "casa", "caso", "cento", "certa", "certe", "certi", "certo", "che", "chi", "chicchessia", "chiunque", "ci", "ciascuna", "ciascuno", "cima", "cio", "ciÃ²", "cioe", "cioÃ¨", "circa", "citta", "cittÃ ", "codesta", "codesti", "codesto", "cogli", "coi", "col", "colei", "coll", "coloro", "colui", "come", "con", "concernente", "consiglio", "contro", "cortesia", "cos", "cosa", "cosi", "cosÃ¬", "cui", "d", "da", "dagli", "dai", "dal", "dall", "dalla", "dalle", "dallo", "davanti", "degli", "dei", "del", "dell", "della", "delle", "dello", "dentro", "detto", "deve", "di", "dice", "dietro", "dire", "dirimpetto", "dopo", "dove", "dovra", "dovrÃ ", "due", "dunque", "durante", "e", "Ã¨", "ecco", "ed", "egli", "ella", "eppure", "era", "erano", "esse", "essendo", "esser", "essere", "essi", "ex", "fa", "fare", "fatto", "favore", "fin", "finalmente", "finche", "fine", "fino", "forse", "fra", "fuori", "gia", "giÃ ", "giacche", "giorni", "giorno", "gli", "gliela", "gliele", "glieli", "glielo", "gliene", "governo", "grande", "grazie", "gruppo", "ha", "hai", "hanno", "ho", "i", "ieri", "il", "improvviso", "in", "infatti", "insieme", "intanto", "intorno", "invece", "io", "l", "la", "lÃ ", "lavoro", "le", "lei", "li", "lo", "lontano", "loro", "lui", "lungo", "ma", "macche", "magari", "mai", "male", "malgrado", "malissimo", "me", "medesimo", "mediante", "meglio", "meno", "mentre", "mesi", "mezzo", "mi", "mia", "mie", "miei", "mila", "miliardi", "milioni", "ministro", "mio", "moltissimo", "molto", "mondo", "nazionale", "ne", "negli", "nei", "nel", "nell", "nella", "nelle", "nello", "nemmeno", "neppure", "nessuna", "nessuno", "niente", "no", "noi", "non", "nondimeno", "nostra", "nostre", "nostri", "nostro", "nulla", "nuovo", "o", "od", "oggi", "ogni", "ognuna", "ognuno", "oltre", "oppure", "ora", "ore", "osi", "ossia", "paese", "parecchi", "parecchie", "parecchio", "parte", "partendo", "peccato", "peggio", "per", "perche", "perchÃ¨", "percio", "perciÃ²", "perfino", "pero", "perÃ²", "persone", "piedi", "pieno", "piglia", "piu", "piÃ¹", "po", "pochissimo", "poco", "poi", "poiche", "press", "prima", "primo", "proprio", "puo", "puÃ²", "pure", "purtroppo", "qualche", "qualcuna", "qualcuno", "quale", "quali", "qualunque", "quando", "quanta", "quante", "quanti", "quanto", "quantunque", "quasi", "quattro", "quel", "quella", "quelli", "quello", "quest", "questa", "queste", "questi", "questo", "qui", "quindi", "riecco", "salvo", "sara", "sarÃ ", "sarebbe", "scopo", "scorso", "se", "secondo", "seguente", "sei", "sempre", "senza", "si", "sia", "siamo", "siete", "solito", "solo", "sono", "sopra", "sotto", "sta", "staranno", "stata", "state", "stati", "stato", "stesso", "su", "sua", "successivo", "sue", "sugli", "sui", "sul", "sull", "sulla", "sulle", "sullo", "suo", "suoi", "tale", "talvolta", "tanto", "te", "tempo", "ti", "torino", "tra", "tranne", "tre", "troppo", "tu", "tua", "tue", "tuo", "tuoi", "tutta", "tuttavia", "tutte", "tutti", "tutto", "uguali", "un", "una", "uno", "uomo", "va", "vale", "varia", "varie", "vario", "verso", "vi", "via", "vicino", "visto", "vita", "voi", "volta", "vostra", "vostre", "vostri", "vostro", "a", "adesso", "ai", "al", "alla", "allo", "allora", "altre", "altri", "altro", "anche", "ancora", "avere", "aveva", "avevano", "ben", "buono", "che", "chi", "cinque", "comprare", "con", "consecutivi", "consecutivo", "cosa", "cui", "da", "del", "della", "dello", "dentro", "deve", "devo", "di", "doppio", "due", "e", "ecco", "fare", "fine", "fino", "fra", "gente", "giu", "ha", "hai", "hanno", "ho", "il", "indietro	invece", "io", "la", "lavoro", "le", "lei", "lo", "loro", "lui", "lungo", "ma", "me", "meglio", "molta", "molti", "molto", "nei", "nella", "no", "noi", "nome", "nostro", "nove", "nuovi", "nuovo", "o", "oltre", "ora", "otto", "peggio", "pero", "persone", "piu", "poco", "primo", "promesso", "qua", "quarto", "quasi", "quattro", "quello", "questo", "qui", "quindi", "quinto", "rispetto", "sara", "secondo", "sei", "sembra	sembrava", "senza", "sette", "sia", "siamo", "siete", "solo", "sono", "sopra", "soprattutto", "sotto", "stati", "stato", "stesso", "su", "subito", "sul", "sulla", "tanto", "te", "tempo", "terzo", "tra", "tre", "triplo", "ultimo", "un", "una", "uno", "va", "vai", "voi", "volte", "vostro")

	val text = """Sassuolo, Cannavaro: ''I punti che abbiamo non sono frutto di casualità. Napoli? Non dico nulla per scaramanzia''
Clicca per Ingrandire
Paolo Cannavaro, ai microfoni di Sky e nel post partita di Sassuolo-Lazio, match vinto dalla squadra emiliana per 2-1, ha rilasciato alcune dichiarazioni. Questo quanto detto dal difensore del Sassuolo:

''Il rigore? C’è poco da discutere. Il tocco c’è stato, non so se si vede o no dalla tv, ma anche il giudice di linea ha visto tutto, quindi il rigore è stato dato perché il tocco c’è stato, non è assolutamente una simulazione. Ve lo posso garantire. Un po’ di mestiere c’è…? Il campo era bagnato, diciamo che non ho un grande stile nel cadere, sicuramente.

C’era mio fratello Fabio ad  assistere alla gara ed abbiamo parlato anche del Napoli: era inevitabile, anche se con lui ultimamente si parla solo di tattica, perché da quando ha iniziato ad allenare, ha deciso di fare l’allenatore. Però, al di là di quello, sapete tutti l’amore che la famiglia Cannavaro prova nei confronti del Napoli, è una grande gioia vedere il Napoli lì su.

Per il sottoscritto non è una sorpresa, anche perché tutti i miei compagni di squadra sanno cosa penso di quest’anno, di come andrà a finire. Non lo dico perché sono scaramantico… Il Sassuolo ha gli stessi punti del Napoli."""

	def getWord(w: String) = redis.lrange(w, 0, -1) match {
		case Some(xs) => xs.flatten
		case None => Seq()
	}
	// 1-grams (words)
	val m = text.split("""[\s\p{Punct}]+""") filterNot (stop contains _.toLowerCase)

	// 2-grams
	// val m = text.split("""[\s\p{Punct}]+""") sliding 2 map { _ mkString " " } toList
	
	m foreach { w => 
		val gw = getWord(w)
		println(s"$w -> $gw")
	}

	val wds = m map getWord flatten

	println (   wds.groupBy(identity).mapValues(_.length).toSeq.sortBy{ case (k,v) => -v }    )

}