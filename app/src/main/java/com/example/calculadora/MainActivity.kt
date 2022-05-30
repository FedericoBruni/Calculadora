package com.example.calculadora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.math.BigDecimal
import java.math.RoundingMode

class MainActivity : AppCompatActivity() {
    protected lateinit var equation : TextView
    protected lateinit var result : TextView
    protected val operaciones = listOf("x", "/", "+", "-")
    protected var operacionDisponible = true
    protected var puntoDisponible = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.setViewsAndListeners()

    }

    private fun setViewsAndListeners(){
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

        var botones = mutableListOf<Button>(zeroButton, oneButton, twoButton, threeButton, fourButton, fiveButton, sixButton,
            sevenButton, eightButton, nineButton)

//        this.operaciones = listOf(additionButton, subtractButton, multiplyButton, divideButton)

        var botonesOperaciones = mutableListOf<Button>(divideButton, multiplyButton, additionButton, subtractButton)
        for (boton in botonesOperaciones){
            boton.setOnClickListener {
                if (this.operacionDisponible) {
                    if (this.equation.text == "") {
                        this.equation.text = this.result.text
                    }
                    concatenarNumeros(boton.text.toString())
                    this.operacionDisponible = false
                    this.puntoDisponible = true
                }
            }
        }

        for (boton in botones){
            boton.setOnClickListener {
                concatenarNumeros(boton.text.toString())
                if (boton == pointButton){
                    this.operacionDisponible = false
                }
            }
        }

        pointButton.setOnClickListener {
            if (this.puntoDisponible) {
                concatenarNumeros(pointButton.text.toString())
                this.operacionDisponible = false
                this.puntoDisponible = false
            }
        }

        //CLEAR
        clearButton.setOnClickListener {
            limpiarEquation()
            this.puntoDisponible = true
            this.operacionDisponible = true
        }

        equalsButton.setOnClickListener {
            if (this.operacionDisponible && this.equation.text != ""){
                prueba()
            }
            this.puntoDisponible = true

        }
    }

    fun concatenarNumeros(digito:String){
        this.equation.text = "${this.equation.text}${digito.lowercase()}"
        this.operacionDisponible = true
    }

    fun limpiarEquation(){
        this.equation.text = ""
        this.result.text = ""
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
            if ((listaPrueba[i] in this.operaciones) && (i != 0)){
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
        println("Lista antes: $nuevaLista")
        for (i in nuevaLista.indices){
            if (nuevaLista[i] in this.operaciones){
                if (stack.isEmpty()){
                    stack.add(nuevaLista[i])
                } else {
                    val ordenNuevoOp = ordenOperador(nuevaLista[i])
                    val ordenTope = ordenOperador(stack.last())
                    if (ordenTope <  ordenNuevoOp){
                        stack.add(nuevaLista[i])
                    } else {
                        ordenarOperadores(nuevaLista[i], stack, listaRes)
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
        println("Lista depois: $listaRes")
        var ultimaLista = mutableListOf<String>()
        for (i in listaRes.indices){
            if (!(listaRes[i] in this.operaciones)){
                ultimaLista.add(listaRes[i])
            } else {
                val ultimaPos = ultimaLista.size - 1
                val n1 = ultimaLista[ultimaPos - 1].toBigDecimal()
                val n2 = ultimaLista[ultimaPos].toBigDecimal()
                println("N1: $n1 \nN2: $n2")
                ultimaLista[ultimaPos-1] = realizarOperacion(n1, n2, listaRes[i])
                ultimaLista.removeAt(ultimaPos)
            }
        }
        val resultado = ultimaLista[0]
        this.result.text = "$resultado"
        this.equation.text = ""
    }

    fun ordenarOperadores(nuevoOperador:String, stack:ArrayDeque<String>, listaRes:MutableList<String>){
        var ordenOperadorTope = ordenOperador(stack.last())
        var ordenOperadorNuevo = ordenOperador(nuevoOperador)
        while (ordenOperadorTope >= ordenOperadorNuevo){
            listaRes.add(stack.removeLast())
            if (stack.isEmpty()){
                stack.add(nuevoOperador)
                return
            }
            ordenOperadorTope = ordenOperador(stack.last())
        }
//        if (ordenOperadorTope < ordenOperadorNuevo) stack.add(nuevoOperador)
//        if (ordenOperadorTope == ordenOperadorNuevo) listaRes.add(nuevoOperador)
    }

    fun realizarOperacion(n1:BigDecimal, n2:BigDecimal, operador:String): String {
        lateinit var resultado: BigDecimal
        when(operador) {
            "+" -> resultado = n1.add(n2)
            "-" -> resultado = n1.subtract(n2)
            "x" -> resultado = n1.multiply(n2)
            "/" -> resultado = n1.divide(n2, 10, RoundingMode.HALF_UP)
        }
        return removerCerosIzquierda(removerCerosDerecha(resultado.toString()))
    }

    fun removerCerosIzquierda(num:String) : String{
        val indicePunto = num.indexOf(".")
        if (indicePunto == -1) return num
        var contador = 0
        for (i in 0..indicePunto){
            if (num[i] != '0'){
                break
            }
            contador = i
        }
        return num.removeRange(0..contador-1)
    }

    fun removerCerosDerecha(num:String) : String {
        val indicePunto = num.indexOf(".")
        if (indicePunto == -1) return num
        val len = num.length
        var contador = 0
        for (i in len-1 downTo indicePunto){
            if (num[i] != '0'){
                break
            }
            contador++
        }
        if (contador == 0) return num
        if (num[indicePunto + 1] != '0') contador --
        return num.removeRange(len-contador-1..len-1)
    }

    fun ordenOperador(operador: String): Int {
//        val orden_operadores = listOf("-", "+", "/", "x")
//        return (orden_operadores.indexOf(operador))
        when (operador){
            "-" -> return 0
            "+" -> return 0
            "/" -> return 1
            "x" -> return 2
        }
        return -1
    }
}
