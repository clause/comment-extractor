import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.opencsv.CSVWriter
import edu.stanford.nlp.simple.Document
import edu.stanford.nlp.simple.Sentence
import org.eclipse.jdt.core.dom.*
import java.io.File

class App : CliktCommand() {

    private val outputFile: File by option().file(exists = false).default(File("output.csv"))
    private val sources: List<File> by argument().file(exists = true, readable = true).multiple()

    override fun run() {

        val parser = ASTParser()

        outputFile.writer().use { out ->
            val writer = CSVWriter(out)

            writer.writeNext(arrayOf("path", "comment range", "sentence"))

            val javaSources = sources.flatMap { it.walk().asIterable() }
                    .filter { it.isFile }
                    .filter { "java" == it.extension }

            for (source in javaSources) {

                val comments = parser.parse(source).find<Comment>()

                for (comment in comments) {
                    val range = comment.startPosition until comment.startPosition + comment.length

                    val text = extractTextFromComment(comment)
                    val sentences = splitToSentences(text)

                    for (sentence in sentences) {
                        writer.writeNext(arrayOf(source.path, range.toString(), sentence.text()))
                    }
                }
            }
        }
    }

    private fun extractTextFromComment(comment: Comment): String = when (comment) {
        is Javadoc -> {
            // Grab TagElements with no tag names.  This is the text before the first doctag.  Strip any leading whitespace and *'s
            comment.tags()
                    .filterIsInstance<TagElement>()
                    .filter { null == it.tagName }
                    .joinToString { it.toString() }
                    .replaceFirst(Regex("^[\\s*]+"), "")
        }
        is BlockComment, is LineComment -> {
            comment.toString()
        }
        else -> throw RuntimeException("Unknown comment type: ${comment.javaClass}")
    }

    // Use Stanford CoreNLP to split a chunk of text into individual sentences
    private fun splitToSentences(text: String): List<Sentence> = Document(text).sentences()

}

fun main(args: Array<String>) = App().main(args)