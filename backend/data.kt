package backend

abstract class Data

object None:Data() {
    override fun toString() = "None"
}
data class IntData(val value: Int) : Data(){
    override fun toString(): String= "$value"
}

data class StringData(val value: String) : Data(){
    override fun toString(): String= "$value"
}

data class BooleanData(val value: Boolean) : Data(){
    override fun toString(): String = "$value"
}

data class FuncData(val name: String, val parameters: List<String>,val body: Expr) : Data(){
    override fun toString(): String = parameters.joinToString(", ").let{"$name($it){...}"}
}

