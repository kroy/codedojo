/*
*	Exercises from the book "Scala for the Impatient" starting from Chapter 3 onward
*/

import scala.util.Random
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer

import java.awt.datatransfer._
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.mapAsScalaMap

/***** Chapter 3 *****/

def ch3ex1(n: Int = 10) {
	val r: Random = new Random()
	val a = (for(i <- 0 until n) yield r.nextInt(n)).toArray
	println(a.mkString(","))
}

def ch3ex2(a: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7)) {
	for(i <- 0 until a.length if i % 2 == 1){
		val t = a(i)
		a(i) = a(i-1)
		a(i-1) = t
	}
	println(a.mkString(","))
}

def ch3ex3(a: Array[Int] = Array(1, 2, 3, 4, 5, 6, 7)) {
	val b = {for(i <- 0 until a.length) yield {
		if(i % 2 == 1) a(i-1)		//looking at an odd index, so we want the element before
		else if(i == a.length-1) a(i)	//looking at an even index at the end of the array, so just want this one
		else a(i+1)				//looking at an even index somewhere in the middle of the arr
	}}.toArray

	println(b.mkString(","))
}

def ch3ex4(a: Array[Int] = Array(0, 1, -1, 2, -2, 3, -3, 4, -4)) {
	val pos = new ArrayBuffer[Int]
	val nonPos = new ArrayBuffer[Int]
	for(el <- a) if(el > 0) pos+=el else nonPos+= el
	val b = pos ++ nonPos
	println(b.mkString(","))
}

def ch3ex5(a: Array[Double] = Array(1.0, 2.0, 3.0, 4.0)){
	val avg = a.sum/a.length
	println("The average of the numbers " + a.mkString(",") + " is: " + avg)
}

def ch3ex6a(a: Array[Int] = Array(10, 11, 9, 4, 5, 6)){
	scala.util.Sorting.quickSort(a)
	for(i <- 0 until a.length/2){
		val t = a(i)
		a(i) = a(a.length - 1 - i)
		a(a.length -1 - i) = t
	}
	println(a.mkString(","))
}

def ch3ex7(a: Array[Any] = Array(1, 3, 1, 1, "turkey", "salad", "turkey")) {
	val b = a.distinct
	println(b.mkString(","))
}

def ch3ex8(a: ArrayBuffer[Int] = ArrayBuffer(1, 2, 3, -1, 2, -2, 4, -4)) {
	var negs = for(i <- 0 until a.length if a(i) < 0) yield i
	negs = negs.tail.reverse
	for(i <- negs) a.remove(i)
	println(a.mkString(","))
}

def ch3ex9() {
	var zones = java.util.TimeZone.getAvailableIDs.filter(_.containsSlice("America/"))
	zones = for(zone <- zones) yield zone.drop(8)
	scala.util.Sorting.quickSort(zones)
	println(zones.mkString(","))
}

def ch3ex10() {
	val flavors = SystemFlavorMap.getDefaultFlavorMap().asInstanceOf[SystemFlavorMap]
	val flava: Buffer[String] = flavors.getNativesForFlavor(DataFlavor.imageFlavor)
	println(flava.mkString)
}

/***** Chapter 4 *****/

def ch4ex1() {
	val stuff = Map("4K Display"-> 600.0, "Gaming Rig" -> 1400.0, "Table" -> 200.0, "Travel" -> 2000.0)
	val discounted = for ((k,v) <- stuff) yield (k, v * .9)
	println(discounted.mkString)
}

def ch4ex2() {
	val words = scala.collection.mutable.Map[String, Int]()
	val in = new java.util.Scanner(new java.io.File("test.in"))
	while (in.hasNext()) {
		val word = in.next()
		words(word) = words.getOrElse(word, 0) + 1
	}
	println(words.mkString("\n"))
}

def ch4ex3() {
	var words = Map[String, Int]()
	val in = new java.util.Scanner(new java.io.File("test.in"))
	while (in.hasNext()){
		val word = in.next()
		val num = words.getOrElse(word, 0) + 1
		words = words + (word -> (words.getOrElse(word, 0) + 1))
	}
	println(words.mkString("\n"))
}

def ch4ex4() {
	var words = scala.collection.immutable.SortedMap[String, Int]()
	val in = new java.util.Scanner(new java.io.File("test.in"))
	while (in.hasNext()){
		val word = in.next()
		val num = words.getOrElse(word, 0) + 1
		words = words + (word -> (words.getOrElse(word, 0) + 1))
	}
	println(words.mkString("\n"))
}

def ch4ex5() {
	val words: scala.collection.mutable.Map[String,Int] = 
		new java.util.TreeMap[String, Int]
	val in = new java.util.Scanner(new java.io.File("test.in"))
	while (in.hasNext()) {
		val word = in.next()
		words(word) = words.getOrElse(word, 0) + 1
	}
	println(words.mkString("\n"))
}

def ch4ex6() {
	val days = scala.collection.mutable.LinkedHashMap[String, Int]()
	days += ("Monday" -> java.util.Calendar.MONDAY)
	days += ("Thursday" -> java.util.Calendar.THURSDAY)
	days += ("Tuesday" -> java.util.Calendar.TUESDAY)
	for((k,v) <- days) println("The day is: " + k + " and the int is: " + v)
} 

def ch4ex7() {

}

def ch4ex8(a: Array[Int] = Array(-10, 1, 2, 4, 100, 3, 5, 6)) {
	val (min, max) = minmax(a)
	println("Min: " + min + ", Max: " + max)
}

def minmax(values: Array[Int]): (Int, Int) = {
	(values.min, values.max)
}

def ch4ex9(a: Array[Int] = Array(-10, 1, 2, 4, 100, 3, 5, 6), v: Int = 3) {
	val (lt, eq, gt) = lteqgt(a, v)
	println("Number of elements less than: " + lt + ", num equal: " + 
		eq + ", num gt: " + gt)
}

def lteqgt(values: Array[Int], v: Int): (Int, Int, Int) = {
	(values.count(_ < v), values.count(_ == v), values.count(_ > v))
}

/***** Exercise to execute *****/

ch4ex9()