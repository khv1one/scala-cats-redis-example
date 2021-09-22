package example.daemon.errors

abstract class CustomErrors(message: String) extends Exception(message) {
  def isRecoverable: Boolean
}

object CustomErrors {
  case class RecoverableError() extends CustomErrors("Custom Recoverable Error") {
    override val isRecoverable = true
  }

  case class NotRecoverableError() extends CustomErrors("Custom Not Recoverable Error") {
    override val isRecoverable = false
  }
}
