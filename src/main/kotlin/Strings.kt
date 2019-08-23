import edu.stanford.nlp.simple.Document

fun String.sentences(): List<String> = Document(this).sentences().map { it.text() }
