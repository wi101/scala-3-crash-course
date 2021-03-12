/**
 * Chapter 3: Contextual abstractions (a.k.a. implicits we all love(d) in Scala 2).
 * 
 *
 * The table below presents contextual language features of Scala 3 and how they relate to Scala 2.
 * 
 * +----------------------------------------------------------+------------------------------------------+-------------------------------------------+
 * |                   Contextual feature                     |        Implementation in Scala 2         |         Implementation in Scala 3         |
 * +----------------------------------------------------------+------------------------------------------+-------------------------------------------+
 * | implicit parameters                                      | implicit val                             | given/using clauses                       |
 * | extension methods (adding methods to existing types)     | implicit class                           | extension clause                          |
 * | implicit conversion                                      | implicit def                             | Conversion[From, To] type class           | 
 * | type classes                                             | trait + implicit object + implicit class | trait + given/using + ext. method(s)      |
 * | context functions (formerly known as implicit functions) | -                                        | context functions                         |
 * |----------------------------------------------------------+------------------------------------------+-------------------------------------------+
 * 
 * Why implicits deprecated in favor of distinct Contextual Abstractions: https://dotty.epfl.ch/docs/reference/contextual/motivation.html
 * Relationship between Scala 3 contextual features and Scala 2 implicits: http://dotty.epfl.ch/docs/reference/contextual/relationship-implicits.html
 * Note: `implicit` keyword will be supported only until the last release of Scala 3.1.x.
*/

object ImplicitConfusion:
    implicit def fun1(baz: Int): String = ??? // Implicit conversion 🙈
    def fun2(implicit bar: String): String = ??? // Function, that requires implicit parameter to run.
    implicit def fun3(implicit foo: String): String = ??? // Type class instance, that requires another type class instance. Think of Encoder[List[A]].
    implicit class Class(val foo: String) {} // Implicit class a.k.a. syntax

/**
 * Chapter 3.1: Implicit parameters a.k.a. term inference
 * [] Given/using
 * [] Given/using and implicit can be used interchangibly
 * [] Term inference (the compiler will lookup the defined value)
 */
object ImplicitParams:
    import scala.concurrent.ExecutionContext
    import scala.concurrent.Future
    // definition: instead of implicit --> given without val
    given executionContext: ExecutionContext = ExecutionContext.parasitic
    //usage: instead of implicit --> using 
    def sendPostRequest(url: String)(using ec: ExecutionContext): Future[String] = Future.unit.map(_ => "OK")
    sendPostRequest("https://moia.io")

/**
 * Chapter 3.2: Extension methods
 * [] New extension syntax
 * [] Type parameters in the new syntax
 */
@main def extensionMethods =
// instead of implicit class --> extension without naming it
    extension (value: String)
        def times(times: Int): Seq[String] = (1 to times).map(_ => value)
        def unit: Unit = ()

    println("a".times(20).toList)
    println("a".times(20).toList)

/**
 * Exercises: extension methods
 */
@main def extensionMethodsExercises = 
    // Exercise 1: Extend java.time.Instant with a method
    // calculating the duration between two instants: def -(until: Instant): java.time.Duration
    // Hint: You can use java.time.Duration.between(one, two).
    import java.time._

    // The following should compile:
      extension (value: Instant)
          def -(until: Instant): java.time.Duration = java.time.Duration.ofMillis(until.toEpochMilli - value.toEpochMilli)
     
    println(Instant.now() - Instant.now())

   // Exercise 2: Implement def +(thatTuple: Tuple2[A, B]) function on the tuple2 
   // Hint: Use the Numeric type class (https://www.scala-lang.org/api/current/scala/math/Numeric.html).
    
    // The following should compile:
    extension [A, B](tuple: Tuple2[A, B])(using n1: Numeric[A], n2: Numeric[B])
        def +(thatTuple: Tuple2[A, B]) = (n1.plus(tuple._1, thatTuple._1), n2.plus(tuple._2, thatTuple._2))

    println((2, 2.1) + (3, 4.0))

/**
 * Chapter 3.3: Type classes
 * [] Type Classes in Scala 3
 * [] implicitly => summon
 */
object Typeclasses:
    case class Data(field: String)
    
    // 1. Type class declaration
    trait Show[A]:
        extension (a: A) def show: String

    // 2. Type class instance for the custom data type
  // instead of = new Show[Data] --> with
    given dataShow: Show[Data] with // or implicit object...
        extension (a: Data) def show: String = a.toString
  
    // 3. Interface w/ summoner
    object Show:
        def show[A](a: A)(using ev: Show[A]): String = ev.show(a)

    // 4. Syntax	   
//    extension (a: A)(using ev: Show[A])
//        def show: String = ev.show(a)	
//    
    // 5. Usage
    def usage[A: Show](a: A) = a.show

    // 6. Derivation

/**	
 * Exercises: type classes	
 */	
object TypeclassesExercises:	
    // Exercise 1. For the following Monad type class declaration implement:	
    // - instance for Option	
    // - syntax	
    trait Monad[F[_]]: 	
      extension[A] (fa: F[A])
          def bind[B](f: A => F[B]): F[B]
      extension[A] (a: A)
          def unit: F[A]
    ???


/**
 * Chapter 3.4: Implicit conversions
 */
object ImplicitConversions:
//    implicit def booleanToString(input: Boolean): String = input.toString

    given Conversion[Boolean, String] with 
        def apply(input: Boolean): String = input.toString
  
    def identity(input: String): String = input
    identity(true)
    val x: String = false

/**
 * Chapter 3.5: Importing givens & extension methods
 * [] _ is replaced by *
 * [] givens must be explicitly imported
 * [] import by given's type
 * [] import by given's name
 * [] import all givens
 * [] import aliases
 */
object Instances:
    given x: String = "hello world"
    given y: Int = 42
    val test: Int = 42
    extension (str: String) def to42: Int = 42
    given Conversion[Boolean, String] with 
        def apply(input: Boolean): String = input.toString
object Scoping:
//     import Instances.given // if you want to import given 
//     import Instances.{given: String} // by type
//     import Instances.x // by name
//     summon[String] // implicitly 
//     "a".to42
//     val str: String = false
    ???
end Scoping
/**
 * Chapter 3.6: Context functions
 * 
 * https://www.scala-lang.org/blog/2016/12/07/implicit-function-types.html
 */
object ContextFunctions:
    import scala.concurrent.{Future, ExecutionContext}
    import concurrent.ExecutionContext.Implicits.global

    case class TracingContext(traceId: String)

    type WithContext[A] = TracingContext ?=> A // implicit TracingContext => A
    object WithContext:
        def traceId: TracingContext ?=> String = summon[TracingContext].traceId
  
//    def createUser(userData: String)(using TracingContext): Future[Unit] = 
    def createUser(userData: String): WithContext[Future[Unit]] = 
    for {
        _ <- validateUser(userData)
        _ = WithContext.traceId
        _ <- insertUser(userData)
    } yield ()
      
    def validateUser(userData: String):WithContext[Future[Unit]]  = ???
    def insertUser(userData: String): WithContext[Future[Unit]]  = ???
