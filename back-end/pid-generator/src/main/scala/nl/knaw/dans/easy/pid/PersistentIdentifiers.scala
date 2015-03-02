package nl.knaw.dans.easy.pid

import org.scalatra._
import org.scalatra.scalate.ScalateSupport
import org.slf4j._


class PersistentIdentifiers extends ScalatraServlet with ScalateSupport {
  val log = LoggerFactory.getLogger(getClass)

  get("/") {
    Ok("Persistent Identifier Generator running")
  }
  
  post("/*") {
    BadRequest("Cannot create PIDs at this URI")
  }

  post("/") {
    params.get("type") match {
      case Some(pidType) => pidType match {
        case "urn" => Ok(getNextUrn())
        case "doi" => Ok(getNextDoi())
      }
      case None => Ok(getNextDoi())
    }
  }
}