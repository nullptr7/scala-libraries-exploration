package com.github.nullptr7

private object NewTypeExample extends App {
  /*import shapeless._
  import shapeless.tag._

  sealed trait Recipient
  sealed trait Body

  case class Message(recipient: String @@ Recipient, body: String @@ Body)

  val recipient: String @@ Recipient = tag[Recipient]("@user")
  val body:      String @@ Body      = tag[Body]("A Body")*/
  import eu.timepit.refined.api._
  import eu.timepit.refined.collection._
  import eu.timepit.refined.string._
  import eu.timepit.refined.boolean._
  import eu.timepit.refined._

  private type RecipientRules = (StartsWith["@"] Or StartsWith["#"]) And MinSize[2]
  private type BodyRules      = NonEmpty
  private type Recipient      = String Refined RecipientRules
  private type Body           = String Refined BodyRules

  case class Message(recipient: Recipient, body: Body)

  import eu.timepit.refined.auto._

  /*val recipient1: Recipient = "@valid"
  val recipient2: Recipient = "#valid"
  val recipient3: Recipient = "invalid" // won't compile - success!
  val recipient4: Recipient = ""  // won’t compile - success!
  val recipient5: Recipient = "@" // won’t compile - success!*/

//  val recipient: Recipient = "@marcin"
//  val body:      Body      = "How are you today?"

//  Message(recipient, body)
//  Message(body, recipient) // won't compile - success!
//  Message("How are you?", "@marcin") // won't compile but only because "How are you?" does not meet Recipient's requirements.
//  Message("@greg is very clever!", "@marcin") // compiles, because the message body meets refined type requirements for Recipient

  def parseMessage(rawRecipient: String, rawBody: String): Either[String, Message] =
    for {
      recipient <- refineV[RecipientRules](rawRecipient)
      body      <- refineV[BodyRules](rawBody)
    } yield Message(recipient, body)


  println(parseMessage("@marcin", "How are you?"))

}
