import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.output.CliktHelpFormatter
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import com.github.javaparser.JavaParser
import com.google.googlejavaformat.java.Formatter
import com.opencsv.CSVWriter
import java.io.File


class App : CliktCommand() {

    init {
        context { helpFormatter = CliktHelpFormatter(showDefaultValues = true, showRequiredTag = true) }
    }

    private val outputFile: File by option("-o", "--output")
            .file(exists = false, writable = true)
            .default(File("output.csv"))

    private val sources: List<File> by argument()
            .file(exists = true, readable = true)
            .multiple()

    private val formatter = Formatter()
    private val parser = JavaParser()

    override fun run() {

        CSVWriter(outputFile.writer()).use { out ->

            out.writeNext(arrayOf("type", "path", "comment range", "sentence"))

            val javaFiles = sources.asSequence()
                    .flatMap { it.walk() }
                    .filter { it.isFile }
                    .filter { "java" == it.extension }

            for (file in javaFiles) {

                val path = file.path
                val source = formatter.formatSource(file.readText())

                val commentsCollection = parser.parse(source).commentsCollection.orElse(null) ?: continue

                for (comment in commentsCollection.blockComments) {

                    val startLine = comment.range
                            .map { it.begin.line }
                            .orElse(-1)
                            .toString()

                    val text = comment.toText()
                    val sentences = text.sentences()
                    for (sentence in sentences) {
                        out.writeNext(arrayOf(comment.javaClass.simpleName, path, startLine, sentence))
                    }
                }

                for (comment in commentsCollection.javadocComments) {

                    val startLine = comment.range
                            .map { it.begin.line }
                            .orElse(-1)
                            .toString()

                    val text = comment.toText()
                    val sentences = text.sentences()
                    for (sentence in sentences) {
                        out.writeNext(arrayOf(comment.javaClass.simpleName, path, startLine, sentence))
                    }
                }

                val lineCommentBlocks = commentsCollection.lineComments
                        .contiguous { prev, current -> prev.range.isPresent && current.range.isPresent && prev.range.get().begin.line + 1 == current.range.get().begin.line }
                        .toList()

                for (comments in lineCommentBlocks) {
                    val start = comments.first()

                    val startLine = start.range
                            .map { it.begin.line }
                            .orElse(-1)
                            .toString()

                    val text = comments.joinToString(" ") { it.toText() }
                    val sentences = text.sentences()
                    for (sentence in sentences) {
                        out.writeNext(arrayOf(start.javaClass.simpleName, path, startLine, sentence))
                    }
                }
            }
        }
    }
}


fun main(args: Array<String>) {
    App().main(args)
}