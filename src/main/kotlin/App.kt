import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.comments.BlockComment
import com.github.javaparser.ast.comments.Comment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.comments.LineComment
import com.google.googlejavaformat.java.Formatter
import com.opencsv.CSVWriter
import edu.stanford.nlp.simple.Document
import edu.stanford.nlp.simple.Sentence
import java.io.File

class App : CliktCommand() {

    private val outputFile: File by option().file(exists = false).default(File("output.csv"))
    private val sources: List<File> by argument().file(exists = true, readable = true).multiple()
    private val formatter = Formatter()
    private val parser = JavaParser()

    override fun run() {

        CSVWriter(outputFile.writer()).use { out ->

            out.writeNext(arrayOf("path", "comment range", "sentence"))

            val javaFiles = sources.asSequence()
                    .flatMap { it.walk() }
                    .filter { it.isFile }
                    .filter { "java" == it.extension }

            for (file in javaFiles) {

                val path = file.path
                val source = formatter.formatSource(file.readText())

                parser.parse(source).commentsCollection.ifPresent { commentsCollection ->
                    for (comment in commentsCollection.comments) {

                        val range = comment.range.map { it.toString() }.orElse("")
                        val text = extractTextFromComment(comment)

                        splitToSentences(text).forEach { out.writeNext(arrayOf(path, range, it.text())) }
                    }
                }
            }
        }
    }

    private fun extractTextFromComment(comment: Comment): String = when (comment) {
        is JavadocComment -> {
            comment.parse().description.toText().lines().joinToString(" ")
        }
        is BlockComment -> {
            comment.toString()
                    .replace(Regex("^/"), " ")
                    .replace(Regex("/$"), " ")
                    .trimMargin("*")
                    .trimIndent()
                    .lines()
                    .joinToString(" ")
        }
        is LineComment -> {
            comment.toString().replace(Regex("^[\t ]*\\\\[\t ]*"), "")
        }
        else -> {
            throw RuntimeException("Unknown comment type: ${comment.javaClass}")
        }
    }

    // Use Stanford CoreNLP to split a chunk of text into individual sentences
    private fun splitToSentences(text: String): List<Sentence> = Document(text).sentences()

}

fun main(args: Array<String>) = App().main(args)