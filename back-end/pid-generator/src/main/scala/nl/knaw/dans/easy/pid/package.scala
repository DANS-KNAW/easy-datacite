package nl.knaw.dans.easy

import Math.pow

package object pid {
  val urnPrefix = "urn:nbn:nl:ui:13-"
  val urnRadix = 36
  val doiPrefix = "10.17026/dans-"
  val doiRadix = 36 - "01il".length

  var currentUrnSeed: Long = 1
  var currentDoiSeed: Long = 2

  // see http://en.wikipedia.org/wiki/Linear_congruential_generator
  def getNextPidNumber(seed: Long): Long = {
    val factor = 3 * 7 * 11 * 13 * 23 // = 69069
    val increment = 5
    val modulo = pow(2, 31).toLong
    (seed * factor + increment) % modulo
  }

  def formatUrn(pid: Long): String = urnPrefix + putDashAt(convertToString(pid, urnRadix, 6), 4)

  def formatDoi(pid: Long): String = doiPrefix + putDashAt(convertToString(pid, doiRadix, 7), 3)

  def convertToString(pid: Long, radix: Int, length: Int) = {
    def padWithZeroes(s: String) = String.format(s"%${length}s", s).replace(' ', '0')
    padWithZeroes(java.lang.Long.toString(pid, radix).toLowerCase)
  }

  def putDashAt(s: String, i: Int) = s.substring(0, i) + "-" + s.substring(i)

  def getNextUrn(): String = {
    currentUrnSeed = getNextPidNumber(currentUrnSeed)
    formatUrn(currentUrnSeed)
  }

  def getNextDoi(): String = {
    currentDoiSeed = getNextPidNumber(currentDoiSeed)
    formatDoi(currentDoiSeed)
  }
}