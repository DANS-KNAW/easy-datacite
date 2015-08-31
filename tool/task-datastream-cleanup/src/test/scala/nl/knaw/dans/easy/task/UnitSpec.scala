package nl.knaw.dans.easy.task

import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers with
  OptionValues with Inside with Inspectors with OneInstancePerTest with BeforeAndAfter
