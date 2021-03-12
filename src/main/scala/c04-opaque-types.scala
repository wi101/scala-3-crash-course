/**
 * Chapter 4: Opaque types
 */

object AnyValDrawbacks:
    object ValueClasses {
        case class PaymentId(id: String) extends AnyVal
        val paymentId = PaymentId("abc")
        def printName(paymentId: PaymentId) = println(paymentId.id)
        // val somePaymentId: Option[PaymentId] = Some(PaymentId("abc")) // this will box :(
    }
    // More on that: https://failex.blogspot.com/2017/04/the-high-cost-of-anyval-subclasses.html

object OpaqueTypes:
    ???
    /**
     * Example 1: From value classes to Opaque Types.
     */
//    case class FirstName(value: String) extends AnyVal
//    opaque type FirstName >: String <: String = String // specific case of type alias
//
//    /**
//     * Example 2: Defining companion object with constructors and extensions.
//     */
//   object FirstName: // it works only in scope where opaque type is defined
//      def fromString(str: String): FirstName = str
//
//   extension (fn: FirstName)
//      def value: String = fn // in extension methods we cannot use vals
//    
    /**
     * Example 3: Type bounds.
     */

//
//object OpaqueTypesUsage:
//    import OpaqueTypes.*
//    val x: FirstName = FirstName.fromString("hello")
//    val a: FirstName = "hello"

/**
 * Exercises
 */

//
// Exercise 1: Use Opaque types.
//
object OpaqueTypeExercises:
    import java.util.Locale
    opaque type Country = String
    object Country:
        private val validCodes = Locale.getISOCountries
        
//        def apply(code: String): Country = new Country(code)
        
        def fromIso2CountryCode(code: String): Option[Country] = Some(code).filter(validCodes.contains).map(_ => code)
        
        def unsafeFromIso2CountryCode(code: String): Country = fromIso2CountryCode(code)
            .getOrElse(throw new IllegalStateException(s"Cannot parse country from String. Expected country code. Got '$code'."))
        
        val Germany: Country       = "DE"
        val UnitedKingdom: Country = "GB"
    extension (country: Country) def code: String = country

@main def opaqueTypeExercisesMain =
    import OpaqueTypeExercises._
    val country: Option[Country] = Country.fromIso2CountryCode("DE")
    println(country)
    println(country.map(_.code))

