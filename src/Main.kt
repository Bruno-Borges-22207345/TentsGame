import java.io.File

fun main() {

//***NÃO APAGAR****
    var opJogo:Int
    var linhas:Int
    var colunas:Int

    do {
        println(criaMenu())

        opJogo = readln().toIntOrNull() ?: 2

        var opJogoError = false
        var opTerrenoErro = true
        var dataValidaMenorDeIdade:Boolean? = null
        var fimDeJogo:Boolean? = false

        if (opJogo < 0 || opJogo > 1){
            opJogoError = true
            println("Opcao invalida")
        }else if (opJogo == 1){

            linhas = validaLinhas()!!.toInt()
            colunas = validaColunas()!!.toInt()

            opTerrenoErro = validaTamanhoMapa(linhas, colunas)

            if (opTerrenoErro && linhas == 10 && colunas == 10) {
                do {
                    var error = false
                    println("Qual a sua data de nascimento? (dd-mm-yyyy)")
                    val data = readln()
                    val dataValidaErro = validaDataNascimento(data)
                    if (dataValidaErro != null && dataValidaErro.length <= 14){
                        println(dataValidaErro)
                        error = true
                    }else if (dataValidaErro != null){
                        println(dataValidaErro)
                        dataValidaMenorDeIdade = true
                    }

                }while (error)
            }

            if (opTerrenoErro && dataValidaMenorDeIdade == null) {

                val terrenojogo = leTerrenoDoFicheiro(linhas,colunas)
                fimDeJogo = controloDeJogo(terrenojogo,linhas, colunas)
                if (fimDeJogo == null){
                    return
                }

            }
        }else {
            return
        }

    }while (opJogoError || !opTerrenoErro || dataValidaMenorDeIdade == true || fimDeJogo == true)
}

fun criaMenu(): String{
    return "\nBem vindo ao jogo das tendas\n\n1 - Novo jogo\n0 - Sair\n"
}

fun validaTamanhoMapa(numLinhas:Int, numColunas: Int): Boolean {
    var verf = false

    when  {
        numLinhas == 6 && (numColunas == 5 || numColunas == 6 ) -> verf = true
        numLinhas == 8 && (numColunas == 8 || numColunas == 10) -> verf = true
        numLinhas == 10 && (numColunas == 8 || numColunas == 10) -> verf = true
        else -> println("Terreno invalido")
    }

    return verf
}
fun criaLegendaHorizontal(numColunas: Int): String{
    var count = 0
    val alfabeto = "ABCDEFGHIJ"
    var legendString = ""

    do {
        when (count) {
            0 -> {
                legendString = "${alfabeto[count]} |"
            }
            numColunas - 1 -> {
                legendString += " ${alfabeto[count]}"
            }
            else -> {
                legendString += " ${alfabeto[count]} |"
            }
        }
        count++
    }while (count < numColunas)
    return legendString
}

fun criaTerreno(terreno: Array<Array<String?>>,
                contadoresVerticais: Array<Int?>?,
                contadoresHorizontais: Array<Int?>?,
                mostraLegendaHorizontal: Boolean = true,
                mostraLegendaVertical: Boolean = true):String{

    val numColunas = terreno[0].size
    var terrenoLegendHorizontal = ""
    var contadorHorizontal = ""
    var terrenoJogo = ""



    if (contadoresVerticais != null){
        contadorHorizontal = "       " + criaLegendaContadoresHorizontal(contadoresVerticais) + "\n"
    }

    if (mostraLegendaHorizontal){
        terrenoLegendHorizontal = "     | " + criaLegendaHorizontal(numColunas)+"\n"
    }

    for (numLinha in 0 until terreno.size){

        if (contadoresHorizontais != null && contadoresHorizontais[numLinha] != null){
            terrenoJogo += "${contadoresHorizontais[numLinha]} "
        }else{
            terrenoJogo += "  "
        }

        if (mostraLegendaVertical){
                terrenoJogo += "${numLinha + 1} "

        }else{
            terrenoJogo += "   "
        }

        for (numColuna in 0 until terreno[numLinha].size){

            if (numColuna < terreno[numLinha].size - 1) {
                if (terreno[numLinha][numColuna] == "A"){
                    terrenoJogo += "| △ "
                }else if (terreno[numLinha][numColuna] == null){
                    terrenoJogo += "|   "
                }else {
                    terrenoJogo += "| T "
                }
            }else {
                if(numLinha == terreno.size - 1 && numColuna == terreno[numLinha].size - 1){
                    if (terreno[numLinha][numColuna] == "A"){
                        terrenoJogo += "| △"
                    }else if (terreno[numLinha][numColuna] == null){
                        terrenoJogo += "|  "
                    }else{
                        terrenoJogo += "| T"
                    }
                }else if (terreno[numLinha][numColuna] == "A") {
                    terrenoJogo += "| △\n"
                }else if (terreno[numLinha][numColuna] == null){
                    terrenoJogo += "|  \n"
                }else {
                    terrenoJogo += "| T\n"
                }
            }
        }
    }

    val terrenoCompleto = contadorHorizontal + terrenoLegendHorizontal + terrenoJogo

    return terrenoCompleto
}

fun processaCoordenadas(coordenadasStr: String?, numLines: Int, numColumns: Int): Pair<Int,Int>? {

    val coordenadas:Array<String>? = when {
        coordenadasStr != null && coordenadasStr.length == 3 && coordenadasStr[1] == ',' -> (coordenadasStr.split(",")).toTypedArray()
        else -> null
    }
    var primeiro = 0
    var segundo = 0

    if (coordenadasStr != null && coordenadas != null ) {
        val verfCoordenadasLinha = coordenadas[0].toInt() in 0 .. numLines
        val coordenadaColuna = coordenadas[1].first()
        val verfCoordenadaColuna = ((coordenadaColuna.code - 64) in (0..numColumns))

        if (verfCoordenadaColuna && verfCoordenadasLinha){
            primeiro = coordenadas[0].toInt() - 1
            segundo = (coordenadaColuna.code - 65)
        }else{
            return null
        }

        return Pair(primeiro, segundo)
    }else{
        return null
    }

}

fun validaLinhas():Int?{
    var linhaCorreto:Int?
    do {
        var linhasError = false
        println("Quantas linhas?")
        val linhas = readln().toIntOrNull()
        if (linhas == null || linhas < 1){
            println("Resposta invalida")
            linhasError = true
        }
        linhaCorreto = linhas
    }while (linhasError == true)
    return linhaCorreto
}

fun validaColunas():Int?{
    var colunasCorreto:Int?

    do {
        var colunasError = false
        println("Quantas colunas?")
        val colunas = readln().toIntOrNull()
        if (colunas == null || colunas < 1){
            println("Resposta invalida")
            colunasError = true
        }
        colunasCorreto = colunas
    }while (colunasError == true)

    return colunasCorreto
}


fun validaDataNascimento(data:String?) :String?{

    var validaMSG:String? = null
    val dataInvalid = "Data invalida"
    val menorIdade = "Menor de idade nao pode jogar"
    var eMenorDeIdade = false
    val eAnoBissexto:Boolean

    if (data != null && data.length == 10 && data[2] == '-' && data[5] == '-'){

        val ano: Int? = ("" + data[6] + data[7] + data[8] + data[9]).toIntOrNull()
        val mes: Int? = ("" + data[3] + data[4]).toIntOrNull()
        val dia: Int? = ("" + data[0] + data[1]).toIntOrNull()

        if (ano != null && mes != null && dia != null && ano in 1900..2022 && mes in 0..12 ){

            when{
                mes == 1 || mes == 3 || mes == 5 || mes == 7 || mes == 8 || mes == 10 || mes == 12 -> if (dia > 31 || dia < 0) {
                    validaMSG = dataInvalid

                } else {
                    eMenorDeIdade = validaEMenorIdade(ano, mes)
                }
                mes == 2 -> if (dia < 0 || dia > 29){
                    validaMSG = dataInvalid

                }else{
                    eAnoBissexto = eBissexto(mes, ano)

                    if (!eAnoBissexto && dia > 28){
                        validaMSG = dataInvalid

                    }else {
                        eMenorDeIdade = validaEMenorIdade(ano, mes)
                    }
                }
                else -> if (dia < 0 || dia > 30){
                    validaMSG = dataInvalid

                }else{
                    eMenorDeIdade = validaEMenorIdade(ano, mes)
                }
            }

            if (eMenorDeIdade) {
                validaMSG = menorIdade
            }

        }else{
            validaMSG = dataInvalid
        }

    }else{
        validaMSG = dataInvalid
    }

    return validaMSG
}


fun validaEMenorIdade(ano: Int, mes:Int):Boolean{

    return when {
        ano < 2004 -> false
        (ano == 2004 && mes < 11) -> false
        else -> true
    }
}


fun eBissexto(mes: Int,ano: Int) : Boolean {
    return when {
        (ano % 4 == 0 || ano % 100 == 0) -> true
        ano % 400 == 0 -> true
        else -> false
    }//considera-se que ñ é bissexto pois serve apenas para garantir
    //que o input é válido, logo só vale para fevereiro
}

fun criaLegendaContadoresHorizontal(contadoresHorizontal: Array<Int?>): String{
    var strLegendaHoriz = ""

    for (num in 0 until contadoresHorizontal.size){
        if (num == contadoresHorizontal.size - 1){
            strLegendaHoriz += contadoresHorizontal[num].toString()
        }else if (contadoresHorizontal[num] != null){
            strLegendaHoriz += "${contadoresHorizontal[num]}   "
        }else {
            strLegendaHoriz += "    "
        }
    }

    return strLegendaHoriz
}

fun leContadoresDoFicheiro(numLines: Int, numColumns: Int, verticais: Boolean): Array<Int?>{

    val ficheiro = File("${numLines}x${numColumns}.txt").readLines()

    if (verticais){
        val arrayLegendaVert:Array<Int?> = Array(numColumns){null}
        val stringArrayVertical = ficheiro[0].split(",")
        for (num in 0 until stringArrayVertical.size){
            if (stringArrayVertical[num].toInt() > 0){
                arrayLegendaVert[num] =  stringArrayVertical[num].toIntOrNull()
            }
        }
        return arrayLegendaVert
    }else{
        val arrayLegendaHori:Array<Int?> = Array(numLines){null}

        val stringArrayHorizontal = ficheiro[1].split(",")
        for (num in 0 until stringArrayHorizontal.size){
            if (stringArrayHorizontal[num].toInt() > 0){
                arrayLegendaHori[num] = stringArrayHorizontal[num].toIntOrNull()
            }
        }
        return arrayLegendaHori
    }

}

fun leTerrenoDoFicheiro(numLines: Int, numColumns: Int): Array<Array<String?>>{
    val arrayTerreno:Array<Array<String?>> = Array(numLines) { Array(numColumns) {null} }
    val ficheiro = File("${numLines}x${numColumns}.txt").readLines()
    for (arvorePos in 2 until ficheiro.size){
        val posicoeArvore = ficheiro[arvorePos].split(",")
        val arvoreEmLinha = posicoeArvore[0].toInt()
        val arvoreEmColuna = posicoeArvore[1].toInt()

        arrayTerreno[arvoreEmLinha][arvoreEmColuna] = "A"
    }


    return arrayTerreno
}

fun temArvoreAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>) : Boolean {

    val linhaSize = terreno.size - 1
    val colunaSize = terreno[0].size - 1
    val coordsLinha = coords.first
    val coordsColuna = coords.second
    val verfTemArvore =  when{
        coordsColuna < colunaSize &&  terreno[coordsLinha][coordsColuna + 1] == "A" ->  true
        coordsColuna > 0 && (terreno[coordsLinha][coordsColuna - 1]) == "A" ->  true
        coordsLinha < linhaSize && terreno[coordsLinha + 1][coordsColuna] == "A" ->  true
        coordsLinha > 0 && terreno[coordsLinha - 1][coordsColuna] == "A" ->  true
        else -> false
    }

    return verfTemArvore
}

fun temTendaAdjacente(terreno: Array<Array<String?>>, coords: Pair<Int, Int>) : Boolean{

    val linhaSize = terreno.size - 1
    val colunaSize = terreno[0].size - 1
    val coordsLinha = coords.first
    val coordsColuna = coords.second

    val verfTemTenda = when{
        coordsColuna < colunaSize && terreno[coordsLinha][coordsColuna + 1] == "T" -> true
        coordsColuna > 0 && (terreno[coordsLinha][coordsColuna - 1]) == "T" ->  true
        coordsLinha < linhaSize && terreno[coordsLinha + 1][coordsColuna] == "T" ->  true
        coordsLinha > 0 && terreno[coordsLinha - 1][coordsColuna] == "T" ->  true
        coordsLinha < linhaSize && coordsColuna < colunaSize && (terreno[coordsLinha + 1][coordsColuna + 1]) == "T" -> true
        coordsLinha > 0 && coordsColuna < colunaSize && terreno[coordsLinha - 1][coordsColuna + 1] == "T" -> true
        coordsLinha < linhaSize && coordsColuna > 0 && terreno[coordsLinha + 1][coordsColuna - 1] == "T" -> true
        coordsLinha > 0 && coordsColuna > 0 && terreno[coordsLinha - 1 ][coordsColuna - 1] == "T" -> true
        else -> false
    }

    return verfTemTenda

}

fun contaTendasColuna(terreno:Array<Array<String?>>, coluna: Int): Int{
    var numDeTendas = 0

    if (coluna in 0 until  terreno[0].size){
        for (num in 0 until terreno.size){
            if (terreno[num][coluna] == "T"){
                numDeTendas++
            }
        }
    }

    return numDeTendas
}

fun contaTendasLinha(terreno:Array<Array<String?>>, linha: Int): Int{
    var numDeTendas = 0

    if (linha in 0 until terreno.size) {
        for (num in 0 until terreno[linha].size) {
            if (terreno[linha][num] == "T") {
                numDeTendas++
            }
        }
    }
    return numDeTendas
}

fun colocaTenda(terreno: Array<Array<String?>>, coords: Pair<Int, Int>): Boolean{

    var verfProcessoCorreu = false
    val coordLinha = coords.first
    val coordColuna = coords.second

    if (terreno[coordLinha][coordColuna] == "A" || !temArvoreAdjacente(terreno, coords) || temTendaAdjacente(terreno,coords)){
        return false
    }else if (terreno[coordLinha][coordColuna] == "T"){
        verfProcessoCorreu = true
        terreno[coordLinha][coordColuna] = null

    }else if (terreno[coordLinha][coordColuna] == null) {
        verfProcessoCorreu = true
        terreno[coordLinha][coordColuna] = "T"
    }
    return verfProcessoCorreu
}


fun terminouJogo(terreno: Array<Array<String?>>, contadoresVerticais: Array<Int?>, contadoresHorizontais: Array<Int?>): Boolean{
    var jogoJaAcabou = false
    val verificaTendasLinhaCorreto = Array(contadoresHorizontais.size){false}
    val verificaTendasColunaCorreto = Array(contadoresVerticais.size){false}
    val arr1 = Array(verificaTendasColunaCorreto.size){true}
    val arr2 = Array(verificaTendasLinhaCorreto.size){true}

    for (verticais in 0 until contadoresHorizontais.size){
        val tendasPorLinha = contaTendasLinha(terreno, verticais)

        if (contadoresHorizontais[verticais] == tendasPorLinha){
            verificaTendasLinhaCorreto[verticais] = true
        }else if (contadoresHorizontais[verticais] == null && tendasPorLinha == 0){
            verificaTendasLinhaCorreto[verticais] = true
        }
    }

    for (coluna in 0 until contadoresVerticais.size){
        val tendasPorColuna = contaTendasColuna(terreno, coluna)

        if (contadoresVerticais[coluna] == tendasPorColuna){
            verificaTendasColunaCorreto[coluna] = true
        }else if (contadoresVerticais[coluna] == null && tendasPorColuna == 0){
            verificaTendasColunaCorreto[coluna] = true
        }
    }

    val valoresDeComparacaoColunas = verificaTendasColunaCorreto.contentDeepToString() == (arr1).contentDeepToString()
    val valoresDeComparacaoLinhas = verificaTendasLinhaCorreto.contentDeepToString() == (arr2).contentDeepToString()

    if (valoresDeComparacaoColunas && valoresDeComparacaoLinhas) {
        jogoJaAcabou = true
    }

    return jogoJaAcabou
}

fun controloDeJogo(terreno: Array<Array<String?>>, linhas:Int, colunas: Int):Boolean?{

    val contadoresVerticais = leContadoresDoFicheiro(linhas, colunas, true)
    val contadoresHorizontais = leContadoresDoFicheiro(linhas,colunas,false)
    var stringCoordInfo = "Coordenadas da tenda? (ex: 1,B)\n"

    do {
        println("\n" + criaTerreno(terreno,contadoresVerticais,contadoresHorizontais) )
        var coordenadas:Pair<Int,Int>?
        var coordenadasVerfError = false
        var erroColocaTenda = false

        do {
            print(stringCoordInfo)
            val coordenadasStr:String = readln()

            if (coordenadasStr == "sair" || coordenadasStr == "Sair" || coordenadasStr == "SAIR"){
                return null
            }else{
                coordenadas = processaCoordenadas(coordenadasStr, linhas, colunas)
            }

            if (coordenadas != null){
                coordenadasVerfError = false
                erroColocaTenda = colocaTenda(terreno,coordenadas)
                if (!erroColocaTenda){
                    stringCoordInfo = ("Tenda nao pode ser colocada nestas coordenadas\nCoordenadas da tenda? (ex: 1,B)\n")
                }else{
                    stringCoordInfo = "Coordenadas da tenda? (ex: 1,B)\n"
                }
            }else{
                stringCoordInfo = "Coordenadas invalidas\nCoordenadas da tenda? (ex: 1,B)\n"
                coordenadasVerfError = true
            }

        }while (coordenadasVerfError || !erroColocaTenda)


        val terminou = terminouJogo(terreno,contadoresVerticais,contadoresHorizontais)

        if (terminou){
            println("\n" + criaTerreno(terreno,contadoresVerticais,contadoresHorizontais) )
            print("Parabens! Terminou o jogo!\n")
        }

    }while (!terminou)

    return true
}