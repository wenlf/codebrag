package com.softwaremill.codebrag.usecases.user

import com.typesafe.scalalogging.slf4j.Logging
import com.softwaremill.codebrag.service.user.RegisterService
import com.softwaremill.scalaval.Validation
import com.softwaremill.codebrag.domain.{Authentication, User}
import org.bson.types.ObjectId
import java.util.UUID

case class RegistrationForm(login: String, email: String, password: String, invitationCode: String) {
  lazy val toUser = User(new ObjectId, Authentication.basic(login, password), login, email.toLowerCase, UUID.randomUUID().toString)
}

class RegisterNewUserUseCase(registerService: RegisterService, validator: UserRegistrationValidator) extends Logging {

  def execute(form: RegistrationForm): Either[Validation.Errors, Unit] = {
    logger.debug(s"Attempting to register new user ${form.login}")
    val firstUser = registerService.isFirstRegistration
    validator.validateRegistration(form, firstUser).whenOk {
      val user = if(firstUser) form.toUser.makeAdmin else form.toUser
      registerService.registerUser(user)
    }
  }

  }
