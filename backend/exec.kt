import org.antlr.v4.runtime.*
import backend.*
// [THIS IS READ-ONLY]
fun execute(source:String) {

    val source= File(source: String).
    val errorlistener = object: BaseErrorListener() {
        override fun syntaxError(recognizer: Recognizer<*,*>,
               offendingSymbol: Any?,
               line: Int,
               pos: Int,
               msg: String,
               e: RecognitionException?) {
            throw Exception("${e} at line:${line}, char:${pos}")
        }
    }
    val input = CharStreams.fromString(source)
    val lexer = PLLexer(input).apply {
        removeErrorListeners()
        addErrorListener(errorlistener)
    }
    val tokens = CommonTokenStream(lexer)
    val parser = PLParser(tokens).apply {
        removeErrorListeners()
        addErrorListener(errorlistener)
    }    
    
    try {
        val result = parser.program()
        result.expr.eval(Runtime())
    } catch(e:Exception) {
        println("Error: ${e}")
    }
}
//Test case
execute("test1.txt")
execute("test2.txt")
execute("test3.txt")
execute("test4.txt")
execute("test5.txt")