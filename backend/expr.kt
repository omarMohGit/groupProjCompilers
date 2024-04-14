package backend

abstract class Expr {
    abstract fun eval(runtime:Runtime):Data
}

class IntegerLiteral(val lexeme:String):Expr(){
    override fun eval(runtime: Runtime): Data = IntData(lexeme.toInt())
}

class StringLiteral(val lexeme:String): Expr(){
    override fun eval(runtime:Runtime): Data= StringData(lexeme)
}

class BooleanLiteral(val lexeme:String): Expr(){
    override fun eval (runtime:Runtime): Data = BooleanData(lexeme.equals("true"))
}

enum class Operator {
    ADD, SUB, MULT, DIV
}

class Operations(val op:Operator, val left: Expr, val right:Expr) :Expr() {
    override fun eval(runtime: Runtime): Data {
        val L = left.eval(runtime)
        val R = right.eval(runtime)
        
        if (L is IntData && R is IntData) {
            return when (op) {
                Operator.ADD -> IntData(L.value + R.value)
                Operator.SUB -> IntData(L.value - R.value)
                Operator.MULT-> IntData(L.value * R.value)
                Operator.DIV-> if (R.value != 0) {
                                    IntData(L.value / R.value)
                                } else {
                                    throw Exception("no diviy diviy")
                                }
            }
        } else {
            throw Exception("Idk wrong symbols?")
        }
    }
}


class Concat(val left: Expr, val right: Expr) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val L = left.eval(runtime).toString()
        val R = right.eval(runtime).toString()
        return StringData(L + R)
    }
}

class Assign(val symbol:String, val expr:Expr):Expr(){
    override fun eval(runtime:Runtime): Data {
        val result = expr.eval(runtime)
        runtime.symbolTable[symbol] = result
        return result
    }
}


class Deref(val name: String): Expr(){
    override fun eval (runtime:Runtime):Data{
        val data=runtime.symbolTable[name]
        if(data==null){
            throw Exception ("Nothing is assigned")
        }
        return data
}
    
    
class Block (val exprList: List<Expr>):Expr(){
    override fun eval(runtime:Runtime):Data{
     var result:Data= None 
        exprList.forEach{
         result=it.eval(runtime)
     }
     return result
    }
}

class Loop(val cond:Expr, val body:Expr): Expr(){
    override fun eval(runtime:Runtime):Data{
        var flag = cond.eval(runtime) as BooleanData
        var result: Data = None
        var iter:Int= 1000000
        for(iter in 1000000 downTo 1){
            result = body.eval(runtime)
            flag = cond.eval(runtime) as BooleanData
            if(iter ==1){
                println("maxed out")
                println(runtime)
                return None
            }
        }
        return result
    }
}

class ifStatement(val cond:Expr, val trueExpr:Expr, val falseExpr:Expr):Expr(){
    override fun eval (runtime:Runtime): Data{
        val cond_data= cond.eval(runtime)
        if(cond_data !is BooleanData){
            throw Exception ("need data")
        }
        return if (cond_data.value){
            return trueExpr.eval(runtime)
        }else{
            return falseExpr.eval(runtime)
        }
    }
}


class Invoke(val name: String, val args: List<Expr>) : Expr() {
    override fun eval(runtime: Runtime): Data {
        val functionData = runtime.symbolTable[name]
        if (functionData is FuncData) {
            val r = runtime.subscope(functionData.parameters.zip(args.map {it.eval(runtime)}).toMap())
            return functionData.body.eval(r)
        } else {
            throw Exception("not defined func")
        }}
    }
}


class maxVal(val values: List<Expr>): Expr(){
    override fun eval(runtime:RunTime): Data{
        val nums= values.map{
            values.eval(runtime)
        }

        val max = nums.maxOrNull()?: throw Exception("nothing in there")
        return IntData(max)
    }
}

class minVal(val values: List<Expr>): Expr(){
    override fun eval(runtime:RunTime): Data{
        val nums= values.map{
            values.eval(runtime)
        }

        val min = nums.minOrNull()?: throw Exception("nothing in there")
        return IntData(min)
    }
}


class argNeg( val values: List<Expr>): Expr(){
    override fun eval(runtime: Runtime): Data{
        val numbersTotaled = map{
            when (val result = it.eval(runtime)){
                is Data.IntData -> result.value
                else -> throw Exception("we need integerss)
            }
        }
        val total = numbersTotaled.sum()
        if (total<0){
            print("negative total: " $total)
        }else{
            return IntData($total)
        }
    }
}

class avg(val values: List<Expr>): Expr(){
    override fun eval (runtime:Runtime): Data{
        var sum=0.0
        var count=0
        for(a in 0 until values.size){
            val value = values [a].eval(runtime)
            if(result is Data.IntData){
                sum=sum+result.value
                count++
            }
            else{
                throw Exception ("none int value detected")
            }
        }
        val avg =sum/count
        return IntData(avg.toInt())
    }
}