package nl.knaw.dans.easy.lib

import org.scalatest.matchers._
import org.scalatest.words.ResultOfATypeInvocation

import scala.util.{Success, Failure, Try}

/** See also <a href="http://www.scalatest.org/user_guide/using_matchers#usingCustomMatchers">CustomMatchers</a> */
trait CustomMatchers {

  class ExceptionMatcher(expectedException: ResultOfATypeInvocation[_], expectedMessage: String*) extends Matcher[Try[Any]] {

    def apply(left: Try[Any]) = {

      val expected = s"$expectedException having a message matching [$expectedMessage]"
      val thrown = left match { case Failure(e) => e case _=>""}
      def failureMessage = left match {
        case Failure(e) => s"failed with $e"//string includes message
        case Success(_) => "succeeded"
      }
      def actualMessage = Option(thrown.asInstanceOf[Throwable].getMessage) match{case Some(msg)=>msg case _=>""}
      val containsMessageFragments: Seq[Boolean] = for {m<-expectedMessage} yield {actualMessage.lastIndexOf(m)>=0}
      MatchResult(
        // TODO replace == by something like instanceOf
        thrown.getClass == expectedException.clazz && (!containsMessageFragments.contains(false)),
        s"did not fail with $expected but $failureMessage",
        s"failed with $expected"
      )
    }
  }
  /** usage example: Try[Any] should failWith (a[Throwable],"some message fragment", "another fragment") */
  def failWith(exception: ResultOfATypeInvocation[_], messageFragment: String*) = new ExceptionMatcher(exception, messageFragment: _*)
}

// Make them easy to import with:
// import CustomMatchers._
object CustomMatchers extends CustomMatchers
