/**
 * Chapter 5: Union and intersection types
 */

object UnionTypes:
    /**
     * Example 1: Basics
     * [] Union type
     * [] Commutativity
     * [] Variance
     * [] Union types vs either
     */

    // definition
//    val x: Boolean = true
    val x: Boolean | String | Int = 42

    // usage
    def f(in: Boolean | String | Int): String = 
        in match
            case _: Boolean => "bool"
            case _: String => "string"
            case _: Int => "int"

    f(x)
  
     val xx: List[Int] | List[String] = ??? //  it's the same as:  List[Int | String] = ???
    
    // How many values does type `Either[Boolean, Boolean]` have? == 4 
    // How many values does type `Boolean | Boolean` have? == 2 



    /**
     * Example 2: What type will be inferred?
     * [] Can we improve inference with union types?
     */
    val what: String | Int = if 1==1 then "a" else 10

    /**
     * Example 3: Possible use case: union types and eithers
     */
    enum FooError:
        case FooError1
        case FooError2

    enum BarError:
        case BarError1
        case BarError2

    val error1: Either[FooError, String] = Left(FooError.FooError1)
    val error2: Either[BarError, String] = Left(BarError.BarError1)

    val value: Either[FooError | BarError, Unit] = for 
        _ <- error1
        _ <- error2
    yield ()

//
// Exercise 1: Model a PaymentAuthorizationError ADT from Chapter 2 (enums) using a union type. 
// What pros/cons do you see when you use a union type vs enums in modeling ADTs?
//
// enum PaymentAuthorizationError(retriable: Boolean):
//        case IllegalPaymentStatus(existingPaymentId: PaymentId, existingPaymentStatus: PaymentStatus) extends PaymentAuthorizationError(retriable = false)
//        case IllegalRequestData(reason: String) extends PaymentAuthorizationError(retriable = false)
//        case CustomerUnknown(unknownCustomerId: CustomerId) extends PaymentAuthorizationError(retriable = false)
//        case InvalidToken(invalidToken: Token) extends PaymentAuthorizationError(retriable = true)

    case class IllegalPaymentStatus(existingPaymentId: Int, existingPaymentStatus: String, retriable: Boolean)
    case class IllegalRequestData(reason: String, retriable: Boolean)
    type PaymentAuthorizationError = IllegalPaymentStatus | IllegalRequestData


object IntersectionTypes:
    /**
     * Example 1: Basics
     * [] Example
     * [] Subtyping
     * [] Variance
     * [] Definition
     */
    
    // replaces compounds type: TypeA with TypeB with TypeC
    object Example1:
        trait A:
            def foo: String

        trait B:
            def bar: Int
        
        def x: A & B = new A with B {
            override def foo: String = ???
            override def bar: Int = ???
        }
        x.foo
        x.bar    
  
        val a: A = x
        val b: B = x
        val y: List[A] & List[B] = ???
        val yy: List[A & B] = ???

    /**
     * Example 2: Conflicting members
     * [] Same name different type
     * [] Same name same type
     */
    object Example21:
        trait A:
            def foo: String
        trait B:
            def foo: Int

        def x: A & B = new A with B {
            override def foo: String & Int = ??? // WOW!
        }
      
        x.foo
  

    object Example22:
        trait A:
            def foo: Boolean
        trait B:
            def foo: Boolean
        
        def x: A & B = ???


    /**
     * Example 3: Intersection types vs compound types (a.k.a. `with` types from Scala 2)
     * [] With vs &
     * [] Commutativity
     */
    trait Foo:
        def f: AnyVal
    trait A extends Foo:
        override def f: Boolean
    trait B extends Foo:
        override def f: AnyVal
    
// in scala 2
// 
