package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    protected lateinit var equation : TextView
    protected lateinit var result : TextView
    protected val operaciones = listOf("x", "/", "+", "-")
    protected var operacionDisponible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val zeroButton = findViewById<Button>(R.id.zero_button)
        val oneButton = findViewById<Button>(R.id.one_button)
        val twoButton = findViewById<Button>(R.id.two_button)
        val threeButton = findViewById<Button>(R.id.three_button)
        val fourButton = findViewById<Button>(R.id.four_button)
        val fiveButton = findViewById<Button>(R.id.five_button)
        val sixButton = findViewById<Button>(R.id.six_button)
        val sevenButton = findViewById<Button>(R.id.seven_button)
        val eightButton = findViewById<Button>(R.id.eight_button)
        val nineButton = findViewById<Button>(R.id.nine_button)

        val clearButton = findViewById<Button>(R.id.clear_button)
        val equalsButton = findViewById<Button>(R.id.equals_button)
        val pointButton = findViewById<Button>(R.id.point_button)
        val divideButton = findViewById<Button>(R.id.divide_button)
        val multiplyButton = findViewById<Button>(R.id.multiply_button)
        val additionButton = findViewById<Button>(R.id.addition_button)
        val subtractButton = findViewById<Button>(R.id.subtract_button)

        this.result = findViewById<TextView>(R.id.result_view)
        this.equation = findViewById<TextView>(R.id.equation_view)


        pointButton.setOnClickListener {
            concatenarNumeros(pointButton.text.toString())
            this.operacionDisponible = false
        }

        zeroButton.setOnClickListener {
            concatenarNumeros(zeroButton.text.toString())
        }
        oneButton.setOnClickListener {
            concatenarNumeros(oneButton.text.toString())
        }
        twoButton.setOnClickListener {
            concatenarNumeros(twoButton.text.toString())
        }
        threeButton.setOnClickListener {
            concatenarNumeros(threeButton.text.toString())
        }
        fourButton.setOnClickListener {
            concatenarNumeros(fourButton.text.toString())
        }
        fiveButton.setOnClickListener {
            concatenarNumeros(fiveButton.text.toString())
        }
        sixButton.setOnClickListener {
            concatenarNumeros(sixButton.text.toString())
        }
        sevenButton.setOnClickListener {
            concatenarNumeros(sevenButton.text.toString())
        }
        eightButton.setOnClickListener {
            concatenarNumeros(eightButton.text.toString())
        }
        nineButton.setOnClickListener {
            concatenarNumeros(nineButton.text.toString())
        }

        clearButton.setOnClickListener {
            limpiarEquation()
        }

        equalsButton.setOnClickListener {
            prueba()
        }

        //4 operaciones
        additionButton.setOnClickListener {
            if (this.operacionDisponible) {
                if (this.equation.text == "") {
                    this.equation.text = this.result.text
                }
                concatenarNumeros(additionButton.text.toString())
                this.operacionDisponible = false

            }
        }
        subtractButton.setOnClickListener {
            if (this.equation.text == "") {
                this.equation.text = this.result.text
            }
            if (this.operacionDisponible) {
                concatenarNumeros(subtractButton.text.toString())
                this.operacionDisponible = false
            }
        }
        multiplyButton.setOnClickListener {
            if (this.equation.text == "") {
                this.equation.text = this.result.text
            }
            if (this.operacionDisponible) {
                concatenarNumeros(multiplyButton.text.toString())
                this.operacionDisponible = false
            }
        }
        divideButton.setOnClickListener {
            if (this.equation.text == "") {
                this.equation.text = this.result.text
            }
            if (this.operacionDisponible) {
                concatenarNumeros(divideButton.text.toString())
                this.operacionDisponible = false
            }
        }
    }

    fun concatenarNumeros(digito:String){
        this.equation.text = "${this.equation.text}${digito.lowercase()}"
        this.operacionDisponible = true
    }

    fun limpiarEquation(){
        this.equation.text = ""
        this.result.text = "0"
    }

    fun prueba(){
        var listaPrueba = this.equation.text.split("").toMutableList()
        listaPrueba.removeAt(0)
        listaPrueba.removeAt(listaPrueba.size - 1)
        // Ya quedó [1, 2, 3, +, 4, 5, 6]
        var nuevaLista = mutableListOf<String>()
        var contador = 1
        var numero = ""
        for (i in listaPrueba.indices){
            if (listaPrueba[i] in this.operaciones){
                nuevaLista.add(listaPrueba[i])
                continue
            }
            if ((i == listaPrueba.size - 1) || (listaPrueba[i + 1] in this.operaciones)){
                for (j in i-contador+1..i){
                    numero = "$numero${listaPrueba[j]}"
                }
                nuevaLista.add(numero)
                numero = ""
                contador = 1
            } else {
                contador ++
            }
        }
        // Ya tengo la lista [123, +, 456]
        var stack = ArrayDeque<String>()
        var listaRes = mutableListOf<String>()
        for (i in nuevaLista.indices){
            if (nuevaLista[i] in this.operaciones){
                if (stack.isEmpty()){
                    stack.add(nuevaLista[i])
                } else {
                    val ordenNuevoOp = ordenOperador(nuevaLista[i])
                    val ordenTope = ordenOperador(stack.last())
                    if (ordenTope <= ordenNuevoOp){
                        stack.add(nuevaLista[i])
                    } else {
                        ordenarOperadores(nuevaLista[i], stack, listaRes)
                        println("Lista: $listaRes \nStack: $stack")
                    }
                }
            } else {
                listaRes.add(nuevaLista[i])
            }
        }
        while (!stack.isEmpty()){
            listaRes.add(stack.removeLast())
        }
        // Ya tengo [10, 2, 3, *, +]
        var ultimaLista = mutableListOf<String>()
        for (i in listaRes.indices){
            if (!(listaRes[i] in this.operaciones)){
                ultimaLista.add(listaRes[i])
            } else {
                val ultimaPos = ultimaLista.size - 1
                val n1 = ultimaLista[ultimaPos - 1].toFloat()
                val n2 = ultimaLista[ultimaPos].toFloat()
                ultimaLista[ultimaPos-1] = realizarOperacion(n1, n2, listaRes[i])
                ultimaLista.removeAt(ultimaPos)
            }
        }
        val resultado = ultimaLista[0]
        this.result.text = "$resultado"
        this.equation.text = ""
    }

    fun armarTermino(digitos:String){ // 2 + (2*3) - 1  ->  +2*3 | +2 | -1
        // [+2, +6, -1]

    }

    fun ordenarOperadores(nuevoOperador:String, stack:ArrayDeque<String>, listaRes:MutableList<String>){
        var operadorTope = stack.last()
        while (operadorTope > nuevoOperador){
            listaRes.add(stack.removeLast())
            if (stack.isEmpty()){
                stack.add(nuevoOperador)
                break
            }
            operadorTope = stack.last()

        }

    }

    fun realizarOperacion(n1:Float, n2:Float, operador:String): String {
        var resultado: Float = 0F
        when(operador) {
            "+" -> resultado = n1 + n2
            "-" -> resultado = n1 - n2
            "x" -> resultado = n1 * n2
            "/" -> resultado = n1 / n2
        }
        if ((resultado % 1) == 0F){
            println("Resultado: $resultado")
            return resultado.toInt().toString()
        }
        return resultado.toString()
    }

    fun ordenOperador(operador: String): Int {
        val orden_operadores = listOf("-", "+", "/", "x")
        return (orden_operadores.indexOf(operador))


    }
}
